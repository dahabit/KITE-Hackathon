const WebDriverUtility = require('./WebDriverUtility.js');

const {Builder, By, Key, until, promise} = require('selenium-webdriver');
module.exports = {
	getDriver: async function(browserName, remoteAddress) {
		const options = WebDriverUtility.getOptions(browserName)
		switch (browserName) {
			case 'chrome': {
				return new Builder().forBrowser(browserName)
          .setChromeOptions(options)
          .usingServer(remoteAddress).build();
			}
			default:
				throw new Error('Unsupported browser type: ' + browserName);
		}

	}
}
