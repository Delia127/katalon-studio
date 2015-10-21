package com.kms.katalon.core.webui.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

class WebMobileDriverFactory {
	private Process appiumServer;
	private Process webProxyServer;
	private Map<String, String> androidDevices;
	private Map<String, String> iosDevices;
	
	private static final ThreadLocal<WebMobileDriverFactory> localWebMobileDriverFactoryStorage = new ThreadLocal<WebMobileDriverFactory>() {
        @Override
        protected WebMobileDriverFactory initialValue() {
            return new WebMobileDriverFactory();
        }
    };

	private WebMobileDriverFactory() {
		androidDevices = new LinkedHashMap<>();
		iosDevices = new LinkedHashMap<>();
	}

    static WebMobileDriverFactory getInstance() {
		return localWebMobileDriverFactoryStorage.get();
	}

	private void cleanup() {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("win")) {
			killProcessOnWin("adb.exe");
			killProcessOnWin("node.exe");
		} else {
			killProcessOnMac("adb");
			killProcessOnMac("node");
			killProcessOnMac("instruments");
			killProcessOnMac("deviceconsole");
			killProcessOnMac("ios_webkit_debug_proxy");
		}
	}

	private void killProcessOnWin(String processName) {
		ProcessBuilder pb = new ProcessBuilder("taskkill", "/f", "/im", processName, "/t");
		try {
			pb.start().waitFor();
		} catch (Exception e) {
			// LOGGER.error(e.getMessage(), e);
		}
	}

	private void killProcessOnMac(String processName) {
		ProcessBuilder pb = new ProcessBuilder("killall", processName);
		try {
			pb.start().waitFor();
		} catch (Exception e) {
			// LOGGER.error(e.getMessage(), e);
		}
	}

	AppiumDriver<?> getAndroidDriver(String deviceName)
			throws Exception {
		cleanup();
		if (!isServerStarted()) {
			startAppiumServer();
		}
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setPlatform(Platform.ANDROID);
		capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
		return new SwipeableAndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
	}

	@SuppressWarnings("rawtypes")
	AppiumDriver<?> getIosDriver(String deviceName) throws Exception {
		cleanup();
        if (!isWebProxyServerStarted()) {
            startWebProxyServer(deviceName);
        }
		if (!isServerStarted()) {
			startAppiumServer();
		}
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
		capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Safari");
		capabilities.setCapability("udid", deviceName);
		capabilities.setCapability("autoAcceptAlerts", false);
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
	
	private boolean isWebProxyServerStarted() {
        if (webProxyServer == null) {
            return false;
        } else {
            try {
                webProxyServer.exitValue();
                return false;
            } catch (IllegalThreadStateException e) {
                return true;
            }
        }
    }

	private void startAppiumServer() throws Exception {
		String appium = System.getenv("APPIUM_HOME") + "/bin" + "/appium.js";
		String appiumTemp = System.getProperty("user.home") + File.separator + "Appium_Temp";
		String[] cmd = {"node", appium, "--command-timeout", "3600", "--tmp", appiumTemp };
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectOutput(new File("bin" + File.separator + "appium.log"));
		appiumServer = pb.start();
		while (!isServerStarted()) {
        }
		
	}
	
	private void startWebProxyServer(String deviceId) throws Exception {
        String webProxyServerLocation = "ios_webkit_debug_proxy";
        String[] webProxyServerCmd = { webProxyServerLocation, "-c", deviceId + ":27753"};
        ProcessBuilder webProxyServerProcessBuilder = new ProcessBuilder(webProxyServerCmd);
        webProxyServerProcessBuilder.redirectOutput(new File("bin" + File.separator + "appium-proxy-server.log"));
        webProxyServer = webProxyServerProcessBuilder.start();
        while (!isWebProxyServerStarted()) {
        }
	}

	void quitServer() {
		if (appiumServer != null) {
			appiumServer.destroy();
			appiumServer = null;
		}
	}

	List<String> getDevices() throws Exception {
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

	String getDeviceId(String deviceName) {
		if (((androidDevices == null) || androidDevices.isEmpty()) && ((iosDevices == null) || iosDevices.isEmpty())) {
			return null;
		}

		for (Map.Entry<String, String> entry : androidDevices.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(deviceName)) {
				return entry.getKey();
			}
		}

		for (Map.Entry<String, String> entry : iosDevices.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(deviceName)) {
				return entry.getKey();
			}
		}

		return null;
	}
}
