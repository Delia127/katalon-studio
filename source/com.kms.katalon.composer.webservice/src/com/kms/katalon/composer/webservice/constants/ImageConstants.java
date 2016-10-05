package com.kms.katalon.composer.webservice.constants;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

    private static final Bundle sharedBundle = FrameworkUtil.getBundle(com.kms.katalon.composer.components.impl.constants.ImageConstants.class);

    // OpenWebServiceRequestObjectHandler
    public static final String URL_24_NEW_WS_TEST_OBJECT = ImageUtil.getImageUrl(currentBundle,
            "/icons/new_ws_test_object_24.png");

    public static final String URL_16_WS_TEST_OBJECT = ImageUtil.getImageUrl(sharedBundle,
            "/icons/ws_test_object_16.png");

}
