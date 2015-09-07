package com.kms.katalon.composer.search.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

/**
 * Provides constant images for this bundle
 * @author duyluong
 *
 */
public class ImageConstants {
	private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

	// SearchResultPageLabelProvider
	public static final Image IMG_16_FOLDER = ImageUtil.loadImage(currentBundle, "/icons/folder_16.png");
	public static final Image IMG_16_FOLDER_TEST_CASE = ImageUtil.loadImage(currentBundle, "/icons/folder_test_case_16.png");
	public static final Image IMG_16_FOLDER_TEST_SUITE = ImageUtil.loadImage(currentBundle, "/icons/folder_test_suite_16.png");
	public static final Image IMG_16_FOLDER_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/folder_object_16.png");
	public static final Image IMG_16_FOLDER_DATA = ImageUtil.loadImage(currentBundle, "/icons/folder_data_16.png");
	public static final Image IMG_16_FOLDER_KEYWORD = ImageUtil.loadImage(currentBundle, "/icons/folder_keyword_16.png");
	public static final Image IMG_16_FOLDER_REPORT = ImageUtil.loadImage(currentBundle, "/icons/folder_report_16.png");
	public static final Image IMG_16_TEST_CASE = ImageUtil.loadImage(currentBundle, "/icons/test_case_16.png");
	public static final Image IMG_16_TEST_SUITE = ImageUtil.loadImage(currentBundle, "/icons/test_suite_16.png");
	public static final Image IMG_16_TEST_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/test_object_16.png");
	public static final Image IMG_16_TEST_DATA = ImageUtil.loadImage(currentBundle, "/icons/test_data_16.png");
	public static final Image IMG_16_KEYWORD = ImageUtil.loadImage(currentBundle, "/icons/keyword_16.png");
	
	// package_16.png and report_16.png are unused
}
