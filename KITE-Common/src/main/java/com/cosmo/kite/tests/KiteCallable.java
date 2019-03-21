package com.cosmo.kite.tests;

import com.cosmo.kite.report.custom_kite_allure.AllureTestReport;
import com.cosmo.kite.steps.TestStep;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.cosmo.kite.util.TestUtils.processTestStep;

public class KiteCallable implements Callable<Object> {
  
  protected final Logger logger = Logger.getLogger(this.getClass().getName());
  protected final AllureTestReport testReport;
  protected final WebDriver webDriver;
  protected final List<TestStep> steps;
  
  public KiteCallable(WebDriver webDriver, AllureTestReport testReport) {
    this.webDriver = webDriver;
    this.testReport = testReport;
    this.steps = new ArrayList<>();
  }
  
  public void addStep(TestStep step) {
    this.steps.add(step);
  }
  
  @Override
  public Object call() {
    for (TestStep step : steps) {
      processTestStep(step, testReport);
    }
    return null;
  }
  
  public List<TestStep> getSteps() {
    return steps;
  }
  
  public WebDriver getWebDriver() {
    return webDriver;
  }
}
