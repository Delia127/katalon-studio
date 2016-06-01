package com.kms.katalon.composer.testsuite.collection.constant;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    private static final Bundle componentBundle = Platform.getBundle("com.kms.katalon.composer.components.impl");

    // OpenTestSuiteCollectionHandler
    public static final String URL_16_TEST_SUITE_COLLECTION = ImageUtil.getImageUrl(componentBundle,
            "/icons/test_suite_collection_16.png");

    // WrappedTestSuiteLabelProvider
    public static final Image IMG_16_CHECKED = com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_CHECKED;

    public static final Image IMG_16_UNCHECKED = com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_UNCHECKED;

    public static final Image IMG_24_ADD = ImageUtil.loadImage(componentBundle, "/icons/add_24.png");

    public static final Image IMG_24_REMOVE = ImageUtil.loadImage(componentBundle, "/icons/remove_24.png");

    public static final Image IMG_24_UP = ImageUtil.loadImage(componentBundle, "/icons/up_24.png");

    public static final Image IMG_24_DOWN = ImageUtil.loadImage(componentBundle, "/icons/down_24.png");

    public static final Image IMG_24_EXECUTE = ImageUtil.loadImage(componentBundle, "/icons/play_24.png");
}
