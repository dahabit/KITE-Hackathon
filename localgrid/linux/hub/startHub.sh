#!/bin/sh
ip="$(hostname -I|cut -f1 -d ' ')"

java -jar ../../selenium.jar -role hub --debug -host $ip

