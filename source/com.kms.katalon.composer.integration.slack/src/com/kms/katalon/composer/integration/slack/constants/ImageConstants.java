package com.kms.katalon.composer.integration.slack.constants;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

    // SlackUtil
    public static final URL URL_16_LOADING = currentBundle.getEntry("/icons/loading_16.gif");
}
