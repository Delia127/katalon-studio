package com.kms.katalon.core.webui.driver;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.webui.constants.StringConstants;
import com.kms.katalon.core.webui.exception.BrowserNotOpenedException;
import com.kms.katalon.core.webui.setting.DriverPropertyStore;

public class DriverFactory {
	// Temp error constant message for
	// https://github.com/danascheider/canto-front-end/issues/27
	private static final String JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE = "[JavaScript Error: \"h is null\"";
	public static final String IE_DRIVER_PATH_PROPERTY = StringConstants.CONF_PROPERTY_IE_DRIVER_PATH;
	public static final String CHROME_DRIVER_PATH_PROPERTY = StringConstants.CONF_PROPERTY_CHROME_DRIVER_PATH;
	public static final String WAIT_FOR_IE_HANGING_PROPERTY = StringConstants.CONF_PROPERTY_WAIT_FOR_IE_HANGING;
	public static final String EXECUTED_BROWSER_PROPERTY = StringConstants.CONF_PROPERTY_EXECUTED_BROWSER;
	public static final String REMOTE_WEB_DRIVER_URL = StringConstants.CONF_PROPERTY_REMOTE_WEB_DRIVER_URL;
	private static WebDriver seleniumWebDriver;

	public static WebDriver openWebDriver() throws Exception {
		try {
			if (null != seleniumWebDriver && null != ((RemoteWebDriver) seleniumWebDriver).getSessionId()) {
				KeywordLogger.getInstance().logWarning(StringConstants.DRI_LOG_WARNING_BROWSER_ALREADY_OPENED);
				closeWebDriver();
			}
			WebUIDriverType driver = (WebUIDriverType) getExecutedBrowser();
			switch (driver) {
			case FIREFOX_DRIVER:
				seleniumWebDriver = new FirefoxDriver(DriverPropertyStore.getFirefoxProfile());
				setTimeout();
				break;
			case IE_DRIVER:
				System.setProperty("webdriver.ie.driver", getIEDriverPath());
				seleniumWebDriver = new InternetExplorerDriver();
				setTimeout();
				break;
			case SAFARI_DRIVER:
				seleniumWebDriver = new SafariDriver();
				break;
			case CHROME_DRIVER:
				System.setProperty("webdriver.chrome.driver", getChromeDriverPath());
				seleniumWebDriver = new ChromeDriver(DriverPropertyStore.getChromeOptions());
				setTimeout();
				break;
			case REMOTE_WEB_DRIVER:
				seleniumWebDriver = new RemoteWebDriver(new URL(getRemoteWebDriverServerUrl()),
						DriverPropertyStore.getRemoteWebDriverOptions());
			default:
				break;
			}
		} catch (Error e) {
			KeywordLogger.getInstance().logMessage(LogLevel.WARNING, e.getMessage());
			throw new StepFailedException(e);
		}
		return seleniumWebDriver;
	}

	public static WebDriver openWebDriver(DriverType driver, String projectDir, Object options) throws Exception {
		try {
			if (!(driver instanceof WebUIDriverType)) {
				return null;
			}
			closeWebDriver();
			WebUIDriverType webUIDriver = (WebUIDriverType) driver;
			switch (webUIDriver) {
			case FIREFOX_DRIVER:
				if (options instanceof FirefoxProfile) {
					seleniumWebDriver = new FirefoxDriver((FirefoxProfile) options);
				} else {
					seleniumWebDriver = new FirefoxDriver();
				}
				break;
			case IE_DRIVER:
				System.setProperty("webdriver.ie.driver", getIEDriverPath());
				seleniumWebDriver = new InternetExplorerDriver();
				break;
			case SAFARI_DRIVER:
				seleniumWebDriver = new SafariDriver();
				break;
			case CHROME_DRIVER:
				System.setProperty("webdriver.chrome.driver", getChromeDriverPath());
				if (options instanceof DesiredCapabilities) {
					ChromeDriver chromeDriver = new ChromeDriver((DesiredCapabilities) options);
					return chromeDriver;
				}
				break;
			default:
				break;
			}
		} catch (Error e) {
			KeywordLogger.getInstance().logMessage(LogLevel.WARNING, e.getMessage());
			throw new StepFailedException(e);
		}
		return seleniumWebDriver;
	}

	private static void setTimeout() {
		// seleniumWebDriver.manage().timeouts().pageLoadTimeout(RunConfiguration.getPageLoadTimeout(),
		// TimeUnit.SECONDS);
		seleniumWebDriver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
	}

	public static WebDriver getWebDriver() throws StepFailedException, WebDriverException {
		verifyWebDriver();
		return seleniumWebDriver;
	}

	private static void verifyWebDriver() throws StepFailedException, WebDriverException {
		verifyWebDriverIsOpen();
		try {
			if (null == ((RemoteWebDriver) seleniumWebDriver).getSessionId()) {
				switchToAvailableWindow();
			} else {
				checkIfWebDriverIsBlock();
			}
		} catch (WebDriverException e) {
			if (!(e instanceof NoSuchWindowException) && e.getMessage() != null
					&& !e.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
				throw e;
			}
		}
	}

