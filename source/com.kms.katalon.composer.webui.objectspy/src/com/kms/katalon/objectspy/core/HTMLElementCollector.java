package com.kms.katalon.objectspy.core;

import com.kms.katalon.objectspy.element.WebElement;

public interface HTMLElementCollector {

    /**
     * Add captured web element for new object spy
     * 
     * @param newElement new WebElement
     */
    public void addNewElement(WebElement newElement);
}
