package com.kms.katalon.integration.qtest;

import java.util.List;

import org.qas.qtest.api.auth.BasicQTestCredentials;
import org.qas.qtest.api.auth.QTestCredentials;
import org.qas.qtest.api.services.design.TestDesignServiceAsyncClient;
import org.qas.qtest.api.services.design.model.CreateTestStepRequest;
import org.qas.qtest.api.services.design.model.ListTestStepRequest;
import org.qas.qtest.api.services.design.model.TestStep;

import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestTestStep;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;

/**
 * Provides a set of utility methods that relate with {@link QTestTestStep}
 */
public class QTestIntegrationTestStepManager {
    
    private QTestIntegrationTestStepManager() {
        //Disable default constructor
    }
    
    public static QTestTestStep addTestStep(IQTestCredential credential, long projectId, long testCaseId, long testCaseVersionId,
            String description) throws QTestUnauthorizedException, QTestInvalidFormatException {
        String token = credential.getToken().getAccessToken();
        String serverUrl = credential.getServerUrl();

        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
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
