/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.tests;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.testers.CallableTester;
import com.cosmo.kite.util.TestUtils;
import com.cosmo.kite.util.WebDriverUtils;
import io.qameta.allure.model.Status;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.webrtc.kite.config.EndPoint;
import org.webrtc.kite.exception.KiteGridException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.util.*;


import static com.cosmo.kite.util.ReportUtils.getStackTrace;
import static com.cosmo.kite.util.ReportUtils.timestamp;
import static com.cosmo.kite.util.TestUtils.createDirs;
import static com.cosmo.kite.util.TestUtils.printJsonTofile;

/**
 * The type Kite js test.
 */
public class KiteJsTest extends KiteLoadTest {
  
  private static final Logger logger = Logger.getLogger(KiteJsTest.class.getName());
  // todo : get JS_PATH from config file
  private final String JS_PATH = "js/";
  private final String jsTestImpl;
  private final String tempPath;
  
  /**
   * Instantiates a new Kite js test.
   *
   * @param jsTestImpl the js file
   */
  public KiteJsTest(String jsTestImpl) {
    this.jsTestImpl = jsTestImpl;
    this.tempPath = "temp/" + jsTestImpl + "_" + timestamp() + "_" +UUID.randomUUID().toString().substring(0,5);
}
  
  @Override
  protected void populateDrivers() {
    // not creating webdriver but write capabilities to temp dirs
    for (int index = 0; index < this.endPointList.size(); index ++) {
      EndPoint endpoint = this.endPointList.get(index);
      createDirs(JS_PATH + tempPath + "/" + index);
      printJsonTofile(endpoint.toString(), JS_PATH + tempPath + "/" + index + "/capabilities.json");
    }
  }
  
  @Override
  protected void populateCallables() {
    for (int index = 0; index < this.endPointList.size(); index ++) {
      KiteJsCallable callable = new KiteJsCallable(this.report, jsTestImpl, index);
      callable.setNumberOfParticipant(endPointList.size());
      callable.setReportPath(tempPath);
      this.callables.add(callable);
    }
  }
  
  /**
   * Print the payload to the js temp folder
   */
  @Override
  protected void payloadHandling() {
    super.payloadHandling();
    createDirs(JS_PATH + tempPath);
    printJsonTofile(this.payload.toString(), JS_PATH + tempPath +"/payload.json");
  }
  
  @Override
  protected void testSequentially() {
    logger.error("Sorry, JavaScript test with Kite cannot be run sequentially at the moment.");
  }
  
  @Override
  protected int calculateTestTimeOut() {
    // todo: better estimation on test duration for JS
    return 10;
  }
  
  //  /**
//   * Print the payload to the js temp folder
//   */
//  @Override
//  public JsonObject testScript(String testDescription) {
//    return super.testScript(testDescription);
//  }
//
//  private class JsTester extends CallableTester {
//
//    /**
//     * Instantiates a new Js tester.
//     *
//     * @param webDriver   the web driver
//     * @param sessionData the session data
//     * @param url         the url
//     * @param loadTest    the load test
//     */
//    public JsTester(
//      WebDriver webDriver, Map<String, String> sessionData, String url, KiteLoadTest loadTest) {
//      super(webDriver, sessionData, url, loadTest);
//    }
//
//    @Override
//    public JsonObjectBuilder call() throws Exception {
//      if (botName == null) {
//        botName = botName(webDriver, this.id);
//        logHeader = String.format("%1$-28s", botName);
//      }
//      JsonObjectBuilder steps = Json.createObjectBuilder();
//      steps.add("botname", botName);
//      steps.add("vmName", this.logHeader(webDriver).trim());
//      steps.add("timeStamp", dateFormat.format(new Date()));
//      steps.add("url", this.url);
//      try {
//        if (rampUp) {
//          logger.info(logHeader + "- ramp up test script");
//        } else {
//          logger.info(logHeader + "- load reached test script");
//        }
//      } catch (Exception e) {
//        throw e;
//      } finally {
//        rampUp = false;
//      }
//      try {
//        List<String> command = java.util.Arrays.asList("node", jsTestImpl, "" + this.id);
//        TestUtils.executeCommand("js/", command, logger, logHeader);
//      } catch (Exception e) {
//        logger.error(getStackTrace(e));
//      }
//      return steps;
//    }
//
//    /**
//     * STUB method where the test steps are implemented, which are executed once the target load is
//     * reached.
//     *
//     * @param steps
//     *
//     * @return true the test steps are all successful, false otherwise
//     */
//    @Override
//    protected boolean endSteps(JsonObjectBuilder steps) {
//      return false;
//    }
//
//    /**
//     * STUB method where the test steps are implemented.
//     *
//     * @param steps
//     *
//     * @return true the test steps are all successful, false otherwise
//     */
//    @Override
//    protected boolean testSteps(JsonObjectBuilder steps) throws Exception {
//      return false;
//    }
//
//  }
  
  
}
