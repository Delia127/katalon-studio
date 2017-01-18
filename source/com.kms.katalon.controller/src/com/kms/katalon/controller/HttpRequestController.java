package com.kms.katalon.controller;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.HashMap;

import org.apache.http.HttpHeaders;
import org.javalite.http.Get;
import org.javalite.http.Http;
import org.javalite.http.Post;
import org.javalite.http.Request;

import com.kms.katalon.controller.constants.ControllerMessageConstants;
import com.kms.katalon.controller.constants.StringConstants;
import com.kms.katalon.dal.exception.DALException;

public class HttpRequestController {

    private static HttpRequestController instance;

    public static HttpRequestController getInstance() {
        if (instance == null) {
            instance = new HttpRequestController();
        }
        return instance;
    }

    public Get get(String url) throws DALException {
        return get(url, null);
    }

    public Get get(String url, HashMap<String, String> headers) throws DALException {
        if (isBlank(url)) {
            throw new DALException(ControllerMessageConstants.CTRL_EXC_REQUEST_URL_IS_BLANK);
        }

        try {
            Get get = Http.get(url);

            addHeaders(get, headers);

            return get;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public Post post(String url, String body) throws DALException {
        return post(url, body, null);
    }

    public Post post(String url, String body, HashMap<String, String> headers) throws DALException {
        if (isBlank(url)) {
            throw new DALException(ControllerMessageConstants.CTRL_EXC_REQUEST_URL_IS_BLANK);
        }

        if (isBlank(body)) {
            throw new DALException(ControllerMessageConstants.CTRL_EXC_REQUEST_BODY_IS_BLANK);
        }

        try {
            Post post = Http.post(url, body);

            addHeaders(post, headers);

            return post;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    private void addHeaders(Request<?> request, HashMap<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return;
        }

        headers.putIfAbsent(HttpHeaders.USER_AGENT, StringConstants.APP_NAME + StringConstants.CR_SPACE
                + System.getProperty(WebServiceController.KATALON_VERSION_NUMBER_KEY, StringConstants.EMPTY));
        headers.forEach((name, value) -> request.header(name, value));
    }

}
