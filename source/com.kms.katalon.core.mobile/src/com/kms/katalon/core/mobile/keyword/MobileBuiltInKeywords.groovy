package com.kms.katalon.core.mobile.keyword;

import groovy.transform.CompileStatic
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.NetworkConnectionSetting
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidKeyCode
import io.appium.java_client.ios.IOSDriver

import java.text.MessageFormat

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.openqa.selenium.Dimension
import org.openqa.selenium.OutputType
import org.openqa.selenium.Point
import org.openqa.selenium.ScreenOrientation
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.touch.TouchActions

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.helper.KeywordHelper
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.keyword.KeywordMain
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.mobile.constants.StringConstants
import com.kms.katalon.core.mobile.helper.MobileCommonHelper
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory.OsType
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject

@CompileStatic
public class MobileBuiltInKeywords extends BuiltinKeywords {
    private static final KeywordLogger logger = KeywordLogger.getInstance();

    /**
     * @deprecated<p>
     * This property is deprecated and will be removed in future releases.<p>
     * Use MobileDriverFactory.getDriver() instead
     * @see com.kms.katalon.core.mobile.keyword.MobileDriverFactory#getDriver()
     */
    @Deprecated()
    private static AppiumDriver driver;

    //Device name should be selected by user from a UI Form
    //private static String deviceName = "LGE Nexus 4 5.1.1";

