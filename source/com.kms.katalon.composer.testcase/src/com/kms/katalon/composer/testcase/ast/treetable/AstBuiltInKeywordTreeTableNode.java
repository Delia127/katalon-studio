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

    // Built-In keyword methods without FailureHandling, to show in drop-down list in test case manual mode
    // This list is being used many places, it should be initiated only once
    private List<KeywordMethod> builtInKeywordMethods = new ArrayList<KeywordMethod>();

    public AstBuiltInKeywordTreeTableNode(ExpressionStatementWrapper methodCallStatement, AstTreeTableNode parentNode) {
        super(methodCallStatement, parentNode);
        builtInKeywordMethods.addAll(getKeywords());
    }

    protected String getBuiltInKWClassSimpleName() {
        return methodCall.getObjectExpressionAsString();
    }

    private List<KeywordMethod> getKeywords() {
        List<KeywordMethod> keywords = new ArrayList<KeywordMethod>();
        for (KeywordMethod keywordMethod : getBuiltInKeywordMethodsWithoutFlowControl()) {
            keywords.add(keywordMethod);
        }
        return keywords;
    }

    @Override
    public CellEditor getCellEditorForItem(Composite parent) {
        List<String> keywordNames = new ArrayList<String>();
        List<String> toolTips = new ArrayList<String>();
        String builtInKWClassSimpleName = getBuiltInKWClassSimpleName();
        for (KeywordMethod keywordMethod : builtInKeywordMethods) {
            keywordNames.add(TreeEntityUtil.getReadableKeywordName(keywordMethod.getName()));
            toolTips.add(TestCaseEntityUtil.getKeywordJavaDocText(builtInKWClassSimpleName, keywordMethod.getName()));
        }

        return new ComboBoxCellEditorWithContentProposal(parent, keywordNames.toArray(new String[keywordNames.size()]),
                toolTips.toArray(new String[toolTips.size()]));
    }

    @Override
    public Object getItem() {
        for (KeywordMethod keywordMethod : builtInKeywordMethods) {
            if (keywordMethod.getName().equals(getKeywordName())) {
                return builtInKeywordMethods.indexOf(keywordMethod);
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
        if (!(item instanceof Integer) || (int) item < 0 || (int) item >= builtInKeywordMethods.size()) {
            return false;
        }
        String newMethodName = builtInKeywordMethods.get((int) item).getName();
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
        KeywordMethod keywordMethod = findKeywordMethod();

        if (keywordMethod == null || keywordMethod.getParameters().length == 0) {
            return false;
        }
        boolean hasTestObjectParam = false;
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            boolean isTestObjectParam = AstEntityInputUtil.isTestObjectClass(keywordMethod.getParameters()[i].getType());
            if (!isTestObjectParam || hasTestObjectParam) {
                return true;
            }
            hasTestObjectParam = true;
        }
        return false;
    }

    @Override
    public String getInputText() {
        ArgumentListExpressionWrapper arguments = methodCall.getArguments();
        if (arguments == null || arguments.getExpressions().size() == 0) {
            return "";
        }
        KeywordMethod keywordMethod = findKeywordMethod();
        if (keywordMethod == null) {
            return "";
        }
        return buildInputDisplayString(arguments, keywordMethod);
    }

    protected String buildInputDisplayString(ArgumentListExpressionWrapper arguments, KeywordMethod keywordMethod) {
        int count = 0;
        StringBuilder displayString = new StringBuilder();
        boolean hasTestObjectParam = false;
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter keywordParam = keywordMethod.getParameters()[i];
            Class<?> paramType = keywordParam.getType();
            if (AstEntityInputUtil.isTestObjectClass(paramType) && !hasTestObjectParam) {
                hasTestObjectParam = true;
                continue;
            }
            if (isIgnoreParamType(paramType)) {
                continue;
            }
            if (count > 0) {
                displayString.append("; ");
            }
            ExpressionWrapper inputExpression = arguments.getExpression(i);
            if (inputExpression == null) {
                // TODO: Handle missing arguments for keyword
                count++;
                continue;
            }
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
        return AstEntityInputUtil.isFailureHandlingClass(paramType);
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
        KeywordMethod keywordMethod = findKeywordMethod();
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
        KeywordMethod keywordMethod = findKeywordMethod();
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
        KeywordMethod keywordMethod = findKeywordMethod();
        return isOutputNotVoid(keywordMethod);
    }

    private boolean isOutputNotVoid(KeywordMethod keywordMethod) {
        return keywordMethod != null && !AstKeywordsInputUtil.isVoidClass(keywordMethod.getReturnType());
    }

    @Override
    protected Class<?> getOutputReturnType() {
        KeywordMethod keywordMethod = findKeywordMethod();
        if (isOutputNotVoid(keywordMethod)) {
            return keywordMethod.getReturnType();
        }
        return null;
    }

    /**
     * Get built-in keywords without Failure Handling, these keyword methods will be shown in test case manual mode for
     * user selection
     * 
     * @return list of keyword method
     */
    private List<KeywordMethod> getBuiltInKeywordMethodsWithoutFlowControl() {
        return KeywordController.getInstance().getBuiltInKeywords(getBuiltInKWClassSimpleName(), true);
    }

    /**
     * Find the keyword method that most suitable for the given method call
     * @return most suitable eywordMethod
     */
    private KeywordMethod findKeywordMethod() {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
                getBuiltInKWClassSimpleName(), getKeywordName(),
                methodCall.getArguments().getArgumentListParameterTypes());
        return keywordMethod;
    }
}
