package com.kms.katalon.composer.testcase.constants;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.util.ImageUtil;

public class ImageConstants {
    private static final Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);

    // OpenTestCaseHandler
    public static final String IMG_16_TEST_CASE_PATH = "/icons/test_case_16.png";

    public static final String URL_16_TEST_CASE = ImageUtil.getImageUrl(currentBundle, IMG_16_TEST_CASE_PATH);

    // TestCaseCompositePart
    public static final Image IMG_16_MANUAL = ImageUtil.loadImage(currentBundle, "/icons/manual_16.png");

    public static final Image IMG_16_SCRIPT = ImageUtil.loadImage(currentBundle, "/icons/script_16.png");

    public static final Image IMG_16_VARIABLE = ImageUtil.loadImage(currentBundle, "/icons/variable_16.png");

    public static final Image IMG_16_INTEGRATION = ImageUtil.loadImage(currentBundle, "/icons/integration_16.png");

    // TestCasePart
    public static final Image IMG_16_ARROW_UP_BLACK = ImageUtil.loadImage(currentBundle, "/icons/arrow_up_black_16.png");

    public static final Image IMG_16_ARROW_DOWN_BLACK = ImageUtil.loadImage(currentBundle,
            "/icons/arrow_down_black_16.png");

    public static final Image IMG_16_RECORD = ImageUtil.loadImage(currentBundle, "/icons/record_16.png");

    public static final Image IMG_24_ADD = ImageUtil.loadImage(currentBundle, "/icons/add_24.png");

    public static final Image IMG_24_INSERT = ImageUtil.loadImage(currentBundle, "/icons/insert_24.png");

    public static final Image IMG_24_REMOVE = ImageUtil.loadImage(currentBundle, "/icons/remove_24.png");

    public static final Image IMG_24_UP = ImageUtil.loadImage(currentBundle, "/icons/up_24.png");

    public static final Image IMG_24_DOWN = ImageUtil.loadImage(currentBundle, "/icons/down_24.png");

    public static final Image IMG_24_CLEAR = ImageUtil.loadImage(currentBundle, "/icons/clear_24.png");

    // KeywordTreeLabelProvider
    public static final Image IMG_16_FAILED_CONTINUE = ImageUtil.loadImage(currentBundle,
            "/icons/failed_continue_16.png");

    public static final Image IMG_16_FOLDER = ImageUtil.loadImage(currentBundle, "/icons/folder_16.png");

    // AstAbstractKeywordTreeTableNode
    public static final Image IMG_16_FAILED_STOP = ImageUtil.loadImage(currentBundle, "/icons/failed_stop_16.png");

    public static final Image IMG_16_COMMENT = ImageUtil.loadImage(currentBundle, "/icons/comment_16.png");

    public static final Image IMG_16_OPTIONAL_RUN = ImageUtil.loadImage(currentBundle, "/icons/optional_run_16.png");

    // AstAssertStatementTreeTableNode
    public static final Image IMG_16_ASSERT = ImageUtil.loadImage(currentBundle, "/icons/assert_16.png");

    // AstBinaryStatementTreeTableNode
    public static final Image IMG_16_BINARY = ImageUtil.loadImage(currentBundle, "/icons/binary_16.png");

    // AstCallTestCaseKeywordTreeTableNode
    public static final Image IMG_16_CALL_TEST_CASE = ImageUtil.loadImage(currentBundle, "/icons/call_test_case_16.png");

    // AstClassTreeTableNode
    public static final Image IMG_16_FUNCTION = ImageUtil.loadImage(currentBundle, "/icons/function_16.png");

    // AstIfStatementTreeTableNode
    public static final Image IMG_16_IF = ImageUtil.loadImage(currentBundle, "/icons/if_16.png");

    // AstElseIfStatementTreeTableNode
    public static final Image IMG_16_ELSE_IF = ImageUtil.loadImage(currentBundle, "/icons/else_if_16.png");

    // AstElseStatementTreeTableNode
    public static final Image IMG_16_ELSE = ImageUtil.loadImage(currentBundle, "/icons/else_16.png");

    // AstForStatementTreeTableNode
    public static final Image IMG_16_LOOP = ImageUtil.loadImage(currentBundle, "/icons/loop_16.png");

    // KeywordTreeLabelProvider
    public static final Image IMG_16_KEYWORD = ImageUtil.loadImage(currentBundle, "/icons/keyword_16.png");

    // ArgumentInputBuilderDialog
    public static final Image IMG_16_WARN_TABLE_ITEM = PlatformUI.getWorkbench()
            .getSharedImages()
            .getImage(ISharedImages.IMG_OBJS_WARN_TSK);

    public static final Image IMG_KEYWORD_WIKI = ImageUtil.loadImage(currentBundle, "/icons/keyword_wiki.png");
}
