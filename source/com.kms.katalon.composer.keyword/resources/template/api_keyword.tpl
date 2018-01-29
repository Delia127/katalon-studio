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

/**
 * Add Header basic authorization field,
 * this field value is Base64 encoded token from user name and password
 * @param request object, must be an instance of RequestObject
 * @param username username
 * @param password password
 * @return the original request object with basic authorization header field added
 */
@Keyword
def addBasicAuthorizationProperty(TestObject request, String username, String password) {
    if (request instanceof RequestObject) {
        String authorizationValue = username + ":" + password
        authorizationValue = "Basic " + authorizationValue.bytes.encodeBase64().toString()

        // Find available basic authorization field and change its value to the new one, if any
        List<TestObjectProperty> headerProperties = request.getHttpHeaderProperties()
        boolean fieldExist = false
        for (int i = 0; i < headerProperties.size(); i++) {
            TestObjectProperty headerField = headerProperties.get(i)
            if (headerField.getName().equals('Authorization')) {
                KeywordUtil.logInfo("Found existent basic authorization field. Replacing its value.")
                headerField.setValue(authorizationValue)
                fieldExist = true
                break
            }
        }

        if (!fieldExist) {
            TestObjectProperty authorizationProperty = new TestObjectProperty("Authorization",
                    ConditionType.EQUALS, authorizationValue, true)
            headerProperties.add(authorizationProperty)
        }
        KeywordUtil.markPassed("Basic authorization field has been added to request header")
    } else {
        KeywordUtil.markFailed(request.getObjectId() + "is not a RequestObject")
    }
    return request
}