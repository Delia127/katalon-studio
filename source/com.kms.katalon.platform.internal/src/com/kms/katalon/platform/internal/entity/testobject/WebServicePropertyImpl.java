package com.kms.katalon.platform.internal.entity.testobject;

import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class WebServicePropertyImpl implements com.katalon.platform.api.model.testobject.WebServiceProperty {

    private WebElementPropertyEntity source;

    public WebServicePropertyImpl(WebElementPropertyEntity source) {
        this.source = source;
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getValue() {
        return source.getValue();
    }

}
