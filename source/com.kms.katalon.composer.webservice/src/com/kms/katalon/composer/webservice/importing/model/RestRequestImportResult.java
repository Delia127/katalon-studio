package com.kms.katalon.composer.webservice.importing.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public abstract class RestRequestImportResult extends RestResourceImportNode {
    
    protected RestMethodImportResult methodImportResult;
    
    private String name;

    protected String mediaType;

    protected String bodyContent;
    
    protected String httpMethod;
    
    protected boolean followRedirects = true;
    
    private RestServiceImportResult serviceImportResult;
    
    public RestRequestImportResult(RestMethodImportResult methodResult) {
        this.methodImportResult = methodResult;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType.trim();
    }

    public void setRequestBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    public void setParameter(String name, String value) {
        RestParameterImportResult pr;
        if (hasParameter(name)) {
            pr = getParameter(name);
        } else {
            pr = addNewParameter(name);
        }
        pr.setValue(value);
    }

    public FileEntity getFileEntity() {
        try {
            WebServiceRequestEntity request = buildRequest();
            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WebServiceRequestEntity buildRequest() throws Exception {
        FolderEntity parentFolder = (FolderEntity) methodImportResult.getFileEntity();
        WebServiceRequestEntity request = ObjectRepositoryController.getInstance()
                .newWSTestObjectWithoutSave(parentFolder, name);

        String url = buildRequestUrl();
        request.setRestUrl(url);

        List<WebElementPropertyEntity> headers = getRequestHeaders();
        request.setHttpHeaderProperties(headers);

        setBodyContent(request);

        List<VariableEntity> variables = getRequestVariables();
        request.setVariables(variables);

        request.setRestRequestMethod(httpMethod);

        request.setFollowRedirects(followRedirects);

        request.setServiceType(WebServiceRequestEntity.RESTFUL);

        return request;
    }
    
    private String buildRequestUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        
        String basePath = getBaseUrl();
        if (StringUtils.isNotBlank(basePath)) {
            urlBuilder.append(basePath);
        }
        
        List<RestParameterImportResult> queryParameters = getUrlQueryParameters();
        if (!queryParameters.isEmpty()) {
            urlBuilder.append("?");
            String queryString = queryParameters.stream().map(p -> {
                String parameterName = p.getName();
                String parameterValue = StringUtils.defaultIfBlank(p.getValue(), "");
                return String.format("%s=%s", parameterName, parameterValue);
            }).collect(Collectors.joining("&"));
            urlBuilder.append(queryString);
        }

        return urlBuilder.toString();
    }
    
    protected abstract List<RestParameterImportResult> getUrlQueryParameters();

    protected abstract String getBaseUrl();
        
    protected abstract void setBodyContent(WebServiceRequestEntity request);
    
    protected abstract List<WebElementPropertyEntity> getRequestHeaders(); 
    
    private List<VariableEntity> getRequestVariables() {
        return getTemplateParameters().stream().map(p -> {
            VariableEntity variable = new VariableEntity();
            variable.setName(p.getName());
            variable.setDefaultValue(StringUtils.defaultIfBlank(p.getValue(), ""));
            return variable;
        }).collect(Collectors.toList());
    }
    
    protected String getServiceBasePath() {
        RestServiceImportResult serviceImportResult = getServiceImportResult();
        String basePath = serviceImportResult.getBasePath();
        if (StringUtils.isNotBlank(basePath) && !basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        return basePath;
    } 
    
    protected RestServiceImportResult getServiceImportResult() {
        if (serviceImportResult == null) {
            RestImportNode importNode = methodImportResult;
            while (!(importNode instanceof RestServiceImportResult)) {
                importNode = importNode.getParentImportNode();
            }
            serviceImportResult = (RestServiceImportResult) importNode;
        }
        return serviceImportResult;
    }
    
    protected String getResourcePath() {
        String resourcePath = methodImportResult.getResourceImportResult().getPath();
        if (StringUtils.isNotBlank(resourcePath)) {
            if (!resourcePath.startsWith("/")) {
                resourcePath = "/" + resourcePath;
            }
            resourcePath = StringUtils.replace(resourcePath, "{", "${");
        }
        return resourcePath;
    }
    
    public RestImportNode getParentImportNode() {
        return methodImportResult;
    }

    public List<RestImportNode> getChildImportNodes() {
        return Collections.emptyList();
    }
}
