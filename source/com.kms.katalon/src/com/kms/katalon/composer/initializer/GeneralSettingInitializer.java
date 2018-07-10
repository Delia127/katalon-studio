package com.kms.katalon.composer.initializer;

import com.kms.katalon.composer.preferences.GeneralPreferenceDefaultValueInitializer;

public class GeneralSettingInitializer implements ApplicationInitializer {

    @Override
    public void setup() {
        GeneralPreferenceDefaultValueInitializer defaultValueInitializer = new GeneralPreferenceDefaultValueInitializer();
        defaultValueInitializer.applyDefaultValues();
    }

}
