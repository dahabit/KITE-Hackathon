package com.cosmo.kite.janus.steps;

import com.cosmo.kite.janus.pages.JanusPage;
import com.cosmo.kite.steps.TestStep;
import org.apache.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.util.WebDriverUtils.loadPage;

public class JoinVideoCallStep extends TestStep {



  private final JanusPage janusPage = new JanusPage(this.webDriver);
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
    janusPage.load(url);
  }
}
