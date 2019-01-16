package com.kms.katalon.platform.internal.ui;

import com.kms.katalon.composer.components.services.UISynchronizeService;

public class UISynchronizeServiceImpl implements com.katalon.platform.api.ui.UISynchronizeService {

    public void asyncExec(Runnable runnable) {
        UISynchronizeService.asyncExec(runnable);
    }

    public void syncExec(Runnable runnable) {
        UISynchronizeService.syncExec(runnable);
    }
}
