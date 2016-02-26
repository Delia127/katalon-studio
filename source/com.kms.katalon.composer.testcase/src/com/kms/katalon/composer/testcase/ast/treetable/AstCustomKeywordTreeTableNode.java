package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.MethodNode;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.editors.ComboBoxCellEditorWithContentProposal;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;

public class AstCustomKeywordTreeTableNode extends AstAbstractKeywordTreeTableNode {
    private List<MethodNode> customKeywordMethods;

    public AstCustomKeywordTreeTableNode(ExpressionStatementWrapper methodCallStatement, AstTreeTableNode parentNode) {
        super(methodCallStatement, parentNode);
        if (methodCall.getMethod() instanceof ConstantExpressionWrapper) {
            methodCall.setMethod(KeywordController.getInstance().getCustomKeywordName(methodCall.getMethodAsString()));
        }
        try {
            AstTreeTableInputUtil.generateCustomKeywordArguments(methodCall);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        try {
            customKeywordMethods = KeywordController.getInstance().getCustomKeywords(
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private List<String> getKeywordNames() {
        List<String> keywordNames = new ArrayList<String>();
        try {
            for (MethodNode keywordMethodNode : customKeywordMethods) {
                keywordNames.add(keywordMethodNode.getName());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return keywordNames;
    }

    @Override
    public String getItemText() {
        return KeywordController.getInstance().getRawCustomKeywordName(methodCall.getMethodAsString());
    }

    @Override
    public Object getItem() {
        for (MethodNode keywordMethodNode : customKeywordMethods) {
            if (keywordMethodNode.getName().equals(getKeywordName())) {
                return customKeywordMethods.indexOf(keywordMethodNode);
            }
        }
        return 0;
    }

    @Override
    public CellEditor getCellEditorForItem(Composite parent) {
        List<String> keywordNames = getKeywordNames();
        return new ComboBoxCellEditorWithContentProposal(parent, keywordNames.toArray(new String[keywordNames.size()]),
                keywordNames.toArray(new String[keywordNames.size()]));
    }

    @Override
    public boolean setItem(Object item) {
        if (!(item instanceof Integer) || (int) item < 0 || (int) item >= customKeywordMethods.size()
                || getKeywordName().equals(customKeywordMethods.get((int) item).getName())) {
            return false;
        }
        methodCall.setMethod(customKeywordMethods.get((int) item).getName());
        try {
            AstTreeTableInputUtil.generateCustomKeywordArguments(methodCall);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (!canEditOutput()) {
            setOutput(null);
        }
        return true;
    }

    @Override
    public boolean canEditInput() {
        MethodNode keywordMethodNode = null;
        try {
            keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                    methodCall.getObjectExpressionAsString(), getItemText(),
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (keywordMethodNode == null) {
            return false;
        }
        int count = 0;
        for (int i = 0; i < keywordMethodNode.getParameters().length; i++) {
            if (!AstEntityInputUtil.isObjectClass(keywordMethodNode.getParameters()[i].getType())) {
                count++;
            }
        }
        return count > 0;
    }

    @Override
    public String getInputText() {
        ArgumentListExpressionWrapper arguments = (ArgumentListExpressionWrapper) methodCall.getArguments();
        if (arguments == null || arguments.getExpressions() == null || arguments.getExpressions().isEmpty()) {
            return "";
        }
        MethodNode keywordMethodNode = null;
        try {
            keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                    methodCall.getObjectExpressionAsString(), getItemText(),
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (keywordMethodNode == null) {
            return "";
        }
        StringBuilder displayString = new StringBuilder();
        int count = 0;
        for (int i = 0; i < keywordMethodNode.getParameters().length && i < arguments.getExpressions().size(); i++) {
            if (AstEntityInputUtil.isObjectClass(keywordMethodNode.getParameters()[i].getType())) {
                continue;
            }
            if (count > 0) {
                displayString.append("; ");
            }
            ExpressionWrapper inputExpressionWrapper = arguments.getExpression(i);
            displayString.append(inputExpressionWrapper.getText());
            count++;
        }
        return displayString.toString();
    }

    @Override
    public Object getInput() {
        ArgumentListExpressionWrapper argumentList = (ArgumentListExpressionWrapper) methodCall.getArguments();
        if (argumentList == null) {
            return null;
        }
        try {
            return AstTreeTableInputUtil.generateCustomKeywordInputParameters(getClassName(), getKeywordName(),
                    argumentList.clone());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    @Override
    public boolean setInput(Object input) {
        if (!(input instanceof List<?>)) {
            return false;
        }
        MethodNode keywordMethodNode = null;
        try {
            keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                    methodCall.getObjectExpressionAsString(), getItemText(),
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (keywordMethodNode == null) {
            return false;
        }
        List<?> inputParameters = (List<?>) input;
        ArgumentListExpressionWrapper argumentListExpressionWrapper = new ArgumentListExpressionWrapper(methodCall);
        for (int i = 0; i < inputParameters.size(); i++) {
            argumentListExpressionWrapper.addExpression(AstTreeTableInputUtil.getArgumentExpression(
                    (InputParameter) inputParameters.get(i), argumentListExpressionWrapper));
        }
        methodCall.setArguments(argumentListExpressionWrapper);
        return true;
    }

    @Override
    protected int getObjectArgumentIndex() {
        MethodNode keywordMethodNode = null;
        try {
            keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                    methodCall.getObjectExpressionAsString(), getItemText(),
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (keywordMethodNode == null) {
            return -1;
        }
        for (int i = 0; i < keywordMethodNode.getParameters().length; i++) {
            if (AstEntityInputUtil.isObjectClass(keywordMethodNode.getParameters()[i].getType())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean canEditOutput() {
        MethodNode keywordMethodNode = null;
        try {
            keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                    methodCall.getObjectExpressionAsString(), getItemText(),
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return (keywordMethodNode != null && !AstTreeTableInputUtil.isVoidClass(keywordMethodNode.getReturnType()));
    }

    private String getClassName() {
        return methodCall.getObjectExpressionAsString();
    }

    @Override
    protected Class<?> getOutputReturnType() {
        MethodNode keywordMethodNode = null;
        try {
            keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                    methodCall.getObjectExpressionAsString(), getItemText(),
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (keywordMethodNode != null && !AstTreeTableInputUtil.isVoidClass(keywordMethodNode.getReturnType())) {
            return keywordMethodNode.getReturnType().getTypeClass();
        }
        return null;
    }
}
