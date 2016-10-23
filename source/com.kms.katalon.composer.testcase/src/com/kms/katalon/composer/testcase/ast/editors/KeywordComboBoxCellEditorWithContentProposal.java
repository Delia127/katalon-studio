package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.ast.MethodNode;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.editors.ComboBoxCellEditorWithContentProposal;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.custom.keyword.KeywordMethod;

public class KeywordComboBoxCellEditorWithContentProposal extends ComboBoxCellEditorWithContentProposal {
    private Object[] items;

    private StatementWrapper parentStatement;

    private String keywordClassAliasName;

    public KeywordComboBoxCellEditorWithContentProposal(Composite parent, StatementWrapper parentStatement,
            String keywordClassAliasName, Object[] items, Object[] displayedItems, String[] toolTips) {
        super(parent, displayedItems, toolTips);
        this.items = items;
        this.parentStatement = parentStatement;
        this.keywordClassAliasName = keywordClassAliasName;
    }

    @Override
    protected Object doGetValue() {
        int selectedIndex = (int) super.doGetValue();
        Object selectedItem = items[selectedIndex];
        String newMethodName = getMethodName(selectedItem);
        MethodCallExpressionWrapper newMethodCall = new MethodCallExpressionWrapper(keywordClassAliasName,
                newMethodName, parentStatement);
        generateArguments(newMethodCall);
        return newMethodCall;
    }

    protected void generateArguments(MethodCallExpressionWrapper newMethodCall) {
        AstKeywordsInputUtil.generateBuiltInKeywordArguments(newMethodCall);
    }
    
    @Override
    protected void doSetValue(Object value) {
        if (!(value instanceof MethodCallExpressionWrapper)) {
            super.doSetValue(value);
            return;
        }
        MethodCallExpressionWrapper methodCall = (MethodCallExpressionWrapper) value;
        String keywordName = getKeywordName(methodCall);
        for (int index = 0; index < items.length; index++) {
            if (getMethodName(items[index]).equals(keywordName)) {
                super.doSetValue(index);
                return;
            }
        }
    }

    protected String getKeywordName(MethodCallExpressionWrapper methodCall) {
        return methodCall.getMethodAsString();
    }

    public String getMethodName(Object selectedItem) {
        if (selectedItem instanceof String) {
            return (String) selectedItem;
        }
        if (selectedItem instanceof MethodNode) {
            return ((MethodNode) selectedItem).getName();
        }
        if (selectedItem instanceof KeywordMethod) {
            return ((KeywordMethod) selectedItem).getName();
        }
        return null;
    }

    
    
}
