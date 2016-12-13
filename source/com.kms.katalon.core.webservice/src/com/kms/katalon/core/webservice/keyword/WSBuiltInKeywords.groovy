package com.kms.katalon.core.webservice.keyword;

import groovy.transform.CompileStatic

import java.text.MessageFormat
import java.util.regex.Pattern

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.exception.StepErrorException
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.keyword.internal.KeywordExecutor
import com.kms.katalon.core.keyword.internal.KeywordMain
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.common.ServiceRequestFactory
import com.kms.katalon.core.webservice.constants.StringConstants
import com.kms.katalon.core.webservice.helper.WebServiceCommonHelper
import com.kms.katalon.core.configuration.RunConfiguration

@CompileStatic
public class WSBuiltInKeywords extends BuiltinKeywords {

    /**
     * Send a HTTP Request to web server
     * @param request the object represents for a HTTP Request, user need to define it from Object Repository->New->Web Service Request, and get it by ObjectRepository.findRequestObject("requestObjectId")
     * @param flowControl
     * @return
     * @throws Exception
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_REQUEST)
    public static ResponseObject sendRequest(RequestObject request, FailureHandling flowControl) throws Exception {
        return (ResponseObject) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "sendRequest", request, flowControl)
    }

    /**
     * Send a HTTP Request to web server
     * @param request the object represents for a HTTP Request, user need to define it from Object Repository->New->Web Service Request, and get it by ObjectRepository.findRequestObject("requestObjectId")
     * @return
     * @throws Exception
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_REQUEST)
    public static ResponseObject sendRequest(RequestObject request) throws Exception {
        return (ResponseObject) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "sendRequest", request)
    }

    /**
     * Verify number of expected elements (JSON/XML) in the response (output) of a web service call 
     * @param response the object represents for a HTTP Response, user can get responded content type, data, header properties (sometime user may want to get cookie from response header)
     * @param locator an expression Katalon will use to go through and look for expected element(s), please refer to our user guide for how to write it   
     * @param count the expected number of element(s) should appear in the responded data (usually is JSON/XML)
     * @param flowControl
     * @return true if your expectation is met, otherwise false 
     * @throws Exception
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementsCount(ResponseObject response, String locator, int count, FailureHandling flowControl) throws Exception {
        return (boolean) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "verifyElementsCount", response, locator, count, flowControl)
    }

    /**
     * Verify number of expected elements (JSON/XML) in the response (output) of a web service call
     * @param response the object represents for a HTTP Response, user can get responded content type, data, header properties (sometime user may want to get cookie from response header)
     * @param locator an expression Katalon will use to go through and look for expected element(s), please refer to our user guide for how to write it
     * @param count the expected number of element(s) should appear in the responded data (usually is JSON/XML)
     * @return true if your expectation is met, otherwise false
     * @throws Exception
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementsCount(ResponseObject response, String locator, int count) throws Exception {
        return (boolean) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "verifyElementsCount", response, locator, count)
    }

    /**
     * Verify that there is an element with expected property value appear in the returned data from a web service call
     * @param response the object represents for a HTTP Response, user can get responded content type, data, header properties (sometime user may want to get cookie from response header)
     * @param locator an expression Katalon will use to go through and look for expected element(s), please refer to our user guide for how to write it
     * @param value the expected value of element you want to verify in the responded data (usually is JSON/XML)
     * @param flowControl
     * @return true if your expectation is met, otherwise false 
     * @throws StepErrorException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementPropertyValue(ResponseObject response, String locator, String value, FailureHandling flowControl) throws StepErrorException {
        return (boolean) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "verifyElementPropertyValue", response, locator, value, flowControl)
    }

    /**
     * Verify that there is an element with expected property value appear in the returned data from a web service call
     * @param response the object represents for a HTTP Response, user can get responded content type, data, header properties (sometime user may want to get cookie from response header)
     * @param locator an expression Katalon will use to go through and look for expected element(s), please refer to our user guide for how to write it
     * @param value the expected value of element you want to verify in the responded data (usually is JSON/XML)
     * @return true if your expectation is met, otherwise false
     * @throws StepErrorException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementPropertyValue(ResponseObject response, String locator, String value) throws StepErrorException {
        return (boolean) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "verifyElementPropertyValue", response, locator, value)
    }

    /**
     * Verify that there is an element with expected text appear in the returned data from a web service call
     * @param response the object represents for a HTTP Response, user can get responded content type, data, header properties (sometime user may want to get cookie from response header)
     * @param locator an expression Katalon will use to go through and look for expected element(s), please refer to our user guide for how to write it
     * @param text the expected text of element you want to verify in the responded data (usually is JSON/XML)
     * @param flowControl
     * @return true if your element text is found, otherwise false
     * @throws StepFailedException
     * @throws StepErrorException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementText(ResponseObject response, String locator, String text, FailureHandling flowControl) throws StepFailedException, StepErrorException {
        return (boolean) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "verifyElementText", response, locator, text, flowControl)
    }

    /**
     * Verify that there is an element with expected text appear in the returned data from a web service call
     * @param response the object represents for a HTTP Response, user can get responded content type, data, header properties (sometime user may want to get cookie from response header)
     * @param locator an expression Katalon will use to go through and look for expected element(s), please refer to our user guide for how to write it
     * @param text the expected text of element you want to verify in the responded data (usually is JSON/XML)
     * @return true if your element text is found, otherwise false
     * @throws StepFailedException
     * @throws StepErrorException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementText(ResponseObject response, String locator, String text) throws StepFailedException, StepErrorException {
        return (boolean) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "verifyElementText", response, locator, text)
    }

    /**
     * Verify that an expected text appear in the returned data from a web service call
     * @param response the object represents for a HTTP Response, user can get responded content type, data, header properties (sometime user may want to get cookie from response header)
     * @param string the text you want to look for
     * @param useRegex use regular expression or not
     * @param flowControl
     * @return true if your text is found, otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static boolean containsString(ResponseObject response, String string, boolean useRegex, FailureHandling flowControl) throws StepFailedException {
        return (boolean) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "containsString", response, string, useRegex, flowControl)
    }

    /**
     * Verify that an expected text appear in the returned data from a web service call
     * @param response the object represents for a HTTP Response, user can get responded content type, data, header properties (sometime user may want to get cookie from response header)
     * @param string the text you want to look for
     * @param useRegex use regular expression or not
     * @return true if your text is found, otherwise false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static boolean containsString(ResponseObject response, String string, boolean useRegex) throws StepFailedException {
        return (boolean) KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB_SERVICE, "containsString", response, string, useRegex)
    }
}