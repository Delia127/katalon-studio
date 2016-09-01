package com.kms.katalon.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {

    public static final Bundle CURRENT_BUNDLE = FrameworkUtil.getBundle(ImageConstants.class);

    public static final Image IMG_BRANDING_BACKGROUND = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/branding_background.png");

    public static final Image IMG_BRANDING = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/branding_text.png");

    public static final Image IMG_NEW_PROJECT = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/new_project.png");

    public static final Image IMG_OPEN_PROJECT = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/open_project.png");

    public static final Image IMG_RECENT_PROJECT = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/recent_project.png");

    public static final Image IMG_FAQ = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/faqs.png");

    public static final Image IMG_GETTING_STARTED = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/getting_started.png");

    public static final Image IMG_HOW_TO_ARTICLES = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/how_to_articles.png");
    
    public static final String URI_IMG_WELCOME = "platform:/plugin/org.eclipse.ui.intro/icons/welcome16.gif";

    // FunctionsIntroductionDialog
    public static final Image IMG_INTRO_SCREEN_1 = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/intro/screen_1.png");

    public static final Image IMG_INTRO_SCREEN_2 = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/intro/screen_2.png");

    public static final Image IMG_INTRO_SCREEN_3 = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/intro/screen_3.png");

    public static final Image IMG_INTRO_SCREEN_4 = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/intro/screen_4.png");

    public static final Image IMG_INTRO_SCREEN_5 = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/intro/screen_5.png");

    public static final Image IMG_INTRO_SCREEN_6 = ImageUtil.loadImage(CURRENT_BUNDLE, "/icons/intro/screen_6.png");
}