    /**
     * Start up an application
     * @param appFile
     *      absolute path of the application install file
     * @param uninstallAfterCloseApp
     *      true if uninstalling the application automatically after run completed; otherwise, false
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_APPLICATION)
    public static void startApplication(String appFile, boolean uninstallAfterCloseApp, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_STARTING_APP_AT, appFile));
            MobileCommonHelper.initializeMobileDriver(appFile.toString(), uninstallAfterCloseApp);
            driver = MobileDriverFactory.getDriver();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_START_APP_AT, appFile));
        }, flowControl, MessageFormat.format(StringConstants.KW_MSG_UNABLE_TO_START_APP_AT, appFile))
    }

    /**
     * Close the current running application
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_APPLICATION)
    public static void closeApplication(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileDriverFactory.getDriver().quit();
            MobileDriverFactory.quitServer();
            logger.logPassed(StringConstants.KW_LOG_PASSED_CLOSE_APP);
        }, flowControl, StringConstants.KW_MSG_UNABLE_TO_CLOSE_APPLICATION)
    }

    /**
     * Simulate pressing back button on a mobile device (Android only)
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void pressBack(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                switchToNativeContext(driver);
                if (driver instanceof AndroidDriver) {
                    ((AndroidDriver) driver).pressKeyCode(AndroidKeyCode.BACK);
                } else {
                    KeywordMain.stepFailed(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE, flowControl, null);
                }
            } finally {
                driver.context(context)
            }
            logger.logPassed(StringConstants.KW_LOG_PASSED_PRESS_BACK_BTN);
        }, flowControl, StringConstants.KW_MSG_CANNOT_PRESS_BACK_BTN)
    }

    /**
     * Simulate swiping fingers on the mobile device
     * @param startX
     *      starting x position
     * @param startY
     *      starting y position
     * @param endX
     *      ending x position
     * @param endY
     *      ending y position
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void swipe(int startX, int startY, int endX, int endY, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileCommonHelper.swipe(MobileDriverFactory.getDriver(), startX, startY, endX, endY);
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SWIPED_FROM_STARTXY_TO_ENDXY, startX, startY, endX, endY));
        }, flowControl, StringConstants.KW_MSG_CANNOT_SWIPE_ON_DEVICE)
    }

    /**
     * Taking screenshot of the mobile device screen
     * @param fileName
     *      the absolute path of the saved screenshot image file
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void takeScreenshot(String fileName, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                switchToNativeContext(driver);
                File tempFile = driver.getScreenshotAs(OutputType.FILE);
                if (!tempFile.exists()) {
                    KeywordMain.stepFailed(StringConstants.KW_MSG_UNABLE_TO_TAKE_SCREENSHOT, flowControl, null);
                }
                try{
                    FileUtils.copyFile(tempFile, new File(fileName));
                    FileUtils.forceDelete(tempFile);
                } catch (Exception e) {
                    logger.logWarning(e.getMessage());
                    // do nothing
                }
                logger.logPassed(StringConstants.KW_LOG_PASSED_SCREENSHOT_IS_TAKEN);
            } finally {
                driver.context(context)
            }
        }, flowControl, StringConstants.KW_MSG_UNABLE_TO_TAKE_SCREENSHOT)
    }

    /**
     * Simulate opening notification action on mobile devices
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NOTIFICATION)
    public static void openNotifications(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                switchToNativeContext(driver);
                if (driver instanceof AndroidDriver) {
                    AndroidDriver androidDriver = (AndroidDriver) driver;
                    Object version = androidDriver.getCapabilities().getCapability("platformVersion");
                    if (version != null && String.valueOf(version).compareTo("4.3") >= 0) {
                        ((AndroidDriver) driver).openNotifications();
                    } else {
                        MobileCommonHelper.swipe(driver, 50, 1, 50, 300);
                    }
                } else if (driver instanceof IOSDriver) {
                    MobileCommonHelper.swipe(driver, 50, 0, 50, 300);
                }
                logger.logPassed(StringConstants.KW_MSG_PASSED_OPEN_NOTIFICATIONS);
            } finally {
                driver.context(context)
            }
        }, flowControl, StringConstants.KW_MSG_CANNOT_OPEN_NOTIFICATIONS)
    }

    /**
     * Simulate pressing home button on mobile devices (Android only)
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void pressHome(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                if (driver instanceof AndroidDriver) {
                    switchToNativeContext(driver);
                    ((AndroidDriver)driver).pressKeyCode(AndroidKeyCode.HOME);
                } else {
                    KeywordMain.stepFailed(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE, flowControl, null);
                }
                logger.logPassed(StringConstants.KW_LOG_PASSED_HOME_BTN_PRESSED);
            } finally {
                driver.context(context)
            }
        }, flowControl, StringConstants.KW_MSG_CANNOT_PRESS_HOME_BTN);
    }

    /**
     * Get the manufacturer of the current active mobile device
     * @param flowControl
     * @return
     *      the manufacturer of the current active mobile device
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getDeviceManufacturer(FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            String deviceId = MobileDriverFactory.getDeviceId(MobileDriverFactory.getDeviceName());
            OsType deviceOs = MobileDriverFactory.getDeviceOs(deviceId);
            String manufacturer = null;
            switch (deviceOs) {
                case OsType.IOS:
                    manufacturer = StringConstants.KW_MANUFACTURER_APPLE;
                    break;
                case OsType.ANDROID:
                    String adbPath = System.getenv("ANDROID_HOME") + File.separator + "platform-tools" + File.separator + "adb";
                    String[] cmd = [adbPath, "-s", deviceId, "shell", "getprop", "ro.product.manufacturer"]
                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    Process p = pb.start();
                    p.waitFor();
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    manufacturer = br.readLine();
                    br.close();
                    break;
                default:
                    KeywordMain.stepFailed(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE, flowControl, null);
            }
            logger.logPassed(MessageFormat.format(StringConstants.KW_MSG_DEVICE_MANUFACTURER_IS, manufacturer));
            return manufacturer;
        }, flowControl, StringConstants.KW_MSG_CANNOT_GET_MANUFACTURER);
    }

    /**
     * Get the device os of the current active mobile device
     * @param flowControl
     * @return
     *      the device os of the current active mobile device
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getDeviceOS(FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            String deviceId = MobileDriverFactory.getDeviceId(MobileDriverFactory.getDeviceName());
            OsType deviceOs = MobileDriverFactory.getDeviceOs(deviceId);
            String osName = null;
            switch (deviceOs) {
                case OsType.IOS:
                    osName = StringConstants.KW_OS_IOS;
                    break;
                case OsType.ANDROID:
                    String adbPath = System.getenv("ANDROID_HOME") + File.separator + "platform-tools" + File.separator + "adb";
                    String[] cmd = [adbPath, "-s", deviceId, "shell", "getprop", "net.bt.name"]
                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    Process p = pb.start();
                    p.waitFor();
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    osName = br.readLine();
                    br.close();
                    break;
                default:
                    KeywordMain.stepFailed(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE, flowControl, null);
            }
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DEVICE_OS_NAME, osName));
            return osName;
        }, flowControl, StringConstants.KW_MSG_CANNOT_GET_OS_NAME);
    }

    /**
     * Get the device os version of the current active mobile device
     * @param flowControl
     * @return
     *      the device os version of the current active mobile device
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getDeviceOSVersion(FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            String osVersion = MobileCommonHelper.getDeviceOSVersion();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DEVICE_OS_VER_IS, osVersion));
            return osVersion;
        }, flowControl, StringConstants.KW_MSG_CANNOT_GET_OS_VER);
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getDeviceModel(FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            String model = MobileCommonHelper.getDeviceModel();
            logger.logPassed(model);
            return model;
        }, flowControl, StringConstants.KW_MSG_CANNOT_GET_DEVICE_MODEL);
    }

    /**
     * Simulate closing notification action on mobile devices
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NOTIFICATION)
    public static void closeNotifications(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                switchToNativeContext(driver);
                int height = driver.manage().window().getSize().height;
                MobileCommonHelper.swipe(driver, 50, height - 1, 50, 1);
                logger.logPassed(StringConstants.KW_LOG_PASSED_NOTIFICATION_CLOSED);
            } finally {
                driver.context(context)
            }

        }, flowControl, StringConstants.KW_MSG_CANNOT_CLOSE_NOTIFICATIONS);
    }

    /**
     * Simulate toggling airplane mode on mobile devices
     * @param mode
     *          ["yes", "on", "true"] to turn on airplane mode; otherwise, airplane mode is turn off
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void toggleAirplaneMode(String mode, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                switchToNativeContext(driver);

                boolean isTurnOn = false;
                if (StringUtils.equalsIgnoreCase("yes", mode)
                || StringUtils.equalsIgnoreCase("on", mode)
                || StringUtils.equalsIgnoreCase("true", mode)) {
                    isTurnOn = true;
                }
                if (driver instanceof AndroidDriver) {
                    AndroidDriver androidDriver = (AndroidDriver) driver;
                    androidDriver.setNetworkConnection(new NetworkConnectionSetting(isTurnOn, !isTurnOn, !isTurnOn));
                } else {
                    String deviceModel = MobileCommonHelper.getDeviceModel();
                    //ResourceBundle resourceBundle = ResourceBundle.getBundle("resource");
                    //String[] point = resourceBundle.getString(deviceModel).split(";");
                    if(MobileCommonHelper.deviceModels.get(deviceModel) == null){
                        throw new StepFailedException("Device info not found. Please use ideviceinfo -u <udid> to read ProductType of iOS devices");
                    }
                    if(MobileCommonHelper.airPlaneButtonCoords.get(MobileCommonHelper.deviceModels.get(deviceModel)) == null
                    || MobileCommonHelper.airPlaneButtonCoords.get(MobileCommonHelper.deviceModels.get(deviceModel)).equals("")) {
                        throw new StepFailedException("AirplaneMode button coordinator not found.");
                    }

                    String[] point = MobileCommonHelper.airPlaneButtonCoords.get(MobileCommonHelper.deviceModels.get(deviceModel)).split(";");
                    int x = Integer.parseInt(point[0]);
                    int y = Integer.parseInt(point[1]);
                    Dimension size = driver.manage().window().getSize();
                    MobileCommonHelper.swipe(driver, 50, size.height, 50, size.height - 300);
                    Thread.sleep(500);
                    driver.tap(1, x, y, 500);
                    MobileCommonHelper.swipe(driver, 50, 1, 50, size.height);
                }
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TOGGLE_AIRPLANE_MODE, mode));
            } finally {
                driver.context(context)
            }

        }, flowControl, StringConstants.KW_MSG_CANNOT_TOGGLE_AIRPLANE_MODE);
    }

    /**
     * Running the active application in background
     * @param seconds
     *      amounts of time (in seconds) for the application to run in background
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_APPLICATION)
    public static void runIOSAppInBackgroundAndWait(int seconds, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver<?> driver = MobileDriverFactory.getDriver();
            String osVersion = MobileCommonHelper.getDeviceOSVersion();
            int majorversion = Integer.parseInt(osVersion.split("\\.")[0]);
            if (majorversion >= 8) {
                String command = String.format("UIATarget.localTarget().deactivateAppForDuration(%d);", (int)(seconds/2));
                driver.executeScript(command + command);
            } else {
                String command = String.format("UIATarget.localTarget().deactivateAppForDuration(%d);", seconds);
                driver.executeScript(command);
            }
            logger.logPassed(StringConstants.KW_LOG_PASSED_RUN_IOS_APP_PASSED);
        }, flowControl, StringConstants.KW_MSG_CANNOT_RUN_IOS_APP_IN_BACKGROUND);
    }

    /**
     * Get text of a mobile element
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      the text of the mobile element
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static String getText(TestObject to, Object timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            int timeoutValue = Integer.parseInt(timeout.toString());
            WebElement element = findElement(to, timeoutValue * 1000);
            if(element == null){
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()), flowControl, null);
            }
            String text = element.getText();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_TEXT_IS, to.getObjectId(), text));
            return text;
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_GET_ELEMENT_TEXT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_GET_ELEMENT_TEXT);
    }

    /**
     * Set text to a mobile element
     * @param to
     *      represent a mobile element
     * @param text
     *      the text to set to the mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static void setText(TestObject to, String text, int timeout, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            WebElement element = findElement(to, timeout * 1000);
            if (element == null) {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()), flowControl, null);
            }
            element.clear();
            element.sendKeys(text.toString());
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TEXT_HAS_BEEN_SET_TO_ELEMENT, text, to.getObjectId()));
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_SET_ELEMENT_TEXT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_SET_ELEMENT_TEXT);
    }

    /**
     * Tap on an mobile element
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void tap(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            WebElement element = findElement(to, timeout * 1000);
            if(element == null){
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()), flowControl, null);
            }
            ((MobileElement) element).tap(1, 1);
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TAPPED_ON_ELEMENT, to.getObjectId()));
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_TAP_ON_ELEMENT_X, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_TAP_ON_ELEMENT);
    }

    /**
     * Get a specific attribute of a mobile element
     * @param to
     *      represent a mobile element
     * @param name
     *      name of the attribute to get
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      value of the attribute
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ATTRIBUTE)
    public static String getAttribute(TestObject to, String name, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            WebElement element = findElement(to, timeout * 1000);
            if (element == null) {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()), flowControl, null);
            }
            String val = "";
            switch (name.toString()) {
                case GUIObject.HEIGHT:
                    val = String.valueOf(element.getSize().height);

                case GUIObject.WIDTH:
                    val = String.valueOf(element.getSize().width);

                case GUIObject.X:
                    val = String.valueOf(element.getLocation().x);

                case GUIObject.Y:
                    val = String.valueOf(element.getLocation().y);

                default:
                    val = element.getAttribute(name);
            }
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_HAS_ATTR, to.getObjectId(), name, val));
            return val;
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_GET_ELEMENT_X_ATTR_Y, to.getObjectId(), name)
        : StringConstants.KW_MSG_FAILED_TO_GET_ELEMENT_ATTR);
    }

    /**
     * Wait for a mobile element to present
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      true if the element is presented; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementPresent(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            WebElement element = findElement(to, timeout * 1000);
            if (element != null) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_PRESENTED, to.getObjectId()));
                return true;
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()), flowControl, null);
                return false;
            }
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_WAIT_FOR_ELEMENT_PRESENT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_WAIT_FOR_ELEMENT_X_PRESENT);
    }

    /**
     * Verify if a mobile element is presented
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      true if the element is presented; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementExist(TestObject to, int timeOut, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            WebElement element = findElement(to, timeOut * 1000);
            if (element != null) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_EXISTED, to.getObjectId()));
                return true;
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ELEMENT_NOT_FOUND, to.getObjectId()), flowControl, null);
                return false;
            }
        }, flowControl, to != null ?  MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_X_EXIST, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_EXIST);
    }

    /**
     * Clear text of a mobile element
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static void clearText(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            WebElement element = findElement(to, timeout * 1000);
            if (element == null) {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_ELEMENT_NOT_FOUND, to.getObjectId()), flowControl, null);
            }
            element.clear();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_TEXT_IS_CLEARED, to.getObjectId()));
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CLEAR_TEXT_OF_ELEMENT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CLEAR_TEXT_OF_ELEMENT);
    }

    /**
     * Verify if current device is in landscape mode
     * @param flowControl
     * @return
     *      true if the device is in landscape mode ; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static void verifyIsLandscape(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                switchToNativeContext(driver);
                if (driver.getOrientation() == ScreenOrientation.LANDSCAPE) {
                    logger.logPassed(StringConstants.KW_LOG_PASSED_VERIFY_LANDSCAPE);
                    return true;
                } else {
                    KeywordMain.stepFailed(StringConstants.KW_LOG_FAILED_VERIFY_LANDSCAPE, flowControl, null);
                    return false;
                }
            } finally {
                driver.context(context);
            }
        }, flowControl, StringConstants.KW_MSG_UNABLE_VERIFY_LANDSCAPE);
    }

    /**
     * Verify if current device is in portrait mode
     * @param flowControl
     * @return
     *      true if the device is in portrait mode ; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static void verifyIsPortrait(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                switchToNativeContext(driver);
                if (driver.getOrientation() == ScreenOrientation.PORTRAIT) {
                    logger.logPassed(StringConstants.KW_LOG_PASSED_VERIFY_PORTRAIT);
                    return true;
                } else {
                    KeywordMain.stepFailed(StringConstants.KW_LOG_FAILED_VERIFY_PORTRAIT, flowControl, null);
                    return false;
                }
            } finally {
                driver.context(context);
            }
        }, flowControl, StringConstants.KW_MSG_UNABLE_VERIFY_PORTRAIT);
    }

    @CompileStatic
    private static boolean switchToNativeContext(AppiumDriver driver) {
        for (String context : driver.getContextHandles()) {
            if (context.contains("NATIVE")) {
                driver.context(context);
            }
        }
    }

    /**
     * Internal method to get any appium driver from either mobile web or native app for general keywords
     */
    @CompileStatic
    private static AppiumDriver getAnyAppiumDriver() {
        AppiumDriver<?> driver = null;
        try {
            driver = MobileDriverFactory.getDriver();
        } catch (StepFailedException e) {
            // Native app not running, so get from driver store
            for (Object driverObject : RunConfiguration.getStoredDrivers()) {
                if (driverObject instanceof AppiumDriver<?>) {
                    driver = (AppiumDriver) driverObject;
                }
            }
        }
        if (driver == null) {
            throw new StepFailedException(StringConstants.KW_MSG_UNABLE_FIND_DRIVER);
        }
        return driver;
    }

