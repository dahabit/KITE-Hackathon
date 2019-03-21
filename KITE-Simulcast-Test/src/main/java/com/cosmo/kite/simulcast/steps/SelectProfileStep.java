package com.cosmo.kite.simulcast.steps;

import com.cosmo.kite.simulcast.pages.MedoozeLoopbackPage;
import com.cosmo.kite.steps.TestStep;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static com.cosmo.kite.util.TestUtils.waitAround;

public class SelectProfileStep extends TestStep {

  private final MedoozeLoopbackPage loopbackPage;

  private final String rid;
  private final int tid;

  public SelectProfileStep(WebDriver webDriver, String rid, int tid) {
    super(webDriver);
    this.loopbackPage = new MedoozeLoopbackPage(webDriver);
    this.rid = rid;
    this.tid = tid;
  }
  
  @Override
  public String stepDescription() {
    return "Clicking button " + rid + tid;
  }
  
  @Override
  protected void step() {
    loopbackPage.clickButton(rid, tid);
    waitAround(3 * ONE_SECOND_INTERVAL);
  }
}
