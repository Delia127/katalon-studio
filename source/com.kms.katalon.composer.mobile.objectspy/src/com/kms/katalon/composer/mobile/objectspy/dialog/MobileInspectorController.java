package com.kms.katalon.composer.mobile.objectspy.dialog;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.util.Util;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.exception.AppiumStartException;
import com.kms.katalon.core.mobile.exception.IOSWebkitStartException;
import com.kms.katalon.core.mobile.exception.MobileDriverInitializeException;
import com.kms.katalon.core.mobile.keyword.AndroidProperties;
import com.kms.katalon.core.mobile.keyword.GUIObject;
import com.kms.katalon.core.mobile.keyword.IOSProperties;
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory;
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

public class MobileInspectorController {

    private AppiumDriver<?> driver;

    public MobileInspectorController() throws Exception {
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

        MobileDriverFactory.startMobileDriver(mobileDeviceInfo.getDeviceId(), mobileDriverType, appFile,
                uninstallAfterCloseApp);

        driver = MobileDriverFactory.getDriver();
    }

    private static MobileDriverType getMobileDriverType(MobileDeviceInfo mobileDeviceInfo) {
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
            if (driver == null) {
                return false;
            } else {
                driver.quit();
                driver = null;
            }
        } catch (Exception e) {}
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

