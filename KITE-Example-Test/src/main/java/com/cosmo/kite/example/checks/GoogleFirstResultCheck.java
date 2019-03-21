package com.cosmo.kite.example.checks;

import com.cosmo.kite.example.pages.GoogleResultPage;
import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.steps.TestCheck;
import com.cosmo.kite.steps.TestStep;
import com.cosmo.kite.report.custom_kite_allure.Status;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.util.ReportUtils.saveScreenshotPNG;
import static com.cosmo.kite.util.ReportUtils.timestamp;

public class GoogleFirstResultCheck extends TestCheck {
  final GoogleResultPage searchPage = new GoogleResultPage(this.webDriver);
  final String EXPECTED_RESULT = "CoSMo Software: RTC Experts";
  
  public GoogleFirstResultCheck(WebDriver webDriver) {
    super(webDriver);
  }
  
  @Override
  public String stepDescription() {
    return "Open first result on Google result page and verify the page title";
  }
  
  @Override
  protected void step() throws KiteTestException {
    searchPage.openFirstResult();
    String found = webDriver.getTitle().trim();
    if (!found.equalsIgnoreCase(EXPECTED_RESULT)){
      throw new KiteTestException("The title of the first Google result was not correct: \n" +
        "Expected: " + EXPECTED_RESULT + " but found " + found, Status.FAILED);
    }
    Reporter.getInstance().screenshotAttachment(report, saveScreenshotPNG(webDriver));
  }
}
