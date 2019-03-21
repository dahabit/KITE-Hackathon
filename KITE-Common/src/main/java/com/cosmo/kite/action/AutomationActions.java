/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.action;

import com.cosmo.kite.exception.KiteTestException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import com.cosmo.kite.report.custom_kite_allure.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static com.cosmo.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static com.cosmo.kite.util.TestUtils.waitAround;
import static com.cosmo.kite.util.WebDriverUtils.*;

/**
 * The type Test actions.
 */
public class AutomationActions {
  
  /**
   * Finds and clicks an element with given description and a selector.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   * @param action             true to use Actions to click the element instead of WebElement.click()
   *
   * @throws KiteTestException if an Exception occurs during method execution or fail.
   */
  public static void clickElement(WebDriver webDriver, String elementDescription, By selector, boolean action) throws KiteTestException {
    clickElement(webDriver, elementDescription, selector, 0, action);
  }
  
  /**
   * Finds and clicks an element with given description and a selector.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   *
   * @throws KiteTestException if an Exception occurs during method execution or fail.
   */
  public static void clickElement(WebDriver webDriver, String elementDescription, By selector) throws KiteTestException {
    clickElement(webDriver, elementDescription, selector, -1, false);
  }
  
  /**
   * Finds and clicks an element with given description and a selector.
   *
   * @param webDriver          the webdriver.
   * @param elementDescription given description of the element, for logging purpose
   * @param selector           given selector to locate element
   * @param index              index of the element with given selector
   * @param action             true to use Actions to click the element instead of WebElement.click()
   *
   * @throws KiteTestException if an Exception occurs during method execution or fail.
   */
  public static void clickElement(WebDriver webDriver, String elementDescription, By selector, int index, boolean action) throws KiteTestException {
    WebElement element = getElement(webDriver, elementDescription, selector, index);
    try {
      clickElement(webDriver, element, action);
      waitAround(ONE_SECOND_INTERVAL / 2);
    } catch (Exception e) {
      throw new KiteTestException(e.getClass().getName() + " in clicking element \"" + elementDescription + "\": " + e.getLocalizedMessage(), Status.BROKEN, e);
    }
  }
  
  /**
   * Click element.
   *
   * @param webDriver the webdriver
   * @param element   a given WebElement
   * @param action    true to use Actions to click the element instead of WebElement.click()
   *
   */
  public static void clickElement(WebDriver webDriver, WebElement element, boolean action) {
    if (action) {
      Actions actions = new Actions(webDriver);
      actions.moveToElement(element).click().perform();
    } else {
      element.click();
    }
  }
  
  /**
   * Click element.
   *
   * @param webDriver the webdriver
   * @param elementDescription given description of the element, for logging purpose
   * @param element   a given WebElement
   *
   */
  public static void clickElement(WebDriver webDriver,  String elementDescription, WebElement element) throws KiteTestException {
    try {
      clickElement(webDriver, element, false);
      waitAround(ONE_SECOND_INTERVAL / 2);
    } catch (Exception e) {
      throw new KiteTestException(e.getClass().getName() + " in clicking element \"" + elementDescription + "\": " + e.getLocalizedMessage(), Status.BROKEN, e);
    }
  }
  
