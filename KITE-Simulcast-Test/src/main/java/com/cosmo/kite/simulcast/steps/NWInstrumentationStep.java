package com.cosmo.kite.simulcast.steps;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.instrumentation.NWInstConfig;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.steps.TestStep;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static com.cosmo.kite.util.TestUtils.waitAround;

public class NWInstrumentationStep extends TestStep {

  private final NWInstConfig nwInstConfig;

  public NWInstrumentationStep(WebDriver webDriver, NWInstConfig nwInstConfig) {
    super(webDriver);
    this.nwInstConfig = nwInstConfig;
  }
  
  
  @Override
  public String stepDescription() {
    return "Network Instrumentation";
  }
  
  @Override
  protected void step() throws KiteTestException {
    Reporter.getInstance().textAttachment(report, "NW Instrumentation", nwInstConfig.toString(), "plain");
    waitAround( ONE_SECOND_INTERVAL);
    nwInstConfig.runGatewayCommands(0);
    waitAround( 10 * ONE_SECOND_INTERVAL);
  }
}
