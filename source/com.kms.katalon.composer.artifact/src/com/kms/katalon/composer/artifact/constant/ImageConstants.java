package com.kms.katalon.composer.artifact.constant;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;

public class ImageConstants {

    public static Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
    
    public static final Image IMG_16_ADD = ImageManager.getImage(IImageKeys.ADD_16);

    public static final Image IMG_16_DELETE = ImageManager.getImage(IImageKeys.DELETE_16);
}
