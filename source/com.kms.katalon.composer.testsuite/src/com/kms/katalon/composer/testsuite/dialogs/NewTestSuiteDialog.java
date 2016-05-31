package com.kms.katalon.composer.testsuite.dialogs;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.TestEnvironmentController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class NewTestSuiteDialog extends CommonNewEntityDialog<TestSuiteEntity> {

    public NewTestSuiteDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle(StringConstants.DIA_TITLE_TEST_SUITE);
        setDialogMsg(StringConstants.DIA_MSG_CREATE_NEW_TEST_SUITE);
    }

    @Override
    protected void createEntity() {
        try {
            entity = TestSuiteController.getInstance().newTestSuiteWithoutSave(parentFolder, getName());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected void setEntityProperties() {
        super.setEntityProperties();
        entity.setPageLoadTimeout(TestEnvironmentController.getInstance().getPageLoadTimeOutDefaultValue());
        entity.setMailRecipient(getPreferenceStore(PreferenceConstants.EXECUTION_QUALIFIER).getString(
                PreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS));
    }

}
