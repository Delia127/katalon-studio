package com.kms.katalon.composer.integration.jira.constant;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;

public class ImageConstants extends com.kms.katalon.composer.components.impl.constants.ImageConstants {
    public static Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

    public static final Image IMG_16_JIRA = ImageUtil.loadImage(currentBundle, "resources/icons/jira_active_16.png");

    public static final Image IMG_ISSUE_HOVER_IN = ImageUtil.loadImage(currentBundle, "resources/icons/bug_16.png");

    public static final Image IMG_ISSUE_HOVER_OUT = ImageUtil.loadImage(currentBundle,
            "resources/icons/bug_disabled_16.png");
    
    public static final Image IMG_16_WARNING = ImageManager.getImage(IImageKeys.WARNING_16);
}
