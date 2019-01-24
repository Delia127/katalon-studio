package com.kms.katalon.composer.resources.processor;

import org.eclipse.e4.core.di.annotations.Execute;

import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;

public class ImageProcessor {

    @Execute
    public void run() {
        registerIntroImages();
        registerWelcomeImages();
        registerDialogImages();
        registerExecutionImages();
        registerLogImages();
        registerToolbarImages();
        registerTestCaseStepImages();
        registerEntityImages();
        registerSubToolbarImages();
        registerCommonImages();
        registerRecorderImages();
        registerHistoryRequestImages();
        registerApiQuickStartImages();
        registerInAppSurveyImages();
    }
    
    private void registerInAppSurveyImages(){
    	ImageManager.registerImage(IImageKeys.STAR);
    }
    
    private void registerApiQuickStartImages() {
        ImageManager.registerImage(IImageKeys.API_QUICKSTART_BACKGROUND);
        ImageManager.registerImage(IImageKeys.NEW_DRAFT_REST_REQUEST);
        ImageManager.registerImage(IImageKeys.NEW_DRAFT_SOAP_REQUEST);
        ImageManager.registerImage(IImageKeys.IMPORT_SWAGGER);
        ImageManager.registerImage(IImageKeys.IMPORT_WSDL);
        ImageManager.registerImage(IImageKeys.API_QUICKSTART_BACKGROUND_LEFT);
        ImageManager.registerImage(IImageKeys.API_QUICKSTART_BACKGROUND_WEB_LEFT);
        ImageManager.registerImage(IImageKeys.API_QUICKSTART_BACKGROUND_MOBILE_LEFT);
    }

    private void registerHistoryRequestImages() {
        ImageManager.registerImage(IImageKeys.HISTORY_REQUEST_16);
    }

    private void registerRecorderImages() {
        ImageManager.registerImage(IImageKeys.START_RECORDING_24);
        ImageManager.registerImage(IImageKeys.PAUSE_RECORDING_24);
        ImageManager.registerImage(IImageKeys.RESUME_RECORDING_24);
        ImageManager.registerImage(IImageKeys.STOP_RECORDING_24);
    }

