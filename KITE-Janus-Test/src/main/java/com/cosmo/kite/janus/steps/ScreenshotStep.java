package com.cosmo.kite.janus.steps;

import com.cosmo.kite.janus.pages.JanusPage;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.steps.TestStep;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.util.ReportUtils.saveScreenshotPNG;
import static com.cosmo.kite.util.ReportUtils.timestamp;

public class ScreenshotStep extends TestStep {

  public ScreenshotStep(WebDriver webDriver) {
    super(webDriver);
  }
  
  
  @Override
  public String stepDescription() {
    return "Get a screenshot";
  }
  
  @Override
  protected void step() {
    Reporter.getInstance().screenshotAttachment(report,
      "ScreenshotStep_" + timestamp(), saveScreenshotPNG(webDriver));
  }
}
