@echo off
setlocal
  title Hub
  for /f "delims=[] tokens=2" %%a in ('ping -4 -n 1 %ComputerName% ^| findstr [') do set NetworkIP=%%a
  java -jar ../../selenium.jar -role hub --debug -host %NetworkIP%
endlocal
pause