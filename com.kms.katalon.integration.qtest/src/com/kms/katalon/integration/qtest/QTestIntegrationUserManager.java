package com.kms.katalon.integration.qtest;

import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestUser;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

/**
 * Provides a set of utility methods that relate with {@link QTestUser}
 */
public class QTestIntegrationUserManager {

    private QTestIntegrationUserManager() {
        // Disable default constructor
    }

    /**
     * Returns a {@link QTestUser} by using qTest setting information (user
     * name, password).
     * 
     * @param project
     * @param projectDir
     * @return
     * @throws QTestException
     *             thrown if the request is invalid
     */
    public static QTestUser getUser(QTestProject project, String projectDir) throws QTestException {
        String token = QTestSettingStore.getToken(projectDir);
        String username = QTestSettingStore.getUsername(projectDir);
        String serverUrl = QTestSettingStore.getServerUrl(projectDir);

        String result = QTestAPIRequestHelper.sendGetRequestViaAPI(
                serverUrl + "/api/v3/projects/" + Long.toString(project.getId()) + "/user-profiles/current", token);

        try {
            Long userId = new JsonObject(result).getLong("user_id");

            return new QTestUser(userId, username);
        } catch (JsonException e) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(result);
        }
    }
}
