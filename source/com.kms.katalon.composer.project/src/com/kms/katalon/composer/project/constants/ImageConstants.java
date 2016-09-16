package com.kms.katalon.composer.project.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    public static final String WEB_ICON_PATH = "/icons/template_web.png";

    public static final String MOBILE_ICON_PATH = "/icons/template-mobile.png";

    public static final String WEB_API_ICON_PATH = "/icons/template_api.png";

    public static final Image WEB_ICON = ImageUtil.loadImage(FrameworkUtil.getBundle(ImageConstants.class),
            WEB_ICON_PATH);

    public static final Image MOBILE_ICON = ImageUtil.loadImage(FrameworkUtil.getBundle(ImageConstants.class),
            MOBILE_ICON_PATH);

    public static final Image API_ICON = ImageUtil.loadImage(FrameworkUtil.getBundle(ImageConstants.class),
            WEB_API_ICON_PATH);

}
