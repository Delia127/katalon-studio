package com.kms.katalon.core.windows.keyword.helper;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

import com.kms.katalon.core.testobject.WindowsTestObject;
import com.kms.katalon.core.windows.driver.WindowsDriverFactory;
import com.kms.katalon.core.windows.keyword.exception.DriverNotStartedException;

import io.appium.java_client.windows.WindowsDriver;

public class WindowsElementHelper {

    public static WebElement findElement(WindowsTestObject testObject) throws IllegalArgumentException, DriverNotStartedException {
        if (testObject == null) {
            throw new IllegalArgumentException("Test object cannot be null");
        }

        WindowsTestObject.LocatorStrategy selectedLocator = testObject.getLocatorStrategy();
        String locator = testObject.getLocator();
        if (StringUtils.isEmpty(locator)) {
            throw new IllegalArgumentException(String.format("Test object %s does not have locator for strategy: %s. ",
                    testObject.getObjectId(), selectedLocator));
        }

        WindowsDriver<WebElement> windowsDriver = WindowsDriverFactory.getWindowsDriver();
        if (windowsDriver == null) {
            throw new DriverNotStartedException("WindowsDriver has not started yet!");
        }
        
        switch (selectedLocator) {
            case ACCESSIBILITY_ID:
                return windowsDriver.findElementByAccessibilityId(locator);
            case CLASS_NAME:
                return windowsDriver.findElementByClassName(locator);
            case ID:
                return windowsDriver.findElementById(locator);
            case NAME:
                return windowsDriver.findElementByName(locator);
            case TAG_NAME:
                return windowsDriver.findElementByTagName(locator);
            case XPATH:
                return windowsDriver.findElementByXPath(locator);
            default:
                break;
        }
        return null;
    }
}
