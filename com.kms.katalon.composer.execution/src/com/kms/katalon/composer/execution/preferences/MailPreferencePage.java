package com.kms.katalon.composer.execution.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.execution.util.MailUtil;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class MailPreferencePage extends FieldEditorPreferencePage {

    private static final String HOST_LABEL = StringConstants.PREF_LBL_HOST;
    private static final String SECURITY_PROTOCOL_LABEL = StringConstants.PREF_LBL_SECURITY_PROTOCOL;
    private static final String PORT_LABEL = StringConstants.PREF_LBL_PORT;
    private static final String USERNAME_LABEL = StringConstants.PREF_LBL_USERNAME;
    private static final String PASSWORD_LABEL = StringConstants.PREF_LBL_PASSWORD;
    private static final String MAIL_CONFIG_REPORT_RECIPIENTS_LABEL = StringConstants.PREF_LBL_REPORT_RECIPIENTS;
    private static final String SIGNATURE = StringConstants.PREF_LBL_SIGNATURE;
    private static final String SEND_ATTACHMENT = StringConstants.PREF_LBL_SEND_ATTACHMENT;

    @Override
    protected void createFieldEditors() {
        addField(new StringFieldEditor(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_HOST, HOST_LABEL,
                getFieldEditorParent()));
        addField(new ComboFieldEditor(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SECURITY_PROTOCOL,
                SECURITY_PROTOCOL_LABEL, MailUtil.getMailSecurityProtocolTypeArrayValues(), getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PORT, PORT_LABEL,
                getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_USERNAME,
                USERNAME_LABEL, getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_PASSWORD,
                PASSWORD_LABEL, getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_REPORT_RECIPIENTS,
                MAIL_CONFIG_REPORT_RECIPIENTS_LABEL, getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_SIGNATURE, SIGNATURE,
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(PreferenceConstants.ExecutionPreferenceConstans.MAIL_CONFIG_ATTACHMENT, SEND_ATTACHMENT,
                getFieldEditorParent()));
        adjustGridLayout();
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
    }
}
