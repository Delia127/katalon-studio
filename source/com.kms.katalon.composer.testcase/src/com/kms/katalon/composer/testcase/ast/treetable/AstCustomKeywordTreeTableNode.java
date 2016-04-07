package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.MethodNode;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.editors.ComboBoxCellEditorWithContentProposal;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;

public class AstCustomKeywordTreeTableNode extends AstAbstractKeywordTreeTableNode {
    public AstCustomKeywordTreeTableNode(ExpressionStatementWrapper methodCallStatement, AstTreeTableNode parentNode) {
        super(methodCallStatement, parentNode);
    }

    private List<String> getKeywordNames() {
        List<String> keywordNames = new ArrayList<String>();
        for (MethodNode keywordMethodNode : getCustomKeywordMethods()) {
            keywordNames.add(keywordMethodNode.getName());
        }
        return keywordNames;
    }

    @Override
    public String getItemText() {
        return KeywordController.getInstance().getRawCustomKeywordName(methodCall.getMethodAsString());
    }

    @Override
    public Object getItem() {
        for (MethodNode keywordMethodNode : getCustomKeywordMethods()) {
            if (keywordMethodNode.getName().equals(getKeywordName())) {
                return getCustomKeywordMethods().indexOf(keywordMethodNode);
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
        List<MethodNode> customKeywordMethods = getCustomKeywordMethods();
        if (!(item instanceof Integer) || (int) item < 0 || (int) item >= customKeywordMethods.size()) {
            return false;
        }

        String newKeywordName = customKeywordMethods.get((int) item).getName();
        if (getKeywordName().equals(newKeywordName)) {
            return false;
        }
        if (methodCall.setMethod(newKeywordName)) {
            AstKeywordsInputUtil.generateCustomKeywordArguments(methodCall);
            if (!canEditOutput()) {
                removeOutput();
            }
            return true;
        }
        return false;
    }

    private MethodNode getMethodNode() {
        MethodNode keywordMethodNode = KeywordController.getInstance().getCustomKeywordByName(
                methodCall.getObjectExpressionAsString(), getItemText(),
                ProjectController.getInstance().getCurrentProject());
        return keywordMethodNode;
    }

    @Override
    public boolean canEditInput() {
        MethodNode keywordMethodNode = getMethodNode();
        if (keywordMethodNode == null) {
            return false;
        }
        int count = 0;
        for (int i = 0; i < keywordMethodNode.getParameters().length; i++) {
            if (!AstEntityInputUtil.isTestObjectClass(keywordMethodNode.getParameters()[i].getType())) {
                count++;
            }
        }
        return count > 0;
    }

    @Override
    public String getInputText() {
        ArgumentListExpressionWrapper arguments = methodCall.getArguments();
        if (arguments == null || arguments.getExpressions() == null || arguments.getExpressions().isEmpty()) {
            return "";
        }
        MethodNode keywordMethodNode = getMethodNode();
        if (keywordMethodNode == null) {
            return "";
        }
        return buildInputDisplayString(arguments, keywordMethodNode);
    }

    private String buildInputDisplayString(ArgumentListExpressionWrapper arguments, MethodNode keywordMethodNode) {
        StringBuilder displayString = new StringBuilder();
        int count = 0;
        for (int i = 0; i < keywordMethodNode.getParameters().length && i < arguments.getExpressions().size(); i++) {
            if (AstEntityInputUtil.isTestObjectClass(keywordMethodNode.getParameters()[i].getType())) {
                continue;
            }
            if (count > 0) {
                displayString.append("; ");
            }
            displayString.append(arguments.getExpression(i).getText());
            count++;
        }
        return displayString.toString();
    }

    @Override
    public Object getInput() {
        return AstKeywordsInputUtil.generateCustomKeywordInputParameters(getClassName(), getKeywordName(),
                methodCall.getArguments().clone());
    }

    @Override
    public boolean setInput(Object input) {
        if (!(input instanceof List<?>)) {
            return false;
        }
        List<?> inputParameters = (List<?>) input;
        ArgumentListExpressionWrapper argumentListExpression = new ArgumentListExpressionWrapper(methodCall);
        for (int i = 0; i < inputParameters.size(); i++) {
            InputParameter inputParameter = (InputParameter) inputParameters.get(i);
            argumentListExpression.addExpression(inputParameter.getValueAsExpression());
        }
        return methodCall.setArguments(argumentListExpression);
    }

    @Override
    protected int getObjectArgumentIndex() {
        MethodNode keywordMethodNode = getMethodNode();
        if (keywordMethodNode == null) {
            return -1;
        }
        for (int i = 0; i < keywordMethodNode.getParameters().length; i++) {
            if (AstEntityInputUtil.isTestObjectClass(keywordMethodNode.getParameters()[i].getType())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean canEditOutput() {
        MethodNode keywordMethodNode = getMethodNode();
        return isOutputNotVoid(keywordMethodNode);
    }

    private boolean isOutputNotVoid(MethodNode keywordMethodNode) {
        return keywordMethodNode != null && !AstKeywordsInputUtil.isVoidClass(keywordMethodNode.getReturnType());
    }

    private String getClassName() {
        return methodCall.getObjectExpressionAsString();
    }

    @Override
    protected Class<?> getOutputReturnType() {
        MethodNode keywordMethodNode = getMethodNode();
        if (isOutputNotVoid(keywordMethodNode)) {
            return keywordMethodNode.getReturnType().getTypeClass();
        }
        return null;
    }

    private List<MethodNode> getCustomKeywordMethods() {
        return KeywordController.getInstance().getCustomKeywords(ProjectController.getInstance().getCurrentProject());
    }
}
