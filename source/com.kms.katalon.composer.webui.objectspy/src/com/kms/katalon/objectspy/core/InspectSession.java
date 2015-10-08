package com.kms.katalon.objectspy.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map.Entry;

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

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.entity.IDriverConnector;
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

	protected Logger logger;
	protected String projectDir;
	protected boolean isRunFlag;
	protected WebDriver driver;
	protected Object options;
	protected WebUIDriverType webUiDriverType;
	protected String serverUrl;

	public InspectSession(String serverUrl, WebUIDriverType webUiDriverType, ProjectEntity currentProject, Logger logger)
			throws Exception {
		this.serverUrl = serverUrl;
		this.webUiDriverType = webUiDriverType;
		if (webUiDriverType == WebUIDriverType.CHROME_DRIVER || webUiDriverType == WebUIDriverType.FIREFOX_DRIVER) {
			setUp(webUiDriverType, currentProject);
		} else if (webUiDriverType == WebUIDriverType.IE_DRIVER) {
			checkIEAddon();
		}
		this.logger = logger;
		isRunFlag = true;
	}

	protected void checkIEAddon() throws Exception {
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

	protected void setUp(WebUIDriverType webUIDriverType, ProjectEntity currentProject) throws Exception {
		projectDir = currentProject.getFolderLocation();

		IDriverConnector driverConnector = WebUIExecutionUtil.getBrowserDriverConnector(webUIDriverType, projectDir);
		for (Entry<String, Object> entry : driverConnector.getExecutionSettingPropertyMap().entrySet()) {
		    if (entry.getValue() instanceof String) {
	            System.setProperty(entry.getKey(), (String) entry.getValue());
		    }
		}

		options = createDriverOptions(webUIDriverType);
	}

	@Override
	public void run() {
		if (webUiDriverType == WebUIDriverType.CHROME_DRIVER || webUiDriverType == WebUIDriverType.FIREFOX_DRIVER) {
			runSeleniumWebDriver();
		} else if (webUiDriverType == WebUIDriverType.IE_DRIVER) {
			try {
				runIE();
			} catch (IOException e) {
				logger.error(e);
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

			driver = DriverFactory.openWebDriver(webUiDriverType, projectDir,
					options);

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
			logger.error(e);
		} finally {
			dispose();
		}
	}

	protected Object createDriverOptions(WebUIDriverType driverType) throws Exception {
		if (driverType == WebUIDriverType.CHROME_DRIVER) {
			return createChromDriverOptions();
		}
		if (driverType == WebUIDriverType.FIREFOX_DRIVER) {
			return createFireFoxProfile();
		} else {
			throw new BrowserNotSupportedException(driverType);
		}
	}

	protected FirefoxProfile createFireFoxProfile() throws Exception {
		File file = getFirefoxAddonFile();
		if (file != null) {
			FirefoxProfile firefoxProfile = new FirefoxProfile();
			firefoxProfile.addExtension(file);
			firefoxProfile.setPreference("serverUrl", serverUrl);
			return firefoxProfile;
		}
		return null;
	}

	protected DesiredCapabilities createChromDriverOptions() throws Exception {
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
