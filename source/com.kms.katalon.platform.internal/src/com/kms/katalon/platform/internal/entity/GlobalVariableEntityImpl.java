package com.kms.katalon.platform.internal.entity;

import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableEntityImpl implements com.katalon.platform.api.model.VariableEntity {

    private GlobalVariableEntity source;

    public GlobalVariableEntityImpl(GlobalVariableEntity source) {
        this.source = source;
    }

    @Override
    public String getDefaultValue() {
        return source.getInitValue();
    }

    @Override
    public String getDescription() {
        return source.getDescription();
    }

    @Override
    public String getName() {
        return source.getName();
    }

}
