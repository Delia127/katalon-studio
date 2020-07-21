package com.kms.katalon.composer.windows.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.project.ProjectEntity;

public class WindowsRecorderProHandlerTest {

    @Test
    public void canExecuteNoProjectOpenedTest() {
        // Given
        DataProviderState.getInstance().setCurrentProject(null);

        // When
        WindowsRecorderProHandler handler = new WindowsRecorderProHandler();

        // Then
        assertFalse("Should disable when no project is opened", handler.canExecute());
    }

    @Test
    public void canExecuteOnlyForWindowsTest() {
        // Given
        ProjectEntity project = new ProjectEntity();
        DataProviderState.getInstance().setCurrentProject(project);

        // When
        WindowsRecorderProHandler handler = new WindowsRecorderProHandler();

        // Then
        assertEquals("Should enable for Windows only", SystemUtils.IS_OS_WINDOWS, handler.canExecute());
    }
}
