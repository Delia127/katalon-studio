package com.kms.katalon.composer.testcase.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstInputValueLabelProvider extends ColumnLabelProvider {
    @Override
    public String getText(Object element) {
        if (!(element instanceof ASTNodeWrapper)) {
            return "";
        }
        InputValueType typeValue = AstTreeTableValueUtil.getTypeValue((ASTNodeWrapper) element);
        if (typeValue != null) {
            return typeValue.getValueToDisplay(element);
        }
        return "";
    }
}
