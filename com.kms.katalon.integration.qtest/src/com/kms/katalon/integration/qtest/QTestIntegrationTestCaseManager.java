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
import org.qas.qtest.api.services.design.model.GetTestCaseRequest;
import org.qas.qtest.api.services.design.model.ListTestStepRequest;
import org.qas.qtest.api.services.design.model.TestCase;
import org.qas.qtest.api.services.design.model.TestStep;

import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.entity.QTestEntity;
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
        //Disable default constructor
    }

    public static QTestTestCase getQTestTestCaseByIntegratedEntity(IntegratedEntity integratedEntity) {
        if (integratedEntity == null || integratedEntity.getType() != IntegratedType.TESTCASE) {
            return null;
        }

        Map<String, String> properties = integratedEntity.getProperties();

        if (properties == null) return null;

        String id = properties.get(QTestEntity.ID_FIELD);
        String name = properties.get(QTestEntity.NAME_FIELD);
        String parentId = properties.get(QTestEntity.PARENT_ID_FIELD);
        String pid = properties.get(QTestEntity.PID_FIELD);
        String versionId = properties.get("versionId");

        QTestTestCase testCase = new QTestTestCase(Long.parseLong(id), name, Long.parseLong(parentId), pid);
        testCase.setVersionId(Long.parseLong(versionId));
        return testCase;
    }

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

    public static void deleteTestCaseOnQTest(QTestTestCase qTestTC, QTestProject qTestProject, String projectDir)
            throws QTestException {
        if (qTestProject == null) {
            throw new QTestUnauthorizedException(
                    "Cannot find qTest project. Please select a qTest project on qTest setting page.");
        }

        Map<String, Object> bodyProperties = new LinkedHashMap<String, Object>();
        int testCaseType = QTestTestCase.getType();

        bodyProperties.put(QTestEntity.ID_FIELD, Integer.toString(testCaseType) + "-" + qTestTC.getId());
        bodyProperties.put(QTestEntity.OBJECT_ID_FIELD, qTestTC.getId());
        bodyProperties.put(QTestEntity.PARENT_ID_FIELD, qTestTC.getParentId());
        bodyProperties.put(QTestEntity.TYPE_FIELD, testCaseType);

        String serverUrl = QTestSettingStore.getServerUrl(projectDir);

        String url = "/p/" + Long.toString(qTestProject.getId()) + "/portal/tree/delete";

        String username = QTestSettingStore.getUsername(projectDir);
        String password = QTestSettingStore.getPassword(projectDir);

        QTestIntegrationAuthenticationManager.authenticate(username, password);

        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("data", QTestHttpRequestHelper.createDataBody(bodyProperties, true)));
        QTestHttpRequestHelper.sendPostRequest(serverUrl, url, username, password, postParams);
    }

    public static QTestTestCase addTestCase(QTestProject qTestProject, long parentId, String name, String description,
            String preCondition, String projectDir) throws QTestException {
        String token = QTestSettingStore.getToken(projectDir);
        String serverUrl = QTestSettingStore.getServerUrl(projectDir);

        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        long projectId = qTestProject.getId();

        Map<String, Object> testCasePropertiesMap = new LinkedHashMap<String, Object>();
        testCasePropertiesMap.put(QTestEntity.NAME_FIELD, name);
        testCasePropertiesMap.put("parent_id", parentId);
        //testCasePropertiesMap.put("description", getUploadedDescription(description));
        testCasePropertiesMap.put("properties", new JsonArray());

        String url = serverUrl + "/api/v3/projects/" + Long.toString(projectId) + "/test-cases";
        String responseResult = QTestAPIRequestHelper.sendPostRequestViaAPI(url, token, new JsonObject(
                testCasePropertiesMap).toString());
        try {
            if (responseResult != null && !responseResult.isEmpty()) {
                JsonObject testCaseJsonObject = new JsonObject(responseResult);

                QTestTestCase qTestTestCase = new QTestTestCase(testCaseJsonObject.getLong(QTestEntity.ID_FIELD), name,
                        parentId, testCaseJsonObject.getString(QTestEntity.PID_FIELD));
                qTestTestCase.setVersionId(testCaseJsonObject.getLong("test_case_version_id"));

                updateTestCase(projectDir, qTestProject, qTestTestCase);
                return qTestTestCase;
            } else {
                return null;
            }
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(responseResult);
        }
    }

    private static void updateTestCase(String projectDir, QTestProject qTestProject, QTestTestCase testCase)
            throws QTestException {
        String token = QTestSettingStore.getToken(projectDir);
        String serverUrl = QTestSettingStore.getServerUrl(projectDir);
        String url = serverUrl + "/api/v3/projects/" + Long.toString(qTestProject.getId()) + "/test-cases/"
                + testCase.getId();
        try {
            JsonArray reponseJsonArray = getTestCaseFieldJsonArray(qTestProject.getId(), projectDir);
            List<FieldValue> fieldValues = new ArrayList<FieldValue>();
            QTestUser user = QTestIntegrationUserManager.getUser(qTestProject, projectDir);
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
            QTestAPIRequestHelper.sendPostOrPutRequestViaAPI(url, token,
                    new JsonObject(testCasePropertiesMap).toString(), "PUT");
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(ex.getMessage());
        }
    }

    public static String getUploadedDescription(String description) {
        if (description == null) return "";
        StringBuilder descriptionBuilder = new StringBuilder();
        String[] stringLines = description.split("\n");
        for (String stringLine : stringLines) {
            descriptionBuilder.append("<p>").append(StringEscapeUtils.escapeHtml(stringLine)).append("</p>");
        }
        return descriptionBuilder.toString();
    }

    private static JsonArray getTestCaseFieldJsonArray(long projectId, String projectDir) throws QTestException {
        String serverUrl = QTestSettingStore.getServerUrl(projectDir);
        String token = QTestSettingStore.getToken(projectDir);

        String url = serverUrl + "/api/v3/projects/" + Long.toString(projectId) + "/settings/test-cases/fields";

        String response = QTestAPIRequestHelper.sendGetRequestViaAPI(url, token);
        try {
            if (response == null || response.isEmpty()) return null;
            JsonArray responseJsonArray = new JsonArray(response);

            return responseJsonArray;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(response);
        }
    }

    private static FieldValue getTestCaseFieldValue(String fieldName, String typeName, JsonArray responseJsonArray)
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

    public static TestCase getTestCaseFromQTest(String serverUrl, String token, long projectId, long qTestId,
            long qTestVersionId) throws QTestUnauthorizedException {
        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }
        try {
            QTestCredentials credentials = new BasicQTestCredentials(token);
            TestDesignServiceClient testDesignService = new TestDesignServiceClient(credentials);
            testDesignService.setEndpoint(serverUrl);

            GetTestCaseRequest getTestCaseRequest = new GetTestCaseRequest().withProjectId(projectId)
                    .withTestCaseId(qTestId).withTestCaseVersion(qTestVersionId);

            return testDesignService.getTestCase(getTestCaseRequest);
        } catch (Exception ex) {
            return null;
        }
    }

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

    public static long getTestCaseVersionId(String projectDir, long projectId, long testCaseId) throws QTestException {
        String token = QTestSettingStore.getToken(projectDir);

        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        String serverUrl = QTestSettingStore.getServerUrl(projectDir);

        String url = "/p/" + Long.toString(projectId) + "/portal/project/testdesign/testcase/testcase?testcaseId="
                + Long.toString(testCaseId);

        String username = QTestSettingStore.getUsername(projectDir);
        String password = QTestSettingStore.getPassword(projectDir);

        String response = QTestHttpRequestHelper.sendGetRequest(serverUrl, url, username, password);

        Document htmlDocument = Jsoup.parse(response);
        Element testCaseVersionIdElement = htmlDocument.getElementById("qas-testdesign-testcase-testcase-propTcvId");
        return Long.valueOf(testCaseVersionIdElement.text());
    }

    /**
     * Gets list of test steps of a qTestCase via qTest API
     */
    public static List<QTestStep> getListSteps(String projectDir, long projectId, QTestTestCase testCase)
            throws QTestException {
        String token = QTestSettingStore.getToken(projectDir);
        String serverUrl = QTestSettingStore.getServerUrl(projectDir);

        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        if (testCase.getVersionId() <= 0) {
            long testCaseVersionId = getTestCaseVersionId(projectDir, projectId, testCase.getId());
            testCase.setVersionId(testCaseVersionId);

            TestCase testCaseFromQTest = getTestCaseFromQTest(serverUrl, token, projectId, testCase.getId(),
                    testCaseVersionId);
            // replace <p> and </p> to empty strings because the returned
            // description
            // from qTest includes them
            String description = StringEscapeUtils.unescapeHtml(testCaseFromQTest.getDescription().replace("<p>", "")
                    .replace("</p>", ""));
            testCase.setDescription(description);
        }

        QTestCredentials credentials = new BasicQTestCredentials(token);
        TestDesignServiceClient testDesignService = new TestDesignServiceClient(credentials);
        testDesignService.setEndpoint(serverUrl);

        ListTestStepRequest testStepRequest = new ListTestStepRequest().withProjectId(projectId)
                .withTestCaseId(testCase.getId()).withTestCaseVersion(testCase.getVersionId());
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
