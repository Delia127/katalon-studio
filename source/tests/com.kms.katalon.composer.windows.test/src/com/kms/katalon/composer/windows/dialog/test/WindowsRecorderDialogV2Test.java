package com.kms.katalon.composer.windows.dialog.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.windows.dialog.WindowsRecorderDialogV2;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.project.ProjectEntity;

public class WindowsRecorderDialogV2Test {

    @Before
    public void setUp() throws Exception {
        File testProjectFolder = Files.createTempDirectory("kat-test").toFile();
        String location = testProjectFolder.getAbsolutePath();
        ProjectEntity testProject = ProjectController.getInstance().addNewProject("test-project", "", location);
        ProjectController.getInstance().openProjectForUI(testProject.getId(), false, new NullProgressMonitor());

        DataProviderState.getInstance().setCurrentProject(testProject);
    }

    @Test
    public void createTest() {
        UISynchronizeService.syncExec(() -> {
            Display display = Display.getCurrent();
            Shell shell = new Shell(display);
            WindowsRecorderDialogV2 dialog = Mockito.spy(new WindowsRecorderDialogV2(shell));

            dialog.create();

            assertTrue("Start button must be enabled", dialog.getBtnStart().isEnabled());
            assertFalse("Stop button must be disabled", dialog.getBtnStop().isEnabled());

            assertNotNull("Configuration section must not be null", dialog.getMobileComposite());
            assertTrue("Configuration section must show Application File input",
                    dialog.getMobileComposite().isShowApplicationFile());
            assertFalse("Configuration section must hide Remote Settings",
                    dialog.getMobileComposite().isShowConfiguration());
            assertFalse("Configuration section must hide Application Title input",
                    dialog.getMobileComposite().isShowApplicationTitle());

            assertNotNull("Step View composite must not be null", dialog.getStepView());

            assertNotNull("Captured Objects Table Viewer composite must not be null",
                    dialog.getCapturedObjectsTableViewer());

            assertNotNull("Properties composite must not be null", dialog.getPropertiesComposite());

            dialog.close();
        });
    }
}
