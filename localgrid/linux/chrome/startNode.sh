#!/bin/sh
ip="$(hostname -I|cut -f1 -d ' ')"

java -Dwebdriver.chrome.driver=./chromedriver -jar selenium.jar -role node -maxSession 10 -port 6000 -host $ip -hub http://$ip:4444/grid/register -browser browserName=chrome,version=70,platform=LINUX,maxInstances=5 --debug

