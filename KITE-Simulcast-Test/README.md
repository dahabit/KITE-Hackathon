#  KITE-Simulcast-Test

This sample test script is designed for testing the simulcast loopback page against medooze SFU.

## Test Script


1.	Open URL https://playground.cosmosoftware.io/simulcast/index.html?codec=h264
2.	Check the published video
3.	Check received video (looped back) video
4.	GetStats on all the peerConnection
5.	Take a screenshot
6.	For each of the 3 layers: click the button (A0, B0, C0), then check the received video resolution: if the sent video is 1280x720, the received resolution is expected to be respectively:
A0: 1280x720
B0: 640x360
C0: 320x180


## Pre-requisite: Selenium Grid

To run this test you will need a Selenium Grid with the browsers to be tested.

## Config
 
 A sample config file is provided at  
 
 `configs/local.simulcast.config.json`





You should not need to change any other parameter.


## Compile

Under the root directory:  
``` 
mvn -DskipTests clean install 
``` 

## Run

Under the KITE-Simulcast-Test/ folder, execute:
```
java -Dkite.firefox.profile=../third_party/ -cp ../KITE-Engine/target/kite-jar-with-dependencies.jar;../KITE-Common/target/kite-common-1.0-SNAPSHOT.jar;../KITE-Engine-IF/target/kite-if-1.0-SNAPSHOT.jar;../KITE-Engine/target/kite-engine-1.0-SNAPSHOT.jar;target/simulcast-test-1.0-SNAPSHOT.jar org.webrtc.kite.Engine configs/local.simulcast.config.json
```


## Test output

Each will generate allure report found in `kite-allure-report/` folder.
To run Allure:
```
allure serve kite-allure-reports
```





