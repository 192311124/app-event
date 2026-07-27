const BasePage = require('../base.page');
const logger = require('../../utilities/logger');

class BottomNav extends BasePage {
  get homeTab() { return '//*[@content-desc="Home" or @resource-id="com.example.rent:id/navigation_home"]'; }
  get catalogTab() { return '//*[@content-desc="Catalog" or @resource-id="com.example.rent:id/navigation_catalog"]'; }
  get cartTab() { return '//*[@content-desc="Cart" or @resource-id="com.example.rent:id/navigation_cart"]'; }
  get profileTab() { return '//*[@content-desc="Profile" or @resource-id="com.example.rent:id/navigation_profile"]'; }

  async clickHome() {
    logger.info('Navigating to Home via Bottom Bar');
    await this.click(this.homeTab, 'Bottom Nav Home Tab');
  }

  async clickCatalog() {
    logger.info('Navigating to Catalog via Bottom Bar');
    await this.click(this.catalogTab, 'Bottom Nav Catalog Tab');
  }

  async clickCart() {
    logger.info('Navigating to Cart via Bottom Bar');
    await this.click(this.cartTab, 'Bottom Nav Cart Tab');
  }

  async clickProfile() {
    logger.info('Navigating to Profile via Bottom Bar');
    await this.click(this.profileTab, 'Bottom Nav Profile Tab');
  }
}

module.exports = new BottomNav();
