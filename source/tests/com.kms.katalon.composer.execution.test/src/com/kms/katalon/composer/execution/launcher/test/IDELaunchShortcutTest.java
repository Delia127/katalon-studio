package com.kms.katalon.composer.execution.launcher.test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.composer.execution.launcher.IDELaunchShorcut;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.configuration.BasicRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.groovy.util.GroovyUtil;

public class IDELaunchShortcutTest {

    private File testProjectFolder;

    private ProjectEntity testProject;
    
    private IFile scriptFile;

    @Before
    public void setUp() throws Exception {
        testProjectFolder = Files.createTempDirectory("kat-test").toFile();
        String location = testProjectFolder.getAbsolutePath();
        testProject = ProjectController.getInstance().addNewProject("test-project", "", location);
        ProjectController.getInstance().openProjectForUI(testProject.getId(), false, new NullProgressMonitor());
    
        IProject project = GroovyUtil.getGroovyProject(testProject);
        IFolder scriptFolder = project.getFolder("Include/scripts/groovy");
        scriptFile = scriptFolder.getFile("TestScript.groovy");
        InputStream scriptContent = getClass().getClassLoader()
                .getResourceAsStream("resources/script/SampleScript.groovy");
        scriptFile.create(scriptContent, IFile.FORCE, new NullProgressMonitor());
        scriptFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
    }

    @Test
    public void testLaunchingScriptFileWithAdditionalEnvironmentVariablesAndVMArguments() throws CoreException, InterruptedException {
        IRunConfiguration runConfig = new BasicRunConfiguration();

        Map<String, String> environmentVariables = new HashMap<String, String>() {
            {
                put("ENV_1", "env_value_1");
                put("ENV_2", "env_value_2");
            }
        };
        runConfig.setAdditionalEnvironmentVariables(environmentVariables);

        String[] vmArgs = new String[] { "-DtestArg1=value1", "-DtestArg2=value2" };
        runConfig.setVmArgs(vmArgs);

        ILaunch launch = new IDELaunchShorcut().launch(scriptFile, LaunchMode.RUN, runConfig);
        IProcess launchProcess = launch.getProcesses()[0];
        StringBuffer outputBuffer = new StringBuffer();
        launchProcess.getStreamsProxy().getOutputStreamMonitor().addListener(new IStreamListener() {
            @Override
            public void streamAppended(String text, IStreamMonitor monitor) {
                outputBuffer.append(text);
            }
        });
        waitForProcessTerminated(launchProcess);

        assertThat("The process should exit normally", launchProcess.getExitValue() == 0);
        
        String processOutputContent = outputBuffer.toString();
        assertThat("The process output should contain logged value of environment variable ENV_1",
                processOutputContent.contains("ENV_1:env_value_1"));
        assertThat("The process output should contain logged value of environment variable ENV_2",
                processOutputContent.contains("ENV_2:env_value_2"));
        assertThat("The process output should contain logged value of system property testArg1",
                processOutputContent.contains("-DtestArg1=value1"));
        assertThat("The process output should contain logged value of system property testArg2",
                processOutputContent.contains("-DtestArg2=value2"));
    }
    
    private void waitForProcessTerminated(IProcess process) throws InterruptedException {
        while (!process.isTerminated()) {
            Thread.sleep(500L);
        }
    }

    @After
    public void tearDown() throws Exception {
        ProjectController.getInstance().closeProject(testProject.getId(), new NullProgressMonitor());
        FileUtils.forceDelete(testProjectFolder);
    }
}
