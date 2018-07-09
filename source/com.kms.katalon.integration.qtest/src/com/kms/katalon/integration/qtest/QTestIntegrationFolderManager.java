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
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestEntity;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.helper.QTestHttpRequestHelper;

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
    public static QTestModule getQTestModuleByFolderEntity(FolderEntity folderEntity) {
        if (folderEntity == null)
            return null;

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
    public static void deleteModuleOnQTest(QTestModule qTestModule, QTestProject qTestProject,
            IQTestCredential credential) throws QTestException, IOException {
        if (qTestProject == null) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_PROJECT_NOT_FOUND);
        }

        // cannot return root module of qTest
        if (qTestModule.getParentId() == 0)
            return;

        Map<String, Object> bodyProperties = new LinkedHashMap<String, Object>();
        int testCaseType = QTestModule.getType();

        bodyProperties.put(QTestEntity.ID_FIELD, Integer.toString(testCaseType) + "-" + qTestModule.getId());
        bodyProperties.put(QTestEntity.OBJECT_ID_FIELD, qTestModule.getId());
        bodyProperties.put(QTestEntity.PARENT_ID_FIELD, qTestModule.getParentId());
        bodyProperties.put(QTestEntity.TYPE_FIELD, testCaseType);

        String url = "/p/" + Long.toString(qTestProject.getId()) + "/portal/tree/delete";

        QTestIntegrationAuthenticationManager.authenticate(credential.getUsername(), credential.getPassword());

        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("data", QTestHttpRequestHelper.createDataBody(bodyProperties, true)));
        QTestHttpRequestHelper.sendPostRequest(credential, url, postParams);
    }

    /**
     * Creates new {@link QTestModule} from the given <code>integratedEntity</code> with {@link IntegratedType#FOLDER}
     * type
     * 
     * @param integratedEntity
     * @return
     */
    public static QTestModule getQTestModuleByIntegratedEntity(IntegratedEntity integratedEntity) {
        if (integratedEntity.getType() != IntegratedType.FOLDER)
            return null;

        Map<String, String> properties = integratedEntity.getProperties();

        if (properties == null)
            return null;

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
    
    public static IntegratedEntity getFolderIntegratedEntityByQTestProject(QTestProject qTestProject) {
        IntegratedEntity folderIntegratedEntity = new IntegratedEntity();

        folderIntegratedEntity.setProductName(QTestStringConstants.PRODUCT_NAME);
        folderIntegratedEntity.setType(IntegratedType.FOLDER);

        folderIntegratedEntity.getProperties().put(QTestEntity.ID_FIELD, Long.toString(qTestProject.getId()));
        folderIntegratedEntity.getProperties().put(QTestEntity.NAME_FIELD, qTestProject.getName());

        return folderIntegratedEntity;
    }

    /**
     * Creates new {@link QTestModule} by using qTest SDK.
     * 
     * @param parentId
     * @param name
     * @return
     * @throws QTestUnauthorizedException
     * @throws QTestInvalidFormatException
     */
    public static QTestModule createNewQTestTCFolder(IQTestCredential credential, long projectId, long parentId,
            String name) throws QTestUnauthorizedException, QTestInvalidFormatException {
        String accessToken = credential.getToken().getAccessTokenHeader();
        if (!QTestIntegrationAuthenticationManager.validateToken(accessToken)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        QTestCredentials credentials = new BasicQTestCredentials(accessToken);
        ProjectServiceClient projectServiceClient = new ProjectServiceClient(credentials);
        projectServiceClient.setEndpoint(credential.getServerUrl());

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
     * @param IQTestCredential
     * @param projectId
     * @return
     * @throws QTestException
     * thrown if system cannot send request or the response message is not a JSON string
     * @throws IOException
     */
    public static QTestModule getModuleRoot(IQTestCredential credential, QTestProject qTestProject)
            throws QTestException, IOException {
        return new QTestModule(0, qTestProject.getName(), 0);
    }

    /**
     * Gathers all information of the given <code>qTestParentModule</code> via qTest API
     * 
     * @param projectDir
     * @param projectId
     * @param qTestParentModule
     * @return the updated {@link QTestModule}
     * @throws QTestException
     * thrown if system cannot send request or the response message is not a JSON string
     */
    public static QTestModule updateModuleViaAPI(IQTestCredential credential, long projectId,
            QTestModule qTestParentModule) throws QTestException {
        String serverUrl = credential.getServerUrl();

        if (!QTestIntegrationAuthenticationManager.validateToken(credential.getToken().getAccessTokenHeader())) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        String parentIdPrefix = "";
        if (qTestParentModule.getId() > 0) {
            parentIdPrefix = "parentId=" + Long.toString(qTestParentModule.getId()) + "&";
        }
        String url = serverUrl + "/api/v3/projects/" + Long.toString(projectId) + "/modules?" + parentIdPrefix
                + "expand=descendants";

        String result = QTestAPIRequestHelper.sendGetRequestViaAPI(url, credential.getToken());
        try {
            if (result != null && !result.isEmpty()) {
                updateChildrenForModule(new JsonArray(result), qTestParentModule);
            }

            return qTestParentModule;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(result);
        }
    }
    
    private static List<QTestModule> getModules(IQTestCredential credential, QTestModule qTestParentModule, long projectId)
            throws QTestException {
        String serverUrl = credential.getServerUrl();

        if (!QTestIntegrationAuthenticationManager.validateToken(credential.getToken().getAccessTokenHeader())) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        String parentIdPrefix = "";
        if (qTestParentModule.getId() > 0) {
            parentIdPrefix = "parentId=" + Long.toString(qTestParentModule.getId()) + "&";
        }
        String url = serverUrl + "/api/v3/projects/" + Long.toString(projectId) + "/modules?" + parentIdPrefix
                + "expand=descendants";

        String result = QTestAPIRequestHelper.sendGetRequestViaAPI(url, credential.getToken());
        try {
            List<QTestModule> modules = new ArrayList<>();
            JsonArray childrenJsonArray = new JsonArray(result);

            for (int index = 0; index < childrenJsonArray.length(); index++) {
                JsonObject childJsonObject = childrenJsonArray.getJsonObject(index);

                QTestModule qTestCase = new QTestModule(childJsonObject.getLong(QTestEntity.ID_FIELD),
                        childJsonObject.getString(QTestEntity.NAME_FIELD), qTestParentModule.getId());

                modules.add(qTestCase);
            }
            return modules;
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(result);
        }
    }
    
    private static List<QTestTestCase> getTestCases(IQTestCredential credential, QTestModule qTestParentModule, long projectId)
            throws QTestException {
        String serverUrl = credential.getServerUrl();

        if (!QTestIntegrationAuthenticationManager.validateToken(credential.getToken().getAccessTokenHeader())) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        String parentIdPrefix = "";
        if (qTestParentModule.getId() > 0) {
            parentIdPrefix = "parentId=" + Long.toString(qTestParentModule.getId()) + "&";
        }
        String url = serverUrl + "/api/v3/projects/" + Long.toString(projectId) + "/test-cases?" + parentIdPrefix
                + "expand=descendants";

        String result = QTestAPIRequestHelper.sendGetRequestViaAPI(url, credential.getToken());
        try {
            List<QTestTestCase> testCases = new ArrayList<>();
            JsonArray childrenJsonArray = new JsonArray(result);

            for (int index = 0; index < childrenJsonArray.length(); index++) {
                JsonObject childJsonObject = childrenJsonArray.getJsonObject(index);

                QTestTestCase qTestCase = new QTestTestCase(childJsonObject.getLong(QTestEntity.ID_FIELD),
                        childJsonObject.getString(QTestEntity.NAME_FIELD), qTestParentModule.getId(),
                        childJsonObject.getString(QTestEntity.PID_FIELD));

                testCases.add(qTestCase);
            }
            return testCases;
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
    public static QTestModule updateModule(IQTestCredential credential, long projectId, QTestModule qTestParentModule,
            boolean updateChildren) throws QTestException, IOException {

        qTestParentModule.setChildTestCases(getTestCases(credential, qTestParentModule, projectId));
        qTestParentModule.setChildModules(getModules(credential, qTestParentModule, projectId));
        // update children of child modules recursively if updateModule flag
        // is true
        if (updateChildren) {
            for (QTestModule qTestChildModule : qTestParentModule.getChildModules()) {
                updateModule(credential, projectId, qTestChildModule, updateChildren);
            }
        }
        return qTestParentModule;
    }
}
