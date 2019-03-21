/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.webrtc.kite.wdmgmt;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.webrtc.kite.config.Browser;
import org.webrtc.kite.config.EndPoint;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Utility class serving WebDriver related operations.
 */
public class WebDriverUtility {
  
  private static final Logger logger = Logger.getLogger(WebDriverUtility.class.getName());
  
  /**
   * Gets web driver for endPoint.
   *
   * @param testName the test name
   * @param endPoint the endPoint
   * @param id       an ID to identify the WebDriver
   *
   * @return the web driver for endPoint
   * @throws MalformedURLException the malformed url exception
   * @throws WebDriverException    the web driver exception
   */
  public static WebDriver getWebDriverForConfigObject(String testName, EndPoint endPoint, String id)
    throws MalformedURLException, WebDriverException {
    WebDriver webDriver = WebDriverFactory.createWebDriver(endPoint, testName, id);
    if (endPoint instanceof Browser) {
      Capabilities capabilities = ((RemoteWebDriver) webDriver).getCapabilities();
      ((Browser) endPoint).setWebDriverVersion(capabilities.getVersion());
      ((Browser) endPoint).setWebDriverPlatform(capabilities.getPlatform().name());
    }
    return webDriver;
  }
  
  /**
   * Gets web driver for endPoint.
   *
   * @param testName the test name
   * @param endPoint the endPoint
   *
   * @return the web driver for endPoint
   * @throws MalformedURLException the malformed url exception
   * @throws WebDriverException    the web driver exception
   */
  public static WebDriver getWebDriverForConfigObject(String testName, EndPoint endPoint)
    throws MalformedURLException, WebDriverException {
    return WebDriverUtility.getWebDriverForConfigObject(testName, endPoint, "");
  }
  
  /**
   * Populate info from navigator.
   *
   * @param webDriver the web driver
   * @param endPoint  the endPoint
   */
  public static void populateInfoFromNavigator(WebDriver webDriver, EndPoint endPoint) {
    if (endPoint instanceof Browser) {
      if (!((Browser) endPoint).shouldGetUserAgent() || !WebDriverUtility.isAlive(webDriver)) {
        return;
      }
      Object resultObject = null;
      webDriver.get("http://www.google.com");
      try {
        Alert alert = webDriver.switchTo().alert();
        alert.accept();
        resultObject = ((JavascriptExecutor) webDriver).executeScript(userAgentScript());
        logger.info("Browser platform and userAgent for: " + endPoint.toString() + "->" + resultObject);
      } catch (NoAlertPresentException e) {
        logger.info("No alert found.");
      } catch (Exception e) {
        logger.warn("Could not handle the Alert!");
        e.printStackTrace();
      }
      
      if (resultObject != null) {
        if (resultObject instanceof String) {
          String resultOfScript = (String) resultObject;
          ((Browser) endPoint).setUserAgentVersionAndPlatform(resultOfScript);
        }
      }
    }
  }
  
  /**
   * Close drivers.
   *
   * @param webDriverList the web driver list
   */
  public static void closeDrivers(List<WebDriver> webDriverList) {
    for (WebDriver webDriver : webDriverList)
      try {
        // Open about:config in case of fennec (Firefox for Android) and close.
        logger.info("closeDrivers: closing down " + webDriverList.size() + " webDrivers");
        if (((RemoteWebDriver) webDriver).getCapabilities().getBrowserName()
          .equalsIgnoreCase("fennec")) {
          webDriver.get("about:config");
          webDriver.close();
        }
        webDriver.quit();
      } catch (Exception e) {
        logger.error("Exception while closing/quitting the WebDriver", e);
      }
  }
  
  private static Boolean isAlive(WebDriver webDriver) {
    try {
      webDriver.getCurrentUrl();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }
  
  private static String userAgentScript() {
    return "var nav = '';" + "try { var myNavigator = {};"
      + "for (var i in navigator) myNavigator[i] = navigator[i];"
      + "nav = JSON.stringify(myNavigator); } catch (exception) { nav = exception.message; }"
      + "return nav;";
  }
  
}
