package com.cosmo.kite.mediasoup.checks;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.mediasoup.pages.MediasoupPage;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.steps.TestStep;
import com.cosmo.kite.report.custom_kite_allure.Status;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.cosmo.kite.util.TestUtils.videoCheck;

public class FirstVideoCheck extends TestStep {

  private final MediasoupPage mediasoupPage = new MediasoupPage(this.webDriver);

  public FirstVideoCheck(WebDriver webDriver) {
    super(webDriver);
  }

  @Override
  public String stepDescription() {
    return "Check the first video is being sent OK";
  }

  @Override
  protected void step() throws KiteTestException {
    try {
      logger.info("Looking for video object");
      List<WebElement> videos = mediasoupPage.getVideoElements();
      if (videos.isEmpty()) {
        throw new KiteTestException(
            "Unable to find any <video> element on the page", Status.FAILED);
      }

      String videoCheck = videoCheck(webDriver, 0);
      if (!"video".equalsIgnoreCase(videoCheck)) {
        Reporter.getInstance().textAttachment(report, "Sent Video", videoCheck, "plain");
        throw new KiteTestException("The first video is " + videoCheck, Status.FAILED);
      }
    } catch (KiteTestException e) {
      throw e;
    } catch (Exception e) {
      throw new KiteTestException("Error looking for the video", Status.BROKEN, e);
    }
  }
}
