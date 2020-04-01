package com.kms.katalon.application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.kms.katalon.application.KatalonApplication;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.logging.LogUtil;

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

        createLicenseFolder();
        
        if (!activeLoggingBundle()) {
            return IApplication.EXIT_OK;
        }

        if (!activateInternalPlatformBundle()) {
            return IApplication.EXIT_OK;
        }

        try {
            LogUtil.printOutputLine("Before preRunInit()");
            preRunInit();
            LogUtil.printOutputLine("After preRunInit()");
        } catch (Error e) {
            LogUtil.printOutputLine("Before resolve()");
            resolve();
            LogUtil.printOutputLine("After resolve()");
        }
        final Map<?, ?> args = context.getArguments();
        final String[] appArgs = (String[]) args.get(IApplicationContext.APPLICATION_ARGS);
        RunningModeParam runningModeParam = getRunningModeParamFromParam(parseOption(appArgs));

        if (isKSRE()) {
            runningModeParam = RunningModeParam.CONSOLE;
        }
        switch (runningModeParam) {
            case CONSOLE:
                System.out.println("runningModeParam = CONSOLE");
                try {
                    Bundle consoleBundle = Platform.getBundle("com.kms.katalon.console");
                    if (consoleBundle == null) {
                        System.out.println(INVALID_RUNNING_MODE);
                        return IApplication.EXIT_OK;
                    }
                    consoleBundle.start();
                } catch (BundleException e) {
                    return IApplication.EXIT_OK;
                }
                return runConsole(context, appArgs);
            case SELFTEST:
                return runSelfTest();
            case GUI:
                try {
                    Bundle composerKatalonBundle = Platform.getBundle(IdConstants.KATALON_GENERAL_BUNDLE_ID);
                    if (composerKatalonBundle == null) {
                        System.out.println(INVALID_RUNNING_MODE);
                        return IApplication.EXIT_OK;
                    }
                    composerKatalonBundle.start();
                } catch (BundleException e) {
                    LogUtil.logError(e);
                }
                return runGUI(appArgs);
            default:
                System.out.println(INVALID_RUNNING_MODE);
                return IApplication.EXIT_OK;
        }

    }

    private void resolve() {
        MetadataCorruptedResolver resolver = new MetadataCorruptedResolver();
        resolver.resolve();
    }

    private boolean isKSRE() {
        return Platform.getProduct().getId().equals("com.kms.katalon.console.product");
    }

    private Object runConsole(IApplicationContext context, final String[] appArgs) {
        try {
            // hide splash screen
            context.applicationRunning();
            ApplicationRunningMode.setRunningMode(RunningMode.CONSOLE);
            return KatalonApplicationActivator.getInstance().getApplicationStarters().get(RunningMode.CONSOLE).start(appArgs);
        } catch (Error e) {
            LogUtil.logError(e);
            return IApplication.EXIT_OK;
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

        LogUtil.printOutputLine("Before MacOSAddon.initMacOSConfig()");
        MacOSAddon.initMacOSConfig();
        LogUtil.printOutputLine("Before ApplicationInfo.setAppInfoIntoUserHomeDir()");
        ApplicationInfo.setAppInfoIntoUserHomeDir();
    }

    private File getWorkspaceFile() throws URISyntaxException {
        if (isKSRE()) {
            File tempParentFolder = FileUtils.getTempDirectory();
            File workSpaceFile = new File(tempParentFolder, "/session-" + KatalonApplication.SESSION_ID.substring(0, 8));
            workSpaceFile.mkdirs();
            return workSpaceFile;
        } else {
            File installLocation = new File(Platform.getInstallLocation().getURL().getPath());
            String configRelativePath = Platform.OS_MACOSX.equals(Platform.getOS()) ? "../MacOS/config" : "config";
            if (Platform.inDevelopmentMode()) {
                configRelativePath += "-dev";
            }
            return new File(installLocation.getAbsolutePath(), configRelativePath);
        }
    }

    private OptionSet parseOption(final String[] appArgs) {
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();
        parser.accepts(RUN_MODE_OPTION).withRequiredArg().ofType(String.class);
        OptionSet options = parser.parse(appArgs);
        return options;
    }

    private int runSelfTest() {
//        ApplicationSingleton.getInstance().enableServerMode();
//        new KatServer().start();
//        return runGUI();
        return 0;
    }

    private int runGUI(String[] arguments) {
        ApplicationRunningMode.setRunningMode(RunningMode.GUI);
        return KatalonApplicationActivator.getInstance().getApplicationStarters().get(RunningMode.GUI).start(arguments);
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
        KatalonApplicationActivator.getInstance().getApplicationStarters().get(ApplicationRunningMode.get()).stop();
        try {
            ResourcesPlugin.getWorkspace().save(false, new NullProgressMonitor());
        } catch (CoreException e) {
            LogUtil.logError(e);
        }
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
            Platform.getBundle(IdConstants.KATALON_INTERNAL_PLATFORM_BUNDLE_ID).start();
            return true;
        } catch (BundleException ex) {
            return false;
        }
    }
    
    private void createLicenseFolder() {
        try {
            File licenseFolder = new File(ApplicationInfo.userDirLocation(), "license");
            if (!licenseFolder.exists()) {
                licenseFolder.mkdir();
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }
}
