package com.kms.katalon.composer.testsuite.editors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testsuite.dialogs.DataIterationDialog;
import com.kms.katalon.entity.link.IterationEntity;

public class DataIterationCellEditor extends CellEditor {

    private IterationEntity iterationEntity;
    private Composite parent;

    
    public DataIterationCellEditor(Composite parent) {
        super(parent);
        this.parent = parent;
    }
    
    @Override
    public void activate() {
        doSetValue(openDialog(parent.getShell()));
        fireApplyEditorValue();
    }

    private Object openDialog(Shell shell) {
        DataIterationDialog dialog = new DataIterationDialog(shell, iterationEntity.clone());
        if (dialog.open() == Window.OK) {
            return dialog.getIterationEntity();
        }
        return null;
    }

    @Override
    protected Control createControl(Composite parent) {
        return null;
    }

    @Override
    protected Object doGetValue() {
        return iterationEntity;
    }

    @Override
    protected void doSetFocus() {
    }

    @Override
    protected void doSetValue(Object value) {
        iterationEntity = (IterationEntity) value;
    }
}
