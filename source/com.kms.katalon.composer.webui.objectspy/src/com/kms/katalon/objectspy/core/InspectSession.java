package com.kms.katalon.objectspy.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

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
import com.kms.katalon.core.webui.util.WebDriverPropertyUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;
import com.kms.katalon.objectspy.exception.BrowserNotSupportedException;
import com.kms.katalon.objectspy.exception.ExtensionNotFoundException;
import com.kms.katalon.objectspy.util.FileUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

@SuppressWarnings("restriction")
public class InspectSession implements Runnable {
    public static final String OBJECT_SPY_ADD_ON_NAME = "Object Spy";

    private static final String IE_WINDOW_CLASS = "IEFrame";

    protected static final String LOAD_EXTENSION_CHROME_PREFIX = "load-extension=";

    private static final String VARIABLE_INIT_EXPRESSION_FOR_CHROME = "katalonServerPort = ''{0}''" + "\r\n"
            + "katalonOnOffStatus = true";

    private static final String VARIABLE_INIT_FILE_FOR_CHROME = "chrome_variables_init.js";

    private static final String FIREFOX_SERVER_PORT_PREFERENCE_KEY = "extensions.@objectspy.katalonServerPort";

    private static final String FIREFOX_ON_OFF_PREFERENCE_KEY = "extensions.@objectspy.katalonOnOffStatus";

    private static final String SERVER_URL_FILE_NAME = "serverUrl.txt";

    private static final String OBJECT_SPY_APPLICATION_DATA_FOLDER = System.getProperty("user.home") + File.separator
            + "AppData" + File.separator + "Local" + File.separator + "KMS" + File.separator + "qAutomate"
            + File.separator + "ObjectSpy";

    protected static final String IE_ADDON_BHO_KEY = "{8CB0FB3A-8EFA-4F94-B605-F3427688F8C7}";

    protected static final String IE_ABSOLUTE_PATH = "C:\\Program Files\\Internet Explorer\\iexplore.exe";

    protected static final String IE_32BIT_ABSOLUTE_PATH = "C:\\Program Files (x86)\\Internet Explorer\\iexplore.exe";

    protected static final String CHROME_EXTENSION_RELATIVE_PATH = File.separator + "Chrome" + File.separator
            + OBJECT_SPY_ADD_ON_NAME;

    protected static final String FIREFOX_ADDON_RELATIVE_PATH = File.separator + "Firefox" + File.separator
            + "objectspy.xpi";

    protected String projectDir;

    protected boolean isRunFlag;

    protected WebDriver driver;

    protected Object options;

    protected WebUIDriverType webUiDriverType;

    protected HTMLElementCaptureServer server;

    protected ProjectEntity currentProject;

    private boolean isInstant;

    public InspectSession(HTMLElementCaptureServer server, WebUIDriverType webUiDriverType, boolean isInstant,
            ProjectEntity currentProject, Logger logger) throws Exception {
        this.server = server;
        this.webUiDriverType = webUiDriverType;
        this.currentProject = currentProject;
        isRunFlag = true;
        this.isInstant = isInstant;
    }

    protected void setUp(WebUIDriverType webUIDriverType, ProjectEntity currentProject) throws IOException,
            ExtensionNotFoundException, BrowserNotSupportedException {
        projectDir = currentProject.getFolderLocation();

        IDriverConnector webUIDriverConnector = WebUIExecutionUtil.getBrowserDriverConnector(webUIDriverType,
                projectDir);
        DefaultExecutionSetting executionSetting = new DefaultExecutionSetting();
        executionSetting.setTimeout(ExecutionUtil.getDefaultImplicitTimeout());

        Map<String, IDriverConnector> driverConnectors = new HashMap<String, IDriverConnector>(1);
        driverConnectors.put(DriverFactory.WEB_UI_DRIVER_PROPERTY, webUIDriverConnector);

        RunConfiguration.setExecutionSetting(ExecutionUtil.getExecutionProperties(executionSetting, driverConnectors));
        options = createDriverOptions(webUIDriverType);
    }

