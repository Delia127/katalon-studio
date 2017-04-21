package com.kms.katalon.controller;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.javalite.http.Delete;
import org.javalite.http.Get;
import org.javalite.http.Http;
import org.javalite.http.Post;
import org.javalite.http.Put;
import org.javalite.http.Request;

import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WebServiceController extends EntityController {

    public static final String KATALON_VERSION_NUMBER_KEY = "katalon.versionNumber";

    private static final String HTTP_USER_AGENT = "User-Agent";

    private static EntityController _instance;

    private WebServiceController() {
        super();
    }

    public static WebServiceController getInstance() {
        if (_instance == null) {
            _instance = new WebServiceController();
        }
        return (WebServiceController) _instance;
    }

    public Request<?> sendRESTfulRequest(WebServiceRequestEntity entity) throws Exception {
        if (entity == null || isBlank(entity.getRestUrl()) || isBlank(entity.getRestRequestMethod())) {
            return null;
        }

        boolean isRESTful = WebServiceRequestEntity.RESTFUL.equals(entity.getServiceType());
        if (!isRESTful) {
            return null;
        }

        switch (entity.getRestRequestMethod()) {
            case WebServiceRequestEntity.GET_METHOD:
                return sendRESTfulGetRequest(entity);
            case WebServiceRequestEntity.POST_METHOD:
                return sendRESTfulPostRequest(entity);
            case WebServiceRequestEntity.PUT_METHOD:
                return sendRESTfulPutRequest(entity);
            case WebServiceRequestEntity.DELETE_METHOD:
                return sendRESTfulDeleteRequest(entity);
            default:
                return null;
        }
    }

    public Get sendRESTfulGetRequest(WebServiceRequestEntity entity) throws Exception {
        if (entity == null) {
            return null;
        }
        Get get = Http.get(buildURLWithParams(entity));
        addHeaders(get, entity);
        return get;
    }

    public Post sendRESTfulPostRequest(WebServiceRequestEntity entity) {
        if (entity == null) {
            return null;
        }

        Post post = Http.post(entity.getRestUrl(), entity.getHttpBody());

        // Add params
        List<WebElementPropertyEntity> params = entity.getRestParameters();
        if (!params.isEmpty()) {
            params.forEach(param -> post.param(param.getName(), param.getValue()));
        }

        // Add headers
        addHeaders(post, entity);
        return post;
    }

    public Request<?> sendRESTfulPutRequest(WebServiceRequestEntity entity) throws Exception {
        if (entity == null) {
            return null;
        }
        Put put = Http.put(buildURLWithParams(entity), entity.getHttpBody());
        addHeaders(put, entity);
        return put;
    }

    public Request<?> sendRESTfulDeleteRequest(WebServiceRequestEntity entity) throws Exception {
        if (entity == null) {
            return null;
        }
        Delete delete = Http.delete(buildURLWithParams(entity));
        addHeaders(delete, entity);
        return delete;
    }

    private String buildURLWithParams(WebServiceRequestEntity entity) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(entity.getRestUrl());
        List<WebElementPropertyEntity> params = entity.getRestParameters();
        if (!params.isEmpty()) {
            params.forEach(param -> uriBuilder.addParameter(param.getName(), param.getValue()));
        }
        return uriBuilder.build().toString();
    }

    private void addHeaders(Request<?> request, WebServiceRequestEntity entity) {
        List<WebElementPropertyEntity> httpHeaders = entity.getHttpHeaderProperties();
        boolean hasUserAgent = false;
        if (!httpHeaders.isEmpty()) {
            for (WebElementPropertyEntity header : httpHeaders) {
                String headerName = header.getName();
                request.header(headerName, header.getValue());
                if (HTTP_USER_AGENT.equalsIgnoreCase(headerName)) {
                    hasUserAgent = true;
                }
            }
        }
        if (!hasUserAgent) {
            request.header(HTTP_USER_AGENT, StringConstants.APP_NAME + StringConstants.CR_SPACE
                    + System.getProperty(KATALON_VERSION_NUMBER_KEY, StringConstants.EMPTY));
        }
    }

}
