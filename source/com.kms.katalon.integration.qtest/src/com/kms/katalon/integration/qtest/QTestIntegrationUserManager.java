package com.kms.katalon.integration.qtest;

import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestUser;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;

/**
 * Provides a set of utility methods that relate with {@link QTestUser}
 */
public class QTestIntegrationUserManager {

    private QTestIntegrationUserManager() {
        // Disable default constructor
    }

    /**
     * Returns a {@link QTestUser} by using qTest setting information (user name, password).
     * 
     * @param project
     * @return new instance of {@link QTestUser}
     * @throws QTestException
     *             thrown if the request is invalid
     */
    public static QTestUser getUser(QTestProject project, IQTestCredential credential) throws QTestException {
        String username = credential.getUsername();
        String serverUrl = credential.getServerUrl();

        String result = QTestAPIRequestHelper.sendGetRequestViaAPI(
                serverUrl + "/api/v3/projects/" + Long.toString(project.getId()) + "/user-profiles/current",
                credential.getToken());

        try {
            Long userId = new JsonObject(result).getLong("user_id");

            return new QTestUser(userId, username);
        } catch (JsonException e) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(result);
        }
    }
}
