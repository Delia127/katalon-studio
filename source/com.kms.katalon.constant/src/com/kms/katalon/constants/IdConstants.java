package com.kms.katalon.constants;

public interface IdConstants {
    // Application
    public static final String APPLICATION_ID = "com.kms.katalon";

    // Main Window
    public static final String MAIN_WINDOW_ID = "com.kms.katalon.composer.window.main";
    
    // Eclipse Shared Area
    public static final String SHARE_AREA_ID = "org.eclipse.ui.editorss";
    
    //Place Holder
    public static final String PLACE_HOLDER_BOTTOM_PART_STACK = "com.kms.katalon.placeholder.bottom";
    
    //Command
    public static final String QUIT_COMMAND_ID = "com.kms.katalon.composer.quit";
    
    //Toolbar
    public static final String EXECUTION_TOOL_ITEM_ID = "com.kms.katalon.composer.execution.handledtoolitem.run";

    // Composer PartStack content
    public static final String COMPOSER_CONTENT_PARTSTACK_ID = "com.kms.katalon.composer.content";

    public static final String CONSOLE_PARTSTACK_ID = "com.kms.katalon.partstack.console";

    public static final String SYSTEM_CONSOLE_PART_ID = "org.eclipse.ui.console.ConsoleView";
    public static final String IDE_CONSOLE_LOG_PART_ID = "com.kms.katalon.partstack.console.log";
    public static final String IDE_SEARCH_PART_ID = "org.eclipse.search.ui.views.SearchView";
    
    // Explorer part content
    public static final String EXPLORER_PART_ID = "com.kms.katalon.composer.part.explorer";

    // Test Case Part content
    public static final String TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".testcase";

    public static final String TEST_CASE_SUB_PART_STACK_ID_SUFFIX = ".partStack";
    public static final String TEST_CASE_EDITOR_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".editor";    
    public static final String TEST_CASE_VARIABLES_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".variables";    
    public static final String TEST_CASE_INTEGRATION_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".integration";
    public static final String TEST_CASE_GENERAL_PART_ID_SUFFIX = TEST_CASE_SUB_PART_STACK_ID_SUFFIX + ".testCasePart";

    // Test Object Part content
    public static final String TESTOBJECT_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".testobject";

    // Test Suite Part content
    public static final String TESTSUITE_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".testsuite";
    
    public static final String TEST_SUITE_SUB_PART_STACK_ID_SUFFIX = ".partStack";   
    public static final String TEST_SUITE_INTEGRATION_PART_ID_SUFFIX = TEST_SUITE_SUB_PART_STACK_ID_SUFFIX + ".integration";
    public static final String TEST_SUITE_MAIN_PART_ID_SUFFIX = TEST_SUITE_SUB_PART_STACK_ID_SUFFIX + ".testSuitePart";

    // Test Data Part content
    public static final String TESTDATA_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".testdata";

    // Report Part content
    public static final String REPORT_CONTENT_PART_ID_PREFIX = COMPOSER_CONTENT_PARTSTACK_ID + ".report";

    // Test Suite Part: key of TestSuiteEntity in content part
    public static final String TESTSUITE_CONTEXT_OBJECT_KEY = "testSuite";

    // Command ID
    public static final String SAVE_COMMAND_ID = "com.kms.katalon.command.save";
    public static final String CLOSE_COMMAND_ID = "com.kms.katalon.command.close";

    // Open Recent Project Command id
    public static final String OPEN_RECENT_PROJECT_COMMAND_ID = "com.kms.katalon.composer.project.command.openrecentproject";
    public static final String OPEN_RECENT_PROJECT_COMMAND_PARAMETER_ID = "com.kms.katalon.composer.project.command.openrecentproject.parameters.project";

    // Bundle
    public static final String KATALON_CUSTOM_BUNDLE_ID = "com.kms.katalon.custom";
    public static final String KATALON_CORE_BUNDLE_ID = "com.kms.katalon.core";
    public static final String KATALON_WEB_UI_BUNDLE_ID = "com.kms.katalon.core.webui";
    public static final String KATALON_SELENIUM_BUNDLE_ID = "com.kms.katalon.selenium.server-standalone";
    public static final String KATALON_EXECUTION_BUNDLE_ID = "com.kms.katalon.execution";
    public static final String XERCES_BUNDLE_ID = "org.apache.xerces.impl";
    public static final String XML_APIS_BUNDLE_ID = "org.apache.xml.apis";
    public static final String COMPOSER_EXECUTION_BUNDLE_ID = "com.kms.katalon.composer.execution";

    // Groovy
    public static final String GROOVY_EDITOR_URI = "org.codehaus.groovy.eclipse.editor.GroovyEditor";

    // Log viewer part
    public static final String LOG_VIEWER_TOOL_ITEM_ALL_ID = "com.kms.katalon.composer.execution.logviewer.all";
    public static final String LOG_VIEWER_TOOL_ITEM_INFO_ID = "com.kms.katalon.composer.execution.logviewer.info";
    public static final String LOG_VIEWER_TOOL_ITEM_PASSED_ID = "com.kms.katalon.composer.execution.logviewer.passed";
    public static final String LOG_VIEWER_TOOL_ITEM_FAILED_ID = "com.kms.katalon.composer.execution.logviewer.failed";
    public static final String LOG_VIEWER_TOOL_ITEM_ERROR_ID = "com.kms.katalon.composer.execution.logviewer.error";
    
    public static final String LOG_VIEWER_MENU_ITEM_TREE_ID = "com.kms.katalon.composer.execution.directtoolitem.tree";
    public static final String LOG_VIEWER_MENU_ITEM_PIN_ID = "com.kms.katalon.composer.execution.directtoolitem.pin";
    
    //Outline Part Stack
    public static final String OUTLINE_PARTSTACK_ID = "com.kms.katalon.partstack.outline";
    public static final String JOBVIEWER_PART_ID = "com.kms.katalon.composer.execution.part.job";
	public static final String OUTLINE_TRIMSTACK_ID = 
			"com.kms.katalon.partstack.outline(com.kms.katalon.composer.perspective.keyword)";
	public static final String TOOLBAR_TRIMSTACK_ID = 
			"com.kms.katalon.trimbar";
	public static final String CONSOLE_TRIMSTACK_ID = 
			"com.kms.katalon.partstack.console(com.kms.katalon.composer.perspective.keyword)";
}
