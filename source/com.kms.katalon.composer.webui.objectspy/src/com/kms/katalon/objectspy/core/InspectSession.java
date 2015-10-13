package com.kms.katalon.objectspy.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.e4.core.services.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;
import com.kms.katalon.objectspy.exception.BrowserNotSupportedException;
import com.kms.katalon.objectspy.exception.ExtensionNotFoundException;
import com.kms.katalon.objectspy.exception.IEAddonNotInstalledException;
import com.kms.katalon.objectspy.util.FileUtil;
import com.kms.katalon.objectspy.util.WinRegistry;

@SuppressWarnings("restriction")
public class InspectSession implements Runnable {
    private static final String SERVER_URL_FILE_NAME = "serverUrl.txt";
    private static final String OBJECT_SPY_APPLICATION_DATA_FOLDER = System.getProperty("user.home") + File.separator
            + "AppData" + File.separator + "Local" + File.separator + "KMS" + File.separator + "qAutomate"
            + File.separator + "ObjectSpy";

    protected static final String IE_ADDON_BHO_KEY = "{8CB0FB3A-8EFA-4F94-B605-F3427688F8C7}";
    protected static final String WINDOWS_32BIT_BHO_REGISTRY_KEY = "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\explorer\\Browser Helper Objects";
    protected static final String WINDOWS_BHO_REGISTRY_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\explorer\\Browser Helper Objects";
    protected static final String IE_ABSOLUTE_PATH = "C:\\Program Files\\Internet Explorer\\iexplore.exe";
    protected static final String IE_32BIT_ABSOLUTE_PATH = "C:\\Program Files (x86)\\Internet Explorer\\iexplore.exe";
    protected static final String CHROME_EXTENSION_RELATIVE_PATH = File.separator + "Chrome" + File.separator
            + "Object Spy";
    protected static final String FIREFOX_ADDON_RELATIVE_PATH = File.separator + "Firefox" + File.separator
            + "objectspy.xpi";

    protected String projectDir;
    protected boolean isRunFlag;
    protected WebDriver driver;
    protected Object options;
    protected WebUIDriverType webUiDriverType;
    protected String serverUrl;
    protected ProjectEntity currentProject;

    public InspectSession(String serverUrl, WebUIDriverType webUiDriverType, ProjectEntity currentProject, Logger logger)
            throws Exception {
        this.serverUrl = serverUrl;
        this.webUiDriverType = webUiDriverType;
        this.currentProject = currentProject;
        isRunFlag = true;
    }

    protected void checkIEAddon() throws IllegalAccessException, InvocationTargetException,
            IEAddonNotInstalledException {
        boolean found = checkRegistryKey(WINDOWS_32BIT_BHO_REGISTRY_KEY);
        if (!found) {
            found = checkRegistryKey(WINDOWS_BHO_REGISTRY_KEY);
        }
        if (!found) {
            throw new IEAddonNotInstalledException(getAddOnName());
        }
    }

