package com.kms.katalon.integration.qtest;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.qas.api.internal.util.json.JsonArray;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;
import org.qas.qtest.api.auth.BasicQTestCredentials;
import org.qas.qtest.api.auth.QTestCredentials;
import org.qas.qtest.api.internal.model.FieldValue;
import org.qas.qtest.api.services.design.TestDesignServiceClient;
import org.qas.qtest.api.services.design.model.CreateTestCaseRequest;
import org.qas.qtest.api.services.design.model.GetTestCaseRequest;
import org.qas.qtest.api.services.design.model.ListTestStepRequest;
import org.qas.qtest.api.services.design.model.TestCase;
import org.qas.qtest.api.services.design.model.TestStep;

import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestEntity;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestStep;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.entity.QTestUser;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestIOException;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.helper.QTestHttpRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

/**
 * Provides a set of utility methods that relate with {@link QTestTestCase}
 */
public class QTestIntegrationTestCaseManager {

    private QTestIntegrationTestCaseManager() {
        // Disable default constructor
    }

    /**
     * Returns {@link QTestTestCase} by parsing the given <code>integratedEntity</code> that it's type is
     * {@link IntegratedType#TESTCASE}
     * 
     * @param integratedEntity
     * qTest {@link IntegratedEntity} of a {@link TestCaseEntity}
     * @return
     */
    public static QTestTestCase getQTestTestCaseByIntegratedEntity(IntegratedEntity integratedEntity) {
        if (integratedEntity == null || integratedEntity.getType() != IntegratedType.TESTCASE) {
            return null;
        }

        Map<String, String> properties = integratedEntity.getProperties();

        if (properties == null)
            return null;

        String id = properties.get(QTestEntity.ID_FIELD);
        String name = properties.get(QTestEntity.NAME_FIELD);
        String parentId = properties.get(QTestEntity.PARENT_ID_FIELD);
        String pid = properties.get(QTestEntity.PID_FIELD);
        String versionId = properties.get("versionId");

        QTestTestCase testCase = new QTestTestCase(Long.parseLong(id), name, Long.parseLong(parentId), pid);
        testCase.setVersionId(Long.parseLong(versionId));
        return testCase;
    }

    /**
     * Returns {@link IntegratedEntity} with {@link IntegratedType#TESTCASE} by parsing the given <code>qTestTC</code>
     * 
     * @param qTestTC
     * @return qTest {@link IntegratedEntity} of a {@link TestCaseEntity}
     */
    public static IntegratedEntity getIntegratedEntityByQTestTestCase(QTestTestCase qTestTC) {
        IntegratedEntity testCaseIntegratedEntity = new IntegratedEntity();

        testCaseIntegratedEntity.setProductName(QTestStringConstants.PRODUCT_NAME);
        testCaseIntegratedEntity.setType(IntegratedType.TESTCASE);

        testCaseIntegratedEntity.getProperties().put(QTestEntity.ID_FIELD, Long.toString(qTestTC.getId()));
        testCaseIntegratedEntity.getProperties().put(QTestEntity.NAME_FIELD, qTestTC.getName());
        testCaseIntegratedEntity.getProperties().put(QTestEntity.PARENT_ID_FIELD, Long.toString(qTestTC.getParentId()));
        testCaseIntegratedEntity.getProperties().put(QTestEntity.PID_FIELD, qTestTC.getPid());
        testCaseIntegratedEntity.getProperties().put("versionId", Long.toString(qTestTC.getVersionId()));

        return testCaseIntegratedEntity;
    }

