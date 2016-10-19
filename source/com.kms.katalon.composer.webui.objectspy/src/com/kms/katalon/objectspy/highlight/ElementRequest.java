package com.kms.katalon.objectspy.highlight;

import com.kms.katalon.objectspy.core.KatalonRequest;
import com.kms.katalon.objectspy.core.MessageConstant;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;

public abstract class ElementRequest extends KatalonRequest{
    private HTMLElement element;

    public ElementRequest(String request, HTMLElement element) {
        super(request);
        this.element = element;
    }
    
    protected String createXPathExpression() {
        StringBuilder xPath = new StringBuilder();
        
        if (element.getParentElement() == null) {
            xPath.append(element.getXpath());
        }
        else {
            HTMLElement parent = element;
            while (parent != null && !(parent instanceof HTMLPageElement)) {
                xPath.insert(0, parent.getXpath()).insert(0, MessageConstant.XPATH_SEPARATOR);
                parent = parent.getParentElement();
            }
            xPath.delete(0, MessageConstant.XPATH_SEPARATOR.length());
        }
        
        return xPath.toString();
    }
    
    public HTMLElement getElement() {
        return element;
    }
}
