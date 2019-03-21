/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Utility class for small grid related functions.
 */
public class GridUtils {
  
  private final static Logger logger = Logger.getLogger(GridUtils.class.getName());
  
  /**
   * Gets private ip by querying the hub against a session id.
   *
   * @param hupIpOrDns the hup ip or dns
   * @param hubPort    the hub port
   * @param sessionId  the session id
   *
   * @return the private ip
   */
  public static URL getAssignedUrl(String hupIpOrDns, int hubPort, String sessionId) {
    
    CloseableHttpClient client = null;
    CloseableHttpResponse response = null;
    InputStream stream = null;
    JsonReader reader = null;
    URL url = null;
    try {
      client = HttpClients.createDefault();
      HttpGet httpGet = new HttpGet(
        "http://" + hupIpOrDns + ":" + hubPort + "/grid/api/testsession?session=" + sessionId);
      response = client.execute(httpGet);
      stream = response.getEntity().getContent();
      reader = Json.createReader(stream);
      JsonObject object = reader.readObject();
      String proxyId = object.getString("proxyId");
      url = new URL(proxyId);
    } catch (Exception e) {
      logger.error("Exception while talking to the grid", e);
    } finally {
      if (reader != null)
        reader.close();
      if (stream != null)
        try {
          stream.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the InputStream", e);
        }
      if (response != null) {
        logger.debug("response->" + response);
        try {
          response.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpResponse", e);
        }
      }
      if (client != null)
        try {
          client.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpClient", e);
        }
    }
    return url;
  }
  
  /**
   * Gets private ip by querying the hub against a session id.
   *
   * @param hupIpOrDns the hup ip or dns
   * @param sessionId  the session id
   *
   * @return the private ip
   */
  public static String getPrivateIp(String hupIpOrDns, String sessionId) {
    
    String privateIp = null;
    
    CloseableHttpClient client = null;
    CloseableHttpResponse response = null;
    InputStream stream = null;
    JsonReader reader = null;
    
    try {
      client = HttpClients.createDefault();
      HttpGet httpGet =
        new HttpGet("http://" + hupIpOrDns + ":4444/grid/api/testsession?session=" + sessionId);
      response = client.execute(httpGet);
      stream = response.getEntity().getContent();
      reader = Json.createReader(stream);
      JsonObject object = reader.readObject();
      String proxyId = object.getString("proxyId");
      URL url = new URL(proxyId);
      privateIp = url.getHost();
    } catch (Exception e) {
      logger.error("Exception while talking to the grid", e);
    } finally {
      if (reader != null)
        reader.close();
      if (stream != null)
        try {
          stream.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the InputStream", e);
        }
      if (response != null) {
        if (logger.isDebugEnabled())
          logger.debug("response->" + response);
        try {
          response.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpResponse", e);
        }
      }
      if (client != null)
        try {
          client.close();
        } catch (IOException e) {
          logger.warn("Exception while closing the CloseableHttpClient", e);
        }
    }
    
    return privateIp;
    
  }
  
  /**
   * Short name for the VM.
   *
   * @param vmInstanceLongName String the long name of the aws instance,                           e.g. ec2-54-174-40-108.compute-1.amazonaws.com                           or http://ec2-54-165-55-187.compute-1.amazonaws.com:4444/
   *
   * @return String the short name for the VM, to be used in logs and reports: ec2-54-174-40-108
   */
  public static String getVMName(String vmInstanceLongName) {
    String s = "error_parsing_name";
    try {
      s = vmInstanceLongName.substring(vmInstanceLongName.contains("http://") ? 7 : 0,
        vmInstanceLongName.indexOf("."));
    } catch (Exception e) {
      logger.error("Error parsing " + vmInstanceLongName + "\r\n" + ReportUtils.getStackTrace(e));
    }
    return s;
  }
  
  /**
   * Id to string string.
   *
   * @param id an int between 0 and 999
   *
   * @return a String with leading zero padding (e.g. 001, 029...)
   */
  public static String idToString(int id) {
    return "#" + (id < 10 ? "00" + id : (id < 100 ? "0" + id : "" + id));
  }
  
  /**
   * Open Chrome to the url given as parameter
   * todo, this is on windows only, to do the same for linux, mac...
   *
   * @param hubIpList the hub ip list
   *
   * @throws IOException
   */
  public static void openChromeToURL(List<String> hubIpList) {
    if (hubIpList == null) {
      logger.warn("openChromeToURL(" + hubIpList + ")");
      return;
    }
    if (!System.getProperty("os.name").toLowerCase().contains("win")) {
      logger.warn("openChromeToURL() is only supported on Windows");
      return;
    }
    String gridConsoleUrl = "http://%s:4444/grid/console";
    for (String hubIp : hubIpList) {
      openChromeToURL(String.format(gridConsoleUrl, hubIp));
    }
  }
  
  /**
   * Open Chrome to the url given as parameter todo, this is on windows only, to do the same for
   * linux, mac...
   *
   * @param url the url of the page to open
   *
   * @throws IOException
   */
  public static void openChromeToURL(String url) {
    if (url == null) {
      logger.warn("openChromeToURL(" + url + ")");
      return;
    }
    try {
      Thread.sleep(2000);
      if (System.getProperty("os.name").toLowerCase().contains("win")) {
        Runtime.getRuntime().exec(new String[]{"cmd", "/c", "setStartTimestamp chrome " + url});
      } else {
        logger.warn("openChromeToURL() is only supported on Windows");
      }
    } catch (IOException | InterruptedException e) {
      logger.warn("Exception while opening up Google Chrome", e);
    }
  }
  
}

