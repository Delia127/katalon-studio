package com.kms.katalon.platform.internal.controller;

import java.util.HashMap;
import java.util.Map;

import com.katalon.platform.api.controller.Controller;
import com.katalon.platform.api.controller.FolderController;
import com.katalon.platform.api.controller.ReportController;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.service.ControllerManager;
import com.kms.katalon.controller.ProjectController;

public class ControllerManagerImpl implements ControllerManager {
    private final Map<String, Controller> lookup;

    {
        lookup = new HashMap<>();
        lookup.put(FolderController.class.getName(), new FolderControllerImpl());
        lookup.put(TestCaseController.class.getName(), new TestCaseControllerImpl());
        lookup.put(ProjectController.class.getName(), new ProjectControllerImpl());
        lookup.put(ReportController.class.getName(), new ReportControllerImpl());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Controller> T getController(Class<T> clazz) {
        String className = clazz.getName();
        if (lookup.containsKey(className)) {
            return (T) lookup.get(className);
        }
        return null;
    }
}
