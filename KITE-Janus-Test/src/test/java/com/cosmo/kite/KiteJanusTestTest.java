/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite;

import com.cosmo.kite.janus.KiteJanusTest;
import com.cosmo.kite.tests.KiteLoadTest;
import com.cosmo.kite.util.TestUtils;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.webrtc.kite.Utility;
import org.webrtc.kite.config.Browser;
import org.webrtc.kite.wdmgmt.WebDriverFactory;
import org.webrtc.kite.config.EndPoint;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KiteJanusTestTest extends TestCase {

  static {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
    System.setProperty("current.date", dateFormat.format(new Date()));
  }

  //Logger must be called after setting the system property "current.data"
  private static final Logger logger = Logger.getLogger(KiteJanusTestTest.class.getName());

  private static final String SELENIUM_SERVER_URL = "http://localhost:4444/wd/hub";
    private static String url = "https://lbclient.cosmosoftware.io/videoroomtest_videoanalysis.html?roomId=";
//  private static String url = "https://soleil.kite.cosmosoftware.io/viewer/";
  private static final String TEST_NAME = "Janus UnitTest";
  private static final String CONFIG_FILE = "configs/local.janus.config.json";

  private List<WebDriver> webDriverList = new ArrayList<>();
  private List<EndPoint> endPointList = TestUtils.getEndPointList(CONFIG_FILE, "browsers");

  private static final String platform = Utility.getPlatform();

  public void setUp() throws Exception {
    super.setUp();
  }

  public void tearDown() throws Exception {
    // Close all the browsers
    for (WebDriver webDriver : this.webDriverList)
      try {
        webDriver.quit();
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  public void testTestScript() throws Exception {
    KiteLoadTest test = new KiteJanusTest();
    test.setGetStats(true);
    test.setDescription(TEST_NAME);
    test.setPayload(TestUtils.getPayload(CONFIG_FILE, 0));
    test.setEndPointList(endPointList);
    JsonObject testResult = test.execute();
  }
}
