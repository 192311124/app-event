const fs = require('fs');
const path = require('path');
const logger = require('./logger');
const excelReporter = require('./excel.reporter');

class LoadTester {
  constructor(config = {}) {
    this.virtualUsers = config.virtualUsers || 100;
    this.durationSeconds = config.durationSeconds || 60;
    this.targetEndpoints = config.targetEndpoints || [
      'https://httpbin.org/get',
      'https://httpbin.org/delay/0',
      'https://httpbin.org/status/200',
    ];
    this.results = [];
    this.startTime = 0;
    this.endTime = 0;
  }

  async runBaselineLoadTest() {
    logger.info(`================================================================================`);
    logger.info(`STARTING BASELINE LOAD TEST: ${this.virtualUsers} VUs | Duration: ${this.durationSeconds}s`);
    logger.info(`Target Endpoints: ${this.targetEndpoints.join(', ')}`);
    logger.info(`================================================================================`);

    this.startTime = Date.now();
    const durationMs = this.durationSeconds * 1000;
    const deadline = this.startTime + durationMs;

    const workerTasks = [];
    for (let vu = 1; vu <= this.virtualUsers; vu++) {
      workerTasks.push(this.virtualUserLoop(vu, deadline));
    }

    await Promise.all(workerTasks);
    this.endTime = Date.now();

    const summary = this.calculateMetrics();
    this.printSummaryConsole(summary);
    await this.exportMetricsToExcel(summary);
    return summary;
  }

  async virtualUserLoop(vuId, deadline) {
    let reqIndex = 0;
    while (Date.now() < deadline) {
      const endpoint = this.targetEndpoints[reqIndex % this.targetEndpoints.length];
      reqIndex++;

      const reqStart = Date.now();
      try {
        const response = await fetch(endpoint, {
          method: 'GET',
          headers: {
            'User-Agent': 'Enterprise-Mobile-E2E-LoadTester/1.0',
            'X-Virtual-User': `VU_${vuId}`,
          },
        }).catch(() => null);

        const latencyMs = Date.now() - reqStart;
        const status = response ? response.status : 500;
        const isSuccess = status >= 200 && status < 400;

        this.results.push({
          vuId,
          endpoint,
          latencyMs,
          status,
          isSuccess,
          timestamp: new Date().toISOString(),
        });
      } catch (err) {
        const latencyMs = Date.now() - reqStart;
        this.results.push({
          vuId,
          endpoint,
          latencyMs,
          status: 500,
          isSuccess: false,
          timestamp: new Date().toISOString(),
        });
      }

      // Small pacing pause between requests (10ms) to allow network socket reuse
      await new Promise(resolve => setTimeout(resolve, 10));
    }
  }

  calculateMetrics() {
    const totalRequests = this.results.length;
    const actualDurationSec = (this.endTime - this.startTime) / 1000 || 1;
    const rps = (totalRequests / actualDurationSec).toFixed(2);

    const latencies = this.results.map(r => r.latencyMs).sort((a, b) => a - b);
    const passedCount = this.results.filter(r => r.isSuccess).length;
    const failedCount = totalRequests - passedCount;
    const successRate = totalRequests > 0 ? ((passedCount / totalRequests) * 100).toFixed(2) + '%' : '0%';

    const min = latencies.length > 0 ? latencies[0] : 0;
    const max = latencies.length > 0 ? latencies[latencies.length - 1] : 0;
    const avg = latencies.length > 0 ? Math.round(latencies.reduce((a, b) => a + b, 0) / latencies.length) : 0;
    const p95 = latencies.length > 0 ? latencies[Math.floor(latencies.length * 0.95)] || max : 0;
    const p99 = latencies.length > 0 ? latencies[Math.floor(latencies.length * 0.99)] || max : 0;

    return {
      virtualUsers: this.virtualUsers,
      durationSeconds: actualDurationSec.toFixed(2),
      totalRequests,
      rps: parseFloat(rps),
      passedCount,
      failedCount,
      successRate,
      responseTimes: {
        min,
        avg,
        max,
        p95,
        p99,
      },
      endpointBreakdown: this.getPerEndpointBreakdown(),
    };
  }

  getPerEndpointBreakdown() {
    const map = {};
    this.results.forEach(r => {
      if (!map[r.endpoint]) {
        map[r.endpoint] = { endpoint: r.endpoint, count: 0, totalLatency: 0, min: Infinity, max: 0, passed: 0, failed: 0 };
      }
      const entry = map[r.endpoint];
      entry.count++;
      entry.totalLatency += r.latencyMs;
      if (r.latencyMs < entry.min) entry.min = r.latencyMs;
      if (r.latencyMs > entry.max) entry.max = r.latencyMs;
      if (r.isSuccess) entry.passed++; else entry.failed++;
    });

    return Object.values(map).map(e => ({
      endpoint: e.endpoint,
      count: e.count,
      avgMs: Math.round(e.totalLatency / e.count),
      minMs: e.min === Infinity ? 0 : e.min,
      maxMs: e.max,
      successRate: ((e.passed / e.count) * 100).toFixed(2) + '%',
    }));
  }

  printSummaryConsole(s) {
    console.log(`
================================================================================
                BASELINE LOAD TEST METRICS & PERFORMANCE SUMMARY
================================================================================
Virtual Users (VUs):     ${s.virtualUsers}
Continuous Duration:     ${s.durationSeconds}s
Total Requests Sent:     ${s.totalRequests.toLocaleString()}
Requests Per Second (RPS): ${s.rps} req/sec
Success Rate:            ${s.successRate} (${s.passedCount.toLocaleString()} Passed, ${s.failedCount.toLocaleString()} Failed)
--------------------------------------------------------------------------------
RESPONSE TIME METRICS (Latency):
• Fastest (Min):         ${s.responseTimes.min} ms
• Average (Avg):         ${s.responseTimes.avg} ms
• Slowest (Max):         ${s.responseTimes.max} ms (${(s.responseTimes.max / 1000).toFixed(2)}s)
• 95th Percentile (P95): ${s.responseTimes.p95} ms
• 99th Percentile (P99): ${s.responseTimes.p99} ms
================================================================================
`);
  }

  async exportMetricsToExcel(summary) {
    // Add load test summary results to Excel reporter
    const now = Date.now();
    excelReporter.addTestCase(
      'TC_LOAD_BASELINE_001',
      'Performance Baseline Load Test',
      `100 VUs Load Test - RPS: ${summary.rps} req/sec | Avg Latency: ${summary.responseTimes.avg}ms`,
      summary.failedCount === 0 || parseFloat(summary.successRate) > 95 ? 'PASSED' : 'FAILED',
      this.startTime,
      this.endTime
    );

    // Save standalone JSON summary for CI pipeline consumption
    const jsonPath = path.resolve(__dirname, '../reports/load_test_summary.json');
    const reportsDir = path.dirname(jsonPath);
    if (!fs.existsSync(reportsDir)) {
      fs.mkdirSync(reportsDir, { recursive: true });
    }
    fs.writeFileSync(jsonPath, JSON.stringify(summary, null, 2));
    logger.info(`Saved Load Test JSON summary to: ${jsonPath}`);
  }
}

module.exports = LoadTester;
