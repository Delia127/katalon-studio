package com.kms.katalon.execution.launcher.process.test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.launcher.process.LaunchProcessor;

public class LaunchProcessorTest {

    private File testProjectFolder;

    private ProjectEntity testProject;
    
    private File scriptFile;

    @Before
    public void setUp() throws Exception {
        testProjectFolder = Files.createTempDirectory("kat-test").toFile();
        String location = testProjectFolder.getAbsolutePath();
        testProject = ProjectController.getInstance().addNewProject("test-project", "", location);
        ProjectController.getInstance().openProject(testProject.getId(), false);
    
        File scriptFolder = new File(testProject.getFolderLocation(), "Include/scripts/groovy");
        scriptFile = new File(scriptFolder, "TestScript.groovy");
        InputStream scriptContent = getClass().getClassLoader()
                .getResourceAsStream("resources/script/SampleScript.groovy");
        FileUtils.copyInputStreamToFile(scriptContent, scriptFile);
    }

    @Test
    public void testLaunchingScriptFileWithAdditionalEnvironmentVariablesAndVMArguments() throws CoreException, InterruptedException, IOException, ControllerException {
        Map<String, String> environmentVariables = new HashMap<String, String>() {
            {
                put("ENV_1", "env_value_1");
                put("ENV_2", "env_value_2");
            }
        };

        String[] vmArgs = new String[] { "-DtestArg1=value1", "-DtestArg2=value2" };

        LaunchProcessor launchProcessor = new LaunchProcessor(ClassPathResolver.getClassPaths(testProject), environmentVariables, vmArgs);
        Process process = launchProcessor.execute(scriptFile);
        process.waitFor();

        assertThat("The process should exit normally", process.exitValue() == 0);
        
        String processOutputContent = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        assertThat("The process output should contain logged value of environment variable ENV_1",
                processOutputContent.contains("ENV_1:env_value_1"));
        assertThat("The process output should contain logged value of environment variable ENV_2",
                processOutputContent.contains("ENV_2:env_value_2"));
        assertThat("The process output should contain logged value of system property testArg1",
                processOutputContent.contains("-DtestArg1=value1"));
        assertThat("The process output should contain logged value of system property testArg2",
                processOutputContent.contains("-DtestArg2=value2"));
    }

    @After
    public void tearDown() throws Exception {
        ProjectController.getInstance().closeProject(testProject.getId(), new NullProgressMonitor());
        FileUtils.forceDelete(testProjectFolder);
    }
}
