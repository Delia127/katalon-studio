package com.kms.katalon.constants;

public class EventConstants {
    // Property's name of event that is broadcast by IEventBroker
    public static final String EVENT_DATA_PROPERTY_NAME = "org.eclipse.e4.data";

    // Workspace events
    public static final String WORKSPACE_CREATED = "WORKSPACE/CREATED";

    // Project Events
    public static final String PROJECT_ALL = "PROJECT/*";
    public static final String PROJECT_CREATED = "PROJECT/CREATED";
    public static final String PROJECT_OPEN = "PROJECT/OPEN";
    public static final String PROJECT_OPENED = "PROJECT/OPENED";
    public static final String PROJECT_CLOSE = "PROJECT/CLOSE";
    public static final String PROJECT_CLOSED = "PROJECT/CLOSED";
    public static final String PROJECT_UPDATED = "PROJECT/UPDATED";

    // Eclipse Events
    public static final String ECLIPSE_EDITOR_CLOSED = "EDITORS_CLOSED";
    public static final String ECLIPSE_EDITOR_SAVED = "EDITORS_SAVED";

    // Explorer Events
    public static final String EXPLORER_RELOAD_INPUT = "EXPLORER/RELOAD_INPUT";
    public static final String EXPLORER_SET_SELECTED_ITEM = "EXPLORER/SET_SELECTED_ITEM";
    public static final String EXPLORER_SHOW_ITEM = "EXPLORER/SHOW_ITEM";
    public static final String EXPLORER_REFRESH = "EXPLORER/REFRESH";
    public static final String EXPLORER_REFRESH_TREE_ENTITY = "EXPLORER/REFRESH_TREE_ENTITY";
    public static final String EXPLORER_EXPAND_TREE_ENTITY = "EXPLORER/EXPAND_TREE_ENTITY";
    public static final String EXPLORER_OPEN_SELECTED_ITEM = "EXPLORER/OPEN_SELECTED_ITEM";
    public static final String EXPLORER_DELETE_SELECTED_ITEM = "EXPLORER/DELETE_SELECTED_ITEM";
    public static final String EXPLORER_DELETED_SELECTED_ITEM = "EXPLORER/DELETED_SELECTED_ITEM";
    public static final String EXPLORER_RENAME_SELECTED_ITEM = "EXPLORER/RENAME_SELECTED_ITEM";
    public static final String EXPLORER_RENAMED_SELECTED_ITEM = "EXPLORER/RENAMED_SELECTED_ITEM";
    public static final String EXPLORER_PASTE_SELECTED_ITEM = "EXPLORER/PASTE_SELECTED_ITEM";
    public static final String EXPLORER_COPY_PASTED_SELECTED_ITEM = "EXPLORER/COPY_PASTED_SELECTED_ITEM";
    public static final String EXPLORER_CUT_PASTED_SELECTED_ITEM = "EXPLORER/CUT_PASTED_SELECTED_ITEM";
    public static final String EXPLORER_FILTER_ITEM = "EXPLORER/FILTER_ITEM";
    public static final String EXPLORER_HIGHLIGH_FILTER_ITEM = "EXPLORER/HIGHLIGH_FILTER_ITEM";
    public static final String EXPLORER_REFRESH_SELECTED_ITEM = "EXPLORER/REFRESH_SELECTED_ITEM";
    public static final String EXPLORER_REFRESH_ALL_ITEMS = "EXPLORER/REFRESH_ALL_ITEMS";
    public static final String EXPLORER_COLLAPSE_ALL_ITEMS = "EXPLORER/COLLAPSE_ALL_ITEMS";
    public static final String EXPLORER_LINK_ITEM_WITH_SELECTED_PART = "EXPLORER/LINK_ITEM_WITH_SELECTED_PART";

    
    public static final String EXPLORER_DELETE_TEST_CASE_FOLDER = "EXPLORER/DELETE_TEST_CASE_FOLDER";
    
    // Folder Events
    public static final String FOLDER_REFRESH_CHILDREN = "EXPLORER/REFRESH_CHILDREN";

    // TestCase Events
    public static final String TESTCASE_ALL = "TESTCASE/*";
    public static final String TESTCASE_OPEN = "TESTCASE/OPEN";
    public static final String TESTCASE_SAVE = "TESTCASE/SAVE";
    public static final String TESTCASE_UPDATED = "TESTCASE/UPDATED";
    public static final String TESTCASE_SAVE_SCRIPT = "TESTCASE/SAVE_SCRIPT";
    public static final String TESTCASE_REFRESH_EDITOR = "TESTCASE/UPDATE_SCRIPT";

