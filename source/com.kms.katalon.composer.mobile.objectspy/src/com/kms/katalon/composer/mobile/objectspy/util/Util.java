package com.kms.katalon.composer.mobile.objectspy.util;

public class Util {

	public static String getDefaultMobileScreenshotPath() throws Exception {
		return getCurrentInstallDirectory() + System.getProperty("file.separator") + "screenshot";
	}

	public static String getCurrentInstallDirectory() {
		return System.getProperty("user.dir");		
	}
}
