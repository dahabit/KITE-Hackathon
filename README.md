# This is KITE, Karoshi Interoperability Testing Engine

The effortless way to test WebRTC compliance, prevent [Karoshi](https://en.wikipedia.org/wiki/Kar%C5%8Dshi) with KITE!

# This is not an official Google product

See LICENSE for licensing.

# I. Single Machine Test setup

## A. Setup Selenium Standalone:

### Install prerequisite software

* Install the browsers you would like to test, available for your machine. Chrome, Edge, Firefox and Safari are supported at this stage. See the wiki for some limitations or hints for each browser.
* Make sure you have a JDK 1.8 (Java 8) installed. It can be downloaded at https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html. Sometimes it might be neccessary to set JAVA_HOME and add it to PATH for Java and Maven to work properly.
NOTE: KITE is not compatible with Java 11.

### Download webdrivers and selenium server standalone

*  Create a new working directory and move in there.
*  Download the corresponding webdrivers on the root of a new working directory:

   *   Download the latest [*chrome webdriver*](https://sites.google.com/a/chromium.org/chromedriver/downloads),
   *   Download the latest [*firefox webdriver*](https://github.com/mozilla/geckodriver/releases),
   *   On Windows, download the latest [*edge webdriver*](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/),

*  Download [*Selenium Server Standalone*](https://www.seleniumhq.org/download/) in the same folder

### If on MAC, enable safari automation

Enable the 'Allow Remote Automation' option in Safari's Develop menu to control Safari via WebDriver.

### Start selenium-server-standalone

Run this command , don't stop it until testing session has finished. Also make sure that you have Firefox and Chrome already installed on your testing machine.

On Linux and Mac run:
```
java -Dwebdriver.chrome.driver=./chromedriver -Dwebdriver.gecko.driver=./geckodriver -jar selenium-server-standalone-3.x.x.jar
```

On Windows run:
```
java -Dwebdriver.chrome.driver=./chromedriver.exe -Dwebdriver.gecko.driver=./geckodriver.exe -Dwebdriver.edge.driver=./MicrosoftWebDriver.exe -jar selenium-server-standalone-3.x.x.jar
```

*  ```-Dwebdriver.xxxx.driver``` specifies the path to the webdriver executable matching the browser xxxx (possible values of xxxx are: gecko, chrome, edge, safari ...).
*  Depending on platform and the testing needs, command line can include one, two or the three drivers

## B. Build and KITE Engine and the basic example test

Build uses [*maven*](https://maven.apache.org/) tool. Installable maven packages are available for common platforms, see [*link*](https://maven.apache.org/install.html) for manual installation.

### Build KITE-Engine and KITE-AppRTC-Test

1.  Clone the repository to your local machine
Download this repository and extract it to your local folder or clone it with

```
https://github.com/ManuCosmo/KITE-Hackathon.git
```

2. Build and install running the command

```
mvn -DskipTests clean install
```


## C. Setup the Dashboard

Install Allure from https://docs.qameta.io/allure/


## D. Run sample basic test

### Choose and edit your test run configuration

You can use example configuration file `./KITE-Example-Test/configs/example.config.json` as starting point.

Read below about the configuration file, check that the desired browsers listed in your configuration file are available in your system.

### Understanding a basic configuration file

The example local.config.json file is almost the simplest config file you can get (Change the version of browsers to the appropriated one that you have installed on your testing machine):

```
{
  "name": "Kite test example (with Allure reporting)",
  "callback": null,
  "reportFolder" : "/home/nam/workSpace/BackEnd/KITEs/KITE.example/",
  "remotes": [
    {
      "type": "local",
      "remoteAddress": "http://localhost:4444/wd/hub"
    }
  ],
  "tests": [
    {
      "name": "KiteExampleTest",
      "tupleSize": 1,
      "description": "This example test opens google and searches for Cosmo Software Consulting and verify the first result",
      "testImpl": "com.cosmo.kite.example.KiteExampleTest",
      "payload" : {
        "test1": "ONE",
        "test2": "TWO"
      }
    }
  ],
  "browsers": [
    {
      "browserName": "chrome",
      "version": "72",
      "platform": "LINUX",
      "flags": []
    },
    {
      "browserName": "firefox",
      "version": "65",
      "platform": "MAC",
      "flags": []
    }
  ]
}

```

It registers only selenium server in the local machine:
```json
  "remotes": [
    {
      "type": "local",
      "remoteAddress": "http://localhost:4444/wd/hub"
    }
  ],
```

It registers IceConnectionTest class as a test (this class is implemented in KITE-AppRTC-Test)
```json
  "tests": [
    {
      "name": "KiteExampleTest",
      "tupleSize": 1,
      "description": "This example test opens google and searches for Cosmo Software Consulting and verify the first result",
      "testImpl": "com.cosmo.kite.example.KiteExampleTest",
      "payload" : {
        "test1": "ONE",
        "test2": "TWO"
      }
    }
  ],
```

It requests for firefox and chrome. Version and platform are required fields. Version and platform actually used in the tests will be reported in the result, and will appear in the dashboard.

Sample config files in ```KITE-Example-Test/configs``` contain the example with different browser, version and platform configuration, take a closer look

```json
  "browsers": [
    {
      "browserName": "chrome",
      "version": "72",
      "platform": "LINUX",
      "flags": []
    },
    {
      "browserName": "firefox",
      "version": "65",
      "platform": "MAC",
      "flags": []
    }
  ]
```

## Run the local test

Execute the following command in the working directory, the last argument specifies the configuration file specifying the tests:

On Linux and Mac run:
```
java -cp KITE-Common/target/kite-common-1.0-SNAPSHOT.jar:KITE-Engine/target/kite-engine-1.0-SNAPSHOT.jar:KITE-Engine/target/kite-jar-with-dependencies.jar:KITE-Example-Test/target/example-test-1.0-SNAPSHOT.jar org.webrtc.kite.Engine KITE-Example-Test/configs/example.config.json
```

On Windows run:
```
java -cp KITE-Common/target/kite-common-1.0-SNAPSHOT.jar;KITE-Engine/target/kite-engine-1.0-SNAPSHOT.jar;KITE-Engine/target/kite-jar-with-dependencies.jar;KITE-Example-Test/target/example-test-1.0-SNAPSHOT.jar org.webrtc.kite.Engine KITE-Example-Test/configs/example.config.json
```

Check the dashboard for the results and reports.

If you have followed steps above, that's [*http://localhost:8080/kiteweb*](http://localhost:8080/kiteweb).

By default, KITE will create a report folder that will work with [Allure](allure.qatools.ru) called kite-allure-reports.

To use this reporting tool, you'll need to install Allure like instructed [here](https://docs.qameta.io/allure/#_installing_a_commandline)

To deploy the Allure report:

```
allure serve PATH_TO/kite-allure-reports
```

# II. Distributed Test setup

## Setup Dashboard

KITE-Dashboard can be setup on any machine as described in previous section, you will need to change the callback url in your config file accordingly.

## Setup a hosted test service account

SauceLabs, BrowserStack and TestingBot have been tested and are supported.

See example files in ```KITE-AppRTC-Test/configs``` mixing different hosted test services.

Complete the fields username and accesskey appropriately.

* Don't forget to modify the example browsers, versions and platforms to suit your needs.

