package com.cosmo.kite.simulcast.steps;

import com.cosmo.kite.steps.TestStep;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
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
    waitAround(meetingDuration * ONE_SECOND_INTERVAL);
  }
}