    /**
     * Internal method to find a mobile element
     * @param to
     *      represent a mobile element
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @return
     * @throws Exception
     */
    @CompileStatic
    private static WebElement findElement(TestObject to, int timeOut) throws Exception {
        Date startTime = new Date();
        Date endTime;
        long span = 0;
        WebElement webElement = null;
        Point elementLocation = null;
        AppiumDriver<?> driver = MobileDriverFactory.getDriver();
        MobileSearchEngine searchEngine = new MobileSearchEngine(driver, to);

        Dimension screenSize = driver.manage().window().getSize();

        while (span < timeOut) {
            webElement = searchEngine.findWebElement(false);
            if (webElement != null) {
                elementLocation = webElement.getLocation();
                if (elementLocation.y >= screenSize.height) {
                    try {
                        if (driver instanceof AndroidDriver) {
                            TouchActions ta = new TouchActions((AndroidDriver) driver);
                            ta.down(screenSize.width / 2, screenSize.height / 2).perform();
                            ta.move(screenSize.width / 2, (int) ((screenSize.height / 2) * 0.5)).perform();
                            ta.release().perform();
                        } else {
                            driver.swipe(screenSize.width / 2, screenSize.height / 2, screenSize.width / 2,
                                    (int) ((screenSize.height / 2) * 0.5), 500);
                        }
                    } catch (Exception e) {
                    }
                } else {
                    break;
                }
            }
            endTime = new Date();
            span = endTime.getTime() - startTime.getTime();
        };

        return webElement;
    }
}
