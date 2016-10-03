package com.kms.katalon.composer.testsuite.constants;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants extends com.kms.katalon.composer.components.impl.constants.ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
    private static final Bundle componentBundle = FrameworkUtil
            .getBundle(com.kms.katalon.composer.components.impl.constants.ImageConstants.class);

    // OpenTestSuiteHandler
    public static final String IMG_16_TEST_SUITE_PATH = "/icons/test_suite_16.png";
    public static final String URL_16_TEST_SUITE = ImageUtil.getImageUrl(componentBundle, IMG_16_TEST_SUITE_PATH);

    // TestSuitePart
    public static final Image IMG_16_CHECKBOX_CHECKED = ImageUtil.loadImage(componentBundle,
            "/icons/checkbox_checked_16.png");
    public static final Image IMG_16_CHECKBOX_UNCHECKED = ImageUtil.loadImage(componentBundle,
            "/icons/checkbox_unchecked_16.png");

    // TestDataIDColumnLabelProvider
    public static final Image IMG_16_DATA_CROSS = ImageUtil.loadImage(currentBundle, "/icons/data_cross_16.png");
    public static final Image IMG_16_DATA_ONE_ONE = ImageUtil.loadImage(currentBundle, "/icons/data_one_one_16.png");

    // TestSuitePart
    public static final Image IMG_16_SEARCH = ImageUtil.loadImage(currentBundle, "/icons/search_16.png");
    public static final Image IMG_16_CLOSE_SEARCH = ImageUtil.loadImage(currentBundle, "/icons/close_search_16.png");
    public static final Image IMG_16_ADVANCED_SEARCH = ImageUtil.loadImage(currentBundle,
            "/icons/advanced_search_16.png");

    // Table
    public static final Image IMG_16_NOTIFICATION_HEADER = 
            com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_NOTIFICATION_HEADER;

    public static final Image IMG_16_WARN_TABLE_ITEM = PlatformUI.getWorkbench().getSharedImages()
            .getImage(ISharedImages.IMG_OBJS_WARN_TSK);
    public static final Image IMG_16_ERROR_TABLE_ITEM = IMG_16_NOTIFICATION_HEADER;

    // TestSuiteCompositePart
    public static final Image IMG_16_MAIN = ImageUtil.loadImage(currentBundle, "/icons/main_16.png");
    public static final Image IMG_16_INTEGRATION = ImageUtil.loadImage(currentBundle, "/icons/integration_16.png");

}
