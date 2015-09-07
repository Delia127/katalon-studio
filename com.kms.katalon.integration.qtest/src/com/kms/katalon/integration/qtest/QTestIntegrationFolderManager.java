package com.kms.katalon.integration.qtest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.qas.api.internal.util.json.JsonArray;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;
import org.qas.qtest.api.auth.BasicQTestCredentials;
import org.qas.qtest.api.auth.QTestCredentials;
import org.qas.qtest.api.services.project.ProjectServiceClient;
import org.qas.qtest.api.services.project.model.CreateModuleRequest;
import org.qas.qtest.api.services.project.model.Module;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.qtest.constants.StringConstants;
import com.kms.katalon.integration.qtest.entity.QTestEntity;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.helper.QTestHttpRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationFolderManager {
	public static QTestModule getQTestCaseFolderByTestCaseEntity(String projectDir, TestCaseEntity testCase) {
		FolderEntity folderEntity = testCase.getParentFolder();

		if (folderEntity == null) return null;

		return getQTestModuleByFolderEntity(projectDir, folderEntity);
	}

	/**
	 * If the given folder is integrated, return its integrated object.
	 * Otherwise, check its parent recursively then create new QTestModule by
	 * calling {@link QTestIntegrationFolderManager}
	 * {@link #createNewQTestTCFolder(String, long, long, String)} method.
	 * 
	 * @param folderEntity
	 * @return
	 */
	public static QTestModule getQTestModuleByFolderEntity(String projectDir, FolderEntity folderEntity) {
		if (folderEntity == null) return null;
		
		IntegratedEntity folderIntegratedEntity = folderEntity.getIntegratedEntity(QTestConstants.PRODUCT_NAME);
		QTestModule currentQTestTCFolder = null;

		if (folderIntegratedEntity != null) {
			currentQTestTCFolder = getQTestModuleByIntegratedEntity(folderIntegratedEntity);
		}

		/*if (folderIntegratedEntity == null || currentQTestTCFolder == null) {
			long parentId = -1L;
			if (folderEntity.getParentFolder() != null) {
				QTestModule parentQTestTCFolder = getQTestModuleByFolderEntity(projectDir, folderEntity.getParentFolder());

				parentId = parentQTestTCFolder.getId();
			}
			QTestProject qTestProject = QTestIntegrationProjectManager.getQTestProjectByIntegratedEntity(folderEntity
					.getProject());
			currentQTestTCFolder = createNewQTestTCFolder(projectDir, qTestProject.getId(), parentId, folderEntity.getName());

			// save folder integrated entity into folderEntity.
			int index = folderEntity.getIntegratedEntities().size();
			if (folderIntegratedEntity != null) {
				index = folderEntity.getIntegratedEntities().indexOf(folderIntegratedEntity);
				folderEntity.getIntegratedEntities().remove(index);
			}

			folderIntegratedEntity = getFolderIntegratedEntityByQTestModule(currentQTestTCFolder);
			folderEntity.getIntegratedEntities().add(index, folderIntegratedEntity);
		}*/
		return currentQTestTCFolder;
	}

	public static void deleteModuleOnQTest(QTestModule qTestModule, QTestProject qTestProject, String projectDir)
			throws Exception {
		if (qTestProject == null) {
			throw new QTestUnauthorizedException(
					"Cannot find qTest project. Please select a qTest project on qTest setting page.");
		}

		// cannot return root module of qTest
		if (qTestModule.getParentId() == 0) return;

		Map<String, Object> bodyProperties = new LinkedHashMap<String, Object>();
		int testCaseType = QTestModule.getType();

		bodyProperties.put(QTestEntity.ID_FIELD, Integer.toString(testCaseType) + "-" + qTestModule.getId());
		bodyProperties.put(QTestEntity.OBJECT_ID_FIELD, qTestModule.getId());
		bodyProperties.put(QTestEntity.PARENT_ID_FIELD, qTestModule.getParentId());
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

	public static QTestModule getQTestModuleByIntegratedEntity(IntegratedEntity integratedEntity) {
		if (integratedEntity.getType() != IntegratedType.FOLDER) return null;

		Map<String, String> properties = integratedEntity.getProperties();

		if (properties == null) return null;

		String id = properties.get(QTestEntity.ID_FIELD);
		String name = properties.get(QTestEntity.NAME_FIELD);
		String parentId = properties.get(QTestEntity.PARENT_ID_FIELD);

		return new QTestModule(Long.parseLong(id), name, Long.parseLong(parentId));
	}

	public static IntegratedEntity getFolderIntegratedEntityByQTestModule(QTestModule qTestModule) {
		IntegratedEntity folderIntegratedEntity = new IntegratedEntity();

		folderIntegratedEntity.setProductName(QTestConstants.PRODUCT_NAME);
		folderIntegratedEntity.setType(IntegratedType.FOLDER);

		folderIntegratedEntity.getProperties().put(QTestEntity.ID_FIELD, Long.toString(qTestModule.getId()));
		folderIntegratedEntity.getProperties().put(QTestEntity.NAME_FIELD, qTestModule.getName());
		folderIntegratedEntity.getProperties().put(QTestEntity.PARENT_ID_FIELD,
				Long.toString(qTestModule.getParentId()));

		return folderIntegratedEntity;
	}

	public static QTestModule createNewQTestTCFolder(String projectDir, long projectId, long parentId,
			String name) {	
		String token = QTestSettingStore.getToken(projectDir);
		String serverUrl = QTestSettingStore.getServerUrl(projectDir);
		
		if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
			throw new QTestUnauthorizedException(StringConstants.QTEST_EXC_INVALID_TOKEN);
		}

		QTestCredentials credentials = new BasicQTestCredentials(token);
		ProjectServiceClient projectServiceClient = new ProjectServiceClient(credentials);
		projectServiceClient.setEndpoint(serverUrl);

		Module module = new Module().withName(name);

		if (parentId > 0L) {
			module.withParentId(parentId);
		}

		CreateModuleRequest createTestCaseFolderRequest = new CreateModuleRequest().withProjectId(projectId)
				.withModule(module);

		Module moduleResult = projectServiceClient.createModule(createTestCaseFolderRequest);

		if (moduleResult != null) {
			QTestModule qTestTestCase = new QTestModule(moduleResult.getId(), name, moduleResult.getParentId());
			qTestTestCase.setGid(moduleResult.getPid());
			return qTestTestCase;
		}
		return null;
	}

	public static QTestModule getModuleRoot(String projectDir, long projectId) throws Exception {
		String url = "/p/" + Long.toString(projectId) + "/portal/project/testdesign/rootmodulelazy/get";

		String serverUrl = QTestSettingStore.getServerUrl(projectDir);
		
		String username = QTestSettingStore.getUsername(projectDir);
		String password = QTestSettingStore.getPassword(projectDir);

		String response = QTestHttpRequestHelper.sendGetRequest(serverUrl, url, username, password);

		JsonObject reponseJsonObject = new JsonObject(response);

		long moduleId = reponseJsonObject.getLong("objId");
		String moduleName = reponseJsonObject.getString(QTestEntity.NAME_FIELD);

		return new QTestModule(moduleId, moduleName, 0);
	}
	
	public static QTestModule updateModuleViaAPI(String projectDir, long projectId, QTestModule qTestParentModule) throws Exception {
		String token = QTestSettingStore.getToken(projectDir);
		String serverUrl = QTestSettingStore.getServerUrl(projectDir);
		
		if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
			throw new QTestUnauthorizedException(StringConstants.QTEST_EXC_INVALID_TOKEN);
		}
		
		String url = serverUrl + "/api/v3/projects/" + Long.toString(projectId) + "/modules?parentId="
				+ Long.toString(qTestParentModule.getId()) + "&expand=descendants";
		
		String result = QTestAPIRequestHelper.sendGetRequestViaAPI(url, token);
		
		if (result != null && !result.isEmpty()) {
			updateChildrenForModule(new JsonArray(result), qTestParentModule);
		}
		
		return qTestParentModule;
	}
	
	private static void updateChildrenForModule(JsonArray jsonArray, QTestModule qTestParentModule) throws JsonException {
		for (int index = 0; index < jsonArray.length(); index++) {
			JsonObject moduleJsonObject = jsonArray.getJsonObject(index);
			QTestModule qTestChildModule = new QTestModule(moduleJsonObject.getLong(QTestEntity.ID_FIELD), 
					moduleJsonObject.getString(QTestEntity.NAME_FIELD), 
					qTestParentModule.getId());
			qTestParentModule.getChildModules().add(qTestChildModule);
			
			String childrenField = "children";
			if (moduleJsonObject.has(childrenField)) {
				updateChildrenForModule(moduleJsonObject.getJsonArray(childrenField), qTestChildModule);
			}
		}
	}

	/**
	 * Updates recursively children of a qTest module. System will fetch
	 * module's info from qTest via {@link QTestHttpRequestHelper} and
	 * automatically create new children.
	 * 
	 * !!!Note: New test cases have no test case version id because qTest
	 * doesn't return that. System will update test case version id when get
	 * test steps.
	 * 
	 * @param projectDir
	 * @param projectId
	 * @param qTestParentModule
	 */
	public static QTestModule updateModule(String projectDir, long projectId, QTestModule qTestParentModule,
			boolean updateChildren) throws Exception {

		String url = "/p/" + Long.toString(projectId) + "/portal/project/testdesign/children/get/"
				+ Long.toString(qTestParentModule.getId()) + "/1";

		String serverUrl = QTestSettingStore.getServerUrl(projectDir);
		
		String username = QTestSettingStore.getUsername(projectDir);
		String password = QTestSettingStore.getPassword(projectDir);

		String response = QTestHttpRequestHelper.sendGetRequest(serverUrl, url, username, password);

		JsonArray childrenJsonArray = new JsonArray(response);

		for (int index = 0; index < childrenJsonArray.length(); index++) {
			JsonObject childJsonObject = childrenJsonArray.getJsonObject(index);

			// get test case's info from childJsonObject
			long objId = childJsonObject.getLong("objId");
			String objName = childJsonObject.getString("name");

			// In this case, test case's pid has only number format so we have
			// to add TC prefix
			String objPid = childJsonObject.getString("idPrefix") + "-" + childJsonObject.getString("pid");
			int type = childJsonObject.getInt("type");

			if (type == QTestTestCase.getType()) {
				QTestTestCase qTestChildTestCase = new QTestTestCase(objId, objName, qTestParentModule.getId(), objPid);

				qTestParentModule.getChildTestCases().add(qTestChildTestCase);
			} else {
				QTestModule qTestChildModule = new QTestModule(objId, objName, qTestParentModule.getId());
				qTestChildModule.setGid(objPid);

				qTestParentModule.getChildModules().add(qTestChildModule);
			}
		}

		// update children of child modules recursively if updateModule flag is true
		if (updateChildren) {
			for (QTestModule qTestChildModule : qTestParentModule.getChildModules()) {
				updateModule(projectDir, projectId, qTestChildModule, updateChildren);
			}
		}

		return qTestParentModule;
	}
}
