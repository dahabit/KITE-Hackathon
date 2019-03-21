/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.tests;

import com.cosmo.kite.instrumentation.NWInstConfig;
import com.cosmo.kite.manager.RoomManager;
import com.cosmo.kite.testers.CallableTester;
import com.cosmo.kite.util.ReportUtils;
import com.cosmo.kite.util.TestHelper;
import com.cosmo.kite.util.TestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * The type Kite load test.
 */
public abstract class KiteLoadTest extends KiteBaseTest {
  
  /**
   * The constant CALL.
   */
  public static final String CALL = "ru"; //ramp up
  /**
   * The constant END.
   */
  public static final String END = "lr"; //load reached
  private static final Logger logger = Logger.getLogger(KiteLoadTest.class.getName());
  /**
   * The constant driverId.
   */
  public static int driverId = 0;


  /**
   * The constant roomManager.
   */
  protected static RoomManager roomManager = null;
  private static int accumulatedTestCount = 0;
  // unique ID for this test, used mainly for segregating reports and screenshots.
  private final String uid = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
  /**
   * The Count.
   */
  protected int count = 1;
  /**
   * The Expected test duration.
   */
  protected int expectedTestDuration = 60; //in minutes
  /**
   * The Hub ip or dns.
   */
  protected String hubIpOrDns;
  /**
   * The Increment.
   */
  protected int increment = 1;
  /**
   * The Interval.
   */
  protected int interval = 5; //5 seconds
  /**
   * The Max users per room.
   */
  protected int maxUsersPerRoom = 1;
  /**
   * The Page title.
   */
  protected String pageTitle = "Kite";
  /**
   * The Room id.
   */
  protected int roomId;
  /**
   * The Successful tests.
   */
  protected int successfulTests = 0;
  /**
   * The Test name.
   */
  protected String testName = "KiteLoadTest";
  private String testDescription;
  /**
   * The Url.
   */
  protected String url = "https://www.youtube.com/watch?v=b7Z_E4SRGro&feature=youtu.be";
  private String configFile = null;
  private boolean createCSVReport = true;
  private boolean createJsonFile = true;
  private boolean fastRampUp = false;
  private boolean getStats = false;
  private int latencyCollectionInterval = 6;
  private int latencyCollectionTime = 14440;
  private JsonArray selectedStats = null;
  private NWInstConfig nwInstConfig = null;
  private int rejoinInterval = 0;
  private int rejoinRandomInterval = 0;
  private String resultPath = "results/" + uid + "/";
  private String screenshotPath = resultPath + "screenshots/";
  private Map<WebDriver, Map<String, String>> sessionData;
  private int statsCollectionInterval = 1;
  private int statsCollectionTime = 10;
  private int stayInTime = 0;
  private boolean takeScreenshotForEachTest = false; // false by default
  private int testTimeout = 60;
  private Vector<CallableTester> testerList = new Vector<>();
  
  /**
   * Fast ramp up boolean.
   *
   * @return true for fastRampUp
   */
  public boolean fastRampUp() {
    return fastRampUp;
  }
  
  /**
   * Gets increment.
   *
   * @return the increment value
   */
  public int getIncrement() {
    return this.increment;
  }
  
  /**
   * Set increment according to option given in browsers object from config file.
   *
   * @param increment the increment
   */
  public void setIncrement(int increment) {
    this.increment = increment;
  }
  
  /**
   * Gets interval.
   *
   * @return the interval value
   */
  public int getInterval() {
    return this.interval;
  }
  
  /**
   * Set interval according to option given in browsers object from config file.
   *
   * @param interval the interval
   */
  public void setInterval(int interval) {
    this.interval = interval;
  }
  
  /**
   * Gets latency collection interval.
   *
   * @return latencyCollectionInterval Time interval between each OCR latency checks call (Default 6s)
   */
  public int getLatencyCollectionInterval() {
    return latencyCollectionInterval;
  }
  
  /**
   * Gets latency collection time.
   *
   * @return latencyCollectionTime Time in seconds to collect latency (Default 14400s)
   */
  public int getLatencyCollectionTime() {
    return latencyCollectionTime;
  }
  
