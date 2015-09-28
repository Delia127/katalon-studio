package com.kms.katalon.composer.global.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
	private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
	
	// GlobalVariablePart
	public static final Image IMG_16_ADD = ImageUtil.loadImage(currentBundle, "/icons/add_16.png");
	public static final Image IMG_16_REMOVE = ImageUtil.loadImage(currentBundle, "/icons/remove_16.png");
	public static final Image IMG_16_CLEAR = ImageUtil.loadImage(currentBundle, "/icons/clear_16.png");
	public static final Image IMG_16_EDIT = ImageUtil.loadImage(currentBundle, "/icons/edit_16.png");
	public static final Image IMG_16_REFRESH = ImageUtil.loadImage(currentBundle, "/icons/refresh_16.png");
}
