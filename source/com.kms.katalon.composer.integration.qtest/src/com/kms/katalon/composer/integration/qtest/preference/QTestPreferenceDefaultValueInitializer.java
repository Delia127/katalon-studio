package com.kms.katalon.composer.integration.qtest.preference;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.composer.integration.qtest.dialog.model.TestSuiteParentCreationOption;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class QTestPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final String QTEST_TESTSUITE_CREATION_OPTION = "testSuiteCreationOption";

    @Override
    public void initializeDefaultPreferences() {
        getStore().setDefault(QTEST_TESTSUITE_CREATION_OPTION,
                TestSuiteParentCreationOption.CREATE_UPLOAD_AND_SET_AS_DEFAULT.name());
    }

    public static ScopedPreferenceStore getStore() {
        return getPreferenceStore(QTestPreferenceDefaultValueInitializer.class);
    }

    public static TestSuiteParentCreationOption getCreationOption() {
        String creationValue = getStore().getString(QTEST_TESTSUITE_CREATION_OPTION);
        if (creationValue == null || creationValue.isEmpty()) {
            return TestSuiteParentCreationOption.CREATE_ONLY;
        }

        return TestSuiteParentCreationOption.valueOf(creationValue);
    }

    public static void setCreationOption(TestSuiteParentCreationOption creationOption) {
        getStore().setValue(QTEST_TESTSUITE_CREATION_OPTION, creationOption.name());
    }

}
