package com.kms.katalon.core.testobject;

import java.util.ArrayList;
import java.util.List;

public class RequestObject extends TestObject {

    private String name;

    private String serviceType;

    private List<TestObjectProperty> httpHeaderProperties;

    private String httpBody = "";

    private String wsdlAddress = "";

    private String soapBody = "";

    private String soapRequestMethod = "";

    private String restUrl = "";

    private String restRequestMethod = "";
    
    private String soapServiceFunction = "";

    private List<TestObjectProperty> restParameters;

    private String objectId;

    public RequestObject(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public List<TestObjectProperty> getHttpHeaderProperties() {
        if (httpHeaderProperties == null) {
            httpHeaderProperties = new ArrayList<TestObjectProperty>();
        }
        return httpHeaderProperties;
    }

    public void setHttpHeaderProperties(List<TestObjectProperty> httpHeaderProperties) {
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

    public String getSoapBody() {
        return soapBody;
    }

    public void setSoapBody(String soapBody) {
        this.soapBody = soapBody;
    }

    public String getSoapRequestMethod() {
        return soapRequestMethod;
    }

    public void setSoapRequestMethod(String soapRequestMethod) {
        this.soapRequestMethod = soapRequestMethod;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public String getRestRequestMethod() {
        return restRequestMethod;
    }

    public void setRestRequestMethod(String restRequestMethod) {
        this.restRequestMethod = restRequestMethod;
    }

    public List<TestObjectProperty> getRestParameters() {
        if (restParameters == null) {
            restParameters = new ArrayList<TestObjectProperty>();
        }
        return restParameters;
    }

    public void setRestParameters(List<TestObjectProperty> restParameters) {
        this.restParameters = restParameters;
    }
    
    public String getSoapServiceFunction() {
        return soapServiceFunction;
    }

    public void setSoapServiceFunction(String soapServiceFunction) {
        this.soapServiceFunction = soapServiceFunction;
    }
}
