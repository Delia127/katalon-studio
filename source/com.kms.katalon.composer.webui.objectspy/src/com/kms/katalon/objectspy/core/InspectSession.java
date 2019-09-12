package com.kms.katalon.objectspy.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.InvalidPathException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.osgi.framework.FrameworkUtil;

import com.google.gson.Gson;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.core.util.internal.ZipUtil;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.core.webui.util.WebDriverPropertyUtil;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.exception.BrowserNotSupportedException;
import com.kms.katalon.objectspy.exception.ExtensionNotFoundException;
import com.kms.katalon.objectspy.preferences.ObjectSpyPreferences;
import com.kms.katalon.objectspy.util.FileUtil;
import com.kms.katalon.objectspy.websocket.AddonHotKeyData;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.AddonSocketServer;
import com.kms.katalon.objectspy.websocket.messages.StartInspectAddonMessage;
import com.kms.katalon.selenium.driver.CFirefoxDriver;

public class InspectSession implements Runnable {
    
    private static final String HTTP = "http";

    private static final String ABOUT_BLANK = "about:blank";

    public static final String OBJECT_SPY_ADD_ON_NAME = "Object Spy";

    protected static final String LOAD_EXTENSION_CHROME_PREFIX = "load-extension=";

    private static final String VARIABLE_INIT_EXPRESSION_FOR_CHROME = "katalonServerPort = ''{0}''\r\n"
            + "katalonOnOffStatus = true\r\n" + "spy_captureObjectHotKey = {1};\r\n"
            + "spy_loadDomMapHotKey = {2};\r\n";

    private static final String VARIABLE_INIT_FILE_FOR_CHROME = "chrome_variables_init.js";

    private static final String SERVER_URL_FILE_NAME = "serverUrl.txt";

    private static final String OBJECT_SPY_APPLICATION_DATA_FOLDER = System.getProperty("user.home") + File.separator
            + "AppData" + File.separator + "Local" + File.separator + "KMS" + File.separator + "qAutomate"
            + File.separator + "ObjectSpy";

    protected static final String IE_ADDON_BHO_KEY = "{8CB0FB3A-8EFA-4F94-B605-F3427688F8C7}";

    protected static final String IE_ABSOLUTE_PATH = "C:\\Program Files\\Internet Explorer\\iexplore.exe";

    protected static final String IE_32BIT_ABSOLUTE_PATH = "C:\\Program Files (x86)\\Internet Explorer\\iexplore.exe";

    protected static final String CHROME_RECORD_SPY_EXTENSION_RELATIVE_PATH = File.separator + "Chrome" + File.separator
            + OBJECT_SPY_ADD_ON_NAME + File.separator + "KR";

    protected static final String FIREFOX_RECORD_SPY_EXTENSION_RELATIVE_PATH = File.separator + "Firefox" + File.separator
            + "objectspy.xpi";
    
    protected static final String FIREFOX_RECORD_SMART_WAIT_RELATIVE_PATH = File.separator + "Firefox" + File.separator
            + "smartwait.xpi";

    protected static final String FIREFOX_RECORD_SPY_FOLDER_RELATIVE_PATH = File.separator + "Firefox" + File.separator
            + "objectspy";
    
    protected static final String CHROME_SMART_WAIT_EXTENSION_RELATIVE_PATH = File.separator + "Chrome" + File.separator
            + "Smart Wait";

    protected String projectDir;

    protected boolean isRunFlag;

    protected WebDriver driver;

    protected Object options;

    protected WebUIDriverType webUiDriverType;

    protected HTMLElementCaptureServer server;

    private String startUrl;

    private boolean driverStarted = false;

    private IDriverConnector driverConnector;

    public InspectSession(HTMLElementCaptureServer server, IDriverConnector driverConnector, String startUrl) {
        this.server = server;
        this.driverConnector = driverConnector;
        this.webUiDriverType = (WebUIDriverType) driverConnector.getDriverType();
        this.startUrl = startUrl;
        isRunFlag = true;
    }

    public InspectSession(HTMLElementCaptureServer server, IDriverConnector driverConnector) {
        this(server, driverConnector, null);
    }

