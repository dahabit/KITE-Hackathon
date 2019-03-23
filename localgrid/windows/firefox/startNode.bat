@echo off
setlocal
  title Firefox Node
  for /f "delims=[] tokens=2" %%a in ('ping -4 -n 1 %ComputerName% ^| findstr [') do set NetworkIP=%%a
  echo Hub IP: %NetworkIP%
  java -Dwebdriver.gecko.driver=./geckodriver.exe -jar ../../selenium.jar -role node -maxSession 10 -port 6001 -host %NetworkIP% -hub http://%NetworkIP%:4444/grid/register  -browser browserName=firefox,version=65,platform=WINDOWS,maxInstances=10 --debug 
  endlocal 
pause