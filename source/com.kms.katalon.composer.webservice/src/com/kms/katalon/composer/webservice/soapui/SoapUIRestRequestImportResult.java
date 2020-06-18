package com.kms.katalon.composer.webservice.soapui;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;
import com.kms.katalon.entity.webservice.HttpBodyContent;
import com.kms.katalon.entity.webservice.ParameterizedBodyContent;
import com.kms.katalon.entity.webservice.TextBodyContent;
import com.kms.katalon.entity.webservice.UrlEncodedBodyParameter;

public class SoapUIRestRequestImportResult extends SoapUIRestResourceImportNode {

    private static final String FILE_QUERY_PARAM_PREFIX = "file:";
    
    public static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    
    public static final String APPLICATION_XML_CONTENT_TYPE = "application/xml";
    
    public static final String TEXT_XML_CONTENT_TYPE = "text/xml";

    private static final String MULTIPART_FORM_DATA_CONTENT_TYPE = "multipart/form-data";

    private static final String FORM_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private SoapUIProjectImportResult projectImportResult;
    
    private SoapUIRestServiceImportResult serviceImportResult;
    
    private SoapUIRestMethodImportResult methodImportResult;

    private String name;

    private String endpoint;

    private Map<String, String> headers = new LinkedHashMap<>();

    private String mediaType;

    private String bodyContent;

    private boolean postQueryString;
    
    private String httpMethod;
    
    private String encoding;
    
    private boolean followRedirects = true;

    private List<SoapUIAttachment> attachments = new ArrayList<>();
    
    private SoapUIBasicCredential basicCredential;
    
    private String oAuth1ProfileName;
    
    private String oAuth2ProfileName;
    
    public SoapUIRestRequestImportResult(SoapUIRestMethodImportResult methodResult, String name) {
        this.methodImportResult = methodResult;
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

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    protected void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    protected void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setParameterOrder(String[] parameterOrder) {
        List<String> parameterOrderList = Arrays.asList(parameterOrder);
        parameters.sort((p1, p2) -> 
            parameterOrderList.indexOf(p1.getName()) - parameterOrderList.indexOf(p2.getName()));
    }
    
    public void setParameter(String name, String value) {
        int idx = getParameterIndex(name);
        if (idx != -1) {
            SoapUIRestParameter parameter = parameters.get(idx);
            parameter.setValue(value);
        }
    }

    public SoapUIAttachment newAttachment(String name, String contentType, String contentId, String url) {
        SoapUIAttachment attachment = new SoapUIAttachment();
        attachment.setName(name);
        attachment.setContentType(contentType);
        attachment.setContentId(contentId);
        attachment.setUrl(url);
        attachments.add(attachment);
        return attachment;
    }
    
    @Override
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

        List<WebElementPropertyEntity> headers = collectRequestHeaders();
        request.setHttpHeaderProperties(headers);

        if (isBodySupported()) {
            request.setHttpBodyType(getBodyType());
            HttpBodyContent httpBodyContent = getHttpBodyContent();
            request.setHttpBodyContent(JsonUtil.toJson(httpBodyContent));
        }

        List<VariableEntity> variables = collectRequestVariables();
        request.setVariables(variables);

        request.setRestRequestMethod(httpMethod);

        request.setFollowRedirects(followRedirects);

        request.setServiceType(WebServiceRequestEntity.RESTFUL);

        return request;
    }
    
    private String getBodyType() {
        switch (mediaType) {
        case APPLICATION_JSON_CONTENT_TYPE:
        case APPLICATION_XML_CONTENT_TYPE:
        case TEXT_XML_CONTENT_TYPE:
            return "text";
        case FORM_URLENCODED_CONTENT_TYPE:
            return "x-www-form-urlencoded";
        case MULTIPART_FORM_DATA_CONTENT_TYPE:
            return "form-data";
        default:
            return "text";
        }
    }

    private HttpBodyContent getHttpBodyContent() {
        switch (mediaType) {
        case APPLICATION_JSON_CONTENT_TYPE:
        case APPLICATION_XML_CONTENT_TYPE:
        case TEXT_XML_CONTENT_TYPE:
            return getTextBodyContent();
        case FORM_URLENCODED_CONTENT_TYPE:
            return getUrlEncodedBodyContent();
        case MULTIPART_FORM_DATA_CONTENT_TYPE:
            return getFormDataBodyContent();
        default:
            return getTextBodyContent();
        }
    }

    private HttpBodyContent getTextBodyContent() {
        TextBodyContent httpBody = new TextBodyContent();
        httpBody.setContentType(mediaType);
        httpBody.setText(bodyContent);
        return httpBody;
    }

