@echo off
setlocal
  title Chrome Node
  for /f "delims=[] tokens=2" %%a in ('ping -4 -n 1 %ComputerName% ^| findstr [') do set NetworkIP=%%a
  echo Hub IP: %NetworkIP%
  java -Dwebdriver.chrome.driver=./chromedriver.exe -jar ../../selenium.jar -role node -maxSession 5 -port 6000 -host %NetworkIP% -hub http://%NetworkIP%:4444/grid/register -browser browserName=chrome,version=70,platform=WINDOWS,maxInstances=5 --debug
endlocal 
pause