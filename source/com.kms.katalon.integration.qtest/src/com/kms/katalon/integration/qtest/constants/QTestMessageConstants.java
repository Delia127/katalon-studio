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

    public static final String EXPIRED_ACTIVATION_CODE_ERROR_MESSAGE = "Expired Activation Code. Please contact qTest for support!";

    public static final String INVALID_ACTIVATION_CODE_ERROR_MESSAGE = "Invalid Activation Code. Please contact qTest for support!";

    public static final String ONLINE_ACTIVATION_LICENSE_ALREADY_ACTIVTED_MSG_ERR = "Your activation code has been used. Please use another activation code!";

    public static final String ONLINE_ACTIVATION_LICENSE_NOT_FOUND_MSG_ERR = "The qTest Username or Activation Code you entered is incorrect. Please try again!";

    public static final String ONLINE_ACTIVATION_INTERNAL_SERVER_ERROR_MSG_ERROR = "Could not connect to Katalon License Server. Please try again later!";
}
