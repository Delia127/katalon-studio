package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.ThrowBuilderDialog;

public class ThrowInputCellEditor extends AbstractAstDialogCellEditor {

	public ThrowInputCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof ThrowStatement || value == null) {
					return null;
				}
				return getValidatorMessage(ThrowStatement.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new ThrowBuilderDialog(shell, (getValue() == null) ? null : (ThrowStatement) getValue(), scriptClass);
	}

}
