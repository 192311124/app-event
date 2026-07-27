const BasePage = require('../base.page');
const logger = require('../../utilities/logger');

class DialogComponent extends BasePage {
  get dialogTitle() { return '//*[@resource-id="android:id/alertTitle" or @resource-id="com.example.rent:id/dialog_title"]'; }
  get dialogMessage() { return '//*[@resource-id="android:id/message" or @resource-id="com.example.rent:id/dialog_message"]'; }
  get confirmButton() { return '//*[@resource-id="android:id/button1" or @text="OK" or @text="Confirm"]'; }
  get cancelButton() { return '//*[@resource-id="android:id/button2" or @text="Cancel"]'; }

  get toastMessage() { return '//android.widget.Toast'; }
  get snackbarText() { return '//*[@resource-id="com.google.android.material:id/snackbar_text"]'; }

  async getDialogTitle() {
    return await this.getText(this.dialogTitle);
  }

  async getDialogMessage() {
    return await this.getText(this.dialogMessage);
  }

  async confirmDialog() {
    logger.info('Confirming dialog popup');
    await this.click(this.confirmButton, 'Dialog Confirm Button');
  }

  async cancelDialog() {
    logger.info('Canceling dialog popup');
    await this.click(this.cancelButton, 'Dialog Cancel Button');
  }

  async getToastMessageText() {
    logger.info('Retrieving Toast message text');
    const toast = await browser.$(this.toastMessage);
    await toast.waitForDisplayed({ timeout: 5000 });
    return await toast.getText();
  }

  async getSnackbarText() {
    logger.info('Retrieving Snackbar message text');
    return await this.getText(this.snackbarText);
  }
}

module.exports = new DialogComponent();
