package com.kms.katalon.console.application;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleException;

import com.kms.katalon.console.addons.MacOSAddon;
import com.kms.katalon.console.constants.ConsoleMessageConstants;
import com.kms.katalon.console.utils.ActivationInfoCollector;
import com.kms.katalon.console.utils.ApplicationInfo;
import com.kms.katalon.console.utils.VersionUtil;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.custom.addon.CustomBundleActivator;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.launcher.result.LauncherResult;
import com.kms.katalon.logging.LogUtil;

/**
 * This class controls all aspects of the console application's execution
 */
public class Application implements IApplication {

    public static final String SESSION_ID;
    
    static {
        SESSION_ID = UUID.randomUUID().toString();
    }

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
            if (!VersionUtil.isInternalBuild() && !ActivationInfoCollector.checkConsoleActivation(arguments)) {
                return LauncherResult.RETURN_CODE_PASSED;
            }
            return ConsoleMain.launch(arguments);
        } catch (Exception e) {
            LogUtil.logError(e);
            System.out.println(ConsoleMessageConstants.ERR_CONSOLE_MODE + ": " + ExceptionUtils.getStackTrace(e));
            return LauncherResult.RETURN_CODE_ERROR;
        }
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