    private void registerCommonImages() {
        ImageManager.registerImage(IImageKeys.LOGO_16);
        ImageManager.registerImage(IImageKeys.ACTIVE_16);
        ImageManager.registerImage(IImageKeys.ALERT_16);
        ImageManager.registerImage(IImageKeys.ATTACHMENT_16);
        ImageManager.registerImage(IImageKeys.BUG_16);
        ImageManager.registerImage(IImageKeys.BUG_DISABLED_16);
        ImageManager.registerImage(IImageKeys.CHECKBOX_CHECKED_16);
        ImageManager.registerImage(IImageKeys.CHECKBOX_UNCHECKED_16);
        ImageManager.registerImage(IImageKeys.CHEVRON_DOWN_16);
        ImageManager.registerImage(IImageKeys.CHEVRON_RIGHT_16);
        ImageManager.registerImage(IImageKeys.CLEAN_PROJECT_16);
        ImageManager.registerImage(IImageKeys.CUSTOM_EXECUTION_16);
        ImageManager.registerImage(IImageKeys.DEBUG_PERSPECTIVE_16);
        ImageManager.registerImage(IImageKeys.DESKTOP_16);
        ImageManager.registerImage(IImageKeys.OK_16);
        ImageManager.registerImage(IImageKeys.DONE_16);
        ImageManager.registerImage(IImageKeys.GLOBAL_VARIABLE_16);
        ImageManager.registerImage(IImageKeys.INACTIVE_16);
        ImageManager.registerImage(IImageKeys.JIRA_ACTIVE_16);
        ImageManager.registerImage(IImageKeys.JIRA_INACTIVE_16);
        ImageManager.registerImage(IImageKeys.KEYWORD_WIKI_16);
        ImageManager.registerImage(IImageKeys.KOBITON_16);
        ImageManager.registerImage(IImageKeys.KATALON_ANALYTICS_16);
        ImageManager.registerImage(IImageKeys.NEW_PROJECT_16);
        ImageManager.registerImage(IImageKeys.OPEN_PROJECT_16);
        ImageManager.registerImage(IImageKeys.PROGRESS_16);
        ImageManager.registerImage(IImageKeys.QTEST_ACTIVE_16);
        ImageManager.registerImage(IImageKeys.QTEST_INACTIVE_16);
        ImageManager.registerImage(IImageKeys.QTEST_TEST_CASE_16);
        ImageManager.registerImage(IImageKeys.QTEST_FOLDER_16);
        ImageManager.registerImage(IImageKeys.QTEST_TEST_SUITE_16);
        ImageManager.registerImage(IImageKeys.QUIT_16);
        ImageManager.registerImage(IImageKeys.SEARCH_16);
        ImageManager.registerImage(IImageKeys.TAB_INTEGRATION_16);
        ImageManager.registerImage(IImageKeys.TAB_MAIN_16);
        ImageManager.registerImage(IImageKeys.TAB_MANUAL_16);
        ImageManager.registerImage(IImageKeys.TAB_SCRIPT_16);
        ImageManager.registerImage(IImageKeys.TAB_VARIABLE_16);
        ImageManager.registerImage(IImageKeys.TEMPLATE_API_16);
        ImageManager.registerImage(IImageKeys.TEMPLATE_MOBILE_16);
        ImageManager.registerImage(IImageKeys.TEMPLATE_WEB_16);
        ImageManager.registerImage(IImageKeys.TEST_DATA_TYPE_MANY_16);
        ImageManager.registerImage(IImageKeys.TEST_DATA_TYPE_ONE_16);
        ImageManager.registerImage(IImageKeys.UPLOADED_16);
        ImageManager.registerImage(IImageKeys.UPLOADING_16);
        ImageManager.registerImage(IImageKeys.WEB_FRAME_ELEMENT_16);
        ImageManager.registerImage(IImageKeys.WEB_PAGE_ELEMENT_16);
        ImageManager.registerImage(IImageKeys.ADVANCE_SEARCH_16);
        ImageManager.registerImage(IImageKeys.ALL_16);
        ImageManager.registerImage(IImageKeys.CLEAR_FIELD_16);
        ImageManager.registerImage(IImageKeys.LOADING_16);
        ImageManager.registerImage(IImageKeys.INFO_16);
        ImageManager.registerImage(IImageKeys.WARNING_16);
        ImageManager.registerImage(IImageKeys.ERROR_16);
        ImageManager.registerImage(IImageKeys.COLLAPSE_16);
        ImageManager.registerImage(IImageKeys.EXPAND_16);
        ImageManager.registerImage(IImageKeys.GIT_16);
        ImageManager.registerImage(IImageKeys.PROPERTIES_16);
        ImageManager.registerImage(IImageKeys.FOCUS_16);
        ImageManager.registerImage(IImageKeys.UNDO_16);
        ImageManager.registerImage(IImageKeys.REDO_16);
        ImageManager.registerImage(IImageKeys.HELP_16);
        ImageManager.registerImage(IImageKeys.SAMPLE_WEB_UI_16);
        ImageManager.registerImage(IImageKeys.SAMPLE_MOBILE_16);
        ImageManager.registerImage(IImageKeys.SAMPLE_WEB_SERVICE_16);
        ImageManager.registerImage(IImageKeys.VIDEO_16);
        ImageManager.registerImage(IImageKeys.IMPORT_16);
        ImageManager.registerImage(IImageKeys.CHECK_16);
        ImageManager.registerImage(IImageKeys.FIT_SCREEN_16);
        ImageManager.registerImage(IImageKeys.FULL_SIZE_16);
        ImageManager.registerImage(IImageKeys.CONFLICT_ELEMENT_STATUS_16);
        ImageManager.registerImage(IImageKeys.NEW_ELEMENT_STATUS_16);
        ImageManager.registerImage(IImageKeys.FORUM_SEARCH_BOX);
        ImageManager.registerImage(IImageKeys.FORUM_SEARCH_ICON);
    }

