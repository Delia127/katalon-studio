package com.kms.katalon.composer.windows.dialog;

import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.spy.WindowsInspectorController;

public interface WindowsObjectDialog {
    void refreshButtonsState();

    void setSelectedElementByLocation(int x, int y);

    void updateSelectedElement(CapturedWindowsElement editingElement);
    
    WindowsInspectorController getInspectorController();
    
    void highlightElementRects(List<Rectangle> rects);
}