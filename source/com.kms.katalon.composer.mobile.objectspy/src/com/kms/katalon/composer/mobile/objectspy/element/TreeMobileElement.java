package com.kms.katalon.composer.mobile.objectspy.element;

import java.util.List;

import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

public interface TreeMobileElement extends MobileElement {

    TreeMobileElement getParentElement();

    List<? extends TreeMobileElement> getChildrenElement();

    CapturedMobileElement getCapturedElement();

    CapturedMobileElement newCapturedElement();

    void setCapturedElement(CapturedMobileElement object);

    TreeMobileElement findBestMatch(CapturedMobileElement needToVerify);
}
