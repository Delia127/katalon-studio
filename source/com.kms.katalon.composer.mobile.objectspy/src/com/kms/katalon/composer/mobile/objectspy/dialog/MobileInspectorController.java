package com.kms.katalon.composer.mobile.objectspy.dialog;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.impl.DefaultExecutionSetting;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.device.AndroidDeviceInfo;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;
import com.kms.katalon.execution.util.ExecutionUtil;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;

public class MobileInspectorController {

    private static final int SERVER_START_TIMEOUT = 60;

    private AppiumDriver<?> driver;

    public MobileInspectorController() {
    }

    public void startMobileApp(MobileDeviceInfo mobileDeviceInfo, String appFile, boolean uninstallAfterCloseApp)
            throws ExecutionException, InterruptedException, IOException, AppiumStartException,
            MobileDriverInitializeException, IOSWebkitStartException {
        if (mobileDeviceInfo == null) {
            return;
        }
        if (driver != null) {
            driver.quit();
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

        // Only use system properties in inspecting mode.
        mobileDriverConnector.getUserConfigProperties().clear();

        Map<String, IDriverConnector> driverConnectors = new HashMap<String, IDriverConnector>(1);
        driverConnectors.put(MobileDriverFactory.MOBILE_DRIVER_PROPERTY, mobileDriverConnector);
        DefaultExecutionSetting generalExecutionSetting = new DefaultExecutionSetting();
        generalExecutionSetting.setTimeout(60);

        RunConfiguration.setExecutionSetting(ExecutionUtil.getExecutionProperties(generalExecutionSetting,
                driverConnectors));
        RunConfiguration.setAppiumLogFilePath(projectDir + File.separator + "appium.log");

        AppiumDriverManager.startAppiumServerJS(SERVER_START_TIMEOUT,
                getAdditionalEnvironmentVariables(mobileDriverType));
        driver = MobileDriverFactory.startMobileDriver(mobileDriverType, mobileDeviceInfo.getDeviceId(),
                mobileDeviceInfo.getDeviceName(), appFile, uninstallAfterCloseApp);
    }

    private Map<String, String> getAdditionalEnvironmentVariables(MobileDriverType mobileDriverType) throws IOException {
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
            MobileDriverFactory.closeDriver();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return true;
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
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(pageSource));
                Document doc = db.parse(is);
                Element rootElement = doc.getDocumentElement();
                
                AndroidSnapshotMobileElement htmlMobileElementRootNode = new AndroidSnapshotMobileElement();

                htmlMobileElementRootNode.getAttributes()
                        .put(AndroidProperties.ANDROID_CLASS, rootElement.getTagName());
                htmlMobileElementRootNode.render(rootElement);
                return htmlMobileElementRootNode;
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            return null;
        }
    }

    private TreeMobileElement getIosObjectRoot()
            throws java.util.concurrent.ExecutionException, ParserConfigurationException, SAXException, IOException {
        if (AppiumDriverManager.getXCodeVersion() >= 8) {
            return getXCUIObjectRoot();
        }
        @SuppressWarnings("unchecked")
        Map<Object, Object> map = (Map<Object, Object>) driver.executeScript("UIATarget.localTarget().frontMostApp().getTree()");
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
        
        IosXCUISnapshotMobileElement htmlMobileElementRootNode = new IosXCUISnapshotMobileElement();

        htmlMobileElementRootNode.getAttributes()
                .put(IOSProperties.IOS_TYPE, rootElement.getTagName());
        htmlMobileElementRootNode.render(rootElement);
        return htmlMobileElementRootNode;
    }

}
