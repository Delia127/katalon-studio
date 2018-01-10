/**
 * Send request and verify status code
 * @param request request object, must be an instance of RequestObject
 * @param expectedStatusCode
 * @return a boolean to indicate whether the response status code equals the expected one
 */
@Keyword
def verifyStatusCode(TestObject request, int expectedStatusCode) {
	if (request instanceof RequestObject) {
		RequestObject requestObject = (RequestObject) request
		ResponseObject response = WSBuiltInKeywords.sendRequest(requestObject)
		if (response.getStatusCode() == expectedStatusCode) {
			KeywordUtil.markPassed("Response status codes match")
		} else {
			KeywordUtil.markFailed("Response status code not match. Expected: " +
					expectedStatusCode + " - Actual: " + response.getStatusCode() )
		}
	} else {
		KeywordUtil.markFailed(request.getObjectId() + " is not a RequestObject")
	}
}