const { expect } = require('chai');
const LoadTester = require('../../utilities/load.tester');
const logger = require('../../utilities/logger');

describe('Baseline Performance Load Test Module (100 VUs, 1 Minute)', function () {
  this.timeout(180000); // 3-minute timeout for load test execution

  it('TC_LOAD_001: Execute 100 Virtual Users Baseline Load Test for 1 Minute', async function () {
    logger.info('Executing TC_LOAD_001: 100 VUs 1-Minute Baseline Load Test');

    const tester = new LoadTester({
      virtualUsers: 100,
      durationSeconds: 60,
      targetEndpoints: [
        'https://httpbin.org/get',
        'https://httpbin.org/delay/0',
        'https://httpbin.org/status/200',
      ],
    });

    const summary = await tester.runBaselineLoadTest();

    expect(summary).to.have.property('totalRequests');
    expect(summary.totalRequests).to.be.above(100, 'Thousands of requests should be processed during the 1-minute load test');
    expect(summary).to.have.property('rps');
    expect(summary.rps).to.be.above(0, 'RPS should be greater than 0 req/sec');
    expect(summary.responseTimes.min).to.be.a('number');
    expect(summary.responseTimes.avg).to.be.a('number');
    expect(summary.responseTimes.max).to.be.a('number');

    logger.info(`Baseline Load Test Completed:
    • Total Requests: ${summary.totalRequests.toLocaleString()}
    • RPS: ${summary.rps} req/sec
    • Avg Latency: ${summary.responseTimes.avg} ms
    • Min Latency: ${summary.responseTimes.min} ms
    • Max Latency: ${summary.responseTimes.max} ms`);
  });
});
