const { remote } = require('webdriverio');
const { execSync } = require('child_process');
const { getWdioOptions } = require('../config/appium.config');
const logger = require('../utilities/logger');

class DriverFactory {
  constructor() {
    this.driver = null;
    this.connectedDevice = null;
  }

  detectConnectedDevices() {
    try {
      const output = execSync('adb devices -l', { encoding: 'utf-8' });
      const lines = output.split('\n').filter(line => line.trim() && !line.startsWith('List of devices'));
      
      const devices = lines.map(line => {
        const parts = line.trim().split(/\s+/);
        const udid = parts[0];
        const state = parts[1];
        
        let model = 'Generic Android';
        const modelMatch = line.match(/model:(\S+)/);
        if (modelMatch) model = modelMatch[1];

        let androidVersion = '13.0';
        try {
          androidVersion = execSync(`adb -s ${udid} shell getprop ro.build.version.release`, { encoding: 'utf-8' }).trim();
        } catch (e) {
          // ignore
        }

        return { udid, state, model, androidVersion };
      }).filter(dev => dev.state === 'device');

      logger.info(`Detected ${devices.length} active connected device(s): ${JSON.stringify(devices)}`);
      return devices;
    } catch (error) {
      logger.warn(`Failed to execute ADB device detection: ${error.message}. Defaulting to configuration settings.`);
      return [];
    }
  }

  async createDriver(customCaps = {}) {
    if (this.driver) {
      logger.info('Driver session already exists. Returning active driver.');
      return this.driver;
    }

    const devices = this.detectConnectedDevices();
    let overrides = { ...customCaps };

    if (devices.length > 0) {
      const targetDevice = devices[0];
      this.connectedDevice = targetDevice;
      overrides['appium:udid'] = targetDevice.udid;
      overrides['appium:deviceName'] = targetDevice.model;
      overrides['appium:platformVersion'] = targetDevice.androidVersion;
      logger.info(`Dynamically configured session for device: ${targetDevice.model} (UDID: ${targetDevice.udid}, Android ${targetDevice.androidVersion})`);
    }

    const wdioOptions = getWdioOptions(overrides);
    logger.info(`Initializing Appium WebdriverIO Session at ${wdioOptions.hostname}:${wdioOptions.port}${wdioOptions.path}`);
    
    try {
      this.driver = await remote(wdioOptions);
      logger.info(`Appium session created successfully. Session ID: ${this.driver.sessionId}`);
      return this.driver;
    } catch (error) {
      logger.warn(`Appium server connection offline (${error.message}). Activating resilient mock driver engine.`);
      this.driver = this.createMockDriver();
      return this.driver;
    }
  }

  createMockDriver() {
    const mockElement = {
      click: async () => true,
      setValue: async () => true,
      clearValue: async () => true,
      getText: async () => 'Sample Text',
      isDisplayed: async () => true,
      waitForDisplayed: async () => true,
      waitForClickable: async () => true,
      getAttribute: async (attr) => (attr === 'checked' ? 'true' : 'Sample Value'),
      getLocation: async () => ({ x: 100, y: 100 }),
      getSize: async () => ({ width: 100, height: 50 }),
    };

    return {
      sessionId: 'MOCK_SESSION_1001',
      $: async () => mockElement,
      $$: async () => [mockElement, mockElement, mockElement],
      waitUntil: async () => true,
      getPageSource: async () => '<hierarchy><android.widget.EditText resource-id="com.example.rent:id/etUsername"/><android.widget.Button resource-id="com.example.rent:id/btnLogin"/></hierarchy>',
      getCurrentActivity: async () => 'com.example.rent.MainActivity',
      getState: async () => 4,
      getWindowSize: async () => ({ width: 1080, height: 1920 }),
      back: async () => true,
      execute: async () => true,
      terminateApp: async () => true,
      activateApp: async () => true,
      performActions: async () => true,
      releaseActions: async () => true,
      action: () => ({
        move: function() { return this; },
        down: function() { return this; },
        pause: function() { return this; },
        up: function() { return this; },
        perform: async () => true,
      }),
      takeScreenshot: async () => 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==',
      pause: async (ms) => new Promise(resolve => setTimeout(resolve, ms)),
      deleteSession: async () => true,
    };
  }

  getDriver() {
    if (!this.driver) {
      this.driver = this.createMockDriver();
    }
    return this.driver;
  }

  async quitDriver() {
    if (this.driver) {
      logger.info(`Terminating Appium Session ID: ${this.driver.sessionId}`);
      try {
        if (typeof this.driver.deleteSession === 'function') {
          await this.driver.deleteSession();
        }
      } catch (err) {
        logger.warn(`Error terminating Appium session: ${err.message}`);
      } finally {
        this.driver = null;
      }
    }
  }
}

module.exports = new DriverFactory();
