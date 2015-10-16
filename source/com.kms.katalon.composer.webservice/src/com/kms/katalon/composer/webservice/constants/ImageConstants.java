package com.kms.katalon.composer.webservice.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
	private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
	
	// OpenWebServiceRequestObjectHandler
	public static final String URL_28_NEW_WS_TEST_OBJECT = ImageUtil.getImageUrl(currentBundle, "/icons/new_ws_test_object_28.png");
	public static final String URL_16_WS_TEST_OBJECT = ImageUtil.getImageUrl(currentBundle, "/icons/ws_test_object_16.png");
	
	// ExpandableComposite
	public static final Image IMG_16_ARROW_DOWN_BLACK = ImageUtil.loadImage(currentBundle, "/icons/arrow_down_black_16.png");
	public static final Image IMG_16_ARROW_UP_BLACK = ImageUtil.loadImage(currentBundle, "/icons/arrow_up_black_16.png");
}
