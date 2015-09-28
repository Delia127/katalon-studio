package com.kms.katalon.composer.testcase.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.dialogs.MethodObjectBuilderDialog;

public class MethodObjectBuilderCellEditor extends AbstractAstDialogCellEditor {
	public MethodObjectBuilderCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof MethodNode) {
					return null;
				}
				return getValidatorMessage(MethodNode.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new MethodObjectBuilderDialog(shell, getValue()  == null ? null : (MethodNode) getValue());
	}

}