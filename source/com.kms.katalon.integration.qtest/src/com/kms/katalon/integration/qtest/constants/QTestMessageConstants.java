package com.kms.katalon.integration.qtest.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class QTestMessageConstants extends GlobalStringConstants {
    // QTestIntegrationExecutionManager
    public static final String QTEST_EXC_INVALID_LOG_STATUS = "Invalid log status {0}";

    public static final String QTEST_EXC_INVALID_TOKEN = "Token is not valid";

    public static final String QTEST_EXC_CANNOT_READ_INPUT_STREAM = "Unable to read input stream: {0}";

    // JSON format
    public static final String QTEST_INVALID_JSON_FORMAT = "The response message is not valid JSON format: {0}";

    public static final String QTEST_PROJECT_NOT_FOUND = "Cannot find qTest project. Please select a qTest project on qTest setting page.";

    // QTest Activation Dialog
    public static final String QTEST_USERNAME_LABEL = "qTest-username";

    public static final String QTEST_CODE_LABEL = "qTest-code";

    public static final String QTEST_ACTIVATION_PROMPT_ENTER_USERNAME_CODE = "Please fill your qTest-username and qTest-code.";

    public static final String ACTIVATE_QTEST_CODE_LABEL = "qTest Offline Activation Code";

    public static final String EXPIRED_ACTIVATION_CODE_ERROR_MESSAGE = "Expired Activation Code. Please contact qTest for support!";

    public static final String INVALID_ACTIVATION_CODE_ERROR_MESSAGE = "Invalid Activation Code. Please contact qTest for support!";

    public static final String OFFLINE_ACTIVATION_HINT = "Please send the request code below to qTest for an Offline Activation Code. Be prepared to provide your qTest account information and \nRequest Code (below). \n";

    public static final String REACTIVATION_EXPIRED_LICENSE_WARNING = "Katalon Studio license has expired. Please contact qTest for license renewal.\n\n";

    public static final String ONLINE_ACTIVATION_LICENSE_ALREADY_ACTIVTED_MSG_ERR = "License already activation";
    
    public static final String ONLINE_ACTIVATION_LICENSE_NOT_FOUND_MSG_ERR = "License not found";
    
    public static final String ONLINE_ACTIVATION_INTERNAL_SERVER_ERROR_MSG_ERROR = "Have a problem to connect to server for verification";
}
