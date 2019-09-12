package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.ast.editors.InputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestObjectCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.WindowsTestObjectCellEditor;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputParameterBuilder;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.execution.setting.TestCaseSettingStore;

public abstract class AstAbstractKeywordTreeTableNode extends AstInputEditableStatementTreeTableNode
        implements IAstItemEditableNode, IAstObjectEditableNode, IAstOutputEditableNode {
    private static final String COMMENT_KW_NAME = "comment";

    protected MethodCallExpressionWrapper methodCall;

    protected ExpressionStatementWrapper parentStatement;

    protected BinaryExpressionWrapper binaryExpression;

    private ITestCasePart testCasePart;

    public AstAbstractKeywordTreeTableNode(ExpressionStatementWrapper methodCallStatement,
            AstTreeTableNode parentNode) {
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
    public Object getItem() {
        return methodCall;
    }

    @Override
    public boolean setItem(Object item) {
        if (!(item instanceof MethodCallExpressionWrapper)) {
            return false;
        }
        MethodCallExpressionWrapper newMethodCall = (MethodCallExpressionWrapper) item;
        if (StringUtils.equals(newMethodCall.getMethodAsString(), getKeywordName())) {
            return false;
        }
        methodCall.setMethod(newMethodCall.getMethodAsString());
        methodCall.setArguments(newMethodCall.getArguments());
        if (!canEditOutput()) {
            removeOutput();
        }
        return true;
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
        return methodCall.getArguments().getExpression(index);
    }

    @Override
    public Object getTestObject() {
        return getTestObjectExpression();
    }

    @Override
    public String getTestObjectText() {
        ExpressionWrapper expression = getTestObjectExpression();
        if (expression == null) {
            return StringUtils.EMPTY;
        }
        InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
        if (inputValueType != null) {
            return inputValueType.getValueToDisplay(expression);
        }
        return getTestObjectExpression().getText();
    }

    @Override
    public String getTestObjectTooltipText() {
        ExpressionWrapper expression = getTestObjectExpression();
        if (expression == null) {
            return StringUtils.EMPTY;
        }
        InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
        if (inputValueType != null) {
            return getTestObjectTooltipText(expression);
        }
        return getTestObjectExpression().getText();
    }

    @Override
    public CellEditor getCellEditorForTestObject(Composite parent) {
        InputValueType inputValueType = AstValueUtil.getTypeValue(getTestObjectExpression());
        switch (inputValueType) {
            case TestObject: {
                TestObjectCellEditor cellEditor = new TestObjectCellEditor(parent, getTestObjectText(), true);
                cellEditor.setTestCasePart(getTestCasePart());
                return cellEditor;
            }
            case WindowsObject: {
                WindowsTestObjectCellEditor cellEditor = new WindowsTestObjectCellEditor(parent, getTestObjectText(),
                        true);
                cellEditor.setTestCasePart(getTestCasePart());
                return cellEditor;
            }
            default:
                return null;
        }
    }

    @Override
    public boolean setTestObject(Object object) {
        int index = getObjectArgumentIndex();
        if (index == -1 || !(object instanceof ExpressionWrapper)) {
            return false;
        }
        methodCall.getArguments().setExpression((ExpressionWrapper) object, index);
        return true;
    }

    @Override
    public CellEditor getCellEditorForInput(Composite parent) {
        return new InputCellEditor(parent, getInputText(), methodCall.getArguments());
    }

    @Override
    public final Object getInput() {
        return InputParameterBuilder.createForMethodCall(getInputParameters());
    }

    protected abstract List<InputParameter> getInputParameters();

    @Override
    public final boolean setInput(Object input) {
        if (input instanceof InputParameterBuilder) {
            setInputParameters(((InputParameterBuilder) input).getOriginalParameters());
            return true;
        }
        return false;
    }

    protected boolean setInputParameters(List<InputParameter> originalParameters) {
        ArgumentListExpressionWrapper argumentListExpression = new ArgumentListExpressionWrapper(methodCall);
        for (InputParameter input : originalParameters) {
            argumentListExpression.addExpression(input.getValueAsExpression());
        }
        return methodCall.setArguments(argumentListExpression);
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
            return removeOutput();
        }
        if (!(output instanceof String)) {
            return false;
        }
        String outputString = (String) output;
        if (outputString.isEmpty()) {
            return removeOutput();
        }
        if (binaryExpression == null) {
            return createNewOuput(outputString);
        }
        return changeExistingOutput(outputString);
    }

    @Override
    public void setTestCasePart(ITestCasePart testCasePart) {
        this.testCasePart = testCasePart;
    }

    @Override
    public ITestCasePart getTestCasePart() {
        return testCasePart;
    }

    private boolean changeExistingOutput(String outputString) {
        if (binaryExpression.getLeftExpression() instanceof VariableExpressionWrapper) {
            VariableExpressionWrapper variableExpressionWrapper = (VariableExpressionWrapper) binaryExpression
                    .getLeftExpression();
            if (variableExpressionWrapper.getVariable().equals(outputString)) {
                return false;
            }
            variableExpressionWrapper.setVariable(outputString);
            return true;
        }
        VariableExpressionWrapper newLeftExpression = new VariableExpressionWrapper(outputString, getOutputReturnType(),
                binaryExpression);
        if (!newLeftExpression.isEqualsTo(binaryExpression.getLeftExpression())) {
            binaryExpression.setLeftExpression(newLeftExpression);
            return true;
        }
        return false;
    }

    private boolean createNewOuput(String outputString) {
        binaryExpression = new BinaryExpressionWrapper(parentStatement);
        VariableExpressionWrapper leftExpression = new VariableExpressionWrapper(outputString, getOutputReturnType(),
                binaryExpression);
        binaryExpression.setLeftExpression(leftExpression);
        binaryExpression.setOperation(new TokenWrapper(GeneralUtils.ASSIGN, binaryExpression));
        binaryExpression.setRightExpression(methodCall);
        methodCall.setParent(binaryExpression);
        parentStatement.setExpression(binaryExpression);
        return true;
    }

    protected boolean removeOutput() {
        if (binaryExpression == null) {
            return false;
        }
        binaryExpression = null;
        parentStatement.setExpression(methodCall);
        methodCall.setParent(parentStatement);
        return true;
    }

    protected PropertyExpressionWrapper getFailureHandlingPropertyExpression() {
        for (ExpressionWrapper expression : methodCall.getArguments().getExpressions()) {
            if (!(expression instanceof PropertyExpressionWrapper)
                    || !(((PropertyExpressionWrapper) expression).isObjectExpressionOfClass(FailureHandling.class))) {
                continue;
            }
            return (PropertyExpressionWrapper) expression;
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
        if (failureHandlingPropertyExpression == null) {
            failureHandlingPropertyExpression = new PropertyExpressionWrapper(FailureHandling.class.getSimpleName(),
                    failureHandling.name(), methodCall.getArguments());
            methodCall.getArguments().addExpression(failureHandlingPropertyExpression);
            return true;
        }
        if (StringUtils.equals(failureHandling.toString(), failureHandlingPropertyExpression.getPropertyAsString())) {
            return false;
        }
        failureHandlingPropertyExpression.setProperty(
                new ConstantExpressionWrapper(failureHandling.toString(), failureHandlingPropertyExpression));
        return true;
    }

    @Override
    public Image getIcon() {
        if (isComment()) {
            return ImageConstants.IMG_16_COMMENT;
        }

        FailureHandling failureHandling = getFailureHandlingValue();
        if (failureHandling == null) {
            failureHandling = new TestCaseSettingStore(
                    ProjectController.getInstance().getCurrentProject().getFolderLocation())
                            .getDefaultFailureHandling();
        }

        switch (failureHandling) {
            case OPTIONAL:
                return ImageConstants.IMG_16_OPTIONAL_RUN;
            case STOP_ON_FAILURE:
                return ImageConstants.IMG_16_FAILED_STOP;
            default:
                return ImageConstants.IMG_16_FAILED_CONTINUE;
        }
    }

    private boolean isComment() {
        return methodCall.getMethod() != null && COMMENT_KW_NAME.equals(methodCall.getMethodAsString())
                && methodCall.getObjectExpression() != null && KeywordController.getInstance()
                        .getBuiltInKeywordClassByName(methodCall.getObjectExpressionAsString()) != null;
    }

    public String getTestObjectTooltipText(ASTNodeWrapper astObject) {
        if (astObject instanceof MethodCallExpressionWrapper) {
            return getTooltipForTestObjectArgument((MethodCallExpressionWrapper) astObject);
        }
        return ((ASTNodeWrapper) astObject).getText();
    }

    public String getTooltipForTestObjectArgument(MethodCallExpressionWrapper methodCall) {
        return AstEntityInputUtil.getEntityRelativeIdFromMethodCall(methodCall);
    }
}
