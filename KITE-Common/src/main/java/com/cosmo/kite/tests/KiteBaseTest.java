package com.cosmo.kite.tests;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.report.custom_kite_allure.AllureStepReport;
import com.cosmo.kite.report.custom_kite_allure.AllureTestReport;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.report.custom_kite_allure.Status;
import com.cosmo.kite.steps.TestStep;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.webrtc.kite.config.App;
import org.webrtc.kite.config.Browser;
import org.webrtc.kite.config.EndPoint;
import org.webrtc.kite.exception.KiteGridException;
import org.webrtc.kite.wdmgmt.WebDriverUtility;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.cosmo.kite.util.ReportUtils.getStackTrace;
import static com.cosmo.kite.util.ReportUtils.timestamp;
import static com.cosmo.kite.util.TestUtils.processTestStep;

public abstract class KiteBaseTest {
  protected final String name = this.getClass().getSimpleName();
  protected final Logger logger = Logger.getLogger(name);
  protected final List<KiteCallable> callables = new ArrayList<>();
  protected String parentSuite = "";
  protected String suite = "";
  
  protected String description;
  protected boolean multiThread = true;
  protected int tupleSize;
  protected JsonValue payload;
  
  protected List<EndPoint> endPointList;
  protected List<WebDriver> webDriverList = new ArrayList<>();
  /**
   * The Remote address.
   */
  protected String remoteAddress;
  
  protected AllureTestReport report;
  
  public KiteBaseTest() {
    fillOutReport();
  }
  
  public JsonObject execute() {
    try {
      init();
      logger.info("Test initiation finished..");
      if (multiThread) {
        testInParallel();
      } else {
        testSequentially();
      }
    } catch (Exception e) {
      // this is for the initiation mostly
      Reporter.getInstance().processException(report,e);
    } finally {
      if (!webDriverList.isEmpty()) {
        WebDriverUtility.closeDrivers(this.webDriverList);
      }
    }
    return report.toJson();
  }
  
//  @AllureStepReport("Test initiation")
  public void init() throws KiteTestException {
    this.report.setStartTimestamp();
    AllureStepReport initStep = new AllureStepReport("Creating webdrivers and preparing threads..");
    try {
      initStep.setStartTimestamp();
      if (this.payload != null) {
        payloadHandling();
        Reporter.getInstance().jsonAttachment(initStep, "Test payload", this.payload);
      }
      populateDrivers();
      populateInfoFromNavigator();
      populateCallables();
      setTestScript();
    } catch (KiteGridException e) {
      logger.error("Exception while populating web drivers, " +
        "closing already created webdrivers...\r\n" + getStackTrace(e));
      Reporter.getInstance().textAttachment(initStep, "KiteGridException", getStackTrace(e), "plain");
      initStep.setStatus(Status.FAILED);
      throw new KiteTestException("Exception while populating web drivers", Status.FAILED);
    }
    initStep.setStatus(Status.PASSED);
    this.report.addStepReport(initStep);
  }
  
  /**
   /**
   * Executes the tests in parallel.
   *
   * @throws Exception if an Exception occurs during method execution.
   */
  private void testInParallel() throws Exception {
    ExecutorService executorService = Executors.newFixedThreadPool(callables.size());
    List<Future<Object>> futureList =
      executorService.invokeAll(callables, this.calculateTestTimeOut(), TimeUnit.MINUTES);
    executorService.shutdown();
    for (Future<Object> future : futureList) {
      future.get();
    }
  }
  
  protected int calculateTestTimeOut() {
    int maxNumberOfStep = 0;
    for (KiteCallable callable : callables) {
      if (maxNumberOfStep < callable.getSteps().size()) {
        maxNumberOfStep = callable.getSteps().size();
      }
    }
    logger.info("Maximum estimated time for the test: " + maxNumberOfStep * 2 + " minutes.");
    // Estimating one step takes maximum 2 minutes
    return maxNumberOfStep * 2;
  }
  
