package com.kms.katalon.integration.qtest.credential;

import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.setting.QTestVersion;

public interface IQTestToken {
    
    /**
     * The version of this token.
     */
    public QTestVersion getVersion();

    /**
     * The raw token that returned from qTest.
     * </p>
     * Used to display or store.
     */
    public String getRawToken();

    /**
     * Used to access to qTest via API
     */
    public String getAccessToken() throws QTestInvalidFormatException;
}
