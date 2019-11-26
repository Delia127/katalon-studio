package com.kms.katalon.composer.mobile.objectspy.element;

import java.util.List;

import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

import io.appium.java_client.AppiumDriver;

public interface TreeMobileElement extends MobileElement {

    TreeMobileElement getParentElement();

    List<? extends TreeMobileElement> getChildrenElement();

    CapturedMobileElement getCapturedElement();

    CapturedMobileElement newCapturedElement(AppiumDriver<?> windowsDriver);

    void setCapturedElement(CapturedMobileElement object);

    TreeMobileElement findBestMatch(CapturedMobileElement needToVerify);
}
