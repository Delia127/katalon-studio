package com.kms.katalon.constants;

public interface IdConstants {
    // Application
    public static final String APPLICATION_ID = "com.kms.katalon";
    
    // LifeCycleManager
    public static final String KATALON_CONTEXT_ID = "katalonContext";

    // Main Window
    public static final String MAIN_WINDOW_ID = "com.kms.katalon.composer.window.main";

    // Main Perspective Stack
    public static final String MAIN_PERSPECTIVE_STACK_ID = "com.kms.katalon.composer.perspectiveStack";

    // Keyword Perspective
    public static final String KEYWORD_PERSPECTIVE_ID = "com.kms.katalon.composer.perspective.keyword";

    // Debug Perspective
    public static final String DEBUG_PERSPECTIVE_ID = "org.eclipse.debug.ui.DebugPerspective";

    // Eclipse Shared Area
    public static final String SHARE_AREA_ID = "org.eclipse.ui.editorss";

    // Place Holder
    public static final String PLACE_HOLDER_BOTTOM_PART_STACK = "com.kms.katalon.placeholder.bottom";

    // Command
    public static final String QUIT_COMMAND_ID = "com.kms.katalon.composer.quit";

    // Toolbar
    
    public static final String MAIN_TOOLBAR_ID = "com.kms.katalon.composer.toolbar"; 

    public static final String RUN_TOOL_ITEM_ID = "com.kms.katalon.composer.execution.handledtoolitem.run";

    public static final String DEBUG_TOOL_ITEM_ID = "com.kms.katalon.composer.execution.handledtoolitem.debug";

    public static final String PERSPECTIVE_SWITCHER_TOOL_CONTROL_ID = "com.kms.katalon.composer.toolbar.switcher";
    
    public static final String MANAGE_PLUGIN_TOOL_ITEM_ID = "com.kms.katalon.composer.toolbar.plugin.toolitem";

    // Composer PartStack content
    public static final String COMPOSER_CONTENT_PARTSTACK_ID = "com.kms.katalon.composer.content";

    public static final String CONSOLE_PART_STACK_ID = "com.kms.katalon.partstack.console";

    public static final String ECLIPSE_CONSOLE_PART_ID = "org.eclipse.ui.console.ConsoleView";

    public static final String IDE_CONSOLE_LOG_PART_ID = "com.kms.katalon.partstack.console.log";

    public static final String IDE_SEARCH_PART_ID = "org.eclipse.search.ui.views.SearchView";

    public static final String ECLIPSE_EXPRESSION_PART_ID = "org.eclipse.debug.ui.ExpressionView";

    public static final String ECLIPSE_BREAKPOINT_PART_ID = "org.eclipse.debug.ui.BreakpointView";

    public static final String ECLIPSE_VARIABLE_PART_ID = "org.eclipse.debug.ui.VariableView";

    public static final String ECLIPSE_DEBUG_PART_ID = "org.eclipse.debug.ui.DebugView";

    public static final String DEBUG_TOP_LEFT_PART_STACK_ID = "com.kms.katalon.partstack.debug.top.left";

    public static final String DEBUG_TOP_RIGHT_PART_STACK_ID = "com.kms.katalon.partstack.debug.top.right";

    public static final String DEBUG_PLACEHOLDER_ID = "com.kms.katalon.placeholder.debug.top.debug";

    public static final String DEBUG_VARIABLE_PLACEHOLDER_ID = "com.kms.katalon.placeholder.debug.top.variable";

    public static final String DEBUG_BREAKPOINT_PLACEHOLDER_ID = "com.kms.katalon.placeholder.debug.top.breakpoints";

    public static final String DEBUG_EXPRESSION_PLACEHOLDER_ID = "com.kms.katalon.placeholder.debug.top.expressions";
    
    // EventLog Part
    public static final String EVENT_LOG_PART_ID = "com.kms.katalon.placeholder.eventLog";

    // Explorer part content
    public static final String EXPLORER_PART_ID = "com.kms.katalon.composer.part.explorer";

    public static final String EXPLORER_TOOL_ITEM_COLLAPSE_ALL = "com.kms.katalon.composer.explorer.handledtoolitem.collapseAll";

