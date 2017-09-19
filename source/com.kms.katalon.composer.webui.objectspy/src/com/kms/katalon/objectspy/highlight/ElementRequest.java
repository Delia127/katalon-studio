package com.kms.katalon.objectspy.highlight;

import com.kms.katalon.objectspy.core.KatalonRequest;
import com.kms.katalon.objectspy.core.MessageConstant;
import com.kms.katalon.objectspy.element.XPathProvider;

public abstract class ElementRequest extends KatalonRequest{
    private XPathProvider element;

    public ElementRequest(String request, XPathProvider element) {
        super(request);
        this.element = element;
    }
    
    protected String createXPathExpression() {
        StringBuilder xPath = new StringBuilder();
        
        if (element.getParent() == null) {
            xPath.append(element.getXpath());
        }
        else {
            XPathProvider parent = element;
            while (parent != null && !(parent.getParent() == null)) {
                xPath.insert(0, parent.getXpath()).insert(0, MessageConstant.XPATH_SEPARATOR);
                parent = parent.getParent();
            }
            xPath.delete(0, MessageConstant.XPATH_SEPARATOR.length());
        }
        
        return xPath.toString();
    }
    
    public XPathProvider getElement() {
        return element;
    }
}
