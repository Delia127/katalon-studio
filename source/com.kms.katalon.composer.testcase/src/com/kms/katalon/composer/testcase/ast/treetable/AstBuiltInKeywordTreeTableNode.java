package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.editors.ComboBoxCellEditorWithContentProposal;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.keyword.KeywordParameter;

public class AstBuiltInKeywordTreeTableNode extends AstAbstractKeywordTreeTableNode {
    public AstBuiltInKeywordTreeTableNode(ExpressionStatementWrapper methodCallStatement, AstTreeTableNode parentNode) {
        super(methodCallStatement, parentNode);
    }

    protected String getBuiltInKWClassSimpleName() {
        return methodCall.getObjectExpressionAsString();
    }

    private List<String> getKeywordNames() {
        List<String> keywordNames = new ArrayList<String>();
        for (KeywordMethod keywordMethod : getBuiltInKeywordMethods()) {
            keywordNames.add(TreeEntityUtil.getReadableKeywordName(keywordMethod.getName()));
        }
        return keywordNames;
    }

    private List<String> getKeywordToolTips() {
        return TestCaseEntityUtil.getAllKeywordJavaDocText(getBuiltInKWClassSimpleName());
    }

    @Override
    public CellEditor getCellEditorForItem(Composite parent) {
        List<String> keywordNames = getKeywordNames();
        List<String> toolTips = getKeywordToolTips();
        return new ComboBoxCellEditorWithContentProposal(parent, keywordNames.toArray(new String[keywordNames.size()]),
                toolTips.toArray(new String[toolTips.size()]));
    }

    @Override
    public Object getItem() {
        for (KeywordMethod keywordMethod : getBuiltInKeywordMethods()) {
            if (keywordMethod.getName().equals(getKeywordName())) {
                return getBuiltInKeywordMethods().indexOf(keywordMethod);
            }
        }
        return 0;
    }

    @Override
    public String getItemTooltipText() {
        String keywordJavaDoc = TestCaseEntityUtil.getKeywordJavaDocText(getBuiltInKWClassSimpleName(),
                getKeywordName());
        if (!keywordJavaDoc.isEmpty()) {
            return keywordJavaDoc;
        }
        return super.getItemTooltipText();
    }

    @Override
    public boolean setItem(Object item) {
        if (!(item instanceof Integer) || (int) item < 0 || (int) item >= getBuiltInKeywordMethods().size()) {
            return false;
        }
        String newMethodName = getBuiltInKeywordMethods().get((int) item).getName();
        if (getKeywordName().equals(newMethodName)) {
            return false;
        }
        if (methodCall.setMethod(newMethodName)) {
            AstKeywordsInputUtil.generateBuiltInKeywordArguments(methodCall);
            if (!canEditOutput()) {
                removeOutput();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canEditInput() {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                getBuiltInKWClassSimpleName(), getKeywordName());
        if (keywordMethod == null || keywordMethod.getParameters().length == 0) {
            return false;
        }
        int count = 0;
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (!AstEntityInputUtil.isTestObjectClass(keywordMethod.getParameters()[i].getType())) {
                count++;
            }
        }
        return count > 0;
    }

    @Override
    public String getInputText() {
        ArgumentListExpressionWrapper arguments = methodCall.getArguments();
        if (arguments == null || arguments.getExpressions().size() == 0) {
            return "";
        }
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                getBuiltInKWClassSimpleName(), getKeywordName());
        if (keywordMethod == null) {
            return "";
        }
        return buildInputDisplayString(arguments, keywordMethod);
    }

    protected String buildInputDisplayString(ArgumentListExpressionWrapper arguments, KeywordMethod keywordMethod) {
        int count = 0;
        StringBuilder displayString = new StringBuilder();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter keywordParam = keywordMethod.getParameters()[i];
            if (isIgnoreParamType(keywordParam.getType())) {
                continue;
            }
            if (count > 0) {
                displayString.append("; ");
            }
            ExpressionWrapper inputExpression = arguments.getExpression(i);
            InputValueType typeValue = AstValueUtil.getTypeValue(inputExpression);
            if (typeValue != null) {
                displayString.append(typeValue.getValueToDisplay(inputExpression));
            } else {
                displayString.append(inputExpression.getText());
            }
            count++;
        }
        return displayString.toString();
    }

    protected boolean isIgnoreParamType(Class<?> paramType) {
        return AstEntityInputUtil.isTestObjectClass(paramType) || AstEntityInputUtil.isFailureHandlingClass(paramType);
    }

    @Override
    public Object getInput() {
        ArgumentListExpressionWrapper argumentList = methodCall.getArguments();
        if (argumentList == null) {
            return null;
        }
        return AstKeywordsInputUtil.generateBuiltInKeywordInputParameters(getBuiltInKWClassSimpleName(),
                getKeywordName(), argumentList.clone());
    }

    @Override
    public boolean setInput(Object input) {
        if (!(input instanceof List<?>)) {
            return false;
        }
        List<?> inputParameters = (List<?>) input;
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                getBuiltInKWClassSimpleName(), getKeywordName());
        if (keywordMethod == null) {
            return false;
        }
        return setInput(inputParameters, keywordMethod);
    }

    protected boolean setInput(List<?> inputParameters, KeywordMethod keywordMethod) {
        ArgumentListExpressionWrapper argumentListExpression = new ArgumentListExpressionWrapper(methodCall);
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (!(inputParameters.get(i) instanceof InputParameter)) {
                continue;
            }
            InputParameter inputParameter = (InputParameter) inputParameters.get(i);
            argumentListExpression.addExpression(inputParameter.getValueAsExpression());

        }
        return methodCall.setArguments(argumentListExpression);
    }

    @Override
    protected int getObjectArgumentIndex() {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                getBuiltInKWClassSimpleName(), getKeywordName());
        if (keywordMethod == null) {
            return -1;
        }
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (AstEntityInputUtil.isTestObjectClass(keywordMethod.getParameters()[i].getType())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean canEditOutput() {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                getBuiltInKWClassSimpleName(), getKeywordName());
        return isOutputNotVoid(keywordMethod);
    }

    private boolean isOutputNotVoid(KeywordMethod keywordMethod) {
        return keywordMethod != null && !AstKeywordsInputUtil.isVoidClass(keywordMethod.getReturnType());
    }

    @Override
    protected Class<?> getOutputReturnType() {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                getBuiltInKWClassSimpleName(), getKeywordName());
        if (isOutputNotVoid(keywordMethod)) {
            return keywordMethod.getReturnType();
        }
        return null;
    }

    private List<KeywordMethod> getBuiltInKeywordMethods() {
        return KeywordController.getInstance().getBuiltInKeywords(getBuiltInKWClassSimpleName());
    }
}
