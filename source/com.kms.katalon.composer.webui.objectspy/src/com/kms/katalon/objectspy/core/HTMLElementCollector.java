package com.kms.katalon.objectspy.core;

import org.w3c.dom.Document;

import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLRawElement;
import com.kms.katalon.objectspy.element.WebElement;

public interface HTMLElementCollector {
    // TODO remove this when new object spy complete
    void setHTMLDOMDocument(final HTMLRawElement bodyElement, Document document);

    // TODO remove this when new object spy complete
    public void addNewElement(HTMLElement newElement);

    /**
     * Add captured web element for new object spy
     * 
     * @param newElement new WebElement
     */
    public void addNewElement(WebElement newElement);
}
