package com.kms.katalon.composer.webservice.importing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RestResourceImportNode extends RestImportNode {

    protected List<RestParameterImportResult> parameters = new ArrayList<>();

    public RestParameterImportResult addNewParameter(String name) {
        RestParameterImportResult parameter = new RestParameterImportResult();
        parameter.setName(name);
        parameters.add(parameter);
        return parameter;
    }

    public boolean hasParameter(String name) {
        return getParameterIndex(name) != -1;
    }

    public RestParameterImportResult getParameter(String name) {
        int idx = getParameterIndex(name);
        if (idx != -1) {
            return parameters.get(idx);
        } else {
            return null;
        }
    }

    public List<RestParameterImportResult> getQueryParameters() {
        return getParametersWithStyle(RestParameterImportResult.Style.QUERY);
    }

    public List<RestParameterImportResult> getHeaderParameters() {
        return getParametersWithStyle(RestParameterImportResult.Style.HEADER);
    }

    public List<RestParameterImportResult> getTemplateParameters() {
        return getParametersWithStyle(RestParameterImportResult.Style.TEMPLATE);
    }

    public List<RestParameterImportResult> getParametersWithStyle(RestParameterImportResult.Style style) {
        List<RestParameterImportResult> parameterList = parameters.stream().filter(p -> p.getStyle() == style).collect(Collectors.toList());
        return Collections.unmodifiableList(parameterList);
    }

    public List<RestParameterImportResult> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    private int getParameterIndex(String name) {
        for (int idx = 0; idx < parameters.size(); idx++) {
            if (name.equals(parameters.get(idx).getName())) {
                return idx;
            }
        }
        return -1;
    }
}