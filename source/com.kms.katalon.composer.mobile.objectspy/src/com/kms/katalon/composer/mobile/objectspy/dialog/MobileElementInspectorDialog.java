package com.kms.katalon.composer.mobile.objectspy.dialog;

import com.kms.katalon.composer.mobile.objectspy.composites.MobileCapturedObjectsComposite;
import com.kms.katalon.composer.mobile.objectspy.composites.MobileElementPropertiesComposite;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

/**
 * Interface for dialogs that can inspect element
 *
 */
public interface MobileElementInspectorDialog {
    void setSelectedElementByLocation(int x, int y);
    
    MobileElementPropertiesComposite getPropertiesComposite();
    
    void setSelectedElement(CapturedMobileElement element);
    
    void removeSelectedCapturedElements(CapturedMobileElement[] elements);
    
    void verifyCapturedElementsStates(CapturedMobileElement[] elements);

    void highlightElement(MobileElement selectedElement);
    
    void targetElementChanged(CapturedMobileElement mobileElement);
    
    MobileCapturedObjectsComposite getCapturedObjectsComposite();

    // To Inspect we need:
    // - Captured Elements table: show info of the captured element
    // - Properties table: show details of the captured element
    // - All Elements tree: no need -> communicate through main dialog
    // 
    // What an Inspector can do:
    // 1. Highlight element
    // 2. Store elements in a list (Captured Elements Table)
    //    - storeElement(element)
    // 3. View element properties (Element Properties)
    //    - setEdittingElement(element)
    // 4. Inspecting all elements in a view (All Elements Tree)
    //
    // Components:
    // 1. Screen View
    // 2. All Elements Tree
    // 3. Captured Elements Table
    // 4. Element Properties Table
    // 
    // 
    // Communicate between components:
    // 1.1 [Captured Elements Table] -> [Screen View]
    // 1.2 [Captured Elements Table] -> [Element Properties Table]
    // 1.3 [Captured Elements Table] -> [All Elements Tree]
    // 2.1 [All Elements Tree] -> [Captured Elements Table]
    // 2.2 [All Elements Tree] -> [Screen View]
    // 2.3 [All Elements Tree] -> [Element Properties Table]
    // 3.1 [Screen View] -> [Captured Elements Table]
    // 3.2 [Screen View] -> [All Elements Tree]
    // 3.3 [Screen View] -> [Element Properties Table]
    // 4.1 [Element Properties Table] -> [Captured Elements Table]
    // 4.2 [Element Properties Table] -> [Screen View]
    // 4.3 [Element Properties Table] -> [All Elements Tree]
}
