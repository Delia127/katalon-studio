package com.kms.katalon.composer.webservice.soapui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SoapUIRestResourceImportNode extends SoapUIImportNode {

    protected List<SoapUIRestParameter> parameters = new ArrayList<>();

    public SoapUIRestParameter addParameter(String name, String value, SoapUIRestParameter.Style style) {
        int idx = getParameterIndex(name);
        if (idx != -1) {
            SoapUIRestParameter parameter = parameters.get(idx);
            parameter.setValue(value);
            parameter.setStyle(style);
            return parameter;
        } else {
            SoapUIRestParameter parameter = new SoapUIRestParameter();
            parameter.setName(name);
            parameter.setValue(value);
            parameter.setStyle(style);
            parameters.add(parameter);
            return parameter;
        }
    }

    protected List<SoapUIRestParameter> getQueryParameters() {
        return getParametersWithStyle(SoapUIRestParameter.Style.QUERY);
    }
    
    protected List<SoapUIRestParameter> getHeaderParameters() {
        return getParametersWithStyle(SoapUIRestParameter.Style.HEADER);
    }
    
    protected List<SoapUIRestParameter> getTemplateParameters() {
        return getParametersWithStyle(SoapUIRestParameter.Style.TEMPLATE);
    }
    
    protected List<SoapUIRestParameter> getParametersWithStyle(SoapUIRestParameter.Style style) {
        return parameters.stream().filter(p -> p.getStyle() == style).collect(Collectors.toList());
    }
    
    protected List<SoapUIRestParameter> getParameters() {
        return parameters;
    }
    
    protected int getParameterIndex(String name) {
        for (int idx = 0; idx < parameters.size(); idx++) {
            if (name.equals(parameters.get(idx).getName())) {
                return idx;
            }
        }
        return -1;
    }
}