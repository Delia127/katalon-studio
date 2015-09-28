package com.kms.katalon.integration.qtest;

import java.util.ArrayList;
import java.util.Collections;
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
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.entity.QTestEntity;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;

/**
 * Provides a set of utility methods that relate with {@link QTestProject}
 */
public class QTestIntegrationProjectManager {

    private QTestIntegrationProjectManager() {
        // Disable default contructor
    }

    /**
     * Returns an array of {@link QTestProject} by parsing the given
     * <code>integratedEntity</code>
     * 
     * @param integratedEntity
     *            the qTest {@link IntegratedEntity} of the current
     *            {@link ProjectEntity}
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
     * @param token
     *            qTest token
     * @param serverUrl
     *            qTest server URL
     * @return a list of {@link QTestProject}
     * @throws QTestUnauthorizedException
     *             thrown if the given <code>token</code> is invalid or
     *             <code>serverUrl</code> is not found.
     * @see {@link #getAll(String, String)}
     */
    public static List<QTestProject> getAllProject(String token, String serverUrl) throws QTestUnauthorizedException {
        List<Project> projects = getAll(token, serverUrl);
        if (projects != null && projects.size() > 0) {
            List<QTestProject> result = new ArrayList<QTestProject>();
            for (Project project : projects) {
                result.add(new QTestProject(project.getId(), project.getName()));
            }
            return result;
        }
        return Collections.emptyList();
    }

    /**
     * Returns a {@link QTestProject} that's name is equal with the given
     * <code>name</code> by filtering from list of {@link QTestProject} from
     * qTest server
     * 
     * @param name
     *            qTest project name
     * @param token
     *            qTest token
     * @param serverUrl
     *            qTest's server URL
     * @return a {@link QTestProject} that's id is equal with the given
     *         <code>name</code>
     * @throws QTestUnauthorizedException
     *             thrown if token is invalid or serverUrl not found.
     * @see {@link QTestProject#getName()}
     */
    public static QTestProject getProjectByName(String name, String token, String serverUrl)
            throws QTestUnauthorizedException {
        List<Project> projects = getAll(token, serverUrl);
        if (projects != null && projects.size() > 0) {
            for (Project project : projects) {
                if (project.getName().equals(name)) {
                    return new QTestProject(project.getId(), project.getName());
                }
            }
        }
        return null;
    }

    /**
     * Returns a {@link QTestProject} that's id is equal with the given
     * <code>id</code> by filtering from list of {@link QTestProject} from qTest
     * server
     * 
     * @param id
     *            qTest project id
     * @param token
     *            qTest token
     * @param serverUrl
     *            qTest's server URL
     * @return a {@link QTestProject} that's id is equal with the given
     *         <code>id</code>
     * @throws QTestUnauthorizedException
     *             if token is invalid or serverUrl not found.
     * @see {@link QTestProject#getId()}
     */
    public static QTestProject getProjectByID(long id, String token, String serverUrl)
            throws QTestUnauthorizedException {
        List<Project> projects = getAll(token, serverUrl);
        if (projects != null && projects.size() > 0) {
            for (Project project : projects) {
                if (project.getId() == id) {
                    return new QTestProject(project.getId(), project.getName());
                }
            }
        }
        return null;
    }

    /**
     * Returns a collection of {@link QTestProject} by getting from qTest server
     * 
     * @param token
     *            qTest token
     * @param serverUrl
     *            qTest server URL
     * @return a collection of {@link QTestProject}
     * @throws QTestUnauthorizedException
     *             if token is invalid or serverUrl not found.
     */
    private static List<Project> getAll(String token, String serverUrl) throws QTestUnauthorizedException {
        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        QTestCredentials credentials = new BasicQTestCredentials(token);
        ProjectServiceClient projectService = new ProjectServiceClient(credentials);
        projectService.setEndpoint(serverUrl);

        List<Project> projects = projectService.listProject(new ListProjectRequest());
        return projects;
    }
}