    public MobileElement getMobileObjectRoot() {
        MobileElement htmlMobileElementRootNode = new MobileElement();
        try {
            if (driver instanceof IOSDriver) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) driver.executeScript("UIATarget.localTarget().frontMostApp().getTree()");
                JSONObject jsonObject = new JSONObject(map);
                renderTree(jsonObject, htmlMobileElementRootNode);
            } else {
                String pageSource = driver.getPageSource();
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(pageSource));
                Document doc = db.parse(is);
                renderTree(doc.getDocumentElement(), htmlMobileElementRootNode);
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
        return htmlMobileElementRootNode;
    }

    private void renderTree(JSONObject jsonObject, MobileElement currentNode) throws Exception {
        if (jsonObject == null) {
            return;
        }
        convertXMLElementToWebElementForIos(jsonObject, currentNode);
        // Create child-Node
        if (jsonObject.has("children")) {
            JSONArray childrens = jsonObject.getJSONArray("children");
            for (int i = 0; i < childrens.length(); i++) {
                JSONObject child = childrens.getJSONObject(i);
                MobileElement childNode = new MobileElement();
                renderTree(child, childNode);
                currentNode.getChildrenElement().add(childNode);
            }
        }
    }

    private void renderTree(Element xmlElement, MobileElement htmlMobileElement) {
        if (xmlElement == null) {
            return;
        }
        convertXMLElementToWebElementForAndroid(xmlElement, htmlMobileElement);
        // Create child-Node
        if (xmlElement.hasChildNodes()) {
            int count = xmlElement.getChildNodes().getLength();
            for (int i = 0; i < count; i++) {
                org.w3c.dom.Node node = xmlElement.getChildNodes().item(i);
                if ((node != null) && (node instanceof Element)) {
                    MobileElement childNode = new MobileElement();
                    renderTree((Element) node, childNode);
                    htmlMobileElement.getChildrenElement().add(childNode);
                }
            }
        }
    }

    private MobileElement convertXMLElementToWebElementForAndroid(Element xmlElement, MobileElement htmlMobileElement) {

        htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_CLASS,
                xmlElement.getAttribute(AndroidProperties.ANDROID_CLASS));

        String instance = "0";
        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_INSTANCE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_INSTANCE).length() > 0)) {
            instance = xmlElement.getAttribute(AndroidProperties.ANDROID_INSTANCE);
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_INSTANCE, instance);
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_TEXT)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_TEXT).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_TEXT,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_TEXT));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_RESOURCE_ID)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_RESOURCE_ID).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_RESOURCE_ID,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_RESOURCE_ID));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_PACKAGE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_PACKAGE).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_PACKAGE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_PACKAGE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_CONTENT_DESC)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_CONTENT_DESC).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_CONTENT_DESC,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_CONTENT_DESC));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_CHECKABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_CHECKABLE).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_CHECKABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_CHECKABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_CHECKED)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_CHECKED).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_CHECKED,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_CHECKED));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_CLICKABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_CLICKABLE).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_CLICKABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_CLICKABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_ENABLED)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_ENABLED).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_ENABLED,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_ENABLED));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_FOCUSABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_FOCUSABLE).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_FOCUSABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_FOCUSABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_FOCUSED)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_FOCUSED).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_FOCUSED,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_FOCUSED));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_SCROLLABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_SCROLLABLE).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_SCROLLABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_SCROLLABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_LONG_CLICKABLE)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_LONG_CLICKABLE).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_LONG_CLICKABLE,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_LONG_CLICKABLE));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_PASSWORD)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_PASSWORD).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_PASSWORD,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_PASSWORD));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_SELECTED)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_SELECTED).length() > 0)) {
            htmlMobileElement.getAttributes().put(AndroidProperties.ANDROID_SELECTED,
                    xmlElement.getAttribute(AndroidProperties.ANDROID_SELECTED));
        }

        if (xmlElement.hasAttribute(AndroidProperties.ANDROID_BOUNDS)
                && (xmlElement.getAttribute(AndroidProperties.ANDROID_BOUNDS).length() > 0)) {
            String bounds = xmlElement.getAttribute(AndroidProperties.ANDROID_BOUNDS);
            int left = Integer.parseInt(bounds.substring(1, bounds.indexOf(',')));
            int top = Integer.parseInt(bounds.substring(bounds.indexOf(',') + 1, bounds.indexOf(']')));
            int right = Integer.parseInt(bounds.substring(bounds.lastIndexOf('[') + 1, bounds.lastIndexOf(',')));
            int bottom = Integer.parseInt(bounds.substring(bounds.lastIndexOf(',') + 1, bounds.lastIndexOf(']')));

            htmlMobileElement.getAttributes().put(GUIObject.X, String.valueOf(left));
            htmlMobileElement.getAttributes().put(GUIObject.Y, String.valueOf(top));
            htmlMobileElement.getAttributes().put(GUIObject.WIDTH, String.valueOf(right - left));
            htmlMobileElement.getAttributes().put(GUIObject.HEIGHT, String.valueOf(bottom - top));
        }

        String guiName = htmlMobileElement.getAttributes().get(AndroidProperties.ANDROID_CLASS);
        if (htmlMobileElement.getAttributes().get(AndroidProperties.ANDROID_TEXT) != null) {
            guiName += " - " + htmlMobileElement.getAttributes().get(AndroidProperties.ANDROID_TEXT);
        }
        if (guiName != null && guiName.contains("\n")) {
            guiName = guiName.replace("\n", "");
        }
        guiName += instance;
        htmlMobileElement.setName(guiName);
        return htmlMobileElement;
    }

    private MobileElement convertXMLElementToWebElementForIos(JSONObject jsonObject, MobileElement currentMobileElement)
            throws Exception {

        // Extract web element property info
        Map<String, String> properties = currentMobileElement.getAttributes();

        String propType = jsonObject.getString(IOSProperties.IOS_TYPE);
        properties.put(IOSProperties.IOS_TYPE, propType);

        String propName = null;
        if (jsonObject.has(IOSProperties.IOS_NAME) && (jsonObject.getString(IOSProperties.IOS_NAME).length() > 0)) {
            properties.put(IOSProperties.IOS_NAME, propName = jsonObject.getString(IOSProperties.IOS_NAME));
        }

        String propLabel = null;
        if (jsonObject.has(IOSProperties.IOS_LABEL) && (jsonObject.getString(IOSProperties.IOS_LABEL).length() > 0)) {
            properties.put(IOSProperties.IOS_LABEL, propLabel = jsonObject.getString(IOSProperties.IOS_LABEL));
        }

        String propValue = null;
        if (jsonObject.has(IOSProperties.IOS_VALUE) && (jsonObject.getString(IOSProperties.IOS_VALUE).length() > 0)) {
            properties.put(IOSProperties.IOS_VALUE, propValue = jsonObject.getString(IOSProperties.IOS_VALUE));
        }

        if (jsonObject.has(IOSProperties.IOS_HINT) && (jsonObject.getString(IOSProperties.IOS_HINT).length() > 0)) {
            properties.put(IOSProperties.IOS_HINT, jsonObject.getString(IOSProperties.IOS_HINT));
        }

        if (jsonObject.has(IOSProperties.IOS_RECT)) {
            JSONObject rect = jsonObject.getJSONObject(IOSProperties.IOS_RECT);
            if (rect.has(IOSProperties.IOS_ORIGIN)) {
                JSONObject origin = rect.getJSONObject(IOSProperties.IOS_ORIGIN);
                if (origin.has(GUIObject.X)) {
                    properties.put(GUIObject.X, String.valueOf(origin.getDouble(GUIObject.X)));
                }
                if (origin.has(GUIObject.Y)) {
                    properties.put(GUIObject.Y, String.valueOf(origin.getDouble(GUIObject.Y)));
                }
            }
            if (rect.has(IOSProperties.IOS_SIZE)) {
                JSONObject size = rect.getJSONObject(IOSProperties.IOS_SIZE);
                if (size.has(GUIObject.WIDTH)) {
                    properties.put(GUIObject.WIDTH, String.valueOf(size.getDouble(GUIObject.WIDTH)));
                }
                if (size.has(GUIObject.HEIGHT)) {
                    properties.put(GUIObject.HEIGHT, String.valueOf(size.getDouble(GUIObject.HEIGHT)));
                }
            }
        }

        if (jsonObject.has(IOSProperties.IOS_ENABLED)) {
            properties.put(IOSProperties.IOS_ENABLED, String.valueOf(jsonObject.getBoolean(IOSProperties.IOS_ENABLED)));
        }

        if (jsonObject.has(IOSProperties.IOS_VALID)) {
            properties.put(IOSProperties.IOS_VALID, String.valueOf(jsonObject.getBoolean(IOSProperties.IOS_VALID)));
        }

        if (jsonObject.has(IOSProperties.IOS_VISIBLE)) {
            properties.put(IOSProperties.IOS_VISIBLE, String.valueOf(jsonObject.getBoolean(IOSProperties.IOS_VISIBLE)));
        }

        String guiName = propType;
        if (propName != null) {
            guiName = guiName + " - " + propName;
        } else {
            if (propLabel != null) {
                guiName = guiName + " - " + propLabel;
            } else {
                if (propValue != null) {
                    guiName = guiName + " - " + propValue;
                }
            }
        }

        currentMobileElement.setName(guiName);
        return currentMobileElement;
    }

    /*
     * private WebElementProperty createProperty(String propertyName, String propertyValue, WebElement parentWebElement)
     * { WebElementProperty propEntity = new WebElementProperty(); propEntity.setId(-1L);
     * propEntity.setName(propertyName); propEntity.setType("STRING"); String propValue = propertyValue; if ((propValue
     * != null) && (propValue.length() > 1000)) { propValue = propValue.substring(0, 1000); }
     * propEntity.setValue(propValue); propEntity.setMatchCondition(WebElementProperty.defaultMatchCondition);
     * propEntity.setIsSelected(false); propEntity.setWebElement(parentWebElement);
     * propEntity.setBrowser(com.kms.katalon.service.model.TargetType.All); return propEntity; }
     * public BaseTreeNode getRootWithElementByName(String searchText, BaseTreeNode root) { BaseTreeNode filteredNode =
     * new BaseTreeNode(root.getId(), root.getName(), root.getFullPath(), root.getNodeType());
     * filteredNode.setExtraInfo(root.getExtraInfo()); getNodeByName(root, filteredNode, searchText); return
     * filteredNode; }
     * private boolean getNodeByName(BaseTreeNode node, BaseTreeNode filteredNode, String searchText) { boolean
     * existFilteredChild = false; for (int i = 0; i < node.getChildCount(); i++) { BaseTreeNode childNode =
     * node.getChildAt(i); BaseTreeNode filteredChildNode = cloneTreeNode(childNode); boolean exist =
     * getNodeByName(childNode, filteredChildNode, searchText); if (exist) {
     * filteredNode.addChildNode(filteredChildNode); existFilteredChild = true; } else { if
     * (childNode.getName().toLowerCase().contains(searchText.toLowerCase())) {
     * filteredNode.addChildNode(filteredChildNode); existFilteredChild = true; } } } return existFilteredChild; }
     * private BaseTreeNode cloneTreeNode(BaseTreeNode node) { BaseTreeNode newNode = new BaseTreeNode(node.getId(),
     * node.getName(), node.getFullPath(), node.getNodeType()); newNode.setExtraInfo(node.getExtraInfo()); return
     * newNode; }
     * public Folder getFolderByID(long id) throws Exception { return
     * dataRepository.getFolderRepository().getFolderbyId(id); }
     * public WebElement getWebElementByID(long id) { return
     * dataRepository.getWebElementRepository().getWebElementByID(id); }
     */
}
