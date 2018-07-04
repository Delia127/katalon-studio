package com.kms.katalon.execution.webservice;

import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.WSVerificationTestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.execution.configuration.ExistingRunConfiguration;
import com.kms.katalon.execution.entity.WSVerificationTestCaseExecutedEntity;
import com.kms.katalon.execution.launcher.RecordingScriptLauncher;
import com.kms.katalon.execution.launcher.VerificationScriptLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.util.ExecutionProfileStore;

public class RecordingScriptGenerator {

    private static final String WEBUI_VERIFICATION = "WebUIVerifition_";

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private VerificationScriptLauncher launcher;

    private String capturedTestObjectsCacheFile;

    public RecordingScriptGenerator(String recordSessionFolder) {
        this.capturedTestObjectsCacheFile = recordSessionFolder;
    }

    public void execute(String script, List<VariableEntity> variables, WebDriver webDriver, WebUIDriverType driverType,
            ProjectEntity project) throws Exception {
        WSVerificationTestCaseEntity testCaseEntity = createTestCaseEntity(script, variables);

        RecodingRunConfiguration runConfig = new RecodingRunConfiguration(project.getFolderLocation(),
                ((RemoteWebDriver) webDriver).getSessionId().toString(),
                DriverFactory.getWebDriverServerUrl((RemoteWebDriver) webDriver), driverType.getName());
        runConfig.setExecutionProfile(ExecutionProfileStore.getInstance().getSelectedProfile());

        runConfig.build(testCaseEntity, new WSVerificationTestCaseExecutedEntity(testCaseEntity));

        LauncherManager launcherManager = LauncherManager.getInstance();
        launcher = new RecordingScriptLauncher(launcherManager, runConfig, new Runnable() {

            @Override
            public void run() {
                ILauncherResult result = launcher.getResult();
                TestStatusValue[] statusValues = result.getResultValues();

                eventBroker.post(EventConstants.WEBUI_VERIFICATION_EXECUTION_FINISHED, statusValues[0]);
            }
        }) {
            @Override
            protected void onStartExecutionComplete() {
                super.onStartExecutionComplete();
                eventBroker.post(EventConstants.WEBUI_VERIFICATION_START_EXECUTION, null);
            }
        };
        launcherManager.addLauncher(launcher);
    }

    private WSVerificationTestCaseEntity createTestCaseEntity(String script, List<VariableEntity> variables)
            throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();

        WSVerificationTestCaseEntity testCaseEntity = new WSVerificationTestCaseEntity();
        testCaseEntity.setId(WEBUI_VERIFICATION + System.currentTimeMillis());
        testCaseEntity.setVariables(variables);
        testCaseEntity.setProject(project);
        testCaseEntity.setScript(script);

        return testCaseEntity;
    }

    private class RecodingRunConfiguration extends ExistingRunConfiguration {

        public RecodingRunConfiguration(String projectDir, String sessionId, String remoteUrl, String driverName) {
            super(projectDir, sessionId, remoteUrl, driverName);
        }

        @Override
        public Map<String, Object> getProperties() {
            Map<String, Object> properties = super.getProperties();

            properties.put(RunConfiguration.RECORD_CAPTURED_OBJECTS_FILE, capturedTestObjectsCacheFile);

            return properties;
        }
    }
    
    public void stopLauncher() {
        LauncherManager.getInstance().stopLauncher(launcher);
    }
}
