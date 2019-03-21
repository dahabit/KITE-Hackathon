/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.util;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.report.custom_kite_allure.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.cosmo.kite.entities.Timeouts.*;
import static com.cosmo.kite.util.TestUtils.waitAround;

/**
 * The type Web driver utils.
 */
public class WebDriverUtils {
  
  /**
   * Handles alert popup if exists
   *
   * @param webDriver the web driver
   *
   * @return String alert message
   */
  public static String alertHandling(WebDriver webDriver) {
    String alertMsg;
    try {
      Alert alert = webDriver.switchTo().alert();
      alertMsg = alert.getText();
      if (alertMsg != null) {
        alertMsg =
          ((RemoteWebDriver) webDriver).getCapabilities().getBrowserName()
            + " alert: "
            + alertMsg;
        alert.accept();
      }
    } catch (ClassCastException e) {
      alertMsg = " Cannot retrieve alert message due to alert.getText() class cast problem.";
      webDriver.switchTo().alert().accept();
    } catch (Exception e) {
      alertMsg = null;
    }
    return alertMsg;
  }
  
  /**
   * Analyze log list.
   *
   * @param webDriver the subject web driver that we want to get console log
   *
   * @return List of log entries.
   */
  public static List<String> analyzeLog(WebDriver webDriver) {
    List<String> log = new ArrayList<>();
    Set<String> logTypes = webDriver.manage().logs().getAvailableLogTypes();
    if (logTypes.contains(LogType.BROWSER)) {
      LogEntries logEntries = webDriver.manage().logs().get(LogType.BROWSER);
      for (LogEntry entry : logEntries) {
        log.add(entry.getLevel() + " " + entry.getMessage().replaceAll("'", ""));
      }
    } else {
      log.add("This browser does not support getting console log.");
    }
    return log;
  }
  
  /**
   * Finds out if an element with given name and xpath value exists on current page.
   *
   * @param webDriver the webdriver.
   * @param selector  given selector to locate element
   *
   * @return true if the element is visible
   */
  public static boolean elementExist(WebDriver webDriver, By selector) {
    try {
      WebElement element = webDriver.findElement(selector);
      return element != null;
    } catch (Exception e) {
      return false;
    }
  }
  
  /**
   * Executes a JS script string with a given webdriver
   *
   * @param webDriver    the webdriver
   * @param scriptString the JS script to execute
   *
   * @return the result of the script execution
   */
  public static Object executeJsScript(WebDriver webDriver, String scriptString) {
    return ((JavascriptExecutor) webDriver).executeScript(scriptString);
  }
  
  /**
   * Find all elements with the corresponding locator and iterate through the list to find the one
   * that is required.
   *
   * @param webDriver the web driver
   * @param locator   locator to locate the elements
   * @param attribute type of the attribute we
   * @param value     the value
   *
   * @return web element
   */
  public static WebElement findElementWithCondition(
    WebDriver webDriver, By locator, String attribute, String value) {
    List<WebElement> elements = webDriver.findElements(locator);
    for (WebElement element : elements) {
      switch (attribute) {
        case "text": {
          if (element.getText().equalsIgnoreCase(value) ||
            element.getCssValue("value").equalsIgnoreCase(value)) {
            return element;
          }
          break;
        }
        default: {
          if (element.getAttribute(attribute).equalsIgnoreCase(value)) {
            return element;
          }
          break;
        }
      }
    }
    return null;
  }
  
  /**
   * Gets the webDriver as a JsonObject:
   * {
   * "browserName": "chrome",
   * "version": "70",
   * "platform": "WINDOWS"
   * }
   *
   * @param webDriver the webDriver
   *
   * @return the webDriver as a JsonObject
   */
  public static JsonObject getCapabilityJsonFromWebDriver(WebDriver webDriver) {
    RemoteWebDriver w = (RemoteWebDriver) webDriver;
    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
    jsonObjectBuilder.add("browserName", w.getCapabilities().getBrowserName());
    jsonObjectBuilder.add("version", w.getCapabilities().getVersion());
    jsonObjectBuilder.add("platform", w.getCapabilities().getPlatform().toString());
    return jsonObjectBuilder.build();
  }
  
  /**
   * Retrieves browser console log if possible.
   *
   * @param webDriver the web driver
   *
   * @return console log
   */
  public static JsonArrayBuilder getConsoleLog(WebDriver webDriver) {
    JsonArrayBuilder log = Json.createArrayBuilder();
    List<String> logEntries = analyzeLog(webDriver);
    for (String entry : logEntries) {
      log.add(entry);
    }
    return log;
  }
  
  /**
   * Get the web element from web driver
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   *
   * @return the corresponding WebElement Object
   * @throws KiteTestException if an the element can not be found.
   */
  public static WebElement getElement(WebDriver webDriver, String elementDescription, By selector) throws KiteTestException {
    return getElement(webDriver, elementDescription, selector, -1);
  }
  
