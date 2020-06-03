package com.kms.katalon.composer.webservice.soapui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SoapUIResourceElementImportResult extends SoapUIBaseImportResult {

    protected List<SoapUIRestParameter> parameters = new ArrayList<>();

    public SoapUIRestParameter newParameter(String name, String value, SoapUIRestParameter.Style style) {
        SoapUIRestParameter parameter = new SoapUIRestParameter();
        parameter.setName(name);
        parameter.setValue(value);
        parameter.setStyle(style);
        parameters.add(parameter);
        return parameter;
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
    
    protected List<SoapUIRestParameter> getMatrixParameters() {
        return getParametersWithStyle(SoapUIRestParameter.Style.MATRIX);
    }

    protected List<SoapUIRestParameter> getParametersWithStyle(SoapUIRestParameter.Style style) {
        return parameters.stream().filter(p -> p.getStyle() == style).collect(Collectors.toList());
    }
}