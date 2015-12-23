package com.kms.katalon.integration.qtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.qas.api.internal.util.json.JsonArray;
import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;
import org.qas.qtest.api.auth.BasicQTestCredentials;
import org.qas.qtest.api.auth.QTestCredentials;
import org.qas.qtest.api.services.project.ProjectServiceClient;
import org.qas.qtest.api.services.project.model.ListProjectRequest;
import org.qas.qtest.api.services.project.model.Project;

import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestEntity;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;
import com.kms.katalon.integration.qtest.setting.QTestVersion;

/**
 * Provides a set of utility methods that relate with {@link QTestProject}
 */
public class QTestIntegrationProjectManager {

    private QTestIntegrationProjectManager() {
        // Disable default contructor
    }

    /**
     * Returns an array of {@link QTestProject} by parsing the given <code>integratedEntity</code>
     * 
     * @param integratedEntity
     *            the qTest {@link IntegratedEntity} of the current {@link ProjectEntity}
     * @return a list of {@link QTestProject} that's sorted by order.
     */
    public static List<QTestProject> getQTestProjectsByIntegratedEntity(IntegratedEntity integratedEntity) {
        List<QTestProject> qTestProjects = new ArrayList<QTestProject>();

        if (integratedEntity == null) {
            return qTestProjects;
        }

        // TreeMap is used for sorting qTestProjects by their order
        Map<String, String> properties = new TreeMap<String, String>(integratedEntity.getProperties());

        try {
            for (Entry<String, String> entry : properties.entrySet()) {
                String value = entry.getValue();

                JsonObject qTestProjectJsonObject = new JsonObject(value.replace("'", "\"").replace("},\n", "},"));

                QTestProject qTestProject = new QTestProject();
                qTestProject.setId(qTestProjectJsonObject.getLong(QTestEntity.ID_FIELD));
                qTestProject.setName(qTestProjectJsonObject.getString(QTestEntity.NAME_FIELD));

                // Parse TestCaseRepository's collection
                JsonArray testCaseFolderJsonArray = qTestProjectJsonObject.optJsonArray("testCaseFolderMappeds");
                for (int index = 0; index < testCaseFolderJsonArray.length(); index++) {
                    qTestProject.getTestCaseFolderIds().add(testCaseFolderJsonArray.getString(index));
                }

                // Parse TestSuiteRepository's collection
                JsonArray testSuiteFolderJsonArray = qTestProjectJsonObject.optJsonArray("testSuiteFolderMappeds");
                for (int index = 0; index < testSuiteFolderJsonArray.length(); index++) {
                    qTestProject.getTestSuiteFolderIds().add(testSuiteFolderJsonArray.getString(index));
                }

                qTestProjects.add(qTestProject);
            }
        } catch (JsonException e) {
            // return an empty arrays
        }

        return qTestProjects;
    }

    /**
     * Returns qTest {@link IntegratedEntity} of the current
     * {@link ProjectEntity} that is combined from the given
     * <code>qTestProjects</code>
     * 
     * @param qTestProjects a collection of {@link QTestProject} that will be transformed to an {@link IntegratedEntity)
     * @return qTest {@link IntegratedEntity} of the current
     *         {@link ProjectEntity}
     */
    public static IntegratedEntity getIntegratedEntityByQTestProjects(List<QTestProject> qTestProjects) {
        IntegratedEntity integratedEntity = new IntegratedEntity();
        integratedEntity.setProductName(QTestStringConstants.PRODUCT_NAME);
        integratedEntity.setType(IntegratedType.PROJECT);

        for (QTestProject qTestProject : qTestProjects) {
            String key = Integer.toString(qTestProjects.indexOf(qTestProject));
            StringBuilder valueBuilder = new StringBuilder(new JsonObject(qTestProject.getProperties()).toString());
            String value = valueBuilder.toString().replace("\"", "'").replace("},", "},\n");

            integratedEntity.getProperties().put(key, value);
        }

        return integratedEntity;
    }

    /**
     * Returns a list of {@link QTestProject} by getting from qTest server
     * 
     * @param credential
     *            qTest credential
     * @return a list of {@link QTestProject}
     * @throws QTestException
     * @see {@link #getAll(String, String)}
     */
    public static List<QTestProject> getAllProject(IQTestCredential credential) throws QTestException {
        String accessToken = credential.getToken().getAccessToken();
        if (!QTestIntegrationAuthenticationManager.validateToken(accessToken)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        List<QTestProject> qTestProjects = new ArrayList<QTestProject>();
        if (credential.getVersion() == QTestVersion.V6) {
            QTestCredentials credentials = new BasicQTestCredentials(accessToken);
            ProjectServiceClient projectService = new ProjectServiceClient(credentials);
            projectService.setEndpoint(credential.getServerUrl());

            List<Project> projects = projectService.listProject(new ListProjectRequest());
            for (Project project : projects) {
                qTestProjects.add(new QTestProject(project.getId(), project.getName()));
            }
        } else {
            String result = QTestAPIRequestHelper.sendGetRequestViaAPI(credential.getServerUrl() + "/api/v3/projects",
                    credential.getToken());
            if (result != null) {
                try {
                    JsonArray projectArrs = new JsonArray(result);
                    for (int index = 0; index < projectArrs.length(); index++) {
                        JsonObject projectJsObj = projectArrs.getJsonObject(index);
                        qTestProjects.add(new QTestProject(projectJsObj.getLong(QTestEntity.ID_FIELD), projectJsObj
                                .getString(QTestEntity.NAME_FIELD)));
                    }
                } catch (JsonException e) {
                    QTestInvalidFormatException.createInvalidJsonFormatException(e.getMessage());
                }
            }
        }
        return qTestProjects;
    }

    /**
     * Returns a {@link QTestProject} that's name is equal with the given <code>name</code> by filtering from list of
     * {@link QTestProject} from qTest server
     * 
     * @param name
     *            qTest project name
     * @return a {@link QTestProject} that's id is equal with the given <code>name</code>
     * @throws QTestException
     * @see {@link QTestProject#getName()}
     */
    public static QTestProject getProjectByName(String name, IQTestCredential credential) throws QTestException {
        List<QTestProject> projects = getAllProject(credential);
        if (projects != null && projects.size() > 0) {
            for (QTestProject project : projects) {
                if (project.getName().equals(name)) {
                    return project;
                }
            }
        }
        return null;
    }

    /**
     * Returns a {@link QTestProject} that's id is equal with the given <code>id</code> by filtering from list of
     * {@link QTestProject} from qTest server
     * 
     * @param id
     *            qTest project id
     * @param credential
     *            qTest credential
     * @return a {@link QTestProject} that's id is equal with the given <code>id</code>
     * @throws QTestException
     * @see {@link QTestProject#getId()}
     */
    public static QTestProject getProjectByID(long id, IQTestCredential credential) throws QTestException {
        List<QTestProject> projects = getAllProject(credential);
        if (projects != null && projects.size() > 0) {
            for (QTestProject project : projects) {
                if (project.getId() == id) {
                    return project;
                }
            }
        }
        return null;
    }

    /**
     * Returns the prefix of an qTest API for a specified project. Almost APIs start with this.
     * </p>
     * Format: https://<code>project url</code>/api/v3/projects/<code>project id</code>
     * @param credential
     * @param qTestProject
     */
    public static String getProjectAPIPrefix(IQTestCredential credential, QTestProject qTestProject) {
        return credential.getServerUrl() + "/api/v3/projects/" + Long.toString(qTestProject.getId());
    }
}
