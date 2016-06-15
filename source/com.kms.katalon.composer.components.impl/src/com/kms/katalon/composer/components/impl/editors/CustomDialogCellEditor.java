package com.kms.katalon.composer.components.impl.editors;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public abstract class CustomDialogCellEditor extends DialogCellEditor {
    protected Label defaultLabel;

    protected CustomDialogCellEditor(Composite parent) {
        super(parent);
    }

    protected final Control createContents(Composite cell) {
        defaultLabel = (Label) super.createContents(cell);

        return defaultLabel;
    }
}
