package com.kms.katalon.composer.global.dialog;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.VariableBuilderDialog;
import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableBuilderDialog extends VariableBuilderDialog {
	private GlobalVariableEntity variable;
	private Point location;

	public GlobalVariableBuilderDialog(Shell parentShell, Point location) {
		super(parentShell, DialogType.NEW);
		this.location = location;
	}
	
	public GlobalVariableBuilderDialog(Shell parentShell, GlobalVariableEntity variableEntity, Point location) {
		super(parentShell, DialogType.EDIT);
		variable = variableEntity;
		this.location = location;
	}

	@Override
	protected void okPressed() {
		if (variable == null) {
			variable = new GlobalVariableEntity();
		}
		variable.setName(textVariableName.getText());
		variable.setInitValue(textDefaultValue.getText());
		super.okPressed();
	}

	@Override
	public void create() {
		super.create();
		setInput();
	}

	private void setInput() {
		if (variable != null) {
			textVariableName.setText(variable.getName());
			textDefaultValue.setText(variable.getInitValue());
		}
	}
	
	public GlobalVariableEntity getVariable() {
		return variable;
	}

	@Override
	public Point getInitialLocation(Point initialSize) {
		return new Point(this.location.x - initialSize.x - 10, this.location.y);
	}
}