  /**
   * Get the web element from web driver with index
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   * @param index              index of element with given selector
   *
   * @return the corresponding WebElement Object
   * @throws KiteTestException if an the element can not be found.
   */
  public static WebElement getElement(WebDriver webDriver, String elementDescription, By selector, int index) throws KiteTestException {
    waitForElement(webDriver, selector);
    if (index == -1) {
      try {
        return webDriver.findElement(selector);
      } catch (Exception e) {
        throw new KiteTestException("Could not find element: \"" + elementDescription + "\"", Status.BROKEN);
      }
    } else {
      WebElement element = webDriver.findElements(selector).get(index);
      if (element != null) {
        return element;
      } else {
        throw new KiteTestException("Could not find element: \"" + elementDescription + "\"", Status.BROKEN);
      }
    }
  }
  
  /**
   * Switches to a new tab on a webdriver.
   *
   * @param webDriver the webdriver
   *
   * @return the other window handler
   * @throws KiteTestException the kite test exception
   */
  public static String getOtherWindowHandler(WebDriver webDriver) throws KiteTestException {
    String currentWindowHandler = webDriver.getWindowHandle();
    for (String windowHandler : webDriver.getWindowHandles()) {
      if (windowHandler != currentWindowHandler) {
        return windowHandler;
      }
    }
    return currentWindowHandler;
  }
  
  /**
   * Finds and get text from an element with given name and a given selector.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given name of the element, for logging purpose
   * @param selector           given selector to locate element
   *
   * @return the text
   * @throws KiteTestException if an Exception occurs during method execution or fail.
   */
  public static String getText(WebDriver webDriver, String elementDescription, By selector) throws KiteTestException {
    WebElement element = getElement(webDriver, elementDescription, selector);
    try {
      String text = element.getText().trim();
      return text.isEmpty() ? null : text;
    } catch (Exception e) {
      throw new KiteTestException(e.getClass().getName() + " getting text from " + elementDescription, Status.BROKEN);
    }
  }
  
  /**
   * Returns the dimension of the window if possible, if exception, returns a fixed one;
   *
   * @param webDriver the web driver
   *
   * @return window size
   */
  public static Dimension getWindowSize(WebDriver webDriver) {
    int fixedMeasure = 1024;
    try {
      return webDriver.manage().window().getSize();
    } catch (Exception e) {
      return new Dimension(fixedMeasure, fixedMeasure);
    }
  }
  
  /**
   * Looks for the number of elements with given selector
   *
   * @param webDriver      the webdriver.
   * @param selector       to locate the element
   * @param expectedNumber expected number of element visible on page.
   *
   * @return whether there are an exact number of element.
   */
  private static boolean hasExpectedNumberOfElements(WebDriver webDriver, By selector, int expectedNumber) {
    return webDriver.findElements(selector).size() == expectedNumber;
  }
  
  /**
   * Is electron boolean.
   *
   * @param webDriver the web driver
   *
   * @return true if it is electron
   */
  public static boolean isElectron(WebDriver webDriver) {
    Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
    return "chrome".equals(capabilities.getBrowserName()) && capabilities.getVersion().isEmpty();
  }
  
  /**
   * Is ios boolean.
   *
   * @param webDriver the web driver
   *
   * @return whether the webdriver is on iOS
   */
  public static boolean isIOS(WebDriver webDriver) {
    if (isMobileApp(webDriver)) {
      Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
      String platform = capabilities.getPlatform().toString();
      return platform.toUpperCase().equalsIgnoreCase("MAC");
    }
    return false;
  }
  
  /**
   * Is mobile app boolean.
   *
   * @param webDriver the web driver
   *
   * @return whether the webdriver is in on a mobile app
   */
  public static boolean isMobileApp(WebDriver webDriver) {
    Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
    return capabilities.getBrowserName().isEmpty();
  }
  
