package com.kms.katalon.composer.mobile.objectspy.dialog;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.text.StrMatcher;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.AndroidSnapshotMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.IosSnapshotMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.IosXCUISnapshotMobileElement;
import com.kms.katalon.composer.mobile.objectspy.util.Util;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.appium.driver.AppiumDriverManager;
import com.kms.katalon.core.appium.exception.AppiumStartException;
import com.kms.katalon.core.appium.exception.IOSWebkitStartException;
import com.kms.katalon.core.appium.exception.MobileDriverInitializeException;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.AndroidProperties;
import com.kms.katalon.core.mobile.keyword.internal.IOSProperties;
import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.core.util.internal.ProcessUtil;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.device.AndroidDeviceInfo;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;
import com.kms.katalon.execution.mobile.exception.AndroidSetupException;
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.integration.kobiton.driver.KobitonDriverConnector;
import com.kms.katalon.integration.kobiton.entity.KobitonApplication;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;

public class MobileInspectorController {

    private static final int SERVER_START_TIMEOUT = 60;

    private AppiumDriver<?> driver;

    private Process appiumServerProcess;

    private Process iosWebKitProcess;

    private AppiumStreamHandler streamHandler;

    private Thread appiumTailerThread;

    public MobileInspectorController() {
    }

    public void startExistingApp(MobileDeviceInfo mobileDeviceInfo, String appId)
            throws ExecutionException, InterruptedException, IOException, AppiumStartException,
            MobileDriverInitializeException, IOSWebkitStartException {
        if (mobileDeviceInfo == null) {
            return;
        }
        if (driver != null) {
            closeApp();
            Thread.sleep(2000);
        }
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();

        MobileDriverType mobileDriverType = getMobileDriverType(mobileDeviceInfo);
        if (mobileDriverType == null) {
            throw new ExecutionException(StringConstants.DIA_ERROR_MSG_OS_NOT_SUPPORT);
        }

        MobileDriverConnector mobileDriverConnector = getMobileDriverConnector(mobileDriverType, projectDir);

        if (mobileDriverConnector == null) {
            throw new ExecutionException(StringConstants.DIA_ERROR_MSG_OS_NOT_SUPPORT);
        }

        mobileDriverConnector = (MobileDriverConnector) mobileDriverConnector.clone();
        mobileDriverConnector.setDevice(mobileDeviceInfo);
        Map<String, IDriverConnector> driverConnectors = new HashMap<String, IDriverConnector>(2);
        driverConnectors.put(MobileDriverFactory.MOBILE_DRIVER_PROPERTY, mobileDriverConnector);

        String logFilePath = projectDir + File.separator + "appium.log";
        RunConfiguration.setAppiumLogFilePath(logFilePath);

        DefaultExecutionSetting generalExecutionSetting = new DefaultExecutionSetting();
        generalExecutionSetting.setTimeout(60);

        RunConfiguration.setExecutionSetting(
                ExecutionUtil.getExecutionProperties(generalExecutionSetting, driverConnectors, null));

        if (!AppiumDriverManager.isAppiumServerStarted(1)) {
            createAppiumLogTailer(logFilePath);
        }

        mobileDeviceInfo.updateRuntimeEnvironmentVariables();

        AppiumDriverManager.startAppiumServerJS(SERVER_START_TIMEOUT,
                getAdditionalEnvironmentVariables(mobileDriverType));
        driver = MobileDriverFactory.startMobileDriver(appId);
        driver.activateApp(appId);

        appiumServerProcess = AppiumDriverManager.getAppiumSeverProcess();

        iosWebKitProcess = AppiumDriverManager.getIosWebKitProcess();
    }

