package com.kms.katalon.execution.webservice;

import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.WSVerificationTestCaseEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.VariableEvaluationRunConfiguration;
import com.kms.katalon.execution.entity.WSVerificationTestCaseExecutedEntity;
import com.kms.katalon.execution.util.ExecutionProfileStore;
import com.kms.katalon.groovy.util.GroovyUtil;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;

public class VariableEvaluator {

    private static final String EVALUATION_SCRIPT_TEMPLATE_CLASS = IdConstants.KATALON_EXECUTION_BUNDLE_ID
            + ".generator.VariableEvaluationScriptTemplate";
    
    private static final String GENERATE_EVALUATION_SCRIPT_METHOD = "generateEvaluationScript";

    private static final String TEST_CASE_ID_PREFIX = "Variable-Eval_";
    
    public Map<String, Object> evaluate(String testObjectId, Map<String, String> variables) throws Exception {
        
        Map<String, Object> evaluatedVariables = new HashMap<>();
        
        WSVerificationTestCaseEntity testCaseEntity = createTestCaseEntity();

        VariableEvaluationRunConfiguration runConfig = new VariableEvaluationRunConfiguration();
        runConfig.setExecutionProfile(ExecutionProfileStore.getInstance().getSelectedProfile());
        runConfig.build(testCaseEntity, new WSVerificationTestCaseExecutedEntity(testCaseEntity));
        
        String evaluationScript = generateEvaluationScript(variables, runConfig);
        
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        URLClassLoader classLoader = GroovyUtil.getProjectClasLoader(project);
        GroovyShell groovyShell = new GroovyShell(classLoader);
        evaluatedVariables = (Map<String, Object>) groovyShell.evaluate(evaluationScript);
       
        return evaluatedVariables;
    }
    
    
    private String generateEvaluationScript(Map<String, String> variables, IRunConfiguration runConfig) 
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(EVALUATION_SCRIPT_TEMPLATE_CLASS);
        GroovyObject object = (GroovyObject) clazz.newInstance();
        String script = (String) object.invokeMethod(GENERATE_EVALUATION_SCRIPT_METHOD, 
                new Object[] {variables, runConfig});
        return script;
    }
    
    private WSVerificationTestCaseEntity createTestCaseEntity() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();

        WSVerificationTestCaseEntity testCaseEntity = new WSVerificationTestCaseEntity();
        testCaseEntity.setId(TEST_CASE_ID_PREFIX + System.currentTimeMillis());
        testCaseEntity.setProject(project);
        testCaseEntity.setScript("");

        return testCaseEntity;
    }
}
