package com.kms.katalon.platform.internal.controller;

import java.util.HashMap;
import java.util.Map;

import com.katalon.platform.api.controller.Controller;
import com.katalon.platform.api.controller.FeatureFileController;
import com.katalon.platform.api.controller.FolderController;
import com.katalon.platform.api.controller.ReportController;
import com.katalon.platform.api.controller.RequestController;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.controller.TestExecutionController;
import com.katalon.platform.api.controller.TestSuiteCollectionController;
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
        lookup.put(FeatureFileController.class.getName(), new FeatureFileControllerImpl());
        lookup.put(TestSuiteCollectionController.class.getName(), new TestSuiteCollectionControllerImpl());
        lookup.put(TestExecutionController.class.getName(), new TestExecutionControllerImpl());
        lookup.put(RequestController.class.getName(), new RequestControllerImpl());
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
