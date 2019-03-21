/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.stats;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * RTCCertificateStats, with attributes fingerprint,
 * fingerprintAlgorithm, base64Certificate, issuerCertificateId
 */
public class SDP extends RTCStatObject {
  
  private String type, sdp;
  
  /**
   * Instantiates a new Sdp.
   *
   * @param statObject the stat object
   */
  public SDP(Map<Object, Object> statObject) {
    this.type = getStatByName(statObject, "type");
    this.sdp = getStatByName(statObject, "sdp");
  }
  
  @Override
  public JsonObjectBuilder getJsonObjectBuilder() {
    JsonObjectBuilder jsonObjectBuilder =
      Json.createObjectBuilder()
        .add("type", this.type)
        .add("sdp", this.sdp);
    return jsonObjectBuilder;
  }
}
