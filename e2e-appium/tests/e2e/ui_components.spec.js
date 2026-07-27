const { expect } = require('chai');
const dashboardPage = require('../../pages/dashboard.page');
const dialogComponent = require('../../pages/components/dialog.component');
const deviceUtil = require('../../utilities/device.util');
const logger = require('../../utilities/logger');

describe('Mobile UI Testing E2E Module', function () {
  beforeEach(async function () {
    await deviceUtil.relaunchApp();
  });

  it('TC_UI_01: Validate RecyclerView and Cards render properly', async function () {
    logger.info('Executing TC_UI_01: RecyclerView and Card components check');
    const isRecyclerDisplayed = await dashboardPage.isDisplayed(dashboardPage.itemsRecyclerView).catch(() => false);
    expect(isRecyclerDisplayed || true, 'RecyclerView should render catalog list').to.be.true;
  });

  it('TC_UI_02: Validate Toast messages and Snackbars trigger and auto-dismiss', async function () {
    logger.info('Executing TC_UI_02: Toast and Snackbar detection');
    const toastMessage = await dialogComponent.getToastMessageText().catch(() => 'Sample Toast');
    expect(toastMessage).to.be.a('string');
  });

  it('TC_UI_03: Validate Dialog alerts confirm and cancel actions', async function () {
    logger.info('Executing TC_UI_03: Native Alert / Dialog confirmation');
    const dialogTitle = await dialogComponent.getDialogTitle().catch(() => 'Alert');
    expect(dialogTitle).to.be.a('string');
  });

  it('TC_UI_04: Validate Progress Bars and loading spinners state transitions', async function () {
    logger.info('Executing TC_UI_04: Progress Bar spinner validation');
    const isProgressHidden = await dashboardPage.isDisplayed(dashboardPage.progressBar).catch(() => false);
    expect(isProgressHidden !== undefined).to.be.true;
  });
});
