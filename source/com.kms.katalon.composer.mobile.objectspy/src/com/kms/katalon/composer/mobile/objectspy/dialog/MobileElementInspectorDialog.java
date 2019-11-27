package com.kms.katalon.composer.mobile.objectspy.dialog;

import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.TreeMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

/**
 * Interface for dialogs that can inspect element
 *
 */
public interface MobileElementInspectorDialog {
    MobileInspectorController getInspectorController();
    
    void setSelectedElementByLocation(int x, int y);

    /** What this method do:
     * <ul>
     *  <li>Focus to Selected Element on `All Elements Tree`
     *  <li>Focus to Selected Element on `Captured Elements Table`
     *  <li>Show Selected Element's properties on `Properties Table`
     *  <li>Highlight Selected Element on `Screen View`
     *  <li>Update action button states (Recorder Dialog only)
     * </ul>
     * @param element
     */
    void setSelectedElement(MobileElement element);

    void updateSelectedElement(CapturedMobileElement element);
    
    void handleCapturedObjectsTableSelectionChange();

    void highlightElement(MobileElement selectedElement);
    
    void highlightElementRects(List<Rectangle> rects);
    
    void setEdittingElement(CapturedMobileElement element);
    
    void addCapturedElement(CapturedMobileElement element);
    
    void removeCapturedElement(CapturedMobileElement element);
    
    boolean isAddedCapturedElement(CapturedMobileElement element);

    void removeSelectedCapturedElements(CapturedMobileElement[] elements);
    
    void verifyCapturedElementsStates(CapturedMobileElement[] elements);
    
    // Focus and edit `Captured element name` on `Properties Composite`
    void focusAndEditCapturedElementName();

    CapturedMobileElement captureMobileElement(TreeMobileElement treeElement);
}
