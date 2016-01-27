package com.kms.katalon.constants;

import java.io.File;

/**
 * This interface will do the job as a language file does
 * 
 * <p>
 * Naming convention:
 * <li>--------------------------
 * <li>COM Composer;
 * <li>COMP Components;
 * <li>PA Parts;
 * <li>CO Core;
 * <li>APP Application;
 * <li>HAND Handlers;
 * <li>ADD Addons;
 * <li>DIA Dialogs;
 * <li>EVT Events;
 * <li>MENU Menu;
 * <li>PROV Providers;
 * <li>SUP Support;
 * <li>UTIL Util;
 * <li>VIEW View;
 * <li>ADAP Adapter;
 * <li>LOG Log, Logging;
 * <li>TRAN Transfer;
 * <li>SERV Services;
 * <li>TREE Tree, Treetable;
 * <li>WIZ Wizard;
 * <li>TRACE Trace;
 * <li>CUS Custom;
 * <li>EXC Exception;
 * <li>ACT Action;
 * <li>CONTR Contributions;
 * <li>ADAP Adapter;
 * <li>CONS Constants;
 * <li>PREF Preferences;
 * <li>EDI Editors;
 * <li>LIS Listeners;
 * <li>SETT Setting
 * <li>CTRL Controller
 * <li>TC Test Case
 * <li>TS Test Suite
 * <li>TD Test Data
 * <li>TO Test Object
 * <li>COMM Common
 * <li>FS FileService
 * <li>MNG Mangager
 * <li>INL Internal
 * <li>--------------------------
 */
public class GlobalStringConstants {
    // Studio's name
    public static final String APP_NAME = "Katalon Studio";

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

    public static final String MENU_ID_SAERCH_ = "com.kms.katalon.handledmenuitem.searchFile";

    public static final String MENU_ID_PROJECT = "com.kms.katalon.composer.menu.project";

    public static final String MENU_ID_HELP = "com.kms.katalon.composer.menu.help";

    public static final String MENU_ID_HELP_ABOUT = "com.kms.katalon.composer.menu.help.about";

    // Command IDs
    public static final String MENU_CMD_ID_FILE_SAVE = "com.kms.katalon.command.save";

    public static final String MENU_CMD_ID_FILE_SAVE_ALL = "com.kms.katalon.command.saveAll";

    public static final String MENU_CMD_ID_FILE_QUIT = "com.kms.katalon.composer.quit";

    public static final String MENU_CMD_ID_SEARCH_ = "com.kms.katalon.command.search";

    public static final String MENU_CMD_ID_HELP_ABOUT = "org.eclipse.ui.help.aboutAction";

    // Part IDs
    public static final String PART_ID_TESTS_EXPLORER = "com.kms.katalon.composer.part.explorer";

    // Path
    public static final String ENTITY_ID_SEPERATOR = "/";

    // Common action use
    public static final String ADD = "Add";

    public static final String INSERT = "Insert";

    public static final String DELETE = "Delete";

    public static final String REMOVE = "Remove";

    public static final String CLEAR = "Clear";

    public static final String EDIT = "Edit";

    public static final String CUT = "Cut";

    public static final String COPY = "Copy";

    public static final String PASTE = "Paste";

    public static final String REFRESH = "Refresh";

    public static final String SEARCH = "Search";

    public static final String BROWSE = "Browse...";

    public static final String FIND = "Find...";

    public static final String UP = "Move Up";

    public static final String DOWN = "Move Down";

    public static final String UPDATE = "Update";

    public static final String EMPTY = "";

    // Dialog Title
    public static final String CONFIRMATION = "Confirmation";

    public static final String NOTIFICATION = "Notification";

    public static final String PROJECT_SETTINGS = "Project Settings";

    // Test Status
    public static final String ALL = "All";

    public static final String PASSED = "Passed";

    public static final String FAILED = "Failed";

    public static final String INFO = "Info";

    public static final String WARN = "Warning";

    public static final String ERROR = "Error";

    public static final String INCOMPLETE = "Incomplete";

    // Tool-bar
    public static final String FOLDER = "Folder";

    public static final String PACKAGE = "Package";

    public static final String DEFAULT_PACKAGE_NAME = "(default package)";

    public static final String TEST_CASE = "Test Case";

    public static final String TEST_SUITE = "Test Suite";

    public static final String TEST_OBJECT = "Test Object";

    public static final String TEST_DATA = "Test Data";

    public static final String KEYWORD = "Keyword";

    public static final String REPORT = "Report";

    public static final String OBJECT_SPY = "Object Spy";

    public static final String RUN = "Run";

    public static final String STOP = "Stop";

    // Field name
    public static final String ID = "ID";

    public static final String NAME = "Name";

    public static final String VALUE = "Value";

    public static final String IMAGE = "Image";

    public static final String DESCRIPTION = "Description";

    public static final String INFORMATION = "General Information";

    public static final String COMMENT = "Comment";

    public static final String TAG = "Tag";

    public static final String INPUT = "Input";

    public static final String OUTPUT = "Output";

    public static final String OBJECT = "Object";

    public static final String EMAIL = "Email";

    public static final String LEVEL = "Level";

    public static final String TIME = "Time";

    public static final String MESSAGE = "Message";

    public static final String NOT_FOUND = " not found.";

    public static final String DONE = "Done";

    public static final String NO_ = "No.";

    public static final String ATTACHMENT = "Attachment";

    public static final String STATUS = "Status";

    public static final String ANDROID = "Android";

    public static final String IOS = "iOS";

    // Entity name
    public static final String ROOT_FOLDER_NAME_TEST_CASE = "Test Cases";

    public static final String ROOT_FOLDER_NAME_TEST_SUITE = "Test Suites";

    public static final String ROOT_FOLDER_NAME_DATA_FILE = "Data Files";

    public static final String ROOT_FOLDER_NAME_OBJECT_REPOSITORY = "Object Repository";

    public static final String ROOT_FOLDER_NAME_KEYWORD = "Keywords";

    public static final String ROOT_FOLDER_NAME_REPORT = "Reports";

    public static final String ROOT_FOLDER_NAME_SETTINGS = "settings";

    public static final String ROOT_FOLDER_NAME_SETTINGS_INTERNAL = ROOT_FOLDER_NAME_SETTINGS + File.separator
            + "internal";

    public static final String ROOT_FOLDER_NAME_SETTINGS_EXTERNAL = ROOT_FOLDER_NAME_SETTINGS + File.separator
            + "external";

    public static final String FILE_NAME_GLOBAL_VARIABLE = "GlobalVariables";

    public static final String GROOVY_FILE_EXENSION = ".groovy";

    public static final String PROPERTY_FILE_EXENSION = ".properties";

    // Entity keyword
    public static final String ENTITY_KW_TEST_CASE = "tc";

    public static final String ENTITY_KW_TEST_SUITE = "ts";

    public static final String ENTITY_KW_TEST_OBJECT = "ob";

    public static final String ENTITY_KW_TEST_DATA = "td";

    public static final String ENTITY_KW_REPORT = "rp";

    public static final String ENTITY_KW_KEYWORD = "kw";
}