    private void registerSubToolbarImages() {
        ImageManager.registerImage(IImageKeys.ADD_16);
        ImageManager.registerImage(IImageKeys.CLEAR_16);
        ImageManager.registerImage(IImageKeys.DELETE_16);
        ImageManager.registerImage(IImageKeys.EDIT_16);
        ImageManager.registerImage(IImageKeys.HIGHLIGHT_16);
        ImageManager.registerImage(IImageKeys.INSERT_16);
        ImageManager.registerImage(IImageKeys.LOCK_16);
        ImageManager.registerImage(IImageKeys.MAXIMIZE_16);
        ImageManager.registerImage(IImageKeys.MINIMIZE_16);
        ImageManager.registerImage(IImageKeys.MOBILE_16);
        ImageManager.registerImage(IImageKeys.MOVE_DOWN_16);
        ImageManager.registerImage(IImageKeys.MOVE_UP_16);
        ImageManager.registerImage(IImageKeys.MOVE_LEFT_16);
        ImageManager.registerImage(IImageKeys.MOVE_RIGHT_16);
        ImageManager.registerImage(IImageKeys.PLAY_16);
        ImageManager.registerImage(IImageKeys.PAUSE_16);
        ImageManager.registerImage(IImageKeys.REFRESH_16);
        ImageManager.registerImage(IImageKeys.RESTORE_16);
        ImageManager.registerImage(IImageKeys.RECORD_16);
        ImageManager.registerImage(IImageKeys.STOP_16);
        ImageManager.registerImage(IImageKeys.TERMINAL_16);
        ImageManager.registerImage(IImageKeys.TERMINATE_16);
        ImageManager.registerImage(IImageKeys.TREE_16);
        ImageManager.registerImage(IImageKeys.WATCH_16);
        ImageManager.registerImage(IImageKeys.UNWATCH_16);
        ImageManager.registerImage(IImageKeys.MAP_ALL_16);
        ImageManager.registerImage(IImageKeys.RECENT_16);

        // Sub-toolbar disabled icons
        ImageManager.registerImage(IImageKeys.CLEAR_DISABLED_16);
        ImageManager.registerImage(IImageKeys.DELETE_DISABLED_16);
        ImageManager.registerImage(IImageKeys.EDIT_DISABLED_16);
        ImageManager.registerImage(IImageKeys.REFRESH_DISABLED_16);

        // Sub-toolbar icon 24
        ImageManager.registerImage(IImageKeys.ADD_TO_OBJECT_REPOSITORY_24);
        ImageManager.registerImage(IImageKeys.CAPTURE_24);
        ImageManager.registerImage(IImageKeys.DELETE_24);
        ImageManager.registerImage(IImageKeys.HIGHLIGHT_24);
        ImageManager.registerImage(IImageKeys.NEW_ELEMENT_24);
        ImageManager.registerImage(IImageKeys.NEW_FRAME_ELEMENT_24);
        ImageManager.registerImage(IImageKeys.NEW_PAGE_ELEMENT_24);
        ImageManager.registerImage(IImageKeys.PAUSE_24);
        ImageManager.registerImage(IImageKeys.START_DEVICE_24);
        ImageManager.registerImage(IImageKeys.STOP_DEVICE_24);

        // Sub-toolbar icon 24 disabled icons
        ImageManager.registerImage(IImageKeys.ADD_TO_OBJECT_REPOSITORY_DISABLED_24);
        ImageManager.registerImage(IImageKeys.CAPTURE_DISABLED_24);
        ImageManager.registerImage(IImageKeys.DELETE_DISABLED_24);
        ImageManager.registerImage(IImageKeys.HIGHLIGHT_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_ELEMENT_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_FRAME_ELEMENT_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_PAGE_ELEMENT_DISABLED_24);
        ImageManager.registerImage(IImageKeys.PAUSE_DISABLED_24);
        ImageManager.registerImage(IImageKeys.START_DEVICE_DISABLED_24);
        ImageManager.registerImage(IImageKeys.STOP_DEVICE_DISABLED_24);
    }