    public static final String EXPLORER_TOOL_ITEM_LINK_PART = "com.kms.katalon.composer.explorer.handledtoolitem.linkPart";

    public static final String WELCOME_PART_ID = "com.kms.katalon.part.welcome";

    // Test Case Part content
    public static final String TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".testcase";

    public static final String TEST_CASE_SUB_PART_STACK_ID_SUFFIX = ".partStack";

    public static final String TEST_CASE_EDITOR_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".editor";
    
    public static final String TEST_CASE_EDITOR_PART_ID_SUFFIX_2 = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".editor2";

    public static final String TEST_CASE_VARIABLES_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".variables";
    

	public static final String TEST_CASE_VARIABLE_EDITOR_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".variableEditor";

    public static final String TEST_CASE_INTEGRATION_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX
            + ".integration";

    public static final String TEST_CASE_PROPERTIES_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".properties";

    public static final String TEST_CASE_GENERAL_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".testCasePart";

    // Test Object Part content
    public static final String TESTOBJECT_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".testobject";
    
    public static final String DRAFT_REQUEST_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".draftRequest";

    // Test Suite Part content
    public static final String TESTSUITE_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".testsuite";

    public static final String TEST_SUITE_SUB_PART_STACK_ID_SUFFIX = ".partStack";

    public static final String TEST_SUITE_INTEGRATION_PART_ID_SUFFIX = TEST_SUITE_SUB_PART_STACK_ID_SUFFIX
            + ".integration";

    public static final String TEST_SUITE_MAIN_PART_ID_SUFFIX = TEST_SUITE_SUB_PART_STACK_ID_SUFFIX + ".testSuitePart";
    
    public static final String TEST_SUITE_SCRIPT_PART_ID_SUFFIX = TEST_SUITE_SUB_PART_STACK_ID_SUFFIX + ".editor";

    // Test Data Part content
    public static final String TESTDATA_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".testdata";

    // Test Data Part content
    public static final String TEST_SUITE_COLLECTION_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID
            + ".testSuiteCollection";

    public static final String FEATURE_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID
            + ".feature";

    // Report Part content
    public static final String REPORT_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".report";

    // Report Collection Part content
    public static final String REPORT_COLLECTION_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID
            + ".collectionReport";

    // Checkpoint Part content
    public static final String CHECKPOINT_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".checkpoint";

    // Test Suite Part: key of TestSuiteEntity in content part
    public static final String TESTSUITE_CONTEXT_OBJECT_KEY = "testSuite";
    
    // Execution Profile content (GlobalVariable part)
    public static final String EXECUTION_PROFILE_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID
            + ".executionProfile";

    // Command ID
    public static final String SAVE_COMMAND_ID = "com.kms.katalon.command.save";

    public static final String SAVE_ALL_COMMAND_ID = "com.kms.katalon.command.saveAll";

    public static final String CLOSE_COMMAND_ID = "com.kms.katalon.command.close";

    public static final String SEARCH_COMMAND_ID = "org.eclipse.search.ui.openSearchDialog";

    public static final String RESET_PERSPECTIVE_HANDLER_ID = "com.kms.katalon.composer.menu.handler.resetPerspective";

    // Parameter ID
    public static final String RUN_MODE_PARAMETER_ID = "com.kms.katalon.composer.execution.runMode";
    
    public static final String EXISTING_SESSION_SESSION_ID_ID = "com.kms.katalon.composer.execution.existingSession.sessionId";
    
    public static final String EXISTING_SESSION_SERVER_URL_ID = "com.kms.katalon.composer.execution.existingSession.serverUrl";
    
    public static final String EXISTING_SESSION_DRIVER_NAME_ID = "com.kms.katalon.composer.execution.existingSession.driverName";
    
    public static final String EXISTING_SESSION_DRIVER_PORT_ID = "com.kms.katalon.composer.execution.existingSession.port";

    // Open Recent Project Command id
    public static final String OPEN_RECENT_PROJECT_COMMAND_ID = "com.kms.katalon.composer.project.command.openrecentproject";

