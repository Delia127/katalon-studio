/**
 * Refresh browser
 */
@Keyword
def refreshBrowser() {
	KeywordUtil.logInfo("Refreshing")
	WebDriver webDriver = DriverFactory.getWebDriver()
	webDriver.navigate().refresh()
	KeywordUtil.markPassed("Refresh successfully")
}

/**
 * Click element
 * @param to Katalon test object
 */
@Keyword
def clickElement(TestObject to) {
	try {
		WebElement element = WebUiBuiltInKeywords.findWebElement(to);
		KeywordUtil.logInfo("Clicking element")
		element.click()
		KeywordUtil.markPassed("Element has been clicked")
	} catch (WebElementNotFoundException e) {
		KeywordUtil.markFailed("Element not found")
	} catch (Exception e) {
		KeywordUtil.markFailed("Fail to click on element")
	}
}

/**
 * Get all rows of HTML table
 * @param table Katalon test object represent for HTML table
 * @param outerTagName outer tag name of TR tag, usually is TBODY
 * @return All rows inside HTML table
 */
@Keyword
def List<WebElement> getHtmlTableRows(TestObject table, String outerTagName) {
	WebElement mailList = WebUiBuiltInKeywords.findWebElement(table)
	List<WebElement> selectedRows = mailList.findElements(By.xpath("./" + outerTagName + "/tr"))
	return selectedRows
}