package com.kms.katalon.composer.integration.qtest.constant;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;
import com.kms.katalon.composer.integration.qtest.dialog.provider.QTestSuiteParentTreeLabelProvider;

public class ImageConstants {
    public static final Image IMG_16_FOLDER = 
            com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_FOLDER;
    public static final Image IMG_16_TEST_CASE = 
            com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_TEST_CASE;
    public static final Image IMG_16_CHECKED= 
            com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_CHECKED;
    public static final Image IMG_16_UNCHECKED= 
            com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_UNCHECKED;


    public static final Image IMG_16_UPLOADED = ImageUtil.loadImage(
            FrameworkUtil.getBundle(QTestSuiteParentTreeLabelProvider.class), "icons/uploaded.png");
    public static final Image IMG_16_UPLOADING = ImageUtil.loadImage(
            FrameworkUtil.getBundle(QTestSuiteParentTreeLabelProvider.class), "icons/uploading.png");
}
