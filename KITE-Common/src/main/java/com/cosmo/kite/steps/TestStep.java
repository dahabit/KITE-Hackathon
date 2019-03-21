package com.cosmo.kite.steps;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.report.custom_kite_allure.AllureStepReport;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.report.custom_kite_allure.Status;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.openqa.selenium.WebDriver;

import static com.cosmo.kite.util.ReportUtils.*;

public abstract class TestStep {


  protected final Logger logger = Logger.getLogger(this.getClass().getName());

  protected final WebDriver webDriver;
  protected String message;
  protected AllureStepReport report;
  
  
  public TestStep(WebDriver webDriver) {
    this.webDriver = webDriver;
    MDC.put("prefix", getLogHeader(this.webDriver));
  }
    
  public void execute() {
    try {
      logger.info("Executing step: " + stepDescription());
      step();
    } catch (Exception e) {
      Reporter.getInstance().processException(this.report, e);
    }
  }
  
  public void skip() {
    logger.warn("Skipping step: " + stepDescription());
    this.report.setStatus(Status.SKIPPED);
  }
  
  public void init(){
    this.report = new AllureStepReport(getLogHeader(webDriver) + ": " + stepDescription());
    this.report.setDescription(stepDescription());
    this.report.setStartTimestamp();
  }
  
  public void finish(){
    if (message != null) {
      Reporter.getInstance().textAttachment(report,"Error message", message, "plain");
    }
  }
  
  public AllureStepReport getStepReport() {
    return report;
  }
  
  public abstract String stepDescription();
  
  protected abstract void step() throws KiteTestException;
}
