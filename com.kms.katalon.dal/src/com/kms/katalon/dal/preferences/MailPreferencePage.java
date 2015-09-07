package com.kms.katalon.dal.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.dal.constants.StringConstants;

public class MailPreferencePage extends FieldEditorPreferencePage {
	
	private static final String HOST_LABEL = StringConstants.PREF_LBL_HOST;
	private static final String PORT_LABEL = StringConstants.PREF_LBL_PORT;
	private static final String USERNAME_LABEL = StringConstants.PREF_LBL_USERNAME;
	private static final String PASSWORD_LABEL = StringConstants.PREF_LBL_PASSWORD;
	private static final String MAIL_CONFIG_REPORT_RECIPIENTS_LABEL = StringConstants.PREF_LBL_REPORT_RECIPIENTS;
	private static final String SIGNATURE = StringConstants.PREF_LBL_SIGNATURE;
	private static final String SEND_ATTACHMENT = StringConstants.PREF_LBL_SEND_ATTACHMENT;

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_HOST, HOST_LABEL,
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_PORT, PORT_LABEL,
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_USERNAME, USERNAME_LABEL,
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_PASSWORD, PASSWORD_LABEL,
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_REPORT_RECIPIENTS, MAIL_CONFIG_REPORT_RECIPIENTS_LABEL,
				getFieldEditorParent()));		
		addField(new StringFieldEditor(PreferenceConstants.DALPreferenceConstans.MAIL_CONFIG_SIGNATURE, SIGNATURE,
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.DALPreferenceConstans.MAIL_ATTACHMENT, SEND_ATTACHMENT, getFieldEditorParent()));
	}

}
