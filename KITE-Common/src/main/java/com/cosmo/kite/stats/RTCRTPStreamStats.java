/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * Represent RTCRTPStreamStats, outbound and inbound, sent and received.
 */
public class RTCRTPStreamStats extends RTCStatObject {
  private boolean inbound;
  private String ssrc, mediaType, trackId, transportId, codecId, nackCount, timestamp;
  private Map<Object, Object> statObject;
  
  /**
   * Instantiates a new Rtcrtp stream stats.
   *
   * @param statObject the stat object
   * @param inbound    the inbound
   */
  public RTCRTPStreamStats(Map<Object, Object> statObject, boolean inbound) {
    this.setId(getStatByName(statObject, "id"));
    this.ssrc = getStatByName(statObject, "ssrc");
    this.mediaType = getStatByName(statObject, "mediaType");
    this.trackId = getStatByName(statObject, "trackId");
    this.transportId = getStatByName(statObject, "parameters");
    this.nackCount = getStatByName(statObject, "nackCount");
    this.codecId = getStatByName(statObject, "codecId");
    this.timestamp = getStatByName(statObject, "timestamp");
    this.inbound = inbound;
    this.statObject = statObject;
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("ssrc", this.ssrc)
        .add("mediaType", this.mediaType)
        .add("trackId", this.trackId)
        .add("transportId", this.transportId)
        .add("nackCount", this.nackCount)
        .add("codecId", this.codecId)
        .add("timestamp", this.timestamp);
    if (this.inbound)
      jsonObjectBuilder.add("packetsReceived", getStatByName(this.statObject, "packetsReceived"))
        .add("bytesReceived", getStatByName(this.statObject, "bytesReceived"))
        .add("packetsLost", getStatByName(this.statObject, "packetsLost"))
        .add("packetsDiscarded", getStatByName(this.statObject, "packetsDiscarded"))
        .add("jitter", getStatByName(this.statObject, "jitter"))
        .add("remoteId", getStatByName(this.statObject, "remoteId"))
        .add("framesDecoded", getStatByName(this.statObject, "framesDecoded"));
    else
      jsonObjectBuilder.add("packetsSent", getStatByName(this.statObject, "packetsSent"))
        .add("bytesSent", getStatByName(this.statObject, "bytesSent"))
        .add("remoteId", getStatByName(this.statObject, "remoteId"))
        .add("framesDecoded", getStatByName(this.statObject, "framesDecoded"));
    
    return jsonObjectBuilder;
  }
}
