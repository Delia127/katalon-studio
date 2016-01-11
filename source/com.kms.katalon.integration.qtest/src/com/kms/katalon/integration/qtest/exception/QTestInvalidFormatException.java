package com.kms.katalon.integration.qtest.exception;

import java.text.MessageFormat;

import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;

public class QTestInvalidFormatException extends QTestException {

    private static final long serialVersionUID = 5890393957508013391L;

    public QTestInvalidFormatException(String message) {
        super(message);
    }

    public static QTestInvalidFormatException createInvalidJsonFormatException(String message) {
        return new QTestInvalidFormatException(MessageFormat.format(QTestMessageConstants.QTEST_INVALID_JSON_FORMAT,
                message));
    }
    
    public static QTestInvalidFormatException createInvalidTokenException(String message) {
        return new QTestInvalidFormatException(message);
    }
}
