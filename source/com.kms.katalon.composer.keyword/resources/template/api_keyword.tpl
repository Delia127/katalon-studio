@Keyword
def List<WebElement> getHtmlTableRowsA(TestObject table, String outerTagName) {
	WebElement mailList = WebUiBuiltInKeywords.findWebElement(table)
	List<WebElement> selectedRows = mailList.findElements(By.xpath("./" + outerTagName + "/tr"))
	return selectedRows
}