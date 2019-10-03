package com.kms.katalon.objectspy.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.FieldEditorPreferencePageWithHelp;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.constants.UtilitiesAddonPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class UtilitiesAddonPreferencePage extends FieldEditorPreferencePageWithHelp {
    private static final int MIN_PORT_NUMBER = 1;

    private static final int MAX_PORT_NUMBER = 65534;

    public UtilitiesAddonPreferencePage() {
        setPreferenceStore(
                PreferenceStoreManager.getPreferenceStore(UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        IntegerFieldEditor portEditor = new IntegerFieldEditor(
                UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_PORT,
                StringConstants.PREF_LBL_ACTIVE_BROWSER_PORT, composite);
        portEditor.setValidRange(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
        addField(portEditor);
        addCheckboxField(composite);

        initialize();
        checkState();
        return composite;
    }

    private void addCheckboxField(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        addField(new BooleanFieldEditor(
                UtilitiesAddonPreferenceConstants.UTILITIES_ADDON_ACTIVE_BROWSER_DO_NOT_SHOW_AGAIN,
                StringConstants.PREF_LBL_ACTIVE_BROWSER_PORT_DO_NOT_SHOW_WARNING_DIALOG, composite));
    }

    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.PREFERENCE_UTILITY_ADDON;
    }
}
