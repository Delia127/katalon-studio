package com.kms.katalon.composer.testcase.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;

public class AstInputValueLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element) {
		return AstTreeTableTextValueUtil.getTextValue(element);
	}
}
