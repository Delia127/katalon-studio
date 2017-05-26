package com.kms.katalon.composer.execution.debug;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupService;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

@SuppressWarnings("restriction")
public class CustomSourceLookupService extends SourceLookupService {

    public CustomSourceLookupService(IWorkbenchWindow window) {
        super(window);
    }

    @Override
    public void displaySource(Object context, IWorkbenchPage page, boolean forceSourceLookup) {
        if (context instanceof IAdaptable && context instanceof JDIStackFrame) {
            CustomSourceLookupFacility.getDefault().displaySource(context, page, forceSourceLookup);

        }
    }
}
