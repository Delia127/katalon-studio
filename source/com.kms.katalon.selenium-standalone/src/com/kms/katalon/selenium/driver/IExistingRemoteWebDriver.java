package com.kms.katalon.selenium.driver;

import java.util.HashMap;

import org.openqa.selenium.remote.Response;

public interface IExistingRemoteWebDriver {
    static final int SUCCESS_STATUS = 0;

    static final String SUCCESS_STATE = "success";

    default Response createResponseForNewSession(String oldSessionId) {
        Response response = new Response();
        response.setSessionId(oldSessionId);
        response.setState(SUCCESS_STATE);
        response.setStatus(SUCCESS_STATUS);
        response.setValue(new HashMap<String, Object>());
        return response;
    }
}
