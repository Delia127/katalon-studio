package com.kms.katalon.core.application;

import static org.eclipse.ui.PlatformUI.getPreferenceStore;

import java.io.File;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleException;

import com.kms.katalon.addons.MacOSAddon;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.util.ApplicationInfo;

/**
 * This class controls all aspects of the application's execution
 */

public class Application implements IApplication {
    private static final String INVALID_RUNNING_MODE = "Invalid running mode.";

    public static final String RUN_MODE_OPTION = "runMode";

    public static final String RUN_MODE_OPTION_CONSOLE = "console";

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
     * IApplicationContext)
     */
    public Object start(IApplicationContext context) {
        if (!activeLoggingBundle()) {
            return IApplication.EXIT_OK;
        }

        preRunInit();
        final Map<?, ?> args = context.getArguments();
        final String[] appArgs = (String[]) args.get(IApplicationContext.APPLICATION_ARGS);

        RunningModeParam runningModeParam = getRunningModeParamFromParam(parseOption(appArgs));
        switch (runningModeParam) {
            case CONSOLE:
                // hide splash screen
                context.applicationRunning();
                return runConsole(appArgs);
            case GUI:
                return runGUI();
            default:
                System.out.println(INVALID_RUNNING_MODE);
                return IApplication.EXIT_OK;
        }

    }

    private void preRunInit() {
        MacOSAddon.initDefaultJRE();
        ApplicationInfo.setAppInfoIntoUserHomeDir();
    }

    private OptionSet parseOption(final String[] appArgs) {
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();
        parser.accepts(RUN_MODE_OPTION).withRequiredArg().ofType(String.class);
        OptionSet options = parser.parse(appArgs);
        return options;
    }

    private int runGUI() {
        int returnCode = internalRunGUI();
        if (returnCode == PlatformUI.RETURN_RESTART) {
            return IApplication.EXIT_RESTART;
        }
        return IApplication.EXIT_OK;
    }

    private int runConsole(String[] arguments) {
        // Set this to allow application to return it's own exit code instead of Eclipse's exit code
        System.setProperty(IApplicationContext.EXIT_DATA_PROPERTY, "");
        try {
            return ConsoleMain.launch(arguments);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return LauncherResult.RETURN_CODE_ERROR;
        }
    }

    private int internalRunGUI() {
        Display display = PlatformUI.createDisplay();
        try {
            return PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clearSession();
            display.dispose();
        }
        return PlatformUI.RETURN_OK;
    }

    private enum RunningModeParam {
        GUI, CONSOLE, INVALID
    }

    private RunningModeParam getRunningModeParamFromParam(OptionSet options) {
        if (!options.has(RUN_MODE_OPTION)) {
            return RunningModeParam.GUI;
        }
        if (RUN_MODE_OPTION_CONSOLE.equals((String) options.valueOf(RUN_MODE_OPTION))) {
            return RunningModeParam.CONSOLE;
        }
        return RunningModeParam.INVALID;
    }

    private void clearSession() {
        if (getPreferenceStore().getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION)) {
            return;
        }
        // Clear workbench layout
        File workbenchXmi = new File(Platform.getLocation().toString()
                + "/.metadata/.plugins/org.eclipse.e4.workbench/workbench.xmi");
        if (workbenchXmi.exists()) {
            workbenchXmi.delete();
        }

        // Clear working state of recent projects
        ProjectController.getInstance().clearWorkingStateOfRecentProjects();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop() {
        if (!PlatformUI.isWorkbenchRunning())
            return;
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                if (!display.isDisposed())
                    workbench.close();
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
