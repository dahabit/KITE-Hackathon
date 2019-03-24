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

import static com.cosmo.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static com.cosmo.kite.util.TestUtils.videoCheck;
import static com.cosmo.kite.util.TestUtils.waitAround;

public class AllVideoCheck extends TestStep {

  private final MediasoupPage mediasoupPage = new MediasoupPage(this.webDriver);
  private final int numberOfParticipants;

  public AllVideoCheck(WebDriver webDriver, int numberOfParticipants) {
    super(webDriver);
    this.numberOfParticipants = numberOfParticipants;
  }

  @Override
  public String stepDescription() {
    return "Check the other videos are being received OK";
  }

  @Override
  protected void step() throws KiteTestException {
    try {
      //wait a while to allow all videos to load.
      waitAround(numberOfParticipants * 3 * ONE_SECOND_INTERVAL);
      logger.info("Looking for video elements");
      List<WebElement> videos = mediasoupPage.getVideoElements();
      if (videos.size() < numberOfParticipants) {
        throw new KiteTestException(
            "Unable to find " + numberOfParticipants + " <video> element on the page. No video found = "
              + videos.size(), Status.FAILED);
      }
      String videoCheck = "";
      boolean error = false;
      for (int i = 1; i < numberOfParticipants; i++) {
        String v = videoCheck(webDriver, i);
        videoCheck += v;
        if (i < numberOfParticipants - 1) {
          videoCheck += "|";
        }
        if (!"video".equalsIgnoreCase(v)) {
          error = true;
        }
      }
      if (error) {
        Reporter.getInstance().textAttachment(report, "Reveived Videos", videoCheck, "plain");
        throw new KiteTestException("Some videos are still or blank: " + videoCheck, Status.FAILED);
      }
    } catch (KiteTestException e) {
      throw e;
    } catch (Exception e) {
      throw new KiteTestException("Error looking for the video", Status.BROKEN, e);
    }
  }
}
