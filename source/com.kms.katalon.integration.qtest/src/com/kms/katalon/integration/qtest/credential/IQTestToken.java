package com.kms.katalon.integration.qtest.credential;

import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.setting.QTestVersion;

public interface IQTestToken {
    public QTestVersion getVersion();

    public String getRawToken();

    public String getAccessToken() throws QTestInvalidFormatException;
}
