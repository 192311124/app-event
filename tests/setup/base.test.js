const driverFactory = require('../../drivers/driver.factory');
const logger = require('../../utilities/logger');
const screenshotUtil = require('../../utilities/screenshot.util');
const deviceUtil = require('../../utilities/device.util');
const excelReporter = require('../../utilities/excel.reporter');

let testStartTime = 0;

before(async function () {
  this.timeout(180000);
  logger.info('=== Enterprise Mobile E2E Test Suite Starting ===');
  await driverFactory.createDriver();
  global.browser = driverFactory.getDriver();
});

beforeEach(async function () {
  testStartTime = Date.now();
  const testTitle = this.currentTest ? this.currentTest.fullTitle() : 'Test';
  logger.info(`>>> Starting Test: ${testTitle}`);
  logger.logStep(testTitle, 'Test Execution Initialized', 'PASSED', 'Session active');
});

afterEach(async function () {
  const test = this.currentTest;
  const testTitle = test ? test.fullTitle() : 'Test';
  const endTime = Date.now();
  const state = test ? test.state : 'unknown';

  if (state === 'failed') {
    logger.error(`<<< Test FAILED: ${testTitle}`);
    
    const screenshotPath = await screenshotUtil.captureFailureScreenshot(testTitle);
    const logcatPath = await deviceUtil.captureLogcatLogs(testTitle);
    const currentActivity = await deviceUtil.getCurrentActivity();

    const failureReason = test.err ? test.err.message : 'Unknown assertion error';
    excelReporter.addTestCase(
      `TC_${Math.floor(1000 + Math.random() * 9000)}`,
      test.parent ? test.parent.title : 'E2E Module',
      test.title,
      'FAILED',
      testStartTime,
      endTime,
      {
        reason: failureReason,
        screenshotPath: screenshotPath || 'N/A',
        activityName: currentActivity,
      }
    );

    logger.logStep(testTitle, `Test Failed: ${failureReason}`, 'FAILED', `Activity: ${currentActivity}, Screenshot: ${screenshotPath}, Logcat: ${logcatPath}`);
  } else if (state === 'passed') {
    logger.info(`<<< Test PASSED: ${testTitle}`);
    excelReporter.addTestCase(
      `TC_${Math.floor(1000 + Math.random() * 9000)}`,
      test.parent ? test.parent.title : 'E2E Module',
      test.title,
      'PASSED',
      testStartTime,
      endTime
    );
    logger.logStep(testTitle, 'Test Completed Successfully', 'PASSED', 'All assertions passed');
  } else {
    logger.warn(`<<< Test SKIPPED: ${testTitle}`);
    excelReporter.addTestCase(
      `TC_${Math.floor(1000 + Math.random() * 9000)}`,
      test.parent ? test.parent.title : 'E2E Module',
      test.title,
      'SKIPPED',
      testStartTime,
      endTime
    );
    logger.logStep(testTitle, 'Test Skipped', 'SKIPPED', 'Test skipped');
  }
});

after(async function () {
  this.timeout(60000);
  logger.info('=== Enterprise Mobile E2E Test Suite Teardown ===');
  await excelReporter.generateReport();
  await driverFactory.quitDriver();
});
