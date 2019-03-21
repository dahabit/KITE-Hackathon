/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * RTCIceCandidateStats, with attributes ip, port, protocol, candidateType, priority, url
 */
public class RTCIceCandidateStats extends RTCStatObject {
  
  private String ip, port, protocol, candidateType, priority, url;
  
  /**
   * Instantiates a new Rtc ice candidate stats.
   *
   * @param statObject the stat object
   */
  public RTCIceCandidateStats(Map<Object, Object> statObject) {
    this.setId(getStatByName(statObject, "id"));
    this.ip = getStatByName(statObject, "ip");
    this.port = getStatByName(statObject, "port");
    this.protocol = getStatByName(statObject, "protocol");
    this.candidateType = getStatByName(statObject, "candidateType");
    this.priority = getStatByName(statObject, "priority");
    this.url = getStatByName(statObject, "url");
    
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("ip", this.ip)
        .add("port", this.port)
        .add("protocol", this.protocol)
        .add("candidateType", this.candidateType)
        .add("priority", this.priority)
        .add("url", this.url);
    return jsonObjectBuilder;
  }
}
