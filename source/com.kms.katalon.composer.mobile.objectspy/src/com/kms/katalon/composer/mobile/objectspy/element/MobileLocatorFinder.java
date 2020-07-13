package com.kms.katalon.composer.mobile.objectspy.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.core.mobile.keyword.internal.MobileSearchEngine;
import com.kms.katalon.core.testobject.ConditionType;
import com.kms.katalon.core.testobject.MobileTestObject;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.entity.repository.MobileElementEntity;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class MobileLocatorFinder {

    private CapturedMobileElement element;

    public MobileLocatorFinder(CapturedMobileElement element) {
        this.element = element;
    }

    public MobileTestObject buildTestObject() {
        MobileTestObject testObject = new MobileTestObject("");
        List<TestObjectProperty> properties = new ArrayList<>();
        for (Entry<String, String> attr : this.element.getAttributes().entrySet()) {
            String key = attr.getKey();
            String value = attr.getValue();
            TestObjectProperty prop = new TestObjectProperty(key, ConditionType.EQUALS, value);
            if ("visible".equals(key)) {
                prop.setActive(false);
            }
            properties.add(prop);
        }
        testObject.setProperties(properties);
        return testObject;
    }

    @SuppressWarnings("rawtypes")
    public String findLocator(AppiumDriver<?> appiumDriver, MobileElementEntity.LocatorStrategy strategy) {
        if (appiumDriver instanceof AndroidDriver) {
            MobileSearchEngine searchEngine = new MobileSearchEngine(appiumDriver, buildTestObject());
            switch (strategy) {
                case ACCESSIBILITY:
                    return searchEngine.findAndroidAccessibilityId();
                case ANDROID_UI_AUTOMATOR:
                    return searchEngine.findAndroidUIAutomatorSelector();
                case CLASS_NAME:
                    return ((SnapshotMobileElement) element.getLink()).getTagName();
                case ID:
                    return searchEngine.findAndroidID();
                case NAME:
                    return searchEngine.findAndroidName();
                case XPATH:
                    return this.element.getXpath();
                default:
                    return StringUtils.EMPTY;
            }
        } else if (appiumDriver instanceof IOSDriver) {
            MobileSearchEngine searchEngine = new MobileSearchEngine(appiumDriver, buildTestObject());
            switch (strategy) {
                case CLASS_NAME:
                    return ((SnapshotMobileElement) element.getLink()).getTagName();
                case ID:
                    return searchEngine.findIOSID();
                case XPATH:
                    return this.element.getXpath();
                case IOS_CLASS_CHAIN:
                    return searchEngine.findIOSClassChain();
                case IOS_PREDICATE_STRING:
                    return searchEngine.findIOSPredicateString();
                case NAME:
                    return searchEngine.findIOSName();
                default:
                    return StringUtils.EMPTY;

            }
        }
        return StringUtils.EMPTY;

    }
}
