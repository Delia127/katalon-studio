package com.kms.katalon.composer.testcase.editors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataSelectionDialogCellEditor extends EntitySelectionDialogCellEditor {

    private static final String pluginId = FrameworkUtil.getBundle(TestDataSelectionDialogCellEditor.class)
            .getSymbolicName();

    public TestDataSelectionDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
        setDialogValidator(new ISelectionStatusValidator() {

            @Override
            public IStatus validate(Object[] selection) {
                if (TreeEntityUtil.isValidTreeEntitySelectionType(selection,
                        com.kms.katalon.composer.components.impl.constants.StringConstants.TREE_TEST_DATA_TYPE_NAME)) {
                    return new Status(IStatus.OK, pluginId, IStatus.OK, null, null);
                }
                return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, null, null);
            }
        });
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.EDI_TITLE_TEST_DATA_BROWSER;
    }

    @Override
    public FolderEntity getRootFolder() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return null;
        }
        try {
            return FolderController.getInstance().getTestDataRoot(currentProject);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    @Override
    public ITreeEntity getInitialSelection() {
        if (getValue() instanceof DataFileEntity) {
            DataFileEntity selectedDataFile = (DataFileEntity) getValue();
            return new TestDataTreeEntity(selectedDataFile, TreeEntityUtil.createSelectedTreeEntityHierachy(
                    selectedDataFile.getParentFolder(), getRootFolder()));
        }
        return null;
    }

    @Override
    protected void doSetValue(Object value) {
        assert (value instanceof DataFileEntity);
        super.doSetValue(value);
    }

    @Override
    protected Object doGetValue() {
        Object value = super.doGetValue();
        if (!(value instanceof DataFileEntity)) {
            return null;
        }
        return value;
    }

}
