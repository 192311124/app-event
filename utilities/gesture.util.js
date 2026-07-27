const logger = require('./logger');

class GestureUtil {
  async getWindowRectSafely() {
    try {
      if (typeof browser.getWindowSize === 'function') {
        return await browser.getWindowSize();
      }
    } catch (e) {}
    return { width: 1080, height: 1920 };
  }

  async tap(elementOrCoords) {
    if (typeof elementOrCoords === 'object' && elementOrCoords.x && elementOrCoords.y) {
      const { x, y } = elementOrCoords;
      logger.info(`Tapping at coordinates (${x}, ${y})`);
      if (typeof browser.action === 'function') {
        await browser.action('pointer', { parameters: { pointerType: 'touch' } })
          .move({ x, y })
          .down({ button: 0 })
          .pause(50)
          .up({ button: 0 })
          .perform().catch(() => {});
      }
    } else {
      const el = typeof elementOrCoords === 'string' ? await browser.$(elementOrCoords) : elementOrCoords;
      await el.click().catch(() => {});
    }
  }

  async doubleTap(element) {
    const el = typeof element === 'string' ? await browser.$(element) : element;
    const location = await el.getLocation().catch(() => ({ x: 100, y: 100 }));
    const size = await el.getSize().catch(() => ({ width: 100, height: 50 }));
    const x = Math.round(location.x + size.width / 2);
    const y = Math.round(location.y + size.height / 2);

    logger.info(`Double tapping element at (${x}, ${y})`);
    if (typeof browser.action === 'function') {
      await browser.action('pointer', { parameters: { pointerType: 'touch' } })
        .move({ x, y })
        .down({ button: 0 })
        .pause(50)
        .up({ button: 0 })
        .pause(100)
        .down({ button: 0 })
        .pause(50)
        .up({ button: 0 })
        .perform().catch(() => {});
    }
  }

  async longPress(element, durationMs = 2000) {
    const el = typeof element === 'string' ? await browser.$(element) : element;
    const location = await el.getLocation().catch(() => ({ x: 100, y: 100 }));
    const size = await el.getSize().catch(() => ({ width: 100, height: 50 }));
    const x = Math.round(location.x + size.width / 2);
    const y = Math.round(location.y + size.height / 2);

    logger.info(`Long pressing element at (${x}, ${y}) for ${durationMs}ms`);
    if (typeof browser.action === 'function') {
      await browser.action('pointer', { parameters: { pointerType: 'touch' } })
        .move({ x, y })
        .down({ button: 0 })
        .pause(durationMs)
        .up({ button: 0 })
        .perform().catch(() => {});
    }
  }

  async swipe(direction, percent = 0.75, speedMs = 800) {
    const { width, height } = await this.getWindowRectSafely();
    let startX, startY, endX, endY;

    switch (direction.toLowerCase()) {
      case 'up':
        startX = width / 2;
        startY = height * (0.5 + percent / 2);
        endX = width / 2;
        endY = height * (0.5 - percent / 2);
        break;
      case 'down':
        startX = width / 2;
        startY = height * (0.5 - percent / 2);
        endX = width / 2;
        endY = height * (0.5 + percent / 2);
        break;
      case 'left':
        startX = width * (0.5 + percent / 2);
        startY = height / 2;
        endX = width * (0.5 - percent / 2);
        endY = height / 2;
        break;
      case 'right':
        startX = width * (0.5 - percent / 2);
        startY = height / 2;
        endX = width * (0.5 + percent / 2);
        endY = height / 2;
        break;
      default:
        throw new Error(`Unsupported swipe direction: ${direction}`);
    }

    logger.info(`Swiping ${direction} from (${startX}, ${startY}) to (${endX}, ${endY})`);
    if (typeof browser.action === 'function') {
      await browser.action('pointer', { parameters: { pointerType: 'touch' } })
        .move({ x: Math.round(startX), y: Math.round(startY) })
        .down({ button: 0 })
        .pause(100)
        .move({ duration: speedMs, x: Math.round(endX), y: Math.round(endY) })
        .up({ button: 0 })
        .perform().catch(() => {});
    }
  }

  async swipeUp(percent = 0.6) {
    await this.swipe('up', percent);
  }

  async swipeDown(percent = 0.6) {
    await this.swipe('down', percent);
  }

  async swipeLeft(percent = 0.6) {
    await this.swipe('left', percent);
  }

  async swipeRight(percent = 0.6) {
    await this.swipe('right', percent);
  }

  async scrollUntilVisible(selector, maxScrolls = 10, direction = 'down') {
    logger.info(`Scrolling ${direction} until element '${selector}' is visible`);
    let isDisplayed = false;

    for (let i = 0; i < maxScrolls; i++) {
      const el = await browser.$(selector).catch(() => null);
      if (el) {
        isDisplayed = await el.isDisplayed().catch(() => false);
        if (isDisplayed) {
          logger.info(`Found element '${selector}' after ${i} scrolls.`);
          return el;
        }
      }
      await this.swipe(direction, 0.5);
      await browser.pause(500);
    }

    return null;
  }

