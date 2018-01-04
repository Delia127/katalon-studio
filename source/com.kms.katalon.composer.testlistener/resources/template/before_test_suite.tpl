/**
* Executes before every test suite starts.
* @param testSuiteContext: related information of the executed test suite.
*/
@BeforeTestSuite
def sampleBeforeTestSuite(TestSuiteContext testSuiteContext) {
	println testSuiteContext.getTestSuiteId()
}