package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.dialogs.IAstDialogBuilder;

public abstract class AstDialogCellEditor extends AbstractDialogCellEditor {

    public AstDialogCellEditor(Composite parent, String defaultContent, final Class<?> typeClass) {
        super(parent, defaultContent);
        if (typeClass == null) {
            return;
        }
        this.setValidator(new ICellEditorValidator() {
            @Override
            public String isValid(Object value) {
                if (typeClass.isAssignableFrom(value.getClass())) {
                    return null;
                }
                return getValidatorMessage(typeClass.getName());
            }
        });
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        try {
            IAstDialogBuilder dialog = getDialog(Display.getCurrent().getActiveShell());
            if (dialog != null && dialog.open() == Window.OK) {
                return getReturnValue(dialog);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    protected Object getReturnValue(IAstDialogBuilder dialog) {
        return dialog.getReturnValue();
    }

    protected abstract IAstDialogBuilder getDialog(Shell shell);
}
