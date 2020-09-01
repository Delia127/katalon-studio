package com.kms.katalon.composer.webservice.soapui;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.webservice.importing.model.RestImportNode;
import com.kms.katalon.composer.webservice.importing.model.RestMethodImportResult;
import com.kms.katalon.composer.webservice.importing.model.RestParameterImportResult;
import com.kms.katalon.composer.webservice.importing.model.RestRequestImportResult;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.webservice.FormDataBodyParameter;
import com.kms.katalon.entity.webservice.HttpBodyContent;
import com.kms.katalon.entity.webservice.ParameterizedBodyContent;
import com.kms.katalon.entity.webservice.TextBodyContent;
import com.kms.katalon.entity.webservice.UrlEncodedBodyParameter;

public class SoapUIRestRequestImportResult extends RestRequestImportResult {
    
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    
    private static final String APPLICATION_XML_CONTENT_TYPE = "application/xml";
    
    private static final String TEXT_XML_CONTENT_TYPE = "text/xml";

    private static final String MULTIPART_FORM_DATA_CONTENT_TYPE = "multipart/form-data";

    private static final String FORM_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private static final String FILE_QUERY_PARAM_PREFIX = "file:";
    
    private SoapUIProjectImportResult projectImportResult;
        
    private String endpoint;

    private boolean postQueryString;    
    
    private String encoding;

    private List<SoapUIAttachment> attachments = new ArrayList<>();
    
    private Map<String, String> headers = new HashMap<>();
    
    private SoapUIBasicCredential basicCredential;
    
    private String oAuth1ProfileName;
    
    private String oAuth2ProfileName;
    
    public SoapUIRestRequestImportResult(RestMethodImportResult methodResult) {
        super(methodResult);
    }
    
    public void setHeader(String name, String value) {
        headers.put(name, value);
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

    public void setParameterOrder(String[] parameterOrder) {
        List<String> parameterOrderList = Arrays.asList(parameterOrder);
        parameters.sort((p1, p2) -> 
            parameterOrderList.indexOf(p1.getName()) - parameterOrderList.indexOf(p2.getName()));
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
    protected String getBaseUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        
        if (StringUtils.isNotBlank(endpoint)) {
            urlBuilder.append(endpoint);
            if (endpoint.endsWith("/")) {
                urlBuilder.deleteCharAt(endpoint.length() - 1);
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
    
    private SoapUIProjectImportResult getProjectImportResult() {
        if (projectImportResult == null) {
            RestImportNode importNode = methodImportResult;
            while (!(importNode instanceof SoapUIProjectImportResult)) {
                importNode = importNode.getParentImportNode();
            }
            projectImportResult = (SoapUIProjectImportResult) importNode;
        }
        return projectImportResult;
    }

    protected List<WebElementPropertyEntity> getRequestHeaders() {
        Map<String, String> combinedHeaders = new LinkedHashMap<>();
        for (RestParameterImportResult headerParameter : getHeaderParameters()) {
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
    
    private SoapUIOAuth1Credential getOAuth1Credential() {
        if (StringUtils.isBlank(oAuth1ProfileName)) {
            return null;
        }
        SoapUIProjectImportResult projectImportResult = getProjectImportResult();
        return projectImportResult.getOAuth1CredentialByProfile(oAuth1ProfileName);
    }
    
    protected void setOAuth1Profile(String oAuth1ProfileName) {
        this.oAuth1ProfileName = oAuth1ProfileName;
    }
    
    private SoapUIOAuth2Credential getOAuth2Credential() {
        if (StringUtils.isBlank(oAuth2ProfileName)) {
            return null;
        }
        SoapUIProjectImportResult projectImportResult = getProjectImportResult();
        return projectImportResult.getOAuth2CredentialByProfile(oAuth2ProfileName);
    }
    
    protected void setOAuth2Profile(String oAuth2ProfileName) {
        this.oAuth2ProfileName = oAuth2ProfileName;
    }
        
    @Override
    protected void setBodyContent(WebServiceRequestEntity request) {
        if (isBodySupported()) {
            request.setHttpBodyType(getBodyType());
            HttpBodyContent httpBodyContent = getHttpBodyContent();
            request.setHttpBodyContent(JsonUtil.toJson(httpBodyContent));
        }
    }
    
    private boolean isBodySupported() {
        final List<String> BODY_SUPPORTED_METHODS = Arrays.asList("POST", "PUT", "DELETE", "PATCH", "PROPFIND", "LOCK");
        return BODY_SUPPORTED_METHODS.contains(httpMethod);
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
            return null;
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
        List<UrlEncodedBodyParameter> bodyParameters = getUrlEncodedBodyParameters();
        bodyParameters.stream().forEach(p -> httpBody.addParameter(p));
        httpBody.setContentType(FORM_URLENCODED_CONTENT_TYPE);
        httpBody.setCharset(StandardCharsets.UTF_8.name());
        return httpBody;
    }
    
    private List<UrlEncodedBodyParameter> getUrlEncodedBodyParameters() {
        if (postQueryString) {
            List<RestParameterImportResult> queryParameters = getQueryParameters();
            return queryParameters.stream().map(p -> {
                UrlEncodedBodyParameter urlEncodedParameter = new UrlEncodedBodyParameter();
                urlEncodedParameter.setName(p.getName());
                urlEncodedParameter.setValue(p.getValue());
                return urlEncodedParameter;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    
    private HttpBodyContent getFormDataBodyContent() {
        ParameterizedBodyContent<FormDataBodyParameter> httpBody = new ParameterizedBodyContent<>();
        List<FormDataBodyParameter> bodyParameters = getFormDataBodyParameters();
        bodyParameters.stream().forEach(p -> httpBody.addParameter(p));
        httpBody.setContentType(MULTIPART_FORM_DATA_CONTENT_TYPE);
        httpBody.setCharset(StandardCharsets.UTF_8.name());
        return httpBody;
    }

    private List<FormDataBodyParameter> getFormDataBodyParameters() {
        List<FormDataBodyParameter> formDataBodyParameters = new ArrayList<>();
        if (StringUtils.isNotBlank(bodyContent)) {
            FormDataBodyParameter bodyContentParam = new FormDataBodyParameter();
            bodyContentParam.setName("");
            bodyContentParam.setValue(bodyContent);
            bodyContentParam.setType(FormDataBodyParameter.PARAM_TYPE_TEXT);
            formDataBodyParameters.add(bodyContentParam);
        }

        if (postQueryString) {
            formDataBodyParameters.addAll(getFormDataBodyParametersIfPostQueryString());
        }
        return formDataBodyParameters;
    }

    private List<FormDataBodyParameter> getFormDataBodyParametersIfPostQueryString() {
        List<RestParameterImportResult> queryParameters = getQueryParameters();
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

    public void setBasicCredential(String username, String password) {
        if (basicCredential == null) {
            basicCredential = new SoapUIBasicCredential();
        }
        basicCredential.setUsername(username);
        basicCredential.setPassword(password);
    }

    public List<RestParameterImportResult> getUrlQueryParameters() {
        List<RestParameterImportResult> queryParameters = super.getQueryParameters();
        if (!queryParameters.isEmpty() && !postQueryString) {
            return queryParameters;
        } else {
            return Collections.emptyList();
        }
    }
}