    private void registerEntityImages() {
        ImageManager.registerImage(IImageKeys.PROJECT_16);
        ImageManager.registerImage(IImageKeys.FOLDER_16);
        ImageManager.registerImage(IImageKeys.FOLDER_CHECKPOINT_16);
        ImageManager.registerImage(IImageKeys.FOLDER_KEYWORD_16);
        ImageManager.registerImage(IImageKeys.FOLDER_REPORT_16);
        ImageManager.registerImage(IImageKeys.FOLDER_TEST_SUITE_16);
        ImageManager.registerImage(IImageKeys.FOLDER_TEST_CASE_16);
        ImageManager.registerImage(IImageKeys.FOLDER_TEST_DATA_16);
        ImageManager.registerImage(IImageKeys.FOLDER_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.CHECKPOINT_16);
        ImageManager.registerImage(IImageKeys.TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.BTN_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.CBX_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.CHK_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.FILE_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.IMG_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.LBL_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.LNK_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.WS_POST_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_GET_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_PUT_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_DELETE_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_CONNECT_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_CUSTOM_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_HEAD_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_OPTIONS_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_TRACE_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_SOAP12_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_SOAP_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_SOAP_GET_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_SOAP_POST_METHOD_16);
        ImageManager.registerImage(IImageKeys.WS_NEW_REST_REQUEST_64);
        ImageManager.registerImage(IImageKeys.WS_NEW_SOAP_REQUEST_64);
        ImageManager.registerImage(IImageKeys.WS_IMPORT_REST_REQUEST_64);
        ImageManager.registerImage(IImageKeys.WS_IMPORT_SOAP_REQUEST_64);
        ImageManager.registerImage(IImageKeys.WS_PATCH_METHOD_16);
        ImageManager.registerImage(IImageKeys.RBT_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.TXT_TEST_OBJECT_16);

        ImageManager.registerImage(IImageKeys.TEST_SUITE_16);
        ImageManager.registerImage(IImageKeys.TEST_SUITE_COLLECTION_16);
        ImageManager.registerImage(IImageKeys.FILTERING_TEST_SUITE_16);
        ImageManager.registerImage(IImageKeys.WS_TEST_OBJECT_16);
        ImageManager.registerImage(IImageKeys.PACKAGE_16);
        ImageManager.registerImage(IImageKeys.REPORT_16);
        ImageManager.registerImage(IImageKeys.REPORT_COLLECTION_16);
        ImageManager.registerImage(IImageKeys.TEST_CASE_16);
        ImageManager.registerImage(IImageKeys.TEST_DATA_16);
        ImageManager.registerImage(IImageKeys.KEYWORD_16);
        ImageManager.registerImage(IImageKeys.PROFILE_FOLDER_ENTITY_16);
        ImageManager.registerImage(IImageKeys.PROFILE_ENTITY_16);
        ImageManager.registerImage(IImageKeys.FEATURE_16);
        ImageManager.registerImage(IImageKeys.FOLDER_FEATURE_16);
        ImageManager.registerImage(IImageKeys.FOLDER_SOURCE_16);
        ImageManager.registerImage(IImageKeys.CONFIG_16);
        ImageManager.registerImage(IImageKeys.GROOVY_16);
    }

    private void registerTestCaseStepImages() {
        ImageManager.registerImage(IImageKeys.ASSERT_16);
        ImageManager.registerImage(IImageKeys.BINARY_16);
        ImageManager.registerImage(IImageKeys.CALL_TEST_CASE_16);
        ImageManager.registerImage(IImageKeys.COMMENT_16);
        ImageManager.registerImage(IImageKeys.ELSE_16);
        ImageManager.registerImage(IImageKeys.ELSE_IF_16);
        ImageManager.registerImage(IImageKeys.FAILED_CONTINUE_16);
        ImageManager.registerImage(IImageKeys.FAILED_STOP_16);
        ImageManager.registerImage(IImageKeys.FUNCTION_16);
        ImageManager.registerImage(IImageKeys.IF_16);
        ImageManager.registerImage(IImageKeys.LOOP_16);
        ImageManager.registerImage(IImageKeys.OPTIONAL_RUN_16);
        ImageManager.registerImage(IImageKeys.WAIT_16);
    }

