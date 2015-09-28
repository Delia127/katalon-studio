package com.kms.katalon.composer.testdata.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
	private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
	
	// OpenTestDataHandler
	public static final String URL_16_TEST_DATA = ImageUtil.getImageUrl(currentBundle, "/icons/test_data_16.png");

	// CSVTestDataPart
	public static final Image IMG_16_ARROW_DOWN_BLACK = ImageUtil.loadImage(currentBundle, "/icons/arrow_down_black_16.png");
	public static final Image IMG_16_ARROW_UP_BLACK = ImageUtil.loadImage(currentBundle, "/icons/arrow_up_black_16.png");
	
	// InternalTestDataPart
	public static final Image IMG_16_ADD = ImageUtil.loadImage(currentBundle, "/icons/add_16.png");
}
