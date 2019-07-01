package com.kms.katalon.integration.qtest;

import java.io.File;
import java.io.FilenameFilter;
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
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.qas.api.internal.util.json.JsonArray;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.logging.CustomXmlFormatter;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XMLParserException;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.core.setting.ReportFormatType;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
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
import com.kms.katalon.integration.qtest.setting.QTestSettingCredential;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;
import com.kms.katalon.integration.qtest.util.DateUtil;
import com.kms.katalon.integration.qtest.util.ZipUtil;
import com.kms.katalon.logging.LogUtil;

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
        String url = QTestSettingStore.getServerUrl(QTestSettingStore.isEncryptionEnabled(projectDir), projectDir);

        return new URL(url + "/p/" + Long.toString(qTestProject.getId()) + "/portal/project#tab=testexecution&object="
                + QTestRun.getType() + "&id=" + Long.toString(qTestRun.getId()));
    }

    public static QTestReport getQTestReportByIntegratedEntity(IntegratedEntity reportIntegratedEntity)
            throws QTestInvalidFormatException {
        if (reportIntegratedEntity == null)
            return null;

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
        if (qTestReport == null)
            return null;
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
     * the prepared test log that's formated by user
     * @param tempDir
     * qTest folder inside Katalon temporary folder used for creating ZIP file
     * @param logFolder
     * the folder that contains the ZIP file a.k.a attachment
     * @return
     * @throws QTestException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static QTestLog uploadTestLog(String projectDir, QTestLogUploadedPreview preparedTestCaseResult,
            String tempDir, TestSuiteLogRecord testSuiteLogRecord)
            throws QTestException, IOException, URISyntaxException {

        QTestProject qTestProject = preparedTestCaseResult.getQTestProject();
        QTestTestCase qTestCase = preparedTestCaseResult.getQTestCase();
        QTestRun qTestRun = preparedTestCaseResult.getQTestRun();
        QTestLog qTestLog = preparedTestCaseResult.getQTestLog();
        TestCaseLogRecord testCaseLogRecord = preparedTestCaseResult.getTestCaseLogRecord();
        File logFolder = new File(testSuiteLogRecord.getLogFolder());

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_FORMAT);
        String startDate = sdf.format(testCaseLogRecord.getStartTime());
        String endDate = sdf.format(testCaseLogRecord.getEndTime());
        // Should report for latest version? If not, submit to the current version kept by Katalon
        Long testLongVersionId = null;
        if (QTestSettingStore.isSubmitResultToLatestVersionActive(projectDir) == false) {
            testLongVersionId = qTestCase.getVersionId();
        }

        String message = (qTestLog != null) ? qTestLog.getMessage() : testCaseLogRecord.getMessage();
        if (testSuiteLogRecord.getRunData() != null && testSuiteLogRecord.getRunData().containsKey("browser")) {
            message += "\nBrowser: " + testSuiteLogRecord.getRunData().get("browser");
        }

        IQTestCredential credential = QTestSettingCredential.getCredential(projectDir);
        ;

        TestStatusValue testCaseStatus = testCaseLogRecord.getStatus().getStatusValue();

        QTestExecutionStatus qTestExStatus = getMappingStatus(qTestProject.getId(), credential, testCaseStatus);
        if (qTestExStatus == null) {
            throw new QTestInvalidFormatException(
                    MessageFormat.format(QTestMessageConstants.QTEST_EXC_INVALID_LOG_STATUS,
                            testCaseLogRecord.getStatus().getStatusValue().name()));
        }

        Map<String, Object> statusProperties = new HashMap<String, Object>();
        statusProperties.put(QTestEntity.ID_FIELD, qTestExStatus.getId());

        Map<String, Object> bodyProperties = new HashMap<String, Object>();
        bodyProperties.put("exe_start_date", startDate);
        bodyProperties.put("exe_end_date", endDate);
        bodyProperties.put("status", new JsonObject(statusProperties));
        bodyProperties.put("test_case_version_id", testLongVersionId != null ? testLongVersionId : "");
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

            for (ReportFormatType formatType : QTestSettingStore.getFormatReportTypes(projectDir)) {
                if (formatType == ReportFormatType.LOG) {
                    for (File reportEntry : logFolder.listFiles()) {
                        if (!isValidFileToAttach(reportEntry, projectDir)) {
                            continue;
                        }
                        ReportFormatType format = ReportFormatType
                                .getTypeByExtension(FilenameUtils.getExtension(reportEntry.getAbsolutePath()));
                        if (format == ReportFormatType.LOG) {
                            // Extract log for this test case
                            if (!extractTestCaseLog(testSuiteLogRecord, testCaseLogRecord, logTempFolder)) {
                                moveReportFile(reportEntry, new File(logTempFolder, reportEntry.getName()));
                            }
                        }
                    }
                } else {
                    File newReportFile = new File(logTempFolder, FilenameUtils.getBaseName(logFolder.getAbsolutePath())
                            + "." + formatType.getFileExtension());
                    generateReportFile(formatType, newReportFile, testCaseLogRecord, testSuiteLogRecord, logFolder);
                }
            }

            for (File reportTempFile : logTempFolder.listFiles()) {
                jsonObjects.add(getAttachmentJsonObject(reportTempFile.getAbsolutePath()));
            }

            bodyProperties.put("attachments", new JsonArray(jsonObjects));
            attachmentIncluded = true;

            try {
                FileUtils.cleanDirectory(logTempFolder);
            } catch (IOException e) {
                LogUtil.logError(e);
            }
        }

        // upload result of the given test run
        String result = uploadTestResult(credential, qTestProject.getId(), qTestRun.getId(),
                new JsonObject(bodyProperties).toString());

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

    private static void generateReportFile(ReportFormatType format, File destReportFile,
            TestCaseLogRecord testCaseLogRecord, TestSuiteLogRecord testSuiteLR, File logFolder)
            throws IOException, URISyntaxException {
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
                checkPDFReportInProject(format, logFolder, destReportFile);
            default:
                break;
        }
    }
    
    private static void checkPDFReportInProject(ReportFormatType formatType, File logFolder, File destReportFile) {
        try {
            if(logFolder.exists()) {
                File[] pdfReportFiles = checkPDFFileExistFolder(logFolder);
                if (pdfReportFiles.length > 0) {
                    FileUtils.copyFile(pdfReportFiles[0], destReportFile);
                }
            }
        } catch (IOException e) {
            LogUtil.logError(e);
        }
    }
    
    private static File[] checkPDFFileExistFolder(File folder) {
        return folder.listFiles(new FilenameFilter() { 
            public boolean accept(File dir, String filename)
                 { return filename.endsWith(".pdf"); }
        } );
    }

    private static boolean isValidFileToAttach(File file, String projectDir) {
        String fileExt = FilenameUtils.getExtension(file.getAbsolutePath());
        return QTestSettingStore.getFormatReportTypes(projectDir)
                .contains(ReportFormatType.getTypeByExtension(fileExt));
    }

    private static JsonObject getAttachmentJsonObject(String filePath) throws QTestException, IOException {
        ReportFormatType format = ReportFormatType.getTypeByExtension(FilenameUtils.getExtension(filePath));
        String contentType = "";
        if (format == null) {
            contentType = "application/octet-stream";
        } else {
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

    public static boolean isAvailableForSendingResult( String projectDir) {
        return true;
    }

    /**
     * Uploads the request that contains all the parameters to qTest via qTest API Returns the response body content
     * 
     * @param credential
     * qTest credential
     * @param projectId
     * @param testRunId
     * @param postBody
     * @return the response body content
     * @throws QTestException
     * thrown if system cannot send request
     */
    public static String uploadTestResult(IQTestCredential credential, long projectId, long testRunId, String postBody)
            throws QTestException {
        String url = String.format(credential.getServerUrl() + "/api/v3/projects/%s/test-runs/%s/test-logs", projectId,
                testRunId);
        String resText = QTestAPIRequestHelper.sendPostRequestViaAPI(url, credential.getToken(), postBody);
        return resText;
    }

    /**
     * Uploads the request that contains all the parameters to qTest via qTest API Returns the response body content
     * 
     * @param credential
     * qTest credential
     * @param projectId
     * @param postBody
     * @return
     * @throws QTestException
     */
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

    /**
     * For result submission, the result's format of Katalon has difference status with qTest status's result.
     * <p>
     * This method will converts from Katalon {@link TestStatusValue} to the correct {@link TestStatusValue}
     * <p>
     * 
     * @param projectId
     * @param credential
     * qTest credential
     * @param status
     * @return
     * @throws QTestException
     * @see {@link TestStatusValue}
     */
    public static QTestExecutionStatus getMappingStatus(long projectId, IQTestCredential credential,
            TestStatusValue status) throws QTestException {
        String mappingValue = QTestExecutionStatus.getMappedValue(status.name());
        for (QTestExecutionStatus qTestStatus : getMappingStatuses(projectId, credential)) {
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
     * @param credential
     * qTest credential
     * @return
     * @throws QTestException
     */
    public static List<QTestExecutionStatus> getMappingStatuses(long projectId, IQTestCredential credential)
            throws QTestException {
        if (!QTestIntegrationAuthenticationManager.validateToken(credential.getToken().getAccessTokenHeader())) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }
        List<QTestExecutionStatus> list = new ArrayList<QTestExecutionStatus>();
        String jsonString = QTestAPIRequestHelper.sendGetRequestViaAPI(
                credential.getServerUrl() + "/api/v3/projects/" + projectId + "/test-runs/execution-statuses",
                credential.getToken());
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
    public static List<QTestStepLog> getStepLogs(IQTestCredential credential, long qTestProjectId, long qTestRunId)
            throws QTestException {
        List<QTestStepLog> stepLogs = new ArrayList<QTestStepLog>();
        String url = String.format("%s/api/v3/projects/%s/test-runs/%s/test-logs/last-run?expand=teststeplog.teststep",
                credential.getServerUrl(), qTestProjectId, qTestRunId);
        String resText = QTestAPIRequestHelper.sendGetRequestViaAPI(url, credential.getToken());
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

    private static boolean extractTestCaseLog(TestSuiteLogRecord testSuiteLogRecord,
            TestCaseLogRecord testCaseLogRecord, File toFolder) {
        Logger logger = Logger.getLogger("");
        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler(toFolder.getAbsolutePath() + File.separator + "execution.log");
            fileHandler.setFormatter(new CustomXmlFormatter());
            logger.addHandler(fileHandler);
            List<XmlLogRecord> logRecs = new ArrayList<>();
            String testCaseId = testCaseLogRecord.getId();
            boolean foundTest = false;
            int foundTestNestedLevel = -1;
            for (XmlLogRecord rc : ReportUtil.getAllLogRecords(testSuiteLogRecord.getLogFolder())) {
                String level = rc.getLevel().getName();
                String methodName = rc.getSourceMethodName();
                int nestedLevel = rc.getNestedLevel();
                if (level.equals(LogLevel.START.toString()) && StringConstants.LOG_START_SUITE_METHOD.equals(methodName)
                        || level.equals(LogLevel.END.toString())
                                && StringConstants.LOG_END_SUITE_METHOD.equals(methodName)) {
                    logRecs.add(rc);
                    continue;
                }
                if (level.equals(LogLevel.START.toString()) && StringConstants.LOG_START_TEST_METHOD.equals(methodName)
                        && testCaseId.equals(rc.getProperties().get(StringConstants.XML_LOG_ID_PROPERTY))) {
                    foundTest = true;
                    foundTestNestedLevel = rc.getNestedLevel();
                    logRecs.add(rc);
                    continue;
                }
                // If found the right test case, extracts the logs between start test and end test
                if (foundTest) {
                    // If this test was not ended gracefully, stop when reach begin another test
                    if (level.equals(LogLevel.START.toString())
                            && StringConstants.LOG_START_TEST_METHOD.equals(methodName)
                            && nestedLevel <= foundTestNestedLevel) {
                        foundTest = false;
                        continue;
                    }
                    logRecs.add(rc);
                    // If reach end test, stop collecting
                    if (level.equals(LogLevel.END.toString()) && StringConstants.LOG_END_TEST_METHOD.equals(methodName)
                            && testCaseId.equals(rc.getProperties().get(StringConstants.XML_LOG_ID_PROPERTY))
                            && nestedLevel == foundTestNestedLevel) {
                        foundTest = false;
                        continue;
                    }
                }
            }
            for (XmlLogRecord rc : logRecs) {
                logger.log(rc);
            }
            fileHandler.flush();
            fileHandler.close();
            return true;
        } catch (SecurityException | IOException | XMLParserException | XMLStreamException e) {
            LogUtil.logError(e);
            return false;
        }
    }
}
