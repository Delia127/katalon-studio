package com.kms.katalon.composer.components.impl.util;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.startsWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

import org.apache.commons.lang.SystemUtils;
import org.eclipse.core.runtime.Platform;

public class PlatformUtil {
    private static final String BROWSE_NAME_GOOGLE_CHROME_ON_LINUX = "google-chrome-stable";

    private static final String BROWSER_NAME_FIREFOX_ON_LINUX = "firefox";

    private final static String OS = System.getProperty("os.name").toLowerCase();

    private static final String WINDOWS_10_VERSION_PREFIX = "10";

    private static final String REG_SZ = "REG_SZ"; //$NON-NLS-1$

    private static final String BROWSER_NAME_IE = "Internet Explorer"; //$NON-NLS-1$

    /**
     * Chrome browser app name on macOS
     */
    private static final String BROWSER_NAME_GOOGLE_CHROME = "Google Chrome"; //$NON-NLS-1$

    /**
     * Firefox browser app name on macOS
     */
    private static final String BROWSER_NAME_FIREFOX = "Firefox"; //$NON-NLS-1$

    /**
     * Safari browser app name on macOS
     */
    private static final String BROWSER_NAME_SAFARI = "Safari"; //$NON-NLS-1$

    public static boolean isIEInstalled() {
        return isBrowserInstalledOnWindows(BROWSER_NAME_IE);
    }

    public static boolean isEdgeInstalled() {
        // Microsoft Edge is apart of Windows since Windows 10 released
        return isWindows10();
    }

    public static boolean isFirefoxInstalled() {
        if (isMacOS()) {
            return isBrowserInstalledOnMac(BROWSER_NAME_FIREFOX);
        }
        if (isWindowsOS()) {
            return isBrowserInstalledOnWindows(BROWSER_NAME_FIREFOX);
        }
        if (isLinux()) {
            return isBrowserInstalledOnLinux(BROWSER_NAME_FIREFOX_ON_LINUX);
        }
        return false;
    }

    public static boolean isChromeInstalled() {
        if (isMacOS()) {
            return isBrowserInstalledOnMac(BROWSER_NAME_GOOGLE_CHROME);
        }
        if (isWindowsOS()) {
            return isBrowserInstalledOnWindows(BROWSER_NAME_GOOGLE_CHROME);
        }
        if (isLinux()) {
            return isBrowserInstalledOnLinux(BROWSE_NAME_GOOGLE_CHROME_ON_LINUX);
        }
        return false;
    }

    public static boolean isBrowserInstalledOnLinux(String browserName) {
        try {
            Process process = Runtime.getRuntime()
                    .exec("which " + browserName);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Optional<String> output = stdInput.lines()
                    .findFirst();
            return output.isPresent();
        } catch (Exception e) {
            // swallow the exception
            return false;
        }
    }

    public static boolean isSafariInstalled() {
        return isBrowserInstalledOnMac(BROWSER_NAME_SAFARI);
    }

    public static boolean isWindowsOS() {
        return Platform.OS_WIN32.equals(Platform.getOS());
    }

    public static boolean isWindows10() {
        return isWindowsOS() && startsWith(getOSVersion(), WINDOWS_10_VERSION_PREFIX);
    }

    public static boolean isMacOS() {
        return Platform.OS_MACOSX.equals(Platform.getOS());
    }

    public static String getOSName() {
        return SystemUtils.OS_NAME;
    }

    public static String getOSVersion() {
        return SystemUtils.OS_VERSION;
    }

    /**
     * @param browserName Browser Name
     * @return true|false
     * @see {@link https://technet.microsoft.com/en-us/library/cc742028(v=ws.11).aspx}
     */
    private static boolean isBrowserInstalledOnWindows(String browserName) {
        if (!isWindowsOS()) {
            return false;
        }
        try {
            Process process = Runtime.getRuntime()
                    .exec("reg query \"HKLM\\SOFTWARE\\RegisteredApplications\" /v \"" + browserName + "*\"");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Optional<String> output = stdInput.lines()
                    .filter(line -> isNotEmpty(line))
                    .filter(line -> line.contains(browserName) && line.contains(REG_SZ))
                    .findFirst();
            return output.isPresent();
        } catch (Exception e) {
            // swallow the exception
            return false;
        }
    }

    private static boolean isBrowserInstalledOnMac(String browserName) {
        if (!isMacOS()) {
            return false;
        }
        try {
            Process process = Runtime.getRuntime()
                    .exec(new String[] { "osascript", "-e", "version of app \"" + browserName + "\"" });
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Optional<String> output = stdInput.lines()
                    .filter(line -> isNotEmpty(line))
                    .filter(line -> !line.contains("error"))
                    .findFirst();
            return output.isPresent();
        } catch (Exception e) {
            // swallow the exception
            return false;
        }
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static String getPlatform() {
        return String.format("%s-%s", Platform.getOS(), getArch());
    }
    public static String getArch() {
        return Platform.getOSArch();
    }
}
