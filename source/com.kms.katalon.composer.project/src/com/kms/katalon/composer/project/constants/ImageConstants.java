package com.kms.katalon.composer.project.constants;

import java.net.URL;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;

public class ImageConstants {

    public static final Image WEB_ICON = ImageManager.getImage(IImageKeys.TEMPLATE_WEB_16);

    public static final Image MOBILE_ICON = ImageManager.getImage(IImageKeys.TEMPLATE_MOBILE_16);

    public static final Image API_ICON = ImageManager.getImage(IImageKeys.TEMPLATE_API_16);
    
    public static final URL URL_SAMPLE_WEB_16 = ImageManager.getImageURL(IImageKeys.SAMPLE_WEB_UI_16);

    public static final URL URL_SAMPLE_MOBILE_16 = ImageManager.getImageURL(IImageKeys.SAMPLE_MOBILE_16);

    public static final URL URL_SAMPLE_WS_16 = ImageManager.getImageURL(IImageKeys.SAMPLE_WEB_SERVICE_16);

    public static final Image IMG_PROJECT_16 = ImageManager.getImage(IImageKeys.PROJECT_16);

}
