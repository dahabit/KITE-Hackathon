package com.cosmo.kite.janus.steps;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.instrumentation.NWInstConfig;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.steps.TestStep;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.util.TestUtils.waitAround;

public class NWInstCleanupStep extends TestStep {

  private final NWInstConfig nwInstConfig;

  public NWInstCleanupStep(WebDriver webDriver, NWInstConfig nwInstConfig) {
    super(webDriver);
    this.nwInstConfig = nwInstConfig;
  }


  @Override
  public String stepDescription() {
    return "Network Instrumentation Clean Up";
  }

  @Override
  protected void step() throws KiteTestException {
    nwInstConfig.cleanUp();
    waitAround(1000);
    logger.info("cleanUp() done.");

  }
}
