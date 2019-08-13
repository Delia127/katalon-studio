package com.kms.katalon.execution.generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.testcase.TestCaseBinding;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.link.TestDataCombinationType;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.execution.entity.TestCaseExecutedEntity;
import com.kms.katalon.execution.entity.TestDataExecutedEntity;
import com.kms.katalon.execution.exception.SyntaxErrorException;
import com.kms.katalon.execution.util.SyntaxUtil;
import com.kms.katalon.logging.LogUtil;

public class TestCaseBindingStringBuilder {

    private int iterationIdx;

    private TestCaseExecutedEntity testCaseExecutedEntity;

    private Map<String, Object> variableBinding;

    private StringBuilder syntaxErrorMessage;

    public TestCaseBindingStringBuilder(int iterationIdx, TestCaseExecutedEntity testCaseExecutedEntity) {
        this.iterationIdx = iterationIdx;
        this.testCaseExecutedEntity = testCaseExecutedEntity;
        this.variableBinding = new HashMap<String, Object>();
        this.syntaxErrorMessage = new StringBuilder();
    }

    private String getTestCaseBindingName() {
        String testCaseBindingName = testCaseExecutedEntity.getSourceId();
        if (testCaseExecutedEntity.getLoopTimes() > 1) {
            testCaseBindingName += " - Iteration " + Integer.toString(iterationIdx + 1);
        }
        return testCaseBindingName;
    }

    private String getDeclarationWithTestCaseName() {
        return new StringBuilder("new TestCaseBinding('").append(getTestCaseBindingName())
                .append("', '")
                .append(testCaseExecutedEntity.getSourceId())
                .append("', ")
                .toString();
    }

    public String build() {
        TestCaseBinding testCaseBinding = new TestCaseBinding(getTestCaseBindingName(),
                testCaseExecutedEntity.getSourceId(), variableBinding.isEmpty() ? null : variableBinding);
        String testCaseBindingJson = JsonUtil.toJson(testCaseBinding, false);
        return testCaseBindingJson;
    }

    public boolean hasErrors() {
        return syntaxErrorMessage.length() > 0;
    }

    public String getErrorMessage() {
        return syntaxErrorMessage.toString();
    }

    public void append(VariableLink variableLink, Map<String, TestData> testDataMap) {
        VariableEntity variableEntity = null;
        try {
            variableEntity = TestSuiteController.getInstance().getVariable(testCaseExecutedEntity.getSourceId(),
                    variableLink);
        } catch (Exception e) {
            LogUtil.logError(e);
            throw new IllegalArgumentException(e.getMessage());
        }

        if (variableEntity == null) {
            return;
        }

        try {
            String variableName = variableEntity.getName();
            Object variableValue = getVariableValue(variableName, variableLink, testDataMap);
            if (variableValue != null) {
                variableBinding.put(variableName, variableValue);
            }
        } catch (SyntaxErrorException e) {
            syntaxErrorMessage.append(e.getMessage()).append(SyntaxUtil.LINE_SEPARATOR);
        }
    }

    private int getRowIndex(TestDataExecutedEntity testDataExecutedEntity) {
        int rowIndex = 0;
        if (testDataExecutedEntity.getType() == TestDataCombinationType.ONE) {
            rowIndex = iterationIdx % testDataExecutedEntity.getRowIndexes().length;
        } else {
            rowIndex = (iterationIdx / testDataExecutedEntity.getMultiplier())
                    % testDataExecutedEntity.getRowIndexes().length;
        }

        return testDataExecutedEntity.getRowIndexes()[rowIndex];
    }

    private Object getVariableValue(String variableName, VariableLink variableLink, Map<String, TestData> testDataMap)
            throws SyntaxErrorException {
        Object variableValue = variableLink.getValue();
        try {
            switch (variableLink.getType()) {
                case DATA_COLUMN:
                    variableValue = getValueByColumnName(variableName, variableLink, testDataMap);
                    break;
                case DATA_COLUMN_INDEX:
                    variableValue = getValueByColumnIndex(variableName, variableLink, testDataMap);
                    break;
                case DEFAULT:
                    variableValue = null;
                    break;
                case SCRIPT_VARIABLE:
                    break;
                default:
                    throw new NotImplementedException(variableLink.getType().name());
            }

            // SyntaxUtil.checkVariableSyntax(GroovyStringUtil.toGroovyStringFormat(variableName),
            // variableValue.toString());
            return variableValue;
        } catch (IOException | IllegalArgumentException ex) {
            throw new SyntaxErrorException(
                    getErrorSyntaxMessageWithReason(variableName, variableValue.toString(), ex.getMessage()));
        }
    }