  async dragAndDrop(sourceElement, targetElement) {
    const src = typeof sourceElement === 'string' ? await browser.$(sourceElement) : sourceElement;
    const tgt = typeof targetElement === 'string' ? await browser.$(targetElement) : targetElement;

    const srcLoc = await src.getLocation().catch(() => ({ x: 100, y: 100 }));
    const srcSize = await src.getSize().catch(() => ({ width: 100, height: 50 }));
    const tgtLoc = await tgt.getLocation().catch(() => ({ x: 300, y: 300 }));
    const tgtSize = await tgt.getSize().catch(() => ({ width: 100, height: 50 }));

    const startX = Math.round(srcLoc.x + srcSize.width / 2);
    const startY = Math.round(srcLoc.y + srcSize.height / 2);
    const endX = Math.round(tgtLoc.x + tgtSize.width / 2);
    const endY = Math.round(tgtLoc.y + tgtSize.height / 2);

    logger.info(`Drag and drop from (${startX}, ${startY}) to (${endX}, ${endY})`);
    if (typeof browser.action === 'function') {
      await browser.action('pointer', { parameters: { pointerType: 'touch' } })
        .move({ x: startX, y: startY })
        .down({ button: 0 })
        .pause(300)
        .move({ duration: 1000, x: endX, y: endY })
        .pause(200)
        .up({ button: 0 })
        .perform().catch(() => {});
    }
  }

  async pinch(scale = 0.5) {
    logger.info(`Executing Pinch gesture (scale: ${scale})`);
    const { width, height } = await this.getWindowRectSafely();
    const centerX = width / 2;
    const centerY = height / 2;
    const distance = Math.min(width, height) * 0.4;

    const finger1Start = { x: centerX - distance, y: centerY - distance };
    const finger2Start = { x: centerX + distance, y: centerY + distance };
    const finger1End = { x: centerX - distance * scale, y: centerY - distance * scale };
    const finger2End = { x: centerX + distance * scale, y: centerY + distance * scale };

    if (typeof browser.performActions === 'function') {
      await browser.performActions([
        {
          type: 'pointer',
          id: 'finger1',
          parameters: { pointerType: 'touch' },
          actions: [
            { type: 'pointerMove', duration: 0, x: Math.round(finger1Start.x), y: Math.round(finger1Start.y) },
            { type: 'pointerDown', button: 0 },
            { type: 'pause', duration: 100 },
            { type: 'pointerMove', duration: 800, x: Math.round(finger1End.x), y: Math.round(finger1End.y) },
            { type: 'pointerUp', button: 0 },
          ],
        },
        {
          type: 'pointer',
          id: 'finger2',
          parameters: { pointerType: 'touch' },
          actions: [
            { type: 'pointerMove', duration: 0, x: Math.round(finger2Start.x), y: Math.round(finger2Start.y) },
            { type: 'pointerDown', button: 0 },
            { type: 'pause', duration: 100 },
            { type: 'pointerMove', duration: 800, x: Math.round(finger2End.x), y: Math.round(finger2End.y) },
            { type: 'pointerUp', button: 0 },
          ],
        },
      ]).catch(() => {});
    }
  }

  async zoom(scale = 1.5) {
    logger.info(`Executing Zoom gesture (scale: ${scale})`);
    const { width, height } = await this.getWindowRectSafely();
    const centerX = width / 2;
    const centerY = height / 2;
    const startDistance = 50;
    const endDistance = Math.min(width, height) * 0.3 * scale;

    const finger1Start = { x: centerX - startDistance, y: centerY - startDistance };
    const finger2Start = { x: centerX + startDistance, y: centerY + startDistance };
    const finger1End = { x: centerX - endDistance, y: centerY - endDistance };
    const finger2End = { x: centerX + endDistance, y: centerY + endDistance };

    if (typeof browser.performActions === 'function') {
      await browser.performActions([
        {
          type: 'pointer',
          id: 'finger1',
          parameters: { pointerType: 'touch' },
          actions: [
            { type: 'pointerMove', duration: 0, x: Math.round(finger1Start.x), y: Math.round(finger1Start.y) },
            { type: 'pointerDown', button: 0 },
            { type: 'pause', duration: 100 },
            { type: 'pointerMove', duration: 800, x: Math.round(finger1End.x), y: Math.round(finger1End.y) },
            { type: 'pointerUp', button: 0 },
          ],
        },
        {
          type: 'pointer',
          id: 'finger2',
          parameters: { pointerType: 'touch' },
          actions: [
            { type: 'pointerMove', duration: 0, x: Math.round(finger2Start.x), y: Math.round(finger2Start.y) },
            { type: 'pointerDown', button: 0 },
            { type: 'pause', duration: 100 },
            { type: 'pointerMove', duration: 800, x: Math.round(finger2End.x), y: Math.round(finger2End.y) },
            { type: 'pointerUp', button: 0 },
          ],
        },
      ]).catch(() => {});
    }
  }
}

module.exports = new GestureUtil();
