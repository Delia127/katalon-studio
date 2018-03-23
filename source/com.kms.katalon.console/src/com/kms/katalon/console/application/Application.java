package com.kms.katalon.console.application;

import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleException;

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.console.addons.MacOSAddon;
import com.kms.katalon.console.constants.ConsoleMessageConstants;
import com.kms.katalon.console.constants.ConsoleStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.custom.addon.CustomBundleActivator;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.logging.LogUtil;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * This class controls all aspects of the console application's execution
 */
public class Application implements IApplication {
    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
     * IApplicationContext)
     */
    @Override
    public Object start(IApplicationContext context) {
        if (!activeLoggingBundle()) {
            return IApplication.EXIT_OK;
        }

        preRunInit();
        final Map<?, ?> args = context.getArguments();
        final String[] appArgs = (String[]) args.get(IApplicationContext.APPLICATION_ARGS);
        return runConsole(appArgs);

    }

    private void preRunInit() {
        MacOSAddon.initMacOSConfig();
        ApplicationInfo.setAppInfoIntoUserHomeDir();
        initEnvironment();
    }

    protected void initEnvironment() {
        // Call this to initialize com.kms.katalon.custom project in order to populate KeywordContributorCollection list
        CustomBundleActivator.class.getName();
    }

    public static int runConsole(String[] arguments) {
        // Set this to allow application to return it's own exit code instead of Eclipse's exit code
        System.setProperty(IApplicationContext.EXIT_DATA_PROPERTY, "");
        try {
            if (!VersionUtil.isInternalBuild() && !checkConsoleActivation(arguments)) {
                return LauncherResult.RETURN_CODE_PASSED;
            }
            return ConsoleMain.launch(arguments);
        } catch (Exception e) {
            LogUtil.printAndLogError(e, ConsoleMessageConstants.ERR_CONSOLE_MODE);
            return LauncherResult.RETURN_CODE_ERROR;
        }
    }

    public static boolean checkConsoleActivation(String[] arguments) {
        if (ActivationInfoCollector.isActivated()) {
            return true;
        }
        String[] emailPass = getEmailAndPassword(arguments);
        String email = emailPass[0], password = emailPass[1];
        StringBuilder errorMessage = new StringBuilder();
        if (email == null || password == null || !ActivationInfoCollector.activate(email, password, errorMessage)) {
            LogUtil.printErrorLine(email == null || password == null ? ConsoleMessageConstants.KATALON_NOT_ACTIVATED
                    : errorMessage.toString());
            return false;
        }
        return true;
    }

    private static String[] getEmailAndPassword(String[] arguments) {
        OptionParser parser = new OptionParser(false);
        parser.allowsUnrecognizedOptions();
        parser.accepts(ConsoleStringConstants.ARG_EMAIL).withRequiredArg().ofType(String.class);
        parser.accepts(ConsoleStringConstants.ARG_PASSWORD).withRequiredArg().ofType(String.class);

        OptionSet argumentSet = parser.parse(arguments);
        String[] emailPass = { null, null };
        if (argumentSet.has(ConsoleStringConstants.ARG_EMAIL)) {
            emailPass[0] = argumentSet.valueOf(ConsoleStringConstants.ARG_EMAIL).toString();
        }
        if (argumentSet.has(ConsoleStringConstants.ARG_PASSWORD)) {
            emailPass[1] = argumentSet.valueOf(ConsoleStringConstants.ARG_PASSWORD).toString();
        }
        return emailPass;
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
    public void stop() {
        // Do nothing for this
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
