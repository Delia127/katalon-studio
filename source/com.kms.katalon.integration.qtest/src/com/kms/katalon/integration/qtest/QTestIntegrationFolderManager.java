package com.kms.katalon.integration.qtest;

import java.io.IOException;
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
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.entity.QTestEntity;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.helper.QTestHttpRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

/**
 * Provides a set of utility methods that relate with {@link QTestModule}
 */
public class QTestIntegrationFolderManager {

    private QTestIntegrationFolderManager() {
        // Disable default contructor
    }

    /**
     * If the given folder is integrated, return its integrated object. Otherwise, check its parent recursively then
     * create new QTestModule by calling {@link QTestIntegrationFolderManager}
     * {@link #createNewQTestTCFolder(String, long, long, String)} method.
     * 
     * @param folderEntity
     * @return
     */
    public static QTestModule getQTestModuleByFolderEntity(String projectDir, FolderEntity folderEntity) {
        if (folderEntity == null) return null;

        IntegratedEntity folderIntegratedEntity = folderEntity.getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
        QTestModule currentQTestTCFolder = null;

        if (folderIntegratedEntity != null) {
            currentQTestTCFolder = getQTestModuleByIntegratedEntity(folderIntegratedEntity);
        }
        return currentQTestTCFolder;
    }

    /**
     * Deletes the given <code>qTestModule</code> on qTest server
     * 
     * @param qTestModule
     * @param qTestProject
     * @param projectDir
     * @throws QTestException
     * @throws IOException
     */
    public static void deleteModuleOnQTest(QTestModule qTestModule, QTestProject qTestProject, String projectDir)
            throws QTestException, IOException {
        if (qTestProject == null) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_PROJECT_NOT_FOUND);
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

    /**
     * Creates new {@link QTestModule} from the given <code>integratedEntity</code> with {@link IntegratedType#FOLDER}
     * type
     * 
     * @param integratedEntity
     * @return
     */
    public static QTestModule getQTestModuleByIntegratedEntity(IntegratedEntity integratedEntity) {
        if (integratedEntity.getType() != IntegratedType.FOLDER) return null;

        Map<String, String> properties = integratedEntity.getProperties();

        if (properties == null) return null;

        String id = properties.get(QTestEntity.ID_FIELD);
        String name = properties.get(QTestEntity.NAME_FIELD);
        String parentId = properties.get(QTestEntity.PARENT_ID_FIELD);

        return new QTestModule(Long.parseLong(id), name, Long.parseLong(parentId));
    }

    /**
     * Returns qTest {@link IntegratedEntity} of a {@link FolderEntity} from the given <code>qTestModule</code>
     * 
     * @param qTestModule
     * @return
     */
    public static IntegratedEntity getFolderIntegratedEntityByQTestModule(QTestModule qTestModule) {
        IntegratedEntity folderIntegratedEntity = new IntegratedEntity();

        folderIntegratedEntity.setProductName(QTestStringConstants.PRODUCT_NAME);
        folderIntegratedEntity.setType(IntegratedType.FOLDER);

        folderIntegratedEntity.getProperties().put(QTestEntity.ID_FIELD, Long.toString(qTestModule.getId()));
        folderIntegratedEntity.getProperties().put(QTestEntity.NAME_FIELD, qTestModule.getName());
        folderIntegratedEntity.getProperties().put(QTestEntity.PARENT_ID_FIELD,
                Long.toString(qTestModule.getParentId()));

        return folderIntegratedEntity;
    }

    /**
     * Creates new {@link QTestModule} by using qTest SDK.
     * 
     * @param parentId
     * @param name
     * @return
     * @throws QTestUnauthorizedException
     */
    public static QTestModule createNewQTestTCFolder(String projectDir, long projectId, long parentId, String name)
            throws QTestUnauthorizedException {
        String token = QTestSettingStore.getToken(projectDir);
        String serverUrl = QTestSettingStore.getServerUrl(projectDir);

        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        QTestCredentials credentials = new BasicQTestCredentials(token);
        ProjectServiceClient projectServiceClient = new ProjectServiceClient(credentials);
        projectServiceClient.setEndpoint(serverUrl);

        Module module = new Module().withName(name);

        CreateModuleRequest createTestCaseFolderRequest = new CreateModuleRequest().withProjectId(projectId)
                .withModule(module);

        if (parentId > 0L) {
            createTestCaseFolderRequest.withParentId(parentId);
        }

        Module moduleResult = projectServiceClient.createModule(createTestCaseFolderRequest);

        if (moduleResult != null) {
            QTestModule qTestTestCase = new QTestModule(moduleResult.getId(), name, moduleResult.getParentId());
            qTestTestCase.setGid(moduleResult.getPid());
            return qTestTestCase;
        }
        return null;
    }

