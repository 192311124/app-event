const waitUtil = require('../utilities/wait.util');
const gestureUtil = require('../utilities/gesture.util');
const logger = require('../utilities/logger');

class BasePage {
  /**
   * Find element with explicit wait
   */
  async findElement(selector, timeout = 15000) {
    const el = await browser.$(selector);
    await waitUtil.waitForDisplayed(el, timeout);
    return el;
  }

  /**
   * Click element
   */
  async click(selector, description = '') {
    logger.info(`Clicking: ${description || selector}`);
    const el = await this.findElement(selector);
    await waitUtil.waitForClickable(el);
    await el.click();
  }

  /**
   * Clear and enter text into an input field
   */
  async setValue(selector, value, description = '') {
    logger.info(`Setting value '${value}' in ${description || selector}`);
    const el = await this.findElement(selector);
    await el.clearValue();
    await el.setValue(value);
  }

  /**
   * Get text content from element
   */
  async getText(selector) {
    const el = await this.findElement(selector);
    return await el.getText();
  }

  /**
   * Check if element is displayed
   */
  async isDisplayed(selector) {
    try {
      const el = await browser.$(selector);
      return await el.isDisplayed();
    } catch (e) {
      return false;
    }
  }

  /**
   * Scroll down until element is displayed
   */
  async scrollToElement(selector) {
    return await gestureUtil.scrollUntilVisible(selector);
  }
}

module.exports = BasePage;
