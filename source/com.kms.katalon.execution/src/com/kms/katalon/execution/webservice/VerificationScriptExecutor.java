package com.kms.katalon.execution.webservice;

import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.WSVerificationTestCaseEntity;
import com.kms.katalon.execution.configuration.WSVerificationRunConfiguration;
import com.kms.katalon.execution.entity.WSVerificationTestCaseExecutedEntity;
import com.kms.katalon.execution.launcher.VerificationScriptLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.util.ExecutionProfileStore;

public class VerificationScriptExecutor {
    
    private static final String WS_VERIFICATION = "WSVerification_";
    
    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
    
    private VerificationScriptLauncher launcher;
    
    private WSVerificationTestCaseEntity testCaseEntity;
    
    public void execute(String script, ResponseObject responseObject) throws Exception {
        testCaseEntity = createTestCaseEntity(script, responseObject);
        
        WSVerificationRunConfiguration runConfig = new WSVerificationRunConfiguration(responseObject);
        runConfig.setExecutionProfile(ExecutionProfileStore.getInstance().getSelectedProfile());

        runConfig.build(testCaseEntity, new WSVerificationTestCaseExecutedEntity(testCaseEntity));

        LauncherManager launcherManager = LauncherManager.getInstance();
        launcher = new VerificationScriptLauncher(launcherManager, runConfig, new Runnable() {
            
            @Override
            public void run() {
                ILauncherResult result = launcher.getResult();
                TestStatusValue[] statusValues = result.getResultValues();
                
                eventBroker.post(EventConstants.WS_VERIFICATION_EXECUTION_FINISHED, statusValues[0]);
            }
        });
        launcherManager.addLauncher(launcher);
    }
    
    private WSVerificationTestCaseEntity createTestCaseEntity(String script, ResponseObject responseObject) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        
        WSVerificationTestCaseEntity testCaseEntity = new WSVerificationTestCaseEntity();
        testCaseEntity.setId(WS_VERIFICATION + System.currentTimeMillis());
        testCaseEntity.setProject(project);
        testCaseEntity.setScript(script);
        
        return testCaseEntity;
    }
}