    private Object getValueByColumnName(String variableName, VariableLink variableLink,
            Map<String, TestData> testDataMap) throws SyntaxErrorException, IOException {
        Object object = new TestDataValueFinder(variableName, variableLink, testDataMap) {

            @Override
            protected int getColumnIndex(TestData testData) throws SyntaxErrorException, IOException {
                String columnName = variableLink.getValue();
                if (StringUtils.isEmpty(columnName)) {
                    throw new SyntaxErrorException(getErrorSyntaxMessageWithReason(variableName, StringUtils.EMPTY,
                            "Column name cannot be empty."));
                }
                return ArrayUtils.indexOf(testData.getColumnNames(), columnName) + TestData.BASE_INDEX;
            }

        }.getVariableValue();
        if (object instanceof String) {
            return "'" + object + "'";
        }
        return object.toString();
    }

    private Object getValueByColumnIndex(String variableName, VariableLink variableLink,
            Map<String, TestData> testDataMap) throws IOException, SyntaxErrorException {
        Object object = new TestDataValueFinder(variableName, variableLink, testDataMap) {

            @Override
            protected int getColumnIndex(TestData testData) throws SyntaxErrorException, IOException {
                String columnIndexAsString = variableLink.getValue();

                if (StringUtils.isEmpty(columnIndexAsString) || !isIntegerFormat(columnIndexAsString)) {
                    throw new SyntaxErrorException(getErrorSyntaxMessageWithReason(variableName, StringUtils.EMPTY,
                            "Variable value is not Integer format."));
                }
                return Integer.valueOf(columnIndexAsString);
            }

        }.getVariableValue();
        if (object instanceof String) {
            return "'" + object + "'";
        }
        return object.toString();
    }

    private boolean isIntegerFormat(String integerAsString) {
        return StringUtils.isNotEmpty(integerAsString) && StringUtils.isNumeric(integerAsString);
    }

    private String getErrorSyntaxMessageWithReason(String variableName, String variableValue, String reason) {
        return new StringBuilder("Wrong syntax at [Test case ID: ").append(testCaseExecutedEntity.getSourceId())
                .append(", Variable name: ")
                .append(variableName)
                .append(", Variable value: ")
                .append(getDisplayVariableValue(variableValue))
                .append("]. Reason: ")
                .append(reason)
                .toString();
    }

    private String getDisplayVariableValue(String variableValue) {
        String displayedVariableValue = variableValue;
        if (variableValue == null) {
            displayedVariableValue = "null";
        } else if (variableValue.isEmpty()) {
            displayedVariableValue = "<empty string>";
        }
        return displayedVariableValue;
    }

    private abstract class TestDataValueFinder {
        protected String variableName;

        protected VariableLink variableLink;

        private Map<String, TestData> testDataMap;

        public TestDataValueFinder(String variableName, VariableLink variableLink, Map<String, TestData> testDataMap) {
            this.variableName = variableName;
            this.variableLink = variableLink;
            this.testDataMap = testDataMap;
        }

        protected abstract int getColumnIndex(TestData testData) throws SyntaxErrorException, IOException;

        public Object getVariableValue() throws SyntaxErrorException, IOException {
            String testDataLinkId = variableLink.getTestDataLinkId();
            if (StringUtils.isEmpty(testDataLinkId)) {
                throw new SyntaxErrorException(getErrorSyntaxMessageWithReason(variableName, variableLink.getValue(),
                        "Test data value cannot be empty."));
            }
            TestDataExecutedEntity testDataExecutedEntity = testCaseExecutedEntity
                    .getTestDataExecuted(variableLink.getTestDataLinkId());
            TestData testData = testDataMap.get(testDataExecutedEntity.getTestDataId());
            int rowIndex = getRowIndex(testDataExecutedEntity);

            return testData.getObjectValue(getColumnIndex(testData), rowIndex);
        }
    }
}
