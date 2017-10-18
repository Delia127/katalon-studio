package com.kms.katalon.objectspy.highlight;

import java.text.MessageFormat;

import com.kms.katalon.objectspy.core.MessageConstant;
import com.kms.katalon.objectspy.core.RequestType;
import com.kms.katalon.objectspy.element.XPathProvider;

public class FindObjectRequest extends ElementRequest {
    
    public FindObjectRequest(XPathProvider element) {
        super(RequestType.HIGHLIGHT_TEST_OBJECT, element);
        setRequestData(createXPathExpression());
    }

    @Override
    public String processFailed() {
        return MessageFormat.format(MessageConstant.TEST_OBJECT_NOT_FOUND, getElement().getName());
    }
}
