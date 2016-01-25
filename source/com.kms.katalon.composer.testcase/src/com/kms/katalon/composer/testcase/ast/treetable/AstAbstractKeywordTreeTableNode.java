package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.editors.InputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.TestObjectCellEditor;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.editors.ComboBoxCellEditorWithContentProposal;
import com.kms.katalon.composer.testcase.model.ContentProposalCheck;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.model.FailureHandling;

public abstract class AstAbstractKeywordTreeTableNode extends AstStatementTreeTableNode {

    protected static final Image CONTINUE_ON_FAIL = ImageConstants.IMG_16_FAILED_CONTINUE;

    protected static final Image STOP_ON_FAIL = ImageConstants.IMG_16_FAILED_STOP;

    protected static final Image COMMENT_ICON = ImageConstants.IMG_16_COMMENT;

    protected static final Image OPTIONAL_ICON = ImageConstants.IMG_16_OPTIONAL_RUN;

    private static final String COMMENT_KW_NAME = "comment";

    protected MethodCallExpression methodCall;

    protected ExpressionStatement parentStatement;

    protected BinaryExpression binaryExpression;

    public AstAbstractKeywordTreeTableNode(ExpressionStatement methodCallStatement, AstTreeTableNode parentNode,
            ASTNode parentObject, ClassNode scriptClass) {
        super(methodCallStatement, parentNode, parentObject, scriptClass);
        if (methodCallStatement.getExpression() instanceof MethodCallExpression) {
            this.methodCall = (MethodCallExpression) methodCallStatement.getExpression();
        } else if (methodCallStatement.getExpression() instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) methodCallStatement.getExpression();
            if (binaryExpression.getRightExpression() instanceof MethodCallExpression) {
                this.methodCall = (MethodCallExpression) binaryExpression.getRightExpression();
            }
            this.binaryExpression = binaryExpression;
        }
        parentStatement = methodCallStatement;
    }

    @Override
    public boolean isItemEditable() {
        return true;
    }

    @Override
    public String getItemText() {
        return methodCall.getMethod().getText();
    }

    @Override
    public String getItemTextForDisplay() {
        return TreeEntityUtil.getReadableKeywordName(getItemText());
    }

    @Override
    public String getItemTooltipText() {
        return getItemTextForDisplay();
    }

    @Override
    public CellEditor getCellEditorForItem(Composite parent) {
        List<String> keywordNames = getKeywordNames();
        List<String> toolTips = getKeywordToolTips();
        final ContentProposalCheck contentProposalCheck = new ContentProposalCheck();
        final ComboBoxCellEditorWithContentProposal cellEditor = new ComboBoxCellEditorWithContentProposal(parent,
                keywordNames.toArray(new String[keywordNames.size()]), toolTips.toArray(new String[toolTips.size()]),
                contentProposalCheck);

        return cellEditor;
    }

    protected abstract List<String> getKeywordNames();

    protected abstract List<String> getKeywordToolTips();

    public String getKeyword() {
        return getItemText();
    }

    protected void setKeyword(String keyword) {
        methodCall.setMethod(new ConstantExpression(keyword));
        generateArguments();
        if (!isOutputEditatble()) {
            setOutput(null);
        }
    }

    public abstract void generateArguments();

    public void setArguments(ArgumentListExpression argumentListExpression) {
        methodCall.setArguments(argumentListExpression);
    }

    protected abstract int getObjectArgumentIndex() throws Exception;

    @Override
    public boolean isTestObjectEditable() {
        try {
            if (getObjectArgumentIndex() != -1) {
                return true;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    protected Expression getTestObjectExpression() {
        try {
            int index = getObjectArgumentIndex();
            if (index != -1) {
                List<Expression> argumentList = ((ArgumentListExpression) methodCall.getArguments()).getExpressions();
                return argumentList.get(index);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    @Override
    public Object getTestObject() {
        return getTestObjectExpression();
    }

    @Override
    public String getTestObjectText() {
        try {
            if (getObjectArgumentIndex() != -1) {
                return AstTreeTableTextValueUtil.getInstance().getTextValue(getTestObjectExpression());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return "";
    }

    @Override
    public CellEditor getCellEditorForTestObject(Composite parent) {
        return new TestObjectCellEditor(parent, getTestObjectText(), scriptClass, true);
    }

    @Override
    public boolean setTestObject(Object object) {
        try {
            if (object instanceof Expression) {
                int index = getObjectArgumentIndex();
                if (index != -1) {
                    ((ArgumentListExpression) methodCall.getArguments()).getExpressions().set(index,
                            (Expression) object);
                    return true;
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Override
    public CellEditor getCellEditorForInput(Composite parent) {
        return new InputCellEditor(parent, getInputText(), scriptClass);
    }

    @Override
    public String getOutputText() {
        return getOutput();
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

    protected abstract VariableExpression createNewOutput(String output) throws Exception;

    @Override
    public boolean setOutput(Object output) {
        if (output == null) {
            return resetOutput();
        } else if (output instanceof String) {
            String outputString = (String) output;
            if (outputString.isEmpty()) {
                return resetOutput();
            }
            if (binaryExpression != null) {
                Expression newExpression = null;
                if (binaryExpression.getLeftExpression() instanceof VariableExpression) {
                    newExpression = new VariableExpression(outputString,
                            ((VariableExpression) binaryExpression.getLeftExpression()).getType());
                } else {
                    try {
                        newExpression = createNewOutput(outputString);
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                }
                if (newExpression != null
                        && !AstTreeTableValueUtil.compareAstNode(binaryExpression.getLeftExpression(), newExpression)) {
                    binaryExpression.setLeftExpression(newExpression);
                    return true;
                }
            } else {
                try {
                    binaryExpression = new BinaryExpression(createNewOutput(outputString), GeneralUtils.ASSIGN,
                            methodCall);
                    parentStatement.setExpression(binaryExpression);
                    return true;
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }
        }
        return false;
    }

    protected boolean resetOutput() {
        if (binaryExpression != null) {
            binaryExpression = null;
            parentStatement.setExpression(methodCall);
            return true;
        }
        return false;
    }

    protected PropertyExpression getFailureHandlingPropertyExpression() {
        if (methodCall.getArguments() instanceof ArgumentListExpression) {
            for (Expression expression : ((ArgumentListExpression) methodCall.getArguments()).getExpressions()) {
                if (expression instanceof PropertyExpression) {
                    PropertyExpression propertyExpression = (PropertyExpression) expression;
                    if (propertyExpression.getObjectExpression().getText().equals(FailureHandling.class.getName())
                            || propertyExpression.getObjectExpression().getText()
                                    .equals(FailureHandling.class.getSimpleName())) {
                        return propertyExpression;
                    }
                }
            }
        }
        return null;
    }

    public FailureHandling getFailureHandlingValue() {
        PropertyExpression failureHandlingPropertyExpression = getFailureHandlingPropertyExpression();
        if (failureHandlingPropertyExpression != null) {
            return FailureHandling.valueOf(failureHandlingPropertyExpression.getProperty().getText());
        }
        return null;
    }

    public boolean setFailureHandlingValue(FailureHandling failureHandling) {
        PropertyExpression failureHandlingPropertyExpression = getFailureHandlingPropertyExpression();
        if (failureHandlingPropertyExpression != null && methodCall.getArguments() instanceof ArgumentListExpression) {
            ArgumentListExpression argumentList = (ArgumentListExpression) methodCall.getArguments();
            int index = argumentList.getExpressions().indexOf(failureHandlingPropertyExpression);
            if (index >= 0 && index < argumentList.getExpressions().size()) {
                argumentList.getExpressions().set(
                        index,
                        new PropertyExpression(failureHandlingPropertyExpression.getObjectExpression(),
                                new ConstantExpression(failureHandling.toString())));
                return true;
            }
        }
        return false;
    }

    @Override
    public Image getNodeIcon() {
        // If comment
        if (methodCall.getMethod() != null
                && COMMENT_KW_NAME.equals(methodCall.getMethod().getText())
                && methodCall.getObjectExpression() != null
                && KeywordController.getInstance().getBuiltInKeywordClass(methodCall.getObjectExpression().getText()) != null) {
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

    @Override
    public boolean hasChildren() {
        return false;
    }
}
