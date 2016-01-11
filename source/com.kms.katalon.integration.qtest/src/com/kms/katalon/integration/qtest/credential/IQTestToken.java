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
    public String getAccessTokenHeader() throws QTestInvalidFormatException;
    
    /**
     * Returns a String that represents token with token type.
     * </p>
     * Used to check when terminate a session.
     * </p>
     */
    public String getAccessToken() throws QTestInvalidFormatException;
}
