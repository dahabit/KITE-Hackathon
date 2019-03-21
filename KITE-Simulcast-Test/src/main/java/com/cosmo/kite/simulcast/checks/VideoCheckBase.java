package com.cosmo.kite.simulcast.checks;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.report.custom_kite_allure.Status;
import com.cosmo.kite.simulcast.pages.MedoozeLoopbackPage;
import com.cosmo.kite.steps.TestStep;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.cosmo.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static com.cosmo.kite.util.ReportUtils.saveScreenshotPNG;
import static com.cosmo.kite.util.ReportUtils.timestamp;
import static com.cosmo.kite.util.TestUtils.videoCheck;
import static com.cosmo.kite.util.TestUtils.waitAround;

public abstract class VideoCheckBase extends TestStep {

  protected final MedoozeLoopbackPage medoozeLoopbackPage = new MedoozeLoopbackPage(this.webDriver);

  public VideoCheckBase(WebDriver webDriver) {
    super(webDriver);
  }

  protected void step(String direction) throws KiteTestException {
    try {
      List<WebElement> videos = medoozeLoopbackPage.getVideoElements();
      if (videos.isEmpty()) {
        throw new KiteTestException(
            "Unable to find any <video> element on the page", Status.FAILED);
      }
      String videoCheck = videoCheck(webDriver, "sent".equalsIgnoreCase(direction) ? 0 : 1);
      int ct = 0;
      while(!"video".equalsIgnoreCase(videoCheck) && ct < 3) {
        videoCheck = videoCheck(webDriver, 1);
        ct++;
        waitAround(3 * ONE_SECOND_INTERVAL);
      }
      if (!"video".equalsIgnoreCase(videoCheck)) {
        Reporter.getInstance().textAttachment(report, direction +" video", videoCheck, "plain");
        Reporter.getInstance().screenshotAttachment(report,
          direction + "_video_" + timestamp(), saveScreenshotPNG(webDriver));
        throw new KiteTestException("The " + direction + " video is " + videoCheck, Status.FAILED, null, true);
      }
      logger.info(direction + " video is OK");
    } catch (KiteTestException e) {
      throw e;
    } catch (Exception e) {
      throw new KiteTestException("Error looking for the video", Status.BROKEN, e);
    }
  }
}
