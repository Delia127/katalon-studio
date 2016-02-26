package com.kms.katalon.composer.testcase.editors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.folder.FolderEntity;

public abstract class EntitySelectionDialogCellEditor extends AbstractDialogCellEditor {
    public EntitySelectionDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        if (ProjectController.getInstance().getCurrentProject() == null) {
            return null;
        }
        TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(Display.getCurrent().getActiveShell(),
                new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(new EntityProvider()));
        dialog.setAllowMultiple(false);
        dialog.setTitle(getDialogTitle());
        FolderEntity rootFolder = getRootFolder();
        if (rootFolder == null) {
            return null;
        }
        try {
            dialog.setInput(TreeEntityUtil.getChildren(null, rootFolder));
            if (getValue() instanceof Entity && getInitialSelection() != null) {
                dialog.setInitialSelection(getInitialSelection());
            }
            if (dialog.open() == Window.OK) {
                return dialog.getFirstResult();
            } else {
                return null;
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.EDI_ERROR_MSG_CANNOT_OPEN_DIA);
            LoggerSingleton.logError(e);
            return null;
        }

    }

    public abstract String getDialogTitle();

    public abstract FolderEntity getRootFolder();

    public abstract ITreeEntity getInitialSelection();

    @Override
    protected void doSetValue(Object value) {
        assert (value instanceof Entity);
        super.doSetValue(value);
    }

    @Override
    protected Object doGetValue() {
        Object value = super.doGetValue();
        if (!(value instanceof Entity)) {
            return null;
        }
        return (Entity) value;
    }
}
