/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.util;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.report.custom_kite_allure.AllureStepReport;
import com.cosmo.kite.steps.TestStep;
import com.cosmo.kite.report.custom_kite_allure.Status;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.webrtc.kite.Utility;
import org.webrtc.kite.config.App;
import org.webrtc.kite.config.Browser;
import org.webrtc.kite.config.EndPoint;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

import static com.cosmo.kite.action.JSActionScript.getVideoFrameValueSumByIndexScript;
import static com.cosmo.kite.action.JSActionScript.recordVideoStreamScript;
import static com.cosmo.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static com.cosmo.kite.util.ReportUtils.getStackTrace;
import static com.cosmo.kite.util.WebDriverUtils.executeJsScript;
import static com.cosmo.kite.util.WebDriverUtils.getConsoleLog;

/**
 * The type Test utils.
 */
public class TestUtils {
  
  static private final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";

  private static final Logger logger = Logger.getLogger(TestUtils.class.getName());
  static private Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);
  
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
   * Checks console logs for javascript error.
   *
   * @param webDriver the webDriver
   * @param str       the String to find in the logs in case of error.
   *
   * @throws KiteTestException is the check fails
   */
  public static void consoleLogsCheck(WebDriver webDriver, String str) throws KiteTestException {
    String consoleLogs = getConsoleLog(webDriver).build().toString();
    logger.info("Console logs \r\n" + consoleLogs);
    if (consoleLogs.contains(str)) {
      throw new KiteTestException("Electron failed to load properly, Unable to load preload script: [...]\\preload.js", Status.BROKEN);
    }
  }
  
  /**
   * Copy the String s to clipboard
   *
   * @param s the String to be copied to the clipboard
   */
  public static void copyToClipboard(String s) {
    StringSelection selection = new StringSelection(s);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(selection, selection);
  }
  
  /**
   * Creates a Json Array (as a String) from a List of JsonObject provided. E.g.:
   * createJsonArray("result", myList) returns { "result" : [ jsonObject1, jsonObject2....] }
   *
   * @param key      a JSON key for the json array
   * @param jsonList the list of JsonObject to combine into an array
   *
   * @return the json object as a string
   */
  public static String createJsonArray(String key, List<JsonObject> jsonList) {
    String s = "{ \"" + key + "\" : ";
    s += jsonList.size() > 0 ? "\r\n  [\r\n" : "\"jsonList is empty\"}\r\n";
    for (JsonObject list : jsonList) {
      s += "    " + list.toString();
      s += jsonList.indexOf(list) == (jsonList.size() - 1) ? "\r\n" : ",\r\n";
    }
    s += jsonList.size() > 0 ? "  ]\r\n}" : "";
    return s;
  }
  
  /**
   * Execute command string.
   *
   * @param workingDir the working dir
   * @param command    the command
   * @param logger     the logger
   * @param logHeader  the log header
   *
   * @return the string
   * @throws Exception the exception
   */
  public static String executeCommand(String workingDir, List<String> command, Logger logger, String logHeader) throws Exception {
    ProcessBuilder builder =
      new ProcessBuilder(command);
    builder.directory(
      new File(workingDir)
        .getAbsoluteFile()); // this is where you set the root folder for the executable to run
    // with
    builder.redirectErrorStream(true);
    Process process = builder.start();
    
    Scanner s = new Scanner(process.getInputStream());
    StringBuilder text = new StringBuilder();
    while (s.hasNextLine()) {
      String line = s.nextLine();
      text.append(line);
      text.append("\n");
      logger.info("[nodejs console " + logHeader + " ] " + line);
    }
    s.close();
    
    int result = process.waitFor();
    System.out.printf("Process exited with result %d and output %s%n", result, text);
    return text.toString();
  }
  
  /**
   * Gets the json object.
   *
   * @param filepath the path to file
   *
   * @return the json object
   * @throws FileNotFoundException the file not found exception
   */
  public static JsonObject getJsonFromFile(String filepath) throws FileNotFoundException {
    JsonObject jsonObject;
    File file = new File(filepath);
    FileReader fileReader = null;
    JsonReader jsonReader = null;
    try {
      fileReader = new FileReader(file);
      jsonReader = Json.createReader(fileReader);
      jsonObject = jsonReader.readObject();
    } finally {
      if (fileReader != null) {
        try {
          fileReader.close();
        } catch (IOException e) {
          logger.warn(e.getMessage(), e);
        }
      }
      if (jsonReader != null) {
        jsonReader.close();
      }
    }
    
    return jsonObject;
  }
  
  /**
   * Id to string string.
   *
   * @param id an int between 0 and 999
   *
   * @return a String with leading zero padding (e.g. 001, 029...)
   */
  public static String idToString(int id) {
    return "" + (id < 10 ? "00" + id : (id < 100 ? "0" + id : "" + id));
  }
  
  /**
   * Checks whether the given string is not null and is not an empty string.
   *
   * @param value string
   *
   * @return true if the provided value is not null and is not empty.
   */
  public static boolean isNotNullAndNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }
  
  /**
   * Is valid ipv 4 boolean.
   *
   * @param s a string to be validated as a valid IPv4 address
   *
   * @return true if it's a valid IPv4 address.
   */
  public static boolean isValidIPV4(final String s) {
    return IPV4_PATTERN.matcher(s).matches();
  }
  
  /**
   * Calls the playVideoScript function to play the video.
   *
   * @param webDriver the web driver
   * @param video_id  id the video in the list of video elements.
   *
   * @throws InterruptedException the interrupted exception
   */
  public static void playVideo(WebDriver webDriver, String video_id) throws InterruptedException {
    ((JavascriptExecutor) webDriver)
      .executeScript("document.getElementById('" + video_id + "').play();");
  }
  
  /**
   * Create a directory if not existing
   * @param dirName directory name
   */
  public static void createDirs(String dirName) {
    dirName = verifyPathFormat(dirName);
    File dir = new File(dirName);
    if (!dir.exists()) {
      dir.mkdirs();
    }
  }
  
  /**
   * Saves a JSON object into a file, with line breaks and indents.
   *
   * @param jsonStr  the json object as a String.
   * @param filename the file to be created with the json file.
   */
  public static void printJsonTofile(String jsonStr, String filename) {
    try {
      Map<String, Object> properties = new LinkedHashMap<>(1);
      properties.put(JsonGenerator.PRETTY_PRINTING, true);
      FileOutputStream fo = new FileOutputStream(filename);
      PrintWriter pw = new PrintWriter(fo, true);
      JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
      JsonWriter jsonWriter = writerFactory.createWriter(pw);
      JsonObject obj = Json.createReader(new StringReader(jsonStr)).readObject();
      jsonWriter.writeObject(obj);
      jsonWriter.close();
      pw.close();
      fo.close();
    } catch (Exception e) {
      logger.error("\r\n" + getStackTrace(e));
    }
  }
  
  /**
   * Create a directory if not existing
   * @param dirName directory name
   */
  public static void createDirs(String dirName) {
    if (!dirName.endsWith("/")) {
      dirName += "/";
    }
    File dir = new File(dirName + "/");
    if (!dir.exists()) {
      dir.mkdirs();
    }
  }
  
  /**
   * Saves a JSON object into a file, with line breaks and indents.
   *
   * @param testName the name of the test, which will be inlcuded in the file name
   * @param jsonStr  the json object as a String.
   * @param dirPath  the directory path where to save the file.
   */
  public static void printJsonTofile(String testName, String jsonStr, String dirPath) {
    try {
      String jsonFilename =
        testName.replace(" ", "")
          + "_"
          + new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date())
          + ".json";
      createDirs(dirPath);
      if (!dirPath.endsWith("/")) {
        dirPath += "/";
      }
      printJsonTofile(jsonStr, dirPath + jsonFilename);
    } catch (Exception e) {
      logger.error("\r\n" + getStackTrace(e));
    }
  }
  
  /**
   * Reads the file content into a String
   *
   * @param path to the file
   *
   * @return the content of the file as a String
   * @throws IOException the io exception
   */
  public static String readFile(String path) throws IOException {
    String result = "";
    FileInputStream fin = new FileInputStream(path);
    BufferedReader buf = new BufferedReader(new InputStreamReader(fin));
    String line = buf.readLine();
    while (line != null) {
      result += line + "\r\n";
      line = buf.readLine();
    }
    buf.close();
    fin.close();
    return result;
  }
  
  /**
   * Record video from a video element and upload it to a server
   *
   * @param webDriver                     browser running the test
   * @param videoIndex                    video index in page's video array
   * @param recordingDurationInMilisecond duration to record
   * @param details                       Json object containing details about the video file (name, type, ..)
   * @param callbackUrl                   server url to send video back to
   *
   * @return the boolean
   */
  public static boolean recordVideoStream(
    WebDriver webDriver,
    int videoIndex,
    int recordingDurationInMilisecond,
    JsonObject details,
    String callbackUrl) {
    // todo: test this
    try {
      executeJsScript(webDriver,
        recordVideoStreamScript(videoIndex, recordingDurationInMilisecond, details, callbackUrl));
      WebDriverWait wait =
        new WebDriverWait(webDriver, (recordingDurationInMilisecond + 10000) / 1000);
      wait.until(ExpectedConditions.presenceOfElementLocated(By.id("videoRecorded" + videoIndex)));
      return true;
    } catch (Exception e) {
      logger.error("video recording (index: " + videoIndex + ") failed for " + details.toString());
      return false;
    }
  }
  
  /**
   * Saves a screenshot of the webdriver/browser under "report/" + filename + ".png"
   *
   * @param webDriver the webdriver
   * @param path      the path
   * @param filename  the name of the file without path ("report/") and extension (" .png")
   *
   * @return true if successful, false otherwise
   */
  public static byte[] takeScreenshot(WebDriver webDriver, String path, String filename) {
    if (path != null) {
      try {
        File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        File dir = new File(path);
        if (!dir.isDirectory()) {
          dir.mkdirs();
        }
        if (!path.endsWith("/")) {
          path += "/";
        }
        String s = path + "screenshots/" + filename + ".png";
        File f = new File(s);
        FileUtils.copyFile(scrFile, f);
        return FileUtils.readFileToByteArray(f);
      } catch (Exception e) {
        logger.error("Exception in takeScreenshot(driver, " + path + ", " + filename
          + ") \r\n" + e.getLocalizedMessage());
        return null;
      }
    }
    return null;
  }
  
  /**
   * Check the video playback by verifying the pixel sum of 2 frame between a time interval of
   * 500ms. if (getSum(frame2) - getSum(frame1) != 0 ) => return "video", if getSum(frame2) ==
   * getSum(frame1) > 0 => return "still" if getSum(frame2) == getSum(frame1) == 0 => return "blank"
   *
   * @param webDriver webdriver that control the browser
   * @param index     index of the video element on the page in question
   *
   * @return the check sum
   * @throws InterruptedException the interrupted exception
   */
  public static JsonObject videoCheckSum(WebDriver webDriver, int index)
    throws InterruptedException {
    String result = "blank";
    JsonObjectBuilder resultObject = Json.createObjectBuilder();
    long canvasData1 =
      (long) executeJsScript(webDriver, getVideoFrameValueSumByIndexScript(index));
    Thread.sleep(500);
    long canvasData2 =
      (long) executeJsScript(webDriver, getVideoFrameValueSumByIndexScript(index));
    if (canvasData1 != 0 || canvasData2 != 0) {
      long diff = Math.abs(canvasData2 - canvasData1);
      if (diff != 0) {
        result = "video";
      } else {
        result = "still";
      }
    }
    
    resultObject.add("checksum1", canvasData1).add("checksum2", canvasData2).add("result", result);
    return resultObject.build();
  }



  /**
   * Check the video playback by verifying the pixel sum of 2 frame between a time interval of
   * 500ms. if (getSum(frame2) - getSum(frame1) != 0 ) => return "video", if getSum(frame2) ==
   * getSum(frame1) > 0 => return "still" if getSum(frame2) == getSum(frame1) == 0 => return "blank"
   *
   * @param webDriver webdriver that control the browser
   * @param index     index of the video element on the page in question
   *
   * @return "blank" or "still" or "video"
   * @throws InterruptedException the interrupted exception
   */
  public static String videoCheck(WebDriver webDriver, int index) {
    String result = "blank";
    long canvasData1 =
      (long) executeJsScript(webDriver, getVideoFrameValueSumByIndexScript(index));
    waitAround(ONE_SECOND_INTERVAL/2);
    long canvasData2 =
      (long) executeJsScript(webDriver, getVideoFrameValueSumByIndexScript(index));
    if (canvasData1 != 0 || canvasData2 != 0) {
      long diff = Math.abs(canvasData2 - canvasData1);
      if (diff != 0) {
        result = "video";
      } else {
        waitAround(ONE_SECOND_INTERVAL);
        long canvasData3 =
          (long) executeJsScript(webDriver, getVideoFrameValueSumByIndexScript(index));
        result = Math.abs(canvasData3 - canvasData1) != 0 ? "video" : "still";
      }
    }
    return result;
  }
  
  /**
   * Waits for a duration
   *
   * @param durationInMillisecond duration in milliseconds
   */
  public static void waitAround(int durationInMillisecond) {
    try {
      logger.debug("sleeping " + durationInMillisecond + "ms.");
      Thread.sleep(durationInMillisecond);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  /**
   * Process the step in the new KiteCallable and Kite
   * @param step
   * @param testReport
   */
  public static void processTestStep(TestStep step, AllureStepReport testReport) {
    step.init();
    if (!testReport.failed()) {
      step.execute();
    } else {
      step.skip();
    }
    step.finish();
    testReport.addStepReport(step.getStepReport());
  }

  /**
   * Gets the list of Browser from the config file.
   *
   * @param config the kite test config file
   * @param type the type of endpoint: "browser" or "apps"
   * @return the list of Browser from the config file.
   */
  public static List<EndPoint> getEndPointList(String config, String type) {
    List<EndPoint> endPoints = new ArrayList<>();
    try {
      JsonObject jsonObject = readJsonFile(config);
      List<JsonObject> endpointObjectList =
          (List<JsonObject>)
              Utility.throwNoKeyOrBadValueException(jsonObject, type, JsonArray.class, false);
      List<JsonObject> remotes =
        (List<JsonObject>)
          Utility.throwNoKeyOrBadValueException(jsonObject, "remotes", JsonArray.class, false);
      String remoteAddress = remotes.get(0).getString("remoteAddress");
      List<JsonObject> testObjectList =
        (List<JsonObject>)
          Utility.throwNoKeyOrBadValueException(jsonObject, "tests", JsonArray.class, false);
      int tupleSize = testObjectList.get(0).getInt("tupleSize");
      for (JsonObject object : endpointObjectList) {
        for (int i = 0; i < tupleSize; i++) {
          endPoints.add(
              "browsers".equalsIgnoreCase(type)
                  ? new Browser(remoteAddress, object)
                  : new App(remoteAddress, object));
        }
      }
    } catch (Exception e) {
      logger.error(getStackTrace(e));
    }
    return endPoints;
  }



  /**
   * Gets the payload object for the test at index 'testIndex' in the config
   *
   * @param config the kite test config file
   * @param testIndex the index of the test to get the payload from
   * @return the payload object for the test at index 'testIndex' in the config
   */
  public static JsonObject getPayload(String config, int testIndex) {
    try {
      JsonObject jsonObject = readJsonFile(config);
      List<JsonObject> testObjectList =
        (List<JsonObject>)
          Utility.throwNoKeyOrBadValueException(jsonObject, "tests", JsonArray.class, false);
      return testObjectList.get(testIndex).getJsonObject("payload");
    } catch (Exception e) {
      logger.error(getStackTrace(e));
    }
    return null;
  }

  /**
   * Reads a json file into a JsonObject
   *
   * @param jsonFile the file to read
   * @return the jsonObject
   */
  public static JsonObject readJsonFile(String jsonFile) {
    FileReader fileReader = null;
    JsonReader jsonReader = null;
    try {
      logger.info("Reading '" + jsonFile + "' ...");
      fileReader = new FileReader(new File(jsonFile));
      jsonReader = Json.createReader(fileReader);
      return jsonReader.readObject();
    } catch (Exception e) {
      logger.error(getStackTrace(e));
    } finally {
      if (fileReader != null) {
        try {
          fileReader.close();
        } catch (IOException e) {
          logger.warn(e.getMessage(), e);
        }
      }
      if (jsonReader != null) {
        jsonReader.close();
      }
    }
    return null;
  }

  public static String verifyPathFormat(String url) {
    if (!url.endsWith("/")) {
      return url + "/";
    }
    return url;
  }
}
