package com.kms.katalon.composer.windows.dialog;

import com.kms.katalon.composer.windows.element.CapturedWindowsElement;

public interface WindowsObjectDialog {
    void refreshButtonsState();

    void setSelectedElementByLocation(int x, int y);

    void updateSelectedElement(CapturedWindowsElement editingElement);
}