  /**
   * Gets max users per room.
   *
   * @return the max users per room
   */
  public int getMaxUsersPerRoom() {
    return maxUsersPerRoom;
  }
  
  /**
   * Gets nw inst config.
   *
   * @return the network instrumentation config
   */
  public NWInstConfig getNWInstConfig() {
    return this.nwInstConfig;
  }
  
  /**
   * Gets page title.
   *
   * @return the pageTitle as set in the config file.
   */
  public String getPageTitle() {
    return this.pageTitle = testName;
  }
  
  /**
   * Sets HTML page title
   *
   * @param pageTitle the page title
   */
  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }
  
  /**
   * This is the sum of rejoinInterval and Math.random() * rejoinRandomInterval.
   *
   * @return rejoinInterval (in seconds): how often to reload the page and re-join the same room.
   */
  public long getRejoinInterval() {
    return Math.round(rejoinInterval + (Math.random() * rejoinRandomInterval));
  }
  
  /**
   * Gets the path where the results files (screenshots, browser logs, getStats json files) should be saved
   *
   * @return resultPath result path
   */
  public String getResultPath() {
    return resultPath;
  }
  
  /**
   * Gets room id.
   *
   * @return the roomId
   */
  public int getRoomId() {
    return this.roomId;
  }
  
  /**
   * Gets screenshot path.
   *
   * @return the path to the screenshot folder
   */
  public String getScreenshotPath() {
    return this.screenshotPath;
  }
  
  /**
   * Gets session data.
   *
   * @return the session data
   */
  public Map<WebDriver, Map<String, String>> getSessionData() {
    return sessionData;
  }
  
  /**
   * Sets session data.
   *
   * @param sessionData the session data
   */
  public void setSessionData(Map<WebDriver, Map<String, String>> sessionData) {
    this.sessionData = sessionData;
  }
  
  /**
   * Gets stats.
   *
   * @return true to call and collect getStats, false otherwise, as set in the config file.
   */
  public boolean getStats() {
    return getStats;
  }
  
  /**
   * Gets stats collection interval.
   *
   * @return statsCollectionInterval Time interval between each getStats call (Default 1)
   */
  public int getStatsCollectionInterval() {
    return statsCollectionInterval;
  }
  
  /**
   * Gets stats collection time.
   *
   * @return statsCollectionTime Time in seconds to collect stats (Default 10)
   */
  public int getStatsCollectionTime() {
    return statsCollectionTime;
  }
  
  /**
   * Gets stay in time.
   *
   * @return stayInTime (in seconds): how long to stay in the room during rampup.
   */
  public int getStayInTime() {
    return stayInTime;
  }
  
  /**
   * Gets successful tests.
   *
   * @return the successful tests
   */
  public int getSuccessfulTests() {
    return successfulTests;
  }
  
  /**
   * Gets the name of the test.
   *
   * @return testName test name
   */
  public String getTestName() {
    return testName;
  }
  
  /**
   * sets the testName from the config file.
   *
   * @param testName the test name
   */
  public void setTestName(String testName) {
    this.testName = testName;
    this.resultPath = "results/" + uid + "_" + testName + "/";
    this.screenshotPath = resultPath + "screenshots/";
  }
  
  /**
   * Gets test timeout.
   *
   * @return the testTimeout as set in the config file.
   */
  public int getTestTimeout() {
    return this.testTimeout;
  }
  
  /**
   * Gets the unique ID
   *
   * @return the unique ID
   */
  public String getUid() {
    return this.uid;
  }
  
  /**
   * This method can be overridden to return the test statistics at anytime especially if it fails
   * due to some exception.
   * By default it will process each callableTester.getUserData() method and build the csv report file
   *
   * @return Some custom stats object with a toString() implementation. By default, it returns a Vector<JsonObject> containing the results of the CallableTester.getUserData();
   */
  public Object getUserData() {
    logger.info("\r\n          ==========================================================\r\n"
      + "                              KiteLoadTest.getUserData()"
      + "\r\n          ==========================================================\r\n");
    Vector<JsonObject> results = new Vector<>();
    for (int i = 0; i < testerList.size(); i += this.increment) {
      try {
        Vector<CallableTester> callableTesters = new Vector<>();
        for (int j = i; j < i + this.increment; j++) {
          if (j < testerList.size()) {
            callableTesters.add(testerList.elementAt(j));
          } else {
            break;
          }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(this.increment);
        List<Future<JsonObjectBuilder>> futureList =
          executorService.invokeAll(callableTesters, expectedTestDuration, TimeUnit.MINUTES);
        
        executorService.shutdown();
        
        int successCount = 0;
        for (Future<JsonObjectBuilder> future : futureList) {
          try {
            JsonObjectBuilder jsonObjBuilder = future.get();
            JsonObject jsonObject = jsonObjBuilder.build();
            results.add(jsonObject);
            if (createCSVReport) {
              TestHelper.getInstance(END).println(jsonObject, resultPath);
            }
            String url = jsonObject.getString("url", "null");
            url = url.contains("/") ? url.substring(url.lastIndexOf("/")) : url;
            String res = jsonObject.getString("result", "null");
            successCount += res.contains(CallableTester.RESULT_PASS) ? 1 : 0;
            logger.info("Load Reached Test for " + url + " = " + res);
          } catch (Exception e) {
            logger.error(
              "Exception in KiteLoadTest: "
                + e.getLocalizedMessage()
                + "\r\n"
                + ReportUtils.getStackTrace(e));
          }
        }
      } catch (Exception e) {
        logger.error(ReportUtils.getStackTrace(e));
      }
    }
    String resultStr = TestUtils.createJsonArray("resultObj", results);
    
    //logger.info("Test resultObj = \r\n" + resultStr);
    if (createJsonFile) {
      TestUtils.printJsonTofile(END + "_" + testName, resultStr, resultPath + "reports/");
    }
    //TestHelper.getInstance(END).close();
    return results;
  }
  
  /**
   * Restructuring the test according to options given in payload object from config file. This
   * function processes the parameters common to all load tests.
   */
  @Override
  protected void payloadHandling() {
    JsonObject jsonPayload = (JsonObject) this.payload;
    if (jsonPayload != null) {
      testTimeout = jsonPayload.getInt("testTimeout", testTimeout);
      if (jsonPayload.containsKey("loadTestTimeout")) {
        testTimeout = jsonPayload.getInt("loadTestTimeout");
      }
      url = jsonPayload.getString("url", null);
      pageTitle = jsonPayload.getString("pageTitle", pageTitle);
      takeScreenshotForEachTest =
        jsonPayload.getBoolean("takeScreenshotForEachTest", takeScreenshotForEachTest);
      getStats = jsonPayload.getBoolean("getStats", false);
      multiThread = jsonPayload.getBoolean("multiThread", true);
      statsCollectionTime = jsonPayload.getInt("statsCollectionTime", statsCollectionTime);
      statsCollectionInterval = jsonPayload.getInt("statsCollectionInterval", statsCollectionInterval);
      if (jsonPayload.containsKey("selectedStats")) {
        selectedStats = jsonPayload.getJsonArray("selectedStats");
      }
      latencyCollectionTime = jsonPayload.getInt("latencyCollectionTime", latencyCollectionTime);
      latencyCollectionInterval = jsonPayload.getInt("latencyCollectionInterval", latencyCollectionInterval);
      stayInTime = jsonPayload.getInt("stayInTime", stayInTime);
      if (stayInTime > 2 * 60 * testTimeout) {
        //increase test timeout (in min) to be > stayingTime (in s)
        testTimeout = (stayInTime * 2) / 60;
      }
      rejoinInterval = jsonPayload.getInt("rejoinInterval", rejoinInterval);
      rejoinRandomInterval = jsonPayload.getInt("rejoinRandomInterval", rejoinRandomInterval);
      createJsonFile = jsonPayload.getBoolean("csvReport", createJsonFile);
      fastRampUp = jsonPayload.getBoolean("fastRampUp", fastRampUp);
      try {
        JsonObject obj = jsonPayload.getJsonObject("instrumentation");
        if (obj != null) {
          nwInstConfig = new NWInstConfig(obj);
        }
      } catch (Exception e) {
        logger.error("Invalid network instrumentation config.\r\n" + ReportUtils.getStackTrace(e));
      }
    }
  }
  
  /**
   * path to the config file used for this test.
   *
   * @param configFile the config file
   *
   * @throws IOException the io exception
   */
  public void setConfigFile(String configFile) throws IOException {
    if (configFile == null) {
      this.configFile = configFile;
      FileUtils.copyFile(new File(this.configFile), new File(resultPath + this.configFile));
    } else {
      this.configFile = configFile;
    }
  }
  
  /**
   * Set count according to option given in browsers object from config file.
   *
   * @param count the count
   */
  public void setCount(int count) {
    this.count = count;
  }
  
  /**
   * Sets get stats.
   *
   * @param getStats the get stats
   */
  public void setGetStats(boolean getStats) {
    this.getStats = getStats;
  }
  
  /**
   * Sets the hub IP or DNS
   *
   * @param hubIpOrDns the hub IP or DNS
   */
  public void setHubIpOrDns(String hubIpOrDns) {
    this.hubIpOrDns = hubIpOrDns;
  }
  
  /**
   * Set the random roomId
   */
  public void setRoomId() {
    this.roomId = (int) (Math.random() * 10000 + 1);
  }
  
  /**
   * Running the test in multi-threaded manner
   *
   * @return the int
   * @throws Exception the exception
   */
  protected int takeAction() throws Exception {
    Vector<JsonObject> jsonList = new Vector<JsonObject>();
    if (roomManager != null && this.hubIpOrDns != null && !roomManager.roomListProvided()) {
      this.url = RoomManager.getInstance().getRoomUrl(this.hubIpOrDns);
    }
    logger.info("Testing " + this.webDriverList.size() + " " + url + ". testTimeout = " + testTimeout + " minutes.");

    int successCount = 0;
    /*
    List<CallableTester> callableTesters = getTesterList(webDriverList);
    testerList.addAll(callableTesters);
    ExecutorService executorService = Executors.newFixedThreadPool(webDriverList.size());
    List<Future<JsonObjectBuilder>> futureList =
      executorService.invokeAll(callableTesters, expectedTestDuration, TimeUnit.MINUTES);
    executorService.shutdown();

    for (Future<JsonObjectBuilder> future : futureList) {
      try {
        JsonObjectBuilder jsonObjBuilder = future.get();
        JsonObject jsonObject = jsonObjBuilder.build();
        jsonList.add(jsonObject);
        if (createCSVReport) {
          TestHelper.getInstance(CALL).println(jsonObject, resultPath);
        }
        String url = jsonObject.getString("url", "null");
        url = url.contains("/") ? url.substring(url.lastIndexOf("/")) : url;
        String res = jsonObject.getString("result", "null");
        successCount += res.contains(CallableTester.RESULT_PASS) ? 1 : 0;
        logger.info("Ramp Up Test for " + url + " = " + res);
      } catch (Exception e) {
        logger.error(
          "Exception in KiteLoadTest: "
            + e.getLocalizedMessage()
            + "\r\n"
            + ReportUtils.getStackTrace(e));
      }
    }
    */
    this.successfulTests = successCount;
    String resultStr = TestUtils.createJsonArray("resultObj", jsonList);
    //logger.info("Test resultObj = \r\n" + resultStr);
    if (createJsonFile) {
      TestUtils.printJsonTofile(CALL + "_" + testName, resultStr, resultPath + "reports/");
    }
    synchronized (this) {
      KiteLoadTest.accumulatedTestCount += this.successfulTests;
      logger.info(
        "Given WebDrivers: "
          + this.webDriverList.size()
          + ", Successful tests: "
          + this.successfulTests
          + ", Accumulated test count: "
          + KiteLoadTest.accumulatedTestCount);
    }
    return successCount;
  }
  
  /**
   * Take screenshot for each test boolean.
   *
   * @return true or false as set in config file
   */
  public boolean takeScreenshotForEachTest() {
    return takeScreenshotForEachTest;
  }

  @Override
  public void setTestScript() {}

  public JsonObject testScript(String testDescription) {
    this.testDescription = testDescription;
    return execute();
  }

  public JsonArray getSelectedStats() {
    return selectedStats;
  }

  public void setSelectedStats(JsonArray selectedStats) {
    this.selectedStats = selectedStats;
  }
}
