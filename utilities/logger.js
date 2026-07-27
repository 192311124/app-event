const winston = require('winston');
const path = require('path');
const fs = require('fs');

const logDir = path.resolve(__dirname, '../logs');
if (!fs.existsSync(logDir)) {
  fs.mkdirSync(logDir, { recursive: true });
}

const executionLogsBuffer = [];

const customFormat = winston.format.combine(
  winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss.SSS' }),
  winston.format.printf(({ timestamp, level, message }) => {
    return `[${timestamp}] [${level.toUpperCase()}]: ${message}`;
  })
);

const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: customFormat,
  transports: [
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        customFormat
      ),
    }),
    new winston.transports.File({
      filename: path.join(logDir, 'error.log'),
      level: 'error',
    }),
    new winston.transports.File({
      filename: path.join(logDir, 'execution.log'),
    }),
  ],
});

logger.logStep = function (testName, stepDescription, result = 'INFO', remarks = '') {
  const timestamp = new Date().toISOString().replace('T', ' ').substring(0, 19);
  const logEntry = { timestamp, testName, step: stepDescription, result, remarks };
  executionLogsBuffer.push(logEntry);
  logger.info(`[${testName}] ${stepDescription} -> [${result}] ${remarks}`);
};

logger.getExecutionLogs = function () {
  return executionLogsBuffer;
};

logger.clearExecutionLogs = function () {
  executionLogsBuffer.length = 0;
};

module.exports = logger;
