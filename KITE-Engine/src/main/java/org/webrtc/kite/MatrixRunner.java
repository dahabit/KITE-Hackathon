/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.webrtc.kite;

import com.cosmo.kite.report.custom_kite_allure.Container;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import org.apache.log4j.Logger;
import org.webrtc.kite.config.*;
import org.webrtc.kite.config.EndPoint;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.cosmo.kite.util.ReportUtils.timestamp;

/**
 * A class to manage the asynchronous execution of TestManager objects.
 */
public class MatrixRunner {
  
  private static final Logger logger = Logger.getLogger(MatrixRunner.class.getName());
  
  private TestConf testConf;
  private String testName;
  private List<List<EndPoint>> listOfTuples;
  private int numberOfThread;
  private List<List<EndPoint>> multiThreadedList = new ArrayList<List<EndPoint>>();
  private List<List<EndPoint>> singleThreadedList = new ArrayList<List<EndPoint>>();
  
  private Container testSuite;
  
  /**
   * Constructs a new MatrixRunner with the given TestConf and List<List<EndPoint>>.
   *
   * @param testConf     TestConf
   * @param listOfTuples a list of tuples (containing 1 or multiples kite config objects).
   * @param testName     name of the running test suite.
   */
  public MatrixRunner(TestConf testConf, List<List<EndPoint>> listOfTuples, String testName) {
    this.testConf = testConf;
    this.testSuite = new Container(testConf.getName().contains("%ts") ?
      testConf.getName().replaceAll("%ts", "") + " Suite (" + timestamp() + ")" : testConf.getName());
    this.testName = testName;
    this.listOfTuples = listOfTuples;
    this.numberOfThread = testConf.getNoOfThreads();
  }
  
  /**
   * Returns a sublist from the given list of the type of objects specified by the objectClass.
   *
   * @param futureList  List of Future<Object>
   * @param objectClass The class for the desired required object list.
   * @return A sublist from the given list of the type of objects specified by the objectClass.
   */
  private List<?> getSubList(List<Future<Object>> futureList, Class<?> objectClass) {
    List<Object> listOfObject = new ArrayList<Object>();
    for (Future<Object> future : futureList) {
      try {
        Object object = future.get();
        if (objectClass.isInstance(object)) {
          listOfObject.add(object);
        }
      } catch (InterruptedException | ExecutionException e) {
        logger.error(e);
      }
    }
    return listOfObject;
  }
  
  /**
   * Returns a sublist of the given futureList exclusive of the type of objects specified by the
   * objectClass.
   *
   * @param futureList  List of Future<Object>
   * @param objectClass The class for the undesired required object.
   * @return A sublist of the given futureList exclusive of the type of objects specified by the
   * objectClass.
   */
  private List<Future<Object>> getExclusiveSubList(List<Future<Object>> futureList,
                                                   Class<?> objectClass) {
    List<Future<Object>> listOfFutureObjects = new ArrayList<Future<Object>>();
    for (Future<Object> future : futureList) {
      try {
        Object object = future.get();
        if (!objectClass.isInstance(object)) {
          listOfFutureObjects.add(future);
        }
      } catch (InterruptedException | ExecutionException e) {
        logger.error(e);
      }
    }
    return listOfFutureObjects;
  }
  
  /**
   * This method builds up singleThreadedList and multiThreadedList as follows:
   * <p>
   * 1) Omit all the test cases having 2 identical mobile browsers. 2) Put all the test cases
   * having microsoft edge or safari into singleThreadedList. 3) Put all the rest of test cases into
   * multiThreadedList.
   */
  private void purgeListOfBrowserList() {
    
    List<List<EndPoint>> customBrowserMatrix = Configurator.getInstance().getCustomBrowserMatrix();
    if (this.listOfTuples == null) {
      this.singleThreadedList.addAll(customBrowserMatrix);
      return;
    }
    
    for (List<EndPoint> endPointList : this.listOfTuples) {
      
      // Omit test cases with two identical mobile clients in them.
      int mobileCount = 0;
      Set<EndPoint> set = new LinkedHashSet<>();
      for (EndPoint endPoint : endPointList) {
        if (endPoint instanceof Browser) {
          if (((Browser)endPoint).getMobile() != null || ((Browser) endPoint).getVersion().startsWith("fennec")) {
            set.add(endPoint);
            mobileCount++;
          }
        }
      }
      
      // Add all the test cases having mobile in single thread list.
      if (mobileCount > 0) {
        if (mobileCount == set.size()) {
          this.singleThreadedList.add(endPointList);
        }
        continue;
      }
      
      // Add the rest of the test cases in multi thread list.
      this.multiThreadedList.add(endPointList);
    }
    
    this.singleThreadedList.addAll(customBrowserMatrix);
    
  }
  
