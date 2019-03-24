package com.cosmo.kite.mediasoup.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class MediasoupPage {

  private final WebDriver webDriver;

  private final String PUBLISHING = "//b[text()='Publishing...']";
  
  @FindBy(tagName="video")
  private List<WebElement> videos;

  @FindBy(xpath=PUBLISHING)
  private WebElement publishing;

  public MediasoupPage(WebDriver webDriver) {
    this.webDriver = webDriver;
    PageFactory.initElements(webDriver, this);
  }


  /**
   *
   * @param timeout
   * @throws TimeoutException if the element is not invisible within the timeout
   */
  public void videoIsPublishing(int timeout) throws TimeoutException {
    WebDriverWait wait = new WebDriverWait(webDriver, timeout);
    WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PUBLISHING)));
    wait.until(ExpectedConditions.invisibilityOf(element));
  }

  /**
   *
   * @return the list of video elements
   */
  public List<WebElement> getVideoElements() {
    return videos;
  }

}
