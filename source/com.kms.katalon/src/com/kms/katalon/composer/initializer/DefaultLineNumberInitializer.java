package com.kms.katalon.composer.initializer;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class DefaultLineNumberInitializer implements ApplicationInitializer {

	@Override
	public void setup() {
		ScopedPreferenceStore store = PreferenceStoreManager.getPreferenceStore(IdConstants.EDITORS_ID);

        if (store.getBoolean(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED)) {
            return;
        }
		if (store.getBoolean(PreferenceConstants.LINE_NUMBER_RULER)) {
			return;
		} else {
			store.setValue(PreferenceConstants.LINE_NUMBER_RULER, true);
			store.setValue(PreferenceConstants.PREF_FIRST_TIME_SETUP_COMPLETED, false);
		}
		return;
	}
}
