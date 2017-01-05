package com.kms.katalon.core.application;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.AbstractStatusHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.statushandlers.WorkbenchErrorHandler;

import com.kms.katalon.constants.IdConstants;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
    private static final String GREPCLIPSE_PACKAGE_NAME_PREFIX = "org.codehaus.groovy.eclipse";

    /**
     * The workbench error handler.
     */
    private AbstractStatusHandler workbenchErrorHandler;

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    public String getInitialWindowPerspectiveId() {
        return IdConstants.KEYWORD_PERSPECTIVE_ID;
    }

    @Override
    public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        IDE.registerAdapters();
        configurer.setSaveAndRestore(true);
    }

    @Override
    public synchronized AbstractStatusHandler getWorkbenchErrorHandler() {
        if (workbenchErrorHandler != null) {
            return workbenchErrorHandler;
        }
        workbenchErrorHandler = new WorkbenchErrorHandler() {
            @Override
            public void handle(StatusAdapter statusAdapter, int style) {
                if (isGroovyEditorError(statusAdapter)) {
                    handleGroovyEditorError(statusAdapter);
                    return;
                }
                super.handle(statusAdapter, style);
            }

            private void handleGroovyEditorError(StatusAdapter statusAdapter) {
                // Only log down errors, not showing in dialog
                super.handle(statusAdapter, StatusManager.LOG);
            }

            private boolean isGroovyEditorError(StatusAdapter statusAdapter) {
                IStatus status = statusAdapter.getStatus();
                if (!(status.getException() instanceof StringIndexOutOfBoundsException)) {
                    return false;
                }
                StackTraceElement[] stackTraces = status.getException().getStackTrace();
                if (stackTraces.length < 2) {
                    return false;
                }
                return stackTraces[1].getClassName().startsWith(GREPCLIPSE_PACKAGE_NAME_PREFIX);
            }
        };
        return workbenchErrorHandler;
    }
}
