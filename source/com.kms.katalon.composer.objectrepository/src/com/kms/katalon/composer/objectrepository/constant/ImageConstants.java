package com.kms.katalon.composer.objectrepository.constant;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
    private static final Bundle sharedBundle = FrameworkUtil
            .getBundle(com.kms.katalon.composer.components.impl.constants.ImageConstants.class);

    // OpenTestObjectHandler
    public static final String URL_16_TEST_OBJECT = ImageUtil.getImageUrl(currentBundle, "/icons/test_object_16.png");

    // PropertyLabelProvider
    public static final Image IMG_16_CHECKBOX_CHECKED = ImageUtil.loadImage(currentBundle,
            "/icons/checkbox_checked_16.png");
    public static final Image IMG_16_CHECKBOX_UNCHECKED = ImageUtil.loadImage(currentBundle,
            "/icons/checkbox_unchecked_16.png");

    // ObjectPropertyView
    public static final Image IMG_16_ARROW_DOWN_BLACK = ImageUtil.loadImage(currentBundle,
            "/icons/arrow_down_black_16.png");
    public static final Image IMG_16_ARROW_UP_BLACK = ImageUtil
            .loadImage(currentBundle, "/icons/arrow_up_black_16.png");
    
    public static final Image IMG_16_CLEAR = ImageUtil.loadImage(sharedBundle, "/icons/clear_16.png");

    public static final Image IMG_24_ADD = ImageUtil.loadImage(currentBundle, "/icons/add_24.png");
    public static final Image IMG_24_REMOVE = ImageUtil.loadImage(currentBundle, "/icons/remove_24.png");
    public static final Image IMG_24_HIGHLIGHT = ImageUtil.loadImage(currentBundle, "/icons/highlight_24.png");

    // Other icons are using in fragment.e4xmi: new_test_object_28.png, object_spy_28.png
}