    public void startMobileApp(MobileDeviceInfo mobileDeviceInfo, String appFile, boolean uninstallAfterCloseApp)
            throws ExecutionException, InterruptedException, IOException, AppiumStartException,
            MobileDriverInitializeException, IOSWebkitStartException {
        if (mobileDeviceInfo == null) {
            return;
        }
        if (driver != null) {
            closeApp();
            Thread.sleep(2000);
        }
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();

        MobileDriverType mobileDriverType = getMobileDriverType(mobileDeviceInfo);
        if (mobileDriverType == null) {
            throw new ExecutionException(StringConstants.DIA_ERROR_MSG_OS_NOT_SUPPORT);
        }

        MobileDriverConnector mobileDriverConnector = getMobileDriverConnector(mobileDriverType, projectDir);

        if (mobileDriverConnector == null) {
            throw new ExecutionException(StringConstants.DIA_ERROR_MSG_OS_NOT_SUPPORT);
        }

        mobileDriverConnector = (MobileDriverConnector) mobileDriverConnector.clone();
        mobileDriverConnector.setDevice(mobileDeviceInfo);
        Map<String, IDriverConnector> driverConnectors = new HashMap<String, IDriverConnector>(2);
        driverConnectors.put(MobileDriverFactory.MOBILE_DRIVER_PROPERTY, mobileDriverConnector);

        String logFilePath = projectDir + File.separator + "appium.log";
        RunConfiguration.setAppiumLogFilePath(logFilePath);

        DefaultExecutionSetting generalExecutionSetting = new DefaultExecutionSetting();
        generalExecutionSetting.setTimeout(60);

        RunConfiguration.setExecutionSetting(
                ExecutionUtil.getExecutionProperties(generalExecutionSetting, driverConnectors, null));

        if (!AppiumDriverManager.isAppiumServerStarted(1)) {
            createAppiumLogTailer(logFilePath);
        }

        mobileDeviceInfo.updateRuntimeEnvironmentVariables();

        AppiumDriverManager.startAppiumServerJS(SERVER_START_TIMEOUT,
                getAdditionalEnvironmentVariables(mobileDriverType));
        driver = MobileDriverFactory.startLocalMobileDriver(mobileDriverType, mobileDeviceInfo.getDeviceId(),
                mobileDeviceInfo.getDeviceName(), mobileDeviceInfo.getDeviceOSVersion(), appFile,
                uninstallAfterCloseApp);

        appiumServerProcess = AppiumDriverManager.getAppiumSeverProcess();

        iosWebKitProcess = AppiumDriverManager.getIosWebKitProcess();
    }

