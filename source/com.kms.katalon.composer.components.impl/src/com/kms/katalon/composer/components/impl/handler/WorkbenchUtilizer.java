package com.kms.katalon.composer.components.impl.handler;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class WorkbenchUtilizer {
    protected static IWorkbench getActiveWorkbench() {
        return PlatformUI.getWorkbench();
    }

    protected static IWorkbenchWindow getActiveWorkbenchWindow() {
        return getActiveWorkbench().getActiveWorkbenchWindow();
    }

    protected static IEclipseContext getWorkbenchContext() {
        return getService(IEclipseContext.class);
    }
    
    public static <T> T getService(Class<? extends T> clazz) {
        return getActiveWorkbench().getService(clazz);
    }
}
