package com.kms.katalon.core.webservice.keyword;

import groovy.transform.CompileStatic

import java.text.MessageFormat
import java.util.regex.Pattern

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.exception.StepErrorException
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.keyword.KeywordMain
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.common.ServiceRequestFactory
import com.kms.katalon.core.webservice.constants.StringConstants
import com.kms.katalon.core.webservice.helper.WebServiceCommonHelper

@CompileStatic
public class WSBuiltInKeywords extends BuiltinKeywords {

	private static final KeywordLogger logger = KeywordLogger.getInstance();

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_REQUEST)
	public static ResponseObject sendRequest(RequestObject request, FailureHandling flowControl) throws Exception {
		Object object = KeywordMain.runKeyword({
			WebServiceCommonHelper.checkRequestObject(request);
			ResponseObject responseObject = ServiceRequestFactory.getInstance(request).send(request);
			logger.logPassed(StringConstants.KW_LOG_PASSED_SEND_REQUEST_SUCCESS);
			return responseObject;
		}, flowControl, StringConstants.KW_LOG_FAILED_CANNOT_SEND_REQUEST);
		if (object instanceof ResponseObject) {
			return (ResponseObject) object;
		}
		return null;
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static boolean verifyElementsCount(ResponseObject response, String locator, int count, FailureHandling flowControl) throws Exception {
		Object object = KeywordMain.runKeyword({
			WebServiceCommonHelper.checkResponseObject(response);
			Object retValue = response.isXmlContentType() ?
					WebServiceCommonHelper.parseAndExecuteExpressionForXml(locator, "size()", response.getResponseBodyContent())
					: WebServiceCommonHelper.parseAndExecuteExpressionForJson(locator, "size()", response.getResponseBodyContent());
			int actualValue = Integer.parseInt(String.valueOf(retValue))
			boolean isEqual = (count == actualValue);
			if (!isEqual) {
				KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_COUNT_NOT_EQUAL, count, actualValue), flowControl, null, null);
			} else {
				logger.logPassed(StringConstants.KW_LOG_PASSED_VERIFY_ELEMENT_COUNT);
			}
			return isEqual;
		}, flowControl, StringConstants.KW_LOG_FAILED_CANNOT_VERIFY_ELEMENT_COUNT);
		if (object != null) {
			return Boolean.valueOf(object.toString());
		}
		return false;
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static boolean verifyElementPropertyValue(ResponseObject response, String locator, String value, FailureHandling flowControl) throws StepErrorException {
		Object object = KeywordMain.runKeyword({
			WebServiceCommonHelper.checkResponseObject(response);
			Object retValue = response.isXmlContentType() ?
					WebServiceCommonHelper.parseAndGetPropertyValueForXml(locator, response.getResponseBodyContent()) :
					WebServiceCommonHelper.parseAndGetPropertyValueForJson(locator, response.getResponseBodyContent());
			boolean isEqual = (value.equals(String.valueOf(retValue)));
			if (!isEqual) {
				KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_PROP_VAL_NOT_EQUAL, value, retValue.toString()), flowControl, null, null);
			} else {
				logger.logPassed(StringConstants.KW_LOG_PASSED_VERIFY_ELEMENT_PROPERTY_VALUE);
			}
			return isEqual;
		}, flowControl, StringConstants.KW_LOG_FAILED_CANNOT_VERIFY_ELEMENT_PROPERTY_VALUE);
		if (object != null) {
			return Boolean.valueOf(object.toString());
		}
		return false;
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
	public static boolean verifyElementText(ResponseObject response, String locator, String text, FailureHandling flowControl) throws StepFailedException, StepErrorException {
		Object object = KeywordMain.runKeyword({
			WebServiceCommonHelper.checkResponseObject(response);
			Object retValue = response.isXmlContentType() ?
					WebServiceCommonHelper.parseAndExecuteExpressionForXml(locator, "text()", response.getResponseBodyContent()) :
					WebServiceCommonHelper.parseAndExecuteExpressionForJson(locator, "text()", response.getResponseBodyContent());
			boolean isEqual = (text.equals(String.valueOf(retValue)));
			if (!isEqual) {
				KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ACTUAL_ELEMENT_TEXT_IS, text, retValue), flowControl, null, null);
			} else {
				logger.logPassed(StringConstants.KW_LOG_PASSED_VERIFY_ELEMENT_TEXT);
			}
			return isEqual;
		}, flowControl, StringConstants.KW_LOG_FAILED_CANNOT_VERIFY_ELEMENT_TEXT);
		if (object != null) {
			return Boolean.valueOf(object.toString());
		}
		return false;
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
	public static boolean containsString(ResponseObject response, String string, boolean useRegex, FailureHandling flowControl) throws StepFailedException {
		Object object = KeywordMain.runKeyword({
			WebServiceCommonHelper.checkResponseObject(response);
			WebServiceCommonHelper.checkResponseObjectContent(response);
			boolean isMatch = false;
			if (useRegex) {
				Pattern p = Pattern.compile(string, Pattern.DOTALL);
				isMatch = p.matcher(response.getResponseBodyContent()).matches();
			} else {
				isMatch = (string != null && string.equals(response.getResponseText()));
			}
			if (isMatch) {
				logger.logPassed(StringConstants.KW_LOG_PASSED_CONTAIN_STRING);
			} else {
				KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_STR_NOT_FOUND_IN_RES, string, response.getResponseText()), flowControl, null, null);
			}
			return isMatch;
		}, flowControl, StringConstants.KW_LOG_FAILED_CONTAIN_STRING);
		if (object != null) {
			return Boolean.valueOf(object.toString());
		}
		return false;
	}
}