  /**
   * Executes the test contained inside the TestManager for the provided matrix.
   * <p>
   * The algorithm of the method is as follows: 1) Execute the first test. 2) Execute the multi
   * threaded list. 3) Execute the single threaded list. 4) Execute the last test.
   *
   * @return List<Future   <   Object>>
   * @throws InterruptedException if thread pool is interrupted while waiting, in which case
   *                              unfinished tasks are cancelled
   * @throws ExecutionException   if the computation of the first or last thread threw an exception
   */
  public List<Future<Object>> run() throws InterruptedException, ExecutionException {
    
    // Build single and multi threaded lists
    this.purgeListOfBrowserList();
    
    int totalTestCases = this.singleThreadedList.size() + this.multiThreadedList.size();
    if (totalTestCases < 1) {
      return null;
    }
    
    int index = 0;
    TestManager firstTest = null, lastTest = null;
    
    // singleThreadedList and multiThreadedList manipulation
    if (this.singleThreadedList.size() > 1) {
      firstTest = new TestManager(this.testConf, this.singleThreadedList.get(0),
        this.testConf.getRemoteTestIdentifier(index++));
      lastTest = new TestManager(this.testConf,
        this.singleThreadedList.get(this.singleThreadedList.size() - 1),
        this.testConf.getRemoteTestIdentifier(totalTestCases - 1));
      
      this.singleThreadedList.remove(0);
      this.singleThreadedList.remove(this.singleThreadedList.size() - 1);
    } else if (this.multiThreadedList.size() > 1) {
      firstTest = new TestManager(this.testConf, this.multiThreadedList.get(0),
        this.testConf.getRemoteTestIdentifier(index++));
      lastTest = new TestManager(this.testConf,
        this.multiThreadedList.get(this.multiThreadedList.size() - 1),
        this.testConf.getRemoteTestIdentifier(totalTestCases - 1));
      
      this.multiThreadedList.remove(0);
      this.multiThreadedList.remove(this.multiThreadedList.size() - 1);
    } else if (this.singleThreadedList.size() == 1 && this.multiThreadedList.size() == 1) {
      firstTest = new TestManager(this.testConf, this.singleThreadedList.get(0),
        this.testConf.getRemoteTestIdentifier(index++));
      lastTest = new TestManager(this.testConf, this.multiThreadedList.get(0),
        this.testConf.getRemoteTestIdentifier(totalTestCases - 1));
      
      this.singleThreadedList.clear();
      this.multiThreadedList.clear();
    } else if (this.singleThreadedList.size() == 1) {
      firstTest = new TestManager(this.testConf, this.singleThreadedList.get(0),
        this.testConf.getRemoteTestIdentifier(index++));
      firstTest.setIsLastTest(true);
      
      this.singleThreadedList.clear();
    } else if (this.multiThreadedList.size() == 1) {
      firstTest = new TestManager(this.testConf, this.multiThreadedList.get(0),
        this.testConf.getRemoteTestIdentifier(index++));
      firstTest.setIsLastTest(true);
      
      this.multiThreadedList.clear();
    }
    
    // Set first and last tests
    if (firstTest != null) {
      firstTest.setTotalTests(totalTestCases);
    }
    if (lastTest != null) {
      lastTest.setIsLastTest(true);
    }
    
    List<TestManager> testManagerList = new ArrayList<TestManager>();
    List<Future<Object>> futureList = null;
    
    ExecutorService singleExecutorService = Executors.newFixedThreadPool(1);
    ExecutorService multiExecutorService =
      Executors.newFixedThreadPool(this.testConf.getNoOfThreads());
    
    logger.info("Executing " + this.testConf + " for " + totalTestCases + " browser tuples ...");
    
    try {
      // Execute the first test
      if (firstTest != null) {
        firstTest.setTestSuite(testSuite);
        testManagerList.add(firstTest);
        do {
          futureList = singleExecutorService.invokeAll(testManagerList);
        } while (futureList.get(0).get() instanceof TestManager); // In case of a needed retry the
        // instance of TestManger is
        // simply returned.
        testManagerList.clear();
      }
      
      // Execute the middle tests in multithreaded mode
      if (this.multiThreadedList.size() > 0) {
        for (List<EndPoint> configObjectList : this.multiThreadedList) {
          TestManager manager = new TestManager(this.testConf, configObjectList,
            this.testConf.getRemoteTestIdentifier(index++));
          manager.setTestSuite(testSuite);
          testManagerList.add(manager);
        }
        
        List<Future<Object>> tempFutureList = null;
        while (testManagerList.size() > 0) {
          tempFutureList = multiExecutorService.invokeAll(testManagerList);
          testManagerList = (List<TestManager>) this.getSubList(tempFutureList, TestManager.class);
          futureList.addAll(this.getExclusiveSubList(tempFutureList, TestManager.class));
        }
        testManagerList.clear();
      }
      
      // Execute the middle tests in singlethreaded mode
      if (this.singleThreadedList.size() > 0) {
        for (List<EndPoint> configObjectList : this.singleThreadedList) {
          TestManager manager = new TestManager(this.testConf, configObjectList,
            this.testConf.getRemoteTestIdentifier(index++));
          manager.setTestSuite(testSuite);
          testManagerList.add(manager);
        }
        List<Future<Object>> tempFutureList = null;
        while (testManagerList.size() > 0) {
          tempFutureList = singleExecutorService.invokeAll(testManagerList);
          testManagerList = (List<TestManager>) this.getSubList(tempFutureList, TestManager.class);
          futureList.addAll(this.getExclusiveSubList(tempFutureList, TestManager.class));
        }
        testManagerList.clear();
      }
      
      // Execute the last test
      if (lastTest != null) {
        lastTest.setTestSuite(testSuite);
        testManagerList.add(lastTest);
        List<Future<Object>> tempFutureList = null;
        do {
          tempFutureList = singleExecutorService.invokeAll(testManagerList);
        } while (tempFutureList.get(0).get() instanceof TestManager); // In case of a needed retry
        // the instance of TestManger is simply returned.
        futureList.addAll(tempFutureList);
      }
    } finally {
      testSuite.setStopTimestamp();
      Reporter.getInstance().generateReportFiles();
      multiExecutorService.shutdown();
      singleExecutorService.shutdown();
    }
    
    return futureList;
    
  }
  
}
