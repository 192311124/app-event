const BasePage = require('./base.page');
const logger = require('../utilities/logger');

class FormPage extends BasePage {
  get formTitle() { return '//*[@text="Registration Form" or @resource-id="com.example.rent:id/tvFormTitle"]'; }
  get fullNameInput() { return '//*[@resource-id="com.example.rent:id/etFullName"]'; }
  get emailInput() { return '//*[@resource-id="com.example.rent:id/etEmail"]'; }
  get phoneInput() { return '//*[@resource-id="com.example.rent:id/etPhone"]'; }
  get passwordInput() { return '//*[@resource-id="com.example.rent:id/etPassword"]'; }
  get confirmPasswordInput() { return '//*[@resource-id="com.example.rent:id/etConfirmPassword"]'; }
  get datePicker() { return '//*[@resource-id="com.example.rent:id/etDateOfBirth"]'; }
  get categoryDropdown() { return '//*[@resource-id="com.example.rent:id/spinnerCategory"]'; }
  get termsCheckbox() { return '//*[@resource-id="com.example.rent:id/cbTerms"]'; }
  get maleRadioButton() { return '//*[@resource-id="com.example.rent:id/rbMale"]'; }
  get femaleRadioButton() { return '//*[@resource-id="com.example.rent:id/rbFemale"]'; }
  get submitButton() { return '//*[@resource-id="com.example.rent:id/btnSubmit"]'; }

  // Validation Error Messages
  get nameValidationError() { return '//*[@resource-id="com.example.rent:id/tvNameError"]'; }
  get emailValidationError() { return '//*[@resource-id="com.example.rent:id/tvEmailError"]'; }
  get phoneValidationError() { return '//*[@resource-id="com.example.rent:id/tvPhoneError"]'; }
  get passwordValidationError() { return '//*[@resource-id="com.example.rent:id/tvPasswordError"]'; }
  get termsValidationError() { return '//*[@resource-id="com.example.rent:id/tvTermsError"]'; }

  async fillForm(data = {}) {
    logger.info('Filling out form details');
    if (data.fullName !== undefined) await this.setValue(this.fullNameInput, data.fullName, 'Full Name');
    if (data.email !== undefined) await this.setValue(this.emailInput, data.email, 'Email');
    if (data.phone !== undefined) await this.setValue(this.phoneInput, data.phone, 'Phone');
    if (data.password !== undefined) await this.setValue(this.passwordInput, data.password, 'Password');
    if (data.confirmPassword !== undefined) await this.setValue(this.confirmPasswordInput, data.confirmPassword, 'Confirm Password');

    if (data.gender === 'Male') {
      await this.click(this.maleRadioButton, 'Male Radio Button');
    } else if (data.gender === 'Female') {
      await this.click(this.femaleRadioButton, 'Female Radio Button');
    }

    if (data.acceptTerms) {
      const isChecked = await browser.$(this.termsCheckbox).getAttribute('checked');
      if (isChecked !== 'true') {
        await this.click(this.termsCheckbox, 'Terms & Conditions Checkbox');
      }
    }
  }

  async selectCategory(categoryText) {
    logger.info(`Selecting dropdown category: ${categoryText}`);
    await this.click(this.categoryDropdown, 'Category Dropdown');
    const optionSelector = `//*[@text="${categoryText}"]`;
    await this.click(optionSelector, `Dropdown Option: ${categoryText}`);
  }

  async submitForm() {
    logger.info('Submitting Form');
    await this.click(this.submitButton, 'Submit Form Button');
  }

  async getEmailErrorMessage() {
    return await this.getText(this.emailValidationError);
  }

  async getPhoneErrorMessage() {
    return await this.getText(this.phoneValidationError);
  }

  async getPasswordErrorMessage() {
    return await this.getText(this.passwordValidationError);
  }
}

module.exports = new FormPage();
