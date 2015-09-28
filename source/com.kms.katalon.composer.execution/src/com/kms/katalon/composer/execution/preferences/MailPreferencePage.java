package com.kms.katalon.composer.execution.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.preferences.editor.MultiLineStringFieldEditor;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

@SuppressWarnings("restriction")
public class MailPreferencePage extends FieldEditorPreferencePage {
    public MailPreferencePage() {
        super();
        setPreferenceStore((IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite comp = SWTFactory.createComposite(parent, 2, 1, GridData.FILL_HORIZONTAL);

        Group group = SWTFactory.createGroup(comp, StringConstants.PREF_GROUP_LBL_MAIL_SERVER, 2, 2,
                GridData.FILL_HORIZONTAL);

        addStringFieldEditor(group, PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_HOST,
                StringConstants.PREF_LBL_HOST, 1, false);
        addStringFieldEditor(group, PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PORT,
                StringConstants.PREF_LBL_PORT, 1, false);
        addStringFieldEditor(group, PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_USERNAME,
                StringConstants.PREF_LBL_USERNAME, 1, false);
        addStringFieldEditor(group, PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PASSWORD,
                StringConstants.PREF_LBL_PASSWORD, 1, false);

        Composite spacer = SWTFactory.createComposite(group, 2, 2, GridData.FILL_HORIZONTAL);

        FieldEditor fieldEditor = new ComboFieldEditor(
                PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SECURITY_PROTOCOL,
                StringConstants.PREF_LBL_SECURITY_PROTOCOL, MailUtil.getMailSecurityProtocolTypeArrayValues(), spacer);
        fieldEditor.fillIntoGrid(spacer, fieldEditor.getNumberOfControls());
        addField(fieldEditor);

        group = SWTFactory.createGroup(comp, StringConstants.PREF_GROUP_LBL_EXECUTION_MAIL, 2, 2, GridData.FILL_BOTH);
        addStringFieldEditor(group, PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_REPORT_RECIPIENTS,
                StringConstants.PREF_LBL_REPORT_RECIPIENTS, 2, false);
        addStringFieldEditor(group, PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SIGNATURE,
                StringConstants.PREF_LBL_SIGNATURE, 2, true);
        spacer = SWTFactory.createComposite(group, 2, 2, GridData.FILL_HORIZONTAL);

        fieldEditor = new BooleanFieldEditor(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_ATTACHMENT,
                StringConstants.PREF_LBL_SEND_ATTACHMENT, spacer);
        fieldEditor.fillIntoGrid(spacer, fieldEditor.getNumberOfControls());
        addField(fieldEditor);

        initialize();
        checkState();

        return comp;
    }

    protected void addStringFieldEditor(Composite parent, String preferenceName, String preferenceLabelText, int hspan,
            boolean isMulti) {
        FieldEditor fieldEditor = null;
        Composite spacer = SWTFactory.createComposite(parent, 2, hspan, GridData.FILL_HORIZONTAL);
        if (isMulti) {
            fieldEditor = new MultiLineStringFieldEditor(preferenceName, preferenceLabelText,
                    MultiLineStringFieldEditor.UNLIMITED, 60, spacer);
        } else {
            fieldEditor = new StringFieldEditor(preferenceName, preferenceLabelText, spacer);
        }
        fieldEditor.fillIntoGrid(spacer, fieldEditor.getNumberOfControls());
        addField(fieldEditor);
    }

    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }
}
