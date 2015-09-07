package com.kms.katalon.integration.qtest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.qas.api.internal.util.google.io.BaseEncoding;
import org.qas.api.internal.util.json.JsonArray;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.integration.qtest.constants.StringConstants;
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
import com.kms.katalon.integration.qtest.entity.QTestUser;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestAttachmentSendingType;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;
import com.kms.katalon.integration.qtest.util.DateUtil;
import com.kms.katalon.integration.qtest.util.ZipUtil;

public class QTestIntegrationReportManager {

	public static String numberFieldFormat(long fieldId, long fieldValue) {
		return String.format("{ \"field_id\": %s, \"field_value\": %s }", fieldId, fieldValue);
	}

	public static String stringFieldFormat(long fieldId, String fieldValue) {
		return String.format("{ \"field_id\": %s, \"field_value\": \"%s\" }", fieldId, fieldValue);
	}

	public static URL getTestLogURL(String projectDir, QTestProject qTestProject, QTestRun qTestRun, QTestLog qTestLog)
			throws IOException {
		String url = QTestSettingStore.getServerUrl(projectDir);

		return new URL(url + "/p/" + Long.toString(qTestProject.getId()) + "/portal/project#tab=testexecution&object="
				+ QTestRun.getType() + "&id=" + Long.toString(qTestRun.getId()));
	}

	public static QTestReport getQTestReportByIntegratedEntity(IntegratedEntity reportIntegratedEntity)
			throws Exception {
		if (reportIntegratedEntity == null) return null;

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
	}

	public static IntegratedEntity getIntegratedEntityByQTestReport(QTestReport qTestReport) {
		if (qTestReport == null) return null;
		IntegratedEntity integratedEntity = new IntegratedEntity();
		integratedEntity.setProductName(QTestConstants.PRODUCT_NAME);
		integratedEntity.setType(IntegratedType.REPORT);

		for (Entry<Integer, QTestLog> entry : qTestReport.getTestLogMap().entrySet()) {
			String key = Integer.toString(entry.getKey());
			StringBuilder valueBuilder = new StringBuilder(new JsonObject(entry.getValue().getProperties()).toString());
			String value = valueBuilder.toString().replace("\"", "'").replace("},", "},\n");

			integratedEntity.getProperties().put(key, value);
		}

		return integratedEntity;
	}

	public static QTestLog uploadTestLog(String projectDir, QTestLogUploadedPreview preparedTestCaseResult, String tempDir, File logFolder) throws Exception {
		
		QTestProject qTestProject = preparedTestCaseResult.getQTestProject();
		QTestTestCase qTestCase = preparedTestCaseResult.getQTestCase();
		QTestRun qTestRun = preparedTestCaseResult.getQTestRun();
		QTestLog qTestLog = preparedTestCaseResult.getQTestLog();
		TestCaseLogRecord testCaseLogRecord = preparedTestCaseResult.getTestCaseLogRecord();
		
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
			throw new QTestInvalidFormatException(MessageFormat.format(StringConstants.QTEST_EXC_INVALID_LOG_STATUS,
					testCaseLogRecord.getStatus().getStatusValue().name()));
		}

		Map<String, Object> statusProperties = new HashMap<String, Object>();
		statusProperties.put(QTestEntity.ID_FIELD, qTestExStatus.getId());

		Map<String, Object> bodyProperties = new HashMap<String, Object>();
		bodyProperties.put("exe_start_date", startDate);
		bodyProperties.put("exe_end_date", endDate);
		bodyProperties.put("status", new JsonObject(statusProperties));
		bodyProperties.put("test_case_version_id", testLongVersionId);
		bodyProperties.put("note", QTestIntegrationTestCaseManager.getUploadedDescription(message));

		// zip report folder as a attachment
		File reportZipFile = null;
		boolean attachmentIncluded = false;
		
		if ((qTestLog != null && qTestLog.isAttachmentIncluded())
				|| (qTestLog == null && isEnableSendAttachmentForTestRun(testCaseStatus, projectDir))) {
			reportZipFile = new File(tempDir, FilenameUtils.getBaseName(logFolder.getName()) + ".zip");

			ZipFile zipFile = ZipUtil.getZipFile(reportZipFile, logFolder);
			List<JsonObject> jsonObjects = new ArrayList<JsonObject>();

			JsonObject jsonObject = getAttachmentJsonObject(reportZipFile.getAbsolutePath(), zipFile);
			jsonObjects.add(jsonObject);
			bodyProperties.put("attachments", new JsonArray(jsonObjects));
			attachmentIncluded = true;
		}

		// upload result of the given test run
		String result = uploadTestResult(serverUrl, token, 
				qTestProject.getId(), 
				qTestRun.getId(), new JsonObject(
				bodyProperties).toString());

