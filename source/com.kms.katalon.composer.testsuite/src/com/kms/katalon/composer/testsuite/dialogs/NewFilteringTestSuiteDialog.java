package com.kms.katalon.composer.testsuite.dialogs;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.CommonNewEntityDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;

public class NewFilteringTestSuiteDialog extends CommonNewEntityDialog<FilteringTestSuiteEntity> {

    public NewFilteringTestSuiteDialog(Shell parentShell, FolderEntity parentFolder, String suggestedName) {
        super(parentShell, parentFolder, suggestedName);
        setDialogTitle("New Filtering Test Suite");
        setDialogMsg("Create new Filtering Test Suite");
    }

    @Override
    protected void createEntity() {
        try {
            entity = TestSuiteController.getInstance().newFilteringTestSuiteWithoutSave(parentFolder, getName());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected void setEntityProperties() {
        super.setEntityProperties();
        IPreferenceStore store = getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
        entity.setPageLoadTimeout((short) ExecutionDefaultSettingStore.getStore().getElementTimeout());
        entity.setMailRecipient(store.getString(ExecutionPreferenceConstants.MAIL_CONFIG_REPORT_RECIPIENTS));
    }

}
