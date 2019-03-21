const {Builder, By, Key, until, promise} = require('selenium-webdriver');
const TestUtils = require('../kite-common-js-lib/util/TestUtils.js');

const map = promise.map;

module.exports = {
	execute: async function(driver, testReport, url, timeout) {
		let report = {};
		let result = 'passed';
		let attachment = {};
		attachment['type'] = 'json';
    report['name'] = 'Open ' + url + ' and check the video';
    report['start'] = Date.now();
    if (testReport.status === 'passed') {
      try {
        await driver.get(url);
        console.log('wait for page to be loaded');
        await driver.wait(async function() {
          const s = await driver.executeScript("return document.readyState");
          return s === "complete";
        }, timeout);
        // Wait for publishing button
        console.log('Page loaded. Now wait for text "Publishing..."');
        const publishingLocator = By.xpath("//b[text()='Publishing...']");
        await driver.wait(until.elementLocated(publishingLocator));
        let details = {};
				// Verify videos
				await TestUtils.waitForElement(driver, 'tagName', 'video', timeout);
        const videoElements = await driver.findElements(By.tagName("video"));
        const ids = await map(videoElements, e =>  e.getAttribute("id"));
		    ids.forEach(async function(id) {
		      const videoCheck = await TestUtils.verifyVideoDisplayById(driver, id);
		      details['videoCheck_' + id] = videoCheck;
		    });
        attachment['value'] = details;
      } catch (error) {
        result = 'failed';
        testReport['status'] = result;
        console.log(error);
      }
    } else {
      result = 'skipped';
    }
    report['attachment'] = attachment;
    report['status'] = result;
    report['stop'] = Date.now();
    testReport.steps.push(report);
	}
}