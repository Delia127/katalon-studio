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
    public static final Image IMG_28_RECORD = ImageUtil.loadImage(componentBundle, "/icons/record_24.png");

    public static final Image IMG_28_PAUSE = ImageUtil.loadImage(currentBundle, "/icons/pause_24.png");

    public static final Image IMG_28_STOP = ImageUtil.loadImage(currentBundle, "/icons/stop_24.png");

    public static final Image IMG_28_PLAY = ImageUtil.loadImage(currentBundle, "/icons/play_24.png");
    
    public static final Image IMG_24_ADD = ImageUtil.loadImage(componentBundle, "/icons/add_16.png");

    public static final Image IMG_24_DELETE = ImageUtil.loadImage(componentBundle, "/icons/delete_16.png");

    // RecorderDialog
    public static final Image IMG_16_CHROME = ImageUtil.loadImage(currentBundle, "/icons/chrome_16.png");

    public static final Image IMG_16_FIREFOX = ImageUtil.loadImage(currentBundle, "/icons/firefox_16.png");

    public static final Image IMG_16_IE = ImageUtil.loadImage(currentBundle, "/icons/ie_16.png");
}