    private void registerToolbarImages() {
        ImageManager.registerImage(IImageKeys.BUG_24);
        ImageManager.registerImage(IImageKeys.GIT_24);
        ImageManager.registerImage(IImageKeys.MOBILE_OBJECT_SPY_24);
        ImageManager.registerImage(IImageKeys.MOBILE_RECORD_24);
        ImageManager.registerImage(IImageKeys.NEW_24);
        ImageManager.registerImage(IImageKeys.NEW_CHECKPOINT_24);
        ImageManager.registerImage(IImageKeys.NEW_FOLDER_24);
        ImageManager.registerImage(IImageKeys.NEW_KEYWORD_24);
        ImageManager.registerImage(IImageKeys.NEW_PAGKAGE_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_CASE_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_DATA_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_OBJECT_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_SUITE_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_SUITE_COLLECTION_24);
        ImageManager.registerImage(IImageKeys.NEW_WS_TEST_OBJECT_24);
        ImageManager.registerImage(IImageKeys.PLAY_24);
        ImageManager.registerImage(IImageKeys.RECORD_WEB_24);
        ImageManager.registerImage(IImageKeys.REDO_24);
        ImageManager.registerImage(IImageKeys.SAVE_24);
        ImageManager.registerImage(IImageKeys.SAVE_ALL_24);
        ImageManager.registerImage(IImageKeys.STOP_24);
        ImageManager.registerImage(IImageKeys.TERMINAL_24);
        ImageManager.registerImage(IImageKeys.UNDO_24);
        ImageManager.registerImage(IImageKeys.WEB_OBJECT_SPY_24);
        ImageManager.registerImage(IImageKeys.SAVE_16);
        ImageManager.registerImage(IImageKeys.SAVE_ALL_16);
        ImageManager.registerImage(IImageKeys.PROFILE_16);

        // Toolbar disabled icons
        ImageManager.registerImage(IImageKeys.BUG_DISABLED_24);
        ImageManager.registerImage(IImageKeys.GIT_DISABLED_24);
        ImageManager.registerImage(IImageKeys.MOBILE_OBJECT_SPY_DISABLED_24);
        ImageManager.registerImage(IImageKeys.MOBILE_RECORD_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_CHECKPOINT_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_FOLDER_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_KEYWORD_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_PAGKAGE_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_CASE_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_DATA_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_OBJECT_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_SUITE_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_TEST_SUITE_COLLECTION_DISABLED_24);
        ImageManager.registerImage(IImageKeys.NEW_WS_TEST_OBJECT_DISABLED_24);
        ImageManager.registerImage(IImageKeys.PLAY_DISABLED_24);
        ImageManager.registerImage(IImageKeys.RECORD_DISABLED_24);
        ImageManager.registerImage(IImageKeys.REDO_DISABLED_24);
        ImageManager.registerImage(IImageKeys.SAVE_DISABLED_24);
        ImageManager.registerImage(IImageKeys.SAVE_ALL_DISABLED_24);
        ImageManager.registerImage(IImageKeys.STOP_DISABLED_24);
        ImageManager.registerImage(IImageKeys.TERMINAL_DISABLED_24);
        ImageManager.registerImage(IImageKeys.UNDO_DISABLED_24);
        ImageManager.registerImage(IImageKeys.WEB_OBJECT_SPY_DISABLED_24);
        ImageManager.registerImage(IImageKeys.SAVE_DISABLED_16);
        ImageManager.registerImage(IImageKeys.SAVE_ALL_DISABLED_16);
    }

    private void registerLogImages() {
        ImageManager.registerImage(IImageKeys.LOG_16);
        ImageManager.registerImage(IImageKeys.LOG_ALL_16);
        ImageManager.registerImage(IImageKeys.LOG_ERROR_16);
        ImageManager.registerImage(IImageKeys.LOG_FAILED_16);
        ImageManager.registerImage(IImageKeys.LOG_INCOMPLETE_16);
        ImageManager.registerImage(IImageKeys.LOG_INFO_16);
        ImageManager.registerImage(IImageKeys.LOG_NOT_RUN_16);
        ImageManager.registerImage(IImageKeys.LOG_PASSED_16);
        ImageManager.registerImage(IImageKeys.LOG_WARNING_16);
    }

    private void registerExecutionImages() {
        ImageManager.registerImage(IImageKeys.ACTIVE_BROWSER_16);
        ImageManager.registerImage(IImageKeys.CURRENT_SESSION_16);
        ImageManager.registerImage(IImageKeys.NEW_BROWSER_16);
        ImageManager.registerImage(IImageKeys.ANDROID_16);
        ImageManager.registerImage(IImageKeys.APPLE_16);
        ImageManager.registerImage(IImageKeys.CHROME_16);
        ImageManager.registerImage(IImageKeys.CHROME_HEADLESS_16);
        ImageManager.registerImage(IImageKeys.EDGE_16);
        ImageManager.registerImage(IImageKeys.FIREFOX_16);
        ImageManager.registerImage(IImageKeys.FIREFOX_HEADLESS_16);
        ImageManager.registerImage(IImageKeys.IE_16);
        ImageManager.registerImage(IImageKeys.REMOTE_16);
        ImageManager.registerImage(IImageKeys.SAFARI_16);

        ImageManager.registerImage(IImageKeys.ACTIVE_BROWSER_24);
        ImageManager.registerImage(IImageKeys.CURRENT_SESSION_24);
        ImageManager.registerImage(IImageKeys.NEW_BROWSER_24);
        ImageManager.registerImage(IImageKeys.ANDROID_24);
        ImageManager.registerImage(IImageKeys.APPLE_24);
        ImageManager.registerImage(IImageKeys.CHROME_24);
        ImageManager.registerImage(IImageKeys.EDGE_24);
        ImageManager.registerImage(IImageKeys.FIREFOX_24);
        ImageManager.registerImage(IImageKeys.IE_24);
        ImageManager.registerImage(IImageKeys.REMOTE_24);
        ImageManager.registerImage(IImageKeys.SAFARI_24);
    }