  /**
   * Executes the tests sequentially.
   * Assuming that all the callables have the same number of steps
   * If not, overwrite this function with appropriate order.
   */
  protected void testSequentially(){
    for (int i = 0; i < callables.get(0).getSteps().size(); i++) {
      for (KiteCallable callable : callables) {
        TestStep step = callable.getSteps().get(i);
        processTestStep(step, report);
      }
    }
  }
  
  protected void fillOutReport(){
    this.report = new AllureTestReport(timestamp());
    this.report.setFullName(getClass().getName());
    this.report.addLabel("package", getClass().getPackage().toString());
    this.report.addLabel("testClass", getClass().toString());
    this.report.addLabel("testMethod", "execute");
    try {
      this.report.addLabel("host", InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      this.report.addLabel("host", "N/A");
    }
    
    logger.info("Finished filling out initial report");
  }
  
  
  /**
   * Constructs a list of web drivers against the number of provided config objects.
   *
   * @throws MalformedURLException if no protocol is specified in the remoteAddress of a config object, or
   *                               an unknown protocol is found, or spec is null.
   */
//  @AllureStepReport("Populating webdriver from endpoints")
  protected void populateDrivers() throws KiteGridException {
    for (EndPoint endPoint : this.endPointList) {
      try {
        WebDriver webDriver =
          WebDriverUtility.getWebDriverForConfigObject(this.name, endPoint);
        this.webDriverList.add(webDriver);
      } catch (Exception e) {
        e.printStackTrace();
        
        throw new KiteGridException(e.getClass().getSimpleName() + " creating webdriver for \n"
          + endPoint.getJsonObject().toString() + ":\n"
          + e.getLocalizedMessage());
      }
    }
  }
  
  /**
   * Retrieves the navigator.userAgent from all of the config objects and passes it to the the respective
   * Config object for processing.
   */
//  @AllureStepReport("Populating webdriver from navigators")
  protected void populateInfoFromNavigator() {
    for (int i = 0; i < tupleSize; i++) {
      WebDriverUtility
        .populateInfoFromNavigator(this.webDriverList.get(i), this.endPointList.get(i));
    }
  }
  
  public void setTupleSize(int tupleSize) {
    this.tupleSize = tupleSize;
  }
  
  public void setDescription(String description) {
    this.description = description;
    this.report.setDescription(description);
  }

  /**
   * Method to set a the remoteAddress (IP of the hub).
   *
   * @param remoteAddress String the address of the hub
   */
  public void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }
  
  public void setPayload(JsonValue payload) {
    this.payload = payload;
  }

  public void setEndPointList(List<EndPoint> endPointList) {
    this.endPointList = endPointList;
    this.report.setName(generateTestCaseName());
  }
  
  public void setParentSuite(String parentTestSuite) {
    this.parentSuite = parentTestSuite;
    this.report.addLabel("parentSuite", parentTestSuite);
  }
  
  public void setSuite(String suite) {
    this.suite = suite;
    this.report.addLabel("suite", suite);
  }
  
  public AllureTestReport getReport() {
    return report;
  }
  
  protected String generateTestCaseName() {
    String name = "";
    for (int index = 0; index < endPointList.size(); index ++) {
      EndPoint endPoint = endPointList.get(index);
      name += endPoint.getPlatform().substring(0,3);
      if (endPoint instanceof Browser) {
        name += "_" + ((Browser)endPoint).getBrowserName().substring(0,2);
        name += "_" + ((Browser)endPoint).getVersion();
      } else {
        name += "_" + ((App)endPoint).getDeviceName().substring(0,2);
      }
      
      if (index < endPointList.size() -1) {
        name += "-";
      }
    }
    return name;
  }
  
  protected void populateCallables(){
    for (WebDriver webDriver : webDriverList) {
      this.callables.add(new KiteCallable(webDriver, this.report));
    }
  }
  
  public abstract void setTestScript();
  
  protected abstract void payloadHandling();


  
}
