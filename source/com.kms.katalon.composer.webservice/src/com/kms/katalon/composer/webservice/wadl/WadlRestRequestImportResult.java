package com.kms.katalon.composer.webservice.wadl;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.composer.webservice.importing.model.RestMethodImportResult;
import com.kms.katalon.composer.webservice.importing.model.RestParameterImportResult;
import com.kms.katalon.composer.webservice.importing.model.RestRequestImportResult;
import com.kms.katalon.composer.webservice.importing.model.RestServiceImportResult;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;
import com.kms.katalon.entity.webservice.HttpBodyContent;
import com.kms.katalon.entity.webservice.ParameterizedBodyContent;

public class WadlRestRequestImportResult extends RestRequestImportResult {
    
    private static final String MULTIPART_FORM_DATA_CONTENT_TYPE = "multipart/form-data";

    public WadlRestRequestImportResult(RestMethodImportResult methodResult) {
        super(methodResult);
    }

    protected String getBaseUrl() {
        StringBuilder urlBuilder = new StringBuilder();

        String serviceEndpoint = getServiceEndpoint();
        if (StringUtils.isNotBlank(serviceEndpoint)) {
            urlBuilder.append(serviceEndpoint);
            if (serviceEndpoint.endsWith("/")) {
                urlBuilder.deleteCharAt(serviceEndpoint.length() - 1);
            }
        }

        String serviceBasePath = getServiceBasePath();
        if (StringUtils.isNotBlank(serviceBasePath)) {
            urlBuilder.append(serviceBasePath);
        }

        String resourcePath = getResourcePath();
        if (StringUtils.isNotBlank(resourcePath)) {
            urlBuilder.append(resourcePath);
        }

        return urlBuilder.toString();
    }
    
    @Override
    protected List<RestParameterImportResult> getUrlQueryParameters() {
        List<RestParameterImportResult> queryParameters = getQueryParameters();
        if (!queryParameters.isEmpty() && !isBodySupported()) {
            return queryParameters;
        } else {
            return Collections.emptyList();
        }
    }

    private String getServiceEndpoint() {
        RestServiceImportResult serviceImportResult = getServiceImportResult();
        return serviceImportResult.getEndpoint();
    }

    @Override
    protected List<WebElementPropertyEntity> getRequestHeaders() {
        List<RestParameterImportResult> headerParameters = getHeaderParameters();
        return headerParameters.stream().map(p -> {
            WebElementPropertyEntity headerPropertyEntity = new WebElementPropertyEntity();
            headerPropertyEntity.setName(p.getName());
            headerPropertyEntity.setValue(p.getValue());
            return headerPropertyEntity;
        }).collect(Collectors.toList());
    }

    protected void setBodyContent(WebServiceRequestEntity request) {
        HttpBodyContent bodyContent = null;
        if (isBodySupported()) {
            bodyContent = getFormDataBodyContent();
        }
        
        if (bodyContent != null) {
            request.setHttpBodyContent(JsonUtil.toJson(bodyContent));
            request.setHttpBodyType("form-data");
        } else {
            request.setHttpBodyContent("");
            request.setHttpBody("");
        }  
    }
    
    private HttpBodyContent getFormDataBodyContent() {
        List<FormDataBodyParameter> bodyParameters = getFormDataBodyParameters();
        if (bodyParameters != null && !bodyParameters.isEmpty()) {
            ParameterizedBodyContent<FormDataBodyParameter> httpBody = new ParameterizedBodyContent<>();
            bodyParameters.stream().forEach(p -> httpBody.addParameter(p));
            httpBody.setContentType(MULTIPART_FORM_DATA_CONTENT_TYPE);
            httpBody.setCharset(StandardCharsets.UTF_8.name());
            return httpBody;
        } else {
            return null;
        }
    }

    private List<FormDataBodyParameter> getFormDataBodyParameters() {
        List<RestParameterImportResult> queryParameters = getQueryParameters();
        return queryParameters.stream().map(p -> {
            FormDataBodyParameter formDataParameter = new FormDataBodyParameter();
            formDataParameter.setName(p.getName());
            formDataParameter.setValue(p.getValue());
            return formDataParameter;
        }).collect(Collectors.toList());
    }
    
    private boolean isBodySupported() {
        final List<String> BODY_SUPPORTED_METHODS = Arrays.asList("POST", "PUT", "DELETE", "PATCH");
        return BODY_SUPPORTED_METHODS.contains(httpMethod);
    }
}