    private void registerDialogImages() {
        ImageManager.registerImage(IImageKeys.ERROR_20);
        ImageManager.registerImage(IImageKeys.INFO_20);
        ImageManager.registerImage(IImageKeys.WARNING_20);
    }

    private void registerWelcomeImages() {
        ImageManager.registerImage(IImageKeys.FAQ_34);
        ImageManager.registerImage(IImageKeys.USER_GUIDE_34);
        ImageManager.registerImage(IImageKeys.TUTORIAL_34);
        ImageManager.registerImage(IImageKeys.BUSINESS_SUPPORT_34);
        ImageManager.registerImage(IImageKeys.KATALON_LOGO_202);
        ImageManager.registerImage(IImageKeys.RECENT_PROJECT_FILE_29);
        ImageManager.registerImage(IImageKeys.SAMPLE_WEB_UI_PROJECT_72);
        ImageManager.registerImage(IImageKeys.SAMPLE_MOBILE_PROJECT_72);
        ImageManager.registerImage(IImageKeys.SAMPLE_WEB_SERVICES_PROJECT_72);
        ImageManager.registerImage(IImageKeys.SAMPLE_MORE_72);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_LOG_VIEWER);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_RECORD);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_RUN);
        ImageManager.registerImage(IImageKeys.GRADIENT_LINE_SEPARATOR);
        ImageManager.registerImage(IImageKeys.STEP_1_36);
        ImageManager.registerImage(IImageKeys.STEP_2_36);
        ImageManager.registerImage(IImageKeys.STEP_3_36);
        ImageManager.registerImage(IImageKeys.TAB_FIRST_INACTIVE);
        ImageManager.registerImage(IImageKeys.TAB_FIRST_ACTIVE);
        ImageManager.registerImage(IImageKeys.TAB_MIDDLE_INACTIVE);
        ImageManager.registerImage(IImageKeys.TAB_MIDDLE_ACTIVE);
        ImageManager.registerImage(IImageKeys.TAB_LAST_INACTIVE);
        ImageManager.registerImage(IImageKeys.TAB_LAST_ACTIVE);
        ImageManager.registerImage(IImageKeys.SAMPLE_REMOTE_PROJECT_72);
        ImageManager.registerImage(IImageKeys.GITHUB_LOGO_55);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_MOBILE_RECORD);
        ImageManager.registerImage(IImageKeys.STEP_1_26);
        ImageManager.registerImage(IImageKeys.STEP_2_26);
        ImageManager.registerImage(IImageKeys.STEP_3_26);
        ImageManager.registerImage(IImageKeys.STEP_4_26);
        ImageManager.registerImage(IImageKeys.STEP_5_26);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_MOBILE_CONFIG_AND_RECORD_STEP);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_MOBILE_LOG_VIEWER);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_USE_DRAFT_REQUEST);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_SAVE_DRAFT_REQUEST);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_ADD_REQUEST_TO_TEST_CASE);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_API_LOG_VIEWER);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_SCRIPT_NEW_TEST_CASE);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_ADD_OR_IMPORT_KEYWORDS);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_CREATE_TEST_LISTENER);
        ImageManager.registerImage(IImageKeys.SCREEN_SHOT_BUILD_CMD);
        ImageManager.registerImage(IImageKeys.SCRIPT_BULLET);
    }

    private void registerIntroImages() {
        ImageManager.registerImage(IImageKeys.INTRO_SCREEN_1);
        ImageManager.registerImage(IImageKeys.INTRO_SCREEN_2);
        ImageManager.registerImage(IImageKeys.INTRO_SCREEN_3);
        ImageManager.registerImage(IImageKeys.INTRO_SCREEN_4);
        ImageManager.registerImage(IImageKeys.INTRO_SCREEN_5);
        ImageManager.registerImage(IImageKeys.INTRO_SCREEN_6);
        ImageManager.registerImage(IImageKeys.INTRO_SCREEN_WELCOME);
    }
}
