package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.ast.editors.InputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestObjectCellEditor;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.model.FailureHandling;

public abstract class AstAbstractKeywordTreeTableNode extends AstStatementTreeTableNode implements AstItemEditableNode,
        AstInputEditableNode, AstObjectEditableNode, AstOutputEditableNode {

    protected static final Image CONTINUE_ON_FAIL = ImageConstants.IMG_16_FAILED_CONTINUE;

    protected static final Image STOP_ON_FAIL = ImageConstants.IMG_16_FAILED_STOP;

    protected static final Image COMMENT_ICON = ImageConstants.IMG_16_COMMENT;

    protected static final Image OPTIONAL_ICON = ImageConstants.IMG_16_OPTIONAL_RUN;

    private static final String COMMENT_KW_NAME = "comment";

    protected MethodCallExpressionWrapper methodCall;

    protected ExpressionStatementWrapper parentStatement;

    protected BinaryExpressionWrapper binaryExpression;

    public AstAbstractKeywordTreeTableNode(ExpressionStatementWrapper methodCallStatement, AstTreeTableNode parentNode) {
        super(methodCallStatement, parentNode);
        if (methodCallStatement.getExpression() instanceof MethodCallExpressionWrapper) {
            this.methodCall = (MethodCallExpressionWrapper) methodCallStatement.getExpression();
        } else if (methodCallStatement.getExpression() instanceof BinaryExpressionWrapper) {
            this.binaryExpression = (BinaryExpressionWrapper) methodCallStatement.getExpression();
            this.methodCall = (MethodCallExpressionWrapper) binaryExpression.getRightExpression();
        }
        parentStatement = methodCallStatement;
    }

    @Override
    public boolean canEditItem() {
        return true;
    }

    @Override
    public String getItemText() {
        return TreeEntityUtil.getReadableKeywordName(getKeywordName());
    }

    @Override
    public String getItemTooltipText() {
        return getItemText();
    }

    public String getKeywordName() {
        return methodCall.getMethodAsString();
    }

    @Override
    public String getInputTooltipText() {
        return getInputText();
    }

    protected abstract int getObjectArgumentIndex();

    @Override
    public boolean canEditTestObject() {
        if (getObjectArgumentIndex() != -1) {
            return true;
        }
        return false;
    }

    protected ExpressionWrapper getTestObjectExpression() {
        int index = getObjectArgumentIndex();
        if (index == -1) {
            return null;
        }
        List<ExpressionWrapper> argumentList = ((ArgumentListExpressionWrapper) methodCall.getArguments())
                .getExpressions();
        return argumentList.get(index);
    }

    @Override
    public Object getTestObject() {
        return getTestObjectExpression();
    }

    @Override
    public String getTestObjectText() {
        if (!canEditTestObject()) {
            return "";
        }
        ExpressionWrapper expression = getTestObjectExpression();
        InputValueType inputValueType = AstTreeTableValueUtil.getTypeValue(expression);
        if (inputValueType != null) {
            return inputValueType.getValueToDisplay(expression);
        }
        return getTestObjectExpression().getText();
    }

    @Override
    public String getTestObjectTooltipText() {
        return getTestObjectText();
    }

    @Override
    public CellEditor getCellEditorForTestObject(Composite parent) {
        return new TestObjectCellEditor(parent, getTestObjectText(), true);
    }

    @Override
    public boolean setTestObject(Object object) {
        int index = getObjectArgumentIndex();
        if (index == -1 || !(object instanceof ExpressionWrapper)) {
            return false;
        }
        ((ArgumentListExpressionWrapper) methodCall.getArguments()).getExpressions().set(index,
                (ExpressionWrapper) object);
        return true;
    }

    @Override
    public CellEditor getCellEditorForInput(Composite parent) {
        return new InputCellEditor(parent, getInputText(), methodCall.getArguments());
    }

    @Override
    public String getOutputText() {
        return getOutput();
    }

    @Override
    public String getOutputTooltipText() {
        return getOutputText();
    }

    @Override
    public String getOutput() {
        if (binaryExpression != null && binaryExpression.getLeftExpression() != null) {
            return binaryExpression.getLeftExpression().getText();
        }
        return "";
    }

    @Override
    public CellEditor getCellEditorForOutput(Composite parent) {
        return new TextCellEditor(parent);
    }

    protected abstract Class<?> getOutputReturnType();

    @Override
    public boolean setOutput(Object output) {
        if (output == null) {
            return resetOutput();
        }
        if (!(output instanceof String)) {
            return false;
        }
        String outputString = (String) output;
        if (outputString.isEmpty()) {
            return resetOutput();
        }
        if (binaryExpression == null) {
            binaryExpression = new BinaryExpressionWrapper(parentStatement);
            VariableExpressionWrapper leftExpression = new VariableExpressionWrapper(outputString,
                    getOutputReturnType(), binaryExpression);
            binaryExpression.setLeftExpression(leftExpression);
            binaryExpression.setOperation(new TokenWrapper(GeneralUtils.ASSIGN, binaryExpression));
            binaryExpression.setRightExpression(methodCall);
            methodCall.setParent(binaryExpression);
            parentStatement.setExpression(binaryExpression);
            return true;
        }
        if (binaryExpression.getLeftExpression() instanceof VariableExpressionWrapper) {
            VariableExpressionWrapper variableExpressionWrapper = (VariableExpressionWrapper) binaryExpression
                    .getLeftExpression();
            if (variableExpressionWrapper.getVariable().equals(outputString)) {
                return false;
            }
            variableExpressionWrapper.setVariable(outputString);
            return true;
        }
        VariableExpressionWrapper leftExpression = new VariableExpressionWrapper(outputString, getOutputReturnType(),
                binaryExpression);
        if (!AstTreeTableValueUtil.compareAstNode(binaryExpression.getLeftExpression(), leftExpression)) {
            binaryExpression.setLeftExpression(leftExpression);
            return true;
        }
        return false;
    }

    protected boolean resetOutput() {
        if (binaryExpression == null) {
            return false;
        }
        binaryExpression = null;
        parentStatement.setExpression(methodCall);
        methodCall.setParent(parentStatement);
        return true;
    }

    protected PropertyExpressionWrapper getFailureHandlingPropertyExpression() {
        if (!(methodCall.getArguments() instanceof ArgumentListExpressionWrapper)) {
            return null;
        }
        for (ExpressionWrapper expression : ((ArgumentListExpressionWrapper) methodCall.getArguments())
                .getExpressions()) {
            if (!(expression instanceof PropertyExpressionWrapper)) {
                continue;
            }
            PropertyExpressionWrapper propertyExpression = (PropertyExpressionWrapper) expression;
            if (propertyExpression.isObjectExpressionOfClass(FailureHandling.class)) {
                return propertyExpression;
            }
        }
        return null;
    }

    public FailureHandling getFailureHandlingValue() {
        PropertyExpressionWrapper failureHandlingPropertyExpression = getFailureHandlingPropertyExpression();
        if (failureHandlingPropertyExpression != null) {
            return FailureHandling.valueOf(failureHandlingPropertyExpression.getPropertyAsString());
        }
        return null;
    }

    public boolean setFailureHandlingValue(FailureHandling failureHandling) {
        PropertyExpressionWrapper failureHandlingPropertyExpression = getFailureHandlingPropertyExpression();
        if (failureHandlingPropertyExpression == null
                || failureHandling.toString().equals(failureHandlingPropertyExpression.getPropertyAsString())) {
            return false;
        }
        failureHandlingPropertyExpression.setProperty(new ConstantExpressionWrapper(failureHandling.toString(),
                failureHandlingPropertyExpression));
        return true;
    }

    @Override
    public Image getIcon() {
        // If comment
        if (methodCall.getMethod() != null
                && COMMENT_KW_NAME.equals(methodCall.getMethodAsString())
                && methodCall.getObjectExpression() != null
                && KeywordController.getInstance().getBuiltInKeywordClassByName(
                        methodCall.getObjectExpressionAsString()) != null) {
            return COMMENT_ICON;
        }
        FailureHandling failureHandling = getFailureHandlingValue();
        if (failureHandling != null && failureHandling.equals(FailureHandling.STOP_ON_FAILURE)) {
            return STOP_ON_FAIL;
        } else if (failureHandling != null && failureHandling.equals(FailureHandling.OPTIONAL)) {
            return OPTIONAL_ICON;
        }
        return CONTINUE_ON_FAIL;
    }
}
