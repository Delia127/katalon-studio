package com.kms.katalon.composer.webui.setting.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.kms.katalon.composer.webui.setting.EdgeChromiumPreferencePage;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class EdgeChromiumPreferencePageTest {
	private File testProjectFolder;

	@Mock
    private ProjectEntity testProject;
    
	@InjectMocks
	private EdgeChromiumPreferencePage page;
	
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();
	
	@Before
    public void setUp() throws Exception {
        testProjectFolder = Files.createTempDirectory("test").toFile();
        String location = testProjectFolder.getAbsolutePath();
        testProject = ProjectController.getInstance().addNewProject("test-project", "", location);
        ProjectController.getInstance().openProjectForUI(testProject.getId(), false, new NullProgressMonitor());
        page = new EdgeChromiumPreferencePage();
        MockitoAnnotations.initMocks(page);
    }
	
	@Test
	public void getDriverConnectorTest() throws Exception{
		tmpFolder.create();
		File newFolder = tmpFolder.newFolder("config");
		String folderPath = newFolder.getAbsolutePath();
		
		Method method = page.getClass().getDeclaredMethod("getDriverConnector", String.class);
		method.setAccessible(true);
		IDriverConnector connector = (IDriverConnector) method.invoke(page, folderPath);
		//System.out.println(connector.getSettingFileName());
		//System.out.println(connector.getSystemProperties());
		//System.out.println(connector.getUserConfigProperties());
		assertEquals(connector.getParentFolderPath().contains(folderPath), true);
		
	}
}
