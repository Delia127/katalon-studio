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

    public static List<QTestProject> getQTestProjectsByIntegratedEntity(IntegratedEntity integratedEntity) {
        List<QTestProject> qTestProjects = new ArrayList<QTestProject>();

        if (integratedEntity == null) return Collections.emptyList();

        Map<String, String> properties = new TreeMap<String, String>(integratedEntity.getProperties());

        try {
            for (Entry<String, String> entry : properties.entrySet()) {
                String value = entry.getValue();

                JsonObject qTestProjectJsonObject = new JsonObject(value.replace("'", "\"").replace("},\n", "},"));

                QTestProject qTestProject = new QTestProject();
                qTestProject.setId(qTestProjectJsonObject.getLong(QTestEntity.ID_FIELD));
                qTestProject.setName(qTestProjectJsonObject.getString(QTestEntity.NAME_FIELD));

                JsonArray testCaseFolderJsonArray = qTestProjectJsonObject.optJsonArray("testCaseFolderMappeds");
                for (int index = 0; index < testCaseFolderJsonArray.length(); index++) {
                    qTestProject.getTestCaseFolderIds().add(testCaseFolderJsonArray.getString(index));
                }

                JsonArray testSuiteFolderJsonArray = qTestProjectJsonObject.optJsonArray("testSuiteFolderMappeds");
                for (int index = 0; index < testSuiteFolderJsonArray.length(); index++) {
                    qTestProject.getTestSuiteFolderIds().add(testSuiteFolderJsonArray.getString(index));
                }

                qTestProjects.add(qTestProject);

            }
        } catch (JsonException e) {
            return Collections.emptyList();
        }

        return qTestProjects;
    }

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
