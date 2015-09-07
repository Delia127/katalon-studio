package com.kms.katalon.objectspy.exception;

import java.text.MessageFormat;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.constants.StringConstants;

public class BrowserNotSupportedException extends Exception {
	private static final long serialVersionUID = 2593988755326659193L;

	public BrowserNotSupportedException(WebUIDriverType webUiDriverType) {
		super(MessageFormat.format(StringConstants.EXC_OBJ_SPY_FOR_BROWSER_IS_NOT_SUPPORTED, webUiDriverType.toString()));
	}
}
