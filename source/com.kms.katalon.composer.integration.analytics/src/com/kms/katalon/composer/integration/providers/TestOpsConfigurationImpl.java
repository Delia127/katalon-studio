package com.kms.katalon.composer.integration.providers;

import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.integration.analytics.dialog.KatalonTestOpsIntegrationDialog;
import com.kms.katalon.feature.TestOpsConfiguration;

public class TestOpsConfigurationImpl implements TestOpsConfiguration{

    @Override
    public void testOpsQuickIntergration() {
        KatalonTestOpsIntegrationDialog quickStartDialog = new KatalonTestOpsIntegrationDialog(Display.getCurrent().getActiveShell());
        if (quickStartDialog.checkConnection()) {
             quickStartDialog.open();
        }
    }
}
