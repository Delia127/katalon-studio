package com.kms.katalon.platform.internal.service.impl;

import com.katalon.platform.api.service.UISynchronizeService;

public class UISynchronizeServiceImpl implements UISynchronizeService {

    @Override
    public void syncExec(Runnable runnable) {
        com.kms.katalon.composer.components.services.UISynchronizeService.syncExec(runnable);
    }

    @Override
    public void asyncExec(Runnable runnable) {
        com.kms.katalon.composer.components.services.UISynchronizeService.asyncExec(runnable);
    }
    

}