	private static void verifyWebDriverIsOpen() throws WebDriverException {
		if (seleniumWebDriver == null) {
			throw new BrowserNotOpenedException();
		}
	}

	// Only check for IE
	private static void checkIfWebDriverIsBlock() throws StepFailedException {
		if (getExecutedBrowser() == WebUIDriverType.IE_DRIVER) {
			// return if there is an alert blocking
			// if (isAlertPresent()) {
			// return;
			// }

			Thread ieSafeThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						seleniumWebDriver.getWindowHandle();
					} catch (WebDriverException e) {
						// Ignore since we only check for hanging thread
					}
				}
			});
			ieSafeThread.start();
			float count = 0;
			while (ieSafeThread.isAlive()) {
				if (count > getWaitForIEHanging()) {
					ieSafeThread.interrupt();
					throw new StepFailedException(MessageFormat.format(
							StringConstants.DRI_MSG_UNABLE_REACH_WEB_DRI_TIMEOUT, RunConfiguration.getTimeOut()));
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// Thread is interrupted, do nothing
				}
				count += 0.1;
			}
		}
	}

	public static Alert getAlert() throws WebDriverException {
		verifyWebDriverIsOpen();
		Alert alert = null;
		if (getExecutedBrowser() == WebUIDriverType.IE_DRIVER) {
			Thread ieSafeThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						try {
							seleniumWebDriver.switchTo().alert();
						} catch (Exception e) {
							if (!(e instanceof NoSuchWindowException) && e.getMessage() != null
									&& !e.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
								throw e;
							}
							switchToAvailableWindow();
							seleniumWebDriver.switchTo().alert();
						}
					} catch (WebDriverException e) {
						// Ignore since we only check for hanging thread
					}
				}
			});
			ieSafeThread.start();
			float count = 0;
			while (ieSafeThread.isAlive()) {
				if (count > getWaitForIEHanging()) {
					ieSafeThread.interrupt();
					throw new StepFailedException(MessageFormat.format(
							StringConstants.DRI_MSG_UNABLE_REACH_WEB_DRI_TIMEOUT, RunConfiguration.getTimeOut()));
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// Thread is interrupted, do nothing
				}
				count += 0.1;
			}
		}
		try {
			try {
				alert = seleniumWebDriver.switchTo().alert();
			} catch (Exception e) {
				if (!(e instanceof NoSuchWindowException) && e.getMessage() != null
						&& !e.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
					throw e;
				}
				switchToAvailableWindow();
				alert = seleniumWebDriver.switchTo().alert();
			}
			return alert;
		} catch (NoAlertPresentException ex) {
			return null;
		}
	}

	public static boolean waitForAlert(int timeOut) {
		verifyWebDriverIsOpen();
		float count = 0;
		while (count < timeOut) {
			Alert alert = getAlert();
			if (alert != null) {
				return true;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Thread is interrupted, do nothing
			}
			count += 0.5;
		}
		return false;
	}

	public static void switchToAvailableWindow() {
		verifyWebDriverIsOpen();
		try {
			seleniumWebDriver.switchTo().window("");
		} catch (WebDriverException e) {
			if (!(e instanceof NoSuchWindowException) && e.getMessage() != null
					&& !e.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
				throw e;
			}
			// default window is closed, try to switch to available window
			Set<String> availableWindows = seleniumWebDriver.getWindowHandles();
			for (String windowId : availableWindows) {
				try {
					seleniumWebDriver.switchTo().window(windowId);
					return;
				} catch (WebDriverException exception) {
					if (!(exception instanceof NoSuchWindowException) && e.getMessage() != null
							&& !exception.getMessage().startsWith(JAVA_SCRIPT_ERROR_H_IS_NULL_MESSAGE)) {
						throw exception;
					}
					continue;
				}
			}
		}
	}

	private static String getIEDriverPath() {
		return System.getProperty(IE_DRIVER_PATH_PROPERTY);
	}

	private static String getChromeDriverPath() {
		return System.getProperty(CHROME_DRIVER_PATH_PROPERTY);
	}

	private static int getWaitForIEHanging() {
		return Integer.parseInt(System.getProperty(WAIT_FOR_IE_HANGING_PROPERTY));
	}

	public static DriverType getExecutedBrowser() {
		return WebUIDriverType.valueOf(System.getProperty(EXECUTED_BROWSER_PROPERTY));
	}

	public static String getRemoteWebDriverServerUrl() {
		return System.getProperty(REMOTE_WEB_DRIVER_URL);
	}

	public static void closeWebDriver() {
		if (null != seleniumWebDriver && null != ((RemoteWebDriver) seleniumWebDriver).getSessionId()) {
			try {
				seleniumWebDriver.quit();
			} catch (UnreachableBrowserException e) {
				KeywordLogger.getInstance().logWarning(StringConstants.DRI_LOG_WARNING_BROWSER_NOT_REACHABLE);
			}
		}
		seleniumWebDriver = null;
	}
}
