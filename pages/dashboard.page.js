const BasePage = require('./base.page');
const logger = require('../utilities/logger');

class DashboardPage extends BasePage {
  get dashboardHeader() { return '//*[@resource-id="com.example.rent:id/tvHeader" or @text="Dashboard" or @text="VibeCraft Rentals"]'; }
  get welcomeBanner() { return '//*[@resource-id="com.example.rent:id/tvWelcome"]'; }
  get searchInput() { return '//*[@resource-id="com.example.rent:id/etSearch" or @content-desc="Search Input"]'; }
  get itemsRecyclerView() { return '//*[@resource-id="com.example.rent:id/rvItems" or @class="androidx.recyclerview.widget.RecyclerView"]'; }
  get cardItems() { return '//android.view.ViewGroup[contains(@resource-id, "card_item")] | //*[contains(@class, "CardView")]'; }
  get userProfileAvatar() { return '//*[@resource-id="com.example.rent:id/ivAvatar" or @content-desc="User Profile"]'; }
  get logoutButton() { return '//*[@resource-id="com.example.rent:id/btnLogout" or @text="Logout"]'; }
  get progressBar() { return '//android.widget.ProgressBar'; }

  async isDashboardLoaded() {
    logger.info('Verifying Dashboard header and elements');
    return await this.isDisplayed(this.dashboardHeader);
  }

  async getWelcomeText() {
    return await this.getText(this.welcomeBanner);
  }

  async searchItem(keyword) {
    logger.info(`Searching for catalog item: ${keyword}`);
    await this.setValue(this.searchInput, keyword, 'Search Field');
  }

  async getItemCardsCount() {
    const cards = await browser.$$(this.cardItems);
    return cards.length;
  }

  async clickItemCardByIndex(index = 0) {
    logger.info(`Clicking Item Card at index: ${index}`);
    const cards = await browser.$$(this.cardItems);
    if (cards.length > index) {
      await cards[index].click();
    } else {
      throw new Error(`Item card at index ${index} not found. Total cards: ${cards.length}`);
    }
  }

  async logout() {
    logger.info('Executing Logout from Dashboard');
    await this.click(this.userProfileAvatar, 'User Profile Avatar');
    await this.click(this.logoutButton, 'Logout Button');
  }
}

module.exports = new DashboardPage();
