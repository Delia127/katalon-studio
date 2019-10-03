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
    public static final String APP_NAME = GlobalMessageConstants.APP_NAME;
    
    // Path
    public static final String ENTITY_ID_SEPARATOR = "/";
    
    public static final String DF_CHARSET = "UTF-8";

    // Common character
    public static final String CR_DOT = ".";

    public static final String CR_LEFT_PARENTHESIS = "(";

    public static final String CR_RIGHT_PARENTHESIS = ")";

    public static final String CR_PRIME = "'";

    public static final String CR_DOUBLE_PRIMES = "\"";

    public static final String CR_SPACE = " ";

    public static final String CR_HYPHEN = "-";

    public static final String CR_EOL = "\n";

    public static final String CR_COLON = ":";

    /** RFC 4180 defines line breaks as CRLF */
    public static final String CRLF = "\r\n";
    
    public static final String CR_ECO_PASSWORD = "\u2022";

    // Common action use
    public static final String ADD = GlobalMessageConstants.ADD;

    public static final String INSERT = GlobalMessageConstants.INSERT;

    public static final String DELETE = GlobalMessageConstants.DELETE;

    public static final String REMOVE = GlobalMessageConstants.REMOVE;

    public static final String CLEAR = GlobalMessageConstants.CLEAR;

    public static final String EDIT = GlobalMessageConstants.EDIT;

    public static final String CUT = GlobalMessageConstants.CUT;

    public static final String COPY = GlobalMessageConstants.COPY;

    public static final String PASTE = GlobalMessageConstants.PASTE;

    public static final String REFRESH = GlobalMessageConstants.REFRESH;

    public static final String SEARCH = GlobalMessageConstants.SEARCH;

    public static final String BROWSE = GlobalMessageConstants.BROWSE;

    public static final String FIND = GlobalMessageConstants.FIND;

    public static final String UP = GlobalMessageConstants.UP;

    public static final String DOWN = GlobalMessageConstants.DOWN;

    public static final String UPDATE = GlobalMessageConstants.UPDATE;

    public static final String EMPTY = GlobalMessageConstants.EMPTY;

    public static final String PROPERTIES = GlobalMessageConstants.PROPERTIES;

    // Dialog Title
    public static final String CONFIRMATION = GlobalMessageConstants.CONFIRMATION;

    public static final String NOTIFICATION = GlobalMessageConstants.NOTIFICATION;

    public static final String PROJECT_SETTINGS = GlobalMessageConstants.PROJECT_SETTINGS;

    // Test Status
    public static final String ALL = GlobalMessageConstants.ALL;

    public static final String PASSED = GlobalMessageConstants.PASSED;

    public static final String FAILED = GlobalMessageConstants.FAILED;

    public static final String INFO = GlobalMessageConstants.INFO;

    public static final String WARN = GlobalMessageConstants.WARN;

    public static final String ERROR = GlobalMessageConstants.ERROR;

    public static final String INCOMPLETE = GlobalMessageConstants.INCOMPLETE;

    public static final String NOT_RUN = GlobalMessageConstants.NOT_RUN;

    public static final String COMPLETE = GlobalMessageConstants.COMPLETE;

    public static final String NOT_STARTED = GlobalMessageConstants.NOT_STARTED;

    // Tool-bar
    public static final String FOLDER = GlobalMessageConstants.FOLDER;

    public static final String JAR = GlobalMessageConstants.JAR;

    public static final String GIT = GlobalMessageConstants.GIT;

    public static final String PACKAGE = GlobalMessageConstants.PACKAGE;

    public static final String DEFAULT_PACKAGE_NAME = GlobalMessageConstants.DEFAULT_PACKAGE_NAME;

    public static final String TEST_CASE = GlobalMessageConstants.TEST_CASE;

    public static final String TEST_SUITE = GlobalMessageConstants.TEST_SUITE;
    
    public static final String TEST_SUITE_COLLECTION = GlobalMessageConstants.TEST_SUITE_COLLECTION;

    public static final String TEST_OBJECT = GlobalMessageConstants.TEST_OBJECT;

    public static final String TEST_DATA = GlobalMessageConstants.TEST_DATA;

    public static final String KEYWORD = GlobalMessageConstants.KEYWORD;

    public static final String REPORT = GlobalMessageConstants.REPORT;

    public static final String CHECKPOINT = GlobalMessageConstants.CHECKPOINT;

    public static final String OBJECT_SPY = GlobalMessageConstants.OBJECT_SPY;

    public static final String WEB_RECORDER = GlobalMessageConstants.WEB_RECORDER;

    public static final String RUN = GlobalMessageConstants.RUN;

    public static final String STOP = GlobalMessageConstants.STOP;

    // Field name
    public static final String ID = GlobalMessageConstants.ID;

    public static final String NAME = GlobalMessageConstants.NAME;

    public static final String TYPE = GlobalMessageConstants.TYPE;

    public static final String VALUE = GlobalMessageConstants.VALUE;

    public static final String IMAGE = GlobalMessageConstants.IMAGE;

    public static final String DESCRIPTION = GlobalMessageConstants.DESCRIPTION;

    public static final String SUMMARY = GlobalMessageConstants.SUMMARY;

    public static final String INFORMATION = GlobalMessageConstants.INFORMATION;

    public static final String COMMENT = GlobalMessageConstants.COMMENT;

    public static final String TAG = GlobalMessageConstants.TAG;

    public static final String INPUT = GlobalMessageConstants.INPUT;

    public static final String OUTPUT = GlobalMessageConstants.OUTPUT;

    public static final String OBJECT = GlobalMessageConstants.OBJECT;

    public static final String EMAIL = GlobalMessageConstants.EMAIL;

    public static final String SERVER_URL = GlobalMessageConstants.SERVER_URL;

    public static final String LEVEL = GlobalMessageConstants.LEVEL;

    public static final String TIME = GlobalMessageConstants.TIME;

    public static final String MESSAGE = GlobalMessageConstants.MESSAGE;

    public static final String NOT_FOUND = GlobalMessageConstants.NOT_FOUND;

    public static final String DONE = GlobalMessageConstants.DONE;

    public static final String NO_ = GlobalMessageConstants.NO_;

    public static final String ATTACHMENT = GlobalMessageConstants.ATTACHMENT;

    public static final String STATUS = GlobalMessageConstants.STATUS;

    public static final String ANDROID = GlobalMessageConstants.ANDROID;

    public static final String IOS = GlobalMessageConstants.IOS;

    public static final String ELAPSED = GlobalMessageConstants.ELAPSED;

    public static final String SIZE = GlobalMessageConstants.SIZE;

    // Entity name
    public static final String ROOT_FOLDER_NAME_TEST_CASE = GlobalMessageConstants.ROOT_FOLDER_NAME_TEST_CASE;

    public static final String ROOT_FOLDER_NAME_TEST_SUITE = GlobalMessageConstants.ROOT_FOLDER_NAME_TEST_SUITE;

    public static final String ROOT_FOLDER_NAME_DATA_FILE = GlobalMessageConstants.ROOT_FOLDER_NAME_DATA_FILE;

    public static final String ROOT_FOLDER_NAME_OBJECT_REPOSITORY = GlobalMessageConstants.ROOT_FOLDER_NAME_OBJECT_REPOSITORY;

    public static final String ROOT_FOLDER_NAME_KEYWORD = GlobalMessageConstants.ROOT_FOLDER_NAME_KEYWORD;

    public static final String ROOT_FOLDER_NAME_REPORT = GlobalMessageConstants.ROOT_FOLDER_NAME_REPORT;

    public static final String ROOT_FOLDER_NAME_CHECKPOINT = GlobalMessageConstants.ROOT_FOLDER_NAME_CHECKPOINT;

    public static final String ROOT_FOLDER_NAME_PROFILES = GlobalMessageConstants.ROOT_FOLDER_NAME_PROFILES;

    public static final String ROOT_FOLDER_NAME_TEST_LISTENER = GlobalMessageConstants.ROOT_FOLDER_NAME_TEST_LISTENER;

    public static final String ROOT_FOLDER_NAME_INCLUDE = GlobalMessageConstants.ROOT_FOLDER_NAME_INCLUDE;

    public static String SYSTEM_FOLDER_NAME_BIN = GlobalMessageConstants.SYSTEM_FOLDER_NAME_BIN;
    
    public static String SYSTEM_FOLDER_NAME_DRIVER = GlobalMessageConstants.SYSTEM_FOLDER_NAME_DRIVER;
            
    public static String SYSTEM_FOLDER_NAME_LIB = GlobalMessageConstants.SYSTEM_FOLDER_NAME_LIB;
            
    public static String SYSTEM_FOLDER_NAME_PLUGIN = GlobalMessageConstants.SYSTEM_FOLDER_NAME_PLUGIN;
    
    public static String SYSTEM_FOLDER_NAME_SETTINGS = GlobalMessageConstants.SYSTEM_FOLDER_NAME_SETTINGS;
    
    public static String SYSTEM_FOLDER_NAME_SCRIPT = GlobalMessageConstants.SYSTEM_FOLDER_NAME_SCRIPT;
    
    public static final String FILE_NAME_GLOBAL_VARIABLE = GlobalMessageConstants.FILE_NAME_GLOBAL_VARIABLE;
    
    // Entity keyword
    public static final String ENTITY_KW_TEST_CASE = "tc";

    public static final String ENTITY_KW_TEST_SUITE = "ts";

    public static final String ENTITY_KW_TEST_OBJECT = "ob";

    public static final String ENTITY_KW_TEST_DATA = "td";

    public static final String ENTITY_KW_REPORT = "rp";

    public static final String ENTITY_KW_KEYWORD = "kw";

    public static final String ENTITY_KW_CHECKPOINT = "cp";

    // Common value
    public static final String NULL = GlobalMessageConstants.NULL;
    
    public static final String APP_TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "Katalon";

    public static final String APP_USER_DIR_LOCATION = System.getProperty("user.home") + File.separator + ".katalon";

    public static final String APP_INFO_FILE_LOCATION = APP_USER_DIR_LOCATION + File.separator
            + "application.properties";
    
    public static final String APP_BUILD_NUMBER_KEY = "katalon.buildNumber";

    public static final String APP_VERSION_NUMBER_KEY = "katalon.versionNumber";

    public static final String UNKNOWN = GlobalMessageConstants.UNKNOWN;

    public static final String OK = GlobalMessageConstants.OK;

    public static final String TEST_CASE_SCRIPT_ROOT_FOLDER_NAME = GlobalMessageConstants.TEST_CASE_SCRIPT_ROOT_FOLDER_NAME;

    public static final String ROOT_FOLDER_NAME_DRIVERS = GlobalMessageConstants.ROOT_FOLDER_NAME_DRIVERS;

    public static final String WZ_SETUP_BTN_BACK = GlobalMessageConstants.WZ_SETUP_BTN_BACK;

    public static final String WZ_SETUP_BTN_INSTALL = GlobalMessageConstants.WZ_SETUP_BTN_INSTALL;

    public static final String WZ_SETUP_BTN_NEXT = GlobalMessageConstants.WZ_SETUP_BTN_NEXT;

    public static final String DIA_FINISH = GlobalMessageConstants.DIA_FINISH;
    
    public static final String DIA_CANCEL = GlobalMessageConstants.DIA_CANCEL;

    public static final String DIA_OK = GlobalMessageConstants.DIA_OK;
    
    public static final String DIA_SKIP = GlobalMessageConstants.DIA_SKIP;

    public static final String DIA_CLOSE = GlobalMessageConstants.DIA_CLOSE;

    public static final String DIA_YES = GlobalMessageConstants.DIA_YES;

    public static final String DIA_NO = GlobalMessageConstants.DIA_NO;
    
    public static final String DIA_LET_START = GlobalMessageConstants.DIA_LET_START;
    
    public static final String DIA_NEW_PROJECT = GlobalMessageConstants.DIA_NEW_PROJECT;
    
    public static final String DIA_OPEN_PROJECT = GlobalMessageConstants.DIA_OPEN_PROJECT;
    
    public static final String DIA_INTRO_PROJECT = GlobalMessageConstants.DIA_INTRO_PROJECT;

    public static final String CREATED_DATE = GlobalMessageConstants.CREATED_DATE;

    public static final String MODIFIED_DATE = GlobalMessageConstants.MODIFIED_DATE;

    public static final String APPIUM_INSTALLATION_GUIDE_MSG = GlobalMessageConstants.APPIUM_INSTALLATION_GUIDE_MSG;
    
    public static final String URL_TROUBLESHOOTING_MOBILE_TESTING = GlobalMessageConstants.URL_TROUBLESHOOTING_MOBILE_TESTING;
    
    public static final String DIA_TITLE_QUICKSTART = GlobalMessageConstants.DIA_TITLE_QUICKSTART;

}
