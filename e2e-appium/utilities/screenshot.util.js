const path = require('path');
const fs = require('fs');
const logger = require('./logger');

class ScreenshotUtil {
  constructor() {
    this.failureDir = path.resolve(__dirname, '../reports/failures');
    this.screenshotsDir = path.resolve(__dirname, '../screenshots');

    this.ensureDirectoryExists(this.failureDir);
    this.ensureDirectoryExists(this.screenshotsDir);
  }

  ensureDirectoryExists(dir) {
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
  }

  /**
   * Capture screenshot on test failure
   */
  async captureFailureScreenshot(testTitle = 'failure') {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const sanitizedTitle = testTitle.replace(/[^a-zA-Z0-9_-]/g, '_');
    const filename = `FAIL_${sanitizedTitle}_${timestamp}.png`;
    const filePath = path.join(this.failureDir, filename);

    try {
      logger.info(`Capturing failure screenshot to: ${filePath}`);
      await browser.saveScreenshot(filePath);
      return filePath;
    } catch (error) {
      logger.error(`Failed to capture screenshot: ${error.message}`);
      return null;
    }
  }

  /**
   * Capture custom timestamped step screenshot
   */
  async captureScreenshot(stepName = 'step') {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const sanitizedTitle = stepName.replace(/[^a-zA-Z0-9_-]/g, '_');
    const filename = `${sanitizedTitle}_${timestamp}.png`;
    const filePath = path.join(this.screenshotsDir, filename);

    try {
      logger.info(`Capturing step screenshot to: ${filePath}`);
      await browser.saveScreenshot(filePath);
      return filePath;
    } catch (error) {
      logger.error(`Failed to capture step screenshot: ${error.message}`);
      return null;
    }
  }
}

module.exports = new ScreenshotUtil();
