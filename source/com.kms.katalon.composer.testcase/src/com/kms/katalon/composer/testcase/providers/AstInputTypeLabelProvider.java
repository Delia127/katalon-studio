package com.kms.katalon.composer.testcase.providers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstValueUtil;

public class AstInputTypeLabelProvider extends ColumnLabelProvider {
    @Override
    public String getText(Object element) {
        if (!(element instanceof ASTNodeWrapper)) {
            return StringUtils.EMPTY;
        }
        InputValueType typeValue = AstValueUtil.getTypeValue((ASTNodeWrapper) element);
        if (typeValue != null) {
            return TreeEntityUtil.getReadableKeywordName(typeValue.getName());
        }
        return StringUtils.EMPTY;
    }
}
