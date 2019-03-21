package com.cosmo.kite.entities;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public abstract class BasePage {
  protected final Logger logger = Logger.getLogger(this.getClass().getName());
  protected final WebDriver webDriver;
  
  
  protected BasePage(WebDriver webDriver) {
    this.webDriver = webDriver;
    PageFactory.initElements(webDriver, this);
  }
}
