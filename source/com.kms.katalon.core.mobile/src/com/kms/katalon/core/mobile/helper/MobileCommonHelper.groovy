package com.kms.katalon.core.mobile.helper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.openqa.selenium.interactions.touch.TouchActions;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords;
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory;
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory.OsType;

public class MobileCommonHelper {

    public static AppiumDriver initializeMobileDriver(String appFile, boolean uninstallAfterCloseApp) throws Exception {
    	MobileDriverFactory.getInstance().getDevices();
        String deviceId = MobileDriverFactory.getInstance().getDeviceId(System.getProperty(MobileDriverFactory.EXECUTED_DEVICE_NAME));
        OsType deviceOs = MobileDriverFactory.getInstance().getDeviceOs(deviceId);
        switch (deviceOs) {
        case OsType.IOS:
            try {
                return MobileDriverFactory.getInstance().getIosDriver(deviceId, appFile, uninstallAfterCloseApp);
            } catch (Exception e) {
                return null;
            }
        case OsType.ANDROID:
            try {
                return MobileDriverFactory.getInstance().getAndroidDriver(deviceId, appFile, uninstallAfterCloseApp);
            } catch (Exception e) {
                return null;
            }
        default:
            return null;
        }
    }
    
    public static void swipe(AppiumDriver driver, int startX, int startY, int endX, int endY){
        if (driver instanceof AndroidDriver) {
            TouchActions ta = new TouchActions(driver);
            ta.down(startX, startY).move(endX, endY).up(endX, endY).perform();
        } else if (driver instanceof IOSDriver) {
            driver.swipe(startX, startY, endX, endY, 500);
        }
    }

    public static String getDeviceModel() throws StepFailedException, IOException, InterruptedException {
    	String deviceId = MobileDriverFactory.getInstance().getDeviceId(System.getProperty(MobileDriverFactory.EXECUTED_DEVICE_NAME));
        OsType deviceOs = MobileDriverFactory.getInstance().getDeviceOs(deviceId);
        String model = null;
        ProcessBuilder pb = new ProcessBuilder();
        Process p = null;
        BufferedReader br = null;
        switch (deviceOs) {
        case OsType.IOS:
            pb.command("ideviceinfo", "-u", deviceId);
            p = pb.start();
            p.waitFor();
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((model = br.readLine()) != null) {
                if (model.contains("ProductType:")) {
                    model = model.substring(model.lastIndexOf(':') + 1).trim();
                    break;
                }
            }
            break;
        case OsType.ANDROID:
            String adbPath = System.getenv("ANDROID_HOME") + File.separator + "platform-tools" + File.separator + "adb";
            pb.command(adbPath, "-s", deviceId, "shell", "getprop", "ro.product.model");
            p = pb.start();
            p.waitFor();
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            model = br.readLine();
            br.close();
            break;
        default:
        	throw new StepFailedException(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE);
        }
        return model;
    }

    public static String getDeviceOSVersion() throws StepFailedException, IOException, InterruptedException {
    	String deviceId = MobileDriverFactory.getInstance().getDeviceId(System.getProperty(RunConfiguration.EXECUTED_DEVICE_NAME));
        OsType deviceOs = MobileDriverFactory.getInstance().getDeviceOs(deviceId);
        String osVersion = null;
        ProcessBuilder pb = new ProcessBuilder();
        Process p = null;
        BufferedReader br = null;
        switch (deviceOs) {
        case OsType.IOS:
            pb.command("ideviceinfo", "-u", deviceId);
            p = pb.start();
            p.waitFor();
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((osVersion = br.readLine()) != null) {
                if (osVersion.contains("ProductVersion:")) {
                    osVersion = osVersion.substring(osVersion.lastIndexOf(':') + 1).trim();
                    break;
                }
            }
            break;
        case OsType.ANDROID:
            String adbPath = System.getenv("ANDROID_HOME") + File.separator + "platform-tools" + File.separator + "adb";
            pb.command(adbPath, "-s", deviceId, "shell", "getprop", "ro.build.version.release");
            p = pb.start();
            p.waitFor();
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            osVersion = br.readLine();
            br.close();
            break;
        default:
        	throw new StepFailedException(StringConstants.KW_MSG_UNSUPPORT_ACT_FOR_THIS_DEVICE);
        }
        return osVersion;
    }
   
