package com.kms.katalon.platform.internal.entity;

import com.kms.katalon.entity.variable.VariableEntity;

public class VariableEntityImpl implements com.katalon.platform.api.model.VariableEntity {

    private VariableEntity source;

    public VariableEntityImpl(VariableEntity source) {
        this.source = source;
    }

    @Override
    public String getDefaultValue() {
        return source.getDefaultValue();
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
