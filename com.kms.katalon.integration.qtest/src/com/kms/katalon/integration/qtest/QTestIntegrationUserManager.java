package com.kms.katalon.integration.qtest;

import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestUser;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationUserManager {
	public static QTestUser getUser(QTestProject project, String projectDir) throws Exception {
		String token = QTestSettingStore.getToken(projectDir);
		String username = QTestSettingStore.getUsername(projectDir);
		String serverUrl = QTestSettingStore.getServerUrl(projectDir);

		String result = QTestAPIRequestHelper.sendGetRequestViaAPI(
				serverUrl + "/api/v3/projects/" + Long.toString(project.getId()) + "/user-profiles/current", token);
		Long userId = new JsonObject(result).getLong("user_id");
		return new QTestUser(userId, username);
	}
}
