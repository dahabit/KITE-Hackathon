###Before running the local grid:

Note: Electron is for Windows only. On Linux, you can run Appium and Chrome only locally.

Download selenium.jar (rename) from 
[SeleniumHQ page](https://www.seleniumhq.org/download/) into these sub folders:

- */chrome/
- */electron/
- */hub/

Download appropriate chromedriver from 
[here](http://chromedriver.chromium.org/downloads) into:
- */chrome/

Download appropriate chromedriver for Electron from  the mirror found in 
[here](https://github.com/electron/chromedriver)  into:
- */electron/

You can also modify the config file of the node to your requirements.


####For Electron node (windows only):

In KITE-RingCentral-Test/localgrid/windows/electron/startNode.bat
replace the PATH_TO_RingCentral_EXECUTABLE to your correct path to RingCentral.exe
Example: "C:\Users\manua\AppData\Local\Glip\app-18.08.1\RingCentral.exe"

####For Appium nodes:

Change the address and the port of the hub in the node config files for Appium to the 
appropriate address (your local hub) before running the startGrid file. 
Even if it is running on localhost, with Appium you must set the exact hub IP address in the 
two config files:
appium\config1.json and appium\config2.json

Change the `uidi` in the command line of the `startNode*.sh` to match your devices' `udid` 
(get via `adb devices`).

