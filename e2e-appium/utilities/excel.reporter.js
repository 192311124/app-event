const ExcelJS = require('exceljs');
const path = require('path');
const fs = require('fs');
const logger = require('./logger');
const envConfig = require('../config/env.config');

class ExcelReporter {
  constructor() {
    this.excelDir = path.resolve(__dirname, '../excel');
    if (!fs.existsSync(this.excelDir)) {
      fs.mkdirSync(this.excelDir, { recursive: true });
    }
    this.reportPath = path.join(this.excelDir, 'Mobile_E2E_Report.xlsx');

    this.summaryData = {
      executionDate: new Date().toISOString().replace('T', ' ').substring(0, 19),
      deviceName: envConfig.device.deviceName,
      androidVersion: envConfig.device.platformVersion,
      totalTests: 0,
      passed: 0,
      failed: 0,
      skipped: 0,
      passPercentage: '0%',
      duration: '0s',
    };

    this.testCases = [];
    this.failedTests = [];
  }

  /**
   * Record a test case result
   */
  addTestCase(testId, moduleName, scenario, status, startTime, endTime, failureDetails = null) {
    const durationMs = endTime - startTime;
    const durationStr = `${(durationMs / 1000).toFixed(2)}s`;
    
    const record = {
      testId,
      module: moduleName,
      scenario,
      device: this.summaryData.deviceName,
      status, // 'PASSED' | 'FAILED' | 'SKIPPED'
      startTime: new Date(startTime).toISOString().substring(11, 19),
      endTime: new Date(endTime).toISOString().substring(11, 19),
      duration: durationStr,
    };

    this.testCases.push(record);
    this.summaryData.totalTests++;

    if (status === 'PASSED') {
      this.summaryData.passed++;
    } else if (status === 'FAILED') {
      this.summaryData.failed++;
      if (failureDetails) {
        this.failedTests.push({
          testName: `${moduleName} - ${scenario}`,
          failureReason: failureDetails.reason || 'Assertion/Timeout Error',
          screenshotPath: failureDetails.screenshotPath || 'N/A',
          device: this.summaryData.deviceName,
          androidVersion: this.summaryData.androidVersion,
          activityName: failureDetails.activityName || 'MainActivity',
        });
      }
    } else {
      this.summaryData.skipped++;
    }
  }

  /**
   * Finalize metrics and generate Excel workbook
   */
  async generateReport() {
    logger.info(`Generating 4-Sheet Excel Report at: ${this.reportPath}`);

    const total = this.summaryData.totalTests;
    const passCount = this.summaryData.passed;
    this.summaryData.passPercentage = total > 0 ? `${((passCount / total) * 100).toFixed(2)}%` : '0%';

    const workbook = new ExcelJS.Workbook();
    workbook.creator = 'Mobile QA Automation Architect';
    workbook.created = new Date();

    // -------------------------------------------------------------
    // SHEET 1 - SUMMARY
    // -------------------------------------------------------------
    const sheet1 = workbook.addWorksheet('Summary', { views: [{ showGridLines: true }] });
    sheet1.columns = [
      { header: 'Execution Date', key: 'executionDate', width: 22 },
      { header: 'Device Name', key: 'deviceName', width: 22 },
      { header: 'Android Version', key: 'androidVersion', width: 18 },
      { header: 'Total Tests', key: 'totalTests', width: 15 },
      { header: 'Passed', key: 'passed', width: 12 },
      { header: 'Failed', key: 'failed', width: 12 },
      { header: 'Skipped', key: 'skipped', width: 12 },
      { header: 'Pass Percentage', key: 'passPercentage', width: 18 },
      { header: 'Execution Duration', key: 'duration', width: 20 },
    ];

    sheet1.addRow(this.summaryData);
    this.applyHeaderStyle(sheet1);
    this.applyDataRowStyle(sheet1);

    // -------------------------------------------------------------
    // SHEET 2 - TEST CASES
    // -------------------------------------------------------------
    const sheet2 = workbook.addWorksheet('Test Cases', { views: [{ showGridLines: true }] });
    sheet2.columns = [
      { header: 'Test ID', key: 'testId', width: 18 },
      { header: 'Module', key: 'module', width: 20 },
      { header: 'Scenario', key: 'scenario', width: 35 },
      { header: 'Device', key: 'device', width: 22 },
      { header: 'Status', key: 'status', width: 15 },
      { header: 'Start Time', key: 'startTime', width: 15 },
      { header: 'End Time', key: 'endTime', width: 15 },
      { header: 'Duration', key: 'duration', width: 15 },
    ];

    this.testCases.forEach(tc => {
      const row = sheet2.addRow(tc);
      const statusCell = row.getCell('status');
      if (tc.status === 'PASSED') {
        statusCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'C6EFCE' } };
        statusCell.font = { color: { argb: '006100' }, bold: true };
      } else if (tc.status === 'FAILED') {
        statusCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFC7CE' } };
        statusCell.font = { color: { argb: '9C0006' }, bold: true };
      }
    });
    this.applyHeaderStyle(sheet2);

    // -------------------------------------------------------------
    // SHEET 3 - FAILED TESTS
    // -------------------------------------------------------------
    const sheet3 = workbook.addWorksheet('Failed Tests', { views: [{ showGridLines: true }] });
    sheet3.columns = [
      { header: 'Test Name', key: 'testName', width: 35 },
      { header: 'Failure Reason', key: 'failureReason', width: 45 },
      { header: 'Screenshot Path', key: 'screenshotPath', width: 45 },
      { header: 'Device', key: 'device', width: 20 },
      { header: 'Android Version', key: 'androidVersion', width: 18 },
      { header: 'Activity Name', key: 'activityName', width: 30 },
    ];

    this.failedTests.forEach(ft => sheet3.addRow(ft));
    this.applyHeaderStyle(sheet3);

    // -------------------------------------------------------------
    // SHEET 4 - EXECUTION LOGS
    // -------------------------------------------------------------
    const sheet4 = workbook.addWorksheet('Execution Logs', { views: [{ showGridLines: true }] });
    sheet4.columns = [
      { header: 'Timestamp', key: 'timestamp', width: 22 },
      { header: 'Test Name', key: 'testName', width: 30 },
      { header: 'Step', key: 'step', width: 45 },
      { header: 'Result', key: 'result', width: 15 },
      { header: 'Remarks', key: 'remarks', width: 35 },
    ];

    const capturedLogs = logger.getExecutionLogs();
    capturedLogs.forEach(l => sheet4.addRow(l));
    this.applyHeaderStyle(sheet4);

    await workbook.xlsx.writeFile(this.reportPath);
    logger.info(`Successfully written Mobile_E2E_Report.xlsx to ${this.reportPath}`);
    return this.reportPath;
  }

  applyHeaderStyle(sheet) {
    const headerRow = sheet.getRow(1);
    headerRow.font = { bold: true, color: { argb: 'FFFFFF' }, size: 11 };
    headerRow.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: '1F4E78' } };
    headerRow.alignment = { vertical: 'middle', horizontal: 'center' };
  }

  applyDataRowStyle(sheet) {
    sheet.eachRow((row, rowNumber) => {
      if (rowNumber > 1) {
        row.alignment = { vertical: 'middle', horizontal: 'left' };
      }
    });
  }
}

module.exports = new ExcelReporter();
