package com.kms.katalon.platform.internal.entity.testobject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.katalon.platform.api.model.VariableEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.platform.internal.entity.VariableEntityImpl;

public class RestRequestEntityImpl implements com.katalon.platform.api.model.testobject.RestRequestEntity {

    private final WebServiceRequestEntity source;

    public RestRequestEntityImpl(WebServiceRequestEntity source) {
        this.source = source;
    }

    @Override
    public List<com.katalon.platform.api.model.testobject.WebServiceProperty> getHttpHeaders() {
        if (source.getHttpHeaderProperties() == null) {
            return Collections.emptyList();
        }
        return source.getHttpHeaderProperties()
                .stream()
                .map(header -> new WebServicePropertyImpl(header))
                .collect(Collectors.toList());
    }

    @Override
    public String getRequestMethod() {
        return source.getRestRequestMethod();
    }

    @Override
    public List<com.katalon.platform.api.model.testobject.WebServiceProperty> getRequestParameters() {
        if (source.getRestParameters() == null) {
            return Collections.emptyList();
        }
        return source.getRestParameters()
                .stream()
                .map(param -> new WebServicePropertyImpl(param))
                .collect(Collectors.toList());
    }

    @Override
    public String getRequestUrl() {
        return source.getRestUrl();
    }

    @Override
    public String getServiceType() {
        return source.getServiceType();
    }

    @Override
    public List<VariableEntity> getVariables() {
        if (source.getVariables() == null) {
            return Collections.emptyList();
        }
        return source.getVariables()
                .stream()
                .map(variable -> new VariableEntityImpl(variable))
                .collect(Collectors.toList());
    }

    @Override
    public String getVerificationScript() {
        return source.getVerificationScript();
    }

    @Override
    public String getFileLocation() {
        return source.getId();
    }

    @Override
    public String getFolderLocation() {
        return source.getParentFolder().getLocation();
    }

    @Override
    public String getId() {
        return source.getIdForDisplay();
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getHttpBodyContent() {
        return source.getHttpBodyContent();
    }

    @Override
    public String getHttpBodyType() {
        return source.getHttpBodyType();
    }

    @Override
    public String getDescription() {
        return source.getDescription();
    }

}
