package com.kms.katalon.composer.windows.helper;

import java.util.Map;

import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.element.SnapshotWindowsElement;
import com.kms.katalon.entity.repository.WindowsElementEntity.LocatorStrategy;

public class WindowsElementHelper {

    public static String getLocatorByStrategy(LocatorStrategy strategy, SnapshotWindowsElement snapshotWindowsElement) {
        switch (strategy) {
            case ACCESSIBILITY_ID:
                return snapshotWindowsElement.getPropertyValue("AutomationId");
            case CLASS_NAME:
                return snapshotWindowsElement.getPropertyValue("ClassName");
            case ID:
                return snapshotWindowsElement.getPropertyValue("RuntimeId");
            case NAME:
                return snapshotWindowsElement.getPropertyValue("Name");
            case TAG_NAME:
                return snapshotWindowsElement.getTagName();
            case XPATH:
                return snapshotWindowsElement.getXPath();
            default:
                return null;
        }
    }

    public static String getLocatorByStrategy(LocatorStrategy strategy, CapturedWindowsElement capturedWindowsElement) {
        SnapshotWindowsElement snapshotWindowsElement = capturedWindowsElement.getSnapshotWindowsElement();
        if (snapshotWindowsElement != null) {
            return getLocatorByStrategy(strategy, snapshotWindowsElement);
        }

        Map<String, String> elementProperties = capturedWindowsElement.getProperties();
        switch (strategy) {
            case ACCESSIBILITY_ID:
                return elementProperties.get("AutomationId");
            case CLASS_NAME:
                return elementProperties.get("ClassName");
            case ID:
                return elementProperties.get("RuntimeId");
            case NAME:
                return elementProperties.get("Name");
            case TAG_NAME:
                return capturedWindowsElement.getTagName();
            case XPATH:
                return elementProperties.get("XPath");
            default:
                return null;
        }
    }
}
