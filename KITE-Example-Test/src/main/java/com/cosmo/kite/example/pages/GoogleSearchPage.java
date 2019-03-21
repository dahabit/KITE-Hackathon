package com.cosmo.kite.example.pages;

import com.cosmo.kite.entities.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class GoogleSearchPage extends BasePage {
  
  @FindBy(className="gLFyf")
  WebElement searhBar;
  
  public GoogleSearchPage(WebDriver webDriver) {
    super(webDriver);
  }
  
  public void searchFor(String target) {
    searhBar.sendKeys(target + "\n");
  }
}
