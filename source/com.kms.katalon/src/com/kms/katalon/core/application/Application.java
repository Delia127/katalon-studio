package com.kms.katalon.core.application;

import static org.eclipse.ui.PlatformUI.getPreferenceStore;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleException;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants.IPluginPreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.application.ApplicationRunningMode.RunningMode;
import com.kms.katalon.execution.launcher.manager.ConsoleMain;

/**
 * This class controls all aspects of the application's execution
 */

public class Application implements IApplication {
    private static final String CONSOLE_RUNNING_MODE_ARGUMENT = "-runMode=console";

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app. IApplicationContext)
     */
    public Object start(IApplicationContext context) {
        if (!activeLoggingBundle()) {
            return IApplication.EXIT_OK;
        }
        
        final Map<?, ?> args = context.getArguments();
        final String[] appArgs = (String[]) args.get("application.args");
        boolean isConsoleMode = false;
        for (final String arg : appArgs) {
            if (arg.toLowerCase().equals(CONSOLE_RUNNING_MODE_ARGUMENT.toLowerCase())) {
                ApplicationRunningMode runningMode = ApplicationRunningMode.getInstance();
                runningMode.setRunnningMode(RunningMode.Console);
                runningMode.setRunArguments(appArgs);
                isConsoleMode = true;
                break;
            }
        }

        Display display = PlatformUI.createDisplay();
        try {
            int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
            if (isConsoleMode) {
                System.setProperty(IApplicationContext.EXIT_DATA_PROPERTY, "");
                return ConsoleMain.getReturnCode();
            }
            if (returnCode == PlatformUI.RETURN_RESTART) return IApplication.EXIT_RESTART;
            else return IApplication.EXIT_OK;
        } catch (Exception e) {
            e.printStackTrace();
            return IApplication.EXIT_OK;
        } finally {
            clearSession(!isConsoleMode);
            display.dispose();
        }
    }

    private void clearSession(boolean isNotConsoleMode) {
        if (isNotConsoleMode
                && !getPreferenceStore().getBoolean(
                        IPluginPreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION)) {
            // Clear workbench layout
            File workbenchXmi = new File(Platform.getLocation().toString()
                    + "/.metadata/.plugins/org.eclipse.e4.workbench/workbench.xmi");
            if (workbenchXmi.exists()) {
                workbenchXmi.delete();
            }

            // Clear working state of recent projects
            ProjectController.getInstance().clearWorkingStateOfRecentProjects();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop() {
        if (!PlatformUI.isWorkbenchRunning()) return;
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                if (!display.isDisposed()) workbench.close();
            }
        });
    }
    
    private boolean activeLoggingBundle() {
        try {
            Platform.getBundle(IdConstants.KATALON_LOGGING_BUNDLE_ID).start();
            return true;
        } catch (BundleException ex) {
            return false;
        }
    }
}
