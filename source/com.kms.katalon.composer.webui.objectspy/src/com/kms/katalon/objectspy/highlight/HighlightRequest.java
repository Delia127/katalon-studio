package com.kms.katalon.objectspy.highlight;

import java.text.MessageFormat;

import com.kms.katalon.objectspy.core.MessageConstant;
import com.kms.katalon.objectspy.core.RequestType;
import com.kms.katalon.objectspy.element.HTMLElement;

public class HighlightRequest extends ElementRequest {
    private boolean found = false;

    private int clientId = -1;
    

    public HighlightRequest(HTMLElement element) {
        super(RequestType.HIGHLIGHT_TEST_OBJECT, element);
        setRequestData(createXPathExpression());
    }

    public void setFound(boolean isFound) {
        found = isFound;
    }

    public boolean isFound() {
        return found;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }
    
    @Override
    public String getData() {
        return (String) super.getData();
    }

    @Override
    public String processFailed() {
        return MessageFormat.format(MessageConstant.TEST_OBJECT_NOT_FOUND, getElement().getName());
    }
}
