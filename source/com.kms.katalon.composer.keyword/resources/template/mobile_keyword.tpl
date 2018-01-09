/**
 * Get all cells of HTML table row
 * @param row a WebElement instance represent for HTML table row 
 * @param tagName HTML column tag name, usually is TD/TH
 * @return All cells inside HTML table row 
 */
@Keyword
def List<WebElement> getHtmlTableColumns(WebElement row, String tagName) {
	List<WebElement> selectedColumns = row.findElements(By.tagName(tagName))
	return selectedColumns
}
	
@Keyword
def isElementPresentA(TestObject to, int timeout) {
	//Use Katalon built-in function to find elements with time out 1 seconds
	List<WebElement> elements = WebUiBuiltInKeywords.findWebElements(to, timeout)
	return elements.size() > 0
}