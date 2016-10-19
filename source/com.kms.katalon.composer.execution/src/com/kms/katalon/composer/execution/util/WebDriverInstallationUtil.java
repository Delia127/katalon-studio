package com.kms.katalon.composer.execution.util;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;

import com.google.common.io.Files;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class WebDriverInstallationUtil {
    private static final String USER_HOME = System.getProperty("user.home");

    private static final String KATALON_FOLDER = USER_HOME + "/.katalon";

    private static final String SAFARI_WEB_DRIVER_FILE_NAME = "SafariDriver.safariextz";

    private static final String SAFARI_WEBDRIVER_FILE_PATH = KATALON_FOLDER + "/" + SAFARI_WEB_DRIVER_FILE_NAME;

    private static final String INSTALLED_SAFARI_WEBDRIVER = USER_HOME
            + "/Library/Safari/Extensions/WebDriver.safariextz";

    private static String safariDriverFolder;

    private static String safariWebDriverInstallScript;

    static {
        try {
            safariDriverFolder = ClassPathResolver.getConfigurationFolder().getCanonicalPath()
                    + "/resources/drivers/safaridriver";
            safariWebDriverInstallScript = safariDriverFolder + "/InstallWebDriver.scpt";
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }

    public static boolean installSafariWebDriverAsNeed() {
        if (!Platform.getOS().equals(Platform.OS_MACOSX)) {
            return true;
        }
        String installFile2 = INSTALLED_SAFARI_WEBDRIVER.replaceAll("\\.safariextz", "-2.safariextz");
        if (!new File(INSTALLED_SAFARI_WEBDRIVER).exists() && !new File(installFile2).exists()) {
            boolean confirmed = MessageDialog.openConfirm(null, ComposerExecutionMessageConstants.DIA_CONFIRM_INSTALL_WEBDRIVER_TITLE, 
                    MessageFormat.format(ComposerExecutionMessageConstants.DIA_CONFIRM_INSTALL_WEBDRIVER, "Safari"));
            if (!confirmed) {
                return false;
            }
            
            createFileWebDriver();
            return installWebDriver();
        }

        return true;
    }

    private static void createFileWebDriver() {
        try {
            File srcWebDriver = new File(safariDriverFolder + File.separator + SAFARI_WEB_DRIVER_FILE_NAME);
            if (srcWebDriver.exists()) {
                File descWebDriver = new File(KATALON_FOLDER + File.separator + SAFARI_WEB_DRIVER_FILE_NAME);
                Files.copy(srcWebDriver, descWebDriver);
            }

        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }

    }

    private static boolean installWebDriver() {
        try {
            new File(safariWebDriverInstallScript).setExecutable(true);
            Process proc = Runtime.getRuntime().exec(
                    new String[] { "osascript", safariWebDriverInstallScript, SAFARI_WEBDRIVER_FILE_PATH,
                            INSTALLED_SAFARI_WEBDRIVER });
            proc.waitFor();
            return new File(INSTALLED_SAFARI_WEBDRIVER).exists();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }
}