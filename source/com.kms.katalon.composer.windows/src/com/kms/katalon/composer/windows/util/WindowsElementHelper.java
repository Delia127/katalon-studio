package com.kms.katalon.composer.windows.util;

import com.kms.katalon.composer.windows.element.SnapshotWindowsElement;
import com.kms.katalon.entity.repository.WindowsElementEntity.LocatorStrategy;

public class WindowsElementHelper {
    
    public static String getLocatorByStrategy(LocatorStrategy strategy, SnapshotWindowsElement snapshotWindowsElement) {
        switch (strategy) {
            case ACCESSIBILITY_ID:
                return snapshotWindowsElement.getPropertyValue("AccessibilityId");
            case CLASS_NAME:
                return snapshotWindowsElement.getPropertyValue("ClassName");
            case ID:
                return snapshotWindowsElement.getPropertyValue("id");
            case NAME:
                return snapshotWindowsElement.getPropertyValue("Name");
            case TAG_NAME:
                return snapshotWindowsElement.getName();
            case XPATH:
                return snapshotWindowsElement.getXPath();
            default:
                return null;
        }
    }
}
