package com.kms.katalon.execution.webservice;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.configuration.WSVerificationRunConfiguration;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.launcher.VerificationScriptLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.ILauncherResult;

public class VerificationScriptExecutor {
    
    private static final String TEMP_TEST_CASE_META_ROOT_FOLDER = "Libs";
    
    private static final String GROOVY_SCRIPT_FILE_EXTENSION = ".groovy";
    
    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
    
    private VerificationScriptLauncher launcher;
    
    private TestCaseEntity testCaseEntity;
    
    private File scriptFile;
    
    public void execute(String script, ResponseObject responseObject) throws Exception {
        testCaseEntity = createTempTestCase(script, responseObject);
        
        WSVerificationRunConfiguration runConfig = new WSVerificationRunConfiguration(responseObject);

        runConfig.build(testCaseEntity, new TestCaseExecutedEntity(testCaseEntity));

        LauncherManager launcherManager = LauncherManager.getInstance();
        launcher = new VerificationScriptLauncher(launcherManager, runConfig, new Runnable() {
            
            @Override
            public void run() {
                deleteTempTestCase();
                
                ILauncherResult result = launcher.getResult();
                TestStatusValue[] statusValues = result.getResultValues();
                
                eventBroker.post(EventConstants.WS_VERIFICATION_EXECUTION_FINISHED, statusValues[0]);
            }
        });
        launcherManager.addLauncher(launcher);
    }
    
    private TestCaseEntity createTempTestCase(String script, ResponseObject responseObject) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        
        TestCaseEntity testCaseEntity = TestCaseController.getInstance().newTempTestCase(project);
        scriptFile = new File(getTempScriptFilePath(project, testCaseEntity.getName()));
        scriptFile.createNewFile();
        FileUtils.writeStringToFile(scriptFile, script);
        
        return testCaseEntity;
    }
    
    private String getTempScriptFilePath(ProjectEntity project, String testCaseName) {
        return new StringBuilder()
                .append(project.getFolderLocation())
                .append(File.separator)
                .append(TEMP_TEST_CASE_META_ROOT_FOLDER)
                .append(File.separator)
                .append(testCaseName)
                .append(GROOVY_SCRIPT_FILE_EXTENSION)
                .toString();
    }
    
    public void deleteTempTestCase() {
        TestCaseController.getInstance().deleteTempTestCase(testCaseEntity);
        scriptFile.delete();
    }
}