	//public static Map<String,String> configs = new HashMap<String, String>();
	//static {
	//	configs.put("iPhone6,1", "40;195");
	//	configs.put("iPad2,4", "260;905");		
	//	configs.put("iPad4,2", "260;905");
	//}
	
	public static Map<String,String> deviceModels = new HashMap<String, String>();
	static {
		deviceModels.put("iPhone3,1", "iPhone 4");
		deviceModels.put("iPhone3,3", "iPhone 4");
		deviceModels.put("iPhone4,1", "iPhone 4S");
		
		deviceModels.put("iPhone5,1", "iPhone 5");
		deviceModels.put("iPhone5,2", "iPhone 5");
		deviceModels.put("iPhone5,3", "iPhone 5c");
		deviceModels.put("iPhone5,4", "iPhone 5c");
		deviceModels.put("iPhone6,1", "iPhone 5s");
		deviceModels.put("iPhone6,2", "iPhone 5s");
		deviceModels.put("iPhone7,1", "iPhone 6 Plus");
		deviceModels.put("iPhone7,2", "iPhone 6");
		deviceModels.put("iPad1,1", "iPad");
		deviceModels.put("iPad2,1", "iPad 2");
		deviceModels.put("iPad2,2", "iPad 2");
		deviceModels.put("iPad2,3", "iPad 2");
		deviceModels.put("iPad2,4", "iPad 2");
		deviceModels.put("iPad2,5", "iPad mini");
		deviceModels.put("iPad2,6", "iPad mini");
		deviceModels.put("iPad2,7", "iPad mini");

		deviceModels.put("iPad3,1", "iPad 3");
		deviceModels.put("iPad3,2", "iPad 3");
		deviceModels.put("iPad3,3", "iPad 3");
		deviceModels.put("iPad3,4", "iPad 4");
		deviceModels.put("iPad3,5", "iPad 4");
		deviceModels.put("iPad3,6", "iPad 4");
		deviceModels.put("iPad4,1", "iPad Air");
		deviceModels.put("iPad4,2", "iPad Air");
		deviceModels.put("iPad4,3", "iPad Air");
		deviceModels.put("iPad4,4", "iPad mini 2");
		deviceModels.put("iPad4,5", "iPad mini 2");
		deviceModels.put("iPad4,6", "iPad mini 2");
		deviceModels.put("iPad4,7", "iPad mini 3");
		deviceModels.put("iPad4,8", "iPad mini 3");
		deviceModels.put("iPad4,9", "iPad mini 3");
		deviceModels.put("iPad5,3", "iPad Air 2");
		deviceModels.put("iPad5,4", "iPad Air 2");

	}
	
	public static Map<String,String> airPlaneButtonCoords = new HashMap<String, String>();
	static {
		airPlaneButtonCoords.put("iPhone 5s", "40;195");
		airPlaneButtonCoords.put("iPhone 5", "40;195");
		
		airPlaneButtonCoords.put("iPad 2", "260;905");
		airPlaneButtonCoords.put("iPad 3", "260;905");
		airPlaneButtonCoords.put("iPad 4", "260;905");
		
		airPlaneButtonCoords.put("iPad Air", "260;905");
		airPlaneButtonCoords.put("iPad Air 2", "260;905");
		
		airPlaneButtonCoords.put("iPhone 6", "50;290");
		airPlaneButtonCoords.put("iPhone 6 Plus", "59;359");
		
		airPlaneButtonCoords.put("iPad mini", "265;905");
		airPlaneButtonCoords.put("iPad mini 2", "265;905");
		airPlaneButtonCoords.put("iPad mini 3", "265;905");
	}
	
	/*public static void loadConfigs() throws Exception {
		Properties props = new Properties();
		String path = MobileBuiltInKeywords.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		path = URLDecoder.decode(path, "utf-8");
		File jarFile = new File(path);
		if (jarFile.isFile()) {
			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String name = jarEntry.getName();
				if (name.endsWith("config.properties")) {
					props.load(jar.getInputStream(jarEntry));
					break;
				}
			}
			jar.close();
		} else { // Run with IDE
			File confFile = new File(path + "../" + "resources/config.properties");
			props.load(new FileInputStream(confFile));
		}
		
		System.getProperties().putAll(props);
	}
	*/
}
