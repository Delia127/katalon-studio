package com.kms.katalon.platform.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.platform.internal.service.impl.CommonDialogsImpl;
import com.kms.katalon.platform.internal.service.impl.FolderManagerImpl;
import com.kms.katalon.platform.internal.service.impl.ProjectManagerImpl;
import com.kms.katalon.platform.internal.service.impl.TestCaseManagerImpl;
import com.kms.katalon.platform.internal.service.impl.UISynchronizeServiceImpl;

public class Activator implements BundleActivator {
    
    private static BundleContext context;
    
    static BundleContext getContext() {
        return context;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        Activator.context = context;
        ApplicationManager.setProjectManager(new ProjectManagerImpl());
        ApplicationManager.setFolderManager(new FolderManagerImpl());
        ApplicationManager.setTestCaseManager(new TestCaseManagerImpl());
        ApplicationManager.setCommonDialogs(new CommonDialogsImpl());
        ApplicationManager.setUISynchronizeService(new UISynchronizeServiceImpl());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        Activator.context = null;
    }

}
