@echo off
java -Dkite.firefox.profile=../third_party/ -cp ../KITE-Engine/target/kite-jar-with-dependencies.jar;../KITE-Common/target/kite-common-1.0-SNAPSHOT.jar;../KITE-Engine/target/kite-engine-1.0-SNAPSHOT.jar;target/simulcast-test-1.0-SNAPSHOT.jar org.webrtc.kite.Engine configs/janus.simulcast.config.json
