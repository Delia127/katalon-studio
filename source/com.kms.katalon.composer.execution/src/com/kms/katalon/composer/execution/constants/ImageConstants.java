package com.kms.katalon.composer.execution.constants;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.resources.util.ImageUtil;

public class ImageConstants {

    // JobViewerPart
    public static final Image IMG_16_DONE = ImageManager.getImage(IImageKeys.DONE_16);

    public static final Image IMG_16_WATCH = ImageManager.getImage(IImageKeys.WATCH_16);

    public static final Image IMG_16_STOP = ImageManager.getImage(IImageKeys.STOP_16);

    public static final Image IMG_16_WAIT = ImageManager.getImage(IImageKeys.WAIT_16);

    public static final Image IMG_16_TERMINATE = ImageManager.getImage(IImageKeys.TERMINATE_16);

    public static final Image IMG_16_PAUSE = ImageManager.getImage(IImageKeys.PAUSE_16);

    public static final Image IMG_16_PLAY = ImageManager.getImage(IImageKeys.PLAY_16);

    public static final URL URL_16_LOADING = ImageManager.getImageURL(IImageKeys.LOADING_16);

    public static final Image IMG_16_DEBUG = ImageManager.getImage(IImageKeys.DEBUG_PERSPECTIVE_16);

    // LogViewerPart
    public static final Image IMG_16_COLLAPSE_ALL = ImageManager.getImage(IImageKeys.COLLAPSE_16);

    public static final Image IMG_16_EXPAND_ALL = ImageManager.getImage(IImageKeys.EXPAND_16);

    public static final Image IMG_16_PREVIOUS_FAILURE = ImageManager.getImage(IImageKeys.MOVE_UP_16);

    public static final Image IMG_16_NEXT_FAILURE = ImageManager.getImage(IImageKeys.MOVE_DOWN_16);

    public static final Image IMG_16_LOGVIEW_ALL = ImageManager.getImage(IImageKeys.LOG_ALL_16);

    public static final Image IMG_16_LOGVIEW_INFO = ImageManager.getImage(IImageKeys.LOG_INFO_16);

    public static final Image IMG_16_LOGVIEW_PASSED = ImageManager.getImage(IImageKeys.LOG_PASSED_16);

    public static final Image IMG_16_LOGVIEW_FAILED = ImageManager.getImage(IImageKeys.LOG_FAILED_16);

    public static final Image IMG_16_LOGVIEW_ERROR = ImageManager.getImage(IImageKeys.LOG_ERROR_16);

    public static final Image IMG_16_LOGVIEW_NOT_RUN = ImageManager.getImage(IImageKeys.LOG_NOT_RUN_16);

    public static final Image IMG_16_LOGVIEW_WARNING = ImageManager.getImage(IImageKeys.LOG_WARNING_16);

    // ExternalLibratiesSettingPage
    public static final Image IMG_16_EXTERNAL_LIBRARY = ImageUtil.loadImage(Platform.getBundle("org.eclipse.jdt.ui"),
            "icons/full/obj16/jar_obj.png");

    public static final Image IMG_24_ADD = ImageManager.getImage(IImageKeys.ADD_16);

    public static final Image IMG_24_REMOVE = ImageManager.getImage(IImageKeys.DELETE_16);

    public static final String IMG_URL_16_CUSTOM = ImageManager.getImageURLString(IImageKeys.CUSTOM_EXECUTION_16);
    
    // GenerateCommandDialog
    public static final Image IMG_16_EDIT = ImageManager.getImage(IImageKeys.EDIT_16);

}
