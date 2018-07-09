package com.kms.katalon.composer.mobile.constants;

import java.net.URL;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;

public class ImageConstants {

    // RunningMenuContribution
    public static final String IMG_URL_16_ANDROID = ImageManager.getImageURLString(IImageKeys.ANDROID_16);

    public static final String IMG_URL_16_APPLE = ImageManager.getImageURLString(IImageKeys.APPLE_16);

    public static final String IMG_URL_16_MOBILE = ImageManager.getImageURLString(IImageKeys.MOBILE_16);
    
    // MobileDeviceColumnLabelProvider
    public static final Image IMG_16_ANDROID = ImageManager.getImage(IImageKeys.ANDROID_16);

    public static final Image IMG_16_APPLE = ImageManager.getImage(IImageKeys.APPLE_16);

    public static final URL URL_16_LOADING = ImageManager.getImageURL(IImageKeys.LOADING_16);

}
