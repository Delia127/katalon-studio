package com.kms.katalon.engine.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.engine.constants.StringConstants;

public class HostPreferencePage extends FieldEditorPreferencePage {
	
	private static final String HOST_IP_LABEL = StringConstants.PREF_LBL_HOST_IP;
	private static final String HOST_NAME_LABEL = StringConstants.PREF_LBL_HOST_NAME;
	
	public HostPreferencePage() {
		super(GRID);
	}

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.EnginePreferenceConstants.HOST_CONFIG_HOST_IP, HOST_IP_LABEL,
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.EnginePreferenceConstants.HOST_CONFIG_HOST_NAME, HOST_NAME_LABEL,
				getFieldEditorParent()));
	}

}