    private HttpBodyContent getUrlEncodedBodyContent() {
        ParameterizedBodyContent<UrlEncodedBodyParameter> httpBody = new ParameterizedBodyContent<>();
        List<UrlEncodedBodyParameter> bodyParameters = collectUrlEncodedBodyParameters();
        bodyParameters.stream().forEach(p -> httpBody.addParameter(p));
        httpBody.setContentType(FORM_URLENCODED_CONTENT_TYPE);
        httpBody.setCharset(StandardCharsets.UTF_8.name());
        return httpBody;
    }

    private List<UrlEncodedBodyParameter> collectUrlEncodedBodyParameters() {
        List<UrlEncodedBodyParameter> urlEncodedBodyParameters = new ArrayList<>();
        if (postQueryString) {
            urlEncodedBodyParameters.addAll(collectUrlEncodedBodyParametersIfPostQueryString());
        }
        return urlEncodedBodyParameters;
    }

    private List<UrlEncodedBodyParameter> collectUrlEncodedBodyParametersIfPostQueryString() {
        List<SoapUIRestParameter> queryParameters = getQueryParameters();
        return queryParameters.stream().map(p -> {
            UrlEncodedBodyParameter urlEncodedParameter = new UrlEncodedBodyParameter();
            urlEncodedParameter.setName(p.getName());
            urlEncodedParameter.setValue(p.getValue());
            return urlEncodedParameter;
        }).collect(Collectors.toList());
    }

    private HttpBodyContent getFormDataBodyContent() {
        ParameterizedBodyContent<FormDataBodyParameter> httpBody = new ParameterizedBodyContent<>();
        List<FormDataBodyParameter> bodyParameters = collectFormDataBodyParameters();
        bodyParameters.stream().forEach(p -> httpBody.addParameter(p));
        httpBody.setContentType(MULTIPART_FORM_DATA_CONTENT_TYPE);
        httpBody.setCharset(StandardCharsets.UTF_8.name());
        return httpBody;
    }

    private List<FormDataBodyParameter> collectFormDataBodyParameters() {
        List<FormDataBodyParameter> formDataBodyParameters = new ArrayList<>();
        if (StringUtils.isNotBlank(bodyContent)) {
            FormDataBodyParameter bodyContentParam = new FormDataBodyParameter();
            bodyContentParam.setName("");
            bodyContentParam.setValue(bodyContent);
            bodyContentParam.setType(FormDataBodyParameter.PARAM_TYPE_TEXT);
            formDataBodyParameters.add(bodyContentParam);
        }

        if (postQueryString) {
            formDataBodyParameters.addAll(collectFormDataBodyParametersIfPostQueryString());
        }
        return formDataBodyParameters;
    }

    private List<FormDataBodyParameter> collectFormDataBodyParametersIfPostQueryString() {
        List<SoapUIRestParameter> queryParameters = getQueryParameters();
        return queryParameters.stream().map(p -> {
            FormDataBodyParameter formDataParameter = new FormDataBodyParameter();
            formDataParameter.setName(p.getName());
            String value = p.getValue();
            if (value.startsWith(FILE_QUERY_PARAM_PREFIX)) {
                formDataParameter.setType(FormDataBodyParameter.PARAM_TYPE_FILE);
                String attachmentName = value.substring(FILE_QUERY_PARAM_PREFIX.length());
                SoapUIAttachment attachment = getAttachmentWithName(attachmentName);
                if (attachment != null) {
                    formDataParameter.setValue(attachment.getUrl());
                    formDataParameter.setContentType(attachment.getContentType());
                } else {
                    formDataParameter.setValue("");
                }
            } else {
                formDataParameter.setType(FormDataBodyParameter.PARAM_TYPE_TEXT);
                formDataParameter.setValue(p.getValue());
            }
            return formDataParameter;
        }).collect(Collectors.toList());
    }

    private SoapUIAttachment getAttachmentWithName(String name) {
        return attachments.stream().filter(a -> name.equals(a.getName())).findAny().orElse(null);
    }

    private String buildRequestUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        
        if (StringUtils.isNotBlank(endpoint)) {
            urlBuilder.append(endpoint);
            if (endpoint.endsWith("/")) {
                urlBuilder.deleteCharAt(endpoint.length() - 1);
            }
        }
        
        String basePath = getBasePath();
        if (StringUtils.isNotBlank(basePath)) {
            urlBuilder.append(basePath);
        }
        
        String resourcePath = getResourcePath();
        if (StringUtils.isNotBlank(resourcePath)) {
            urlBuilder.append(resourcePath);
        }        