    public static final String OPEN_RECENT_PROJECT_COMMAND_PARAMETER_ID = "com.kms.katalon.composer.project.command.openrecentproject.parameters.project";
    
    // New Sample Local Project Command id
    public static final String NEW_LOCAL_PROJECT_COMMAND_ID = "com.kms.katalon.composer.project.command.newLocalProject";

    public static final String NEW_LOCAL_PROJECT_COMMAND_PARAMETER_TYPE_ID = "com.kms.katalon.composer.project.command.newLocalProject.parameters.project";

    // New Sample Remote Project Command id
    public static final String NEW_REMOTE_PROJECT_COMMAND_ID = "com.kms.katalon.composer.project.command.newRemoteProject";

    public static final String NEW_REMOTE_PROJECT_COMMAND_PARAMETER_ID = "com.kms.katalon.composer.project.command.newRemoteProject.parameters.project";

    // Bundle
    public static final String KATALON_GENERAL_BUNDLE_ID = "com.kms.katalon";

    public static final String KATALON_LOGGING_BUNDLE_ID = "com.kms.katalon.logging";

    public static final String KATALON_CUSTOM_BUNDLE_ID = "com.kms.katalon.custom";

    public static final String KATALON_CORE_BUNDLE_ID = "com.kms.katalon.core";

    public static final String KATALON_WEB_UI_BUNDLE_ID = "com.kms.katalon.core.webui";

    public static final String KATALON_MOBILE_BUNDLE_ID = "com.kms.katalon.core.mobile";

    public static final String KATALON_SELENIUM_BUNDLE_ID = "com.kms.katalon.selenium.server-standalone";

    public static final String KATALON_EXECUTION_BUNDLE_ID = "com.kms.katalon.execution";

    public static final String KATALON_WEB_UI_OBJECT_SPY_BUNDLE_ID = "com.kms.katalon.composer.webui.objectspy";

    public static final String KATALON_WEB_UI_RECORDER_BUNDLE_ID = "com.kms.katalon.composer.webui.recorder";

    public static final String XERCES_BUNDLE_ID = "org.apache.xerces.impl";

    public static final String XML_APIS_BUNDLE_ID = "xml-apis";

    public static final String COMPOSER_EXECUTION_BUNDLE_ID = "com.kms.katalon.composer.execution";

    public static final String WORKBENCH_WINDOW_ID = "org.eclipse.ui.workbench";

    public static final String QTEST_INTEGRATION_BUNDLE_ID = "com.kms.katalon.composer.integration.qtest";
    
    public static final String PLUGIN_DYNAMIC_EXECUTION = "com.katalon.katalon-studio-dynamic-execution-plugin";
    
    public static final String PLUGIN_ADVANCED_TAGS = "com.katalon.tags-plugin";

    // Groovy
    public static final String GROOVY_EDITOR_URI = "org.codehaus.groovy.eclipse.editor.GroovyEditor";
    
    public static final String CUCUMBER_EDITOR_ID = "cucumber.eclipse.editor.editors.Editor";

    public static final String COMPABILITY_EDITOR_ID = "org.eclipse.e4.ui.compatibility.editor";

    // Log viewer part
    public static final String LOG_VIEWER_TOOL_ITEM_ALL_ID = "com.kms.katalon.composer.execution.logviewer.all";

    public static final String LOG_VIEWER_TOOL_ITEM_INFO_ID = "com.kms.katalon.composer.execution.logviewer.info";

    public static final String LOG_VIEWER_TOOL_ITEM_PASSED_ID = "com.kms.katalon.composer.execution.logviewer.passed";

    public static final String LOG_VIEWER_TOOL_ITEM_FAILED_ID = "com.kms.katalon.composer.execution.logviewer.failed";

    public static final String LOG_VIEWER_TOOL_ITEM_ERROR_ID = "com.kms.katalon.composer.execution.logviewer.error";

    public static final String LOG_VIEWER_TOOL_ITEM_TREE_ID = "com.kms.katalon.composer.execution.directtoolitem.tree";

