const logger = require('./logger');
const envConfig = require('../config/env.config');

class PerformanceUtil {
  constructor() {
    this.metrics = [];
  }

  /**
   * Measure App Launch Time (from activation to initial render)
   */
  async measureAppLaunchTime() {
    logger.info('Measuring App Launch Time...');
    const startTime = Date.now();
    
    try {
      await browser.terminateApp(envConfig.app.package).catch(() => {});
      await browser.activateApp(envConfig.app.package);
      
      // Wait until root content decor or view container is loaded
      await browser.waitUntil(
        async () => {
          const state = await browser.getState();
          return state === 4; // 4 = Application running in foreground
        },
        { timeout: 15000, timeoutMsg: 'App launch state transition timed out' }
      );
      
      const launchDurationMs = Date.now() - startTime;
      logger.info(`App Launch Time: ${launchDurationMs} ms`);
      
      this.recordMetric('App Launch Time', launchDurationMs, 'ms');
      return launchDurationMs;
    } catch (error) {
      logger.error(`App Launch Time measurement failed: ${error.message}`);
      return -1;
    }
  }

  /**
   * Measure Screen Load Time for a target view/element
   */
  async measureScreenLoadTime(screenName, selectorTrigger, targetSelector) {
    logger.info(`Measuring Screen Load Time for: ${screenName}`);
    const startTime = Date.now();
    
    if (selectorTrigger) {
      const triggerEl = typeof selectorTrigger === 'string' ? await browser.$(selectorTrigger) : selectorTrigger;
      await triggerEl.click();
    }

    const targetEl = typeof targetSelector === 'string' ? await browser.$(targetSelector) : targetSelector;
    await targetEl.waitForDisplayed({ timeout: 15000 });

    const durationMs = Date.now() - startTime;
    logger.info(`Screen [${screenName}] Load Time: ${durationMs} ms`);
    
    this.recordMetric(`Screen Load: ${screenName}`, durationMs, 'ms');
    return durationMs;
  }

  /**
   * Track simulated API response delay or custom action duration
   */
  async measureAction(actionName, actionFn) {
    const startTime = Date.now();
    try {
      const result = await actionFn();
      const durationMs = Date.now() - startTime;
      logger.info(`Action [${actionName}] Duration: ${durationMs} ms`);
      this.recordMetric(`Action: ${actionName}`, durationMs, 'ms');
      return { result, durationMs };
    } catch (err) {
      const durationMs = Date.now() - startTime;
      this.recordMetric(`Action (Failed): ${actionName}`, durationMs, 'ms');
      throw err;
    }
  }

  recordMetric(metricName, val, unit = 'ms') {
    this.metrics.push({
      metric: metricName,
      value: val,
      unit,
      timestamp: new Date().toISOString(),
    });
  }

  getMetrics() {
    return this.metrics;
  }
}

module.exports = new PerformanceUtil();
