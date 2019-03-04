package com.kms.katalon.platform.internal.entity;

import java.util.Collections;
import java.util.Map;

import com.kms.katalon.entity.integration.IntegratedEntity;

public class IntegrationImpl implements com.katalon.platform.api.model.Integration {

    private final IntegratedEntity source;

    public IntegrationImpl(IntegratedEntity source) {
        this.source = source;
    }

    @Override
    public String getName() {
        return source.getProductName();
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(source.getProperties());
    }
}
