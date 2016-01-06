package com.kms.katalon.composer.testsuite.editors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataCellEditor extends DialogCellEditor {
    private String defaultContent;

    private String selectedTestDataId;

    public TestDataCellEditor(Composite parent, String defaultContent, String selectedPk) {
        super(parent, SWT.NONE);
        this.defaultContent = defaultContent;
        this.selectedTestDataId = selectedPk;
    }

    protected void updateContents(Object value) {
        if (value instanceof DataFileEntity) {
            try {
                getDefaultLabel().setText(((DataFileEntity) value).getIdForDisplay());
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (defaultContent != null) {
            super.updateContents(defaultContent);
        } else {
            super.updateContents(value);
        }
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        try {
            EntityProvider entityProvider = new EntityProvider();
            TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(cellEditorWindow.getShell(),
                    new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(entityProvider));

            dialog.setAllowMultiple(false);
            dialog.setTitle(StringConstants.EDI_TITLE_TEST_DATA_BROWSER);
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject != null) {
                FolderEntity rootFolder = FolderController.getInstance().getTestDataRoot(currentProject);
                dialog.setInput(TreeEntityUtil.getChildren(null, rootFolder));
                if (selectedTestDataId != null) {
                    DataFileEntity selectedTestData = TestDataController.getInstance().getTestDataByDisplayId(
                            selectedTestDataId);
                    if (selectedTestData != null) {
                        dialog.setInitialSelection(new TestDataTreeEntity(selectedTestData, TreeEntityUtil
                                .createSelectedTreeEntityHierachy(selectedTestData.getParentFolder(), rootFolder)));
                    }
                }
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

}
