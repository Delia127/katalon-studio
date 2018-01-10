/**
 * Check if element present in timeout
 * @param to Katalon test object
 * @param timeout time to wait for element to show up
 * @return true if element present, otherwise false
 */
@Keyword
def isElementPresent_Mobile(TestObject to, int timeout){
	try {
		KeywordUtil.logInfo("Finding element with id:" + to.getObjectId())

		WebElement element = MobileElementCommonHelper.findElement(to, timeout)
		if (element != null) {
			KeywordUtil.markPassed("Object " + to.getObjectId() + " is present")
		}
		return true
	} catch (Exception e) {
		KeywordUtil.markFailed("Object " + to.getObjectId() + " is not present aa")
	}
	return false;
}

/**
 * Get mobile driver for current session
 * @return mobile driver for current session
 */
@Keyword
def WebDriver getCurrentSessionMobileDriver() {
	return MobileDriverFactory.getDriver();
}