    /**
     * Deletes the given <code>qTestTC</code> on qTest server
     * 
     * @param qTestTC
     * @param qTestProject
     * @param projectDir
     * @throws QTestException
     */
    public static void deleteTestCaseOnQTest(QTestTestCase qTestTC, QTestProject qTestProject,
            IQTestCredential credential) throws QTestException {
        if (qTestProject == null) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_PROJECT_NOT_FOUND);
        }

        Map<String, Object> bodyProperties = new LinkedHashMap<String, Object>();
        int testCaseType = QTestTestCase.getType();

        bodyProperties.put(QTestEntity.ID_FIELD, Integer.toString(testCaseType) + "-" + qTestTC.getId());
        bodyProperties.put(QTestEntity.OBJECT_ID_FIELD, qTestTC.getId());
        bodyProperties.put(QTestEntity.PARENT_ID_FIELD, qTestTC.getParentId());
        bodyProperties.put(QTestEntity.TYPE_FIELD, testCaseType);

        String url = "/p/" + Long.toString(qTestProject.getId()) + "/portal/tree/delete";

        QTestIntegrationAuthenticationManager.authenticate(credential.getUsername(), credential.getPassword());

        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("data", QTestHttpRequestHelper.createDataBody(bodyProperties, true)));
        QTestHttpRequestHelper.sendPostRequest(credential, url, postParams);
    }

    /**
     * Creates new {@link QTestTestCase} that:
     * <ul>
     * <li>Locates under the {@link QTestModule} that's id equal with the given <code>parentId</code></li>
     * <li>Its name's equal with the given <code>name</code></li>
     * <li>Its description's equal with the given <code>description</code></li>
     * </ul>
     * 
     * @param qTestProject
     * @param parentId
     * @param name
     * @param description
     * @param credential
     * qTest credential
     * @return
     * @throws QTestException
     */
    public static QTestTestCase addTestCase(QTestProject qTestProject, long parentId, String name, String description,
            IQTestCredential credential) throws QTestException {
        String serverUrl = credential.getServerUrl();
        String accessToken = credential.getToken().getAccessTokenHeader();
        if (!QTestIntegrationAuthenticationManager.validateToken(accessToken)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        QTestCredentials credentials = new BasicQTestCredentials(accessToken);
        TestDesignServiceClient testDesignService = new TestDesignServiceClient(credentials);
        testDesignService.setEndpoint(serverUrl);
        long projectId = qTestProject.getId();

        List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        try {
            JsonArray reponseJsonArray = getTestCaseFieldJsonArray(qTestProject.getId(), credential);

            QTestUser user = QTestIntegrationUserManager.getUser(qTestProject, credential);
            fieldValues.add(getTestCaseFieldValue("Type", "Automation", reponseJsonArray));

            FieldValue assignedUserField = getTestCaseFieldValue("Assigned To", "", reponseJsonArray);
            assignedUserField.setProperty("field_value", "[" + user.getId() + "]");
            fieldValues.add(assignedUserField);
        } catch (JsonException ex) {
            // Unable to get field value;
        }

        TestCase testCase = new TestCase().withName(name)
                .withDescription(getUploadedDescription(description))
                .withParentId(parentId)
                .withFieldValues(fieldValues);
        CreateTestCaseRequest request = new CreateTestCaseRequest().withProjectId(projectId).withTestCase(testCase);

        TestCase testCaseResult = testDesignService.createTestCase(request);
        QTestTestCase qTestCase = new QTestTestCase(testCaseResult.getId(), name, parentId, testCaseResult.getPid());
        qTestCase.setVersionId(testCaseResult.getTestCaseVersionId());

        return qTestCase;
    }

    /**
     * Updates the given <code>testCase</code> (type, assigned to,...) on qTest server
     * 
     * @param credential
     * qTest credential
     * @param qTestProject
     * @param testCase
     * @throws QTestException
     * thrown if system cannot send request or the response is invalid JSON format
     */
    @SuppressWarnings("unused")
    private static void updateTestCase(IQTestCredential credential, QTestProject qTestProject, QTestTestCase testCase)
            throws QTestException {
        String serverUrl = credential.getServerUrl();
        String url = serverUrl + "/api/v3/projects/" + Long.toString(qTestProject.getId()) + "/test-cases/"
                + testCase.getId();
        try {
            JsonArray reponseJsonArray = getTestCaseFieldJsonArray(qTestProject.getId(), credential);
            List<FieldValue> fieldValues = new ArrayList<FieldValue>();
            QTestUser user = QTestIntegrationUserManager.getUser(qTestProject, credential);
            JsonArray propertiesArray = new JsonArray();
            fieldValues.add(getTestCaseFieldValue("Type", "Automation", reponseJsonArray));

            FieldValue assignedUserField = getTestCaseFieldValue("Assigned To", "", reponseJsonArray);
            assignedUserField.setProperty("field_value", "[" + user.getId() + "]");
            fieldValues.add(assignedUserField);

            Map<String, Object> testCasePropertiesMap = new LinkedHashMap<String, Object>();
            for (FieldValue fieldValue : fieldValues) {
                JsonObject fieldJsonObject = new JsonObject();
                fieldJsonObject.put("field_id", fieldValue.getId());
                fieldJsonObject.put("field_value", fieldValue.getValue());
                propertiesArray.put(fieldJsonObject);
            }

            testCasePropertiesMap.put("properties", propertiesArray);
            QTestAPIRequestHelper.sendPostOrPutRequestViaAPI(url, credential.getToken(),
                    new JsonObject(testCasePropertiesMap).toString(), "PUT");
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(ex.getMessage());
        }
    }

    /**
     * The returned description of a {@link QTestTestCase} from qTest that contains HTML tag. Hence, this method will
     * convert the raw parameter to java string.
     * 
     * @param description
     * @return formated description
     */
    public static String getUploadedDescription(String description) {
        if (description == null)
            return "";
        StringBuilder descriptionBuilder = new StringBuilder();
        String[] stringLines = description.split("\n");
        for (String stringLine : stringLines) {
            descriptionBuilder.append("<p>").append(StringEscapeUtils.escapeHtml(stringLine)).append("</p>");
        }
        return descriptionBuilder.toString();
    }

    /**
     * Gets qTest fields of {@link QTestTestCase} via qTest API
     * 
     * @param projectId
     * @param credential
     * qTest credential
     * @return
     * @throws QTestException
     * thrown if system cannot send request or the response is invalid JSON format.
     */
    private static JsonArray getTestCaseFieldJsonArray(long projectId, IQTestCredential credential)
            throws QTestException {
        String serverUrl = credential.getServerUrl();

        String url = serverUrl + "/api/v3/projects/" + Long.toString(projectId) + "/settings/test-cases/fields";

        String response = QTestAPIRequestHelper.sendGetRequestViaAPI(url, credential.getToken());
        try {
            if (response == null || response.isEmpty())
                return null;
            JsonArray responseJsonArray = new JsonArray(response);

            return responseJsonArray;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(response);
        }
    }

    /**
     * Filters the given <code>responseJsonArray</code> to find a {@link FieldValue} that:
     * <ul>
     * <li>Its <code>label</code>'s equal with the given <code>fieldName</code></li>
     * <li>Its <code>allowed_values</code> has an item that's <code>label</code> equal with the given
     * <code>typeName</code></li>
     * </ul>
     * 
     * @param fieldName
     * @param typeName
     * @param responseJsonArray
     * @return
     * @throws JsonException
     */
    public static FieldValue getTestCaseFieldValue(String fieldName, String typeName, JsonArray responseJsonArray)
            throws JsonException {
        for (int index = 0; index < responseJsonArray.length(); index++) {
            JsonObject fieldJsonObject = responseJsonArray.getJsonObject(index);
            String responseFieldName = fieldJsonObject.getString("label");
            if (fieldName.equalsIgnoreCase(responseFieldName)) {
                if (fieldJsonObject.has("allowed_values")) {
                    JsonArray allowedValueJsonArray = fieldJsonObject.getJsonArray("allowed_values");

                    for (int entryIndex = 0; entryIndex < allowedValueJsonArray.length(); entryIndex++) {
                        JsonObject entryJsonObject = allowedValueJsonArray.getJsonObject(entryIndex);
                        if (typeName.equalsIgnoreCase(entryJsonObject.getString("label"))) {
                            FieldValue fieldValue = new FieldValue(fieldJsonObject.getLong(QTestEntity.ID_FIELD),
                                    entryJsonObject.getString("value"));
                            return fieldValue;
                        }
                    }
                }
                return new FieldValue(fieldJsonObject.getLong(QTestEntity.ID_FIELD), null);
            }
        }

        return null;
    }

    /**
     * Supporting method for {@link #getListSteps(String, long, QTestTestCase)}
     * 
     * @param credential
     * qTest credential
     * @param projectId
     * @param qTestId
     * @param qTestVersionId
     * @return
     * @throws QTestUnauthorizedException
     * @throws QTestInvalidFormatException
     */
    private static TestCase getTestCaseFromQTest(IQTestCredential credential, long projectId, long qTestId,
            long qTestVersionId) throws QTestUnauthorizedException, QTestInvalidFormatException {
        String accessToken = credential.getToken().getAccessTokenHeader();
        if (!QTestIntegrationAuthenticationManager.validateToken(accessToken)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }
        try {
            QTestCredentials credentials = new BasicQTestCredentials(accessToken);
            TestDesignServiceClient testDesignService = new TestDesignServiceClient(credentials);
            testDesignService.setEndpoint(credential.getServerUrl());

            GetTestCaseRequest getTestCaseRequest = new GetTestCaseRequest().withProjectId(projectId)
                    .withTestCaseId(qTestId)
                    .withTestCaseVersion(qTestVersionId);

            return testDesignService.getTestCase(getTestCaseRequest);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Used for navigating on UI Returns {@link URL} of the given <code>testCase</code> on qTest project.
     * 
     * @param qTestProject
     * @param testCase
     * @param credential
     * qTest credential
     * @return
     * @throws QTestIOException
     */
    public static URL navigatedUrlToQTestTestCase(QTestProject qTestProject, QTestTestCase testCase, String projectDir)
            throws QTestIOException {

        try {
            String url = QTestSettingStore.getServerUrl(projectDir);

            return new URL(url + "/p/" + Long.toString(qTestProject.getId()) + "/portal/project#id="
                    + Long.toString(testCase.getId()) + "&object=" + QTestTestCase.getType() + "&tab=testdesign");
        } catch (IOException ex) {
            throw new QTestIOException(ex);
        }
    }

    /**
     * Gets versionId of the {@link QTestTestCase} that's id equal with the given <code>testCaseId</code>
     * 
     * @param credential
     * qTest credential
     * @param projectId
     * @param testCaseId
     * @return versionId
     * @throws QTestException
     * thrown if system cannot send request or user's authentication is invalid.
     * @see {@link QTestTestCase#getVersionId()}
     */
    public static long getTestCaseVersionId(IQTestCredential credential, long projectId, long testCaseId)
            throws QTestException {
        String serverUrl = credential.getServerUrl();

        if (!QTestIntegrationAuthenticationManager.validateToken(credential.getToken().getAccessTokenHeader())) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }
        String url = serverUrl + "/api/v3/projects/" + Long.toString(projectId) + "/test-cases/"
                + Long.toString(testCaseId) + "/versions";

        String result = QTestAPIRequestHelper.sendGetRequestViaAPI(url, credential.getToken());
        try {
            JsonArray childrenJsonArray = new JsonArray(result);

            int versionSize = childrenJsonArray.length();
            if (versionSize == 0) {
                return 0L;
            }

            JsonObject childJsonObject = childrenJsonArray.getJsonObject(versionSize - 1);
            return childJsonObject.getLong("test_case_version_id");
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(result);
        }
    }

    /**
     * Gets list of test steps of a qTestCase via qTest API
     */
    public static List<QTestStep> getListSteps(IQTestCredential credential, long projectId, QTestTestCase testCase)
            throws QTestException {
        String accessToken = credential.getToken().getAccessTokenHeader();

        if (!QTestIntegrationAuthenticationManager.validateToken(accessToken)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        if (testCase.getVersionId() <= 0) {
            long testCaseVersionId = getTestCaseVersionId(credential, projectId, testCase.getId());
            testCase.setVersionId(testCaseVersionId);

            TestCase testCaseFromQTest = getTestCaseFromQTest(credential, projectId, testCase.getId(),
                    testCaseVersionId);
            // replace <p> and </p> to empty strings because the returned
            // description
            // from qTest includes them
            String description = StringEscapeUtils
                    .unescapeHtml(testCaseFromQTest.getDescription().replace("<p>", "").replace("</p>", ""));
            testCase.setDescription(description);
        }

        QTestCredentials credentials = new BasicQTestCredentials(accessToken);
        TestDesignServiceClient testDesignService = new TestDesignServiceClient(credentials);
        testDesignService.setEndpoint(credential.getServerUrl());

        ListTestStepRequest testStepRequest = new ListTestStepRequest().withProjectId(projectId)
                .withTestCaseId(testCase.getId())
                .withTestCaseVersion(testCase.getVersionId());
        List<TestStep> result = testDesignService.listTestStep(testStepRequest);

        List<QTestStep> qTestSteps = new ArrayList<QTestStep>();
        for (TestStep testStep : result) {
            QTestStep qTestStep = new QTestStep();
            qTestStep.setDescription(testStep.getDescription());
            qTestStep.setExpectedResult(testStep.getExpected());

            qTestSteps.add(qTestStep);
        }

        return qTestSteps;
    }
}
