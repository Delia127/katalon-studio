package com.kms.katalon.composer.windows.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.project.ProjectEntity;

public class WindowsRecorderProHandlerTest {

    @Test
    public void canExecuteNoProjectOpenedTest() {
        DataProviderState.getInstance().setCurrentProject(null);

        WindowsRecorderProHandler handler = new WindowsRecorderProHandler();

        assertFalse("Should disable when no project is opened", handler.canExecute());
    }

    @Test
    public void canExecuteProjectOpenedInvalidLicenseTest() {
        ProjectEntity project = new ProjectEntity();
        DataProviderState.getInstance().setCurrentProject(project);

        WindowsRecorderProHandler handler = new WindowsRecorderProHandler();

        assertTrue("Should enable when a project is opened with free license", handler.canExecute());
    }
}
