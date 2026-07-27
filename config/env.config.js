const path = require('path');
const dotenv = require('dotenv');

dotenv.config({ path: path.resolve(__dirname, '../.env') });

module.exports = {
  appium: {
    host: process.env.APPIUM_HOST || '127.0.0.1',
    port: parseInt(process.env.APPIUM_PORT, 10) || 4723,
    basePath: process.env.APPIUM_BASE_PATH || '/',
  },
  device: {
    mode: process.env.EXECUTION_MODE || 'emulator',
    deviceName: process.env.DEVICE_NAME || 'Android Emulator',
    platformVersion: process.env.PLATFORM_VERSION || '13.0',
    automationName: process.env.AUTOMATION_NAME || 'UiAutomator2',
    udid: process.env.DEVICE_UDID || null,
  },
  app: {
    launchType: process.env.APP_LAUNCH_TYPE || 'installed',
    apkPath: path.resolve(__dirname, '..', process.env.APK_PATH || './app/build/outputs/apk/debug/app-debug.apk'),
    package: process.env.APP_PACKAGE || 'com.example.rent',
    activity: process.env.APP_ACTIVITY || 'com.example.rent.MainActivity',
  },
  timeouts: {
    explicitWait: parseInt(process.env.EXPLICIT_WAIT_TIMEOUT, 10) || 15000,
    implicitWait: parseInt(process.env.IMPLICIT_WAIT_TIMEOUT, 10) || 5000,
  },
  test: {
    retries: parseInt(process.env.TEST_RETRIES, 10) || 1,
    environment: process.env.ENVIRONMENT || 'staging',
    logLevel: process.env.LOG_LEVEL || 'info',
  },
};
