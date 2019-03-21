/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package org.webrtc.kite.config;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.webrtc.kite.exception.KiteInsufficientValueException;
import org.webrtc.kite.exception.KiteUnsupportedRemoteException;
import org.webrtc.kite.scheduler.MatrixRunnerJob;

import javax.json.JsonObject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Config type one handler.
 */
public class ConfigTypeOneHandlerWithGrid extends ConfigHandler {

  private static final Logger logger =
      Logger.getLogger(ConfigTypeOneHandlerWithGrid.class.getName());

  /**
   * Instantiates a new Config type one handler.
   *
   * @param permute           permutation is true, combination if false
   * @param callbackURL       the callback url
   * @param remoteObjectList  the remote object list
   * @param testObjectList    the test object list
   * @param browserObjectList the browser object list
   * @param appObjectList the app object list
   * @throws KiteInsufficientValueException the kite insufficient value exception
   * @throws KiteUnsupportedRemoteException the kite unsupported remote exception
   * @throws InvocationTargetException      the invocation target exception
   * @throws NoSuchMethodException          the no such method exception
   * @throws InstantiationException         the instantiation exception
   * @throws IllegalAccessException         the illegal access exception
   */
  public ConfigTypeOneHandlerWithGrid(boolean permute, String callbackURL, List<JsonObject> remoteObjectList,
      List<JsonObject> testObjectList, List<JsonObject> browserObjectList, List<JsonObject> appObjectList)
      throws KiteInsufficientValueException, KiteUnsupportedRemoteException,
      InvocationTargetException, NoSuchMethodException, InstantiationException,
      IllegalAccessException {

    this.testList = new ArrayList<>();
    for (JsonObject object : testObjectList) {
      this.testList.add(new TestConf(permute,callbackURL, object));
    }
    this.adjustRemotes(new RemoteManager(remoteObjectList), browserObjectList, Browser.class);
    if (appObjectList != null) {
      this.adjustRemotes(new RemoteManager(remoteObjectList), appObjectList, App.class);
    }
    /*
    this.gridConfig = gridConfig;
    if (this.gridConfig == null) {

    } else {
      this.endPointList = new ArrayList<>();
      for (JsonObject object : browserObjectList) {
        this.endPointList.add(new Browser(null, object));
      }
      // todo: determineMaxInstances with appObjectList
    }
    */
  }

  @Override public Class<? extends Job> getJobClass() {
    return MatrixRunnerJob.class;
  }


}
