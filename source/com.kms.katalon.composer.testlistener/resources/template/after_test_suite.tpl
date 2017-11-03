@AfterTestSuite
def sampleAfterTestSuite(TestSuiteContext testSuiteContext) {
	println testSuiteContext.getTestSuiteId()
}