package com.cosmo.kite.janus.steps;

import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.steps.TestStep;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.util.ReportUtils.saveScreenshotPNG;
import static com.cosmo.kite.util.ReportUtils.timestamp;
import static com.cosmo.kite.util.TestUtils.waitAround;

public class StayInMeetingStep extends TestStep {

  private final int meetingDuration;

  public StayInMeetingStep(WebDriver webDriver, int meetingDuration) {
    super(webDriver);
    this.meetingDuration = meetingDuration;
  }
  
  
  @Override
  public String stepDescription() {
    return "Stay in the meeting for " + meetingDuration + "s.";
  }
  
  @Override
  protected void step() {
    waitAround(meetingDuration * 1000);
  }
}
