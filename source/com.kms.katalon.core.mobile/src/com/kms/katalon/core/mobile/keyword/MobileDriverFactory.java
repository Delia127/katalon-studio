package com.kms.katalon.core.mobile.keyword;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.util.MobileDriverPropertyUtil;

public class MobileDriverFactory {
	private Process appiumServer;
	private Map<String, String> androidDevices;
	private Map<String, String> iosDevices;

	public static final String EXECUTED_PLATFORM = StringConstants.CONF_EXECUTED_PLATFORM;
	public static final String EXECUTED_DEVICE_NAME = StringConstants.CONF_EXECUTED_DEVICE_NAME;

	private static final ThreadLocal<MobileDriverFactory> localMobileDriverFactoryStorage = new ThreadLocal<MobileDriverFactory>() {
        @Override
        protected MobileDriverFactory initialValue() {
            return new MobileDriverFactory();
        }
    };
    
	public enum OsType {
		IOS, ANDROID
	}

	private MobileDriverFactory() {
		androidDevices = new LinkedHashMap<>();
		iosDevices = new LinkedHashMap<>();
	}

	public static MobileDriverFactory getInstance() {
		return localMobileDriverFactoryStorage.get();
	}

	private void cleanup() throws InterruptedException, IOException {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("win")) {
			killProcessOnWin("adb.exe");
			killProcessOnWin("node.exe");
		} else {
			killProcessOnMac("adb");
			killProcessOnMac("node");
			killProcessOnMac("instruments");
			killProcessOnMac("deviceconsole");
		}
	}

	private void killProcessOnWin(String processName) throws InterruptedException, IOException {
		ProcessBuilder pb = new ProcessBuilder("taskkill", "/f", "/im", processName, "/t");
		pb.start().waitFor();
	}

	private void killProcessOnMac(String processName) throws InterruptedException, IOException {
		ProcessBuilder pb = new ProcessBuilder("killall", processName);
		pb.start().waitFor();
	}

	public AppiumDriver<?> getAndroidDriver(String deviceId, String appFile, boolean uninstallAfterCloseApp)
			throws Exception {
		cleanup();
		if (!isServerStarted()) {
			startAppiumServer();
		}
		DesiredCapabilities capabilities = MobileDriverPropertyUtil.toDesireCapabilities(
                RunConfiguration.getExecutionDriverProperty(), MobileDriverType.ANDROID_DRIVER);
		capabilities.setPlatform(Platform.ANDROID);
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceId);
		capabilities.setCapability("udid", deviceId);
		capabilities.setCapability(MobileCapabilityType.APP, appFile);
		capabilities.setCapability("fullReset", uninstallAfterCloseApp);
		capabilities.setCapability("noReset", !uninstallAfterCloseApp);
		capabilities.setCapability("newCommandTimeout", 1800);
		return new SwipeableAndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
	}

	@SuppressWarnings("rawtypes")
	public AppiumDriver<?> getIosDriver(String deviceId, String appFile, boolean uninstallAfterCloseApp) throws Exception {
		cleanup();
		if (!isServerStarted()) {
			startAppiumServer();
		}
		DesiredCapabilities capabilities = MobileDriverPropertyUtil.toDesireCapabilities(
                RunConfiguration.getExecutionDriverProperty(), MobileDriverType.IOS_DRIVER);
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceId);
		capabilities.setCapability(MobileCapabilityType.APP, appFile);
		capabilities.setCapability("udid", deviceId);
		capabilities.setCapability("fullReset", uninstallAfterCloseApp);
		capabilities.setCapability("noReset", !uninstallAfterCloseApp);
		capabilities.setCapability("newCommandTimeout", 1800);
		capabilities.setCapability("autoAcceptAlerts", true);
		capabilities.setCapability("waitForAppScript", true);
		return new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
	}

	private boolean isServerStarted() {
		if (appiumServer == null) {
			return false;
		} else {
			try {
				appiumServer.exitValue();
				return false;
			} catch (Exception e) {
				// LOGGER.warn(e.getMessage(), e);
				try {
					URL url = new URL("http://127.0.0.1:4723/wd/hub/status");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					int responseCode = connection.getResponseCode();
					if (200 == responseCode) {
						return true;
					}
				} catch (Exception e1) {
					// LOGGER.warn(e1.getMessage(), e1);
				}
			}
		}
		return false;
	}

	private void startAppiumServer() throws Exception {
		// String node = System.getenv("NODE_HOME") + "/node";
		// String appium = System.getenv("APPIUM_HOME") + "/appium.js";
	    String nodeHome = System.getenv("NODE_HOME") != null ? System.getenv("NODE_HOME") + File.separator : "";
        String node = nodeHome + "node";
		String appium = System.getenv("APPIUM_HOME") + "/bin/appium.js";
		String appiumTemp = System.getProperty("user.home") + File.separator + "Appium_Temp";
		String[] cmd = { node, appium, "--command-timeout", "3600", "--tmp", appiumTemp };
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectOutput(new File("appium.log"));
		appiumServer = pb.start();
		while (!isServerStarted()) {
		}
	}

	public void quitServer() {
		if (appiumServer != null) {
			appiumServer.destroy();
			appiumServer = null;
		}
	}

	public List<String> getDevices() throws Exception {
		getAndroidDevices();
		getIosDevices();
		List<String> devices = new ArrayList<>();
		devices.addAll(androidDevices.values());
		devices.addAll(iosDevices.values());
		return devices;
	}

	private void getIosDevices() throws Exception {
		iosDevices.clear();
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			List<String> deviceIds = new ArrayList<>();
			String[] cmd = { "idevice_id", "-l" };
			ProcessBuilder pb = new ProcessBuilder(cmd);
			Process p = pb.start();
			p.waitFor();
			String line = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = br.readLine()) != null) {
				deviceIds.add(line);
			}

			for (String deviceId : deviceIds) {
				cmd = new String[] { "ideviceinfo", "-u", deviceId };
				pb.command(cmd);
				p = pb.start();
				p.waitFor();
				br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String deviceInfo = "";
				while ((line = br.readLine()) != null) {
					if (line.contains("DeviceClass:")) {
						deviceInfo = line.substring("DeviceClass:".length(), line.length()).trim();
						continue;
					}
					if (line.contains("DeviceName:")) {
						deviceInfo += " " + line.substring("DeviceName:".length(), line.length()).trim();
						continue;
					}
					if (line.contains("ProductVersion:")) {
						deviceInfo += " " + line.substring("ProductVersion:".length(), line.length()).trim();
						continue;
					}
				}

				iosDevices.put(deviceId, deviceInfo);
			}
		}
	}

	private void getAndroidDevices() throws Exception {
		androidDevices.clear();
		String adbPath = System.getenv("ANDROID_HOME");
		if (adbPath != null) {
			List<String> deviceIds = new ArrayList<>();
			adbPath += File.separator + "platform-tools" + File.separator + "adb";
			String[] cmd = new String[] { adbPath, "devices" };
			ProcessBuilder pb = new ProcessBuilder(cmd);
			Process process = pb.start();
			process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line, deviceId, deviceName;
			while ((line = br.readLine()) != null) {
				if (!line.toLowerCase().trim().contains("list of devices")) {
					if (line.toLowerCase().trim().contains("device")) {
						deviceId = line.split("\\s")[0];
						deviceIds.add(deviceId);
					}
				}
			}
			br.close();

			for (String id : deviceIds) {
				cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.product.manufacturer" };
				pb.command(cmd);
				process = pb.start();
				process.waitFor();
				br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				deviceName = br.readLine();
				br.close();

				cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.product.model" };
				pb.command(cmd);
				process = pb.start();
				process.waitFor();
				br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				deviceName += " " + br.readLine();
				br.close();

				cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.build.version.release" };
				pb.command(cmd);
				process = pb.start();
				process.waitFor();
				br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				deviceName += " " + br.readLine();
				br.close();

				androidDevices.put(id, deviceName);
			}
		}
	}

	public String getDeviceId(String deviceName) {
		if (((androidDevices == null) || androidDevices.isEmpty()) && ((iosDevices == null) || iosDevices.isEmpty())) {
			return null;
		}

		for (Map.Entry<String, String> entry : androidDevices.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(deviceName) || entry.getKey().equalsIgnoreCase(deviceName)) {
				return entry.getKey();
			}
		}

		for (Map.Entry<String, String> entry : iosDevices.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(deviceName) || entry.getKey().equalsIgnoreCase(deviceName)) {
				return entry.getKey();
			}
		}

		return null;
	}

	public OsType getDeviceOs(String deviceId) {
		if (androidDevices.containsKey(deviceId)) {
			return OsType.ANDROID;
		} else if (iosDevices.containsKey(deviceId)) {
			return OsType.IOS;
		} else {
			return null;
		}
	}

	public static String getDevicePlatform() {
		return RunConfiguration.getStringProperty(EXECUTED_PLATFORM);
	}

	public static String getDeviceName() {
		return RunConfiguration.getStringProperty(EXECUTED_DEVICE_NAME);
	}
}
