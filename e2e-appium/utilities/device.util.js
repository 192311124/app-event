const path = require('path');
const fs = require('fs');
const { execSync } = require('child_process');
const logger = require('./logger');
const envConfig = require('../config/env.config');

class DeviceUtil {
  /**
   * Capture logcat logs for current device session and save to logs/
   */
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
        // Fallback to ADB command if WebdriverIO logcat endpoint is not active
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

  /**
   * Get current top Android Activity
   */
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

  /**
   * Hide soft keyboard if visible
   */
  async hideKeyboard() {
    try {
      const isKeyboardShown = await browser.isKeyboardShown();
      if (isKeyboardShown) {
        logger.info('Hiding soft keyboard');
        await browser.hideKeyboard();
      }
    } catch (err) {
      // Soft keyboard might already be hidden
    }
  }

  /**
   * Press Android Back Button
   */
  async pressBackButton() {
    logger.info('Pressing Android hardware Back button');
    await browser.back();
  }

  /**
   * Relaunch the target Android Application
   */
  async relaunchApp() {
    logger.info(`Relaunching application package: ${envConfig.app.package}`);
    try {
      await browser.terminateApp(envConfig.app.package);
    } catch (err) {
      // Ignore if app wasn't active
    }
    await browser.activateApp(envConfig.app.package);
    await browser.pause(1000);
  }

  /**
   * Open Deep Link URL in Android app
   */
  async openDeepLink(deepLinkUrl) {
    logger.info(`Opening deep link: ${deepLinkUrl}`);
    const cmd = `adb shell am start -a android.intent.action.VIEW -d "${deepLinkUrl}" ${envConfig.app.package}`;
    execSync(cmd);
    await browser.pause(1500);
  }

  /**
   * Accept Android native alert dialog
   */
  async acceptAlert() {
    try {
      await browser.acceptAlert();
      logger.info('Accepted native alert dialog');
    } catch (e) {
      logger.warn('No active alert dialog found to accept');
    }
  }

  /**
   * Dismiss Android native alert dialog
   */
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
