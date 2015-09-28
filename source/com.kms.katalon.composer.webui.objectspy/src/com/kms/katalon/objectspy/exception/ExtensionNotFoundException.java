package com.kms.katalon.objectspy.exception;

import java.text.MessageFormat;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.constants.StringConstants;

public class ExtensionNotFoundException extends Exception {
	private static final long serialVersionUID = 7635634552086264000L;

	public ExtensionNotFoundException(String extensionName, WebUIDriverType webUiDriverType) {
		super(MessageFormat.format(StringConstants.EXC_EXTENSION_FOR_BROWSER_NOT_FOUND, extensionName,
				webUiDriverType.toString()));
	}
}