    @Override
    public void run() {
        try {
            if (webUiDriverType == WebUIDriverType.IE_DRIVER) {
                runIE();
                return;
            }
            setUp(webUiDriverType, currentProject);
            runSeleniumWebDriver();
        } catch (IOException | ExtensionNotFoundException | BrowserNotSupportedException e) {
            LoggerSingleton.logError(e);
        }
    }

    protected void runIE() throws IOException, ExtensionNotFoundException, BrowserNotSupportedException {
        setupIE();
        if (isInstant) {
            runInstantIE();
        } else {
            openNewIEInstance();
        }
    }

    private void setupIE() throws IOException {
        File settingFolder = new File(getIEApplicationDataFolder());
        if (!settingFolder.exists()) {
            settingFolder.mkdirs();
        }
        File serverSettingFile = new File(getIEApplicationServerSettingFile());
        FileUtils.writeStringToFile(serverSettingFile, server.getServerUrl());
    }

    protected void runInstantIE() throws IOException {
        HWND hwnd = User32.INSTANCE.FindWindow(IE_WINDOW_CLASS, null);
        if (hwnd == null) {
            openNewIEInstance();
            return;
        }
        shiftFocusToWindow(hwnd);
    }

    private void shiftFocusToWindow(HWND hwnd) {
        User32.INSTANCE.ShowWindow(hwnd, 9);        // SW_RESTORE
        User32.INSTANCE.SetForegroundWindow(hwnd);   // bring to front
    }

    private void openNewIEInstance() throws IOException {
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
        } catch (UnreachableBrowserException e) {} catch (Exception e) {
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
        }
        return null;
    }

    protected FirefoxProfile createFireFoxProfile() throws IOException {
        FirefoxProfile firefoxProfile = WebDriverPropertyUtil.createDefaultFirefoxProfile();
        firefoxProfile.setPreference(FIREFOX_SERVER_PORT_PREFERENCE_KEY, String.valueOf(server.getServerPort()));
        firefoxProfile.setPreference(FIREFOX_ON_OFF_PREFERENCE_KEY, true);
        File file = getFirefoxAddonFile();
        if (file != null) {
            firefoxProfile.addExtension(file);
        }
        return firefoxProfile;
    }

    protected DesiredCapabilities createChromDriverOptions() throws IOException, ExtensionNotFoundException {
        File chromeExtensionFolder = getChromeExtensionFile();
        if (chromeExtensionFolder == null || !chromeExtensionFolder.isDirectory() || !chromeExtensionFolder.exists()) {
            throw new ExtensionNotFoundException(getChromeExtensionPath(), WebUIDriverType.CHROME_DRIVER);
        }
        generateVariableInitFileForChrome(chromeExtensionFolder);
        ChromeOptions options = new ChromeOptions();
        options.addArguments(LOAD_EXTENSION_CHROME_PREFIX + chromeExtensionFolder.getAbsolutePath(), WebDriverPropertyUtil.CHROME_SWITCHES, WebDriverPropertyUtil.DISABLE_EXTENSIONS);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        return capabilities;
    }

    private void generateVariableInitFileForChrome(File chromeExtensionFolder) throws IOException {
        File variableInitJSFile = new File(chromeExtensionFolder.getAbsolutePath() + File.separator
                + VARIABLE_INIT_FILE_FOR_CHROME);
        FileUtils.writeStringToFile(variableInitJSFile,
                MessageFormat.format(VARIABLE_INIT_EXPRESSION_FOR_CHROME, String.valueOf(server.getServerPort())),
                Charset.defaultCharset());
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
            if (driver != null && ((RemoteWebDriver) driver).getSessionId() != null) {
                driver.quit();
            }
            File serverSettingFile = new File(getIEApplicationServerSettingFile());
            if (serverSettingFile.exists()) {
                serverSettingFile.delete();
            }
        } catch (UnreachableBrowserException e) {}
    }

    public boolean isRunning() {
        return isRunFlag;
    }

    protected String getAddOnName() {
        return OBJECT_SPY_ADD_ON_NAME;
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

    public boolean isInstant() {
        return isInstant;
    }

    public void setInstant(boolean isInstant) {
        this.isInstant = isInstant;
    }

}
