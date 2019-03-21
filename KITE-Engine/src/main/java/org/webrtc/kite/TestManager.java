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
import com.cosmo.kite.tests.KiteBaseTest;
import com.cosmo.kite.tests.KiteJsTest;
import com.cosmo.kite.usrmgmt.TypeRole;
import com.cosmo.kite.report.custom_kite_allure.Status;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.webrtc.kite.config.*;

import javax.json.*;
import java.util.*;
import java.util.concurrent.Callable;

import static com.cosmo.kite.util.ReportUtils.timestamp;

/**
 * A thread to step an implementation of KiteTest.
 * <p>
 * The algorithm of the thread is as follows: 1) Instantiate the WebDriver objects. 2) Instantiate
 * KiteTest implementation. 3) Set the WebDriver objects to the implementation. 4) Execute the test.
 * 5) Retrieve, parse and populate from userAgent. 6) Get the stack trace of an exception if it
 * occurs during the execution. 7) Quit all WebDrivers. 8) Develop result json. 9) Post the result
 * to the callback url synchronously for the first and last test and asynchronously for the rest of
 * the tests.
 */
public class TestManager implements Callable<Object> {
  
  private static final Logger logger = Logger.getLogger(TestManager.class.getName());
  
  private static final boolean ENABLE_CALLBACK = true;
  /**
   * The Web driver list.
   */
  protected List<WebDriver> webDriverList;
  protected Map<WebDriver, TypeRole> driverRoleMap;
  private TestConf testConf;
  private List<EndPoint> endPointList;
  private String testName;
  private int retryCount;
  private int totalTests = 0;
  private boolean isLastTest = false;
  private long timeTaken;
  private Status overAllStatus = Status.BROKEN;
  private String textAttachment = null;
  private Container testSuite;
  /**
   * Constructs a new TestManager with the given TestConf and List<EndPoint>.
   *
   * @param testConf             TestConf
   * @param endPointList List<EndPoint>
   * @param testName             the test name
   */
  public TestManager(TestConf testConf, List<EndPoint> endPointList, String testName) {
    this.testConf = testConf;
    this.endPointList = endPointList;
    this.testName = testName;
  }
  
  /**
   * Sets total tests.
   *
   * @param totalTests the total tests
   */
  public void setTotalTests(int totalTests) {
    this.totalTests = totalTests;
  }
  
  /**
   * Sets is last test.
   *
   * @param isLastTest the is last test
   */
  public void setIsLastTest(boolean isLastTest) {
    this.isLastTest = isLastTest;
  }
  
  /**
   * Checks whether it is the first test of the batch.
   *
   * @return true if totalTests > 0.
   */
  private boolean isFirstTest() {
    return this.totalTests > 0;
  }
  

  
  @Override
  public Object call() throws Exception {
    String testImpl = this.testConf.getTestImpl();
    KiteBaseTest test;
    if (testImpl.endsWith("js")) {
      test = new KiteJsTest(testImpl);
    } else {
      test = (KiteBaseTest) Class.forName(this.testConf.getTestImpl()).getConstructor().newInstance();
    }
    
    testSuite.addChild(test.getReport().getUuid());
    test.setDescription(testConf.getDescription());
    test.setParentSuite(Configurator.getInstance().getName());
    test.setSuite(testSuite.getName());
    test.setPayload(this.testConf.getPayload());
    test.setEndPointList(endPointList);
    JsonObject testResult = test.execute();
    
    
    if (ENABLE_CALLBACK) {
      if (this.testConf.getCallbackURL() == null) {
        logger.warn("No callback specified for " + this.testConf);
      } else {
        CallbackThread callbackThread =
          new CallbackThread(this.testConf.getCallbackURL(), testResult);
        // if no "meta", post result in other thread; if "meta", post result in same thread
        // "meta" is included for the first and last tests, that are executed synchronously
        if (testResult.getString("meta", null) == null) {
          callbackThread.start();
        } else {
          callbackThread.postResult();
        }
      }
    }
    
    // Update allure report:
    
    return testResult;
    
  }
  
  
  public void setTestSuite(Container testSuite) {
    this.testSuite = testSuite;
  }
}
