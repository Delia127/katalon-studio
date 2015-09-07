package com.kms.katalon.composer.testcase.support;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstInputBuilderValueTypeColumnSupport extends EditingSupport {
	protected InputValueType[] inputValueTypes;
	protected AstBuilderDialog parentDialog;
	protected ClassNode scriptClass;

	public AstInputBuilderValueTypeColumnSupport(ColumnViewer viewer, InputValueType[] inputValueTypes,
			AstBuilderDialog parentDialog, ClassNode scriptClass) {
		super(viewer);
		this.inputValueTypes = inputValueTypes;
		this.parentDialog = parentDialog;
		this.scriptClass = scriptClass;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof ASTNode && value instanceof Integer && (int) value > -1
				&& (int) value < inputValueTypes.length) {
			InputValueType newValueType = inputValueTypes[(int) value];
			InputValueType oldValueType = AstTreeTableValueUtil.getTypeValue((ASTNode) element, scriptClass);
			if (newValueType != oldValueType) {
				ASTNode astNode = AstTreeTableValueUtil.setTypeValue((ASTNode) element, newValueType);
				parentDialog.changeObject(element, astNode);
				getViewer().refresh();
			}
		}
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof ASTNode) {
			return AstTreeTableInputUtil.getInputValueTypeIndex(inputValueTypes,
					AstTreeTableValueUtil.getTypeValue((ASTNode) element, scriptClass));
		}
		return 0;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new ComboBoxCellEditor((Composite) getViewer().getControl(),
				AstTreeTableInputUtil.getInputValueTypeStringList(inputValueTypes));
	}

	@Override
	protected boolean canEdit(Object element) {
		if (element instanceof ASTNode) {
			return true;
		}
		return false;
	}

}
