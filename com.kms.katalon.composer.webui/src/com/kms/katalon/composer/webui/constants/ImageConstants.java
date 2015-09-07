package com.kms.katalon.composer.webui.constants;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {

	private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
	
	public static final String URL_16_CHROME = ImageUtil.getImageUrl(currentBundle, "/icons/chrome_16.png");
	public static final String URL_16_FIREFOX = ImageUtil.getImageUrl(currentBundle, "/icons/firefox_16.png");
	public static final String URL_16_IE = ImageUtil.getImageUrl(currentBundle, "/icons/ie_16.png");
	public static final String URL_16_SAFARI = ImageUtil.getImageUrl(currentBundle, "/icons/safari_16.png");
	public static final String URL_16_REMOTE_WEB = ImageUtil.getImageUrl(currentBundle, "/icons/remote_web_driver_16.png");
}
