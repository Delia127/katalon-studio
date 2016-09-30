package com.kms.katalon.composer.components.impl.constants;

import java.net.URL;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

    // TreeEntitySelectionDialog
    public static final Image IMG_16_SEARCH = ImageUtil.loadImage(currentBundle, "/icons/search_16.png");

    public static final Image IMG_16_ADVANCED_SEARCH = ImageUtil.loadImage(currentBundle,
            "/icons/advanced_search_16.png");

    public static final Image IMG_16_CLOSE_SEARCH = ImageUtil.loadImage(currentBundle, "/icons/close_search_16.png");

    // FolderTreeEntity
    public static final Image IMG_16_FOLDER = ImageUtil.loadImage(currentBundle, "/icons/folder_16.png");

    public static final Image IMG_16_FOLDER_TEST_CASE = ImageUtil.loadImage(currentBundle,
            "/icons/folder_test_case_16.png");

    public static final Image IMG_16_FOLDER_TEST_SUITE = ImageUtil.loadImage(currentBundle,
            "/icons/folder_test_suite_16.png");

    public static final Image IMG_16_FOLDER_KEYWORD = ImageUtil.loadImage(currentBundle, "/icons/folder_keyword_16.png");

    public static final Image IMG_16_FOLDER_DATA = ImageUtil.loadImage(currentBundle, "/icons/folder_data_16.png");

    public static final Image IMG_16_FOLDER_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/folder_object_16.png");

    public static final Image IMG_16_FOLDER_REPORT = ImageUtil.loadImage(currentBundle, "/icons/folder_report_16.png");

    public static final Image IMG_16_FOLDER_CHECKPOINT = ImageUtil.loadImage(currentBundle,
            "/icons/folder_checkpoint_16.png");

    // KeywordTreeEntity
    public static final Image IMG_16_KEYWORD = ImageUtil.loadImage(currentBundle, "/icons/keyword_16.png");

    // PackageTreeEntity
    public static final Image IMG_16_PACKAGE = ImageUtil.loadImage(currentBundle, "/icons/package_16.png");

    // ReportTreeEntity
    public static final Image IMG_16_REPORT = ImageUtil.loadImage(currentBundle, "/icons/report_16.png");

    // ReportCollectionTreeEntity
    public static final Image IMG_16_REPORT_COLLECTION = ImageUtil.loadImage(currentBundle, "/icons/report_list_16.png");

    // TestCaseTreeEntity
    public static final Image IMG_16_TEST_CASE = ImageUtil.loadImage(currentBundle, "/icons/test_case_16.png");

    // TestDataTreeEntity
    public static final Image IMG_16_TEST_DATA = ImageUtil.loadImage(currentBundle, "/icons/test_data_16.png");

    // TestSuiteTreeEntity
    public static final Image IMG_16_TEST_SUITE = ImageUtil.loadImage(currentBundle, "/icons/test_suite_16.png");

    // TestRunTreeEntity
    public static final Image IMG_16_TEST_SUITE_COLLECTION = ImageUtil.loadImage(currentBundle,
            "/icons/test_suite_collection_16.png");

    // WebElementTreeEntity
    public static final Image IMG_16_TEST_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/test_object_16.png");

    public static final Image IMG_16_WS_TEST_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/ws_test_object_16.png");

    // Some common images
    public static final Image IMG_16_CHECKED = ImageUtil.loadImage(currentBundle, "/icons/checkbox_checked_16.png");

    public static final Image IMG_16_UNCHECKED = ImageUtil.loadImage(currentBundle, "/icons/checkbox_unchecked_16.png");

    public static final Image IMG_16_NOTIFICATION_HEADER = ImageUtil.loadImage(currentBundle,
            "/icons/notification_16.gif");

    public static final URL URL_16_LOADING = currentBundle.getEntry("/icons/loading_16.gif");

    public static final Image IMG_16_ADD = ImageUtil.loadImage(currentBundle, "/icons/add_16.png");

    public static final Image IMG_16_REMOVE = ImageUtil.loadImage(currentBundle, "/icons/remove_16.png");

    public static final Image IMG_16_CLEAR = ImageUtil.loadImage(currentBundle, "/icons/clear_16.png");

    public static final Image IMG_16_EDIT = ImageUtil.loadImage(currentBundle, "/icons/edit_16.png");

    public static final Image IMG_16_REFRESH = ImageUtil.loadImage(currentBundle, "/icons/refresh_16.png");

    public static final Image IMG_24_ADD = ImageUtil.loadImage(currentBundle, "/icons/add_24.png");

    public static final Image IMG_24_CLEAR = ImageUtil.loadImage(currentBundle, "/icons/clear_24.png");

    public static final Image IMG_24_EDIT = ImageUtil.loadImage(currentBundle, "/icons/edit_24.png");

    public static final Image IMG_24_REMOVE = ImageUtil.loadImage(currentBundle, "/icons/remove_24.png");

    public static final Image IMG_16_ARROW_DOWN_BLACK = ImageUtil.loadImage(currentBundle,
            "/icons/arrow_down_black_16.png");

    public static final Image IMG_16_ARROW_UP_BLACK = ImageUtil.loadImage(currentBundle, "/icons/arrow_up_black_16.png");

    // CheckpointTreeEntity
    public static final Image IMG_16_CHECKPOINT = ImageUtil.loadImage(currentBundle, "/icons/checkpoint_16.png");

    public static final String URL_16_CHECKPOINT = ImageUtil.getImageUrl(currentBundle, "/icons/checkpoint_16.png");
    
    //AbstractEntityDialog
    public static final Image IMG_20_INFO_MSG = ImageUtil.loadImage(currentBundle, "/icons/info_20.png");
    
    public static final Image IMG_20_WARNING_MSG = ImageUtil.loadImage(currentBundle, "/icons/warning_20.png");

    public static final Image IMG_20_ERROR_MSG = ImageUtil.loadImage(currentBundle, "/icons/error_20.png");
    

}