    // TestSuite Events
    public static final String TEST_SUITE = "TESTSUITE/*";
    public static final String TEST_SUITE_OPEN = "TESTSUITE/OPEN";
    public static final String TEST_SUITE_SAVE = "TESTSUITE/SAVE";
    public static final String TEST_SUITE_UPDATED = "TESTSUITE/UPDATED";

    // TestData Events
    public static final String TEST_DATA_OPEN = "TESTDATA/OPEN";
    public static final String TEST_DATA_UPDATED = "TESTDATA/UPDATE";

    // TestObject Events
    public static final String TEST_OBJECT_OPEN = "TESTOBJECT/OPEN";
    public static final String TEST_OBJECT_SAVE = "TESTOBJECT/SAVE";
    public static final String TEST_OBJECT_UPDATED = "TESTOBJECT/UPDATED";

    public static final String WEBSERVICE_REQUEST_OBJECT_OPEN = "WSOBJECT/OPEN";

    // Report Event
    public static final String REPORT_OPEN = "REPORT/OPEN";
    public static final String REPORT_UPDATED = "REPORT/UPDATED";
    public static final String REPORT_DELETED= "REPORT/DELETED";

    // Keyword Events
    public static final String GROOVY_REFRESH_PROJECT = "GROOVY/REFRESH_PROJECT";

    // Import Events
    public static final String IMPORT_DUPLICATE_ENTITY_RESULT = "IMPORT/DUPLICATE_ENTITY_RESULT";
    public static final String EXECUTION_LOGGING_EVENT = "EXECUTION/LOGGING_EVENT";

    // Console Log Events
    public static final String CONSOLE_LOG_OPEN = "CONSOLE_LOG/OPEN";
    public static final String CONSOLE_LOG_RESET = "CONSOLE_LOG/RESET";
    public static final String CONSOLE_LOG_ADD_ITEMS = "CONSOLE_LOG/ADD_ITEMS";
    public static final String CONSOLE_LOG_REFRESH = "CONSOLE_LOG/REFRESH";
    public static final String CONSOLE_LOG_UPDATE_PROGRESS_BAR = "CONSOLE_LOG/UPDATE_PROGRESS_BAR";
    public static final String CONSOLE_LOG_CHANGE_VIEW_TYPE = "CONSOLE_LOG/CHANGE_VIEW_TYPE";
    public static final String CONSOLE_LOG_WORD_WRAP = "CONSOLE_LOG/WORD_WRAP";

    // Job Events
    public static final String JOB_REFRESH = "JOB/REFRESH";
    public static final String JOB_UPDATE_PROGRESS = "JOB/UPDATE_PROGRESS";
    public static final String JOB_COMPLETED = "JOB/COMPLETED";

    // Object Spy Events
    public static final String OBJECT_SPY_ELEMENT_ADDED = "OBJECT_SPY/ELEMENT_ADDED";
    public static final String OBJECT_SPY_ELEMENT_DOM_MAP_ADDED = "OBJECT_SPY/ELEMENT_DOM_MAP_ADDED";
    public static final String OBJECT_SPY_RESET_SELECTED_TARGET = "OBJECT_SPY/RESET_SELECTED_TARGET";
    public static final String OBJECT_SPY_REFRESH_SELECTED_TARGET = "OBJECT_SPY/REFRESH_SELECTED_TARGET";
    public static final String OBJECT_SPY_TEST_OBJECT_ADDED = "OBJECT_SPY/TEST_OBJECT_ADDED";
    public static final String OBJECT_SPY_CLOSE_MOBILE_APP = "OBJECT_SPY/CLOSE_MOBILE_APP";
    public static final String OBJECT_SPY_MOBILE_HIGHLIGHT = "OBJECT_SPY/MOBILE_HIGHLIGHT";
    public static final String OBJECT_SPY_MOBILE_SCREEN_CAPTURE = "OBJECT_SPY/MOBILE_SCREEN_CAPTURE";
    public static final String OBJECT_SPY_ENSURE_DEVICE_VIEW_DIALOG = "OBJECT_SPY/ENSURE_DEVICE_VIEW_DIALOG";

    // Recorder Events
    public static final String RECORDER_ELEMENT_ADDED = "RECORDER/ELEMENT_ADDED";

    // Global Variable Events
    public static final String GLOBAL_VARIABLE_REFRESH = "GLOBAL_VARIABLE/REFRESH";
    public static final String GLOBAL_VARIABLE_SHOW_REFERENCES = "GLOBAL_VARIABLE/SHOW_REFERENCES";

}
