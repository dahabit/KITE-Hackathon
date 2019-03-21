/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * RTCDataChannelStats, with attributes label, protocol, datachannelId, state, messagesSent, bytesSent,
 * messagesReceived, bytesReceived
 */
public class RTCDataChannelStats extends RTCStatObject {
  
  private String label, protocol, datachannelId, state, messagesSent, bytesSent, messagesReceived, bytesReceived, timestamp;
  
  
  /**
   * Instantiates a new Rtc data channel stats.
   *
   * @param statObject the stat object
   */
  public RTCDataChannelStats(Map<Object, Object> statObject) {
    this.setId(getStatByName(statObject, "id"));
    this.label = getStatByName(statObject, "label");
    this.protocol = getStatByName(statObject, "protocol");
    this.datachannelId = getStatByName(statObject, "datachannelId");
    this.state = getStatByName(statObject, "state");
    this.messagesSent = getStatByName(statObject, "messagesSent");
    this.bytesSent = getStatByName(statObject, "bytesSent");
    this.messagesReceived = getStatByName(statObject, "messagesReceived");
    this.bytesReceived = getStatByName(statObject, "bytesReceived");
    this.timestamp = getStatByName(statObject, "timestamp");
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("label", this.label)
        .add("protocol", this.protocol)
        .add("datachannelId", this.datachannelId)
        .add("state", this.state)
        .add("messagesSent", this.messagesSent)
        .add("bytesSent", this.bytesSent)
        .add("messagesReceived", this.messagesReceived)
        .add("bytesReceived", this.bytesReceived)
        .add("timestamp", this.timestamp);
    return jsonObjectBuilder;
  }
}
