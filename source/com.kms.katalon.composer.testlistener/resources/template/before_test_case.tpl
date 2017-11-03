@BeforeTestCase
def sampleBeforeTestCase(TestCaseContext testCaseContext) {	
	println testCaseContext.getTestCaseId()		
	println testCaseContext.getTestCaseVariables()
}