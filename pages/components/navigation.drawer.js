const BasePage = require('../base.page');
const logger = require('../../utilities/logger');

class NavigationDrawer extends BasePage {
  get menuIcon() { return '~Open navigation drawer'; }
  get drawerContainer() { return '//*[@resource-id="com.example.rent:id/nav_view" or @resource-id="com.example.app:id/nav_view"]'; }
  get homeMenuItem() { return '//*[@text="Home" or @resource-id="com.example.rent:id/nav_home"]'; }
  get profileMenuItem() { return '//*[@text="Profile" or @resource-id="com.example.rent:id/nav_profile"]'; }
  get settingsMenuItem() { return '//*[@text="Settings" or @resource-id="com.example.rent:id/nav_settings"]'; }
  get logoutMenuItem() { return '//*[@text="Logout" or @resource-id="com.example.rent:id/nav_logout"]'; }

  async openDrawer() {
    logger.info('Opening Side Navigation Drawer');
    await this.click(this.menuIcon, 'Side Drawer Hamburger Icon');
  }

  async navigateTo(menuItemName) {
    await this.openDrawer();
    const selector = `//*[@text="${menuItemName}"]`;
    await this.click(selector, `Drawer item: ${menuItemName}`);
  }
}

module.exports = new NavigationDrawer();
