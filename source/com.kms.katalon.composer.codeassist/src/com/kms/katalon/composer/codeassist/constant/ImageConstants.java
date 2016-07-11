package com.kms.katalon.composer.codeassist.constant;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;
public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
    public static final Image IMG_16_BRANDING = ImageUtil.loadImage(currentBundle, "/icons/branding_16.png");
}
