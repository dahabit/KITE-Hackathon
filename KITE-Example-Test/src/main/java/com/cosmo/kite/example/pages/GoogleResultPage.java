package com.cosmo.kite.example.pages;

import com.cosmo.kite.entities.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class GoogleResultPage extends BasePage {
  
  @FindBy(className="LC20lb")
  WebElement result;
  
  public GoogleResultPage(WebDriver webDriver) {
    super(webDriver);
  }
  
  public void openFirstResult() {
    result.click();
  }
}