  /**
   * Finds out if an element with given name and xpath value is visible on current page.
   *
   * @param webDriver the webdriver.
   * @param selector  given selector to locate element
   *
   * @return true if the element is visible
   */
  public static boolean isVisible(WebDriver webDriver, By selector) {
    try {
      WebElement element = webDriver.findElement(selector);
      return element != null && element.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }
  
  /**
   * Open a new tab (window handler should be on the new tab directly).
   *
   * @param webDriver the web driver
   */
  public static void openNewTab(WebDriver webDriver) {
    webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
  }
  
  /**
   * Finds and sends a String to an element with given name and xpath value.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given name of the element, for logging purpose
   * @param selector           given selector to locate element
   * @param message            String to send
   *
   * @throws KiteTestException if an Exception occurs during method execution or fail.
   */
  public static void sendKeysToElement(WebDriver webDriver, String elementDescription, By selector, String message) throws KiteTestException {
    WebElement element = getElement(webDriver, elementDescription, selector);
    try {
      waitAround(ONE_SECOND_INTERVAL / 2);
      try {
        if (!element.getAttribute("value").isEmpty()) {
          element.clear();
        }
        if (!element.getText().isEmpty()) {
          element.clear();
        }
      } catch (Exception e) {
        // Ignore, just a check to see if the element has any existing value/ text.
      }
      element.sendKeys(message);
    } catch (Exception e) {
      throw new KiteTestException("Could not send key to " + elementDescription, Status.BROKEN, e);
    }
  }
  
  /**
   * Sets implicit wait for the webdriver in milliseconds.
   *
   * @param webDriver          the web driver
   * @param waitInMilliSeconds the wait in milli seconds
   */
  public static void setImplicitWait(WebDriver webDriver, int waitInMilliSeconds) {
    webDriver.manage().timeouts().implicitlyWait(waitInMilliSeconds, TimeUnit.MILLISECONDS);
  }
  
  
  /**
   * Switches to a new tab on a webdriver.
   *
   * @param webDriver the webdriver
   *
   * @throws KiteTestException the kite test exception
   */
  public static void switchWindowHandler(WebDriver webDriver) throws KiteTestException {
    String currentWindowHandler = webDriver.getWindowHandle();
    boolean changed = false;
    String windowHandler = getOtherWindowHandler(webDriver);
    if (windowHandler != currentWindowHandler) {
      webDriver.switchTo().window(windowHandler);
      changed = true;
    }
    if (!changed) {
      throw new KiteTestException(
        "Could not switch tab or window. Current window handler not found.", Status.BROKEN);
    }
  }
  
  /**
   * Waits for an element to be visible.
   *
   * @param webDriver the webdriver.
   * @param selector  to locate the element
   *
   * @throws KiteTestException if an Exception occurs during method execution or fail.
   */
  public static void waitForElement(WebDriver webDriver, By selector) throws KiteTestException {
    waitForElement(webDriver, selector, SHORT_TIMEOUT);
  }
  
  /**
   * Waits for an element to be visible
   *
   * @param webDriver the webdriver.
   * @param selector  to locate the element
   * @param timeout   defaultWait duration.
   *
   * @throws KiteTestException if an Exception occurs during method execution or fail.
   */
  public static void waitForElement(WebDriver webDriver, By selector, int timeout) throws KiteTestException {
    setImplicitWait(webDriver, ONE_SECOND_INTERVAL);
    WebDriverWait driverWait = new WebDriverWait(webDriver, 1);
    for (int waitTime = 0; waitTime < timeout; waitTime += ONE_SECOND_INTERVAL) {
      try {
        driverWait.until(ExpectedConditions.visibilityOfElementLocated(selector));
        if (webDriver.findElement(selector) != null) {
          setImplicitWait(webDriver, SHORT_TIMEOUT);
          return;
        }
      } catch (Exception e) {
        // in case the wait throw an abnormal exception, meaning the defaultWait does not defaultWait as it should.
        // logger.debug("Exception in waitForElement " + ReportUtils.getStackTrace(e)
        // ignore
        waitAround(ONE_SECOND_INTERVAL);
      }
    }
    throw new KiteTestException("Timeout waiting for element: " + selector.toString(), Status.BROKEN);
  }
  
  /**
   * Wait for existences of a number of certain elements on page
   *
   * @param webDriver      the webdriver.
   * @param selector       to locate the element
   * @param expectedNumber expected number of element visible on page
   *
   * @throws KiteTestException if an Exception occurs during method execution or fail.
   */
  public static void waitForExpectedNumberOfElements(WebDriver webDriver, By selector, int expectedNumber) throws KiteTestException {
    for (int waitTime = 0; waitTime < DEFAULT_TIMEOUT; waitTime += ONE_SECOND_INTERVAL) {
      try {
        waitAround(ONE_SECOND_INTERVAL / 2);
        if (hasExpectedNumberOfElements(webDriver, selector, expectedNumber)) {
          waitAround(ONE_SECOND_INTERVAL);
          return;
        }
      } catch (Exception e) {
        // in case the defaultWait throw an abnormal exception, meaning the defaultWait does not defaultWait as it should.
      }
      waitAround(ONE_SECOND_INTERVAL);
    }
    throw new KiteTestException("Timeout waiting for " + expectedNumber + " elements: " + selector.toString(), Status.BROKEN);
  }


  /**
   * Load the page, waiting for document.readyState to be complete
   * @param url the url of the web page
   * @param webDriver      the webdriver.
   */
  public static void loadPage(WebDriver webDriver, String url, int timeout) {
    WebDriverWait driverWait = new WebDriverWait(webDriver, timeout);
    webDriver.get(url);
    driverWait.until(driver ->
      ((JavascriptExecutor) driver)
        .executeScript("return document.readyState")
        .equals("complete"));
  }




}