    private void createAppiumLogTailer(String logFilePath) {
        appiumTailerThread = new Thread(new Tailer(new File(logFilePath), new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                if (streamHandler != null) {
                    streamHandler.handleOutput(line);
                }
            }
        }, 100L, true));
        appiumTailerThread.start();
    }

    private void closeAppiumTailerThread() {
        if (appiumTailerThread != null && appiumTailerThread.isAlive()) {
            appiumTailerThread.interrupt();
        }
        appiumTailerThread = null;
    }

    public void startMobileAppOnCloudDevices(RemoteWebRunConfiguration runConfiguration, String applicationId)
            throws InterruptedException, MalformedURLException, MobileDriverInitializeException {
        if (driver != null) {
            closeApp();
            Thread.sleep(2000);
        }
        DefaultExecutionSetting generalExecutionSetting = new DefaultExecutionSetting();
        generalExecutionSetting.setTimeout(60);

        RunConfiguration.setExecutionSetting(ExecutionUtil.getExecutionProperties(generalExecutionSetting,
                runConfiguration.getDriverConnectors(), null));

        RemoteWebDriverConnector remoteDriverConnector = runConfiguration.getRemoteDriverConnector();
        Map<String, Object> userConfigProperties = remoteDriverConnector.getUserConfigProperties();
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(userConfigProperties);
        driver = MobileDriverFactory.startRemoteMobileDriver(runConfiguration.getRemoteServerUrl(), desiredCapabilities,
                remoteDriverConnector.getMobileDriverType(), applicationId);
    }

    public void startMobileApp(KobitonDevice kobitonDevice, KobitonApplication kobitonApplication)
            throws InterruptedException, IOException, AppiumStartException, MobileDriverInitializeException {
        if (kobitonDevice == null || kobitonApplication == null) {
            return;
        }
        if (driver != null) {
            closeApp();
            Thread.sleep(2000);
        }
        KobitonDriverConnector connector = new KobitonDriverConnector(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        connector.setKobitonDevice(kobitonDevice);
        connector.setMobileDriverType(getMobileDriverType(kobitonDevice));
        connector.setApiKey(KobitonPreferencesProvider.getKobitonApiKey());
        connector.setUserName(KobitonPreferencesProvider.getKobitonUserName());
        Map<String, IDriverConnector> driverConnectors = new HashMap<String, IDriverConnector>(2);
        driverConnectors.put(DriverFactory.MOBILE_DRIVER_PROPERTY, connector);
        driverConnectors.put(DriverFactory.WEB_UI_DRIVER_PROPERTY, connector);

        DefaultExecutionSetting generalExecutionSetting = new DefaultExecutionSetting();
        generalExecutionSetting.setTimeout(60);

        RunConfiguration.setExecutionSetting(
                ExecutionUtil.getExecutionProperties(generalExecutionSetting, driverConnectors, null));
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(kobitonDevice.toDesireCapabilitiesMap());
        driver = MobileDriverFactory.startRemoteMobileDriver(connector.getRemoteServerUrl(), desiredCapabilities,
                connector.getMobileDriverType(), kobitonApplication.buildAutomationKey());
    }

    public static MobileDriverType getMobileDriverType(KobitonDevice kobitonDevice) {
        if (kobitonDevice == null || kobitonDevice.getCapabilities() == null) {
            return null;
        }
        if (KobitonDevice.PLATFORM_NAME_IOS.equals(kobitonDevice.getCapabilities().getPlatformName())) {
            return MobileDriverType.IOS_DRIVER;
        }
        if (KobitonDevice.PLATFORM_NAME_ANDROID.equals(kobitonDevice.getCapabilities().getPlatformName())) {
            return MobileDriverType.ANDROID_DRIVER;
        }
        return null;
    }

    public AppiumDriver<?> getDriver() {
        return driver;
    }

    private Map<String, String> getAdditionalEnvironmentVariables(MobileDriverType mobileDriverType)
            throws IOException, InterruptedException, AndroidSetupException {
        if (mobileDriverType == MobileDriverType.ANDROID_DRIVER) {
            return AndroidDeviceInfo.getAndroidAdditionalEnvironmentVariables();
        }
        if (mobileDriverType == MobileDriverType.IOS_DRIVER) {
            return IosDeviceInfo.getIosAdditionalEnvironmentVariables();
        }
        return new HashMap<String, String>();
    }

    public static MobileDriverType getMobileDriverType(MobileDeviceInfo mobileDeviceInfo) {
        if (mobileDeviceInfo == null) {
            return null;
        }
        if (mobileDeviceInfo instanceof AndroidDeviceInfo) {
            return MobileDriverType.ANDROID_DRIVER;
        }
        if (mobileDeviceInfo instanceof IosDeviceInfo) {
            return MobileDriverType.IOS_DRIVER;
        }
        return null;
    }

    private static MobileDriverConnector getMobileDriverConnector(MobileDriverType mobileDriverType,
            String projectDirectory) throws IOException {
        switch (mobileDriverType) {
            case ANDROID_DRIVER:
                return new AndroidDriverConnector(getInternalSettingFolder(projectDirectory));
            case IOS_DRIVER:
                return new IosDriverConnector(getInternalSettingFolder(projectDirectory));
        }
        return null;
    }

    private static String getInternalSettingFolder(String projectDirectory) {
        return projectDirectory + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME;
    }

    public boolean closeApp() {
        try {
            if (null != driver && null != ((RemoteWebDriver) driver).getSessionId()) {
                driver.quit();
            }
            if (appiumServerProcess != null && appiumServerProcess.isAlive()) {
                ProcessUtil.terminateProcess(appiumServerProcess);
            }
            if (iosWebKitProcess != null && iosWebKitProcess.isAlive()) {
                ProcessUtil.terminateProcess(iosWebKitProcess);
            }
            closeAppiumTailerThread();
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        } finally {
            driver = null;
            appiumServerProcess = null;
            iosWebKitProcess = null;
        }
    }

    public String captureScreenshot() throws Exception {

        String screenshotFolder = Util.getDefaultMobileScreenshotPath();
        File screenshot = driver.getScreenshotAs(OutputType.FILE);
        if (!screenshot.exists()) {
            throw new Exception(StringConstants.DIA_ERROR_MSG_UNABLE_TO_CAPTURE_SCREEN);
        }
        String fileName = new String("screenshot_" + new Date().getTime() + ".jpg");
        String path = screenshotFolder + System.getProperty("file.separator") + fileName;
        FileUtils.copyFile(screenshot, new File(path));
        try {
            FileUtils.forceDelete(screenshot);
        } catch (Exception e) {}
        return path;
    }

    public TreeMobileElement getMobileObjectRoot() {
        try {
            if (driver instanceof IOSDriver) {
                return getIosObjectRoot();
            } else {
                String pageSource = driver.getPageSource();
                if (StringUtils.isBlank(pageSource)) {
                    return null;
                }
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = null;
                try {
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(pageSource));
                    doc = db.parse(is);
                } catch (SAXParseException e) {
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(removeEscapeCharacter(pageSource)));
                    doc = db.parse(is);
                }
                Element rootElement = doc.getDocumentElement();

                AndroidSnapshotMobileElement htmlMobileElementRootNode = new AndroidSnapshotMobileElement();

                htmlMobileElementRootNode.getAttributes().put(AndroidProperties.ANDROID_CLASS,
                        rootElement.getTagName());
                htmlMobileElementRootNode.render(rootElement);
                return htmlMobileElementRootNode;
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return null;
        }
    }

    public static String removeEscapeCharacter(String contentBuilder) {
        String pattern = "(\\\"([^=])*\\\")";

        Pattern pattern2 = Pattern.compile(pattern);
        Matcher matcher = pattern2.matcher(contentBuilder);
        StrBuilder sb = new StrBuilder(contentBuilder);

        while (matcher.find()) {
            String str = matcher.group(1).substring(1, matcher.group(1).length() - 1);
            sb = sb.replaceFirst(StrMatcher.stringMatcher(str), StringEscapeUtils.escapeXml(str));
        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private TreeMobileElement getIosObjectRoot() throws ParserConfigurationException, SAXException, IOException {
        try {
            if (AppiumDriverManager.getXCodeVersion() >= 8) {
                return getXCUIObjectRoot();
            }
        } catch (java.util.concurrent.ExecutionException e) {
            // No xcode found, return xcuitest by default
            return getXCUIObjectRoot();
        }

        Map<Object, Object> map = (Map<Object, Object>) driver
                .executeScript("UIATarget.localTarget().frontMostApp().getTree()");
        JSONObject jsonObject = new JSONObject(map);
        IosSnapshotMobileElement htmlMobileElementRootNode = new IosSnapshotMobileElement();
        htmlMobileElementRootNode.render(jsonObject);
        return htmlMobileElementRootNode;
    }

    private TreeMobileElement getXCUIObjectRoot() throws ParserConfigurationException, SAXException, IOException {
        String pageSource = driver.getPageSource();
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(pageSource));
        Document doc = db.parse(is);
        Element rootElement = doc.getDocumentElement();
        Element appElement = null;
        NodeList childElementNodes = rootElement.getChildNodes();
        int count = childElementNodes.getLength();
        for (int i = 0; i < count; i++) {
            Node node = childElementNodes.item(i);
            if (node instanceof Element) {
                appElement = (Element) node;
            }
        }
        if (appElement == null) {
            return null;
        }

        IosXCUISnapshotMobileElement htmlMobileElementRootNode = new IosXCUISnapshotMobileElement();

        htmlMobileElementRootNode.getAttributes().put(IOSProperties.IOS_TYPE, appElement.getTagName());
        htmlMobileElementRootNode.render(appElement);
        return htmlMobileElementRootNode;
    }

    public AppiumStreamHandler getStreamHandler() {
        return streamHandler;
    }

    public void setStreamHandler(AppiumStreamHandler streamHandler) {
        this.streamHandler = streamHandler;
    }
}
