package com.kms.katalon.composer.testcase.ast.treetable;

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
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;
import com.kms.katalon.custom.keyword.KeywordMethod;
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
        String testCaseId = AstEntityInputUtil.findTestCaseIdArgumentFromCallTestCaseMethodCall(methodCall);
        if (testCaseId == null) {
            return;
        }
        TestCaseEntity testCase = null;
        try {
            testCase = TestCaseController.getInstance().getTestCaseByDisplayId(TestCaseFactory.getTestCaseId(testCaseId));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (testCase == null) {
            return;
        }
        testCasePk = testCase.getIdForDisplay();
    }

    private void changeMapExpressionWrapper(MapExpressionWrapper mapExprs) {
        methodCall.getArguments().setExpression(mapExprs, 1);
    }

    private void changeTestCasePk(TestCaseEntity testCase) {
        ArgumentListExpressionWrapper arguments = methodCall.getArguments();
        MethodCallExpressionWrapper testCaseMethodCallEprs = AstEntityInputUtil.createNewFindTestCaseMethodCall(testCase,
                arguments);
        arguments.setExpression(testCaseMethodCallEprs, 0);
        internallySetTestCasePk();
    }

    @Override
    public String getInputText() {
        ArgumentListExpressionWrapper arguments = methodCall.getArguments();
        if (arguments == null || arguments.getExpressions().size() == 0) {
            return "";
        }
        KeywordMethod keywordMethod = BuiltInMethodNodeFactory.findCallTestCaseMethod(getBuiltInKWClassSimpleName());
        if (keywordMethod == null) {
            return "";
        }
        return buildInputDisplayString(arguments, keywordMethod);
    }
    
    protected boolean isIgnoreParamType(Class<?> paramType) {
        return super.isIgnoreParamType(paramType) || AstEntityInputUtil.isTestCaseClass(paramType) ;
    }

    @Override
    public boolean canEditInput() {
        return true;
    }

    @Override
    protected List<InputParameter> getInputParameters() {
        ArgumentListExpressionWrapper argumentList = (ArgumentListExpressionWrapper) methodCall.getArguments();
        if (argumentList == null) {
            return null;
        }
        return AstKeywordsInputUtil.generateInputParameters(findKeywordMethod(), argumentList);
    }

    @Override
    protected KeywordMethod findKeywordMethod() {
        return BuiltInMethodNodeFactory.findCallTestCaseMethod(getBuiltInKWClassSimpleName());
    }

    public List<VariableEntity> getCallTestCaseVariables() {
        return AstEntityInputUtil.getCallTestCaseVariables(methodCall);
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
        if (!(object instanceof TestCaseTreeEntity)) {
            return false;
        }
        TestCaseEntity newTestCase;
        try {
            newTestCase = ((TestCaseTreeEntity) object).getObject();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        }
        if (!verifyCallTestCase(newTestCase)) {
            return false;
        }
        changeTestCasePk(newTestCase);
        changeMapExpressionWrapper(AstEntityInputUtil.generateTestCaseVariableBindingMapExpression(newTestCase,
                methodCall));
        return true;
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
