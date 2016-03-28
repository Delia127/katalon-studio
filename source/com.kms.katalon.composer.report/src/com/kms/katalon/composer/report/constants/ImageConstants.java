package com.kms.katalon.composer.report.constants;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
	private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

	// OpenReportHandler
	public static final String URL_16_REPORT = ImageUtil.getImageUrl(currentBundle, "/icons/report_16.png");

	// ReportPart
	public static final Image IMG_16_ARROW_UP_BLACK = ImageUtil
			.loadImage(currentBundle, "/icons/arrow_up_black_16.png");
	public static final Image IMG_16_ARROW_DOWN_BLACK = ImageUtil.loadImage(currentBundle,
			"/icons/arrow_down_black_16.png");

	public static final Image IMG_16_TEST_CASE = ImageUtil.loadImage(
			FrameworkUtil.getBundle(com.kms.katalon.composer.testcase.constants.ImageConstants.class),
			com.kms.katalon.composer.testcase.constants.ImageConstants.IMG_16_TEST_CASE_PATH);

	public static final Image IMG_16_TEST_SUITE = ImageUtil.loadImage(
			FrameworkUtil.getBundle(com.kms.katalon.composer.testsuite.constants.ImageConstants.class),
			com.kms.katalon.composer.testsuite.constants.ImageConstants.IMG_16_TEST_SUITE_PATH);

	public static final Image IMG_16_TEST_STEP = com.kms.katalon.composer.testcase.constants.ImageConstants.IMG_16_FAILED_STOP;

	public static final Image IMG_MESSAGE = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
	
	public static final Image IMG_16_ATTACHMENT = ImageUtil.loadImage(currentBundle, "/icons/attachment_16.png");
	
	public static final Image IMG_16_PASSED= ImageUtil.loadImage(currentBundle, "/icons/log_passed_16.png");
	public static final Image IMG_16_FAILED = ImageUtil.loadImage(currentBundle, "/icons/log_failed_16.png");
	public static final Image IMG_16_ERROR = ImageUtil.loadImage(currentBundle, "/icons/log_error_16.png");
	public static final Image IMG_16_INCOMPLETE = ImageUtil.loadImage(currentBundle, "/icons/log_incomplete_16.png");
	public static final Image IMG_16_INFO = ImageUtil.loadImage(currentBundle, "/icons/log_info_16.png");
	public static final Image IMG_16_NOT_RUN = ImageUtil.loadImage(currentBundle, "/icons/log_not_run_16.png");
	public static final Image IMG_16_WARNING = ImageUtil.loadImage(currentBundle, "/icons/log_warning_16.png");
	
	public static final Image IMG_16_INTEGRATION = ImageUtil.loadImage(currentBundle, "/icons/integration_16.png");
}
