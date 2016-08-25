package com.kms.katalon.composer.project.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {

    public static final Image WEB_ICON = ImageUtil.loadImage(FrameworkUtil.getBundle(ImageConstants.class),
            StringConstants.WEB_ICON_PATH);

    public static final Image MOBILE_ICON = ImageUtil.loadImage(FrameworkUtil.getBundle(ImageConstants.class),
            StringConstants.MOBILE_ICON_PATH);

    public static final Image API_ICON = ImageUtil.loadImage(FrameworkUtil.getBundle(ImageConstants.class),
            StringConstants.WEB_API_ICON_PATH);

}
