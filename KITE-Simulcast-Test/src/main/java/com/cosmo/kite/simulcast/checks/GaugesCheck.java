package com.cosmo.kite.simulcast.checks;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.simulcast.LoopbackStats;
import com.cosmo.kite.simulcast.pages.MedoozeLoopbackPage;
import com.cosmo.kite.steps.TestStep;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.util.ReportUtils.saveScreenshotPNG;
import static com.cosmo.kite.util.ReportUtils.timestamp;

public class GaugesCheck extends TestStep {

  private final MedoozeLoopbackPage loopbackPage;


  private final String rid;
  private final int tid;

  public GaugesCheck(WebDriver webDriver, String rid, int tid) {
    super(webDriver);
    this.loopbackPage = new MedoozeLoopbackPage(webDriver);
    this.rid = rid;
    this.tid = tid;
  }
  
  @Override
  public String stepDescription() {
    return "Gauges values for profile " + rid + tid;
  }
  
  @Override
  protected void step() throws KiteTestException {
    LoopbackStats loopbackStats = loopbackPage.getLoopbackStats();
    Reporter.getInstance().jsonAttachment(report, "stats", loopbackStats.getJson());
    Reporter.getInstance().screenshotAttachment(report,
      "Gauges_" + rid + tid + "_" + timestamp(), saveScreenshotPNG(webDriver));
    loopbackStats.validate(rid);
  }
}
