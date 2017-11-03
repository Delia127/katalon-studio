@AfterTestCase
def sampleAfterTestCase(TestCaseContext testCaseContext) {
	println testCaseContext.getTestCaseId()
	println testCaseContext.getTestCaseStatus()
}