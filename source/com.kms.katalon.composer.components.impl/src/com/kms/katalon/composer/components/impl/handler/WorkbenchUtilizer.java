package com.kms.katalon.composer.components.impl.handler;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class WorkbenchUtilizer {
    protected IWorkbench getActiveWorkbench() {
        return PlatformUI.getWorkbench();
    }

    protected IWorkbenchWindow getActiveWorkbenchWindow() {
        return getActiveWorkbench().getActiveWorkbenchWindow();
    }

    protected IEclipseContext getWorkbenchContext() {
        return getService(IEclipseContext.class);
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T getService(Class<? extends T> clazz) {
        return (T) getActiveWorkbench().getService(clazz);
    }
}
