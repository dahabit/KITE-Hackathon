#!/bin/sh
cd hub &&
xterm -T "Hub" -e "./startHub.sh" &

cd chrome &&
xterm -T "Chrome Node" -e "./startNode.sh" &&

cd firefox &&
xterm -T "Firefox Node" -e "./startNode.sh" &&

read -p "Ended, press enter to continue" nothing


