@BeforeTestSuite
def sampleBeforeTestSuite(TestSuiteContext testSuiteContext) {
	println testSuiteContext.getTestSuiteId()
}