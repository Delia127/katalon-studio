package com.kms.katalon.composer.mobile.objectspy.dialog;

import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

/**
 * Interface for dialogs that can inspect element
 *
 */
public interface MobileElementInspectorDialog {
    void setSelectedElementByLocation(int x, int y);

    /**
     * <ul>
     *  <li>Focus to Element on `All Elements Tree`
     *  <li>Focus to Element on `Captured Elements Table`
     *  <li>Show Element's properties on `Properties Table`
     *  <li>Highlight Element on `Screen View`
     *  <li>Update button states base on Element type (Recorder Dialog only)
     * </ul>
     * @param element
     */
    void setSelectedElement(CapturedMobileElement element);

    void updateSelectedElement(CapturedMobileElement element);
    
    void handleCapturedObjectsTableSelectionChange();

    void highlightElement(MobileElement selectedElement);
    
    void setEdittingElement(CapturedMobileElement element);
    
    void addCapturedElement(CapturedMobileElement element);
    
    void removeCapturedElement(CapturedMobileElement element);
    
    boolean isAddedCapturedElement(CapturedMobileElement element);

    void removeSelectedCapturedElements(CapturedMobileElement[] elements);
    
    void verifyCapturedElementsStates(CapturedMobileElement[] elements);
    
    // Focus and edit `Captured element name` on `Properties Composite`
    void focusAndEditCapturedElementName();
    
}
