package com.cosmo.kite.mediasoup.steps;

import com.cosmo.kite.mediasoup.pages.MediasoupPage;
import com.cosmo.kite.steps.TestStep;
import org.apache.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.util.WebDriverUtils.loadPage;

public class JoinVideoCallStep extends TestStep {

  private final MediasoupPage mediasoupPage = new MediasoupPage(this.webDriver);
  private final String url;

  
  public JoinVideoCallStep(WebDriver webDriver, String url) {
    super(webDriver);
    this.url = url;
  }
  
  @Override
  public String stepDescription() {
    return "Open " + url;
  }
  
  @Override
  protected void step() {
    logger.info("Opening " + url);
    loadPage(webDriver, url, 20);

    //try reloading 3 times as it sometimesgets stuck at 'publishing...'
    for (int i = 0; i < 3; i++) {
      try {
        mediasoupPage.videoIsPublishing( 10);
        logger.info("Page loaded successfully");
        break;
      } catch (TimeoutException e) {
        logger.warn(" reloading the page (" + (i + 1) + "/3)");
        loadPage(webDriver, url, 20);
      }
    }
  }
}
  
