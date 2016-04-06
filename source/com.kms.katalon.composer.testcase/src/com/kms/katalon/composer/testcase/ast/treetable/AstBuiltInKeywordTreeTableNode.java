package com.kms.katalon.composer.testcase.ast.treetable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.editors.ComboBoxCellEditorWithContentProposal;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.keyword.KeywordMethod;
import com.kms.katalon.custom.keyword.KeywordParameter;

public class AstBuiltInKeywordTreeTableNode extends AstAbstractKeywordTreeTableNode {
    List<KeywordMethod> builtInKeywordMethods;

    public AstBuiltInKeywordTreeTableNode(ExpressionStatementWrapper methodCallStatement, AstTreeTableNode parentNode) {
        super(methodCallStatement, parentNode);
        AstTreeTableInputUtil.generateBuiltInKeywordArguments(methodCall);
        builtInKeywordMethods = KeywordController.getInstance().getBuiltInKeywords(getBuiltInKWClassSimpleName());
    }

    protected String getBuiltInKWClassSimpleName() {
        return methodCall.getObjectExpressionAsString();
    }

    private List<String> getKeywordNames() {
        List<String> keywordNames = new ArrayList<String>();
        for (KeywordMethod keywordMethod : builtInKeywordMethods) {
            keywordNames.add(TreeEntityUtil.getReadableKeywordName(keywordMethod.getName()));
        }
        return keywordNames;
    }

    private List<String> getKeywordToolTips() {
        try {
            return TestCaseEntityUtil.getAllKeywordJavaDocText(getBuiltInKWClassSimpleName(), parentStatement.getScriptClass());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return Collections.emptyList();
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
        List<KeywordMethod> builtInKeywordMethods = KeywordController.getInstance().getBuiltInKeywords(
                getBuiltInKWClassSimpleName());
        for (KeywordMethod keywordMethod : builtInKeywordMethods) {
            if (keywordMethod.getName().equals(getKeywordName())) {
                return builtInKeywordMethods.indexOf(keywordMethod);
            }
        }
        return 0;
    }

    @Override
    public String getItemTooltipText() {
        try {
            String keywordJavaDoc = TestCaseEntityUtil.getKeywordJavaDocText(getBuiltInKWClassSimpleName(), getKeywordName(),
                    parentStatement.getScriptClass());
            if (!keywordJavaDoc.isEmpty()) {
                return keywordJavaDoc;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return super.getItemTooltipText();
    }

    @Override
    public boolean setItem(Object item) {
        if (!(item instanceof Integer) || (int) item < 0 || (int) item >= builtInKeywordMethods.size()
                || getKeywordName().equals(builtInKeywordMethods.get((int) item).getName())) {
            return false;
        }
        methodCall.setMethod(builtInKeywordMethods.get((int) item).getName());
        AstTreeTableInputUtil.generateBuiltInKeywordArguments(methodCall);
        if (!canEditOutput()) {
            setOutput(null);
        }
        return true;
    }

    @Override
    public boolean canEditInput() {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(getBuiltInKWClassSimpleName(),
                getKeywordName());
        if (keywordMethod == null || keywordMethod.getParameters().length == 0) {
            return false;
        }
        int count = 0;
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (!AstEntityInputUtil.isObjectClass(keywordMethod.getParameters()[i].getType())) {
                count++;
            }
        }
        return count > 0;
    }

    @Override
    public String getInputText() {
        ArgumentListExpressionWrapper arguments = (ArgumentListExpressionWrapper) methodCall.getArguments();
        if (arguments == null || arguments.getExpressions().size() == 0) {
            return "";
        }
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(getBuiltInKWClassSimpleName(),
                getKeywordName());
        if (keywordMethod == null) {
            return "";
        }

        int count = 0;
        StringBuilder displayString = new StringBuilder();
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            KeywordParameter keywordParam = keywordMethod.getParameters()[i];
            if (TestObject.class.isAssignableFrom(keywordParam.getType())
                    || FailureHandling.class.isAssignableFrom(keywordParam.getType())) {
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
    public Object getInput() {
        ArgumentListExpressionWrapper argumentList = (ArgumentListExpressionWrapper) methodCall.getArguments();
        if (argumentList == null) {
            return null;
        }
        try {
            return AstTreeTableInputUtil.generateBuiltInKeywordInputParameters(getBuiltInKWClassSimpleName(), getKeywordName(),
                    argumentList.clone());
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
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(getBuiltInKWClassSimpleName(),
                getKeywordName());
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

    @Override
    protected int getObjectArgumentIndex() {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(getBuiltInKWClassSimpleName(),
                getKeywordName());
        if (keywordMethod == null) {
            return -1;
        }
        for (int i = 0; i < keywordMethod.getParameters().length; i++) {
            if (TestObject.class.isAssignableFrom(keywordMethod.getParameters()[i].getType())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean canEditOutput() {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(getBuiltInKWClassSimpleName(),
                getKeywordName());
        return (keywordMethod != null && keywordMethod.getReturnType() != Void.class && keywordMethod.getReturnType() != Void.TYPE);
    }

    @Override
    protected Class<?> getOutputReturnType() {
        KeywordMethod keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(getBuiltInKWClassSimpleName(),
                getKeywordName());
        if (keywordMethod != null && !AstTreeTableInputUtil.isVoidClass(keywordMethod.getReturnType())) {
            return keywordMethod.getReturnType();
        }
        return null;
    }
}
