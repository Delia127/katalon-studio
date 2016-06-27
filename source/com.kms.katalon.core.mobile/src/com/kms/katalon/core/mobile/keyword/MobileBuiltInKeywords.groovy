package com.kms.katalon.core.mobile.keyword;

import groovy.transform.CompileStatic
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.NetworkConnectionSetting
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidKeyCode
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.HideKeyboardStrategy

import java.text.MessageFormat
import java.util.concurrent.TimeUnit

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.transform.tailrec.VariableReplacedListener.*
import org.openqa.selenium.Dimension
import org.openqa.selenium.OutputType
import org.openqa.selenium.Point
import org.openqa.selenium.ScreenOrientation
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.touch.TouchActions
import org.openqa.selenium.support.ui.FluentWait

import com.google.common.base.Function
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.helper.KeywordHelper
import com.kms.katalon.core.keyword.BuiltinKeywords
import com.kms.katalon.core.keyword.KeywordMain
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.mobile.constants.StringConstants
import com.kms.katalon.core.mobile.helper.MobileCommonHelper
import com.kms.katalon.core.mobile.helper.MobileDeviceCommonHelper
import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.mobile.helper.MobileGestureCommonHelper
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject

@CompileStatic
public class MobileBuiltInKeywords extends BuiltinKeywords {
    private static final KeywordLogger logger = KeywordLogger.getInstance();

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
            MobileDriverFactory.startMobileDriver(appFile.toString(), uninstallAfterCloseApp);
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_START_APP_AT, appFile));
        }, flowControl, MessageFormat.format(StringConstants.KW_MSG_UNABLE_TO_START_APP_AT, appFile))
    }

    /**
     * Start up an application
     * @param appFile
     *      absolute path of the application install file
     * @param uninstallAfterCloseApp
     *      true if uninstalling the application automatically after run completed; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_APPLICATION)
    public static void startApplication(String appFile, boolean uninstallAfterCloseApp) throws StepFailedException {
        startApplication(appFile, uninstallAfterCloseApp, RunConfiguration.getDefaultFailureHandling());
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
            MobileDriverFactory.closeDriver();
            logger.logPassed(StringConstants.KW_LOG_PASSED_CLOSE_APP);
        }, flowControl, StringConstants.KW_MSG_UNABLE_TO_CLOSE_APPLICATION)
    }

    /**
     * Close the current running application
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_APPLICATION)
    public static void closeApplication() throws StepFailedException {
        closeApplication(RunConfiguration.getDefaultFailureHandling());
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
                internalSwitchToNativeContext(driver);
                if (driver instanceof AndroidDriver) {
                    ((AndroidDriver) driver).pressKeyCode(AndroidKeyCode.BACK);
                } else {
                    KeywordMain.stepFailed(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE, flowControl, null);
                    return;
                }
            } finally {
                driver.context(context)
            }
            logger.logPassed(StringConstants.KW_LOG_PASSED_PRESS_BACK_BTN);
        }, flowControl, StringConstants.KW_MSG_CANNOT_PRESS_BACK_BTN)
    }

    /**
     * Simulate pressing back button on a mobile device (Android only)
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void pressBack() throws StepFailedException {
        pressBack(RunConfiguration.getDefaultFailureHandling());
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
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver);
                MobileCommonHelper.swipe(driver, startX, startY, endX, endY);
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SWIPED_FROM_STARTXY_TO_ENDXY, startX, startY, endX, endY));
            } finally {
                driver.context(context)
            }
        }, flowControl, StringConstants.KW_MSG_CANNOT_SWIPE_ON_DEVICE)
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
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void swipe(int startX, int startY, int endX, int endY) throws StepFailedException {
        swipe(startX, startY, endX, endY, RunConfiguration.getDefaultFailureHandling());
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
                internalSwitchToNativeContext(driver);
                File tempFile = driver.getScreenshotAs(OutputType.FILE);
                if (!tempFile.exists()) {
                    KeywordMain.stepFailed(StringConstants.KW_MSG_UNABLE_TO_TAKE_SCREENSHOT, flowControl, null);
                    return;
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
     * Taking screenshot of the mobile device screen
     * @param fileName
     *      the absolute path of the saved screenshot image file
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void takeScreenshot(String fileName) throws StepFailedException {
        takeScreenshot(fileName, RunConfiguration.getDefaultFailureHandling());
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
                internalSwitchToNativeContext(driver);
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
     * Simulate opening notification action on mobile devices
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NOTIFICATION)
    public static void openNotifications() throws StepFailedException {
        openNotifications(RunConfiguration.getDefaultFailureHandling());
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
                    internalSwitchToNativeContext(driver);
                    ((AndroidDriver)driver).pressKeyCode(AndroidKeyCode.HOME);
                } else {
                    KeywordMain.stepFailed(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE, flowControl, null);
                    return;
                }
                logger.logPassed(StringConstants.KW_LOG_PASSED_HOME_BTN_PRESSED);
            } finally {
                driver.context(context)
            }
        }, flowControl, StringConstants.KW_MSG_CANNOT_PRESS_HOME_BTN);
    }

    /**
     * Simulate pressing home button on mobile devices (Android only)
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void pressHome() throws StepFailedException {
        pressHome(RunConfiguration.getDefaultFailureHandling());
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
            String manufacturer = MobileDriverFactory.getDeviceManufacturer();
            logger.logPassed(MessageFormat.format(StringConstants.KW_MSG_DEVICE_MANUFACTURER_IS, manufacturer));
            return manufacturer;
        }, flowControl, StringConstants.KW_MSG_CANNOT_GET_MANUFACTURER);
    }

    /**
     * Get the manufacturer of the current active mobile device
     * @return
     *      the manufacturer of the current active mobile device
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getDeviceManufacturer() throws StepFailedException {
        return getDeviceManufacturer(RunConfiguration.getDefaultFailureHandling()) ;
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
            String osName = MobileDriverFactory.getDeviceOS();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DEVICE_OS_NAME, osName));
            return osName;
        }, flowControl, StringConstants.KW_MSG_CANNOT_GET_OS_NAME);
    }

    /**
     * Get the device os of the current active mobile device
     * @return
     *      the device os of the current active mobile device
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getDeviceOS() throws StepFailedException {
        return getDeviceOS(RunConfiguration.getDefaultFailureHandling());
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
            String osVersion = MobileDriverFactory.getDeviceOSVersion();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_DEVICE_OS_VER_IS, osVersion));
            return osVersion;
        }, flowControl, StringConstants.KW_MSG_CANNOT_GET_OS_VER);
    }

    /**
     * Get the device os version of the current active mobile device
     * @return
     *      the device os version of the current active mobile device
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getDeviceOSVersion() throws StepFailedException {
        return getDeviceOSVersion(RunConfiguration.getDefaultFailureHandling());
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getDeviceModel(FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            String model = MobileDriverFactory.getDeviceModel();
            logger.logPassed(model);
            return model;
        }, flowControl, StringConstants.KW_MSG_CANNOT_GET_DEVICE_MODEL);
    }

    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getDeviceModel() throws StepFailedException {
        return getDeviceModel(RunConfiguration.getDefaultFailureHandling());
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
                internalSwitchToNativeContext(driver);
                int height = driver.manage().window().getSize().height;
                MobileCommonHelper.swipe(driver, 50, height - 1, 50, 1);
                logger.logPassed(StringConstants.KW_LOG_PASSED_NOTIFICATION_CLOSED);
            } finally {
                driver.context(context)
            }

        }, flowControl, StringConstants.KW_MSG_CANNOT_CLOSE_NOTIFICATIONS);
    }

    /**
     * Simulate closing notification action on mobile devices
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NOTIFICATION)
    public static void closeNotifications() throws StepFailedException {
        closeNotifications(RunConfiguration.getDefaultFailureHandling());
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
                internalSwitchToNativeContext(driver);

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
                    String deviceModel = MobileDriverFactory.getDeviceModel();
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
     * Simulate toggling airplane mode on mobile devices
     * @param mode
     *          ["yes", "on", "true"] to turn on airplane mode; otherwise, airplane mode is turn off
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
    public static void toggleAirplaneMode(String mode) throws StepFailedException {
        toggleAirplaneMode(mode, RunConfiguration.getDefaultFailureHandling());
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
            String osVersion = MobileDriverFactory.getDeviceOSVersion();
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
     * Running the active application in background
     * @param seconds
     *      amounts of time (in seconds) for the application to run in background
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_APPLICATION)
    public static void runIOSAppInBackgroundAndWait(int seconds) throws StepFailedException {
        runIOSAppInBackgroundAndWait(seconds, RunConfiguration.getDefaultFailureHandling());
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
    public static String getText(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if(element == null){
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()), flowControl, null);
                return;
            }
            String text = element.getText();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_TEXT_IS, to.getObjectId(), text));
            return text;
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_GET_ELEMENT_TEXT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_GET_ELEMENT_TEXT);
    }

    /**
     * Get text of a mobile element
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      the text of the mobile element
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static String getText(TestObject to, int timeout) throws StepFailedException {
        return getText(to, timeout, RunConfiguration.getDefaultFailureHandling());
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
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if (element == null) {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()), flowControl, null);
                return;
            }
            element.clear();
            element.sendKeys(text.toString());
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TEXT_HAS_BEEN_SET_TO_ELEMENT, text, to.getObjectId()));
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_SET_ELEMENT_TEXT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_SET_ELEMENT_TEXT);
    }

    /**
     * Set text to a mobile element
     * @param to
     *      represent a mobile element
     * @param text
     *      the text to set to the mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static void setText(TestObject to, String text, int timeout) throws StepFailedException {
        setText(to, text, timeout, RunConfiguration.getDefaultFailureHandling());
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
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if (element == null){
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()), flowControl, null);
                return;
            }
            ((MobileElement) element).tap(1, 1);
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_TAPPED_ON_ELEMENT, to.getObjectId()));
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_TAP_ON_ELEMENT_X, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_TAP_ON_ELEMENT);
    }

    /**
     * Tap on an mobile element
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void tap(TestObject to, int timeout) throws StepFailedException {
        tap(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Tap and hold on a mobile element for a duration
     * @param to
     *      represent a mobile element
     * @param duration
     *      duration (in seconds) that the tap is hold on the element, if set to <= 0 then will use default duration
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void tapAndHold(TestObject to, Number duration, int timeout, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileElementCommonHelper.tapAndHold(to, duration, timeout);
        }, flowControl, (to != null && duration != null) ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_TAP_AND_HOLD_ON_ELEMENT_X_WITH_DURATION_Y, 
            to.getObjectId(), MobileElementCommonHelper.getStringForDuration(duration)) : StringConstants.KW_MSG_FAILED_TO_TAP_AND_HOLD_ON_ELEMENT);
    }

    /**
     * Tap and hold on a mobile element for a duration
     * @param to
     *      represent a mobile element
     * @param duration
     *      duration (in seconds) that the tap is hold on the element, if set to <= 0 then will use default duration
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void tapAndHold(TestObject to, Number duration, int timeout) throws StepFailedException {
        tapAndHold(to, duration, timeout, RunConfiguration.getDefaultFailureHandling());
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
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if (element == null) {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()), flowControl, null);
                return null;
            }
            String val = MobileCommonHelper.getAttributeValue(element, name);
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_HAS_ATTR, to.getObjectId(), name, val));
            return val;
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_GET_ELEMENT_X_ATTR_Y, to.getObjectId(), name)
        : StringConstants.KW_MSG_FAILED_TO_GET_ELEMENT_ATTR);
    }

    /**
     * Get a specific attribute of a mobile element
     * @param to
     *      represent a mobile element
     * @param name
     *      name of the attribute to get
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      value of the attribute
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ATTRIBUTE)
    public static String getAttribute(TestObject to, String name, int timeout) throws StepFailedException {
        return getAttribute(to, name, timeout, RunConfiguration.getDefaultFailureHandling());
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
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if (element != null) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_PRESENTED, to.getObjectId()));
                return true;
            } else {
                logger.logWarning(MessageFormat.format(StringConstants.KW_MSG_OBJ_NOT_FOUND, to.getObjectId()));
                return false;
            }
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_WAIT_FOR_ELEMENT_PRESENT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_WAIT_FOR_ELEMENT_X_PRESENT);
    }

    /**
     * Wait for a mobile element to present
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      true if the element is presented; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementPresent(TestObject to, int timeout) throws StepFailedException {
        return waitForElementPresent(to, timeout, RunConfiguration.getDefaultFailureHandling());
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
    public static boolean verifyElementExist(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if (element != null) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_X_EXISTED, to.getObjectId()));
                return true;
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()), flowControl, null);
                return false;
            }
        }, flowControl, to != null ?  MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_X_EXIST, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_EXIST);
    }

    /**
     * Verify if a mobile element is presented
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      true if the element is presented; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementExist(TestObject to, int timeout) throws StepFailedException {
        return verifyElementExist(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if a mobile element is NOT presented
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      true if the element is NOT presented; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotExist(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if (element != null) {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_NOT_EXISTED, to.getObjectId()), flowControl, null);
                return false;
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_X_NOT_EXISTED, to.getObjectId()));
                return true;
            }
        }, flowControl, to != null ?  MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_X_NOT_EXIST, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_NOT_EXIST);
    }

    /**
     * Verify if a mobile element is NOT presented
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      true if the element is NOT presented; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotExist(TestObject to, int timeout) throws StepFailedException {
        return verifyElementNotExist(to, timeout, RunConfiguration.getDefaultFailureHandling());
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
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if (element == null) {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()), flowControl, null);
                return;
            }
            element.clear();
            logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_TEXT_IS_CLEARED, to.getObjectId()));
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CLEAR_TEXT_OF_ELEMENT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CLEAR_TEXT_OF_ELEMENT);
    }

    /**
     * Clear text of a mobile element
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static void clearText(TestObject to, int timeout) throws StepFailedException {
        clearText(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if current device is in landscape mode
     * @param flowControl
     * @return
     *      true if the device is in landscape mode ; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static boolean verifyIsLandscape(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver);
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
     * Verify if current device is in landscape mode
     * @return
     *      true if the device is in landscape mode ; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static boolean verifyIsLandscape() throws StepFailedException {
        return verifyIsLandscape(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if current device is in portrait mode
     * @param flowControl
     * @return
     *      true if the device is in portrait mode ; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static boolean verifyIsPortrait(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver);
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

    /**
     * Verify if current device is in portrait mode
     * @return
     *      true if the device is in portrait mode ; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static boolean verifyIsPortrait() throws StepFailedException {
        return verifyIsPortrait(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Switch the current device's mode to landscape mode
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static boolean switchToLandscape(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver);
                driver.rotate(ScreenOrientation.LANDSCAPE);
                logger.logPassed(StringConstants.KW_LOG_PASSED_SWITCH_LANDSCAPE);
            } finally {
                driver.context(context);
            }
        }, flowControl, StringConstants.KW_MSG_UNABLE_SWITCH_LANDSCAPE);
    }

    /**
     * Switch the current device's mode to landscape mode
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static boolean switchToLandscape() throws StepFailedException {
        return switchToLandscape(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Switch the current device's mode to portrait mode
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static boolean switchToPortrait(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver);
                driver.rotate(ScreenOrientation.PORTRAIT);
                logger.logPassed(StringConstants.KW_LOG_PASSED_SWITCH_PORTRAIT);
            } finally {
                driver.context(context);
            }
        }, flowControl, StringConstants.KW_MSG_UNABLE_SWITCH_PORTRAIT);
    }

    /**
     * Switch the current device's mode to portrait mode
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static boolean switchToPortrait() throws StepFailedException {
        return switchToPortrait(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Get current screen orientation of the device
     * @param flowControl
     * @return current screen orientation (portrait, landscape)
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getCurrentOrientation(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver);
                String orientation = driver.getOrientation().value();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_ORIENTATION_X, orientation));
                return orientation;
            } finally {
                driver.context(context);
            }
            return null;
        }, flowControl, StringConstants.KW_MSG_UNABLE_GET_ORIENTATION);
    }

    /**
     * Get current screen orientation of the device
     * @return current screen orientation (portrait, landscape)
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static String getCurrentOrientation() throws StepFailedException {
        return getCurrentOrientation(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Switch the current device driver to web view context
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static void switchToWebView(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver driver = getAnyAppiumDriver();
            boolean result = internalSwitchToWebViewContext(driver);
            if (result) {
                logger.logPassed(StringConstants.KW_LOG_PASSED_SWITCH_WEB_VIEW);
                RunConfiguration.storeDriver(driver);
            } else {
                KeywordMain.stepFailed(StringConstants.KW_LOG_FAILED_SWITCH_WEB_VIEW, flowControl, null);
            }
        }, flowControl, StringConstants.KW_MSG_UNABLE_SWITCH_WEB_VIEW);
    }

    /**
     * Switch the current device driver to web view context
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static void switchToWebView() throws StepFailedException {
        switchToWebView(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Switch the current device driver to native context
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static void switchToNative(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver driver = getAnyAppiumDriver();
            boolean result = internalSwitchToNativeContext(driver);
            if (result) {
                logger.logPassed(StringConstants.KW_LOG_PASSED_SWITCH_NATIVE);
            } else {
                KeywordMain.stepFailed(StringConstants.KW_LOG_FAILED_SWITCH_NATIVE, flowControl, null);
            }
        }, flowControl, StringConstants.KW_MSG_UNABLE_SWITCH_NATIVE);
    }

    /**
     * Switch the current device driver to native context
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static void switchToNative() throws StepFailedException {
        switchToNative(RunConfiguration.getDefaultFailureHandling());
    }

    @CompileStatic
    private static boolean internalSwitchToNativeContext(AppiumDriver driver) {
        return internalSwitchToContext(driver, "NATIVE");
    }

    @CompileStatic
    private static boolean internalSwitchToContext(AppiumDriver driver, String contextName) {
        try {
            for (String context : driver.getContextHandles()) {
                if (context.contains(contextName)) {
                    driver.context(context);
                    return true;
                }
            }
        } catch (WebDriverException e) {
            // Appium will raise WebDriverException error when driver.getContextHandles() is called but ios-webkit-debug-proxy is not started.
            // Catch it here and ignore
        }
        return false;
    }

    @CompileStatic
    private static boolean internalSwitchToWebViewContext(AppiumDriver driver) {
        return internalSwitchToContext(driver, "WEBVIEW");
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
     * Scroll to an element which contains the given text.
     * @param text : text of an element to scroll to
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static void scrollToText(String text, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_TEXT);
            if (text == null) {
                throw new IllegalArgumentException(StringConstants.COMM_EXC_TEXT_IS_NULL);
            }
            AppiumDriver driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver);
                driver.scrollToExact(text);
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SCROLL_TO_TEXT_X, text));
            } finally {
                driver.context(context);
            }
        }, flowControl, MessageFormat.format(StringConstants.KW_MSG_UNABLE_SCROLL_TO_TEXT_X, text));
    }

    /**
     * Scroll to an element which contains the given text.
     * @param text : text of an element to scroll to
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_TEXT)
    public static void scrollToText(String text) throws StepFailedException {
        scrollToText(text, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if a mobile element is visible
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      true if the element is visible; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementVisible(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if (element != null) {
                if (element.isDisplayed()) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_X_VISIBLE, to.getObjectId()));
                    return true;
                } else {
                    KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_VISIBLE, to.getObjectId()), flowControl, null);
                    return false;
                }
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()), flowControl, null);
                return false;
            }
        }, flowControl, to != null ?  MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_X_VISIBLE, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_VISIBLE);
    }

    /**
     * Verify if a mobile element is visible
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      true if the element is visible; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementVisible(TestObject to, int timeout) throws StepFailedException {
        return verifyElementVisible(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if a mobile element is NOT visible
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      true if the element is NOT exists or is NOT visible; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotVisible(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            timeout = KeywordHelper.checkTimeout(timeout)
            WebElement element = findElement(to, timeout * 1000);
            if (element != null) {
                if (element.isDisplayed()) {
                    KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_NOT_VISIBLE, to.getObjectId()), flowControl, null);
                    return false;
                } else {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_X_NOT_VISIBLE, to.getObjectId()));
                    return true;
                }
            } else {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()));
                return true;
            }
        }, flowControl, to != null ?  MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_X_NOT_VISIBLE, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_NOT_VISIBLE);
    }

    /**
     * Verify if a mobile element is NOT visible
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      true if the element is NOT exists or is NOT visible; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotVisible(TestObject to, int timeout) throws StepFailedException {
        return verifyElementNotVisible(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Get device's physical width
     * @param flowControl
     * @return device's physical width
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static int getDeviceWidth(FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeywordAndReturnInt({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver)
                int viewportWidth = driver.manage().window().getSize().getWidth();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_DEVICE_WIDTH_X, viewportWidth.toString()));
                return viewportWidth;
            } finally {
                driver.context(context);
            }
        }
        , flowControl, StringConstants.KW_MSG_UNABLE_GET_DEVICE_WIDTH)
    }

    /**
     * Get device's physical width
     * @return device's physical width
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static int getDeviceWidth() throws StepFailedException {
        return getDeviceWidth(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Get device's physical height
     * @param flowControl
     * @return device's physical height
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static int getDeviceHeight(FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeywordAndReturnInt({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver)
                int viewportHeight = driver.manage().window().getSize().getHeight();
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_GET_DEVICE_HEIGHT_X, viewportHeight.toString()));
                return viewportHeight;
            } finally {
                driver.context(context);
            }
        }
        , flowControl, StringConstants.KW_MSG_UNABLE_GET_DEVICE_HEIGHT)
    }

    /**
     * Get device's physical height
     * @return device's physical height
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static int getDeviceHeight() throws StepFailedException {
        return getDeviceHeight(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if the element has an attribute with the specific name
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to verify
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element has the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementHasAttribute(TestObject to, String attributeName, int timeout, FailureHandling flowControl) {
        KeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            KeywordHelper.checkTestObjectParameter(to);
            logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
            if (attributeName == null) {
                throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
            }
            timeout = KeywordHelper.checkTimeout(timeout);
            WebElement foundElement = findElement(to, timeout);
            if (foundElement == null) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()));
                return false;
            }
            String attribute = MobileCommonHelper.getAttributeValue(foundElement, attributeName);
            if (attribute != null) {
                KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
                return true;
            }  else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName), flowControl, null);
                return false;
            }
        }
        , flowControl, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName)
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_HAS_ATTRIBUTE)
    }

    /**
     * Verify if the element has an attribute with the specific name
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to verify
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @return true if element has the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementHasAttribute(TestObject to, String attributeName, int timeout) {
        return verifyElementHasAttribute(to, attributeName, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if the element doesn't have an attribute with the specific name
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to verify
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element has the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotHasAttribute(TestObject to, String attributeName, int timeout, FailureHandling flowControl) {
        KeywordMain.runKeyword({
            boolean isSwitchIntoFrame = false;
            KeywordHelper.checkTestObjectParameter(to);
            logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
            if (attributeName == null) {
                throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
            }
            timeout = KeywordHelper.checkTimeout(timeout);
            WebElement foundElement = findElement(to, timeout);
            if (foundElement == null) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()));
                return false;
            }
            String attribute = MobileCommonHelper.getAttributeValue(foundElement, attributeName);
            if (attribute == null) {
                KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_NOT_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
                return true;
            }  else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_NOT_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName), flowControl, null);
                return false;
            }
        }
        , flowControl, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_NOT_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName)
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_NOT_HAS_ATTRIBUTE)
    }

    /**
     * Verify if the element doesn't have an attribute with the specific name
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to verify
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @return true if element has the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotHasAttribute(TestObject to, String attributeName, int timeout) {
        return verifyElementNotHasAttribute(to, attributeName, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if the element has an attribute with the specific name and value
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to verify
     * @param attributeValue
     *       the value of the attribute to verify
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element has the attribute with the specific name and value; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementAttributeValue(TestObject to, String attributeName, String attributeValue, int timeout, FailureHandling flowControl) {
        KeywordMain.runKeyword({
            KeywordHelper.checkTestObjectParameter(to);
            logger.logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
            if (attributeName == null) {
                throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
            }
            timeout = KeywordHelper.checkTimeout(timeout);
            WebElement foundElement = findElement(to, timeout);
            if (foundElement == null) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()));
                return false;
            }
            String actualAttributeValue = MobileCommonHelper.getAttributeValue(foundElement, attributeName);
            if (actualAttributeValue != null) {
                if (actualAttributeValue.equals(attributeValue)) {
                    KeywordLogger.getInstance().logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_ATTRIBUTE_Y_VALUE_Z,
                            to.getObjectId(), attributeName, attributeValue));
                    return true;
                } else {
                    KeywordMain.stepFailed(
                            MessageFormat.format(
                            StringConstants.KW_LOG_FAILED_OBJ_X_ATTRIBUTE_Y_ACTUAL_VALUE_Z_EXPECTED_VALUE_W,
                            to.getObjectId(), attributeName, actualAttributeValue, attributeValue), flowControl, null);
                    return false;
                }
            }  else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName), flowControl, null);
                return false;
            }
        }
        , flowControl, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_X_ATTRIBUTE_Y_VALUE_Z, to.getObjectId(), attributeName, attributeValue)
        : StringConstants.KW_MSG_CANNOT_VERIFY_OBJ_ATTRIBUTE_VALUE)
    }

    /**
     * Verify if the element has an attribute with the specific name and value
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to verify
     * @param attributeValue
     *       the value of the attribute to verify
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @return true if element has the attribute with the specific name and value; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementAttributeValue(TestObject to, String attributeName, String attributeValue, int timeout) {
        return verifyElementAttributeValue(to, attributeName, attributeValue, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Wait until the given web element has an attribute with the specific name
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to wait for
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element has the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementHasAttribute(TestObject to, String attributeName, int timeout, FailureHandling flowControl) {
        KeywordMain.runKeyword({
            try {
                KeywordHelper.checkTestObjectParameter(to);
                KeywordLogger.getInstance().logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
                if (attributeName == null) {
                    throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
                }
                timeout = KeywordHelper.checkTimeout(timeout);
                WebElement foundElement = findElement(to, timeout);
                if (foundElement == null) {
                    logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()));
                    return false;
                }
                Boolean hasAttribute = new FluentWait<WebElement>(foundElement)
                        .pollingEvery(500, TimeUnit.MILLISECONDS).withTimeout(timeout, TimeUnit.SECONDS)
                        .until(new Function<WebElement, Boolean>() {
                            @Override
                            public Boolean apply(WebElement element) {
                                return MobileCommonHelper.getAttributeValue(foundElement, attributeName) != null;
                            }
                        });
                if (hasAttribute) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
                    return true;
                }
            } catch (TimeoutException e) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
            }
            return false;
        }
        , flowControl, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName)
        : StringConstants.KW_MSG_CANNOT_WAIT_OBJ_HAS_ATTRIBUTE)
    }

    /**
     * Wait until the given web element has an attribute with the specific name
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to wait for
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @return true if element has the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementHasAttribute(TestObject to, String attributeName, int timeout) {
        return waitForElementHasAttribute(to, attributeName, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Wait until the given web element doesn't have an attribute with the specific name
     * @param to
     *      represent a web element
     * @param attributeName
     *      the name of the attribute to wait for
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element doesn't have the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementNotHasAttribute(TestObject to, String attributeName, int timeout, FailureHandling flowControl) {
        KeywordMain.runKeyword({
            try {
                KeywordHelper.checkTestObjectParameter(to);
                KeywordLogger.getInstance().logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
                if (attributeName == null) {
                    throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
                }
                timeout = KeywordHelper.checkTimeout(timeout);
                WebElement foundElement = findElement(to, timeout);
                if (foundElement == null) {
                    logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()));
                    return false;
                }
                Boolean notHasAttribute = new FluentWait<WebElement>(foundElement)
                        .pollingEvery(500, TimeUnit.MILLISECONDS).withTimeout(timeout, TimeUnit.SECONDS)
                        .until(new Function<WebElement, Boolean>() {
                            @Override
                            public Boolean apply(WebElement element) {
                                return MobileCommonHelper.getAttributeValue(foundElement, attributeName) == null;
                            }
                        });
                if (notHasAttribute) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_FAILED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
                    return true;
                }
            } catch (TimeoutException e) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName));
            }
            return false;
        }
        , flowControl, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_NOT_HAS_ATTRIBUTE_Y, to.getObjectId(), attributeName)
        : StringConstants.KW_MSG_CANNOT_WAIT_OBJ_NOT_HAS_ATTRIBUTE)
    }

    /**
     * Wait until the given web element doesn't have an attribute with the specific name
     * @param to
     *      represent a web element
     * @param attributeName
     *      the name of the attribute to wait for
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @return true if element doesn't have the attribute with the specific name; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementNotHasAttribute(TestObject to, String attributeName, int timeout) {
        return waitForElementNotHasAttribute(to, attributeName, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Wait until the given web element has an attribute with the specific name and value
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to wait for
     * @param attributeValue
     *      the value of the attribute to wait for
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return true if element has the attribute with the specific name and value; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementAttributeValue(TestObject to, String attributeName, String attributeValue, int timeout, FailureHandling flowControl) {
        KeywordMain.runKeyword({
            try {
                KeywordHelper.checkTestObjectParameter(to);
                KeywordLogger.getInstance().logInfo(StringConstants.COMM_LOG_INFO_CHECKING_ATTRIBUTE_NAME);
                if (attributeName == null) {
                    throw new IllegalArgumentException(StringConstants.COMM_EXC_ATTRIBUTE_NAME_IS_NULL);
                }
                timeout = KeywordHelper.checkTimeout(timeout);
                WebElement foundElement = findElement(to, timeout);
                if (foundElement == null) {
                    logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_EXISTED, to.getObjectId()));
                    return false;
                }
                Boolean hasAttributeValue = new FluentWait<WebElement>(foundElement)
                        .pollingEvery(500, TimeUnit.MILLISECONDS).withTimeout(timeout, TimeUnit.SECONDS)
                        .until(new Function<WebElement, Boolean>() {
                            @Override
                            public Boolean apply(WebElement element) {
                                return MobileCommonHelper.getAttributeValue(foundElement, attributeName) == attributeValue;
                            }
                        });
                if (hasAttributeValue) {
                    logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_OBJ_X_ATTRIBUTE_Y_VALUE_Z, to.getObjectId(), attributeName, attributeValue));
                    return true;
                }
            } catch (TimeoutException e) {
                logger.logWarning(MessageFormat.format(StringConstants.KW_LOG_FAILED_WAIT_FOR_OBJ_X_HAS_ATTRIBUTE_Y_VALUE_Z, to.getObjectId(), attributeName, attributeValue));
            }
            return false;
        }
        , flowControl, (to != null) ? MessageFormat.format(StringConstants.KW_MSG_CANNOT_WAIT_OBJ_X_ATTRIBUTE_Y_VALUE_Z, to.getObjectId(), attributeName, attributeValue)
        : StringConstants.KW_MSG_CANNOT_WAIT_OBJ_ATTRIBUTE_VALUE)
    }

    /**
     * Wait until the given web element has an attribute with the specific name and value
     * @param to
     *      represent a mobile element
     * @param attributeName
     *      the name of the attribute to wait for
     * @param attributeValue
     *      the value of the attribute to wait for
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @return true if element has the attribute with the specific name and value; otherwise, false
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean waitForElementAttributeValue(TestObject to, String attributeName, String attributeValue, int timeout) {
        return waitForElementAttributeValue(to, attributeName, attributeValue, timeout, RunConfiguration.getDefaultFailureHandling());
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

    /**
     * Drag and drop an element into another element
     * @param fromObject
     *      represent the drag-able mobile element
     * @param toObject
     *      represent the drop-able mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void dragAndDrop(TestObject fromObject, TestObject toObject, int timeout, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileElementCommonHelper.dragAndDrop(fromObject, toObject, timeout);
        }, flowControl, (fromObject != null && toObject != null) ?
        MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_DRAG_AND_DROP_ELEMENT_X_TO_ELEMENT_Y, fromObject.getObjectId(), toObject.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_DRAG_AND_DROP_ELEMENT);
    }

    /**
     * Drag and drop an element into another element
     * @param fromObject
     *      represent the drag-able mobile element
     * @param toObject
     *      represent the drop-able mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void dragAndDrop(TestObject fromObject, TestObject toObject, int timeout) throws StepFailedException {
        dragAndDrop(fromObject, toObject, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Set the value for Slider control (android.widget.SeekBar for Android, UIASlider for iOS) at specific percentage
     * @param to
     *      represent a mobile element (android.widget.SeekBar for Android, UIASlider for iOS)
     * @param percent
     *      percentage value to set to the slider ( 0 <= percent <= 100 )
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void setSliderValue(TestObject to, Number percent, int timeOut, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileElementCommonHelper.moveSlider(to, percent, timeOut);
        }, flowControl, (to != null && percent != null) ? 
            MessageFormat.format(StringConstants.KW_MSG_FAILED_SET_SLIDER_X_TO_Y, to.getObjectId(), percent) : StringConstants.KW_MSG_FAILED_SET_SLIDER)
    }
    
    /**
     * Set the value for Slider control (android.widget.SeekBar for Android, UIASlider for iOS) at specific percentage
     * @param to
     *      represent a mobile element (android.widget.SeekBar for Android, UIASlider for iOS)
     * @param percent
     *      percentage value to set to the slider ( 0 <= percent <= 100 )
     * @param timeOut
     *      system will wait at most timeout (seconds) to return result
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void setSliderValue(TestObject to, Number percent, int timeOut) throws StepFailedException {
        setSliderValue(to, percent, timeOut, RunConfiguration.getDefaultFailureHandling());
    }
    
    /** Hide the keyboard if it is showing
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static void hideKeyboard(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try {
                internalSwitchToNativeContext(driver);
                try {
                    driver.hideKeyboard();
                } catch (WebDriverException e) {
                    if (!(e.getMessage().startsWith(StringConstants.APPIUM_DRIVER_ERROR_JS_FAILED) && driver instanceof IOSDriver<?>)) {
                        throw e;
                    }
                    // default hide keyboard strategy (tap outside) failed on iOS, use "Done" button
                    IOSDriver<?> iosDriver = (IOSDriver<?>) driver;
                    iosDriver.hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done");
                }
                logger.logPassed(StringConstants.KW_LOG_PASSED_HIDE_KEYBOARD);
            } finally {
                driver.context(context)
            }
        }, flowControl, StringConstants.KW_MSG_CANNOT_HIDE_KEYBOARD)
    }

    /**
     * Hide the keyboard if it is showing
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static void hideKeyboard() throws StepFailedException {
        hideKeyboard(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Check a check-box mobile element (android.widget.CheckBox for Android, UIASwitch for iOS)
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void checkElement(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileElementCommonHelper.checkElement(to, timeout);
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CHECK_ELEMENT_X, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CHECK_ELEMENT);
    }

    /**
     * Check a check-box mobile element (android.widget.CheckBox for Android, UIASwitch for iOS)
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void checkElement(TestObject to, int timeout) throws StepFailedException {
        checkElement(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Un-check a check-box mobile element (android.widget.CheckBox for Android, UIASwitch for iOS)
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void uncheckElement(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileElementCommonHelper.uncheckElement(to, timeout);
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_UNCHECK_ELEMENT_X, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_UNCHECK_ELEMENT);
    }

    /**
     * Un-check a check-box mobile element (android.widget.CheckBox for Android, UIASwitch for iOS)
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static void uncheckElement(TestObject to, int timeout) throws StepFailedException {
        uncheckElement(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if a mobile element is checked
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      true if the element is checked; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementChecked(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            if (MobileElementCommonHelper.isElementChecked(to, timeout)) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_X_CHECKED, to.getObjectId()));
                return true;
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_CHECKED, to.getObjectId()), flowControl, null);
                return false;
            }
        }, flowControl, to != null ?  MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_X_CHECKED, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_CHECKED);
    }

    /**
     * Verify if a mobile element is checked
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      true if the element is checked; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementChecked(TestObject to, int timeout) throws StepFailedException {
        return verifyElementChecked(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Verify if a mobile element is not checked
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return
     *      true if the element is not checked; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotChecked(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeyword({
            if (!MobileElementCommonHelper.isElementChecked(to, timeout)) {
                logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_ELEMENT_X_UNCHECKED, to.getObjectId()));
                return true;
            } else {
                KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_LOG_FAILED_ELEMENT_X_UNCHECKED, to.getObjectId()), flowControl, null);
                return false;
            }
        }, flowControl, to != null ?  MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_X_UNCHECKED, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_CHECK_FOR_ELEMENT_UNCHECKED);
    }

    /**
     * Verify if a mobile element is not checked
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      true if the element is not checked; otherwise, false
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static boolean verifyElementNotChecked(TestObject to, int timeout) throws StepFailedException {
        return verifyElementNotChecked(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Unlock device screen
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static void unlockScreen(FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            AppiumDriver<?> driver = getAnyAppiumDriver();
            String context = driver.getContext();
            try{
                internalSwitchToNativeContext(driver);
                MobileDeviceCommonHelper.unlockScreen(driver);

            } finally {
                driver.context(context)
            }
            logger.logPassed(StringConstants.KW_MSG_PASSED_TO_UNLOCK_SCREEN);
        }, flowControl, StringConstants.KW_MSG_FAILED_TO_UNLOCK_SCREEN);
    }
    
    /**
     * Unlock device screen
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static void unlockScreen() throws StepFailedException {
        unlockScreen(RunConfiguration.getDefaultFailureHandling());
    }

    /**
     *  Tap at a specific position on the screen of the mobile device
     * @param x
     *      x position
     * @param y
     *      y position
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void tapAtPosition(Number x, Number y, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileElementCommonHelper.tapAtPosition(x, y);
        }, flowControl, (x != null && y != null) ? 
            MessageFormat.format(StringConstants.KW_LOG_FAILED_TAPPED_AT_X_Y, x, y)
            : StringConstants.KW_LOG_FAILED_TAPPED_AT_POSITION );
    }
    
    /**
     *  Tap at a specific position on the screen of the mobile device
     * @param x
     *      x position
     * @param y
     *      y position
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void tapAtPosition(Number x, Number y) throws StepFailedException {
        tapAtPosition(x, y, RunConfiguration.getDefaultFailureHandling());
    }
    
    /**
     *  Tap and hold at a specific position on the screen of the mobile device
     * @param x
     *      x position
     * @param y
     *      y position
     * @param duration
     *      duration (in seconds) that the tap is hold on the element, if set to <= 0 then will use default duration
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void tapAndHoldAtPosition(Number x, Number y, Number duration, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileElementCommonHelper.tapAndHold(x, y, duration);
        }, flowControl, (x != null && y != null && duration != null) ?
            MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_TAP_AND_HOLD_AT_X_Y_WITH_DURATION_Z, x, y, 
                MobileElementCommonHelper.getStringForDuration(duration)) : StringConstants.KW_MSG_FAILED_TO_TAP_AND_HOLD_AT_POSITION );
    }
    
    /**
     *  Tap and hold at a specific position on the screen of the mobile device
     * @param x
     *      x position
     * @param y
     *      y position
     * @param duration
     *      duration (in seconds) that the tap is hold on the element, if set to <= 0 then will use default duration
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void tapAndHoldAtPosition(Number x, Number y, Number duration) throws StepFailedException {
        tapAndHoldAtPosition(x, y, duration, RunConfiguration.getDefaultFailureHandling());
    }
    
    /**
     *  Pinch to zoom in at a specific position on the screen of the mobile device
     * @param x
     *      x position
     * @param y
     *      y position
     * @param offset
     *      the offset length to pinch
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void pinchToZoomInAtPosition(Number x, Number y, Number offset, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileGestureCommonHelper.pinchToZoomIn(x, y, offset);
        }, flowControl, (x != null && y != null && offset != null) ?
            MessageFormat.format(StringConstants.KW_LOG_FAILED_ZOOM_AT_X_Y_WITH_OFFSET_Z, x, y, offset)
            : StringConstants.KW_LOG_FAILED_ZOOM_AT_POSITION );
    }
    
    /**
     *  Pinch to zoom in at a specific position on the screen of the mobile device
     * @param x
     *      x position
     * @param y
     *      y position
     * @param offset
     *      the offset length to pinch
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void pinchToZoomInAtPosition(Number x, Number y, Number offset) throws StepFailedException {
        pinchToZoomInAtPosition(x, y, offset, RunConfiguration.getDefaultFailureHandling());
    }
    
    /**
     * Get the width of mobile element
     * @param to 
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return 
     *      width of the element
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static int getElementWidth(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeywordAndReturnInt({
            return MobileElementCommonHelper.getElementWidth(to, timeout);
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_GET_WIDTH_OF_ELEMENT_X, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_GET_WIDTH_OF_ELEMENT);
    }

    /**
     * Get the width of mobile element
     * @param to 
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return 
     *      width of the element
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static int getElementWidth(TestObject to, int timeout) throws StepFailedException {
        return getElementWidth(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }
    
    /**
     *  Pinch to zoom out at a specific position on the screen of the mobile device
     * @param x
     *      x position
     * @param y
     *      y position
     * @param offset
     *      the offset length to pinch
     * @param flowControl
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void pinchToZoomOutAtPosition(Number x, Number y, Number offset, FailureHandling flowControl) throws StepFailedException {
        KeywordMain.runKeyword({
            MobileGestureCommonHelper.pinchToZoomOut(x, y, offset);
        }, flowControl, (x != null && y != null && offset != null) ?
            MessageFormat.format(StringConstants.KW_LOG_FAILED_PINCH_AT_X_Y_WITH_OFFSET_Z, x, y, offset)
            : StringConstants.KW_LOG_FAILED_PINCH_AT_POSITION );
    }
    
    /**
     *  Pinch to zoom out at a specific position on the screen of the mobile device
     * @param x
     *      x position
     * @param y
     *      y position
     * @param offset
     *      the offset length to pinch
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
    public static void pinchToZoomOutAtPosition(Number x, Number y, Number offset) throws StepFailedException {
        pinchToZoomOutAtPosition(x, y, offset, RunConfiguration.getDefaultFailureHandling());
    }
    
    /**
     * Get the top position of mobile element
     * @param to mobile element object
     * @param timeout
     * @param flowControl
     * @return element top position
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static int getElementTopPosition(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeywordAndReturnInt({
            return MobileElementCommonHelper.getElementTopPosition(to, timeout, flowControl);
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_GET_TOP_POSITION_OF_ELEMENT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_GET_TOP_POSITION);
    }
    
    /**
     * Get the top position of mobile element
     * @param to mobile element object
     * @param timeout
     * @return element top position
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static int getElementTopPosition(TestObject to, int timeout) throws StepFailedException {
        return getElementTopPosition(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }
    
    /**
     * Get the height of mobile element
     * @param to 
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return 
     *      height of the element
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static int getElementHeight(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeywordAndReturnInt({
            return MobileElementCommonHelper.getElementHeight(to, timeout);
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_GET_HEIGHT_OF_ELEMENT_X, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_GET_HEIGHT_OF_ELEMENT);
    }

    /**
     * Get the height of mobile element
     * @param to 
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return 
     *      height of the element
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_ELEMENT)
    public static int getElementHeight(TestObject to, int timeout) throws StepFailedException {
        return getElementHeight(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }

    /**
     * Get the left position of mobile element
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @param flowControl
     * @return 
     *      the left position of the mobile element
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static int getElementLeftPosition(TestObject to, int timeout, FailureHandling flowControl) throws StepFailedException {
        return KeywordMain.runKeywordAndReturnInt({
            return MobileElementCommonHelper.getElementLeftPosition(to, timeout, flowControl);
        }, flowControl, to != null ? MessageFormat.format(StringConstants.KW_MSG_FAILED_TO_GET_LEFT_POSITION_OF_ELEMENT, to.getObjectId())
        : StringConstants.KW_MSG_FAILED_TO_GET_LEFT_POSITION);
    }

    /**
     * Get the left position of mobile element
     * @param to
     *      represent a mobile element
     * @param timeout
     *      system will wait at most timeout (seconds) to return result
     * @return
     *      the left position of the mobile element
     * @throws StepFailedException
     */
    @CompileStatic
    @Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
    public static int getElementLeftPosition(TestObject to, int timeout) throws StepFailedException {
        return getElementLeftPosition(to, timeout, RunConfiguration.getDefaultFailureHandling());
    }
}
