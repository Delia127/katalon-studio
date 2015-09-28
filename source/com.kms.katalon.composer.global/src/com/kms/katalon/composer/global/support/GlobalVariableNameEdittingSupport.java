package com.kms.katalon.composer.global.support;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableNameEdittingSupport extends EditingSupport {
	
	private MPart parentPart;
	
	public GlobalVariableNameEdittingSupport(ColumnViewer viewer, MPart mpart) {
		super(viewer);
		parentPart = mpart;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor((Composite) getViewer().getControl());
	}

	@Override
	protected boolean canEdit(Object element) {
		return (element != null && element instanceof GlobalVariableEntity) ? true : false;
	}

	@Override
	protected Object getValue(Object element) {
		if (element != null && element instanceof GlobalVariableEntity) {
			return ((GlobalVariableEntity) element).getName();
		}
		return "";
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element != null && element instanceof GlobalVariableEntity && value != null && value instanceof String) {
			GlobalVariableEntity variable = (GlobalVariableEntity) element;
			if (!variable.getName().equals(value)) {
				variable.setName((String) value);
				getViewer().update(element, null);
				parentPart.setDirty(true);
			}
		}
	}

}
