package com.kms.katalon.composer.testsuite.collection.constant;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants extends com.kms.katalon.composer.components.impl.constants.ImageConstants {
    private static final Bundle componentBundle = Platform.getBundle("com.kms.katalon.composer.components.impl");

    // OpenTestSuiteCollectionHandler
    public static final String URL_16_TEST_SUITE_COLLECTION = ImageUtil.getImageUrl(componentBundle,
            "/icons/test_suite_collection_16.png");

    // WrappedTestSuiteLabelProvider
    public static final Image IMG_16_CHECKED = com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_CHECKED;

    public static final Image IMG_16_UNCHECKED = com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_UNCHECKED;

    public static final Image IMG_24_ADD = IMG_16_ADD;

    public static final Image IMG_24_REMOVE = IMG_16_REMOVE;

    public static final Image IMG_24_UP = IMG_16_MOVE_UP;

    public static final Image IMG_24_DOWN = IMG_16_MOVE_DOWN;

    public static final Image IMG_24_EXECUTE = IMG_16_EXECUTE;

    // TestSuiteCollectionPart
    public static final Image IMG_16_ARROW_DOWN_BLACK = IMG_16_ARROW_DOWN;

    public static final Image IMG_16_ARROW_UP_BLACK = IMG_16_ARROW;
}
