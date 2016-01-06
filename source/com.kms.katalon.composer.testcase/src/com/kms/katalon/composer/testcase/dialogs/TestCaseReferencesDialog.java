package com.kms.katalon.composer.testcase.dialogs;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteEntityDialog;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.handlers.DeleteTestCaseHandler;
import com.kms.katalon.entity.file.FileEntity;

public class TestCaseReferencesDialog extends AbstractDeleteEntityDialog {

    public TestCaseReferencesDialog(Shell parentShell, String testCaseId, List<FileEntity> affectedEntities,
            DeleteTestCaseHandler handler) {
        super(parentShell, handler);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_CASE_REFERENCES);
        setAffectedEntities(affectedEntities);
        setEntityId(testCaseId);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 500);
    }
}
