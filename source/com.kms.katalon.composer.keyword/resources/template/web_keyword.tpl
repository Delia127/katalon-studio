@Keyword
def isElementPresent(TestObject to, int timeout){
	//Use Katalon built-in function to find elements with time out 1 seconds
	List<WebElement> elements = WebUiBuiltInKeywords.findWebElements(to, timeout)
	return elements.size() > 0
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