package com.kms.katalon.engine.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.engine.constants.StringConstants;

public class RemoteCallPreferencePage extends FieldEditorPreferencePage {

	private static final String NUMBER_OF_REMOTE_CALL_LABEL = StringConstants.PREF_LBL_NUM_OF_REMOTE_CALL;
	private static final String IS_ALLOWED_LABEL = StringConstants.PREF_LBL_IS_ALLOWED;

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.EnginePreferenceConstants.REMOTE_CALL_IS_ALLOWED, IS_ALLOWED_LABEL,
				getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.EnginePreferenceConstants.REMOTE_CALL_NUMBER_OF_REMOTE_CALL, NUMBER_OF_REMOTE_CALL_LABEL,
				getFieldEditorParent()));
	}

}
