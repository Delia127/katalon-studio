package com.kms.katalon.platform.internal.entity.testobject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.platform.internal.entity.VariableEntityImpl;

public class SoapRequestEntityImpl implements com.katalon.platform.api.model.testobject.SoapRequestEntity {

    private WebServiceRequestEntity source;

    public SoapRequestEntityImpl(WebServiceRequestEntity source) {
        this.source = source;
    }

    @Override
    public String getSoapBodyContent() {
        return source.getHttpBodyContent();
    }

    @Override
    public String getSoapServiceFunction() {
        return source.getSoapServiceFunction();
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
        return source.getSoapRequestMethod();
    }

    @Override
    public List<com.katalon.platform.api.model.testobject.WebServiceProperty> getRequestParameters() {
        if (source.getSoapParameters() == null) {
            return Collections.emptyList();
        }
        return source.getSoapParameters()
                .stream()
                .map(header -> new WebServicePropertyImpl(header))
                .collect(Collectors.toList());
    }

    @Override
    public String getRequestUrl() {
        return source.getWsdlAddress();
    }

    @Override
    public String getServiceType() {
        return source.getServiceType();
    }

    @Override
    public List<com.katalon.platform.api.model.VariableEntity> getVariables() {
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
    public String getDescription() {
        return source.getDescription();
    }

}
