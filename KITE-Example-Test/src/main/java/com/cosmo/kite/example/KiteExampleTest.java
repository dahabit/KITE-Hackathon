package com.cosmo.kite.example;

import com.cosmo.kite.example.checks.GoogleFirstResultCheck;
import com.cosmo.kite.example.steps.GoogleSearchStep;
import com.cosmo.kite.tests.KiteCallable;
import com.cosmo.kite.tests.KiteBaseTest;
import org.openqa.selenium.WebDriver;

public class KiteExampleTest extends KiteBaseTest {
  
  @Override
  public void setTestScript() {
    for (KiteCallable callable : this.callables) {
      callable.addStep(new GoogleSearchStep(callable.getWebDriver()));
      callable.addStep(new GoogleFirstResultCheck(callable.getWebDriver()));
    }
  }
  
  @Override
  protected void payloadHandling() {
  
  }
  
}
