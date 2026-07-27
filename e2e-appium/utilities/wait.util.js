const envConfig = require('../config/env.config');
const logger = require('./logger');

class WaitUtil {
  /**
   * Wait for element to be displayed
   */
  async waitForDisplayed(element, timeout = envConfig.timeouts.explicitWait, customMessage = '') {
    const el = typeof element === 'string' ? await browser.$(element) : element;
    const msg = customMessage || `Element ${el.selector || element} not displayed within ${timeout}ms`;
    await el.waitForDisplayed({ timeout, timeoutMsg: msg });
    return el;
  }

  /**
   * Wait for element to be clickable
   */
  async waitForClickable(element, timeout = envConfig.timeouts.explicitWait, customMessage = '') {
    const el = typeof element === 'string' ? await browser.$(element) : element;
    const msg = customMessage || `Element ${el.selector || element} not clickable within ${timeout}ms`;
    await el.waitForClickable({ timeout, timeoutMsg: msg });
    return el;
  }

  /**
   * Wait for element to be hidden/invisible
   */
  async waitForInvisibility(element, timeout = envConfig.timeouts.explicitWait) {
    const el = typeof element === 'string' ? await browser.$(element) : element;
    await el.waitForDisplayed({ timeout, reverse: true, timeoutMsg: `Element still visible after ${timeout}ms` });
    return true;
  }

  /**
   * Wait for element to contain specific text
   */
  async waitForText(element, expectedText, timeout = envConfig.timeouts.explicitWait) {
    const el = typeof element === 'string' ? await browser.$(element) : element;
    await browser.waitUntil(
      async () => {
        const text = await el.getText();
        return text && text.includes(expectedText);
      },
      {
        timeout,
        timeoutMsg: `Element text did not contain '${expectedText}' within ${timeout}ms`,
      }
    );
    return true;
  }

  /**
   * Wait for specific Android activity to be current
   */
  async waitForActivity(targetActivity, timeout = envConfig.timeouts.explicitWait) {
    logger.info(`Waiting for Android Activity: ${targetActivity}`);
    await browser.waitUntil(
      async () => {
        const currentActivity = await browser.getCurrentActivity();
        return currentActivity && currentActivity.includes(targetActivity);
      },
      {
        timeout,
        timeoutMsg: `Expected activity '${targetActivity}' was not launched within ${timeout}ms`,
      }
    );
    return true;
  }

  /**
   * Custom pause wrapper
   */
  async sleep(ms) {
    await browser.pause(ms);
  }
}

module.exports = new WaitUtil();
