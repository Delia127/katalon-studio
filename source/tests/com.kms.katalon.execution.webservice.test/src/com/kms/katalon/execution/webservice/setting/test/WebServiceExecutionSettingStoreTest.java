package com.kms.katalon.execution.webservice.setting.test;

import java.io.File;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.webservice.setting.WebServiceExecutionSettingStore;

public class WebServiceExecutionSettingStoreTest {

    private File testProjectFolder;

    private ProjectEntity testProject;

    @Before
    public void setUp() throws Exception {
        testProjectFolder = Files.createTempDirectory("kat-test").toFile();
        String location = testProjectFolder.getAbsolutePath();
        testProject = ProjectController.getInstance().addNewProject("test-project", "", location);
        ProjectController.getInstance().openProject(testProject.getId(), false);
    }

    @After
    public void tearDown() throws Exception {
        ProjectController.getInstance().closeProject(testProject.getId(), new NullProgressMonitor());
        FileUtils.forceDelete(testProjectFolder);
    }

    @Test
    public void getSetTimeoutTest() throws Exception {
        // Given
        WebServiceExecutionSettingStore settingStore = WebServiceExecutionSettingStore.getStore();
        int connectionTimeout = 1234;
        int socketTimeout = 4321;

        // When
        settingStore.setConnectionTimeout(connectionTimeout);
        settingStore.setSocketTimeout(socketTimeout);

        // Then
        Assert.assertEquals(connectionTimeout, settingStore.getConnectionTimeout());
        Assert.assertEquals(socketTimeout, settingStore.getSocketTimeout());
    }

    @Test
    public void getSetMaxResponseSizeTest() throws Exception {
        // Given
        WebServiceExecutionSettingStore settingStore = WebServiceExecutionSettingStore.getStore();
        long maxResponseSize = 1234;

        // When
        settingStore.setMaxResponseSize(maxResponseSize);

        // Then
        Assert.assertEquals(maxResponseSize, settingStore.getMaxResponseSize());
    }
}
