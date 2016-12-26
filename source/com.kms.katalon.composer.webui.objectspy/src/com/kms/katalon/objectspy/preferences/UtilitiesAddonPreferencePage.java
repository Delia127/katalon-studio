package com.kms.katalon.objectspy.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.constants.UtilitiesAddonPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class UtilitiesAddonPreferencePage extends FieldEditorPreferencePage {
    public UtilitiesAddonPreferencePage() {
        setPreferenceStore(
                PreferenceStoreManager.getPreferenceStore(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        addField(new IntegerFieldEditor(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_PORT,
                StringConstants.PREF_LBL_ACTIVE_BROWSER_PORT, composite));
        addCheckboxField(composite);

        initialize();
        checkState();
        return composite;
    }

    private void addCheckboxField(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        addField(new BooleanFieldEditor(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_DO_NOT_SHOW_AGAIN,
                StringConstants.PREF_LBL_ACTIVE_BROWSER_PORT_DO_NOT_SHOW_WARNING_DIALOG, composite));
    }

    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }
}
