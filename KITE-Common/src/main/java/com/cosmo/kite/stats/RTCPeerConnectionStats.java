/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * RTCPeerConnectionStats, with attributes dataChannelsOpened, dataChannelsClosed
 */
public class RTCPeerConnectionStats extends RTCStatObject {
  private String dataChannelsOpened, dataChannelsClosed;
  
  /**
   * Instantiates a new Rtc peer connection stats.
   *
   * @param statObject the stat object
   */
  public RTCPeerConnectionStats(Map<Object, Object> statObject) {
    this.setId(getStatByName(statObject, "id"));
    this.dataChannelsOpened = getStatByName(statObject, "dataChannelsOpened");
    this.dataChannelsClosed = getStatByName(statObject, "dataChannelsClosed");
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("dataChannelsOpened", this.dataChannelsOpened)
        .add("dataChannelsClosed", this.dataChannelsClosed);
    return jsonObjectBuilder;
  }
}
