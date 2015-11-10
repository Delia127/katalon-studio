package com.kms.katalon.composer.global.dialog;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.VariableBuilderDialog;
import com.kms.katalon.composer.components.impl.model.VariableDialogModel;
import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableBuilderDialog extends VariableBuilderDialog {
    private GlobalVariableEntity fVariableEntity;
    private Point location;

    public GlobalVariableBuilderDialog(Shell parentShell, Point location) {
        super(parentShell, DialogType.NEW, null);
        this.location = location;
    }

    public GlobalVariableBuilderDialog(Shell parentShell, GlobalVariableEntity variableEntity, Point location) {
        super(parentShell, DialogType.EDIT, new VariableDialogModel(variableEntity.getName(),
                variableEntity.getInitValue()));
        fVariableEntity = variableEntity;
        this.location = location;
    }

    @Override
    protected void okPressed() {
        super.okPressed();
        VariableDialogModel variableModel = getVariable();
        if (fVariableEntity == null) {
            fVariableEntity = new GlobalVariableEntity();
        }
        fVariableEntity.setName(variableModel.getName());
        fVariableEntity.setInitValue(variableModel.getValue());
    }

    public GlobalVariableEntity getVariableEntity() {
        return fVariableEntity;
    }

    @Override
    public Point getInitialLocation(Point initialSize) {
        return new Point(this.location.x - initialSize.x - 10, this.location.y);
    }
}
