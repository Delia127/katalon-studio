package com.kms.katalon.composer.properties.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {

    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

    public static final Image IMG_SAVE_16 = ImageUtil.loadImage(currentBundle, "/icons/save_16.png");

    public static final Image IMG_REFRESH_16 = ImageUtil.loadImage(currentBundle, "/icons/refresh_16.png");

}
