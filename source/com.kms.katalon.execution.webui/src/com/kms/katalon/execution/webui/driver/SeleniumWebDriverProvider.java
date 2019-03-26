package com.kms.katalon.execution.webui.driver;

import static org.eclipse.core.runtime.Platform.ARCH_X86_64;
import static org.eclipse.core.runtime.Platform.OS_LINUX;
import static org.eclipse.core.runtime.Platform.OS_MACOSX;
import static org.eclipse.core.runtime.Platform.OS_WIN32;
import static org.eclipse.core.runtime.Platform.getOS;
import static org.eclipse.core.runtime.Platform.getOSArch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

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
            String osResourcesFolderName = "";
            switch (getOS()) {
                case OS_WIN32:
                    if (getOSArch().equals(ARCH_X86_64)) {
                        osResourcesFolderName = "win64";
                    } else {
                        osResourcesFolderName = "win32";
                    }
                    break;
                case OS_LINUX:
                    if (getOSArch().equals(ARCH_X86_64)) {
                        osResourcesFolderName = "linux64";
                    } else {
                        osResourcesFolderName = "linux32";
                    }
                    break;
                case OS_MACOSX:
                    osResourcesFolderName = "macosx";
                    break;
            }
            return new File(bundleFile + File.separator + "os_resources" + File.separator + osResourcesFolderName
                    + File.separator + DRIVERS_FOLDER_NAME);
        }
        // run as product
        return new File(ClassPathResolver.getConfigurationFolder() + File.separator + DRIVERS_FOLDER_NAME);
    }

    private static void makeFileExecutable(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            Set<PosixFilePermission> perms = new HashSet<>();
            for (PosixFilePermission permission : PosixFilePermission.values()) {
                perms.add(permission);
            }
            Files.setPosixFilePermissions(file.toPath(), perms);
        }
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
        String chromeDriverPath = getDriverDirectory().getAbsolutePath() + File.separator + "chromedriver_mac"
                + File.separator + "chromedriver";
        makeFileExecutable(chromeDriverPath);
        return chromeDriverPath;
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
        try {
            return getDriverDirectory().getAbsolutePath() + File.separator + "edgedriver" + File.separator
                    + "MicrosoftWebDriver.exe";
        } catch (IOException ex) {
            return "";
        }
    }

    public static String getGeckoDriverPath() throws IOException {
        switch (getOS()) {
            case OS_WIN32: {
                return getDriverDirectory().getAbsolutePath() + File.separator + "firefox_win64" + File.separator
                        + "geckodriver.exe";
            }
            case OS_LINUX: {
                String geckoDriverPath = getDriverDirectory().getAbsolutePath() + File.separator + "firefox_linux64" + File.separator
                        + "geckodriver";
                makeFileExecutable(geckoDriverPath);
                return geckoDriverPath;
            }
            case OS_MACOSX: {
                String geckoDriverPath = getDriverDirectory().getAbsolutePath() + File.separator + "firefox_mac"
                        + File.separator + "geckodriver";
                makeFileExecutable(geckoDriverPath);
                return geckoDriverPath;
            }
        }
        return "";
    }
}