    protected void setUp() throws IOException, ExtensionNotFoundException, BrowserNotSupportedException {
        DefaultExecutionSetting executionSetting = new DefaultExecutionSetting();
        executionSetting.setTimeout(ExecutionUtil.getDefaultImplicitTimeout());

        Map<String, IDriverConnector> driverConnectors = new HashMap<String, IDriverConnector>(1);
        driverConnectors.put(DriverFactory.WEB_UI_DRIVER_PROPERTY, driverConnector);

        RunConfiguration
                .setExecutionSetting(ExecutionUtil.getExecutionProperties(executionSetting, driverConnectors, null));
        options = createDriverOptions(webUiDriverType);

        if (webUiDriverType == WebUIDriverType.IE_DRIVER) {
            setupIE();
        }
    }

    @Override
    public void run() {
        try {
            setUp();
        } catch (IOException | ExtensionNotFoundException | BrowserNotSupportedException e) {
            LoggerSingleton.logError(e);
            showErrorMessageDialog(e.getMessage());
        }
        runSeleniumWebDriver();
    }

    public void setupIE() throws IOException {
        File settingFolder = new File(getIEApplicationDataFolder());
        if (!settingFolder.exists()) {
            settingFolder.mkdirs();
        }
        File serverSettingFile = new File(getIEApplicationServerSettingFile());
        FileUtils.writeStringToFile(serverSettingFile, server.getServerUrl());
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

            driver = DriverFactory.openWebDriver(webUiDriverType, options);
            driverStarted = true;
            if (webUiDriverType == WebUIDriverType.FIREFOX_DRIVER) {
                LoggerSingleton.logInfo(MessageFormat.format("Installing Katalon Recorder for {0}...",
                        DriverFactory.getBrowserVersion(driver)));

                // Fix KAT-3652: Cannot Record/Spy with Firefox latest version (v.62.0)
                CFirefoxDriver firefoxDriver = (CFirefoxDriver) driver;
                URL geckoDriverServiceUrl = firefoxDriver.getGeckoDriverService().getUrl();
                
                // Install Record-Spy extension
                CloseableHttpClient recordSpyInstallclient = HttpClientBuilder.create().build();
                HttpPost httpRecordSpyInstallPost = new HttpPost(geckoDriverServiceUrl.toString() + "/session/"
                        + ((RemoteWebDriver) driver).getSessionId() + "/moz/addon/install");
                String recordSpyInstallBodyContent = String.format("{\"path\": \"%s\"}",
                        StringEscapeUtils.escapeJava(getFirefoxRecordSpyAddonFile().getAbsolutePath()));
                httpRecordSpyInstallPost.setEntity(new StringEntity(recordSpyInstallBodyContent));
                recordSpyInstallclient.execute(httpRecordSpyInstallPost);
                
                // Install Smart Wait extension
                CloseableHttpClient smartWaitInstallclient = HttpClientBuilder.create().build();
                HttpPost smartWaitHttpPost = new HttpPost(geckoDriverServiceUrl.toString() + "/session/"
                        + ((RemoteWebDriver) driver).getSessionId() + "/moz/addon/install");
                String smartWaitBodyContent = String.format("{\"path\": \"%s\"}",
                        StringEscapeUtils.escapeJava(getFirefoxSmartWaitAddonFile().getAbsolutePath()));
                smartWaitHttpPost.setEntity(new StringEntity(smartWaitBodyContent));
                smartWaitInstallclient.execute(smartWaitHttpPost);
                

                handleForFirefoxAddon();
            }

            if (StringUtils.isNotEmpty(startUrl)) {
                try {
                    driver.navigate().to(PathUtil.getUrl(startUrl, HTTP));
                } catch (MalformedURLException | URISyntaxException | InvalidPathException e) {
                    // Invalid url, ignore this
                }
            }

            while (isRunFlag) {
                try {
                    Thread.sleep(1000L);
                    if (driver == null || driver.getTitle() == null) {
                        break;
                    }
                    driver.getWindowHandle();
                } catch (NoSuchWindowException e) {
                    break;
                } catch (UnreachableBrowserException e) {
                    break;
                } catch (WebDriverException e) {
                    if (e.getMessage().startsWith("chrome not reachable")) {
                        break;
                    }
                }
            }
        } catch (WebDriverException e) {
            showErrorMessageDialog(e.getMessage());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            showErrorMessageDialog(e.getMessage());
        } finally {
            dispose();
        }
    }

    protected void handleForFirefoxAddon() throws InterruptedException {
        LoggerSingleton.logInfo("Connecting Firefox Recorder with socket server...");
        final AddonSocketServer socketServer = AddonSocketServer.getInstance();
        while (socketServer.getAddonSocketByBrowserName(webUiDriverType.toString()) == null && isRunFlag) {
            // wait for web socket to connect
            Thread.sleep(500);
        }
        final AddonSocket firefoxAddonSocket = socketServer.getAddonSocketByBrowserName(webUiDriverType.toString());
        firefoxAddonSocket.sendMessage(new StartInspectAddonMessage());
        LoggerSingleton.logInfo("Sending Inspect Session message to Firefox Recorder...");
    }

    public boolean isDriverStarted() {
        return driverStarted;
    }

    private static void showErrorMessageDialog(String message) {
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, message);
            }
        });
    }

    protected Object createDriverOptions(WebUIDriverType driverType)
            throws IOException, ExtensionNotFoundException, BrowserNotSupportedException {
        DesiredCapabilities capabilities = WebDriverPropertyUtil.toDesireCapabilities(
                RunConfiguration.getDriverPreferencesProperties(DriverFactory.WEB_UI_DRIVER_PROPERTY), driverType);
        switch (driverType) {
            case CHROME_DRIVER:
                return createChromDriverOptions(capabilities);
            case IE_DRIVER:
                return createIEDesiredCapabilities(capabilities);
            default:
                return capabilities;
        }
    }

    private DesiredCapabilities createIEDesiredCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, ABOUT_BLANK);
        return capabilities;
    }

    protected FirefoxProfile createFireFoxProfile() throws IOException {
        FirefoxProfile firefoxProfile = WebDriverPropertyUtil.createDefaultFirefoxProfile();
        firefoxProfile.addExtension(getFirefoxRecordSpyAddonFile());
        firefoxProfile.addExtension(getFirefoxSmartWaitAddonFile());
        return firefoxProfile;
    }

    protected DesiredCapabilities createChromDriverOptions(DesiredCapabilities capabilities)
            throws IOException, ExtensionNotFoundException {
        File chromeRecordSpyExtensionFolder = getRecordSpyExtensionFile();
        if (chromeRecordSpyExtensionFolder == null || !chromeRecordSpyExtensionFolder.isDirectory()
                || !chromeRecordSpyExtensionFolder.exists()) {
            throw new ExtensionNotFoundException(getChromeRecordSpyExtensionPath(), WebUIDriverType.CHROME_DRIVER);
        }
        File chromeSmartWaitExtensionFolder = getSmartWaitExtensionFile();
        if (chromeSmartWaitExtensionFolder == null || !chromeSmartWaitExtensionFolder.isDirectory()
                || !chromeSmartWaitExtensionFolder.exists()) {
            throw new ExtensionNotFoundException(getChromeSmartWaitExtensionPath(), WebUIDriverType.CHROME_DRIVER);
        }
        generateVariableInitFileForChrome(chromeRecordSpyExtensionFolder);
        WebDriverPropertyUtil.removeArgumentsForChrome(capabilities, WebDriverPropertyUtil.DISABLE_EXTENSIONS);
        WebDriverPropertyUtil.addArgumentsForChrome(capabilities,
                LOAD_EXTENSION_CHROME_PREFIX + chromeRecordSpyExtensionFolder.getCanonicalPath() + ","
                        + chromeSmartWaitExtensionFolder.getCanonicalPath());
        return capabilities;
    }

    private void generateVariableInitFileForChrome(File chromeExtensionFolder) throws IOException {
        File variableInitJSFile = new File(
                chromeExtensionFolder.getAbsolutePath() + File.separator + VARIABLE_INIT_FILE_FOR_CHROME);
        AddonHotKeyData captureObjectHotKey = AddonHotKeyData.buildFrom(ObjectSpyPreferences.getCaptureObjectHotKey());
        AddonHotKeyData loadDomMapHotKey = AddonHotKeyData.buildFrom(ObjectSpyPreferences.getLoadDomMapHotKey());
        Gson gson = new Gson();
        FileUtils.writeStringToFile(variableInitJSFile,
                MessageFormat.format(VARIABLE_INIT_EXPRESSION_FOR_CHROME, String.valueOf(server.getServerPort()),
                        gson.toJson(captureObjectHotKey), gson.toJson(loadDomMapHotKey)),
                Charset.defaultCharset());
    }

    protected File getRecordSpyExtensionFile() throws IOException {
        File chromeExtension = null;
        File extensionFolder = FileUtil.getExtensionsDirectory(FrameworkUtil.getBundle(InspectSession.class));
        if (extensionFolder.exists() && extensionFolder.isDirectory()) {
            chromeExtension = new File(extensionFolder.getAbsolutePath() + getChromeRecordSpyExtensionPath());
        }
        return chromeExtension;
    }
    
    protected File getSmartWaitExtensionFile() throws IOException {
        File chromeExtension = null;
        File extensionFolder = FileUtil.getExtensionsDirectory(FrameworkUtil.getBundle(InspectSession.class));
        if (extensionFolder.exists() && extensionFolder.isDirectory()) {
            chromeExtension = new File(extensionFolder.getAbsolutePath() + CHROME_SMART_WAIT_EXTENSION_RELATIVE_PATH);
        }
        return chromeExtension;
    }

    protected File getFirefoxAddonExtractedFolder() throws IOException {
        File extensionFolder = FileUtil.getExtensionsDirectory(FrameworkUtil.getBundle(InspectSession.class));
        if (extensionFolder.exists() && extensionFolder.isDirectory()) {
            File firefoxExtensionFolder = FileUtil.getExtensionBuildFolder();
            File firefoxAddonExtracted = new File(firefoxExtensionFolder, FIREFOX_RECORD_SPY_FOLDER_RELATIVE_PATH);
            if (firefoxAddonExtracted.exists()) {
                FileUtils.cleanDirectory(firefoxAddonExtracted);
            }
            File firefoxAddon = new File(extensionFolder.getAbsolutePath() + getFirefoxExtensionPath());
            ZipUtil.extract(firefoxAddon, firefoxAddonExtracted);
            return firefoxAddonExtracted;
        }
        return null;
    }

    protected File getFirefoxRecordSpyAddonFile() throws IOException {
        File extensionFolder = FileUtil.getExtensionsDirectory(FrameworkUtil.getBundle(InspectSession.class));
        if (extensionFolder.exists() && extensionFolder.isDirectory()) {
            return new File(extensionFolder.getAbsolutePath(), FIREFOX_RECORD_SPY_EXTENSION_RELATIVE_PATH);
        }
        return null;
    }
    
    protected File getFirefoxSmartWaitAddonFile() throws IOException {
        File extensionFolder = FileUtil.getExtensionsDirectory(FrameworkUtil.getBundle(InspectSession.class));
        if (extensionFolder.exists() && extensionFolder.isDirectory()) {
            return new File(extensionFolder.getAbsolutePath(), FIREFOX_RECORD_SMART_WAIT_RELATIVE_PATH);
        }
        return null;
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
        } catch (UnreachableBrowserException e) {} catch (WebDriverException e) {
            LoggerSingleton.logError(e);
        }
    }

    public boolean isRunning() {
        return isRunFlag;
    }

    protected String getAddOnName() {
        return OBJECT_SPY_ADD_ON_NAME;
    }

    protected String getChromeRecordSpyExtensionPath() {
        // return CHROME_EXTENSION_RELATIVE_PATH;
        return CHROME_RECORD_SPY_EXTENSION_RELATIVE_PATH;
    }
    
    protected String getChromeSmartWaitExtensionPath() {
        return CHROME_SMART_WAIT_EXTENSION_RELATIVE_PATH;
    }

    protected String getFirefoxExtensionPath() {
        return FIREFOX_RECORD_SPY_EXTENSION_RELATIVE_PATH;
    }

    protected String getIEAddonRegistryKey() {
        return IE_ADDON_BHO_KEY;
    }

    public WebDriver getWebDriver() {
        return driver;
    }

    public WebUIDriverType getWebUiDriverType() {
        return webUiDriverType;
    }

}