    /**
     * Return a {@link QTestModule} that represents the root of module of the {@link QTestProject} that has id equal
     * with the given <code>projectId</code>
     * 
     * @param projectDir
     * @param projectId
     * @return
     * @throws QTestException
     *             thrown if system cannot send request or the response message is not a JSON string
     * @throws IOException
     */
    public static QTestModule getModuleRoot(String projectDir, long projectId) throws QTestException, IOException {
        String url = "/p/" + Long.toString(projectId) + "/portal/project/testdesign/rootmodulelazy/get";

        String serverUrl = QTestSettingStore.getServerUrl(projectDir);

        String username = QTestSettingStore.getUsername(projectDir);
        String password = QTestSettingStore.getPassword(projectDir);

        String response = QTestHttpRequestHelper.sendGetRequest(serverUrl, url, username, password);

        try {
            JsonObject reponseJsonObject = new JsonObject(response);

            long moduleId = reponseJsonObject.getLong("objId");
            String moduleName = reponseJsonObject.getString(QTestEntity.NAME_FIELD);

            return new QTestModule(moduleId, moduleName, 0);
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(response);
        }
    }

    /**
     * Gathers all information of the given <code>qTestParentModule</code> via qTest API
     * 
     * @param projectDir
     * @param projectId
     * @param qTestParentModule
     * @return the updated {@link QTestModule}
     * @throws QTestException
     *             thrown if system cannot send request or the response message is not a JSON string
     */
    public static QTestModule updateModuleViaAPI(String projectDir, long projectId, QTestModule qTestParentModule)
            throws QTestException {
        String token = QTestSettingStore.getToken(projectDir);
        String serverUrl = QTestSettingStore.getServerUrl(projectDir);

        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        String url = serverUrl + "/api/v3/projects/" + Long.toString(projectId) + "/modules?parentId="
                + Long.toString(qTestParentModule.getId()) + "&expand=descendants";

        String result = QTestAPIRequestHelper.sendGetRequestViaAPI(url, token);
        try {
            if (result != null && !result.isEmpty()) {
                updateChildrenForModule(new JsonArray(result), qTestParentModule);
            }

            return qTestParentModule;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(result);
        }
    }

    /**
     * Supporting method of {@link #updateModuleViaAPI(String, long, QTestModule)}
     */
    private static void updateChildrenForModule(JsonArray jsonArray, QTestModule qTestParentModule)
            throws JsonException {
        for (int index = 0; index < jsonArray.length(); index++) {
            JsonObject moduleJsonObject = jsonArray.getJsonObject(index);
            QTestModule qTestChildModule = new QTestModule(moduleJsonObject.getLong(QTestEntity.ID_FIELD),
                    moduleJsonObject.getString(QTestEntity.NAME_FIELD), qTestParentModule.getId());
            qTestParentModule.getChildModules().add(qTestChildModule);

            String childrenField = "children";
            if (moduleJsonObject.has(childrenField)) {
                updateChildrenForModule(moduleJsonObject.getJsonArray(childrenField), qTestChildModule);
            }
        }
    }

    /**
     * Updates recursively children of a qTest module. System will fetch module's info from qTest via
     * {@link QTestHttpRequestHelper} and automatically create new children.
     * <p>
     * !!!Note: New test cases each one has no test case version id because qTest doesn't return that. System will
     * update test case version id when get test steps.
     * 
     * @param projectDir
     * @param projectId
     * @param qTestParentModule
     */
    public static QTestModule updateModule(String projectDir, long projectId, QTestModule qTestParentModule,
            boolean updateChildren) throws QTestException, IOException {

        String url = "/p/" + Long.toString(projectId) + "/portal/project/testdesign/children/get/"
                + Long.toString(qTestParentModule.getId()) + "/1";

        String serverUrl = QTestSettingStore.getServerUrl(projectDir);

        String username = QTestSettingStore.getUsername(projectDir);
        String password = QTestSettingStore.getPassword(projectDir);

        String response = QTestHttpRequestHelper.sendGetRequest(serverUrl, url, username, password);

        try {
            JsonArray childrenJsonArray = new JsonArray(response);

            for (int index = 0; index < childrenJsonArray.length(); index++) {
                JsonObject childJsonObject = childrenJsonArray.getJsonObject(index);

                // get test case's info from childJsonObject
                long objId = childJsonObject.getLong("objId");
                String objName = childJsonObject.getString("name");

                // In this case, test case's pid has only number format so we
                // have
                // to add TC prefix
                String objPid = childJsonObject.getString("idPrefix") + "-" + childJsonObject.getString("pid");
                int type = childJsonObject.getInt("type");

                if (type == QTestTestCase.getType()) {
                    QTestTestCase qTestChildTestCase = new QTestTestCase(objId, objName, qTestParentModule.getId(),
                            objPid);

                    qTestParentModule.getChildTestCases().add(qTestChildTestCase);
                } else {
                    QTestModule qTestChildModule = new QTestModule(objId, objName, qTestParentModule.getId());
                    qTestChildModule.setGid(objPid);

                    qTestParentModule.getChildModules().add(qTestChildModule);
                }
            }

            // update children of child modules recursively if updateModule flag
            // is true
            if (updateChildren) {
                for (QTestModule qTestChildModule : qTestParentModule.getChildModules()) {
                    updateModule(projectDir, projectId, qTestChildModule, updateChildren);
                }
            }

            return qTestParentModule;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(response);
        }
    }
}
