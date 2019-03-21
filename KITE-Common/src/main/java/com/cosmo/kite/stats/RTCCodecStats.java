/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * RTCCodecStats, with attributes payloadType, codec, clockRate, channels, sdpFmtpLine
 */
public class RTCCodecStats extends RTCStatObject {
  
  private String payloadType, codec, clockRate, channels, sdpFmtpLine;
  
  
  /**
   * Instantiates a new Rtc codec stats.
   *
   * @param statObject the stat object
   */
  public RTCCodecStats(Map<Object, Object> statObject) {
    this.setId(getStatByName(statObject, "id"));
    this.payloadType = getStatByName(statObject, "payloadType");
    this.clockRate = getStatByName(statObject, "clockRate");
    this.channels = getStatByName(statObject, "channels");
    this.codec = getStatByName(statObject, "codec");
    this.sdpFmtpLine = getStatByName(statObject, "sdpFmtpLine");
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("payloadType", this.payloadType)
        .add("clockRate", this.clockRate)
        .add("channels", this.channels)
        .add("codec", this.codec)
        .add("sdpFmtpLine", this.sdpFmtpLine);
    return jsonObjectBuilder;
  }
}
