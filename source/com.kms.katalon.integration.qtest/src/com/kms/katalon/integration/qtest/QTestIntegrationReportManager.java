package com.kms.katalon.integration.qtest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.qas.api.internal.util.json.JsonArray;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.entity.QTestDefect;
import com.kms.katalon.integration.qtest.entity.QTestDefectField;
import com.kms.katalon.integration.qtest.entity.QTestEntity;
import com.kms.katalon.integration.qtest.entity.QTestExecutionStatus;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestReport;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestStepLog;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestAttachmentSendingType;
import com.kms.katalon.integration.qtest.setting.QTestReportFormatType;
import com.kms.katalon.integration.qtest.setting.QTestResultSendingType;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;
import com.kms.katalon.integration.qtest.util.DateUtil;
import com.kms.katalon.integration.qtest.util.ZipUtil;
import com.kms.katalon.jasper.pdf.TestCasePdfGenerator;
import com.kms.katalon.jasper.pdf.exception.JasperReportException;

/**
 * Provides a set of utility methods that relate with {@link QTestReport}
 */
public class QTestIntegrationReportManager {

    private QTestIntegrationReportManager() {
        // Disable default constructor.
    }

    /**
     * Returns {@link URL} that navigates with given <code>qTestRun</code> on qTest
     * <p>
     * Used for navigating on UI
     * 
     * @param projectDir
     * @param qTestProject
     * @param qTestRun
     * @param qTestLog
     * @return
     * @throws IOException
     */
    public static URL getTestLogURL(String projectDir, QTestProject qTestProject, QTestRun qTestRun, QTestLog qTestLog)
            throws IOException {
        String url = QTestSettingStore.getServerUrl(projectDir);

        return new URL(url + "/p/" + Long.toString(qTestProject.getId()) + "/portal/project#tab=testexecution&object="
                + QTestRun.getType() + "&id=" + Long.toString(qTestRun.getId()));
    }

    public static QTestReport getQTestReportByIntegratedEntity(IntegratedEntity reportIntegratedEntity)
            throws QTestInvalidFormatException {
        if (reportIntegratedEntity == null) return null;

        try {
            Map<String, String> properties = new TreeMap<String, String>(reportIntegratedEntity.getProperties());
            QTestReport qTestReport = new QTestReport();

            for (Entry<String, String> entry : properties.entrySet()) {
                String value = entry.getValue();
                JsonObject testLogJsonObject = new JsonObject(value.replace("'", "\"").replace("},\n", "},"));

                QTestLog qTestLog = new QTestLog();
                qTestLog.setId(testLogJsonObject.getLong(QTestEntity.ID_FIELD));
                qTestLog.setName(testLogJsonObject.getString(QTestEntity.NAME_FIELD));
                qTestLog.setAttachmentIncluded(testLogJsonObject.getBoolean("attachmentIncluded"));

                qTestReport.getTestLogMap().put(Integer.parseInt(entry.getKey()), qTestLog);
            }

            return qTestReport;
        } catch (JsonException ex) {
            throw new QTestInvalidFormatException(ex.getMessage());
        }
    }

    public static IntegratedEntity getIntegratedEntityByQTestReport(QTestReport qTestReport) {
        if (qTestReport == null) return null;
        IntegratedEntity integratedEntity = new IntegratedEntity();
        integratedEntity.setProductName(QTestStringConstants.PRODUCT_NAME);
        integratedEntity.setType(IntegratedType.REPORT);

        for (Entry<Integer, QTestLog> entry : qTestReport.getTestLogMap().entrySet()) {
            String key = Integer.toString(entry.getKey());
            StringBuilder valueBuilder = new StringBuilder(new JsonObject(entry.getValue().getProperties()).toString());
            String value = valueBuilder.toString().replace("\"", "'").replace("},", "},\n");

            integratedEntity.getProperties().put(key, value);
        }

        return integratedEntity;
    }

