package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.MapInputBuilderDialog;

public class MapInputCellEditor extends AbstractAstDialogCellEditor {
	public MapInputCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof MapExpression || value == null) {
					return null;
				}
				return getValidatorMessage(MapExpression.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new MapInputBuilderDialog(shell, getValue() == null ? null : (MapExpression) getValue(), scriptClass);
	}

}
