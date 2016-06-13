package com.kms.katalon.core.mobile.helper;

import groovy.transform.CompileStatic
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver

import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement

import com.kms.katalon.core.mobile.keyword.AndroidProperties;
import com.kms.katalon.core.mobile.keyword.GUIObject
import com.kms.katalon.core.mobile.keyword.MobileDriverFactory

public class MobileCommonHelper {
    private static final String ATTRIBUTE_NAME_FOR_ANDROID_RESOURCE_ID = "resourceId"

    private static final String ATTRIBUTE_NAME_FOR_ANDROID_CONTENT_DESC = "name";
    
    @CompileStatic
    public static void swipe(AppiumDriver driver, int startX, int startY, int endX, int endY){
        driver.swipe(startX, startY, endX, endY, 500);
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

    @CompileStatic
    public static String getAttributeValue(WebElement element, String attributeName) {
        switch (attributeName.toString()) {
            case GUIObject.HEIGHT:
                return String.valueOf(element.getSize().height);
            case GUIObject.WIDTH:
                return String.valueOf(element.getSize().width);
            case GUIObject.X:
                return String.valueOf(element.getLocation().x);
            case GUIObject.Y:
                return String.valueOf(element.getLocation().y);
            case AndroidProperties.ANDROID_RESOURCE_ID:
                if (MobileDriverFactory.getDriver() instanceof AndroidDriver) {
                    return element.getAttribute(ATTRIBUTE_NAME_FOR_ANDROID_RESOURCE_ID);
                }
            case AndroidProperties.ANDROID_CONTENT_DESC:
                if (MobileDriverFactory.getDriver() instanceof AndroidDriver) {
                    return element.getAttribute(ATTRIBUTE_NAME_FOR_ANDROID_CONTENT_DESC);
                }
            default:
                try {
                    return element.getAttribute(attributeName);
                } catch (NoSuchElementException e) {
                    // attribute not found, return null
                    return null
                }
        }
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
