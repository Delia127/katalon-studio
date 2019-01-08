package com.kms.katalon.platform.internal.ui;

import java.util.HashMap;
import java.util.Map;

import com.katalon.platform.api.service.UIServiceManager;
import com.katalon.platform.api.ui.DialogActionService;
import com.katalon.platform.api.ui.TestExplorerActionService;
import com.katalon.platform.api.ui.UIService;
import com.katalon.platform.api.ui.UISynchronizeService;

public class UIServiceManagerImpl implements UIServiceManager {
    private final Map<String, UIService> lookup;

    {
        lookup = new HashMap<>();
        lookup.put(DialogActionService.class.getName(), new DialogServiceImpl());
        lookup.put(UISynchronizeService.class.getName(), new UISynchronizeServiceImpl());
        lookup.put(TestExplorerActionService.class.getName(), new TestExplorerActionServiceImpl());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends UIService> T getService(Class<T> clazz) {
        String className = clazz.getName();
        if (lookup.containsKey(className)) {
            return (T) lookup.get(className);
        }
        return null;
    }

}
