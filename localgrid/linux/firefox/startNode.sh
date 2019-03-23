#!/bin/sh
ip="$(hostname -I|cut -f1 -d ' ')"

java -Dwebdriver.gecko.driver=./geckodriver.exe -jar ../../selenium.jar -role node -maxSession 10 -port 6001 -host $ip -hub http://$ip:4444/grid/register -browser browserName=firefox,version=65,platform=LINUX,maxInstances=10  --debug

