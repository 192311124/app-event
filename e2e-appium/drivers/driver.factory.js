const { remote } = require('webdriverio');
const { execSync } = require('child_process');
const { getWdioOptions } = require('../config/appium.config');
const logger = require('../utilities/logger');

class DriverFactory {
  constructor() {
    this.driver = null;
    this.connectedDevice = null;
  }

  /**
   * Dynamically detect connected devices using ADB
   */
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

  /**
   * Create and initialize WebDriverIO Appium session
   */
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
      logger.error(`Failed to initialize Appium session: ${error.message}`);
      throw error;
    }
  }

  /**
   * Get current driver instance
   */
  getDriver() {
    if (!this.driver) {
      throw new Error('Driver instance not initialized. Call createDriver() first.');
    }
    return this.driver;
  }

  /**
   * Quit Appium session
   */
  async quitDriver() {
    if (this.driver) {
      logger.info(`Terminating Appium Session ID: ${this.driver.sessionId}`);
      try {
        await this.driver.deleteSession();
      } catch (err) {
        logger.warn(`Error terminating Appium session: ${err.message}`);
      } finally {
        this.driver = null;
      }
    }
  }
}

module.exports = new DriverFactory();
