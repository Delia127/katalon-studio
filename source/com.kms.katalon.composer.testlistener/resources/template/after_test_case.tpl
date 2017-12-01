/**
* Executes after every test case ends.
* @param testCaseContext related information of the executed test case.
*/
@AfterTestCase
def sampleAfterTestCase(TestCaseContext testCaseContext) {
	println testCaseContext.getTestCaseId()
	println testCaseContext.getTestCaseStatus()
}