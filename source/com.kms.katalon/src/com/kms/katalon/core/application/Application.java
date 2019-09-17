package com.kms.katalon.core.application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleException;

import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.console.addons.MacOSAddon;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.support.testing.katserver.KatServer;
import com.kms.katalon.util.ApplicationSession;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * This class controls all aspects of the application's execution
 */

public class Application implements IApplication {
    private static final String INVALID_RUNNING_MODE = "Invalid running mode.";

    public static final String RUN_MODE_OPTION = "runMode";

    public static final String RUN_MODE_OPTION_CONSOLE = "console";

    private static final Object RUN_MODE_OPTION_SELFTEST = "selfTest";

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
     * IApplicationContext)
     */
    @Override
    public Object start(IApplicationContext context) {

        LocalDate now = LocalDate.now();
        LocalDate expiredDate = LocalDate.of(2019, 11, 1);
        if (now.isAfter(expiredDate)) {
            LogUtil.logInfo("This beta build has expired");
            return IApplication.EXIT_OK;
        }

        if (!activeLoggingBundle()) {
            return IApplication.EXIT_OK;
        }

        if (!activateInternalPlatformBundle()) {
            return IApplication.EXIT_OK;
        }

        try {
            preRunInit();
        } catch (Error e) {
            resolve();
        }
        final Map<?, ?> args = context.getArguments();
        final String[] appArgs = (String[]) args.get(IApplicationContext.APPLICATION_ARGS);
        RunningModeParam runningModeParam = getRunningModeParamFromParam(parseOption(appArgs));

        switch (runningModeParam) {
            case CONSOLE:
                return runConsole(context, appArgs);
            case SELFTEST:
                return runSelfTest();
            case GUI:
                return runGUI();
            default:
                System.out.println(INVALID_RUNNING_MODE);
                return IApplication.EXIT_OK;
        }

    }

    private Object runConsole(IApplicationContext context, final String[] appArgs) {
        try {
            // hide splash screen
            context.applicationRunning();
            ApplicationRunningMode.setRunningMode(RunningMode.CONSOLE);
            return com.kms.katalon.console.application.Application.runConsole(appArgs);
        } catch (Error e) {
            LogUtil.logError(e);
            return resolve();
        }
    }

    private void preRunInit() {
        Location instanceLoc = Platform.getInstanceLocation();
        if (instanceLoc.getURL() == null) {
            try {
                File newLocationFile = getWorkspaceFile();
                if (!newLocationFile.exists()) {
                    newLocationFile.mkdirs();
                }
                instanceLoc.set(new URL(newLocationFile.toURI().toURL().toString().replaceAll("%20", " ")), false);

                LogUtil.printOutputLine(
                        "Katalon workspace folder is set to default location: " + newLocationFile.getAbsolutePath());
            } catch (IllegalStateException | IOException | URISyntaxException ex) {
                LogUtil.logError(ex);
                return;
            }
        } else {
            LogUtil.printOutputLine("Katalon workspace folder is set custom to: " + instanceLoc.getURL().toString());
        }

        ApplicationSession.clean();
        MacOSAddon.initMacOSConfig();
        ApplicationInfo.setAppInfoIntoUserHomeDir();
    }

    private File getWorkspaceFile() throws URISyntaxException {
        File installLocation = new File(Platform.getInstallLocation().getURL().getPath());
        String configRelativePath = Platform.OS_MACOSX.equals(Platform.getOS()) ? "../MacOS/config" : "config";
        if (Platform.inDevelopmentMode()) {
            configRelativePath += "-dev";
        }
        return new File(installLocation.getAbsolutePath(), configRelativePath);
    }

    private OptionSet parseOption(final String[] appArgs) {
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();
        parser.accepts(RUN_MODE_OPTION).withRequiredArg().ofType(String.class);
        OptionSet options = parser.parse(appArgs);
        return options;
    }

    private int runSelfTest() {
        ApplicationSingleton.getInstance().enableServerMode();
        new KatServer().start();
        return runGUI();
    }

    private int runGUI() {
        ApplicationRunningMode.setRunningMode(RunningMode.GUI);
        int returnCode = internalRunGUI();
        if (returnCode == PlatformUI.RETURN_RESTART) {
            return IApplication.EXIT_RESTART;
        }
        return IApplication.EXIT_OK;
    }

    private int internalRunGUI() {
        Display display = PlatformUI.createDisplay();
        try {
            return PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
        } catch (Exception e) {
            LogUtil.logError(e);
        } catch (Error e) {
            LogUtil.logError(e);
            return resolve();
        } finally {
            ApplicationSession.close();
            display.dispose();
        }
        return PlatformUI.RETURN_OK;
    }

    private int resolve() {
        MetadataCorruptedResolver resolver = new MetadataCorruptedResolver();
        if (!resolver.isMetaFolderCorrupted()) {
            return PlatformUI.RETURN_UNSTARTABLE;
        }
        return resolver.resolve() ? PlatformUI.RETURN_RESTART : PlatformUI.RETURN_UNSTARTABLE;
    }

    public enum RunningModeParam {
        GUI, CONSOLE, INVALID, SELFTEST
    }

    private RunningModeParam getRunningModeParamFromParam(OptionSet options) {
        if (!options.has(RUN_MODE_OPTION)) {
            return RunningModeParam.GUI;
        }
        if (RUN_MODE_OPTION_CONSOLE.equals(options.valueOf(RUN_MODE_OPTION))) {
            return RunningModeParam.CONSOLE;
        }
        if (RUN_MODE_OPTION_SELFTEST.equals(options.valueOf(RUN_MODE_OPTION))) {
            return RunningModeParam.SELFTEST;
        }
        return RunningModeParam.INVALID;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
    public void stop() {
        if (!PlatformUI.isWorkbenchRunning()) {
            return;
        }
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            @Override
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

    private boolean activateInternalPlatformBundle() {
        try {
            Platform.getBundle("com.kms.katalon.platform.internal").start();
            return true;
        } catch (BundleException ex) {
            return false;
        }
    }
}
