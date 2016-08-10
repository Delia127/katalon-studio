package com.kms.katalon.composer.mobile.objectspy.constant;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

    private static final Bundle componentBundle = FrameworkUtil.getBundle(com.kms.katalon.composer.components.impl.constants.ImageConstants.class);

    // MobileObjectSpyDialog
    public static final Image IMG_16_START_DEVICE = ImageUtil.loadImage(currentBundle, "/icons/start_device_16.png");

    public static final Image IMG_16_CAPTURE = ImageUtil.loadImage(currentBundle, "/icons/capture_16.png");

    public static final Image IMG_16_NEW_TEST_OBJECT = ImageUtil.loadImage(currentBundle,
            "/icons/new_test_object_16.png");

    public static final Image IMG_16_STOP_DEVICE = ImageUtil.loadImage(currentBundle, "/icons/stop_device_16.png");

    public static final Image IMG_24_START_DEVICE = ImageUtil.loadImage(currentBundle, "/icons/start_device_24.png");

    public static final Image IMG_24_CAPTURE = ImageUtil.loadImage(currentBundle, "/icons/capture_24.png");

    public static final Image IMG_24_NEW_TEST_OBJECT = ImageUtil.loadImage(currentBundle,
            "/icons/new_test_object_24.png");

    public static final Image IMG_24_STOP_DEVICE = ImageUtil.loadImage(currentBundle, "/icons/stop_device_24.png");

    // MobileElementLabelProvider
    public static final Image IMG_16_TEST_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/test_object_16.png");

    // Other icon is using in fragment.e4xmi: object_spy_mobile_28.png
    public static final Image IMG_16_CHECKED = ImageUtil.loadImage(componentBundle, "/icons/checkbox_checked_16.png");

    public static final Image IMG_16_UNCHECKED = ImageUtil.loadImage(componentBundle,
            "/icons/checkbox_unchecked_16.png");

    public static final Image IMG_16_ACTIVE = ImageUtil.loadImage(currentBundle, "/icons/active_16.png");

    public static final Image IMG_16_INACTIVE = ImageUtil.loadImage(currentBundle, "/icons/inactive_16.png");
}
