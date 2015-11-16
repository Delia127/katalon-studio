package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.PropertyInputBuilderDialog;

public class PropertyInputCellEditor extends AbstractAstDialogCellEditor {
	public PropertyInputCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof PropertyExpression) {
					return null;
				}
				return getValidatorMessage(PropertyExpression.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		try {
			return new PropertyInputBuilderDialog(shell, getValue() == null ? null : (PropertyExpression) getValue(),
					scriptClass);
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return null;
	}
}