package com.kms.katalon.composer.webui.recorder.constants;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

    // RecorderDialog
    public static final Image IMG_28_RECORD = ImageUtil.loadImage(currentBundle, "/icons/record_28.png");
    public static final Image IMG_16_PAUSE = ImageUtil.loadImage(currentBundle, "/icons/pause_16.png");
    public static final Image IMG_16_STOP = ImageUtil.loadImage(currentBundle, "/icons/stop_16.png");
    public static final Image IMG_16_PLAY = ImageUtil.loadImage(currentBundle, "/icons/play_16.png");

    // RecorderDialog
    public static final Image IMG_16_CHROME = ImageUtil.loadImage(currentBundle, "/icons/chrome_16.png");
    public static final Image IMG_16_FIREFOX = ImageUtil.loadImage(currentBundle, "/icons/firefox_16.png");
    public static final Image IMG_16_IE = ImageUtil.loadImage(currentBundle, "/icons/ie_16.png");
}
