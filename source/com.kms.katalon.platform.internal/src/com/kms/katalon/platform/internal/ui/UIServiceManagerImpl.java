package com.kms.katalon.platform.internal.ui;

import java.util.HashMap;
import java.util.Map;

import com.katalon.platform.api.service.UIServiceManager;
import com.katalon.platform.api.ui.DialogHelper;
import com.katalon.platform.api.ui.UIService;

public class UIServiceManagerImpl implements UIServiceManager {
    private final Map<String, UIService> lookup;

    {
        lookup = new HashMap<>();
        lookup.put(DialogHelper.class.getName(), new DialogHelperImpl());
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
