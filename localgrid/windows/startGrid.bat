@echo off
cd hub
start startHub.bat
cd ..\chrome
start startNode.bat
cd ..\firefox
start startNode.bat
cd..