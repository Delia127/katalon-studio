package com.kms.katalon.platform.internal.entity.testobject;

import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class WebElementPropertyImpl implements com.katalon.platform.api.model.testobject.WebElementProperty {

    private final WebElementPropertyEntity source;

    public WebElementPropertyImpl(WebElementPropertyEntity source) {
        this.source = source;
    }

    @Override
    public String getMatchCondition() {
        return source.getMatchCondition();
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getValue() {
        return source.getValue();
    }

    @Override
    public boolean isSelected() {
        return source.getIsSelected();
    }

}
