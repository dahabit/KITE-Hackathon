package com.cosmo.kite.janus.steps;

import com.cosmo.kite.exception.KiteTestException;
import com.cosmo.kite.report.custom_kite_allure.Reporter;
import com.cosmo.kite.steps.TestStep;
import com.cosmo.kite.report.custom_kite_allure.Status;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import javax.json.*;
import java.util.ArrayList;
import java.util.List;

import static com.cosmo.kite.util.StatsUtils.extractStats;
import static com.cosmo.kite.util.StatsUtils.getPCStatOvertime;

public class GetStatsStep extends TestStep {




  private final int numberOfParticipants;
  private final int statsCollectionTime;
  private final int statsCollectionInterval;
  private final JsonArray selectedStats;

  public GetStatsStep(WebDriver webDriver, int numberOfParticipants, int statsCollectionTime,
                      int statsCollectionInterval, JsonArray selectedStats) {
    super(webDriver);
    this.numberOfParticipants = numberOfParticipants;
    this.statsCollectionTime = statsCollectionTime;
    this.statsCollectionInterval = statsCollectionInterval;
    this.selectedStats = selectedStats;
  }
  
  
  @Override
  public String stepDescription() {
    return "GetStats";
  }
  
  @Override
  protected void step() throws KiteTestException {
    logger.info("Getting WebRTC stats via getStats");
    try {
      JsonObject sentStats =
        getPCStatOvertime(webDriver, "window.pc", statsCollectionTime, statsCollectionInterval,
          selectedStats);
      JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
      List<JsonObject> receivedStats = new ArrayList<>();
      for (int i = 1; i < numberOfParticipants; i++) {
        JsonObject receivedObject = getPCStatOvertime(webDriver,
          "window.remotePc[" + (i-1) + "]",
          statsCollectionTime,
          statsCollectionInterval,
          selectedStats);
        receivedStats.add(receivedObject);
        arrayBuilder.add(receivedObject);
      }
      JsonObject json = extractStats(sentStats, receivedStats);
      JsonObjectBuilder builder = Json.createObjectBuilder();
      builder.add("local", sentStats);
      builder.add("remote", arrayBuilder);
      Reporter.getInstance().jsonAttachment(report, "getStatsRaw", builder.build());
      Reporter.getInstance().jsonAttachment(report, "getStatsSummary", json);
    } catch (Exception e) {
      e.printStackTrace();
      throw new KiteTestException("Failed to getStats", Status.BROKEN, e);
    }
  }
}
