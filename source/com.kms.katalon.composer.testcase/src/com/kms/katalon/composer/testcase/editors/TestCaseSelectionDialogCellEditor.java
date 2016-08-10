package com.kms.katalon.composer.testcase.editors;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestCaseSelectionDialogCellEditor extends EntitySelectionDialogCellEditor {

    public TestCaseSelectionDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.EDI_TITLE_TEST_CASE_BROWSER;
    }

    @Override
    public FolderEntity getRootFolder() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject == null) {
            return null;
        }
        try {
            return FolderController.getInstance().getTestCaseRoot(currentProject);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    @Override
    public ITreeEntity getInitialSelection() {
        if (getValue() instanceof DataFileEntity) {
            TestCaseEntity selectedTestCase = (TestCaseEntity) getValue();
            return new TestCaseTreeEntity(selectedTestCase, TreeEntityUtil.createSelectedTreeEntityHierachy(
                    selectedTestCase.getParentFolder(), getRootFolder()));
        }
        return null;
    }

    @Override
    protected void doSetValue(Object value) {
        assert (value instanceof TestCaseEntity);
        super.doSetValue(value);
    }

    @Override
    protected Object doGetValue() {
        Object value = super.doGetValue();
        if (!(value instanceof TestCaseEntity)) {
            return null;
        }
        return (TestCaseEntity) value;
    }

}
