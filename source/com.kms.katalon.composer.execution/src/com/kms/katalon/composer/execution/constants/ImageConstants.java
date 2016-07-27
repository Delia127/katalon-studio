package com.kms.katalon.composer.execution.constants;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
    private static final Bundle componentBundle = Platform.getBundle("com.kms.katalon.composer.components.impl");

    // JobViewerPart
    public static final Image IMG_16_DONE = ImageUtil.loadImage(currentBundle, "/icons/done_16.png");

    public static final Image IMG_16_WATCH = ImageUtil.loadImage(currentBundle, "/icons/watch_16.png");

    public static final Image IMG_16_STOP = ImageUtil.loadImage(currentBundle, "/icons/stop_16.png");

    public static final Image IMG_16_WAIT = ImageUtil.loadImage(currentBundle, "/icons/wait_16.png");

    public static final Image IMG_16_TERMINATE = ImageUtil.loadImage(currentBundle, "/icons/terminate_16.png");

    public static final Image IMG_16_PAUSE = ImageUtil.loadImage(currentBundle, "/icons/pause_16.png");

    public static final Image IMG_16_PLAY = ImageUtil.loadImage(currentBundle, "/icons/play_16.png");

    public static final String PATH_16_LOADING = "/icons/loading_16.gif";

    public static final Image IMG_16_DEBUG = ImageUtil.loadImage(Platform.getBundle("org.eclipse.debug.ui"),
            "/icons/full/eview16/debug_view.png");

    // LogViewerPart
    public static final Image IMG_16_EXPAND_ALL = ImageUtil.loadImage(Platform.getBundle("org.eclipse.ui"),
            "icons/full/elcl16/expandall.png");

    public static final Image IMG_16_PREVIOUS_FAILURE = ImageUtil.loadImage(Platform.getBundle("org.eclipse.team.ui"),
            "icons/full/elcl16/prev_nav.gif");

    public static final Image IMG_16_NEXT_FAILURE = ImageUtil.loadImage(Platform.getBundle("org.eclipse.team.ui"),
            "icons/full/elcl16/next_nav.gif");

    public static final Image IMG_16_LOGVIEW_ALL = ImageUtil.loadImage(currentBundle, "/icons/logview_all_16.png");

    public static final Image IMG_16_LOGVIEW_INFO = ImageUtil.loadImage(currentBundle, "/icons/logview_info_16.png");

    public static final Image IMG_16_LOGVIEW_PASSED = ImageUtil.loadImage(currentBundle, "/icons/logview_passed_16.png");

    public static final Image IMG_16_LOGVIEW_FAILED = ImageUtil.loadImage(currentBundle, "/icons/logview_failed_16.png");

    public static final Image IMG_16_LOGVIEW_ERROR = ImageUtil.loadImage(currentBundle, "/icons/logview_error_16.png");

    public static final Image IMG_16_LOGVIEW_NOT_RUN = ImageUtil.loadImage(currentBundle,
            "/icons/logview_not_run_16.png");

    public static final Image IMG_16_LOGVIEW_WARNING = ImageUtil.loadImage(currentBundle,
            "/icons/logview_warning_16.png");

    // ExternalLibratiesSettingPage
    public static final Image IMG_16_EXTERNAL_LIBRARY = ImageUtil.loadImage(Platform.getBundle("org.eclipse.jdt.ui"),
            "icons/full/obj16/jar_obj.gif");
    public static final Image IMG_24_ADD = ImageUtil.loadImage(componentBundle, "/icons/add_24.png");

    public static final Image IMG_24_REMOVE = ImageUtil.loadImage(componentBundle, "/icons/remove_24.png");
}
