package com.kms.katalon.composer.integration.qtest.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.composer.integration.qtest.dialog.model.TestSuiteParentCreationOption;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class QTestPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

    public static final String QTEST_PREFERENCES_QUALIFIER = "com.kms.katalon.composer.integration.qtest";

    public static final String QTEST_TESTSUITE_CREATION_OPTION = "testSuiteCreationOption";

    @Override
    public void initializeDefaultPreferences() {
        // TODO Auto-generated method stub

        getStore().setDefault(QTEST_TESTSUITE_CREATION_OPTION,
                TestSuiteParentCreationOption.CREATE_UPLOAD_AND_SET_AS_DEFAULT.name());
    }

    public static IPreferenceStore getStore() {
        return (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE, QTEST_PREFERENCES_QUALIFIER);
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