    public static final String LOG_VIEWER_TOOL_ITEM_PIN_ID = "com.kms.katalon.composer.execution.directtoolitem.pin";
    
    public static final String LOG_VIEWER_TOOL_WATCHED_ENTITY_ID = "com.kms.katalon.composer.execution.directtoolitem.logViewer.currentWatched";

    public static final String LOG_VIEWER_MENU_TREEVIEW = "com.kms.katalon.composer.execution.menu.treeview";

    public static final String LOG_VIEWER_MENU_ITEM_WORD_WRAP = "com.kms.katalon.composer.execution.handledmenuitem.wordWrap";

    // Outline Part Stack
    public static final String OUTLINE_PARTSTACK_ID = "com.kms.katalon.partstack.outline";

    public static final String JOBVIEWER_PART_ID = "com.kms.katalon.composer.execution.part.job";

    public static final String OUTLINE_TRIMSTACK_ID = "com.kms.katalon.partstack.outline(com.kms.katalon.composer.window.main).(com.kms.katalon.composer.perspective.keyword)";

    public static final String TOOLBAR_TRIMSTACK_ID = "com.kms.katalon.trimbar";

    public static final String CONSOLE_TRIMSTACK_ID = "com.kms.katalon.partstack.console(com.kms.katalon.composer.window.main).(com.kms.katalon.composer.perspective.keyword)";

    // Menu IDs
    public static final String MAIN_MENU_ID = "com.kms.katalon.composer.menu";

    public static final String MENU_ID_FILE = "com.kms.katalon.composer.menu.file";

    public static final String MENU_ID_FILE_NEW = "com.kms.katalon.menu.submenu.new";

    public static final String MENU_ID_FILE_SAVE_ALL = "com.kms.katalon.composer.menu.file.save_all";

    public static final String MENU_ID_FILE_SAVE = "com.kms.katalon.composer.menu.file.save";

    public static final String MENU_ID_FILE_QUIT = "com.kms.katalon.composer.menu.file.quit";

    public static final String MENU_ID_EDIT = "com.kms.katalon.composer.menu.edit";

    public static final String MENU_ID_REPORT = "com.kms.katalon.composer.menu.report";

    public static final String MENU_ID_SETTINGS = "com.kms.katalon.composer.menu.settings";

    public static final String MENU_ID_SEARCH = "com.kms.katalon.menu.search";

    public static final String MENU_ID_SEARCH_ = "com.kms.katalon.handledmenuitem.searchFile";

    public static final String MENU_ID_PROJECT = "com.kms.katalon.composer.menu.project";

    public static final String MENU_ID_WINDOW = "com.kms.katalon.composer.menu.window";

    public static final String MENU_ID_WINDOW_RESET_PERSPECTIVE = "com.kms.katalon.composer.menu.window.resetPerspective";

    public static final String MENU_ID_HELP = "com.kms.katalon.composer.menu.help";

    public static final String MENU_ID_HELP_ABOUT = "com.kms.katalon.composer.menu.help.about";

    // Command IDs
    public static final String MENU_CMD_ID_FILE_SAVE_ALL = "com.kms.katalon.command.saveAll";

    public static final String MENU_CMD_ID_SEARCH_ = "com.kms.katalon.command.search";

    public static final String MENU_CMD_ID_WINDOW_RESET_PERSPECTIVE = "com.kms.katalon.composer.command.window.resetPerspective";

    public static final String MENU_CMD_ID_HELP_ABOUT = "org.eclipse.ui.help.aboutAction";

    public static final String MENU_CMD_ID_QUITE = "com.kms.katalon.composer.quit";

    // Left Part Stack
    public static final String COMPOSER_PARTSTACK_EXPLORER_ID = "com.kms.katalon.composer.partstack.explorer";

    public static final String COMPOSER_PARTSTACK_LEFT_OUTLINE_ID = "com.kms.katalon.partstack.left.outline";

    public static final String COMPOSER_REQUEST_HISTORY_PART_ID = "com.kms.katalon.composer.webservice.part.requestHistory";
    
    // Jira Plugin
    public static final String JIRA_PLUGIN_ID = "com.katalon.katalon-studio-jira-plugin";
}
