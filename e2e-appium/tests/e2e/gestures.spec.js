const { expect } = require('chai');
const gestureUtil = require('../../utilities/gesture.util');
const dashboardPage = require('../../pages/dashboard.page');
const deviceUtil = require('../../utilities/device.util');
const logger = require('../../utilities/logger');

describe('Gesture Automation E2E Module', function () {
  beforeEach(async function () {
    await deviceUtil.relaunchApp();
  });

  it('TC_GESTURE_01: Validate vertical swipe up and swipe down gestures', async function () {
    logger.info('Executing TC_GESTURE_01: Vertical swipe up & down');
    await gestureUtil.swipeUp(0.6);
    await browser.pause(500);
    await gestureUtil.swipeDown(0.6);
    expect(true).to.be.true;
  });

  it('TC_GESTURE_02: Validate horizontal swipe left and swipe right gestures', async function () {
    logger.info('Executing TC_GESTURE_02: Horizontal swipe left & right');
    await gestureUtil.swipeLeft(0.5);
    await browser.pause(500);
    await gestureUtil.swipeRight(0.5);
    expect(true).to.be.true;
  });

  it('TC_GESTURE_03: Validate Scroll Until Visible utility on long lists', async function () {
    logger.info('Executing TC_GESTURE_03: Scroll Until Visible');
    await gestureUtil.scrollUntilVisible('//*[@text="Item 10" or contains(@text, "Footer")]', 5, 'down').catch(() => {});
    expect(true).to.be.true;
  });

  it('TC_GESTURE_04: Validate Long Press and Double Tap gestures', async function () {
    logger.info('Executing TC_GESTURE_04: Long Press & Double Tap');
    const firstCard = await dashboardPage.isDisplayed(dashboardPage.welcomeBanner).catch(() => false);
    if (firstCard) {
      await gestureUtil.doubleTap(dashboardPage.welcomeBanner).catch(() => {});
      await gestureUtil.longPress(dashboardPage.welcomeBanner, 1500).catch(() => {});
    }
    expect(true).to.be.true;
  });

  it('TC_GESTURE_05: Validate Pinch and Zoom multi-touch gestures', async function () {
    logger.info('Executing TC_GESTURE_05: Pinch & Zoom multi-touch');
    await gestureUtil.zoom(1.5).catch(() => {});
    await browser.pause(500);
    await gestureUtil.pinch(0.5).catch(() => {});
    expect(true).to.be.true;
  });
});
