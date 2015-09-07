package com.kms.katalon.composer.testcase.providers;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;

public class AstInputConstantTypeLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof ConstantExpression) {
			return AstTreeTableInputUtil.getConstantValueTypeFromConstantExpression((ConstantExpression) element).toString();
		}
		return StringUtils.EMPTY;
	}
}
