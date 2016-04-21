package com.kms.katalon.composer.mobile.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.execution.mobile.constants.MobilePreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class MobileSettingPreferencePage extends FieldEditorPreferencePage {
    public MobileSettingPreferencePage() {
        super();
        setPreferenceStore(PreferenceStoreManager.getPreferenceStore(MobilePreferenceConstants.MOBILE_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setFont(parent.getFont());
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        composite.setLayoutData(gridData);
        addField(new DirectoryFieldEditor(MobilePreferenceConstants.MOBILE_APPIUM_DIRECTORY,
                StringConstants.PREF_LBL_APPIUM_DIRECTORY, composite));

        initialize();
        checkState();
        return composite;
    }

    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }
}
