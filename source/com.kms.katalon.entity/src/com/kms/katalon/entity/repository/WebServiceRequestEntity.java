package com.kms.katalon.entity.repository;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.variable.VariableEntity;

public class WebServiceRequestEntity extends WebElementEntity {

    private static final long serialVersionUID = 1L;

    public static final String SOAP = "SOAP";

    public static final String SOAP12 = "SOAP12";

    public static final String RESTFUL = "RESTful";

    public static final String GET_METHOD = "GET";

    public static final String POST_METHOD = "POST";

    public static final String PUT_METHOD = "PUT";
    
    public static final String PATCH_METHOD = "PATCH";

    public static final String DELETE_METHOD = "DELETE";

    public static final String[] SERVICE_TYPES = new String[] { SOAP, RESTFUL };

    public static final String[] SOAP_REQUEST_METHODS = new String[] { SOAP, SOAP12, GET_METHOD, POST_METHOD };

    public static final String[] REST_REQUEST_METHODS = new String[] { GET_METHOD, POST_METHOD, PUT_METHOD,
            PATCH_METHOD , DELETE_METHOD, };

    private String serviceType = SOAP; // Default

    private List<WebElementPropertyEntity> httpHeaderProperties;
    
    private String httpBodyType = ""; // text, x-www-form-urlencoded, form-data, file
    
    private String httpBodyContent = ""; // JSON format of body content

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
    
    private String verificationScript;

    private String migratedVersion;
    
    private List<VariableEntity> variables = new ArrayList<>();

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

    public String getHttpBodyType() {
        return httpBodyType;
    }

    public void setHttpBodyType(String httpBodyType) {
        this.httpBodyType = httpBodyType;
    }

    public String getHttpBodyContent() {
        return httpBodyContent;
    }

    public void setHttpBodyContent(String httpBodyContent) {
        this.httpBodyContent = httpBodyContent;
    }

    public String getMigratedVersion() {
        return migratedVersion;
    }

    public void setMigratedVersion(String migratedVersion) {
        this.migratedVersion = migratedVersion;
    }

    public String getVerificationScript() {
        return verificationScript;
    }

    public void setVerificationScript(String verificationScript) {
        this.verificationScript = verificationScript;
    }

    public List<VariableEntity> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableEntity> variables) {
        this.variables = variables;
    }
}
