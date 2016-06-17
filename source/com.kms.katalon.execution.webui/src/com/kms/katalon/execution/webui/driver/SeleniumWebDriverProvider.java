package com.kms.katalon.execution.webui.driver;

import static org.eclipse.core.runtime.Platform.ARCH_X86_64;
import static org.eclipse.core.runtime.Platform.OS_LINUX;
import static org.eclipse.core.runtime.Platform.OS_MACOSX;
import static org.eclipse.core.runtime.Platform.OS_WIN32;
import static org.eclipse.core.runtime.Platform.getOS;
import static org.eclipse.core.runtime.Platform.getOSArch;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class SeleniumWebDriverProvider {
    private static final String DRIVERS_FOLDER_NAME = "resources" + File.separator + "drivers";

    public static File getDriverDirectory() throws IOException {
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_WEB_UI_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + DRIVERS_FOLDER_NAME);
        }
        // run as product
        return new File(ClassPathResolver.getConfigurationFolder() + File.separator + DRIVERS_FOLDER_NAME);
    }

    public static String getChromeDriverPath() {
        try {
            switch (getOS()) {
                case OS_WIN32:
                    return getChromeDriverPathForWindows();
                case OS_LINUX:
                    return getChromeDriverPathForLinux();
                case OS_MACOSX:
                    return getChromeDriverPathForMac();
            }
        } catch (IOException e) {
            // do nothing
        }
        return "";
    }

    private static String getChromeDriverPathForLinux() throws IOException {
        String chromeDriverPath = getDriverDirectory().getAbsolutePath() + File.separator;
        if (getOSArch().equals(ARCH_X86_64)) {
            chromeDriverPath += "chromedriver_linux64" + File.separator + "chromedriver";
        } else {
            chromeDriverPath += "chromedriver_linux32" + File.separator + "chromedriver";
        }
        return chromeDriverPath;
    }

    private static String getChromeDriverPathForWindows() throws IOException {
        return getDriverDirectory().getAbsolutePath() + File.separator + "chromedriver_win32" + File.separator
                + "chromedriver.exe";
    }

    private static String getChromeDriverPathForMac() throws IOException {
        return getDriverDirectory().getAbsolutePath() + File.separator + "chromedriver_mac32" + File.separator
                + "chromedriver";
    }

    public static String getIEDriverPath() {
        try {
            String ieDriverPath = getDriverDirectory().getAbsolutePath() + File.separator;
            if (getOSArch().equals(ARCH_X86_64)) {
                ieDriverPath += "iedriver_win64" + File.separator + "IEDriverServer.exe";
                // IEDriverServer now is driver 32-bit because the driver for
                // win64
                // is not stable
                // http://stackoverflow.com/questions/14461827/webdriver-and-ie10-very-slow-input
                // ieDriverPath += "iedriver_win32" + File.separator +
                // "IEDriverServer.exe";

            } else {
                ieDriverPath += "iedriver_win32" + File.separator + "IEDriverServer.exe";
            }
            return ieDriverPath;
        } catch (IOException e) {
            // do nothing
        }
        return "";
    }

    public static String getEdgeDriverPath() {
        if (getOSArch().equals(ARCH_X86_64)) {
            return "C:\\Program Files (x86)\\Microsoft Web Driver\\MicrosoftWebDriver.exe";
        } else {
            return "C:\\Program Files\\Microsoft Web Driver\\MicrosoftWebDriver.exe";
        }
    }
    
    public static String getGeckoDriverPath() throws IOException {
        switch (getOS()) {
            case OS_WIN32:
                return getDriverDirectory().getAbsolutePath() + File.separator + "firefox_win32" + File.separator
                        + "wires.exe";
            case OS_LINUX:
                return getDriverDirectory().getAbsolutePath() + File.separator + "firefox_linux64" + File.separator
                        + "wires";
            case OS_MACOSX:
                return getDriverDirectory().getAbsolutePath() + File.separator + "firefox_mac32" + File.separator
                        + "wires";
        }
        return "";
    }
}
