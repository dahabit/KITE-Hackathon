package com.cosmo.kite.example.steps;

import com.cosmo.kite.example.pages.GoogleSearchPage;
import com.cosmo.kite.steps.TestStep;
import org.openqa.selenium.WebDriver;

public class GoogleSearchStep extends TestStep {
  
  final String TARGET = "CoSMo Software Consulting";
  final String GOOGLE_PAGE = "https://google.com";
  final GoogleSearchPage searchPage = new GoogleSearchPage(this.webDriver);
  
  public GoogleSearchStep(WebDriver webDriver) {
    super(webDriver);
  }
  
  
  @Override
  public String stepDescription() {
    return "Open " + GOOGLE_PAGE + " and look for " + TARGET;
  }
  
  @Override
  protected void step() {
    this.webDriver.get(GOOGLE_PAGE);
    searchPage.searchFor(TARGET);
  }
}
