package com.kms.katalon.composer.integration.qtest.constant;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {

    // Common used
    public static final String CM_TYPE = "Type";
    public static final String CM_MSG_PLEASE_WAIT = "Please wait...";
    public static final String CM_MSG_SERVER_NOT_FOUND = "Server not found.";
    public static final String CM_USERNAME = "Username";
    public static final String CM_PASSWORD = "Password";
    public static final String CM_SERVER_URL = "Server Url";
    public static final String CM_UPLOAD = "Upload";
    public static final String CM_AUTHENTICATION = "Authentication";
    public static final String CM_TOKEN = "Token";
    public static final String CM_DISINTEGRATE = "Disintegrate";
    public static final String CM_NAVIGATE = "Navigate";
    public static final String CM_ALIAS = "Alias";
    public static final String CM_PARENT_ID = "Parent ID";
    public static final String CM_DEFAULT = "Default";

    // Job
    public static final String JOB_TITLE_DISINTEGRATE_TEST_CASE = "Disintegrate test case(s)";
    public static final String JOB_TITLE_UPLOAD_TEST_CASE = "Upload test case(s)";

    // DisintegrateTestCaseJob
    public static final String JOB_TASK_DISINTEGRATE_TEST_CASE = "Disintegrating test cases...";
    public static final String JOB_SUB_TASK_DISINTEGRATE_TEST_CASE = "Disintegrating {0} ...";

    // DownloadTestCaseJob
    public static final String JOB_SUB_TASK_DOWNLOAD_TEST_CASE = "Downloading test cases...";
    public static final String JOB_SUB_TASK_CHECK_SYSTEM = "Checking system...";
    public static final String JOB_SUB_TASK_FETCH_CHILDREN = "Fetching children of: {0}";
    public static final String JOB_TASK_CREATE_TEST_CASE = "Creating new test cases...";
    public static final String JOB_SUB_TASK_CREATE_TEST_CASE = "Creating test cases: {0} ...";
    public static final String JOB_SUB_TASK_CREATE_TEST_CASE_FOLDER = "Creating folder: {0} ...";

    public static final String DIA_TITLE_FOLDER_DUPLICATION = "Folder Duplication Detected";
    public static final String DIA_TITLE_TEST_CASE_DUPLICATION = "Test Case Duplication Detected";
    public static final String DIA_MSG_CONFIRM_MERGE_DOWNLOADED_TEST_CASE_FOLDER = "System has detected you want to create a folder"
            + " with name: {0} but it has already existed on the file system.\nDo you want to merge them?";
    public static final String DIA_MSG_CONFIRM_MERGE_DOWNLOADED_TEST_CASE = "System has detected you want to create a test case "
            + "with name: {0} but it has already existed on the file system.\nDo you want to merge them?";

    // UploadTestCaseJob
    public static final String JOB_TASK_UPLOAD_TEST_CASE = "Uploading test cases...";
    public static final String JOB_SUB_TASK_UPLOAD_TEST_CASE = "Uploading {0} ...";

    public static final String DIA_MSG_UNABLE_UPLOAD_TEST_CASE = "Unable to upload test cases.";
    public static final String DIA_MSG_CONFIRM_MERGE_UPLOADED_TEST_CASE = "System has detected that a test case on qTest"
            + " with id: {0} has the same name as test case: {1}.\nDo you want to merge them?";
    public static final String DIA_MSG_CONFIRM_MERGE_UPLOADED_TEST_CASE_FOLDER = "System has detected that a test folder"
            + " on qTest with id: {0} has the same name as test case folder: {1}.\nDo you want to merge them?";

    // CreateNewTestSuiteParentDialog
    public static final String DIA_TITLE_CREATE_TEST_SUITE_PARENT = "Create Test Suite's parent";
    public static final String DIA_TITLE_IN_USE = "In use";
    public static final String DIA_TITLE_CREATION_OPTIONS = "Creation options";
    public static final String DIA_TITLE_CHOOSE_PARENT_FOR_TEST_SUITE = "Choose parent for test suite.";
    public static final String DIA_MSG_UNABLE_TO_UPLOAD_TEST_SUITE = "Unable to load test suite's parent from qTest "
            + "server.";

    // GenerateNewTokenDialog
    public static final String DIA_MSG_ENTER_SERVER_URL = "Please enter server url.";
    public static final String DIA_MSG_ENTER_USERNAME = "Please enter username.";
    public static final String DIA_MSG_ENTER_PASSWORD = "Please enter password.";
    public static final String DIA_MSG_UNABLE_TO_GET_TOKEN = "Unable to get token";
    public static final String DIA_TITLE_GENERATE_TOKEN = "Generate new token";
    public static final String DIA_TITLE_GENERATE = "Generate";

    // ListReportUploadingPreviewDialog
    public static final String DIA_TITLE_TEST_LOG_UPLOADING_PREVIEW = "Test Log Uploading Preview";

    // RepoDialog
    public static final String DIA_TITLE_QTEST_PROJECT = "qTest Project";
    public static final String DIA_TITLE_QTEST_MODULE = "qTest Module";
    public static final String DIA_TITLE_KATALON_FOLDER = "Katalon Folder";
    public static final String DIA_TITLE_TEST_CASE_FOLDER_BROWSER = "Test Case Folder Browser";
    public static final String DIA_TITLE_TEST_SUITE_FOLDER_BROWSER = "Test Suite Folder Browser";
    public static final String DIA_TITLE_CREATE_TEST_CASE_REPO = "Create Test Case Repository";
    public static final String DIA_TITLE_CREATE_TEST_SUITE_REPO = "Create Test Suite Repository";
    public static final String DIA_MSG_UNABLE_TO_UPDATE_PROJECT = "Unable to update qTest projects.";
    public static final String DIA_MSG_UNABLE_TO_UPDATE_MODULE = "Unable to update qTest modules.";
    public static final String DIA_MSG_UNABLE_TO_FIND_TEST_CASE_FOLDER = "Unable to find test case folder.";
    public static final String DIA_MSG_UNABLE_TO_FIND_TEST_SUITE_FOLDER = "Unable to find test suite folder.";

    // TestCaseRootSelectionDialog
    public static final String DIA_INFO_TEST_CASE_ROOT = "System needs a specific module on qTest for the integration. "
            + "Please choose one. All test cases and folders will be uploaded or created inside your selection.";
    public static final String DIA_MSG_UNABLE_TO_LOAD_TEST_SUITE_PARENT = "Unable to load test suite's parent from qTest"
            + " server.";
    public static final String DIA_TITLE_TEST_CASE_ROOT = "Test case root selection";

    // TestCaseTreeDownloadedPreviewDialog
    public static final String DIA_INFO_TEST_CASE_DOWNLOADED_PREVIEW = "Please select test cases you want to create.";
    public static final String DIA_TITLE_TEST_CASE_DOWNLOADED_PREVIEW = "Downloaded test cases preview.";

    // QTestIntegrationPage
    public static final String DIA_MSG_UNABLE_TO_SAVE_SETTING_PAGE = "Unable to save qTest's settings.";
    public static final String DIA_TITLE_ENABLE_INTEGRATION = "Enable integration";
    public static final String DIA_TITLE_CHECK_DUPLICATES_TEST_CASE = "Check duplicates before uploading test cases";
    public static final String DIA_TITLE_AUTO_SUBMIT_TEST_RESULT = "Automatically submit test run result";
    public static final String DIA_TITLE_SEND_RESULT = "Send result";
    public static final String DIA_TITLE_SEND_ATTACHMENT = "Attachment included";

    // TestCaseRepoPreferencePage
    public static final String DIA_MSG_UNABLE_GET_PROJECT_INFO = "Unable to get qTest projects's information.";
    public static final String DIA_TITLE_TEST_CASE_FOLDER = "Test Case's Folder";
    public static final String DIA_MSG_UNABLE_MOFIDY_TEST_CASE_REPO = "Unable to modify Test Case Repository.";
    public static final String DIA_MSG_UNABLE_REMOVE_TEST_CASE_REPO = "Unable to remove Test Case Repository.";
    public static final String DIA_CONFIRM_DISINTEGRATE_TEST_CASE_FOLDER = "Are you sure you want to disintegrate all "
            + "test cases in this folder with qTest?";

    // QTestIntegrationReportTestCaseView
    public static final String VIEW_TITLE_TEST_LOG_ID = "Test Log ID";
    public static final String VIEW_TITLE_TEST_RUN_ID = "Test Run ID";
    public static final String VIEW_MSG_UNABLE_UPLOAD_TEST_RESULT = "Unable to upload test case's result.";

    // QTestIntegrationTestCaseView
    public static final String VIEW_TOOLTIP_UPLOAD_TEST_CASE = "Upload this test case to qTest";
    public static final String VIEW_TOOLTIP_DISINTEGRATE_TEST_CASE = "Remove all information of this test case on qTest";
    public static final String VIEW_TOOLTIP_NAVIGATE_TEST_CASE = "Navigate to the integrated test case page on qTest";
    public static final String VIEW_TITLE_TEST_CASE_ID = "Test Case ID";
    public static final String VIEW_CONFIRM_DISINTEGRATE_TEST_CASE = "Are you sure you want to disintegrate this test "
            + "case with qTest?";
    public static final String VIEW_MSG_UNABLE_DISINTEGRATE_TEST_CASE = "Unable to disintegrate this test case on qTest.";
    public static final String VIEW_MSG_UNABLE_NAVIGATE_TEST_CASE = "Unable to open qTest navigated test case.";
    public static final String VIEW_MSG_SAVE_BEFORE_UPLOADING = "Please save your test case before uploading.";
    public static final String VIEW_MSG_TOKEN_REQUIRED = "QTest's token is required. Please enter a valid token on qTest "
            + "setting page or\n you can generate a new one by clicking on generate button.";
    public static final String VIEW_MSG_TEST_CASE_NOT_IN_REPO = "This test case isn't in any Test Case Repository. "
            + "Please add a valid Test Case Repository in Test Case Repositories page.";
    public static final String VIEW_MSG_UNABLE_UPLOAD_TEST_CASE = "Unable to upload test case to qTest.";

    // QTestIntegrationTestSuiteView
    public static final String VIEW_TOOLTIP_UPLOAD_TEST_SUITE = "Upload this test suite to qTest";
    public static final String VIEW_TOOLTIP_DISINTEGRATE_TEST_SUITE = "Remove all information of this test suite on qTest server.";
    public static final String VIEW_TOOLTIP_NEW_TEST_SUITE_PARENT = "To upload this test suite to qTest, you need to "
            + "choose a parent (qTest release, cycle,...) for the integration.";
    public static final String VIEW_TOOLTIP_NAVIGATE_TEST_SUITE = "Navigate to the integrated test suite page on qTest";
    public static final String VIEW_TOOLTIP_SET_DEFAULT_TEST_SUITE = "Use the integrated test suite in this parent to "
            + "upload result after the execution completed.";
    public static final String VIEW_TOOLTIP_REMOVE_TEST_SUITE_PARENT = "Remove selected parent from the list.";

    public static final String VIEW_TITLE_NEW_TEST_SUITE_PARENT = "New parent";
    public static final String VIEW_TITLE_SET_DEFAULT_TEST_SUITE = "Set as default";
    public static final String VIEW_TITLE_REMOVE_TEST_SUITE_PARENT = "Remove parent";
    public static final String VIEW_TITLE_TEST_SUITE_ID = "Test Suite ID";
    public static final String VIEW_TITLE_INTEGRATION_INFORMATION = "Integration Information";
    public static final String VIEW_TITLE_LIST_TEST_SUITE_PARENT = "List of test suite's parent";

    public static final String VIEW_MSG_TEST_SUITE_NOT_IN_REPO = "Please make sure this Test Suite is in a valid Test "
            + "Suite Repository.";
    public static final String VIEW_CONFIRM_DISINTEGRATE_TEST_SUITE = "Are you sure you want to disintegrate this test "
            + "suite with qTest?";
    public static final String VIEW_MSG_UNABLE_DISINTEGRATE_TEST_SUITE = "Unable to disintegrate this test suite on "
            + "qTest.";
    public static final String VIEW_MSG_ENABLE_INTEGRATION = "Please enable qTest integration in Project Setting page.";
    public static final String VIEW_MSG_UNABLE_NAVIGATE_TEST_SUITE = "Unable to open qTest navigated test suite.";
    public static final String VIEW_MSG_UNABLE_UPLOAD_TEST_SUITE = "Unable to upload test suite.";
    public static final String VIEW_MSG_INVALID_AUTHENTICATION = "Invalid authentication";
}
