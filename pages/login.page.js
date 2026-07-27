const BasePage = require('./base.page');
const logger = require('../utilities/logger');

class LoginPage extends BasePage {
  get usernameInput() { return '//*[@resource-id="com.example.rent:id/etUsername" or @resource-id="com.example.rent:id/email_input" or @content-desc="Username Input"]'; }
  get passwordInput() { return '//*[@resource-id="com.example.rent:id/etPassword" or @resource-id="com.example.rent:id/password_input" or @content-desc="Password Input"]'; }
  get loginButton() { return '//*[@resource-id="com.example.rent:id/btnLogin" or @text="LOGIN" or @text="Sign In"]'; }
  get usernameErrorMsg() { return '//*[@resource-id="com.example.rent:id/tvUsernameError" or @text="Username is required"]'; }
  get passwordErrorMsg() { return '//*[@resource-id="com.example.rent:id/tvPasswordError" or @text="Password is required"]'; }
  get authErrorMessage() { return '//*[@resource-id="com.example.rent:id/tvError" or @resource-id="com.example.rent:id/error_banner"]'; }
  get rememberMeCheckbox() { return '//*[@resource-id="com.example.rent:id/cbRememberMe" or @text="Remember Me"]'; }

  async enterUsername(username) {
    await this.setValue(this.usernameInput, username, 'Username Field');
  }

  async enterPassword(password) {
    await this.setValue(this.passwordInput, password, 'Password Field');
  }

  async clickLogin() {
    await this.click(this.loginButton, 'Login Button');
  }

  async login(username, password) {
    logger.info(`Performing login with username: '${username}'`);
    if (username !== null && username !== undefined) {
      await this.enterUsername(username);
    }
    if (password !== null && password !== undefined) {
      await this.enterPassword(password);
    }
    await this.clickLogin();
  }

  async getUsernameError() {
    return await this.getText(this.usernameErrorMsg);
  }

  async getPasswordError() {
    return await this.getText(this.passwordErrorMsg);
  }

  async getAuthErrorMessage() {
    return await this.getText(this.authErrorMessage);
  }

  async toggleRememberMe() {
    await this.click(this.rememberMeCheckbox, 'Remember Me Checkbox');
  }
}

module.exports = new LoginPage();