        List<SoapUIRestParameter> queryParameters = getQueryParameters();
        if (!queryParameters.isEmpty() && !postQueryString) {
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
    
    private String getBasePath() {
        SoapUIRestServiceImportResult serviceImportResult = getServiceImportResult();
        String basePath = serviceImportResult.getBasePath();
        if (StringUtils.isNotBlank(basePath) && !basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        return basePath;
    }
    
    private String getResourcePath() {
        String resourcePath = methodImportResult.getResourceImportResult().getPath();
        if (StringUtils.isNotBlank(resourcePath)) {
            if (!resourcePath.startsWith("/")) {
                resourcePath = "/" + resourcePath;
            }
            resourcePath = StringUtils.replace(resourcePath, "{", "${");
        }
        return resourcePath;
    }
   
    private SoapUIRestServiceImportResult getServiceImportResult() {
        if (serviceImportResult == null) {
            SoapUIImportNode importNode = methodImportResult;
            while (!(importNode instanceof SoapUIRestServiceImportResult)) {
                importNode = importNode.getParentImportNode();
            }
            serviceImportResult = (SoapUIRestServiceImportResult) importNode;
        }
        return serviceImportResult;
    }
    
    private SoapUIProjectImportResult getProjectImportResult() {
        if (projectImportResult == null) {
            SoapUIImportNode importNode = methodImportResult;
            while (!(importNode instanceof SoapUIProjectImportResult)) {
                importNode = importNode.getParentImportNode();
            }
            projectImportResult = (SoapUIProjectImportResult) importNode;
        }
        return projectImportResult;
    }

    private List<WebElementPropertyEntity> collectRequestHeaders() {
        Map<String, String> combinedHeaders = new LinkedHashMap<>();
        for (SoapUIRestParameter headerParameter : getHeaderParameters()) {
            combinedHeaders.put(headerParameter.getName(), headerParameter.getValue());
        }
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            combinedHeaders.put(headerEntry.getKey(), headerEntry.getValue());
        }
        if (isBodySupported() && !combinedHeaders.containsKey("Content-Type")) {
            String contentTypeHeaderValue = getDefaultContentTypeHeaderValue();
            if (StringUtils.isNotBlank(contentTypeHeaderValue)) {
                combinedHeaders.put("Content-Type", contentTypeHeaderValue);
            }
        }
        Map<String, String> authHeaders = getAuthHeaders();
        if (authHeaders != null) {
            combinedHeaders.putAll(authHeaders);
        }
        return combinedHeaders.entrySet().stream().map(e -> {
            WebElementPropertyEntity headerPropertyEntity = new WebElementPropertyEntity();
            headerPropertyEntity.setName(e.getKey());
            headerPropertyEntity.setValue(e.getValue());
            return headerPropertyEntity;
        }).collect(Collectors.toList());
    }
    
    private Map<String, String> getAuthHeaders() {
        if (basicCredential != null) {
            return basicCredential.getAuthHeaders();
        }
        SoapUIOAuth1Credential oAuth1Credential = getOAuth1Credential();
        if (oAuth1Credential != null) {
            return oAuth1Credential.getAuthHeaders();
        }
        SoapUIOAuth2Credential oAuth2Credential = getOAuth2Credential();
        if (oAuth2Credential != null) {
            return oAuth2Credential.getAuthHeaders();
        }
        return null;
    }
        
    private boolean isBodySupported() {
        final List<String> BODY_SUPPORTED_METHODS = Arrays.asList("POST", "PUT", "DELETE", "PATCH", "PROPFIND", "LOCK");
        return BODY_SUPPORTED_METHODS.contains(httpMethod);
    }
    
    private String getDefaultContentTypeHeaderValue() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(mediaType)) {
            builder.append(mediaType);
        }
        if (StringUtils.isNotBlank(encoding)) {
            builder.append(";charset=");
            builder.append(encoding);
        }
        return builder.toString();
    }
    
    private SoapUIOAuth1Credential getOAuth1Credential() {
        if (StringUtils.isBlank(oAuth1ProfileName)) {
            return null;
        }
        SoapUIProjectImportResult projectImportResult = getProjectImportResult();
        return projectImportResult.getOAuth1CredentialByProfile(oAuth1ProfileName);
    }
    
    private SoapUIOAuth2Credential getOAuth2Credential() {
        if (StringUtils.isBlank(oAuth2ProfileName)) {
            return null;
        }
        SoapUIProjectImportResult projectImportResult = getProjectImportResult();
        return projectImportResult.getOAuth2CredentialByProfile(oAuth2ProfileName);
    }

    private List<VariableEntity> collectRequestVariables() {
        return getTemplateParameters().stream().map(p -> {
            VariableEntity variable = new VariableEntity();
            variable.setName(p.getName());
            variable.setDefaultValue(StringUtils.defaultIfBlank(p.getValue(), ""));
            return variable;
        }).collect(Collectors.toList());
    }
    
    public void setBasicCredential(String username, String password) {
        if (basicCredential == null) {
            basicCredential = new SoapUIBasicCredential();
        }
        basicCredential.setUsername(username);
        basicCredential.setPassword(password);
    }

    protected void setOAuth1Profile(String oAuth1ProfileName) {
        this.oAuth1ProfileName = oAuth1ProfileName;
    }

    protected void setOAuth2Profile(String oAuth2ProfileName) {
        this.oAuth2ProfileName = oAuth2ProfileName;
    }
}
