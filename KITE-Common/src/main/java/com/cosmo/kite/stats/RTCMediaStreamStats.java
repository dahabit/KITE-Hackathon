/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * RTCMediaStreamStats, with attributes streamIdentifer, trackIds
 */
public class RTCMediaStreamStats extends RTCStatObject {
  private String streamIdentifer, trackIds;
  
  /**
   * Instantiates a new Rtc media stream stats.
   *
   * @param statObject the stat object
   */
  public RTCMediaStreamStats(Map<Object, Object> statObject) {
    this.setId(getStatByName(statObject, "id"));
    this.streamIdentifer = getStatByName(statObject, "streamIdentifer");
    this.trackIds = getStatByName(statObject, "trackIds");
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("streamIdentifer", this.streamIdentifer)
        .add("trackIds", this.trackIds);
    return jsonObjectBuilder;
  }
}
