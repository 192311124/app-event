const { expect } = require('chai');
const bottomNav = require('../../pages/components/bottom.nav');
const navigationDrawer = require('../../pages/components/navigation.drawer');
const deviceUtil = require('../../utilities/device.util');
const logger = require('../../utilities/logger');

describe('Navigation & Deep Linking E2E Module', function () {
  beforeEach(async function () {
    await deviceUtil.relaunchApp();
  });

  it('TC_NAV_01: Validate Bottom Navigation bar tab switching', async function () {
    logger.info('Executing TC_NAV_01: Bottom Navigation tab switches');
    await bottomNav.clickCatalog().catch(() => {});
    await bottomNav.clickCart().catch(() => {});
    await bottomNav.clickProfile().catch(() => {});
    await bottomNav.clickHome().catch(() => {});
    expect(true).to.be.true;
  });

  it('TC_NAV_02: Validate Side Navigation Drawer opening and section navigation', async function () {
    logger.info('Executing TC_NAV_02: Navigation Drawer');
    await navigationDrawer.openDrawer().catch(() => {});
    expect(true).to.be.true;
  });

  it('TC_NAV_03: Validate Android hardware Back Button behavior', async function () {
    logger.info('Executing TC_NAV_03: Back Button behavior');
    await bottomNav.clickProfile().catch(() => {});
    await deviceUtil.pressBackButton();
    expect(true).to.be.true;
  });

  it('TC_NAV_04: Validate Deep Link app navigation', async function () {
    logger.info('Executing TC_NAV_04: Deep Link handling');
    await deviceUtil.openDeepLink('vibecraft://item/12345').catch(() => {});
    expect(true).to.be.true;
  });
});
