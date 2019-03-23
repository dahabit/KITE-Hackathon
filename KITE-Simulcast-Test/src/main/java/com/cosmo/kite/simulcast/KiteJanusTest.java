package com.cosmo.kite.simulcast;

import com.cosmo.kite.simulcast.checks.BandwidthCheck;
import com.cosmo.kite.simulcast.checks.GaugesCheck;
import com.cosmo.kite.simulcast.checks.ReceiverVideoCheck;
import com.cosmo.kite.simulcast.checks.SenderVideoCheck;
import com.cosmo.kite.simulcast.pages.JanusLoopbackPage;
import com.cosmo.kite.simulcast.steps.*;
import com.cosmo.kite.tests.KiteCallable;
import com.cosmo.kite.tests.KiteLoadTest;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import javax.json.JsonObject;

import static org.webrtc.kite.Utility.getStackTrace;

public class KiteJanusTest extends KiteLoadTest {


  private static final Logger logger = Logger.getLogger(KiteJanusTest.class.getName());

  private int loadReachTime = 0;
  private int bandwidthCheckDuration = 0;
  private boolean checkSimulcast = true;

  private final String[] rids = {"a", "b", "c"};
  private final int[] tids = {0, 1, 2};

  @Override
  protected void payloadHandling() {
    super.payloadHandling();
    JsonObject jsonPayload = (JsonObject) this.payload;
    if (jsonPayload != null) {

      testName = jsonPayload.getString("name", "Simulcast Test");
      checkSimulcast = jsonPayload.getBoolean("checkSimulcast", checkSimulcast);
      loadReachTime = jsonPayload.getInt("loadReachTime", loadReachTime);
      bandwidthCheckDuration = jsonPayload.getInt("bandwidthCheckDuration", loadReachTime);
      expectedTestDuration = Math.max(expectedTestDuration, (loadReachTime + 300)/60);
    }
  }

  @Override
  protected void populateCallables() {
    try {
      for (WebDriver webDriver : this.webDriverList) {
        KiteCallable callable = new KiteCallable(webDriver, this.report);
        callable.addStep(new LoadPageStep(webDriver, this.url));
        if (!this.fastRampUp()) {
          JanusLoopbackPage page = new JanusLoopbackPage(webDriver);
          callable.addStep(new SenderVideoCheck(webDriver, page));
          callable.addStep(new ReceiverVideoCheck(webDriver, page));
          if (this.getStats()) {
            callable.addStep(
                new GetStatsStep(
                    webDriver,
                    getStatsCollectionTime(),
                    getStatsCollectionInterval(),
                    getSelectedStats(),
                  "echotest.webrtcStuff.pc"));
          }
          if (this.takeScreenshotForEachTest()) {
            callable.addStep(new ScreenshotStep(webDriver));
          }
          if (bandwidthCheckDuration > 0) {
            callable.addStep(new BandwidthCheck(webDriver, page, 1000000, bandwidthCheckDuration));
          }
          if (checkSimulcast) {
            for (String rid : rids) {
              if (this.url.contains("h264")) {
                callable.addStep(new SelectProfileStep(webDriver, page, rid, -1));
                callable.addStep(new GaugesCheck(webDriver, page, rid, -1));
              } else {
                for (int tid : tids) {
                  callable.addStep(new SelectProfileStep(webDriver, page, rid, tid));
                  callable.addStep(new GaugesCheck(webDriver, page, rid, tid));
                }
              }
            }
          }
        }
        this.callables.add(callable);
      }
    } catch (Exception e) {
      logger.error(getStackTrace(e));
    }
  }
}
