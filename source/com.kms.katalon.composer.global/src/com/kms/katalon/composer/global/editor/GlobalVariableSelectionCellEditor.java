package com.kms.katalon.composer.global.editor;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.global.dialog.GlobalVariableBuilderDialog;
import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableSelectionCellEditor extends AbstractDialogCellEditor {

    private GlobalVariableEntity globalVariableEntity;

    private List<String> allGlobalVeriableNames;

    private Composite composite;

    public GlobalVariableSelectionCellEditor(Composite parent, GlobalVariableEntity globalVariableEntity,
            List<String> allGLobalVeriableNames) {
        super(parent, globalVariableEntity.getName());
        this.composite = parent;
        this.globalVariableEntity = globalVariableEntity;
        this.allGlobalVeriableNames = allGLobalVeriableNames;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        Point centerLocation = null;
        GlobalVariableBuilderDialog dialog = new GlobalVariableBuilderDialog(composite.getShell(), globalVariableEntity,
                centerLocation, allGlobalVeriableNames);
        if (dialog.open() != Dialog.OK) {
            return null;
        }
        return dialog.getVariableEntity();
    }

    protected void updateContents(Object value) {
        if (defaultContent != null) {
            super.updateContents(defaultContent);
        } else {
            super.updateContents(value);
        }
    }

}
