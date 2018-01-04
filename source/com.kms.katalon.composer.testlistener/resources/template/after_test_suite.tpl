/**
* Executes after every test suite ends.
* @param testSuiteContext: related information of the executed test suite.
*/
@AfterTestSuite
def sampleAfterTestSuite(TestSuiteContext testSuiteContext) {
	println testSuiteContext.getTestSuiteId()
}