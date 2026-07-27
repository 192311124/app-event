const logger = require('./logger');

class RetryUtil {
  /**
   * Execute an async function with retry count and delay
   */
  async retry(fn, maxRetries = 3, delayMs = 1000, contextName = 'Operation') {
    let lastError = null;

    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        logger.info(`[${contextName}] Attempt ${attempt}/${maxRetries}`);
        return await fn();
      } catch (error) {
        lastError = error;
        logger.warn(`[${contextName}] Attempt ${attempt} failed: ${error.message}`);

        if (attempt < maxRetries) {
          logger.info(`Waiting ${delayMs}ms before retrying...`);
          await new Promise(resolve => setTimeout(resolve, delayMs));
        }
      }
    }

    logger.error(`[${contextName}] Exhausted all ${maxRetries} retry attempts.`);
    throw lastError;
  }
}

module.exports = new RetryUtil();
