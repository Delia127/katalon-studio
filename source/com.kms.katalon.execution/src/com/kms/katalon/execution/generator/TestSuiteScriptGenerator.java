package com.kms.katalon.execution.generator;

import groovy.lang.GroovyObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.entity.link.TestDataCombinationType;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.entity.TestDataExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.util.SyntaxUtil;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

public class TestSuiteScriptGenerator {
    private static final String TEMPLATE_CLASS_NAME = IdConstants.KATALON_EXECUTION_BUNDLE_ID
            + ".generator.TestSuiteScriptTemplate";

    private static final String GENERATED_TEST_SUITE_SCRIPT_METHOD_NAME = "generateTestSuiteScriptFile";

    private static final String TEMP_TEST_SUITE_FILE_NAME = "TempTestSuite";

    private TestSuiteEntity testSuite;

    private IRunConfiguration config;

    private TestSuiteExecutedEntity testSuiteExecuted;

    public TestSuiteScriptGenerator(TestSuiteEntity testSuite, IRunConfiguration config,
            TestSuiteExecutedEntity testSuiteExecutedEntity) {
        this.testSuite = testSuite;
        this.config = config;
        this.testSuiteExecuted = testSuiteExecutedEntity;
    }

    public File generateScriptFile() throws Exception {
        IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(testSuite.getProject());
        File file = new File(libFolder.getRawLocation().toString(), TEMP_TEST_SUITE_FILE_NAME
                + System.currentTimeMillis() + GroovyConstants.GROOVY_FILE_EXTENSION);
        file.createNewFile();
        GroovyObject object = (GroovyObject) Class.forName(TEMPLATE_CLASS_NAME).newInstance();
        object.invokeMethod(GENERATED_TEST_SUITE_SCRIPT_METHOD_NAME, new Object[] { file, testSuite,
                createTestCaseBindings(), config, testSuiteExecuted });
        libFolder.refreshLocal(IResource.DEPTH_ONE, null);
        return file;
    }

    public String generateScriptAsString() throws Exception {
        GroovyObject object = (GroovyObject) Class.forName(TEMPLATE_CLASS_NAME).newInstance();
        return (String) object.invokeMethod(GENERATED_TEST_SUITE_SCRIPT_METHOD_NAME, new Object[] { null, testSuite,
                createTestCaseBindings(), config });
    }

    public List<String> createTestCaseBindings() throws Exception {
        List<String> testCaseBindings = new ArrayList<String>();
        StringBuilder syntaxErrorCollector = new StringBuilder();

        List<TestSuiteTestCaseLink> lstTestCaseRun = TestSuiteController.getInstance().getTestSuiteTestCaseRun(
                testSuite);
        for (TestCaseExecutedEntity testCaseExecuted : testSuiteExecuted.getTestCaseExecutedEntities()) {
            TestSuiteTestCaseLink testCaseLink = null;
            for (TestSuiteTestCaseLink testSuiteTestCaseLink : lstTestCaseRun) {
                if (testSuiteTestCaseLink.getTestCaseId().equals(testCaseExecuted.getTestCaseId())) {
                    testCaseLink = testSuiteTestCaseLink;
                    break;
                }
            }
            if (testCaseLink != null) {
                TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(
                        testCaseLink.getTestCaseId());
                if (testCase != null && testCaseLink.getIsRun()) {
                    List<String> testCaseBinding = getTestCaseBindingString(testCaseLink, syntaxErrorCollector,
                            testCaseExecuted);
                    testCaseBindings.addAll(testCaseBinding);
                }
            }
        }

        if (syntaxErrorCollector.toString().isEmpty()) {
            return testCaseBindings;
        } else {
            throw new IllegalArgumentException(syntaxErrorCollector.toString());
        }
    }