  /**
   * Performs a double tap action at a specific point
   *
   * @param webDriver the web driver
   * @param x         the x
   * @param y         the y
   */
  public static void doubleTap(WebDriver webDriver, int x, int y) {
    if (isMobileApp(webDriver)) {
      new TouchAction((MobileDriver) webDriver)
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).press(PointOption.point(x, y))
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).release()
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).press(PointOption.point(x, y))
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).release()
        .perform();
    } else {
      new Actions(webDriver).moveByOffset(x, y).doubleClick().perform();
    }
  }
  
  /**
   * Press down at the begin point and move to end point. Can be used to draw a line if the cursor
   * is in drawing/annotating mode.
   *
   * @param webDriver the web driver.
   * @param begin     point to begin.
   * @param end       end point
   */
  public static void drawLine(WebDriver webDriver, Point begin, Point end) {
    if (isMobileApp(webDriver)) {
      TouchAction action = new TouchAction((MobileDriver) webDriver);
      action.press(PointOption.point(begin.x, begin.y))
        .moveTo(PointOption.point(end.x, end.y))
        .release().perform();
    } else {
      Actions action = new Actions(webDriver);
      action.moveByOffset(begin.x, begin.y)
        .clickAndHold()
        .moveByOffset(end.x, end.y)
        .release().perform();
    }
  }
  
  /**
   * Press down at the begin point and move to fill the area of a square. Can be used to draw a
   * square if the cursor is in drawing/annotating mode.
   *
   * @param webDriver        the web driver.
   * @param begin            point to begin.
   * @param squareSideLength length of the side of the square.
   */
  public static void drawSquare(WebDriver webDriver, Point begin, int squareSideLength) {
    final int waitTime = 50;
    int startX = begin.x;
    int startY = begin.y;
    
    if (isMobileApp(webDriver)) {
      TouchAction action = new TouchAction((MobileDriver) webDriver);
      action.press(PointOption.point(startX, startY));
      
      for (int measure = squareSideLength / 20; measure <= squareSideLength; measure += squareSideLength / 20) {
        int length = squareSideLength - measure;
        action.waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
          .moveTo(PointOption.point(startX + length, startY))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
          .moveTo(PointOption.point(startX + length, startY - length))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
          .moveTo(PointOption.point(startX, startY - length))
          .waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
          .moveTo(PointOption.point(startX, startY));
      }
      action.waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
        .release()
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(waitTime)))
        .perform();
    } else {
      Actions action = new Actions(webDriver);
      action.moveByOffset(startX, startY).clickAndHold();
      for (int measure = squareSideLength / 20; measure <= squareSideLength; measure += squareSideLength / 20) {
        int length = squareSideLength - measure;
        action.moveByOffset(length, 0)
          .moveByOffset(0, -length)
          .moveByOffset(-length, 0)
          .moveByOffset(0, length);
      }
      action.release().perform();
    }
  }
  
  /**
   * Resizes the windows to the screen's available width and height
   *
   * @param webDriver the web driver
   */
  public static void maximizeCurrentWindow(WebDriver webDriver) {
    if (!isElectron(webDriver)) {
      String getScreenHeight = "return screen.availHeight";
      String getScreenWidth = "return screen.availWidth";
      int screenHeight = (int) ((long) executeJsScript(webDriver, getScreenHeight));
      int screenWidth = (int) ((long) executeJsScript(webDriver, getScreenWidth));
      webDriver.manage().window().setSize(new Dimension(screenWidth, screenHeight));
    }
  }
  
  /**
   * Moves currently focused app to background for a duration
   *
   * @param webDriver the web driver
   * @param duration  duration to put in background
   */
  public static void moveAppToBackground(WebDriver webDriver, Duration duration) {
    if (webDriver instanceof AppiumDriver) {
      ((AppiumDriver) webDriver).runAppInBackground(duration);
    }
  }
  
  /**
   * Open Activity/App running in background
   *
   * @param webDriver the web driver
   * @param bundleId  id (package name) of the app
   */
  public static void openAppWithBundleId(WebDriver webDriver, String bundleId) {
    if (webDriver instanceof AppiumDriver) {
      ((AppiumDriver) webDriver).activateApp(bundleId);
    }
  }
  
  
  /**
   * Opens a new activity (changes to new activity)
   *
   * @param webDriver the web driver
   * @param activity  activity to open.
   */
  public static void starAppActivity(AndroidDriver webDriver, Activity activity) {
    webDriver.startActivity(activity);
  }
  
  /**
   * Performs a swipe up action from the beginning point to end point.
   *
   * @param webDriver the web driver
   * @param begin     the begin
   * @param end       the end
   *
   * @throws KiteTestException the kite test exception
   */
  public static void swipe(WebDriver webDriver, Point begin, Point end) throws KiteTestException {
    try {
      new TouchAction((MobileDriver) webDriver).press(PointOption.point(begin.x, begin.y))
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
        .moveTo(PointOption.point(end.x, end.y))
        .release().perform();
    } catch (Exception e) {
      throw new KiteTestException(e.getClass().getName() + " while swiping on device", Status.FAILED, e, false);
    }
    
  }
  
  /**
   * Performs a swipe down action from the middle of the screen, taking half screen height as travel
   * distance by default.
   *
   * @param webDriver the web driver
   *
   * @throws KiteTestException the kite test exception
   */
  public static void swipeDown(WebDriver webDriver) throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(size.width / 2, size.height / 2), new Point(size.width / 2, 10));
  }
  
  /**
   * Performs a swipe down action from the top right of the screen,
   * all the way down to the bottom right of the screen.
   *
   * @param webDriver the web driver
   *
   * @throws KiteTestException the kite test exception
   */
  public static void swipeDownFromTopRight(WebDriver webDriver) throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(size.getWidth() - 1, 1), new Point(size.getWidth() - 1, size.height));
  }
  
  /**
   * Performs a swipe left action starting from the edge of the screen to other edge.
   *
   * @param webDriver the web driver
   *
   * @throws KiteTestException the kite test exception
   */
  public static void swipeLeft(WebDriver webDriver) throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(15, size.height / 2), new Point(size.width - 15, size.height / 2));
  }
  
  /**
   * Swipe left with offset.
   *
   * @param webDriver the web driver
   * @param offset    the offset
   *
   * @throws KiteTestException the kite test exception
   */
  public static void swipeLeftWithOffset(WebDriver webDriver, int offset) throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(
      webDriver,
      new Point(15 + offset, size.height / 2),
      new Point(size.width - 15, size.height / 2));
  }
  
  /**
   * Performs a swipe right action starting from the edge of the screen to other edge.
   *
   * @param webDriver the web driver
   *
   * @throws KiteTestException the kite test exception
   */
  public static void swipeRight(WebDriver webDriver) throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(size.width - 15, size.height / 2), new Point(15, size.height / 2));
  }
  
  /**
   * Performs a swipe up action from the middle of the screen, taking half screen height as travel
   * distance by default.
   *
   * @param webDriver the web driver
   *
   * @throws KiteTestException the kite test exception
   */
  public static void swipeUp(WebDriver webDriver) throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(size.width / 2, size.height / 2), new Point(size.width / 2, size.height - 10));
  }
  
  /**
   * Performs a swipe up action from the bottom middle of the screen,
   * all the way down to the top middle of the screen.
   *
   * @param webDriver the web driver
   *
   * @throws KiteTestException the kite test exception
   */
  public static void swipeUpFromBottom(WebDriver webDriver) throws KiteTestException {
    Dimension size = getWindowSize(webDriver);
    swipe(webDriver, new Point(size.getWidth() / 2, size.getHeight() - 1), new Point(size.getWidth() / 2, 1));
  }
  
  /**
   * Performs a simple tap action in the middle of the screen.
   *
   * @param webDriver the web driver
   *
   * @throws KiteTestException the kite test exception
   */
  public static void tap(WebDriver webDriver) throws KiteTestException {
    Dimension size = webDriver.manage().window().getSize();
    tap(webDriver, size.width / 2, size.height / 2);
  }
  
  /**
   * Performs a simple tap action at a specific point
   *
   * @param webDriver the web driver
   * @param x         the x
   * @param y         the y
   *
   * @throws KiteTestException the kite test exception
   */
  public static void tap(WebDriver webDriver, int x, int y) throws KiteTestException {
    try {
      if (isMobileApp(webDriver)) {
      /*new TouchAction((MobileDriver) webDriver)
        .press(PointOption.point(x, y))
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(50))).release()
        .perform();*/
        new TouchAction((MobileDriver) webDriver)
          .tap(PointOption.point(x, y))
          .perform();
      } else {
        new Actions(webDriver).moveByOffset(x, y).click().perform();
      }
    } catch (Exception e) {
      throw new KiteTestException(e.getClass().getName() + " while tapping on device", Status.FAILED, e.getCause(), false);
    }
  }
  
  
}
