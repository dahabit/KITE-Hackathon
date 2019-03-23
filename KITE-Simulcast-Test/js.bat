@echo off
IF "%1"=="h264" (
	java -cp ../KITE-Engine/target/kite-jar-with-dependencies.jar;../KITE-Common/target/kite-common-1.0-SNAPSHOT.jar;../KITE-Engine-IF/target/kite-if-1.0-SNAPSHOT.jar;../KITE-Engine/target/kite-engine-1.0-SNAPSHOT.jar;target/Janus-test-1.0-SNAPSHOT.jar org.webrtc.kite.Engine configs/js.h264.medooze.config.json
) else IF "%1"=="vp8" (
 	java -cp ../KITE-Engine/target/kite-jar-with-dependencies.jar;../KITE-Common/target/kite-common-1.0-SNAPSHOT.jar;../KITE-Engine-IF/target/kite-if-1.0-SNAPSHOT.jar;../KITE-Engine/target/kite-engine-1.0-SNAPSHOT.jar;target/Janus-test-1.0-SNAPSHOT.jar org.webrtc.kite.Engine configs/js.vp8.medooze.config.json
) else (
	java -cp ../KITE-Engine/target/kite-jar-with-dependencies.jar;../KITE-Common/target/kite-common-1.0-SNAPSHOT.jar;../KITE-Engine-IF/target/kite-if-1.0-SNAPSHOT.jar;../KITE-Engine/target/kite-engine-1.0-SNAPSHOT.jar;target/Janus-test-1.0-SNAPSHOT.jar org.webrtc.kite.Engine configs/js.config.json

)
