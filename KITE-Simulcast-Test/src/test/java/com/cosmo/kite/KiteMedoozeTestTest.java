/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite;

import com.cosmo.kite.simulcast.KiteMedoozeTest;
import com.cosmo.kite.tests.KiteLoadTest;
import com.cosmo.kite.util.TestUtils;
import junit.framework.TestCase;
import org.openqa.selenium.WebDriver;
import org.webrtc.kite.Utility;
import org.webrtc.kite.config.EndPoint;

import javax.json.JsonObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KiteMedoozeTestTest extends TestCase {

  static {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
    System.setProperty("current.date", dateFormat.format(new Date()));
  }

  private static final String TEST_NAME = "Medooze Simulcast UnitTest";
  private static final String CONFIG_FILE = "configs/local.simulcast.config.json";

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
    KiteLoadTest test = new KiteMedoozeTest();
    test.setGetStats(true);
    test.setDescription(TEST_NAME);
    test.setPayload(TestUtils.getPayload(CONFIG_FILE, 0));
    test.setEndPointList(endPointList);
    JsonObject testResult = test.execute();
  }
}
