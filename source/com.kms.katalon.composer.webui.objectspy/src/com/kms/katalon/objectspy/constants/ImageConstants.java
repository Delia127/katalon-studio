package com.kms.katalon.objectspy.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
	private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
	
	// ObjectSpyDialog
	public static final Image IMG_24_NEW_PAGE_ELEMENT = ImageUtil.loadImage(currentBundle, "/icons/new_page_element_24.png");
	public static final Image IMG_24_NEW_FRAME_ELEMENT = ImageUtil.loadImage(currentBundle, "/icons/new_frame_element_24.png");
	public static final Image IMG_24_NEW_ELEMENT = ImageUtil.loadImage(currentBundle, "/icons/new_element_24.png");
	public static final Image IMG_16_DELETE = ImageUtil.loadImage(currentBundle, "/icons/delete_16.png");
	public static final Image IMG_24_ADD_TO_OBJECT_REPOSITORY = ImageUtil.loadImage(currentBundle, "/icons/add_to_object_repository_24.png");
	
	// HTMLElementLabelProvider
	public static final Image IMG_16_PAGE_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/page_element_16.png");
	public static final Image IMG_16_FRAME_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/frame_element_16.png");
	public static final Image IMG_16_TEST_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/test_object_16.png");
	public static final Image IMG_16_DONE = ImageUtil.loadImage(currentBundle, "/icons/done_16.png");
	public static final Image IMG_16_BUG = ImageUtil.loadImage(currentBundle, "/icons/bug_16.png");
}
