package com.kms.katalon.composer.testcase.ast.treetable;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.editors.CallTestCaseCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.keyword.KeywordParameter;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class AstCallTestCaseKeywordTreeTableNode extends AstBuiltInKeywordTreeTableNode {
    private String testCasePk = StringUtils.EMPTY;

    public AstCallTestCaseKeywordTreeTableNode(ExpressionStatementWrapper methodCallStatement,
            AstTreeTableNode parentNode) {
        super(methodCallStatement, parentNode);
        internallySetTestCasePk();
    }

    @Override
    public boolean canEditItem() {
        return false;
    }

    private void internallySetTestCasePk() {
        ArgumentListExpressionWrapper arguments = (ArgumentListExpressionWrapper) methodCall.getArguments();
        if (!(arguments.getExpression(0) instanceof MethodCallExpressionWrapper)) {
            return;
        }
        ExpressionWrapper objectExpressionWrapper = AstEntityInputUtil.getCallTestCaseParam((MethodCallExpressionWrapper) arguments.getExpression(0));
        if (objectExpressionWrapper == null) {
            return;
        }
        TestCaseEntity testCase = null;
        try {
            testCase = TestCaseController.getInstance().getTestCaseByDisplayId(
                    (objectExpressionWrapper instanceof ConstantExpressionWrapper)
                            ? String.valueOf(((ConstantExpressionWrapper) objectExpressionWrapper).getValue())
                            : objectExpressionWrapper.getText());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (testCase == null) {
            return;
        }
        testCasePk = testCase.getIdForDisplay();
    }

    protected void changeMapExpressionWrapper(MapExpressionWrapper mapExprs) {
        ArgumentListExpressionWrapper arguments = (ArgumentListExpressionWrapper) methodCall.getArguments();
        arguments.getExpressions().remove(1);
        arguments.getExpressions().add(1, mapExprs);
    }

    protected void changeTestCasePk(TestCaseEntity testCase) {
        MethodCallExpressionWrapper testCaseMethodCallEprs = AstEntityInputUtil.getNewCallTestCaseExpression(testCase,
                methodCall.getArguments());
        ArgumentListExpressionWrapper arguments = (ArgumentListExpressionWrapper) methodCall.getArguments();
        arguments.getExpressions().remove(0);
        arguments.getExpressions().add(0, testCaseMethodCallEprs);
        internallySetTestCasePk();
    }

    @Override
    public String getInputText() {
        ArgumentListExpressionWrapper arguments = (ArgumentListExpressionWrapper) methodCall.getArguments();
        if (arguments == null || arguments.getExpressions().size() == 0) {
            return "";
        }
        KeywordMethod keywordMethod = null;
        try {
            keywordMethod = BuiltInMethodNodeFactory.findCallTestCaseMethod(getBuiltInKWClassSimpleName());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        if (keywordMethod == null) {
            return "";
        }
        int count = 0;
        StringBuilder displayString = new StringBuilder();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter keywordParam = keywordMethod.getParameters()[i];
            if (TestObject.class.isAssignableFrom(keywordParam.getType())
                    || FailureHandling.class.isAssignableFrom(keywordParam.getType())
                    || TestCase.class.isAssignableFrom(keywordParam.getType())) {
                continue;
            }
            if (count > 0) {
                displayString.append("; ");
            }
            ExpressionWrapper inputExpression = arguments.getExpression(i);
            InputValueType typeValue = AstTreeTableValueUtil.getTypeValue(inputExpression);
            if (typeValue != null) {
                displayString.append(typeValue.getValueToDisplay(inputExpression));
            } else {
                displayString.append(inputExpression.getText());
            }
            count++;
        }
        return displayString.toString();
    }

    @Override
    public boolean canEditInput() {
        return true;
    }

    @Override
    public Object getInput() {
        ArgumentListExpressionWrapper argumentList = (ArgumentListExpressionWrapper) methodCall.getArguments();
        if (argumentList == null) {
            return null;
        }
        try {
            return AstTreeTableInputUtil.generateBuiltInKeywordInputParameters(
                    BuiltInMethodNodeFactory.findCallTestCaseMethod(getBuiltInKWClassSimpleName()), argumentList);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    @Override
    public boolean setInput(Object input) {
        if (!(input instanceof List<?>)) {
            return false;
        }
        List<?> inputParameters = (List<?>) input;
        KeywordMethod keywordMethod = null;
        try {
            keywordMethod = BuiltInMethodNodeFactory.findCallTestCaseMethod(getBuiltInKWClassSimpleName());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        if (keywordMethod == null) {
            return false;
        }
        ArgumentListExpressionWrapper argumentListExpression = new ArgumentListExpressionWrapper(methodCall);
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            argumentListExpression.addExpression(AstTreeTableInputUtil.getArgumentExpression(
                    (InputParameter) inputParameters.get(i), parentStatement));

        }
        if (!AstTreeTableValueUtil.compareAstNode(argumentListExpression, methodCall.getArguments())) {
            methodCall.setArguments(argumentListExpression);
            return true;
        }
        return false;
    }

    public List<VariableEntity> getCallTestCaseVariables() {
        return TestCaseTreeTableInput.getCallTestCaseVariables((ArgumentListExpressionWrapper) methodCall.getArguments());
    }

    @Override
    public boolean canEditOutput() {
        return false;
    }

    @Override
    public boolean canEditTestObject() {
        return true;
    }

    @Override
    public String getTestObjectText() {
        TestCaseEntity testCase = null;
        try {
            testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCasePk);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (testCase != null) {
            return testCase.getName();
        }
        return "";
    }

    @Override
    public CellEditor getCellEditorForTestObject(Composite parent) {
        return new CallTestCaseCellEditor(parent, getTestObjectText(), testCasePk);
    }

    @Override
    public boolean setTestObject(Object object) {
        try {
            if (!(object instanceof TestCaseTreeEntity)
                    || !(((TestCaseTreeEntity) object).getObject() instanceof TestCaseEntity)) {
                return false;
            }
            TestCaseEntity newTestCase = (TestCaseEntity) ((TestCaseTreeEntity) object).getObject();
            if (!verifyCallTestCase(newTestCase) || testCasePk.equals(newTestCase.getIdForDisplay())) {
                return false;
            }
            changeTestCasePk(newTestCase);
            changeMapExpressionWrapper(AstEntityInputUtil.generateTestCaseVariableBindingExpression(newTestCase,
                    methodCall));
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    private boolean verifyCallTestCase(TestCaseEntity newTestCase) {
        if (newTestCase == null) {
            return false;
        }
        // Statement doesn't have link to parent script or test case, so cannot verify
        if (statement.getScriptClass() == null || statement.getScriptClass().getTestCaseId() == null) {
            return true;
        }
        if (StringUtils.equals(newTestCase.getRelativePathForUI(), statement.getScriptClass().getTestCaseId())) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_TEST_CASE_CANNOT_CALL_ITSELF);
            return false;
        }

        return true;
    }

    @Override
    public Image getIcon() {
        return ImageConstants.IMG_16_CALL_TEST_CASE;
    }
}
