package com.kms.katalon.execution.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.WSVerificationTestCaseEntity;
import com.kms.katalon.execution.configuration.VariableEvaluationRunConfiguration;
import com.kms.katalon.execution.entity.WSVerificationTestCaseExecutedEntity;
import com.kms.katalon.execution.util.ExecutionProfileStore;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class VariableEvaluator {

    private static final String EVALUATION_SCRIPT_TEMPLATE_CLASS = IdConstants.KATALON_EXECUTION_BUNDLE_ID
            + ".generator.VariableEvaluationScriptTemplate";
    
    private static final String GENERATE_EVALUATION_SCRIPT_METHOD = "generateEvaluationScript";

    private static final String TEST_CASE_ID_PREFIX = "Variable-Eval_";

    private static final String RESULT_FILE_PREFIX = "variable-eval-";

    private static final String RESULT_TEMP_FOLDER = "variables";
    
    public Map<String, Object> evaluate(String testObjectId, Map<String, String> variables) throws Exception {
        
        Map<String, Object> evaluatedVariables = new HashMap<>();
        
        File resultFile = createResultFile();

        String evaluationScript = generateEvaluationScript(resultFile, variables);
        
        WSVerificationTestCaseEntity testCaseEntity = createTestCaseEntity(evaluationScript);

        VariableEvaluationRunConfiguration runConfig = new VariableEvaluationRunConfiguration();
        runConfig.setExecutionProfile(ExecutionProfileStore.getInstance().getSelectedProfile());
        runConfig.build(testCaseEntity, new WSVerificationTestCaseExecutedEntity(testCaseEntity));
        
        System.out.println(runConfig.getExecutionSetting().getSettingFilePath());
        
        URLClassLoader classLoader = ProjectController.getInstance().getProjectClassLoader(
                ProjectController.getInstance().getCurrentProject());
        GroovyShell groovyShell = new GroovyShell(classLoader);
        Script script = groovyShell.parse(runConfig.getExecutionSetting().getScriptFile());
        script.run();
        try (FileInputStream fis = new FileInputStream(resultFile);
                ObjectInputStream ois = new ObjectInputStream(fis);) {

            evaluatedVariables = (Map<String, Object>) ois.readObject();
            
        } catch (IOException | ClassNotFoundException e) {
            LoggerSingleton.logError(e);
        }
        
        FileUtils.forceDelete(resultFile);
        
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
    
    private File createResultFile() throws IOException {
        String tempFolderPath = ProjectController.getInstance().getTempDir() + File.separator + RESULT_TEMP_FOLDER;
        File tempFolder = new File(tempFolderPath);
        tempFolder.mkdirs();
        File resultFile = File.createTempFile(RESULT_FILE_PREFIX, null, tempFolder);
        System.out.println(resultFile.getAbsolutePath());
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
}