    /**
     * Uploads the given <code>preparedTestCaseResult</code> to qTest.
     * <p>
     * Returns new {@link QTestLog} after uploading and parsing the response.
     * 
     * @param projectDir
     * @param preparedTestCaseResult
     *            the prepared test log that's formated by user
     * @param tempDir
     *            qTest folder inside Katalon temporary folder used for creating ZIP file
     * @param logFolder
     *            the folder that contains the ZIP file a.k.a attachment
     * @return
     * @throws QTestException
     * @throws IOException
     * @throws JasperReportException
     * @throws URISyntaxException 
     */
    public static QTestLog uploadTestLog(String projectDir, QTestLogUploadedPreview preparedTestCaseResult,
            String tempDir, TestSuiteLogRecord testSuiteLogRecord) throws QTestException, IOException,
            JasperReportException, URISyntaxException {

        QTestProject qTestProject = preparedTestCaseResult.getQTestProject();
        QTestTestCase qTestCase = preparedTestCaseResult.getQTestCase();
        QTestRun qTestRun = preparedTestCaseResult.getQTestRun();
        QTestLog qTestLog = preparedTestCaseResult.getQTestLog();
        TestCaseLogRecord testCaseLogRecord = preparedTestCaseResult.getTestCaseLogRecord();
        File logFolder = new File(testSuiteLogRecord.getLogFolder());

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_FORMAT);
        String startDate = sdf.format(testCaseLogRecord.getStartTime());
        String endDate = sdf.format(testCaseLogRecord.getEndTime());
        Long testLongVersionId = qTestCase.getVersionId();
        String message = (qTestLog != null) ? qTestLog.getMessage() : testCaseLogRecord.getMessage();

        String serverUrl = QTestSettingStore.getServerUrl(projectDir);
        String token = QTestSettingStore.getToken(projectDir);

        TestStatusValue testCaseStatus = testCaseLogRecord.getStatus().getStatusValue();

        QTestExecutionStatus qTestExStatus = getMappingStatus(qTestProject.getId(), serverUrl, token, testCaseStatus);
        if (qTestExStatus == null) {
            throw new QTestInvalidFormatException(MessageFormat.format(
                    QTestMessageConstants.QTEST_EXC_INVALID_LOG_STATUS, testCaseLogRecord.getStatus().getStatusValue()
                            .name()));
        }

        Map<String, Object> statusProperties = new HashMap<String, Object>();
        statusProperties.put(QTestEntity.ID_FIELD, qTestExStatus.getId());

        Map<String, Object> bodyProperties = new HashMap<String, Object>();
        bodyProperties.put("exe_start_date", startDate);
        bodyProperties.put("exe_end_date", endDate);
        bodyProperties.put("status", new JsonObject(statusProperties));
        bodyProperties.put("test_case_version_id", testLongVersionId);
        bodyProperties.put("note", QTestIntegrationTestCaseManager.getUploadedDescription(message));

        boolean attachmentIncluded = false;

        if ((qTestLog != null && qTestLog.isAttachmentIncluded())
                || (qTestLog == null && isAvailableForSendingAttachment(testCaseStatus, projectDir))) {

            List<JsonObject> jsonObjects = new ArrayList<JsonObject>();

            File logTempFolder = new File(tempDir, FilenameUtils.getBaseName(logFolder.getName()));
            if (logTempFolder.exists()) {
                FileUtils.deleteDirectory(logTempFolder);
            }

            logTempFolder.mkdirs();

            for (QTestReportFormatType formatType : QTestSettingStore.getFormatReportTypes(projectDir)) {
                if (formatType == QTestReportFormatType.LOG) {
                    for (File reportEntry : logFolder.listFiles()) {
                        if (!isValidFileToAttach(reportEntry, projectDir)) {
                            continue;
                        }
                        QTestReportFormatType format = QTestReportFormatType.getTypeByExtension(FilenameUtils
                                .getExtension(reportEntry.getAbsolutePath()));
                        if (format == QTestReportFormatType.LOG) {
                            moveReportFile(reportEntry, new File(logTempFolder, reportEntry.getName()));
                        }
                    }
                } else {
                    File newReportFile = new File(logTempFolder, FilenameUtils.getBaseName(logFolder.getAbsolutePath())
                            + "." + formatType.getFileExtension());
                    generateReportFile(formatType, newReportFile, testCaseLogRecord, testSuiteLogRecord);
                }
            }

            for (File reportTempFile : logTempFolder.listFiles()) {
                jsonObjects.add(getAttachmentJsonObject(reportTempFile.getAbsolutePath()));
            }

            bodyProperties.put("attachments", new JsonArray(jsonObjects));
            attachmentIncluded = true;

            FileUtils.cleanDirectory(logTempFolder);
        }

