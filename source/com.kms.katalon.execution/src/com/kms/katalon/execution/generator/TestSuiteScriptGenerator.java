package com.kms.katalon.execution.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.util.SyntaxUtil;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

import groovy.lang.GroovyObject;

public class TestSuiteScriptGenerator {
    private static final String TEMPLATE_CLASS_NAME = IdConstants.KATALON_EXECUTION_BUNDLE_ID
            + ".generator.TestSuiteScriptTemplate";

    private static final String GENERATED_TEST_SUITE_SCRIPT_METHOD_NAME = "generateTestSuiteScriptFile";

    private static final String TEMP_TEST_SUITE_FILE_NAME = "TempTestSuite";

    private TestSuiteEntity testSuite;

    private IRunConfiguration config;

    private TestSuiteExecutedEntity testSuiteExecuted;

    private StringBuilder syntaxErrorCollector;

    public TestSuiteScriptGenerator(TestSuiteEntity testSuite, IRunConfiguration config,
            TestSuiteExecutedEntity testSuiteExecutedEntity) {
        this.testSuite = testSuite;
        this.config = config;
        this.testSuiteExecuted = testSuiteExecutedEntity;
    }
    
    public File generateScriptFile() throws Exception {
        return generateScriptFile("");
    }

    /**
     * Generate the testCaseBinding file which contains rows
     * of Test Case + its Variable Bindings from Test Data. 
     * 
     * <p>This file will be used for data-driven testing.</p>
     * 
     * <p>
     * If given a non-empty string,
     * it will be written to the file, otherwise the content will
     * be computed based on Test Suite's list of test cases, test case variables,
     * test data and the combination types (ONE, MANY, etc)
     * </p>
     * 
     * @param tcBindings
     * @return
     * @throws Exception
     */
    public File generateScriptFile(String tcBindings) throws Exception {
        IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(testSuite.getProject());
        File file = new File(libFolder.getRawLocation().toString(),
                TEMP_TEST_SUITE_FILE_NAME + System.currentTimeMillis() + GroovyConstants.GROOVY_FILE_EXTENSION);
        file.createNewFile();
        GroovyObject object = (GroovyObject) Class.forName(TEMPLATE_CLASS_NAME).newInstance();
        object.invokeMethod(GENERATED_TEST_SUITE_SCRIPT_METHOD_NAME,
                new Object[] { file, testSuite, createTestCaseBindings(tcBindings), config, testSuiteExecuted });

        libFolder.refreshLocal(IResource.DEPTH_ONE, null);
        return file;
    }

    public String generateScriptAsString() throws Exception {
        GroovyObject object = (GroovyObject) Class.forName(TEMPLATE_CLASS_NAME).newInstance();
        return (String) object.invokeMethod(GENERATED_TEST_SUITE_SCRIPT_METHOD_NAME,
                new Object[] { null, testSuite, createTestCaseBindings(), config });
    }
    
    public File createTestCaseBindings() throws IOException {
        return createTestCaseBindings("");
    }

   private File createTestCaseBindings(String tcBindings) throws IOException {
        File testCaseBindingFile = new File(config.getExecutionSetting().getFolderPath(), "testCaseBinding");
        testCaseBindingFile.createNewFile();
        
        if (!tcBindings.isEmpty()) {
            FileUtils.write(testCaseBindingFile, tcBindings, "UTF-8", false);
            return testCaseBindingFile;
        }
        
        syntaxErrorCollector = new StringBuilder();

        List<TestSuiteTestCaseLink> lstTestCaseRun = TestSuiteController.getInstance().getTestSuiteTestCaseRun(
                testSuite);
        for (IExecutedEntity testCaseExecuted : testSuiteExecuted.getExecutedItems()) {
            String testCaseId = testCaseExecuted.getSourceId();
            TestSuiteTestCaseLink testCaseLink = null;
            if (testSuite instanceof FilteringTestSuiteEntity) {
                testCaseLink = new TestSuiteTestCaseLink();
                testCaseLink.setTestCaseId(testCaseId);
                testCaseLink.setIsRun(true);
            } else {
                testCaseLink = getTestCaseLink(testCaseId, lstTestCaseRun);
                // KAT-4017, removing a test case so the next iteration we will consider
                // the next (possibly duplicate) test cases
                lstTestCaseRun.remove(testCaseLink);
            }
            
            if (testCaseLink == null) {
                throw new IllegalArgumentException("Test case: '" + testCaseId + "' not found");
            }

            List<String> testCaseBinding = getTestCaseBindingString(testCaseLink,
                    (TestCaseExecutedEntity) testCaseExecuted);
            FileUtils.writeLines(testCaseBindingFile, "UTF-8", testCaseBinding, true);
        }

        if (syntaxErrorCollector.toString().isEmpty()) {
            return testCaseBindingFile;
        } else {
            throw new IllegalArgumentException(syntaxErrorCollector.toString());
        }
    }    

    private TestSuiteTestCaseLink getTestCaseLink(String testCaseId, List<TestSuiteTestCaseLink> distinctTestCaseLink) {
        for (TestSuiteTestCaseLink testCaseLink : distinctTestCaseLink) {
            if (testCaseLink.getTestCaseId().equals(testCaseId)) {
                return testCaseLink;
            }
        }
        return null;
    }

    private List<String> getTestCaseBindingString(TestSuiteTestCaseLink testCaseLink,
            TestCaseExecutedEntity testCaseExecutedEntity) {
        List<String> testCaseBindingStrings = new ArrayList<String>();
        for (int iterationIdx = 0; iterationIdx < testCaseExecutedEntity.getLoopTimes(); iterationIdx++) {
            TestCaseBindingStringBuilder builder = new TestCaseBindingStringBuilder(iterationIdx, testCaseExecutedEntity);

            for (VariableLink variableLink : testCaseLink.getVariableLinks()) {
                builder.append(variableLink, testSuiteExecuted.getTestDataMap());
            }

            if (builder.hasErrors()) {
                syntaxErrorCollector.append(builder.getErrorMessage()).append(SyntaxUtil.LINE_SEPARATOR);
                break;
            } else {
                testCaseBindingStrings.add(builder.build());
            }
        }

        return testCaseBindingStrings;
    }
}
