const {Builder, By, Key, until, promise} = require('selenium-webdriver');
const TestUtils = require('../utils/TestUtils.js');
const waitAround = TestUtils.waitAround;
const getStatDuration = 5000;

module.exports = {
	execute: async function(driver, testReport, pcArray) {
		let report = {};
		let attachment = {};
		attachment['type'] = 'json';
		let stats = {};
		let result = 'passed';
		const receivedStats = [];
    report['name'] = 'Getting WebRTC stats via getStats';
    report['start'] = Date.now();
    if (testReport.status === 'passed') {
			try {
        console.log('executing: ' + report.name);
				pcArray.forEach( async(pc) => {
					if (pc.includes('remote')) {
						receivedStats.push(await TestUtils.getStats(driver, pc, getStatDuration, 1000));
					} else {
						stats['sentStats'] = await TestUtils.getStats(driver, pc, getStatDuration, 1000);
					}
				});
				stats['receivedStats'] = receivedStats;
			} catch (error) {
				result = 'failed';
				console.log(error);
				//report['error'] = error;
			}
		} else {
      result = 'skipped';
      console.log('skipping: ' + report.name);
		}
		await waitAround(getStatDuration*pcArray.length); // wait for getstats to actually finish
    report['status'] = result;
    report['stop'] = Date.now();
    attachment['value'] = stats;
    report['attachment'] = attachment;
    testReport.steps.push(report);
	}
}