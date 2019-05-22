package com.kms.katalon.execution.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.WSVerificationTestCaseEntity;
import com.kms.katalon.execution.configuration.VariableEvaluationRunConfiguration;
import com.kms.katalon.execution.entity.WSVerificationTestCaseExecutedEntity;
import com.kms.katalon.execution.launcher.VerificationScriptLauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.util.ExecutionProfileStore;

import groovy.lang.GroovyObject;

public class VariableEvaluator {
    
    private static final String EVALUATION_SCRIPT_TEMPLATE_CLASS = IdConstants.KATALON_EXECUTION_BUNDLE_ID
            + ".generator.VariableEvaluationScriptTemplate";
    
    private static final String GENERATE_EVALUATION_SCRIPT_METHOD = "generateEvaluationScript";

    private static final String TEST_CASE_ID_PREFIX = "Variable-Eval_";

    private static final String RESULT_FILE_PREFIX = "variable-eval-";

    private static final String RESULT_TEMP_FOLDER = "variables";

    private VerificationScriptLauncher launcher;

    private WSVerificationTestCaseEntity testCaseEntity;

    private boolean evaluationFinished = false;

    private Map<String, String> evaluatedVariables;

    public Map<String, String> evaluate(String testObjectId, Map<String, String> variables) throws Exception {
        
        evaluatedVariables = new HashMap<>();
        
        File resultFile = createResultFile();

        String evaluationScript = generateEvaluationScript(resultFile, variables);
        
        testCaseEntity = createTestCaseEntity(evaluationScript);

        VariableEvaluationRunConfiguration runConfig = new VariableEvaluationRunConfiguration();
        runConfig.setExecutionProfile(ExecutionProfileStore.getInstance().getSelectedProfile());
        runConfig.build(testCaseEntity, new WSVerificationTestCaseExecutedEntity(testCaseEntity));

        LauncherManager launcherManager = LauncherManager.getInstance();
        launcher = new VerificationScriptLauncher(UUID.randomUUID().toString(), launcherManager, runConfig,
                new Runnable() {
                    @Override
                    public void run() {
                        evaluationFinished = true;
                    }
                });
        launcherManager.addLauncher(launcher);
        
        waitForEvaluationFinished(resultFile);
        
        return evaluatedVariables;
    }
    
    private String generateEvaluationScript(File resultFile, Map<String, String> variables) 
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class clazz = Class.forName(EVALUATION_SCRIPT_TEMPLATE_CLASS);
        GroovyObject object = (GroovyObject) clazz.newInstance();
        String script = (String) object.invokeMethod(GENERATE_EVALUATION_SCRIPT_METHOD, 
                new Object[] {resultFile.getAbsolutePath(), variables});
        return script;
    }

    private void waitForEvaluationFinished(File resultFile) 
            throws InterruptedException, java.util.concurrent.ExecutionException, TimeoutException {
        
        Future<Map<String, String>> future = Executors.newSingleThreadExecutor().submit(() -> {
            while (!evaluationFinished) {
                Thread.sleep(300);
            }
            
            Map<String, String> evaluationResult = new HashMap<>();
            try (FileInputStream fis = new FileInputStream(resultFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);) {

                evaluationResult = (Map<String, String>) ois.readObject();
                
            } catch (IOException | ClassNotFoundException e) {
                LoggerSingleton.logError(e);
            }

            try {
                FileUtils.forceDelete(resultFile);
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
            
            return evaluationResult;
        });

        evaluatedVariables = future.get(60000, TimeUnit.MILLISECONDS);
    }

    private File createResultFile() throws IOException {
        String tempFolderPath = ProjectController.getInstance().getTempDir() + File.separator + RESULT_TEMP_FOLDER;
        File tempFolder = new File(tempFolderPath);
        tempFolder.mkdirs();
        File resultFile = File.createTempFile(RESULT_FILE_PREFIX, null, tempFolder);
        return resultFile;
    }

    private WSVerificationTestCaseEntity createTestCaseEntity(String script) {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();

        WSVerificationTestCaseEntity testCaseEntity = new WSVerificationTestCaseEntity();
        testCaseEntity.setId(TEST_CASE_ID_PREFIX + System.currentTimeMillis());
        testCaseEntity.setProject(project);
        testCaseEntity.setScript(script);

        return testCaseEntity;
    }

    public boolean isEvaluationFinished() {
        return evaluationFinished;
    }

    public Map<String, String> getEvaluatedVariables() {
        return evaluatedVariables;
    }

}
