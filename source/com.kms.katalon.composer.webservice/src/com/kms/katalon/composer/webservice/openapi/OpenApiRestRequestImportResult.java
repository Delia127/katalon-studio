package com.kms.katalon.composer.webservice.openapi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

public class OpenApiRestRequestImportResult extends OpenApiRestResourceImportNode {

    static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";

    static final String APPLICATION_XML_CONTENT_TYPE = "application/xml";

    static final String TEXT_XML_CONTENT_TYPE = "text/xml";

    static final String MULTIPLE_CONTENT_TYPE = "*/*";

    static final String MULTIPART_FORM_DATA_CONTENT_TYPE = "multipart/form-data";

    static final String FORM_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private OpenApiRestServiceImportResult serviceImportResult;

    private OpenApiRestResourceImportResult resourceImportResult;

    private String name;

    private Map<String, String> headers = new LinkedHashMap<>();

    private String mediaType;

    private String bodyContent;

    private String httpMethod;

    private List<UrlEncodedBodyParameter> urlEncodedBodyParameters;

    private List<FormDataBodyParameter> formDataBodyParameters;

    public OpenApiRestRequestImportResult(OpenApiRestResourceImportResult resourceResult, String name) {
        this.resourceImportResult = resourceResult;
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

    public void setUrlEncodedBodyParameters(List<UrlEncodedBodyParameter> urlEncodedBodyParameters) {
        this.urlEncodedBodyParameters = urlEncodedBodyParameters;
    }

    public void setFormDataBodyParameters(List<FormDataBodyParameter> formDataBodyParameters) {
        this.formDataBodyParameters = formDataBodyParameters;
    }

    protected void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
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
        FolderEntity parentFolder = (FolderEntity) resourceImportResult.getFileEntity();
        WebServiceRequestEntity request = ObjectRepositoryController.getInstance()
                .newWSTestObjectWithoutSave(parentFolder, name);
        String url = buildRequestUrl();
        request.setRestUrl(url);
        List<WebElementPropertyEntity> headers = collectRequestHeaders();
        request.setHttpHeaderProperties(headers);
        if (mediaType != null) {
            request.setHttpBodyType(getBodyType());
            HttpBodyContent httpBodyContent = getHttpBodyContent();
            request.setHttpBodyContent(JsonUtil.toJson(httpBodyContent));
        }
        List<VariableEntity> variables = collectRequestVariables();
        request.setVariables(variables);
        request.setRestRequestMethod(httpMethod);
        request.setServiceType(WebServiceRequestEntity.RESTFUL);
        return request;
    }

    private List<WebElementPropertyEntity> collectRequestHeaders() {
        List<WebElementPropertyEntity> headers = new ArrayList<>();
        for (OpenApiRestParameter headerParameter : getHeaderParameters()) {
            WebElementPropertyEntity headerPropertyEntity = new WebElementPropertyEntity();
            headerPropertyEntity.setName(headerParameter.getName());
            headerPropertyEntity.setValue(headerParameter.getValue());
            headers.add(headerPropertyEntity);
        }
        return headers;
    }

    private List<VariableEntity> collectRequestVariables() {
        return getTemplateParameters().stream().map(p -> {
            VariableEntity variable = new VariableEntity();
            variable.setName(p.getName());
            variable.setDefaultValue(StringUtils.defaultIfBlank(p.getValue(), ""));
            variable.setDescription(p.getDescription());
            return variable;
        }).collect(Collectors.toList());
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
        urlEncodedBodyParameters.stream().forEach(p -> httpBody.addParameter(p));
        httpBody.setContentType(FORM_URLENCODED_CONTENT_TYPE);
        httpBody.setCharset(StandardCharsets.UTF_8.name());
        return httpBody;
    }

    private HttpBodyContent getFormDataBodyContent() {
        ParameterizedBodyContent<FormDataBodyParameter> httpBody = new ParameterizedBodyContent<>();
        formDataBodyParameters.stream().forEach(p -> httpBody.addParameter(p));
        httpBody.setContentType(MULTIPART_FORM_DATA_CONTENT_TYPE);
        httpBody.setCharset(StandardCharsets.UTF_8.name());
        return httpBody;
    }

    private String buildRequestUrl() {
        StringBuilder urlBuilder = new StringBuilder();

        String basePath = getBasePath();
        if (StringUtils.isNotBlank(basePath)) {
            urlBuilder.append(basePath);
        }

        String resourcePath = getResourcePath();
        if (StringUtils.isNotBlank(resourcePath)) {
            urlBuilder.append(resourcePath);
        }

        List<OpenApiRestParameter> queryParameters = getQueryParameters();
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

    private String getBasePath() {
        OpenApiRestServiceImportResult serviceImportResult = getServiceImportResult();
        String basePath = serviceImportResult.getBasePath();
        return basePath;
    }

    private String getResourcePath() {
        String resourcePath = resourceImportResult.getPath();
        if (StringUtils.isNotBlank(resourcePath)) {
            if (!resourcePath.startsWith("/")) {
                resourcePath = "/" + resourcePath;
            }
            resourcePath = StringUtils.replace(resourcePath, "{", "${");
        }
        return resourcePath;
    }

    private OpenApiRestServiceImportResult getServiceImportResult() {
        if (serviceImportResult == null) {
            OpenApiImportNode importNode = resourceImportResult;
            while (!(importNode instanceof OpenApiRestServiceImportResult)) {
                importNode = importNode.getParentImportNode();
            }
            serviceImportResult = (OpenApiRestServiceImportResult) importNode;
        }
        return serviceImportResult;
    }
}
