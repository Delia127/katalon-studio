package com.kms.katalon.objectspy.exception;

import java.text.MessageFormat;

import com.kms.katalon.objectspy.constants.StringConstants;

public class IEAddonNotInstalledException extends Exception {
	private static final long serialVersionUID = 835327598321494259L;
	
	public IEAddonNotInstalledException(String addOnName) {
		super(MessageFormat.format(StringConstants.EXC_ADDON_FOR_IE_IS_NOT_INSTALLED, addOnName));
	}
}
