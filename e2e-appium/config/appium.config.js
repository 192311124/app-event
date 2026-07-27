const envConfig = require('./env.config');

function getCapabilities(overrides = {}) {
  const capabilities = {
    platformName: 'Android',
    'appium:automationName': envConfig.device.automationName,
    'appium:deviceName': envConfig.device.deviceName,
    'appium:platformVersion': envConfig.device.platformVersion,
    'appium:autoGrantPermissions': true,
    'appium:newCommandTimeout': 300,
    'appium:ensureCleanPackageState': false,
    'appium:noReset': false,
  };

  if (envConfig.device.udid) {
    capabilities['appium:udid'] = envConfig.device.udid;
  }

  if (envConfig.app.launchType === 'apk') {
    capabilities['appium:app'] = envConfig.app.apkPath;
  } else {
    capabilities['appium:appPackage'] = envConfig.app.package;
    capabilities['appium:appActivity'] = envConfig.app.activity;
  }

  return { ...capabilities, ...overrides };
}

function getWdioOptions(customCaps = {}) {
  return {
    hostname: envConfig.appium.host,
    port: envConfig.appium.port,
    path: envConfig.appium.basePath,
    logLevel: envConfig.test.logLevel,
    capabilities: getCapabilities(customCaps),
  };
}

module.exports = {
  getCapabilities,
  getWdioOptions,
};
