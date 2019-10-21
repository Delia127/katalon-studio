package com.kms.katalon.composer.mobile.objectspy.dialog;

import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

public interface MobileElementDialog {
    void updateSelectedElement(CapturedMobileElement editingElement);
    void handleCapturedObjectsTableSelectionChange();
}
