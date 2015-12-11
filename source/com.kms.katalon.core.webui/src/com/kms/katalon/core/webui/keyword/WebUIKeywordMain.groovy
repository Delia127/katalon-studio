package com.kms.katalon.core.webui.keyword;

import groovy.transform.CompileStatic

import java.text.MessageFormat

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.KeywordExceptionHandler
import com.kms.katalon.core.keyword.KeywordMain
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.ExceptionsUtil;
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.util.FileUtil

public class WebUIKeywordMain extends KeywordMain {
	private static KeywordLogger logger = KeywordLogger.getInstance();

	@CompileStatic
	public static runKeyword(Closure closure, FailureHandling flowControl, boolean takeScreenShot, String errorMessage) {
		try {
			return closure.call();
		} catch (Throwable e) {
            if (!(e instanceof StepFailedException)) {
                stepFailed(errorMessage, flowControl, ExceptionsUtil.getMessageForThrowable(e), takeScreenShot);
            } else {
                throw e;
            }
		}
	}

	@CompileStatic
	public static stepFailed(String message, FailureHandling flHandling, String reason, boolean takeScreenShot)
	throws StepFailedException {
		if (takeScreenShot) {
			String screenFileName = null;
			try {
				screenFileName = FileUtil.takesScreenshot();
			} catch (Exception ex) {
				// ignore exception raised by taking screenshot
				logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_WARNING_CANNOT_TAKE_SCREENSHOT,
						ExceptionsUtil.getMessageForThrowable(ex)));
			}
			if (screenFileName != null) {
				Map<String, String> attributes = new HashMap<String, String>();
				attributes.put(com.kms.katalon.core.constants.StringConstants.XML_LOG_ATTACHMENT_PROPERTY, screenFileName)
				super.stepFailed(message, flHandling, reason, attributes);
				return;
			}
		}
		super.stepFailed(message, flHandling, reason);
	}
}