    protected boolean checkRegistryKey(String parentKey) throws IllegalAccessException, InvocationTargetException {
        List<String> bhos;
        bhos = WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, parentKey);
        for (String bho : bhos) {
            if (bho.toLowerCase().equals(getIEAddonRegistryKey().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    protected void setUp(WebUIDriverType webUIDriverType, ProjectEntity currentProject) throws IOException,
            ExtensionNotFoundException, BrowserNotSupportedException {
        projectDir = currentProject.getFolderLocation();

        IDriverConnector driverConnector = WebUIExecutionUtil.getBrowserDriverConnector(webUIDriverType, projectDir);
        RunConfiguration.setExecutionSetting(driverConnector.getExecutionSettingPropertyMap());
        options = createDriverOptions(webUIDriverType);
    }

    @Override
    public void run() {
        if (webUiDriverType == WebUIDriverType.CHROME_DRIVER || webUiDriverType == WebUIDriverType.FIREFOX_DRIVER) {
            try {
                setUp(webUiDriverType, currentProject);
                runSeleniumWebDriver();
            } catch (IOException | ExtensionNotFoundException | BrowserNotSupportedException e) {
                LoggerSingleton.logError(e);
            }
        } else if (webUiDriverType == WebUIDriverType.IE_DRIVER) {
            try {
                checkIEAddon();
                runIE();
            } catch (IOException | IllegalAccessException | InvocationTargetException | IEAddonNotInstalledException e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    protected void runIE() throws IOException {
        File settingFolder = new File(getIEApplicationDataFolder());
        if (!settingFolder.exists()) {
            settingFolder.mkdirs();
        }
        File serverSettingFile = new File(getIEApplicationServerSettingFile());
        FileUtils.writeStringToFile(serverSettingFile, serverUrl);

        File ieExecutingFile = new File(IE_32BIT_ABSOLUTE_PATH);
        if (!ieExecutingFile.exists()) {
            ieExecutingFile = new File(IE_ABSOLUTE_PATH);
        }
        if (ieExecutingFile.exists() && ieExecutingFile.isFile()) {
            Runtime.getRuntime().exec(ieExecutingFile.getAbsolutePath());
        }
    }

    protected String getIEApplicationServerSettingFile() {
        return getIEApplicationDataFolder() + File.separator + SERVER_URL_FILE_NAME;
    }

    protected String getIEApplicationDataFolder() {
        return OBJECT_SPY_APPLICATION_DATA_FOLDER;
    }

    protected void runSeleniumWebDriver() {
        try {
            Thread.sleep(5);

            driver = DriverFactory.openWebDriver(webUiDriverType, projectDir, options);

            while (isRunFlag) {
                try {
                    Thread.sleep(5000);
                    if (driver == null || ((RemoteWebDriver) driver).getSessionId() == null) {
                        break;
                    }
                    driver.getWindowHandle();
                } catch (UnreachableBrowserException e) {
                    break;
                } catch (WebDriverException e) {
                    if (e.getMessage().startsWith("chrome not reachable")) {
                        break;
                    }
                    continue;
                }
            }
        } catch (UnreachableBrowserException e) {
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        } finally {
            dispose();
        }
    }

    protected Object createDriverOptions(WebUIDriverType driverType) throws IOException, ExtensionNotFoundException,
            BrowserNotSupportedException {
        if (driverType == WebUIDriverType.CHROME_DRIVER) {
            return createChromDriverOptions();
        }
        if (driverType == WebUIDriverType.FIREFOX_DRIVER) {
            return createFireFoxProfile();
        } else {
            throw new BrowserNotSupportedException(driverType);
        }
    }

    protected FirefoxProfile createFireFoxProfile() throws IOException {
        File file = getFirefoxAddonFile();
        if (file != null) {
            FirefoxProfile firefoxProfile = new FirefoxProfile();
            firefoxProfile.addExtension(file);
            firefoxProfile.setPreference("serverUrl", serverUrl);
            return firefoxProfile;
        }
        return null;
    }

    protected DesiredCapabilities createChromDriverOptions() throws IOException, ExtensionNotFoundException {
        File chromeExtensionFolder = getChromeExtensionFile();
        if (chromeExtensionFolder == null || !chromeExtensionFolder.isDirectory() || !chromeExtensionFolder.exists()) {
            throw new ExtensionNotFoundException(getChromeExtensionPath(), WebUIDriverType.CHROME_DRIVER);
        }
        File serverUrlScriptFile = new File(chromeExtensionFolder.getAbsolutePath() + File.separator + "server.js");
        FileUtils.writeStringToFile(serverUrlScriptFile, "qAutomate_server_url = '" + serverUrl + "'",
                Charset.defaultCharset());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("load-extension=" + chromeExtensionFolder.getAbsolutePath());
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        return capabilities;
    }

    protected File getChromeExtensionFile() throws IOException {
        File chromeExtension = null;
        File extensionFolder = FileUtil.getExtensionsDirectory(FrameworkUtil.getBundle(this.getClass()));
        if (extensionFolder.exists() && extensionFolder.isDirectory()) {
            chromeExtension = new File(extensionFolder.getAbsolutePath() + getChromeExtensionPath());
        }
        return chromeExtension;
    }

    protected File getFirefoxAddonFile() throws IOException {
        File firefoxAddon = null;
        File extensionFolder = FileUtil.getExtensionsDirectory(FrameworkUtil.getBundle(this.getClass()));
        if (extensionFolder.exists() && extensionFolder.isDirectory()) {
            firefoxAddon = new File(extensionFolder.getAbsolutePath() + getFirefoxExtensionPath());
        }
        return firefoxAddon;
    }

    public void stop() {
        isRunFlag = false;
        dispose();
    }

    protected void dispose() {
        try {
            if (driver != null) {
                driver.quit();
            }
            File serverSettingFile = new File(getIEApplicationServerSettingFile());
            if (serverSettingFile.exists()) {
                serverSettingFile.delete();
            }
        } catch (UnreachableBrowserException e) {
        }
    }

    public boolean isRunning() {
        return isRunFlag;
    }

    protected String getAddOnName() {
        return "Object Spy";
    }

    protected String getChromeExtensionPath() {
        return CHROME_EXTENSION_RELATIVE_PATH;
    }

    protected String getFirefoxExtensionPath() {
        return FIREFOX_ADDON_RELATIVE_PATH;
    }

    protected String getIEAddonRegistryKey() {
        return IE_ADDON_BHO_KEY;
    }

}
