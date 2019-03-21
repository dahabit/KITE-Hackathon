package com.cosmo.kite.simulcast.steps;

import com.cosmo.kite.steps.TestStep;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static com.cosmo.kite.util.TestUtils.waitAround;
import static com.cosmo.kite.util.WebDriverUtils.loadPage;

public class LoadPageStep extends TestStep {

  private final String url;

  
  public LoadPageStep(WebDriver webDriver, String url) {
    super(webDriver);
    this.url = url;
  }
  
  @Override
  public String stepDescription() {
    return "Open " + url;
  }
  
  @Override
  protected void step() {
    loadPage(webDriver, url, 20);
    waitAround(ONE_SECOND_INTERVAL);
  }
}
