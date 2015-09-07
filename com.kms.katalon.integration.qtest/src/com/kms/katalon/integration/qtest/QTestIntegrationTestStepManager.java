package com.kms.katalon.integration.qtest;

import java.util.List;

import org.qas.qtest.api.auth.BasicQTestCredentials;
import org.qas.qtest.api.auth.QTestCredentials;
import org.qas.qtest.api.services.design.TestDesignServiceAsyncClient;
import org.qas.qtest.api.services.design.model.CreateTestStepRequest;
import org.qas.qtest.api.services.design.model.ListTestStepRequest;
import org.qas.qtest.api.services.design.model.TestStep;

import com.kms.katalon.integration.qtest.constants.StringConstants;
import com.kms.katalon.integration.qtest.entity.QTestTestStep;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationTestStepManager {
	public static QTestTestStep addTestStep(String projectDir, long projectId, long testCaseId, long testCaseVersionId,
			String description) throws Exception {
		String token = QTestSettingStore.getToken(projectDir);
		String serverUrl = QTestSettingStore.getServerUrl(projectDir);
		
		if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
			throw new QTestUnauthorizedException(StringConstants.QTEST_EXC_INVALID_TOKEN);
		}
		QTestCredentials credentials = new BasicQTestCredentials(token);
		TestDesignServiceAsyncClient testDesignService = new TestDesignServiceAsyncClient(credentials);
		testDesignService.setEndpoint(serverUrl);

		TestStep qTestTestStep = new TestStep().withDescription(description);

		CreateTestStepRequest createSteprequest = new CreateTestStepRequest().withProjectId(projectId)
				.withTestCaseId(testCaseId).withTestStep(qTestTestStep);
		TestStep qtTestStepResult = testDesignService.createTestStep(createSteprequest);

		if (qtTestStepResult != null) {
			int order = qtTestStepResult.getOrder();
			ListTestStepRequest listStepsRequest = new ListTestStepRequest().withProjectId(projectId)
					.withTestCaseId(testCaseId).withTestCaseVersion(testCaseVersionId);
			List<TestStep> lstStepsResult = testDesignService.listTestStep(listStepsRequest);
			if (listStepsRequest != null) {
				for (TestStep step : lstStepsResult) {
					if (step.getOrder() == order) {
						QTestTestStep qTestStepEntity = new QTestTestStep(step.getId());
						return qTestStepEntity;
					}
				}
			}
		}
		return null;
	}
}
