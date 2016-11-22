package com.kms.katalon.entity.repository;

import java.util.ArrayList;
import java.util.List;

public class WebServiceRequestEntity extends WebElementEntity {

    private static final long serialVersionUID = 1L;

    public static final String[] SERVICE_TYPES = new String[] { "SOAP", "RESTful" };

    public static final String[] SOAP_REQUEST_METHODS = new String[] { "POST", "GET" };

    public static final String[] REST_REQUEST_METHODS = new String[] { "GET", "POST", "PUT", "DELETE" };

    private String serviceType = SERVICE_TYPES[0]; // Defaut

    private List<WebElementPropertyEntity> httpHeaderProperties;

    private String httpBody = "";

    private String wsdlAddress = "";

    private String soapHeader = "";

    private String soapBody = "";

    private String soapRequestMethod = "";

    private String soapServiceFunction = "";

    private List<WebElementPropertyEntity> soapParameters;

    private String restUrl = "";

    private String restRequestMethod = "";

    private List<WebElementPropertyEntity> restParameters;

    public String getSoapRequestMethod() {
        return soapRequestMethod;
    }

    public void setSoapRequestMethod(String soapRequestMethod) {
        this.soapRequestMethod = soapRequestMethod;
    }

    public String getSoapServiceFunction() {
        return soapServiceFunction;
    }

    public void setSoapServiceFunction(String soapServiceFunction) {
        this.soapServiceFunction = soapServiceFunction;
    }

    public List<WebElementPropertyEntity> getSoapParameters() {
        if (soapParameters == null) {
            soapParameters = new ArrayList<WebElementPropertyEntity>();
        }
        return soapParameters;
    }

    public void setSoapParameters(List<WebElementPropertyEntity> soapServiceFunctionParameters) {
        this.soapParameters = soapServiceFunctionParameters;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public List<WebElementPropertyEntity> getHttpHeaderProperties() {
        if (httpHeaderProperties == null) {
            httpHeaderProperties = new ArrayList<WebElementPropertyEntity>();
        }
        return httpHeaderProperties;
    }

    public void setHttpHeaderProperties(List<WebElementPropertyEntity> httpHeaderProperties) {
        this.httpHeaderProperties = httpHeaderProperties;
    }

    public String getHttpBody() {
        return httpBody;
    }

    public void setHttpBody(String httpBody) {
        this.httpBody = httpBody;
    }

    public String getWsdlAddress() {
        return wsdlAddress;
    }

    public void setWsdlAddress(String wsdlAddress) {
        this.wsdlAddress = wsdlAddress;
    }

    public String getSoapHeader() {
        return soapHeader;
    }

    public void setSoapHeader(String soapHeader) {
        this.soapHeader = soapHeader;
    }

    public String getSoapBody() {
        return soapBody;
    }

    public void setSoapBody(String soapBody) {
        this.soapBody = soapBody;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getRestRequestMethod() {
        return restRequestMethod;
    }

    public void setRestRequestMethod(String restRequestMethod) {
        this.restRequestMethod = restRequestMethod;
    }

    public List<WebElementPropertyEntity> getRestParameters() {
        if (restParameters == null) {
            restParameters = new ArrayList<WebElementPropertyEntity>();
        }
        return restParameters;
    }

    public void setRestParameters(List<WebElementPropertyEntity> restParameters) {
        this.restParameters = restParameters;
    }
}
