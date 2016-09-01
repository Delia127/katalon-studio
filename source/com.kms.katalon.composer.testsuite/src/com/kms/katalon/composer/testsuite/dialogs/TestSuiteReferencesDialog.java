package com.kms.katalon.composer.testsuite.dialogs;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityDialog;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.entity.file.FileEntity;

public class TestSuiteReferencesDialog extends AbstractDeleteReferredEntityDialog {

    public TestSuiteReferencesDialog(Shell parentShell, 
            String testSuiteId, 
            List<? extends FileEntity> affectedEntities,
            boolean showYesNoToAllButtons) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_CASE_REFERENCES);
        setAffectedEntities(affectedEntities);
        setEntityId(testSuiteId);
        setShowYesNoToAllButtons(showYesNoToAllButtons);
    }

}