    private List<String> getTestCaseBindingString(TestSuiteTestCaseLink testCaseLink,
            StringBuilder syntaxErrorCollector, TestCaseExecutedEntity testCaseExecutedEntity) throws Exception {
        List<String> testCaseBindingStrings = new ArrayList<String>();
        for (int i = 0; i < testCaseExecutedEntity.getLoopTimes(); i++) {
            boolean needToBreak = false;
            String testCaseReportName = testCaseLink.getTestCaseId();
            if (testCaseExecutedEntity.getLoopTimes() > 1) {
                testCaseReportName += " - Iteration " + Integer.toString(i + 1);
            }
            StringBuilder testCaseBindingBuilder = new StringBuilder();
            if (testCaseLink.getVariableLinks().isEmpty()) {
                testCaseBindingBuilder.append("new TestCaseBinding('" + testCaseReportName + "', null)");
            } else {
                testCaseBindingBuilder.append("new TestCaseBinding('" + testCaseReportName + "', [");

                StringBuilder variableBuilder = new StringBuilder();

                for (VariableLink variableLink : testCaseLink.getVariableLinks()) {
                    VariableEntity variableEntity = TestSuiteController.getInstance().getVariable(testCaseLink,
                            variableLink);
                    if (variableEntity != null) {
                        String variableValue = variableLink.getValue();
                        if (variableLink.getType() == VariableType.DATA_COLUMN
                                || variableLink.getType() == VariableType.DATA_COLUMN_INDEX) {

                            if (StringUtils.isBlank(variableLink.getTestDataLinkId())) {
                                syntaxErrorCollector.append("Wrong syntax at [Test case ID: "
                                        + testCaseLink.getTestCaseId() + ", Variable name: " + variableEntity.getName()
                                        + ", Test data: <empty>]: Test data cannot be empty.");
                                needToBreak = true;
                                break;
                            }

                            TestDataExecutedEntity testDataExecutedEntity = testCaseExecutedEntity
                                    .getTestDataExecuted(variableLink.getTestDataLinkId());

                            TestData testData = testSuiteExecuted.getTestDataMap().get(
                                    testDataExecutedEntity.getTestDataId());

                            DataFileEntity dataFileEntity = TestDataController.getInstance().getTestDataByDisplayId(
                                    testDataExecutedEntity.getTestDataId());

                            if (!StringUtils.isBlank(variableValue)) {
                                int rowIndex = 0;
                                if (testDataExecutedEntity.getType() == TestDataCombinationType.ONE) {
                                    rowIndex = i % testDataExecutedEntity.getRowIndexes().length;
                                } else {
                                    rowIndex = (i / testDataExecutedEntity.getMultiplier())
                                            % testDataExecutedEntity.getRowIndexes().length;
                                }

                                try {
                                    if (variableLink.getType() == VariableType.DATA_COLUMN) {
                                        variableValue = GroovyStringUtil.escapeGroovy(testData.getValue(variableValue,
                                                testDataExecutedEntity.getRowIndexes()[rowIndex]));
                                    } else if (variableLink.getType() == VariableType.DATA_COLUMN_INDEX) {
                                        variableValue = GroovyStringUtil.escapeGroovy(testData.getValue(
                                                Integer.parseInt(variableValue),
                                                testDataExecutedEntity.getRowIndexes()[rowIndex]));
                                    }

                                    if (variableValue != null) {
                                        variableValue = "'" + variableValue + "'";
                                    } else {
                                        variableValue = "null";
                                    }

                                } catch (IllegalArgumentException ex) {
                                    syntaxErrorCollector.append("Wrong syntax at [Test case ID: "
                                            + testCaseLink.getTestCaseId() + ", Variale name: "
                                            + variableEntity.getName() + ", Test data: "
                                            + testDataExecutedEntity.getTestDataId() + ", Column name: "
                                            + variableValue + "]: ");
                                    if (dataFileEntity != null && dataFileEntity.isContainsHeaders()) {
                                        syntaxErrorCollector
                                                .append(getMessageForInvalidColumn(variableValue, testData));
                                    } else if (dataFileEntity != null && !dataFileEntity.isContainsHeaders()) {
                                        syntaxErrorCollector.append("Test Data is using index not column name");
                                    }
                                    syntaxErrorCollector.append(SyntaxUtil.LINE_SEPERATOR);
                                    needToBreak = true;
                                    break;
                                }
                            } else {
                                syntaxErrorCollector.append("Wrong syntax at [Test case ID: "
                                        + testCaseLink.getTestCaseId() + ", Variable name: " + variableEntity.getName()
                                        + ", Test data: " + testDataExecutedEntity.getTestDataId() + ", Column name: "
                                        + variableValue + "<empty>]: Column name cannot be empty. ");
                                if (dataFileEntity != null && dataFileEntity.isContainsHeaders()) {
                                    syntaxErrorCollector.append(getMessageForPossibleColumnName(testData));
                                } else if (dataFileEntity != null && !dataFileEntity.isContainsHeaders()) {
                                    syntaxErrorCollector.append("Test Data is using index not column name");
                                }

                                needToBreak = true;
                                break;
                            }
                        } else {
                            if (variableValue.isEmpty()) {
                                // use default value
                                continue;
                            }
                        }

                        if (SyntaxUtil.checkVariableSyntax(variableEntity.getName(), variableValue)
                                && !variableValue.isEmpty()) {
                            variableBuilder.append(variableEntity.getName()).append(" : ").append(variableValue)
                                    .append(" , ");
                        } else {
                            syntaxErrorCollector.append(
                                    "Wrong syntax at [Test case ID: " + testCaseLink.getTestCaseId()
                                            + ", Variable Name: " + variableEntity.getName() + ", Value: "
                                            + variableValue + "]").append(SyntaxUtil.LINE_SEPERATOR);
                            needToBreak = true;
                            break;
                        }
                    }
                }
                if (needToBreak) {
                    break;
                }

                String variableMapBinding = variableBuilder.toString();
                if (variableMapBinding.isEmpty())
                    variableMapBinding = ":";

                testCaseBindingBuilder.append(variableMapBinding).append("])");
            }
            testCaseBindingStrings.add(testCaseBindingBuilder.toString());
        }

        return testCaseBindingStrings;
    }

    private String getMessageForInvalidColumn(String columnName, TestData testData) throws IOException {
        StringBuilder messageBuilder = new StringBuilder("Invalid column name '");
        messageBuilder.append(columnName).append("'. ").append(getMessageForPossibleColumnName(testData));

        return messageBuilder.toString();
    }

    private String getMessageForPossibleColumnName(TestData testData) throws IOException {
        StringBuilder messageBuilder = new StringBuilder("Possible values are '").append(
                arrayToString(testData.getColumnNames())).append("'");

        return messageBuilder.toString();
    }

    private String arrayToString(String[] columnNames) {
        if (ArrayUtils.isEmpty(columnNames)) {
            return "[]";
        }
        StringBuilder stringBuilder = new StringBuilder("[");
        int validColumns = 0;
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            if (StringUtils.isBlank(columnName)) {
                continue;
            }
            
            if (validColumns > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(columnName);
            validColumns++;
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
