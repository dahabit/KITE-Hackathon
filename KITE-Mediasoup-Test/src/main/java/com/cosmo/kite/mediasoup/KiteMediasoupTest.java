package com.cosmo.kite.mediasoup;

import com.cosmo.kite.mediasoup.checks.AllVideoCheck;
import com.cosmo.kite.mediasoup.checks.FirstVideoCheck;
import com.cosmo.kite.mediasoup.steps.*;
import com.cosmo.kite.manager.RoomManager;
import com.cosmo.kite.tests.KiteCallable;
import com.cosmo.kite.tests.KiteLoadTest;
import com.cosmo.kite.util.TestUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import javax.json.JsonArray;
import javax.json.JsonObject;

import static org.webrtc.kite.Utility.getStackTrace;

public class KiteMediasoupTest extends KiteLoadTest {

  private final Logger logger = Logger.getLogger(this.getClass().getName());

  private int loadReachTime = 0;

  
  @Override
  protected void payloadHandling() {
    super.payloadHandling();
    JsonObject jsonPayload = (JsonObject) this.payload;
    String[] rooms = null;
    if (jsonPayload != null) {
      testName = jsonPayload.getString("name", "Mediasoup Load Test");
      loadReachTime = jsonPayload.getInt("loadReachTime", loadReachTime);
      expectedTestDuration = Math.max(expectedTestDuration, (loadReachTime + 300)/60);
      maxUsersPerRoom = jsonPayload.getInt("usersPerRoom", 0);
      JsonArray jsonArray = jsonPayload.getJsonArray("rooms");
      rooms = new String[jsonArray.size()];
      for (int i = 0; i < jsonArray.size(); i++) {
        rooms[i] = jsonArray.getString(i);
      }
    }
    KiteLoadTest.roomManager = RoomManager.getInstance(url, maxUsersPerRoom);
    if (rooms != null) {
      roomManager.setRoomNames(rooms);
    }
  }
  
  @Override
  protected void populateCallables() {
    try {
      int id = 1;
      for (WebDriver webDriver : this.webDriverList) {
        String roomUrl = roomManager.getRoomUrl()  + "&username=user" + TestUtils.idToString(id++);
        KiteCallable callable = new KiteCallable(webDriver, this.report);
        callable.addStep(new JoinVideoCallStep(webDriver, roomUrl));
        if (!this.fastRampUp()) {
          callable.addStep(new FirstVideoCheck(webDriver));
          callable.addStep(new AllVideoCheck(webDriver, maxUsersPerRoom));
          if (this.getStats()) {
            callable.addStep(
                new GetStatsStep(
                    webDriver,
                    maxUsersPerRoom,
                    getStatsCollectionTime(),
                    getStatsCollectionInterval(),
                    getSelectedStats()));
          }
          if (this.takeScreenshotForEachTest()) {
            callable.addStep(new ScreenshotStep(webDriver));
          }
          if (this.loadReachTime > 0) {
            callable.addStep(new StayInMeetingStep(webDriver, loadReachTime));
          }
        }
        this.callables.add(callable);
      }
    } catch (Exception e) {
      logger.error(getStackTrace(e));
    }
  }
}
