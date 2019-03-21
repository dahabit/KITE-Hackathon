/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.tests;

import com.cosmo.kite.testers.CallableTester;
import com.cosmo.kite.util.TestUtils;
import com.cosmo.kite.util.WebDriverUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.webrtc.kite.exception.KiteGridException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.cosmo.kite.util.ReportUtils.getStackTrace;

/**
 * The type Kite js test.
 */
public class OldKiteJsTest extends KiteLoadTest {
  
  private static final Logger logger = Logger.getLogger(OldKiteJsTest.class.getName());
  private final String JS_PATH = "js/";
  private final String jsFile;
  
  /**
   * Instantiates a new Kite js test.
   *
   * @param jsFile the js file
   */
  public OldKiteJsTest(String jsFile) {
    this.jsFile = jsFile;
  }
  
  @Override
  protected void populateCallables() {
    for (WebDriver webDriver : this.webDriverList) {
      KiteCallable callable = new KiteCallable(webDriver, this.report);
//      callable.addStep(new GoogleSearchStep(webDriver));
//      callable.addStep(new GoogleFirstResultCheck(webDriver));
      this.callables.add(callable);
    }
  }
  
  private void createDirs() {
    File dir = new File(JS_PATH + "temp/");
    if (!dir.exists()) {
      dir.mkdirs();
    }
  }
  
  /**
   * Print the payload to the js temp folder
   */
  @Override
  protected void payloadHandling() {
    super.payloadHandling();
    createDirs();
    TestUtils.printJsonTofile(this.payload.toString(), JS_PATH + "temp/payload.json");
  }

  public void populateDrivers() throws KiteGridException {
    super.populateDrivers();
    createDirs();
    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    if (this.hubIpOrDns != null) {
      jsonObjectBuilder.add("remoteAddress", "http://" + this.hubIpOrDns + ":4444/wd/hub");
    } else {
      jsonObjectBuilder.add("remoteAddress", this.remoteAddress);
    }
    for (WebDriver w : webDriverList) {
      jsonArrayBuilder.add(WebDriverUtils.getCapabilityJsonFromWebDriver(w));
      w.quit();
    }
    jsonObjectBuilder.add("capabilities", jsonArrayBuilder);
    JsonObject jsonObject = jsonObjectBuilder.build();
    TestUtils.printJsonTofile(jsonObject.toString(), JS_PATH + "temp/tuples.json");
  }
  
  /**
   * Print the payload to the js temp folder
   */
  @Override
  public JsonObject testScript(String testDescription) {
    return super.testScript(testDescription);
  }
  
  private class JsTester extends CallableTester {
  
    /**
     * Instantiates a new Js tester.
     *
     * @param webDriver   the web driver
     * @param sessionData the session data
     * @param url         the url
     * @param loadTest    the load test
     */
    public JsTester(
      WebDriver webDriver, Map<String, String> sessionData, String url, KiteLoadTest loadTest) {
      super(webDriver, sessionData, url, loadTest);
    }
    
    @Override
    public JsonObjectBuilder call() throws Exception {
      if (botName == null) {
        botName = botName(webDriver, this.id);
        logHeader = String.format("%1$-28s", botName);
      }
      JsonObjectBuilder steps = Json.createObjectBuilder();
      steps.add("botname", botName);
      steps.add("vmName", this.logHeader(webDriver).trim());
      steps.add("timeStamp", dateFormat.format(new Date()));
      steps.add("url", this.url);
      try {
        if (rampUp) {
          logger.info(logHeader + "- ramp up test script");
        } else {
          logger.info(logHeader + "- load reached test script");
        }
      } catch (Exception e) {
        throw e;
      } finally {
        rampUp = false;
      }
      try {
        List<String> command = java.util.Arrays.asList("node", jsFile, "" + this.id);
        TestUtils.executeCommand("js/", command, logger, logHeader);
      } catch (Exception e) {
        logger.error(getStackTrace(e));
      }
      return steps;
    }
    
    /**
     * STUB method where the test steps are implemented, which are executed once the target load is
     * reached.
     *
     * @param steps
     *
     * @return true the test steps are all successful, false otherwise
     */
    @Override
    protected boolean endSteps(JsonObjectBuilder steps) {
      return false;
    }
    
    /**
     * STUB method where the test steps are implemented.
     *
     * @param steps
     *
     * @return true the test steps are all successful, false otherwise
     */
    @Override
    protected boolean testSteps(JsonObjectBuilder steps) throws Exception {
      return false;
    }
    
  }
  
  
}
