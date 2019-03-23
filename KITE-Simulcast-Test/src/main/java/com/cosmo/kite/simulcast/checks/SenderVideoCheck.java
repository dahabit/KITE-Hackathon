package com.cosmo.kite.simulcast.checks;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.simulcast.pages.SimulcastPageBase;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static com.cosmo.kite.util.TestUtils.waitAround;

public class SenderVideoCheck extends VideoCheckBase {

  public SenderVideoCheck(WebDriver webDriver, SimulcastPageBase page) {
    super(webDriver, page);
  }

  @Override
  public String stepDescription() {
    return "Check the first video is being sent OK";
  }

  @Override
  protected void step() throws KiteTestException {
    waitAround(ONE_SECOND_INTERVAL);
    step("sent");
  }
}
