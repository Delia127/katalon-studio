package com.kms.katalon.composer.initializer;

import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class DefaultLineNumberInitializer implements ApplicationInitializer {

	@Override
	public void setup() {
		ScopedPreferenceStore store = PreferenceStoreManager.getPreferenceStore(IdConstants.EDITORS_ID);
		boolean isFirstTimeSetup = store.getBoolean(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED);

		if (isFirstTimeSetup || store.getBoolean(PreferenceConstants.LINE_NUMBER_RULER)) {
			return;
		}
		store.setValue(PreferenceConstants.LINE_NUMBER_RULER, true);
		store.setValue(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED, true);
		try {
			store.save();
		} catch (IOException e) {
			LoggerSingleton.logError(e);
		}
	}
}
