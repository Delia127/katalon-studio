package com.kms.katalon.integration.qtest;

import java.util.ArrayList;
import java.util.List;

import org.qas.api.internal.util.json.JsonArray;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestDefect;
import com.kms.katalon.integration.qtest.entity.QTestDefectField;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestStepLog;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestAttachmentSendingType;

/**
 * Provides a set of utility methods for qTest execution (includes:
 * {@link QTestLog}, {@link QTestAttachmentSendingType}, {@link QTestDefect}).
 * 
 */
public class QTestIntegrationExecutionManager {

    private QTestIntegrationExecutionManager() {
        // Disable default contructor
    }

    public static String numberFieldFormat(long fieldId, long fieldValue) {
        return String.format("{ \"field_id\": %s, \"field_value\": %s }", fieldId, fieldValue);
    }

    public static String stringFieldFormat(long fieldId, String fieldValue) {
        return String.format("{ \"field_id\": %s, \"field_value\": \"%s\" }", fieldId, fieldValue);
    }

    public static QTestDefect submitDefect(IQTestCredential credential, long projectId, String postBody)
            throws QTestException {
        String url = String.format(credential.getServerUrl() + "/api/v3/projects/%s/defects", projectId);
        String res = QTestAPIRequestHelper.sendPostRequestViaAPI(url, credential.getToken(), postBody);

        try {
            JsonObject jo = new JsonObject(res);
            QTestDefect defect = new QTestDefect(jo.getLong("id"), "");
            defect.setGid(jo.getString("pid"));

            return defect;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(res);
        }
    }

    public static List<QTestRun> getTestRuns(IQTestCredential credential, long projectId, long testSuiteId)
            throws QTestException {
        if (!QTestIntegrationAuthenticationManager.validateToken(credential.getToken().getAccessToken())) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        List<QTestRun> list = new ArrayList<QTestRun>();
        String json = QTestAPIRequestHelper.sendGetRequestViaAPI(credential.getServerUrl() + "/api/v3/projects/" + projectId
                + "/test-runs?testSuiteId=" + testSuiteId, credential.getToken());
        try {
            List<String> testRunURLs = parseJsonToGetTestRunURLs(json);
            for (String url : testRunURLs) {
                json = QTestAPIRequestHelper.sendGetRequestViaAPI(url, credential.getToken());
                JsonObject jo = new JsonObject(json);

                long runId = jo.getLong("id");
                String runName = jo.getString("name");

                QTestRun qTestRun = new QTestRun(runId, runName);
                list.add(qTestRun);

                qTestRun.setExecuted(jo.getBoolean("executed"));
                qTestRun.setStatusId(jo.getLong("status_id"));
                qTestRun.setTestCaseVersionId(jo.getLong("test_case_version_id"));
                qTestRun.setOrder(jo.getLong("order"));

                JsonArray jArr = jo.getJsonArray("links");
                for (int i = 0; i < jArr.length(); i++) {
                    JsonObject objLink = jArr.getJsonObject(i);
                    String rel = objLink.getString("rel");
                    String href = objLink.getString("href");
                    if (rel.equals("self")) {
                        qTestRun.setHref(href);
                    } else if (rel.equals("test-case")) {
                        qTestRun.setTestCaseLink(href);
                        if (href != null && !href.equals("")) {
                            String strId = href.substring(href.indexOf("/test-cases/") + "/test-cases/".length(),
                                    href.indexOf("/versions/"));
                            qTestRun.setQTestCaseId(Long.parseLong(strId));
                        }
                    } else if (rel.equals("test-logs")) {
                        qTestRun.setTestLogsLink(href);
                    } else if (rel.equals("status-options")) {
                        qTestRun.setExecutionStatusesLink(href);
                    }
                }
            }
            return list;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(json);
        }
    }

  

    private static List<String> parseJsonToGetTestRunURLs(String jsonString) throws QTestInvalidFormatException {
        try {
            List<String> testRuns = new ArrayList<String>();
            JsonArray jarr = new JsonArray(jsonString);
            for (int i = 0; i < jarr.length(); i++) {
                JsonArray links = jarr.getJsonObject(i).getJsonArray("links");
                for (int j = 0; j < links.length(); j++) {
                    JsonObject link = links.getJsonObject(j);
                    if (link.getString("href") != null) {
                        testRuns.add(link.getString("href"));
                        break;
                    }
                }
            }
            return testRuns;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(jsonString);
        }
    }

    public static List<QTestDefectField> getDefectFields(IQTestCredential credential, long projectId)
            throws QTestException {
        List<QTestDefectField> list = new ArrayList<QTestDefectField>();
        String url = credential.getServerUrl() + "/api/v3/projects/" + projectId + "/defects/fields";
        String json = QTestAPIRequestHelper.sendGetRequestViaAPI(url, credential.getToken());
        try {
            JsonArray jArr = new JsonArray(json);
            for (int i = 0; i < jArr.length(); i++) {
                JsonObject jo = jArr.getJsonObject(i);
                QTestDefectField field = parseToGetDefectField(jo, new QTestDefectField());
                if (jo.has("allowed_values")) {
                    JsonArray allowedValues = new JsonArray(jo.getString("allowed_values"));
                    for (int j = 0; j < allowedValues.length(); j++) {
                        JsonObject valueObject = allowedValues.getJsonObject(j);
                        QTestDefectField subField = parseToGetDefectField(valueObject, new QTestDefectField());
                        subField.setMainField(field);
                        field.getAllowedValues().add(subField);
                    }
                }
                list.add(field);
            }
            return list;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(json);
        }
    }

    private static QTestDefectField parseToGetDefectField(JsonObject jo, QTestDefectField field) throws JsonException {
        String name = jo.getString("label");
        long id = jo.has("id") ? jo.getLong("id") : jo.getLong("value");

        if (field == null) {
            field = new QTestDefectField(id, name);
        } else {
            field.setId(id);
            field.setName(name);
        }
        return field;
    }

    // TODO: consider a case multi-user, the last run may be not correct
    public static List<QTestStepLog> getStepLogs(IQTestCredential credential, long qTestProjectId,
            long qTestRunId) throws QTestException {
        List<QTestStepLog> stepLogs = new ArrayList<QTestStepLog>();
        String url = String.format("%s/api/v3/projects/%s/test-runs/%s/test-logs/last-run?expand=teststeplog.teststep",
                credential.getServerUrl(), qTestProjectId, qTestRunId);
        String response = QTestAPIRequestHelper.sendGetRequestViaAPI(url, credential.getToken());
        try {
            JsonObject jo = new JsonObject(response);

            JsonArray jarrStepLogs = jo.getJsonArray("test_step_logs");
            for (int i = 0; i < jarrStepLogs.length(); i++) {
                jo = jarrStepLogs.getJsonObject(i);
                QTestStepLog stepLog = new QTestStepLog();
                stepLog.setqTestStepId(jo.getLong("test_step_id"));
                stepLogs.add(stepLog);

                JsonArray jarrLinks = jo.getJsonArray("links");
                for (int j = 0; j < jarrLinks.length(); j++) {
                    String rel = jarrLinks.getJsonObject(j).getString("rel");
                    if (rel.equals("self")) {
                        String href = jarrLinks.getJsonObject(j).getString("href");
                        String strId = href.substring(href.indexOf("/test-steps/") + "/test-steps/".length());

                        stepLog.setId(Long.parseLong(strId));
                        stepLog.setSelfLink(href);

                        break;
                    }
                }
            }
            return stepLogs;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(response);
        }
    }
}
