package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;

public abstract class CommandHandler extends AbstractHandler {

    protected IWorkbench getActiveWorkbench() {
        return PlatformUI.getWorkbench();
    }

    protected IWorkbenchWindow getActiveWorkbenchWindow() {
        return getActiveWorkbench().getActiveWorkbenchWindow();
    }

    protected IEclipseContext getWorkbenchContext() {
        return (IEclipseContext) getActiveWorkbench().getService(IEclipseContext.class);
    }
}
