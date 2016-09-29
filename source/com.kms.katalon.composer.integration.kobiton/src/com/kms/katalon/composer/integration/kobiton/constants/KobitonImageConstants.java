package com.kms.katalon.composer.integration.kobiton.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class KobitonImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(KobitonImageConstants.class);

    public static final Image IMG_16_KOBITON = ImageUtil.loadImage(currentBundle, "/icons/kobiton_16.png");
}
