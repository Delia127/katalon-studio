package com.kms.katalon.composer.integration.providers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kms.katalon.composer.integration.analytics.dialog.KatalonTestOpsIntegrationDialog;
import com.kms.katalon.feature.TestOpsConfiguration;
import com.kms.katalon.integration.analytics.entity.TestOpsMessage;

public class TestOpsConfigurationImpl implements TestOpsConfiguration{

    @Override
    public void testOpsQuickIntergration() {
        KatalonTestOpsIntegrationDialog quickStartDialog = new KatalonTestOpsIntegrationDialog(Display.getCurrent().getActiveShell());
        if (quickStartDialog.checkConnection()) {
             quickStartDialog.open();
        }
    }

    @Override
    public String getTestOpsMessage(String message) {
        Gson gson = new GsonBuilder().create();
        TestOpsMessage testOpsMessage = gson.fromJson(message, TestOpsMessage.class);
        
        String reponseMessage = "";
        if (!StringUtils.isEmpty(testOpsMessage.getMessage())) {
            reponseMessage = testOpsMessage.getMessage();
        } else if (!StringUtils.isEmpty(testOpsMessage.getError_description())) {
            reponseMessage = testOpsMessage.getError_description();
        } else {
            reponseMessage = testOpsMessage.getError();
        }
        return reponseMessage;
    }
}
