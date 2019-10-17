package com.kms.katalon.composer.mobile.objectspy.dialog;

import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

/**
 * Interface for dialogs that can inspect element
 *
 */
public interface MobileElementInspectorDialog {
    void setSelectedElementByLocation(int x, int y);
    
    MobileElementPropertiesComposite getPropertiesComposite();
    
    void setSelectedElement(CapturedMobileElement element);
    // To Inspect we need:
    // - Captured Elements table: show info of the captured element
    // - Properties table: show details of the captured element
    // - All Elements tree: no need -> communicate through main dialog
    // 
}
