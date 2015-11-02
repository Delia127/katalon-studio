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
    public static final Image IMG_16_CLOSE_SEARCH = ImageUtil.loadImage(currentBundle, "/icons/close_search_16.png");

    // FolderTreeEntity
    public static final Image IMG_16_FOLDER = ImageUtil.loadImage(currentBundle, "/icons/folder_16.png");
    public static final Image IMG_16_FOLDER_TEST_CASE = ImageUtil.loadImage(currentBundle,
            "/icons/folder_test_case_16.png");
    public static final Image IMG_16_FOLDER_TEST_SUITE = ImageUtil.loadImage(currentBundle,
            "/icons/folder_test_suite_16.png");
    public static final Image IMG_16_FOLDER_KEYWORD = ImageUtil
            .loadImage(currentBundle, "/icons/folder_keyword_16.png");
    public static final Image IMG_16_FOLDER_DATA = ImageUtil.loadImage(currentBundle, "/icons/folder_data_16.png");
    public static final Image IMG_16_FOLDER_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/folder_object_16.png");
    public static final Image IMG_16_FOLDER_REPORT = ImageUtil.loadImage(currentBundle, "/icons/folder_report_16.png");

    // KeywordTreeEntity
    public static final Image IMG_16_KEYWORD = ImageUtil.loadImage(currentBundle, "/icons/keyword_16.png");

    // PackageTreeEntity
    public static final Image IMG_16_PACKAGE = ImageUtil.loadImage(currentBundle, "/icons/package_16.png");

    // ReportTreeEntity
    public static final Image IMG_16_REPORT = ImageUtil.loadImage(currentBundle, "/icons/report_16.png");

    // TestCaseTreeEntity
    public static final Image IMG_16_TEST_CASE = ImageUtil.loadImage(currentBundle, "/icons/test_case_16.png");

    // TestDataTreeEntity
    public static final Image IMG_16_TEST_DATA = ImageUtil.loadImage(currentBundle, "/icons/test_data_16.png");

    // TestSuiteTreeEntity
    public static final Image IMG_16_TEST_SUITE = ImageUtil.loadImage(currentBundle, "/icons/test_suite_16.png");

    // WebElementTreeEntity
    public static final Image IMG_16_TEST_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/test_object_16.png");
    public static final Image IMG_16_WS_TEST_OBJECT = ImageUtil.loadImage(currentBundle, "/icons/ws_test_object_16.png");

    // Some common images
    public static final Image IMG_16_CHECKED = ImageUtil.loadImage(currentBundle, "/icons/checkbox_checked_16.png");
    public static final Image IMG_16_UNCHECKED = ImageUtil.loadImage(currentBundle, "/icons/checkbox_unchecked_16.png");
    public static final Image IMG_16_NOTIFICATION_HEADER = ImageUtil.loadImage(currentBundle,
            "/icons/notification_16.gif");
    public static final URL URL_16_LOADING = currentBundle.getEntry("/icons/loading_16.gif");
}
