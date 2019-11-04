package com.kms.katalon.composer.integration.kobiton.testsuite;

import com.kms.katalon.composer.mobile.execution.testsuite.MobileIntegrationProvider;
import com.kms.katalon.composer.mobile.execution.testsuite.MobileTestExecutionDriverEntry;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;

public class KobitonIntegrationProvider implements MobileIntegrationProvider {

    @Override
    public int getPreferedOrder() {
        return 0;
    }

    @Override
    public MobileTestExecutionDriverEntry getExecutionEntry(String groupName) {
        return KobitonPreferencesProvider.isKobitonIntegrationAvailable() ? new KobitonTestExecutionDriverEntry(groupName)
                : null;
    }

}
