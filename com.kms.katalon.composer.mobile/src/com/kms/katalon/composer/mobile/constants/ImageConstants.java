package com.kms.katalon.composer.mobile.constants;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {

	private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

	// RunningMenuContribution
	public static final String URL_16_ANDROID = ImageUtil.getImageUrl(currentBundle, "/icons/android_16.png");
	public static final String URL_16_APPLE = ImageUtil.getImageUrl(currentBundle, "/icons/apple_16.png");
	public static final String URL_16_MIXED_MODE = ImageUtil.getImageUrl(currentBundle, "/icons/mixed_mode.png");
}