        // upload result of the given test run
        String result = uploadTestResult(serverUrl, token, qTestProject.getId(), qTestRun.getId(), new JsonObject(
                bodyProperties).toString());

        try {
            if (result != null && !result.isEmpty()) {
                JsonObject testLogJsonObject = new JsonObject(result);
                QTestLog returnedQTestLog = new QTestLog();
                returnedQTestLog.setId(testLogJsonObject.getLong(QTestEntity.ID_FIELD));
                returnedQTestLog.setName(qTestCase.getName());
                returnedQTestLog.setAttachmentIncluded(attachmentIncluded);
                returnedQTestLog.setMessage(message);
                return returnedQTestLog;
            } else {
                return null;
            }
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(result);
        }
    }

    private static void moveReportFile(File sourceReportFile, File destReportFile) throws IOException {
        FileUtils.copyFile(sourceReportFile, destReportFile);
    }

    private static void generateReportFile(QTestReportFormatType format, File destReportFile,
            TestCaseLogRecord testCaseLogRecord, TestSuiteLogRecord testSuiteLR) throws IOException,
            JasperReportException, URISyntaxException {
        switch (format) {
            case CSV:
                ReportUtil.writeLogRecordToCSVFile(testSuiteLR, destReportFile,
                        Arrays.asList(new ILogRecord[] { testCaseLogRecord }));
                break;
            case HTML:
                ReportUtil.writeLogRecordToHTMLFile(testSuiteLR, destReportFile,
                        Arrays.asList(new ILogRecord[] { testCaseLogRecord }));
                break;
            case PDF:
                TestCasePdfGenerator generator = new TestCasePdfGenerator(testCaseLogRecord, testSuiteLR.getLogFolder());
                generator.exportToPDF(destReportFile.getAbsolutePath());
                break;
            default:
                break;
        }

    }

    private static boolean isValidFileToAttach(File file, String projectDir) {
        String fileExt = FilenameUtils.getExtension(file.getAbsolutePath());
        return QTestSettingStore.getFormatReportTypes(projectDir).contains(
                QTestReportFormatType.getTypeByExtension(fileExt));
    }

    private static JsonObject getAttachmentJsonObject(String filePath) throws QTestException, IOException {
        QTestReportFormatType format = QTestReportFormatType.getTypeByExtension(FilenameUtils.getExtension(filePath));
        String contentType = "";
        if (format == null) {
            contentType = "application/octet-stream";
        }
        switch (format) {
            case CSV:
                contentType = "text/csv";
                break;
            case HTML:
                contentType = "text/html";
                break;
            case LOG:
                contentType = "application/xml";
                break;
            case PDF:
                contentType = "application/pdf";
                break;
            default:
                break;
        }
        Map<String, Object> attachmentMap = new LinkedHashMap<String, Object>();
        attachmentMap.put("name", FilenameUtils.getName(filePath));
        attachmentMap.put("content_type", contentType);
        attachmentMap.put("data", ZipUtil.encodeFileContent(filePath));

        return new JsonObject(attachmentMap);
    }

    private static boolean isAvailableForSendingAttachment(TestStatusValue status, String projectDir) {
        QTestAttachmentSendingType statusSendingType = null;
        switch (status) {
            case ERROR:
                statusSendingType = QTestAttachmentSendingType.SEND_IF_FAILS;
                break;
            case FAILED:
                statusSendingType = QTestAttachmentSendingType.SEND_IF_FAILS;
                break;
            case PASSED:
                statusSendingType = QTestAttachmentSendingType.SEND_IF_PASSES;
                break;
            default:
                break;
        }

        return QTestSettingStore.getAttachmentSendingTypes(projectDir).contains(statusSendingType);
    }

    public static boolean isAvailableForSendingResult(TestStatusValue status, String projectDir) {
        QTestResultSendingType statusSendingType = null;
        switch (status) {
            case ERROR:
                statusSendingType = QTestResultSendingType.SEND_IF_FAILS;
                break;
            case FAILED:
                statusSendingType = QTestResultSendingType.SEND_IF_FAILS;
                break;
            case PASSED:
                statusSendingType = QTestResultSendingType.SEND_IF_PASSES;
                break;
            default:
                break;
        }

        if (statusSendingType == null) {
            return false;
        }

        return QTestSettingStore.getResultSendingTypes(projectDir).contains(statusSendingType);
    }

    /**
     * Uploads the request that contains all the parameters to qTest via qTest API Returns the response body content
     * 
     * @param serverUrl
     * @param token
     * @param projectId
     * @param testRunId
     * @param postBody
     * @return the reponse body content
     * @throws QTestException
     *             thrown if system cannot send request
     */
    public static String uploadTestResult(String serverUrl, String token, long projectId, long testRunId,
            String postBody) throws QTestException {
        String url = String.format(serverUrl + "/api/v3/projects/%s/test-runs/%s/test-logs", projectId, testRunId);
        String resText = QTestAPIRequestHelper.sendPostRequestViaAPI(url, token, postBody);
        return resText;
    }

    /**
     * Uploads the request that contains all the parameters to qTest via qTest API Returns the response body content
     * 
     * @param serverUrl
     * @param token
     * @param projectId
     * @param postBody
     * @return
     * @throws QTestException
     */
    public static QTestDefect submitDefect(String serverUrl, String token, long projectId, String postBody)
            throws QTestException {
        String url = String.format(serverUrl + "/api/v3/projects/%s/defects", projectId);
        String res = QTestAPIRequestHelper.sendPostRequestViaAPI(url, token, postBody);

        try {
            JsonObject jo = new JsonObject(res);
            QTestDefect defect = new QTestDefect(jo.getLong("id"), "");
            defect.setGid(jo.getString("pid"));

            return defect;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(res);
        }
    }

    /**
     * For result submission, the result's format of Katalon has difference status with qTest status's result.
     * <p>
     * This method will converts from Katalon {@link TestStatusValue} to the correct {@link TestStatusValue}
     * <p>
     * 
     * @param projectId
     * @param serverUrl
     * @param token
     * @param status
     * @return
     * @throws QTestException
     * @see {@link TestStatusValue}
     */
    public static QTestExecutionStatus getMappingStatus(long projectId, String serverUrl, String token,
            TestStatusValue status) throws QTestException {
        String mappingValue = QTestExecutionStatus.getMappedValue(status.name());
        for (QTestExecutionStatus qTestStatus : getMappingStatuses(projectId, serverUrl, token)) {
            if (qTestStatus.getName().equalsIgnoreCase(mappingValue)) {
                return qTestStatus;
            }
        }
        return null;
    }

    /**
     * Gets list of {@link QTestExecutionStatus} of qTest
     * 
     * @param projectId
     * @param serverUrl
     * @param token
     * @return
     * @throws QTestException
     */
    public static List<QTestExecutionStatus> getMappingStatuses(long projectId, String serverUrl, String token)
            throws QTestException {
        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }
        List<QTestExecutionStatus> list = new ArrayList<QTestExecutionStatus>();
        String jsonString = QTestAPIRequestHelper.sendGetRequestViaAPI(serverUrl + "/api/v3/projects/" + projectId
                + "/test-runs/execution-statuses", token);
        try {
            JsonArray jArr = new JsonArray(jsonString);
            for (int i = 0; i < jArr.length(); i++) {
                JsonObject jo = jArr.getJsonObject(i);
                String name = jo.getString("name");
                long id = jo.getLong("id");
                boolean isDefault = jo.getBoolean("is_default");
                QTestExecutionStatus status = new QTestExecutionStatus(id, name);
                status.setDefault(isDefault);
                list.add(status);
            }
            return list;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(jsonString);
        }
    }

    public static List<QTestDefectField> getDefectFields(String serverUrl, String token, long projectId)
            throws QTestException {
        List<QTestDefectField> list = new ArrayList<QTestDefectField>();
        String url = serverUrl + "/api/v3/projects/" + projectId + "/defects/fields";
        String json = QTestAPIRequestHelper.sendGetRequestViaAPI(url, token);
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
    public static List<QTestStepLog> getStepLogs(String serverUrl, String authenToken, long qTestProjectId,
            long qTestRunId) throws QTestException {
        List<QTestStepLog> stepLogs = new ArrayList<QTestStepLog>();
        String url = String.format("%s/api/v3/projects/%s/test-runs/%s/test-logs/last-run?expand=teststeplog.teststep",
                serverUrl, qTestProjectId, qTestRunId);
        String resText = QTestAPIRequestHelper.sendGetRequestViaAPI(url, authenToken);
        try {
            JsonObject jo = new JsonObject(resText);

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
            throw QTestInvalidFormatException.createInvalidJsonFormatException(resText);
        }
    }
}
