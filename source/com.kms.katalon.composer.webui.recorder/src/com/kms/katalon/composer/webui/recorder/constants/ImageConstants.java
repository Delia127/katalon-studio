package com.kms.katalon.composer.webui.recorder.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

    private static final Bundle componentBundle = 
            FrameworkUtil.getBundle(com.kms.katalon.composer.components.impl.constants.ImageConstants.class);

    // RecorderDialog
    public static final Image IMG_28_RECORD = ImageUtil.loadImage(currentBundle, "/icons/record_28.png");

    public static final Image IMG_28_PAUSE = ImageUtil.loadImage(componentBundle, "/icons/pause_28.png");

    public static final Image IMG_28_STOP = ImageUtil.loadImage(componentBundle, "/icons/stop_28.png");

    public static final Image IMG_28_PLAY = ImageUtil.loadImage(componentBundle, "/icons/play_28.png");

    // RecorderDialog
    public static final Image IMG_16_CHROME = ImageUtil.loadImage(currentBundle, "/icons/chrome_16.png");

    public static final Image IMG_16_FIREFOX = ImageUtil.loadImage(currentBundle, "/icons/firefox_16.png");

    public static final Image IMG_16_IE = ImageUtil.loadImage(currentBundle, "/icons/ie_16.png");
}
