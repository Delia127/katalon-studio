package com.kms.katalon.composer.testcase.editors;

import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.dialogs.InputBuilderDialog;
import com.kms.katalon.composer.testcase.model.InputParameter;

public class InputCellEditor extends AbstractAstDialogCellEditor {
	public InputCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof List<?>) {
					List<?> list = (List<?>) value;
					boolean isValid = true;
					for (Object object : list) {
						if (!(object instanceof InputParameter)) {
							isValid = false;
						}
					}
					if (isValid) {
						return null;
					}
				}
				return getValidatorMessage("java.util.List<" + InputParameter.class.getName() + ">");
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new InputBuilderDialog(shell, getValue() == null ? null : (List<InputParameter>) getValue(), scriptClass);
	}
}
