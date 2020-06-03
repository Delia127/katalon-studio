package com.kms.katalon.composer.webservice.soapui;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;
import com.kms.katalon.entity.webservice.HttpBodyContent;
import com.kms.katalon.entity.webservice.TextBodyContent;

public class SoapUIRequestImportResult extends SoapUIResourceElementImportResult {
    
    private SoapUIResourceImportResult resourceImportResult;
    
    private SoapUIMethodImportResult methodImportResult;
    
    private String id;
    
    private String name;
    
    private Map<String, String> headers = new LinkedHashMap<>();
    
    private String mediaType;
    
    private String bodyContent;
    
    private boolean postQueryString;

    public SoapUIRequestImportResult(SoapUIMethodImportResult methodResult, String id, String name) {
        this.methodImportResult = methodResult;
        this.resourceImportResult = methodResult.getResourceImportResult();
        this.id = id;
        this.name = name;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType.trim();
    }
    
    public void setRequestBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }
    
    public void setPostQueryString(boolean postQueryString) {
        this.postQueryString = postQueryString;
    }
    
    public void addAttachment(String name, String contentType, String contentId, String url) {
        
    }
    
    public WebServiceRequestEntity getRequest() {
        WebServiceRequestEntity request = buildRequest();
        return request;
    }

    private WebServiceRequestEntity buildRequest() {
        WebServiceRequestEntity request = new WebServiceRequestEntity();
        request.setName(methodImportResult.getName());

        SoapUIResourceImportResult resourceImportResult = methodImportResult.getResourceImportResult();
        FolderEntity parentFolder = resourceImportResult.getFolderHierarchy().getFolderByPath(resourceImportResult.getPath());
        request.setParentFolder(parentFolder);

        String url = buildRequestUrl();
        request.setRestUrl(url);

        List<WebElementPropertyEntity> headers = collectRequestHeaders();
        request.setHttpHeaderProperties(headers);
        
        List<VariableEntity> variables = collectRequestVariables();
        request.setVariables(variables);
        
        request.setHttpBodyType(mediaType);
        
        return request;
    }
    
    private HttpBodyContent getHttpBodyContent() {
        switch (mediaType) {
        case "application/json":
        case "application/xml":
        case "text/xml":
            TextBodyContent httpBody = new TextBodyContent();
            httpBody.setContentType(mediaType);
            httpBody.setText(bodyContent);
            return httpBody;
        case "multipart/form-data":
            ParameterizedBodyContent<FormDataBodyParameter>
            
        }
    }
    
    private List<FormDataBodyParameter> collectFormDataBodyParameters() {
        if (postQueryString) {
            
        }
    }

    private String buildRequestUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(resourceImportResult.getEndpoint());
        urlBuilder.append(resourceImportResult.getPath());

        for (Map.Entry<String, String> matrixParameter : matrixParameters.entrySet()) {
            urlBuilder.append(String.format(";%s=%s", matrixParameter.getKey(), matrixParameter.getValue()));
        }

        if (!queryParameters.isEmpty()) {
            urlBuilder.append("?");
        }
        List<String> queryParamPairs = queryParameters.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        String queryParamString = StringUtils.join(queryParamPairs, "&");
        urlBuilder.append(queryParamString);   

        return urlBuilder.toString();
    }
    
    private List<WebElementPropertyEntity> collectRequestHeaders() {
        Map<String, String> combinedHeaders = new HashMap<>();
        for (Map.Entry<String, String> headerParameter : headerParameters.entrySet()) {
            combinedHeaders.put(headerParameter.getKey(), headerParameter.getValue());
        }
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            combinedHeaders.put(headerEntry.getKey(), headerEntry.getValue());
        }
        return combinedHeaders.entrySet().stream()
            .map(e -> {
                WebElementPropertyEntity headerPropertyEntity = new WebElementPropertyEntity();
                headerPropertyEntity.setName(e.getKey());
                headerPropertyEntity.setValue(e.getValue());
                return headerPropertyEntity;
            }).collect(Collectors.toList());
    }
    
    private List<VariableEntity> collectRequestVariables() {
        List<VariableEntity> variables = templateParameters.entrySet().stream().map(p -> {
            VariableEntity variable = new VariableEntity();
            variable.setName(p.getKey());
            variable.setDefaultValue(p.getValue());
            return variable;
        }).collect(Collectors.toList());
        return variables;
    }
    
    private class Attachment {
        
        private String name;
        
        private String contentType;
        
        private String contentId;
        
        private String url;
        
        private String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentId() {
            return contentId;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
