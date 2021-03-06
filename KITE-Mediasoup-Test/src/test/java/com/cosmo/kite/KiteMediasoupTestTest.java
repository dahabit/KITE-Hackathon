/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite;

import com.cosmo.kite.mediasoup.KiteMediasoupTest;
import com.cosmo.kite.tests.KiteLoadTest;
import com.cosmo.kite.util.TestUtils;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.webrtc.kite.config.EndPoint;

import javax.json.JsonObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cosmo.kite.util.TestHelper.jsonToString;

public class KiteMediasoupTestTest extends TestCase {

  static {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
    System.setProperty("current.date", dateFormat.format(new Date()));
  }

  //Logger must be called after setting the system property "current.data"
  private final Logger logger = Logger.getLogger(this.getClass().getName());

  private static final String TEST_NAME = "Mediasoup UnitTest";
  private static final String CONFIG_FILE = "configs/local.mediasoup.config.json";

  private List<WebDriver> webDriverList = new ArrayList<>();
  private List<EndPoint> endPointList = TestUtils.getEndPointList(CONFIG_FILE, "browsers");

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
    KiteLoadTest test = new KiteMediasoupTest();
    test.setGetStats(true);
    test.setDescription(TEST_NAME);
    test.setPayload(TestUtils.getPayload(CONFIG_FILE, 0));
    test.setEndPointList(endPointList);
    JsonObject testResult = test.execute();
    logger.info("Test result = \r\n" + jsonToString(testResult));
  }
}
