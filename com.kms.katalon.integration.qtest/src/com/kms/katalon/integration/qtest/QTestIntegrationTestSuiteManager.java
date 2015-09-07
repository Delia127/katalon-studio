package com.kms.katalon.integration.qtest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.qas.api.internal.util.json.JsonArray;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;

import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.integration.qtest.entity.QTestCycle;
import com.kms.katalon.integration.qtest.entity.QTestEntity;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestRelease;
import com.kms.katalon.integration.qtest.entity.QTestReleaseRoot;
import com.kms.katalon.integration.qtest.entity.QTestRun;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestSuiteParent;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.helper.QTestHttpRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationTestSuiteManager {
	private static final String PARENT_PROPERTY_PREFIX = "parent.";

	public static QTestSuite getSelectedQTestSuiteByIntegratedEntity(List<QTestSuite> qTestSuites) {
		for (QTestSuite qTestSuite : qTestSuites) {
			if (qTestSuite.isSelected()) {
				return qTestSuite;
			}
		}
		return null;
	}

	public static QTestRun getTestRunByTestSuiteAndTestCaseId(QTestSuite qTestSuite, long testCaseId) {
		QTestRun testRun = null;
		for (QTestRun childTestRun : qTestSuite.getTestRuns()) {
			if (childTestRun.getQTestCaseId() == testCaseId) {
				testRun = childTestRun;
				break;
			}
		}
		return testRun;
	}

	public static void addQTestSuiteToIntegratedEntity(QTestSuite qTestSuite,
			IntegratedEntity testSuiteIntegratedEntity, int order) {
		testSuiteIntegratedEntity.getProperties().put(Integer.toString(order),
				getQTestSuitePropertiesString(qTestSuite));
	}

	public static String getQTestSuitePropertiesString(QTestSuite qTestSuite) {
		StringBuilder testRunMapStringBuilder = new StringBuilder(new JsonObject(qTestSuite.getProperties()).toString());

		return testRunMapStringBuilder.toString().replace("\"", "'").replace("},", "},\n");
	}

	public static IntegratedEntity getIntegratedEntityByTestSuiteList(List<QTestSuite> qTestSuites) {
		IntegratedEntity testSuiteIntegratedEntity = new IntegratedEntity();
		testSuiteIntegratedEntity.setProductName(QTestConstants.PRODUCT_NAME);
		testSuiteIntegratedEntity.setType(IntegratedType.TESTSUITE);

		for (QTestSuite qTestSuite : qTestSuites) {
			addQTestSuiteToIntegratedEntity(qTestSuite, testSuiteIntegratedEntity, qTestSuites.indexOf(qTestSuite));
		}

		return testSuiteIntegratedEntity;
	}

	public static List<QTestSuite> getQTestSuiteListByIntegratedEntity(IntegratedEntity integratedEntity) {
		List<QTestSuite> qTestSuiteCollection = new ArrayList<QTestSuite>();

		if (integratedEntity == null || integratedEntity.getType() != IntegratedType.TESTSUITE)
			return qTestSuiteCollection;

		try {
			Map<String, String> properties = new TreeMap<String, String>(integratedEntity.getProperties());

			for (Entry<String, String> entry : properties.entrySet()) {
				String value = entry.getValue();

				JsonObject qTestSuiteJsonObject = new JsonObject(value.replace("'", "\"").replace("},\n", "},"));

				QTestSuite qTestSuite = new QTestSuite();
				qTestSuite.setName(qTestSuiteJsonObject.getString(QTestEntity.NAME_FIELD));
				qTestSuite.setId(qTestSuiteJsonObject.getLong(QTestEntity.ID_FIELD));
				qTestSuite.setPid(qTestSuiteJsonObject.getString(QTestSuiteParent.PID_FIELD));
				qTestSuite.setSelected(qTestSuiteJsonObject.getBoolean("default"));

				JsonObject parentJsonObject = qTestSuiteJsonObject.getJsonObject("parent");
				qTestSuite.setParent(getTestSuiteParentByJsonObject(parentJsonObject));

				JsonArray testRunJsonArray = qTestSuiteJsonObject.getJsonArray("testRuns");
				qTestSuite.setTestRuns(getQTestRunCollection(testRunJsonArray));

				qTestSuiteCollection.add(qTestSuite);
			}
		} catch (JsonException e) {
			throw new QTestInvalidFormatException(e.getMessage());
		}

		return qTestSuiteCollection;
	}

	public static QTestSuiteParent getTestSuiteParentByJsonObject(JsonObject parentJsonObject) throws JsonException {
		long id = parentJsonObject.getLong(QTestEntity.ID_FIELD);
		String name = parentJsonObject.getString(QTestEntity.NAME_FIELD);
		int type = parentJsonObject.getInt(QTestEntity.TYPE_FIELD);

		long parentId = parentJsonObject.getLong("parentId");
		if (parentId > 0 && type == QTestSuiteParent.CYCLE_TYPE) {
			QTestRelease release = new QTestRelease();
			release.setId(parentId);

			String parentName = parentJsonObject.optString("parentName", "");
			release.setName((parentName != null) ? parentName : "");

			QTestCycle cycle = new QTestCycle(release);
			cycle.setId(id);
			cycle.setName(name);
			return cycle;
		} else {
			return QTestSuiteParent.getTestSuiteParent(id, type, name);
		}
	}

	public static List<QTestRun> getQTestRunCollection(JsonArray testRunJsonArray) throws JsonException {
		List<QTestRun> qTestRunCollection = new ArrayList<QTestRun>();
		for (int index = 0; index < testRunJsonArray.length(); index++) {
			JsonObject qTestRunJsonObject = testRunJsonArray.getJsonObject(index);
			QTestRun qTestRun = new QTestRun();
			qTestRun.setId(qTestRunJsonObject.getLong(QTestEntity.ID_FIELD));
			qTestRun.setName(qTestRunJsonObject.getString(QTestEntity.NAME_FIELD));
			qTestRun.setQTestCaseId(qTestRunJsonObject.getLong("qTestCaseId"));

			qTestRunCollection.add(qTestRun);
		}

		return qTestRunCollection;
	}

	public static IntegratedEntity getIntegratedEntityByTestSuite(QTestSuite qTestTS) {
		IntegratedEntity testSuiteIntegratedEntity = new IntegratedEntity();

		testSuiteIntegratedEntity.setProductName(QTestConstants.PRODUCT_NAME);
		testSuiteIntegratedEntity.setType(IntegratedType.TESTSUITE);

		testSuiteIntegratedEntity.getProperties().put(QTestEntity.ID_FIELD, Long.toString(qTestTS.getId()));
		testSuiteIntegratedEntity.getProperties().put(QTestEntity.NAME_FIELD, qTestTS.getName());
		testSuiteIntegratedEntity.getProperties().put(QTestEntity.PID_FIELD, qTestTS.getPid());

		QTestSuiteParent parent = qTestTS.getParent();
		testSuiteIntegratedEntity.getProperties().put(PARENT_PROPERTY_PREFIX + QTestEntity.ID_FIELD,
				Long.toString(parent.getId()));
		testSuiteIntegratedEntity.getProperties()
				.put(PARENT_PROPERTY_PREFIX + QTestEntity.NAME_FIELD, parent.getName());
		testSuiteIntegratedEntity.getProperties().put(PARENT_PROPERTY_PREFIX + QTestEntity.TYPE_FIELD,
				Integer.toString(parent.getType()));

		// testRunMap: Used to store list of test run of test suite to the
		// integrated entity.
		// <key, value>: <test case id, map of properties> of test run.
		Map<Long, Object> testRunMap = new HashMap<Long, Object>();
		for (QTestRun testRun : qTestTS.getTestRuns()) {
			testRunMap.put(testRun.getQTestCaseId(), testRun.getMapProperties());
		}

		StringBuilder testRunMapStringBuilder = new StringBuilder(new JsonObject(testRunMap).toString().replace("},",
				"},\n"));
		testSuiteIntegratedEntity.getProperties().put("testRun", testRunMapStringBuilder.toString().replace("\"", ""));

		return testSuiteIntegratedEntity;
	}

	@SuppressWarnings("unused")
	private static String getTestSuiteData(QTestSuite testSuite, QTestProject project, String projectDir)
			throws Exception {
		String url = QTestSettingStore.getServerUrl(projectDir) + "/api/v3/projects/" + Long.toString(project.getId())
				+ "/test-suites/" + Long.toString(testSuite.getId());
		return QTestAPIRequestHelper.sendGetRequestViaAPI(url, QTestSettingStore.getToken(projectDir));
	}
	
	@SuppressWarnings("unused")
	private static String getTestSuiteFields(QTestProject project, String projectDir)
			throws Exception {
		String url = QTestSettingStore.getServerUrl(projectDir) + "/api/v3/projects/" + Long.toString(project.getId())
				+ "/settings/test-suites/fields";
		return QTestAPIRequestHelper.sendGetRequestViaAPI(url, QTestSettingStore.getToken(projectDir));
	}
	
	@SuppressWarnings("unused")
	private static long getTestSuiteFieldId(String fieldName, JsonArray responseJsonArray)
			throws Exception {		
		for (int index = 0; index < responseJsonArray.length(); index++) {
			JsonObject fieldJsonObject = responseJsonArray.getJsonObject(index);
			String responseFieldName = fieldJsonObject.getString("label");
			if (fieldName.equalsIgnoreCase(responseFieldName)) {				
				return fieldJsonObject.getLong(QTestEntity.ID_FIELD);
			}
		}
		
		return 0;
	}
	
	@SuppressWarnings("unused")
	private static String getTestSuiteProperty(long fieldId, JsonObject testSuiteJson) throws JsonException {
		JsonArray propertiesArray = testSuiteJson.getJsonArray("properties");
		for (int index = 0; index < propertiesArray.length(); index++) {
			JsonObject propertyJsonObject = propertiesArray.getJsonObject(index);
			if (propertyJsonObject.getLong("field_id") == fieldId) {
				return propertyJsonObject.getString("field_value");
			}
		}
		return "";
	}

	public static QTestRun uploadTestCaseInTestSuite(QTestTestCase testCase, QTestSuite testSuite,
			QTestProject project, String projectDir) throws Exception {
		String token = QTestSettingStore.getToken(projectDir);
		String url = QTestSettingStore.getServerUrl(projectDir) + "/api/v3/projects/" + Long.toString(project.getId())
				+ "/test-runs?parentId=" + Long.toString(testSuite.getId()) + "&parentType=test-suite";
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		JsonObject testCaseInfoJsonArray = new JsonObject();
		testCaseInfoJsonArray.put(QTestEntity.ID_FIELD, testCase.getId());
		
		bodyMap.put(QTestEntity.NAME_FIELD, testCase.getName());
		bodyMap.put("test_case", testCaseInfoJsonArray);
		String result = QTestAPIRequestHelper.sendPostRequestViaAPI(url, token, new JsonObject(bodyMap).toString());		

		if (result != null && !result.isEmpty()) {
			JsonObject resultObject = new JsonObject(result);
			QTestRun testRun = new QTestRun();
			testRun.setId(resultObject.getLong(QTestEntity.ID_FIELD));
			testRun.setName(testCase.getName());
			testRun.setQTestCaseId(testCase.getId());
			return testRun;
		} else {
			return null;
		}
	}

	public static QTestSuite addTestSuite(String projectDir, String name, QTestSuiteParent parent,
			QTestProject qTestProject) throws Exception {

		Map<String, Object> bodyProperties = new LinkedHashMap<String, Object>();
		int testCaseType = QTestSuite.getType();

		bodyProperties.put(QTestEntity.NAME_FIELD, name);
		bodyProperties.put(QTestEntity.PARENT_ID_FIELD, parent.getId());
		bodyProperties.put(QTestEntity.TYPE_FIELD, testCaseType);

		String serverUrl = QTestSettingStore.getServerUrl(projectDir);

		String url = "/p/" + Long.toString(qTestProject.getId()) + "/portal/tree/create/"
				+ Integer.toString(parent.getType());

		String username = QTestSettingStore.getUsername(projectDir);
		String password = QTestSettingStore.getPassword(projectDir);

		QTestIntegrationAuthenticationManager.authenticate(username, password);

		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("data", QTestHttpRequestHelper.createDataBody(bodyProperties, true)));
		String result = QTestHttpRequestHelper.sendPostRequest(serverUrl, url, username, password, postParams);
		if (result != null && !result.isEmpty()) {
			QTestSuite testSuite = new QTestSuite();
			testSuite.setName(name);
			testSuite.setParent(parent);

			JsonObject data = new JsonObject(result).getJsonArray("data").getJsonObject(0);

			testSuite.setId(data.getLong(QTestEntity.OBJECT_ID_FIELD));
			testSuite.setPid(data.getString("idPrefix") + "-" + data.getString(QTestEntity.PID_FIELD));
			return testSuite;
		} else {
			return null;
		}
	}

	public static void deleteTestSuiteOnQTest(String projectDir, QTestSuite qTestTS, QTestProject qTestProject)
			throws Exception {
		Map<String, Object> bodyProperties = new LinkedHashMap<String, Object>();
		int testCaseType = QTestSuite.getType();

		bodyProperties.put(QTestEntity.ID_FIELD, Integer.toString(testCaseType) + "-" + qTestTS.getId());
		bodyProperties.put(QTestEntity.OBJECT_ID_FIELD, qTestTS.getId());
		bodyProperties.put(QTestEntity.PARENT_ID_FIELD, qTestTS.getParent().getId());
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

	public static URL navigatedUrlToQTestTestSuite(String projectDir, QTestSuite testSuite, QTestProject qTestProject)
			throws MalformedURLException {

		String url = QTestSettingStore.getServerUrl(projectDir);

		return new URL(url + "/p/" + Long.toString(qTestProject.getId()) + "/portal/project#tab=testexecution&object="
				+ QTestSuite.getType() + "&id="+ Long.toString(testSuite.getId()));
	}

	public static QTestReleaseRoot getTestSuiteIdRootOnQTest(String projectDir, QTestProject qTestProject)
			throws Exception {

		if (qTestProject == null) {
			throw new QTestUnauthorizedException(
					"Cannot find qTest project. Please select a qTest project on qTest setting page.");
		}

		String serverUrl = QTestSettingStore.getServerUrl(projectDir);
		String url = "/p/" + Long.toString(qTestProject.getId()) + "/portal/project/testdesign/rootmodulelazy/get";

		String username = QTestSettingStore.getUsername(projectDir);
		String password = QTestSettingStore.getPassword(projectDir);

		QTestIntegrationAuthenticationManager.authenticate(username, password);

		String result = QTestHttpRequestHelper.sendGetRequest(serverUrl, url, username, password);

		if (result != null && !result.isEmpty()) {

			JsonObject data = new JsonObject(result);
			long objId = data.getLong(QTestEntity.OBJECT_ID_FIELD);
			String name = data.getString(QTestEntity.NAME_FIELD);
			QTestReleaseRoot testSuiteParentRoot = new QTestReleaseRoot();
			testSuiteParentRoot.setId(objId);
			testSuiteParentRoot.setName(name);
			testSuiteParentRoot.setChildren(getReleases(qTestProject, projectDir));
			return testSuiteParentRoot;
		} else {
			return null;
		}
	}

	public static List<QTestSuiteParent> getReleases(QTestProject qTestProject, String projectDir) throws Exception {
		List<QTestSuiteParent> qTestReleases = new ArrayList<QTestSuiteParent>();

		String token = QTestSettingStore.getToken(projectDir);
		String serverUrl = QTestSettingStore.getServerUrl(projectDir);
		String url = serverUrl + "/api/v3/projects/" + Long.toString(qTestProject.getId()) + "/releases";
		String result = QTestAPIRequestHelper.sendGetRequestViaAPI(url, token);
		JsonArray resultJsonArray = new JsonArray(result);
		for (int index = 0; index < resultJsonArray.length(); index++) {
			JsonObject testReleaseJsonObject = resultJsonArray.getJsonObject(index);
			QTestRelease release = new QTestRelease();
			release.setId(testReleaseJsonObject.getLong(QTestEntity.ID_FIELD));
			release.setName(testReleaseJsonObject.getString(QTestEntity.NAME_FIELD));

			release.setCycles((getCycles(qTestProject, release, projectDir)));
			qTestReleases.add(release);
		}

		return qTestReleases;
	}

	public static List<QTestSuiteParent> getCycles(QTestProject qTestProject, QTestRelease release, String projectDir)
			throws Exception {
		List<QTestSuiteParent> qTestCycles = new ArrayList<QTestSuiteParent>();

		String token = QTestSettingStore.getToken(projectDir);
		String serverUrl = QTestSettingStore.getServerUrl(projectDir);
		String url = serverUrl + "/api/v3/projects/" + Long.toString(qTestProject.getId()) + "/test-cycles?parentId="
				+ Long.toString(release.getId()) + "&parentType=release";
		String result = QTestAPIRequestHelper.sendGetRequestViaAPI(url, token);
		JsonArray resultJsonArray = new JsonArray(result);

		for (int index = 0; index < resultJsonArray.length(); index++) {
			JsonObject testCycleJsonObject = resultJsonArray.getJsonObject(index);
			QTestCycle cycle = new QTestCycle(release);
			cycle.setId(testCycleJsonObject.getLong(QTestEntity.ID_FIELD));
			cycle.setName(testCycleJsonObject.getString(QTestEntity.NAME_FIELD));
			qTestCycles.add(cycle);
		}

		return qTestCycles;
	}
}
