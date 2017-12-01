/**
* Executes before every test case starts.
* @param testCaseContext related information of the executed test case.
*/
@BeforeTestCase
def sampleBeforeTestCase(TestCaseContext testCaseContext) {	
	println testCaseContext.getTestCaseId()		
	println testCaseContext.getTestCaseVariables()
}