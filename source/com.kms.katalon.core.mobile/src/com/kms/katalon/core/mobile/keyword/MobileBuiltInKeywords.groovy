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
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.touch.TouchActions

import com.kms.katalon.core.annotation.Keyword
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
	private static AppiumDriver driver;
	//Device name should be selected by user from a UI Form
	//private static String deviceName = "LGE Nexus 4 5.1.1";

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_APPLICATION)
	public static void startApplication(String appFile, boolean uninstallAfterCloseApp, FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
			logger.logInfo(MessageFormat.format(StringConstants.KW_LOG_INFO_STARTING_APP_AT, appFile));
			driver = MobileCommonHelper.initializeMobileDriver(appFile.toString(), uninstallAfterCloseApp);
			//MobileCommonHelper.loadConfigs();
			if (driver == null) {
				KeywordMain.stepFailed(MessageFormat.format(StringConstants.KW_MSG_UNABLE_TO_START_APP_AT, appFile), flowControl, null);
			}
			logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_START_APP_AT, appFile));
		}, flowControl, MessageFormat.format(StringConstants.KW_MSG_UNABLE_TO_START_APP_AT, appFile))
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_APPLICATION)
	public static void closeApplication(FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
			driver.quit();
			MobileDriverFactory.getInstance().quitServer();
			logger.logPassed(StringConstants.KW_LOG_PASSED_CLOSE_APP);
		}, flowControl, StringConstants.KW_MSG_UNABLE_TO_CLOSE_APPLICATION)
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void pressBack(FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
			if (driver instanceof AndroidDriver) {
				((AndroidDriver)driver).pressKeyCode(AndroidKeyCode.BACK);
			} else {
				KeywordMain.stepFailed(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE, flowControl, null);
			}
			logger.logPassed(StringConstants.KW_LOG_PASSED_PRESS_BACK_BTN);
		}, flowControl, StringConstants.KW_MSG_CANNOT_PRESS_BACK_BTN)
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_SCREEN)
	public static void swipe(int startX, int startY, int endX, int endY, FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
			MobileCommonHelper.swipe(driver, startX, startY, endX, endY);
			logger.logPassed(MessageFormat.format(StringConstants.KW_LOG_PASSED_SWIPED_FROM_STARTXY_TO_ENDXY, startX, startY, endX, endY));
		}, flowControl, StringConstants.KW_MSG_CANNOT_SWIPE_ON_DEVICE)
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void takeScreenshot(String fileName, FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
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
		}, flowControl, StringConstants.KW_MSG_UNABLE_TO_TAKE_SCREENSHOT)
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NOTIFICATION)
	public static void openNotifications(FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
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
		}, flowControl, StringConstants.KW_MSG_CANNOT_OPEN_NOTIFICATIONS)
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void pressHome(FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
			if (driver instanceof AndroidDriver) {
				((AndroidDriver)driver).pressKeyCode(AndroidKeyCode.HOME);
			} else {
				KeywordMain.stepFailed(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE, flowControl, null);
			}
			logger.logPassed(StringConstants.KW_LOG_PASSED_HOME_BTN_PRESSED);
		}, flowControl, StringConstants.KW_MSG_CANNOT_PRESS_HOME_BTN);
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
	public static String getDeviceManufacturer(FailureHandling flowControl) throws StepFailedException {
		return KeywordMain.runKeyword({
			String deviceId = MobileDriverFactory.getInstance().getDeviceId(MobileDriverFactory.getDeviceName());
			OsType deviceOs = MobileDriverFactory.getInstance().getDeviceOs(deviceId);
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

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_DEVICE)
	public static String getDeviceOS(FailureHandling flowControl) throws StepFailedException {
		return KeywordMain.runKeyword({
			String deviceId = MobileDriverFactory.getInstance().getDeviceId(MobileDriverFactory.getDeviceName());
			OsType deviceOs = MobileDriverFactory.getInstance().getDeviceOs(deviceId);
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

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_NOTIFICATION)
	public static void closeNotifications(FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
			int height = driver.manage().window().getSize().height;
			MobileCommonHelper.swipe(driver, 50, height - 1, 50, 1);
			logger.logPassed(StringConstants.KW_LOG_PASSED_NOTIFICATION_CLOSED);
		}, flowControl, StringConstants.KW_MSG_CANNOT_CLOSE_NOTIFICATIONS);
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void toggleAirplaneMode(String mode, FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
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
				if(MobileCommonHelper.airPlaneButtonCoords.get(MobileCommonHelper.deviceModels.get(deviceModel)) == null || MobileCommonHelper.airPlaneButtonCoords.get(MobileCommonHelper.deviceModels.get(deviceModel)).equals("")){
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
		}, flowControl, StringConstants.KW_MSG_CANNOT_TOGGLE_AIRPLANE_MODE);
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_APPLICATION)
	public static void runIOSAppInBackgroundAndWait(int seconds, FailureHandling flowControl) throws StepFailedException {
		KeywordMain.runKeyword({
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

	@CompileStatic
	private static WebElement findElement(TestObject to, int timeOut) throws Exception {
		Date startTime = new Date();
		Date endTime;
		long span = 0;
		WebElement webElement = null;
		Point elementLocation = null;

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
