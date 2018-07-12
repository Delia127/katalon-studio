package com.kms.katalon.controller;

import java.io.File;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.webservice.common.ServiceRequestFactory;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WebServiceController extends EntityController {

    public static final String KATALON_VERSION_NUMBER_KEY = "katalon.versionNumber";

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

    private RequestObject getRequestObject(WebServiceRequestEntity entity, String projectDir) {
        return (RequestObject) ObjectRepository.readTestObjectFile(entity.getIdForDisplay(), new File(entity.getId()),
                projectDir);
    }

    public ResponseObject sendRequest(WebServiceRequestEntity entity, String projectDir,
            ProxyInformation proxyInformation) throws Exception {
        RequestObject requestObject = getRequestObject(entity, projectDir);
        return sendRequest(requestObject, projectDir, proxyInformation);
    }
    
    public ResponseObject sendRequest(RequestObject requestObject, String projectDir,
            ProxyInformation proxyInformation) throws Exception  {
        return ServiceRequestFactory.getInstance(requestObject, projectDir, proxyInformation).send(requestObject);
    }
}
