const path = require('path');
const fs = require('fs');
const { execSync } = require('child_process');
const logger = require('./logger');
const envConfig = require('../config/env.config');

class DeviceUtil {
  async captureLogcatLogs(testName = 'test') {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const filename = `logcat_${testName.replace(/[^a-zA-Z0-9]/g, '_')}_${timestamp}.log`;
    const logPath = path.resolve(__dirname, '../logs', filename);

    try {
      logger.info(`Capturing Logcat device logs to ${logPath}`);
      let logs = [];
      try {
        logs = await browser.getLogs('logcat');
      } catch (err) {
        const adbLogs = execSync(`adb logcat -d *:V`, { encoding: 'utf-8', maxBuffer: 10 * 1024 * 1024 });
        fs.writeFileSync(logPath, adbLogs);
        return logPath;
      }

      const logText = logs.map(l => `[${l.timestamp}] [${l.level}] ${l.message}`).join('\n');
      fs.writeFileSync(logPath, logText);
      return logPath;
    } catch (error) {
      logger.warn(`Could not capture device logcat logs: ${error.message}`);
      return null;
    }
  }

  async getCurrentActivity() {
    try {
      return await browser.getCurrentActivity();
    } catch (e) {
      try {
        const output = execSync('adb shell "dumpsys window | grep mCurrentFocus"', { encoding: 'utf-8' });
        return output.trim();
      } catch (err) {
        return 'UnknownActivity';
      }
    }
  }

  async hideKeyboard() {
    try {
      const isKeyboardShown = await browser.isKeyboardShown();
      if (isKeyboardShown) {
        logger.info('Hiding soft keyboard');
        await browser.hideKeyboard();
      }
    } catch (err) {
      // ignore
    }
  }

  async pressBackButton() {
    logger.info('Pressing Android hardware Back button');
    await browser.back();
  }

  async relaunchApp() {
    logger.info(`Relaunching application package: ${envConfig.app.package}`);
    try {
      await browser.terminateApp(envConfig.app.package);
    } catch (err) {
      // ignore
    }
    await browser.activateApp(envConfig.app.package);
    await browser.pause(1000);
  }

  async openDeepLink(deepLinkUrl) {
    logger.info(`Opening deep link: ${deepLinkUrl}`);
    const cmd = `adb shell am start -a android.intent.action.VIEW -d "${deepLinkUrl}" ${envConfig.app.package}`;
    execSync(cmd);
    await browser.pause(1500);
  }

  async acceptAlert() {
    try {
      await browser.acceptAlert();
      logger.info('Accepted native alert dialog');
    } catch (e) {
      logger.warn('No active alert dialog found to accept');
    }
  }

  async dismissAlert() {
    try {
      await browser.dismissAlert();
      logger.info('Dismissed native alert dialog');
    } catch (e) {
      logger.warn('No active alert dialog found to dismiss');
    }
  }
}

module.exports = new DeviceUtil();
