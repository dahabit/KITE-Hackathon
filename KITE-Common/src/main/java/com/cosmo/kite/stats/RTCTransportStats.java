/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * RTCTransportStats, with attributes bytesSent, bytesReceived, rtcpTransportStatsId, selectedCan
 */
public class RTCTransportStats extends RTCStatObject {
  
  private String bytesSent, bytesReceived, rtcpTransportStatsId, selectedCandidatePairId,
    localCertificateId, remoteCertificateId, timestamp;
  
  /**
   * Instantiates a new Rtc transport stats.
   *
   * @param statObject the stat object
   */
  public RTCTransportStats(Map<Object, Object> statObject) {
    this.setId(getStatByName(statObject, "id"));
    this.rtcpTransportStatsId = getStatByName(statObject, "rtcpTransportStatsId");
    this.selectedCandidatePairId = getStatByName(statObject, "selectedCandidatePairId");
    this.localCertificateId = getStatByName(statObject, "localCertificateId");
    this.remoteCertificateId = getStatByName(statObject, "remoteCertificateId");
    this.bytesSent = getStatByName(statObject, "bytesSent");
    this.bytesReceived = getStatByName(statObject, "bytesReceived");
    this.timestamp = getStatByName(statObject, "timestamp");
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("rtcpTransportStatsId", this.rtcpTransportStatsId)
        .add("selectedCandidatePairId", this.selectedCandidatePairId)
        .add("localCertificateId", this.localCertificateId)
        .add("remoteCertificateId", this.remoteCertificateId)
        .add("bytesSent", this.bytesSent)
        .add("bytesReceived", this.bytesReceived)
        .add("timestamp", this.timestamp);
    return jsonObjectBuilder;
  }
}