		// clean ZIP file
		if (reportZipFile != null && reportZipFile.exists()) {
			reportZipFile.delete();
		}

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
	}

	private static JsonObject getAttachmentJsonObject(String filePath, ZipFile zipFile) throws Exception {
		Map<String, Object> attachmentMap = new LinkedHashMap<String, Object>();
		attachmentMap.put("name", FilenameUtils.getName(filePath));
		attachmentMap.put("content_type", "application/octet-stream");
		attachmentMap.put("data", encodeFileContent(filePath));

		return new JsonObject(attachmentMap);
	}

	private static boolean isEnableSendAttachmentForTestRun(TestStatusValue status, String projectDir) {
		QTestAttachmentSendingType sendingType = QTestSettingStore.getAttachmentSendingType(projectDir);
		switch (sendingType) {
		case ALWAYS_SEND:
			return true;
		case NOT_SEND:
			return false;
		case SEND_IF_FAILS:
			return (status == TestStatusValue.FAILED || status == TestStatusValue.ERROR);
		case SEND_IF_PASSES:
			return (status == TestStatusValue.PASSED);
		default:
			break;
		}
		return false;
	}

	public static String uploadTestResult(String serverUrl, String token, long projectId, long testRunId,
			String postBody) throws Exception {
		String url = String.format(serverUrl + "/api/v3/projects/%s/test-runs/%s/test-logs", projectId, testRunId);
		String resText = QTestAPIRequestHelper.sendPostRequestViaAPI(url, token, postBody);
		return resText;
	}

	public static QTestDefect submitDefect(String serverUrl, String token, long projectId, String postBody)
			throws Exception {
		String url = String.format(serverUrl + "/api/v3/projects/%s/defects", projectId);
		String res = QTestAPIRequestHelper.sendPostRequestViaAPI(url, token, postBody);

		JsonObject jo = new JsonObject(res);
		QTestDefect defect = new QTestDefect(jo.getLong("id"), "");
		defect.setGid(jo.getString("pid"));

		return defect;
	}

	public static List<QTestRun> getTestRuns(String serverUrl, String token, long projectId, long testSuiteId)
			throws Exception {
		if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
			throw new RuntimeException(StringConstants.QTEST_EXC_INVALID_TOKEN);
		}
		List<QTestRun> list = new ArrayList<QTestRun>();
		String json = QTestAPIRequestHelper.sendGetRequestViaAPI(serverUrl + "/api/v3/projects/" + projectId
				+ "/test-runs?testSuiteId=" + testSuiteId, token);
		List<String> testRunURLs = parseJsonToGetTestRunURLs(json);
		for (String url : testRunURLs) {
			json = QTestAPIRequestHelper.sendGetRequestViaAPI(url, token);
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
	}

	public static QTestExecutionStatus getMappingStatus(long projectId, String serverUrl, String token,
			TestStatusValue status) throws Exception {
		String mappingValue = QTestExecutionStatus.getMappedValue(status.name());
		for (QTestExecutionStatus qTestStatus : getMappingStatuses(projectId, serverUrl, token)) {
			if (qTestStatus.getName().equalsIgnoreCase(mappingValue)) {
				return qTestStatus;
			}
		}
		return null;
	}

	public static List<QTestExecutionStatus> getMappingStatuses(long projectId, String serverUrl, String token)
			throws Exception {
		if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
			throw new RuntimeException(StringConstants.QTEST_EXC_INVALID_TOKEN);
		}
		List<QTestExecutionStatus> list = new ArrayList<QTestExecutionStatus>();
		String jsonString = QTestAPIRequestHelper.sendGetRequestViaAPI(serverUrl + "/api/v3/projects/" + projectId
				+ "/test-runs/execution-statuses", token);
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
	}

	public static QTestUser getQTestUser(String serverUrl, String token, long projectId) throws Exception {
		if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
			throw new RuntimeException(StringConstants.QTEST_EXC_INVALID_TOKEN);
		}
		String url = serverUrl + "/api/v3/projects/" + projectId + "/user-profiles/current";
		String jsonString = QTestAPIRequestHelper.sendGetRequestViaAPI(url, token);
		JsonObject jo = new JsonObject(jsonString);
		QTestUser user = new QTestUser(jo.getLong("user_id"), "");
		return user;
	}

	public static String encodeFileContent(String filePath) throws Exception {
		InputStream is = null;
		try {
			is = new FileInputStream(new File(filePath));
			return BaseEncoding.base64().encode(getBinaryFromInputStream(is));
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private static byte[] getBinaryFromInputStream(InputStream content) throws IOException {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
			byte[] buffer = new byte[8096];
			int length;
			while ((length = content.read(buffer)) > -1) {
				output.write(buffer, 0, length);
			}
			output.close();
			return output.toByteArray();
		} catch (Exception ex) {
			throw new IOException(MessageFormat.format(StringConstants.QTEST_EXC_CANNOT_READ_INPUT_STREAM,
					ex.getMessage()), ex);
		}
	}

	private static List<String> parseJsonToGetTestRunURLs(String jsonString) throws Exception {
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
	}

	public static List<QTestDefectField> getDefectFields(String serverUrl, String token, long projectId)
			throws Exception {
		List<QTestDefectField> list = new ArrayList<QTestDefectField>();
		String url = serverUrl + "/api/v3/projects/" + projectId + "/defects/fields";
		String json = QTestAPIRequestHelper.sendGetRequestViaAPI(url, token);
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
			long qTestRunId) throws Exception {
		List<QTestStepLog> stepLogs = new ArrayList<QTestStepLog>();
		String url = String.format("%s/api/v3/projects/%s/test-runs/%s/test-logs/last-run?expand=teststeplog.teststep",
				serverUrl, qTestProjectId, qTestRunId);
		String resText = QTestAPIRequestHelper.sendGetRequestViaAPI(url, authenToken);
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
	}
}
