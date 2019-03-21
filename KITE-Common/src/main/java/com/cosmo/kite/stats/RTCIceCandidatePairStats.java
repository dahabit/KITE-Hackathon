/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * RTCIceCandidatePairStats, with attributes transportId, localCandidateId, remoteCandidateId, state, priority, nominated, bytesSent,
 * bytesReceived, totalRoundTripTime, currentRoundTripTime
 */
public class RTCIceCandidatePairStats extends RTCStatObject {
  
  private String transportId, localCandidateId, remoteCandidateId, state, priority, nominated,
    bytesSent, bytesReceived, totalRoundTripTime, currentRoundTripTime, timestamp;
  
  /**
   * Instantiates a new Rtc ice candidate pair stats.
   *
   * @param statObject the stat object
   */
  public RTCIceCandidatePairStats(Map<Object, Object> statObject) {
    this.setId(getStatByName(statObject, "id"));
    this.transportId = getStatByName(statObject, "transportId");
    this.localCandidateId = getStatByName(statObject, "localCandidateId");
    this.remoteCandidateId = getStatByName(statObject, "remoteCandidateId");
    this.state = getStatByName(statObject, "state");
    this.priority = getStatByName(statObject, "priority");
    this.nominated = getStatByName(statObject, "nominated");
    this.bytesSent = getStatByName(statObject, "bytesSent");
    this.bytesReceived = getStatByName(statObject, "bytesReceived");
    this.totalRoundTripTime = getStatByName(statObject, "totalRoundTripTime");
    this.currentRoundTripTime = getStatByName(statObject, "currentRoundTripTime");
    this.timestamp = getStatByName(statObject, "timestamp");
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("transportId", this.transportId)
        .add("localCandidateId", this.localCandidateId)
        .add("remoteCandidateId", this.remoteCandidateId)
        .add("state", this.state)
        .add("priority", this.priority)
        .add("nominated", this.nominated)
        .add("bytesSent", this.bytesSent)
        .add("currentRoundTripTime", this.currentRoundTripTime)
        .add("totalRoundTripTime", this.totalRoundTripTime)
        .add("bytesReceived", this.bytesReceived)
        .add("timestamp", this.timestamp);
    return jsonObjectBuilder;
  }
}
