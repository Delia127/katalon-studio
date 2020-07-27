package com.kms.katalon.composer.webservice.openapi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class OpenApiRestResourceImportNode extends OpenApiImportNode {

    protected List<OpenApiRestParameter> parameters = new ArrayList<>();

    public OpenApiRestParameter addParameter(String name, String value, String description,
            OpenApiRestParameter.Style style) {
        OpenApiRestParameter parameter = new OpenApiRestParameter();
        parameter.setName(name);
        parameter.setValue(value);
        parameter.setStyle(style);
        parameter.setDescription(description);
        parameters.add(parameter);
        return parameter;
    }

    protected List<OpenApiRestParameter> getQueryParameters() {
        return getParametersWithStyle(OpenApiRestParameter.Style.QUERY);
    }

    protected List<OpenApiRestParameter> getHeaderParameters() {
        return getParametersWithStyle(OpenApiRestParameter.Style.HEADER);
    }

    protected List<OpenApiRestParameter> getTemplateParameters() {
        return getParametersWithStyle(OpenApiRestParameter.Style.TEMPLATE);
    }

    protected List<OpenApiRestParameter> getParametersWithStyle(OpenApiRestParameter.Style style) {
        return parameters.stream().filter(p -> p.getStyle() == style).collect(Collectors.toList());
    }

    protected List<OpenApiRestParameter> getParameters() {
        return parameters;
    }
}