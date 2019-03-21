/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * RTCMediaStreamTrackStats, with attributes trackIdentifier, remoteSource,
 * ended, detached, frameWidth, frameHeight, framesPerSecond, framesSent, framesReceived,
 * framesDecoded, framesDropped, framesCorrupted, audioLevel
 */
public class RTCMediaStreamTrackStats extends RTCStatObject {
  private String trackIdentifier, remoteSource, ended, detached,
    frameWidth, frameHeight, framesPerSecond, framesSent, framesReceived,
    framesDecoded, framesDropped, framesCorrupted, audioLevel, timestamp;
  
  /**
   * Instantiates a new Rtc media stream track stats.
   *
   * @param statObject the stat object
   */
  public RTCMediaStreamTrackStats(Map<Object, Object> statObject) {
    this.setId(getStatByName(statObject, "id"));
    this.trackIdentifier = getStatByName(statObject, "trackIdentifier");
    this.remoteSource = getStatByName(statObject, "remoteSource");
    this.ended = getStatByName(statObject, "ended");
    this.detached = getStatByName(statObject, "detached");
    this.frameWidth = getStatByName(statObject, "frameWidth");
    this.frameHeight = getStatByName(statObject, "frameHeight");
    this.framesPerSecond = getStatByName(statObject, "framesPerSecond");
    this.framesSent = getStatByName(statObject, "framesSent");
    this.framesReceived = getStatByName(statObject, "framesReceived");
    this.framesDecoded = getStatByName(statObject, "framesDecoded");
    this.framesDropped = getStatByName(statObject, "framesDropped");
    this.framesCorrupted = getStatByName(statObject, "framesCorrupted");
    this.audioLevel = getStatByName(statObject, "audioLevel");
    this.timestamp = getStatByName(statObject, "timestamp");
    
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("trackIdentifier", this.trackIdentifier)
        .add("remoteSource", this.remoteSource)
        .add("ended", this.ended)
        .add("detached", this.detached)
        .add("frameWidth", this.frameWidth)
        .add("frameHeight", this.frameHeight)
        .add("framesPerSecond", this.framesPerSecond)
        .add("framesSent", this.framesSent)
        .add("framesReceived", this.framesReceived)
        .add("frameHeight", this.frameHeight)
        .add("framesDecoded", this.framesDecoded)
        .add("framesDropped", this.framesDropped)
        .add("framesCorrupted", this.framesCorrupted)
        .add("audioLevel", this.audioLevel)
        .add("timestamp", this.timestamp);
    
    return jsonObjectBuilder;
  }
}
