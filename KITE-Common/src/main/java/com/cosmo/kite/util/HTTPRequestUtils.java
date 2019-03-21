/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.util;

import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.cosmo.kite.util.TestUtils.readFile;


/**
 * The type Http request utils.
 */
public class HTTPRequestUtils {
  
  private static final Logger logger = Logger.getLogger(HTTPRequestUtils.class.getName());
  
  /***
   * Creates and install a trust manager that accepts all CA (to be changed)
   */
  public static void installTrustManager() {
    // Creates the TrustManager
    TrustManager[] trustAllCerts = new TrustManager[]{
      new X509TrustManager() {
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
        
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
        
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      }
    };
    
    // Install the TrustManager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * This function sends an HTTP request
   *
   * @param method   Either GET, POST, DELETE etc.
   * @param url      the url
   * @param filePath Path of the file from which the request body is made
   * @param token    OAuth 2.0 Token
   *
   * @return Content of the answer as a String
   */
  public static String sendRequest(String method, String url, String filePath,
                                   String token) {
    
    String result = "";
    HttpURLConnection con = null;
    DataOutputStream wr = null;
    BufferedReader in = null;
    try {
      logger.info("sendRequest(" + method + ", " + url + ", " + filePath + ", " + token + ")");
      URL myurl = new URL(url);
      con = (HttpURLConnection) myurl.openConnection();
      
      con.setDoOutput(true);
      con.setRequestMethod(method);
      con.setRequestProperty("User-Agent", "Java client");
      con.setRequestProperty("Content-Type", "application/json");
      
      if (token != null) {
        con.setRequestProperty("Authorization", "Bearer " + token);
      } else {
        con.setRequestProperty("Authorization", "Bearer dummyToken");
      }
      
      if (filePath != null) {
        String urlParameters = readFile(filePath);
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        wr = new DataOutputStream(con.getOutputStream());
        wr.write(postData);
      }
      
      StringBuilder content;
      
      if (con.getResponseCode() != 200) {
        result = "" + con.getResponseCode();
      } else {
        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        
        String line;
        content = new StringBuilder();
        
        while ((line = in.readLine()) != null) {
          content.append(line);
          content.append(System.lineSeparator());
        }
        result = content.toString();
      }
      
    } catch (Exception e) {
      logger.error(ReportUtils.getStackTrace(e));
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
        
        }
      }
      if (wr != null) {
        try {
          wr.close();
        } catch (IOException e) {
        
        }
      }
      if (con != null) {
        con.disconnect();
      }
    }
    logger.debug("sendRequest() result = " + result);
    return result;
  }
  
  /**
   * This function sends an HTTP request
   *
   * @param method   Either GET, POST, DELETE etc.
   * @param protocol the protocol
   * @param server   the server
   * @param path     the path
   * @param filePath Path of the file from which the request body is made
   * @param token    OAuth 2.0 Token
   *
   * @return Content of the answer as a String
   */
  public static String sendRequest(
    String method, String protocol, String server, String path, String filePath, String token) {
    return sendRequest(method, protocol + "://" + server + path, filePath, token);
  }
  
}
