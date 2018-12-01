package com.kms.katalon.entity.repository;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.variable.VariableEntity;

public class WebServiceRequestEntity extends WebElementEntity {

    private static final long serialVersionUID = 1L;

    public static final String SOAP = "SOAP";

    public static final String SOAP12 = "SOAP12";

    public static final String RESTFUL = "RESTful";

    public static final String REST = "REST";

    public static final String GET_METHOD = "GET";

    public static final String POST_METHOD = "POST";

    public static final String PUT_METHOD = "PUT";
    
    public static final String PATCH_METHOD = "PATCH";

    public static final String DELETE_METHOD = "DELETE";
    
    public static final String HEAD_METHOD = "HEAD";
    
    public static final String CONNECT_METHOD = "CONNECT";
    
    public static final String OPTIONS_METHOD = "OPTIONS";
    
    public static final String TRACE_METHOD = "TRACE";

    public static final String[] SERVICE_TYPES = new String[] { SOAP, RESTFUL };

    public static final String[] SOAP_REQUEST_METHODS = new String[] { SOAP, SOAP12, GET_METHOD, POST_METHOD };

    public static final String[] REST_REQUEST_METHODS = new String[] { GET_METHOD, POST_METHOD, PUT_METHOD,
            PATCH_METHOD , DELETE_METHOD, HEAD_METHOD, CONNECT_METHOD, OPTIONS_METHOD, TRACE_METHOD};

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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((httpBody == null) ? 0 : httpBody.hashCode());
        result = prime * result + ((httpBodyContent == null) ? 0 : httpBodyContent.hashCode());
        result = prime * result + ((httpBodyType == null) ? 0 : httpBodyType.hashCode());
        result = prime * result + ((httpHeaderProperties == null) ? 0 : httpHeaderProperties.hashCode());
        result = prime * result + ((migratedVersion == null) ? 0 : migratedVersion.hashCode());
        result = prime * result + ((restParameters == null) ? 0 : restParameters.hashCode());
        result = prime * result + ((restRequestMethod == null) ? 0 : restRequestMethod.hashCode());
        result = prime * result + ((restUrl == null) ? 0 : restUrl.hashCode());
        result = prime * result + ((serviceType == null) ? 0 : serviceType.hashCode());
        result = prime * result + ((soapBody == null) ? 0 : soapBody.hashCode());
        result = prime * result + ((soapHeader == null) ? 0 : soapHeader.hashCode());
        result = prime * result + ((soapParameters == null) ? 0 : soapParameters.hashCode());
        result = prime * result + ((soapRequestMethod == null) ? 0 : soapRequestMethod.hashCode());
        result = prime * result + ((soapServiceFunction == null) ? 0 : soapServiceFunction.hashCode());
        result = prime * result + ((variables == null) ? 0 : variables.hashCode());
        result = prime * result + ((verificationScript == null) ? 0 : verificationScript.hashCode());
        result = prime * result + ((wsdlAddress == null) ? 0 : wsdlAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof WebServiceRequestEntity)) {
            return false;
        }
        WebServiceRequestEntity other = (WebServiceRequestEntity) obj;
        if (httpBody == null) {
            if (other.httpBody != null)
                return false;
        } else if (!httpBody.equals(other.httpBody))
            return false;
        if (httpBodyContent == null) {
            if (other.httpBodyContent != null)
                return false;
        } else if (!httpBodyContent.equals(other.httpBodyContent))
            return false;
        if (httpBodyType == null) {
            if (other.httpBodyType != null)
                return false;
        } else if (!httpBodyType.equals(other.httpBodyType))
            return false;
        if (httpHeaderProperties == null) {
            if (other.httpHeaderProperties != null)
                return false;
        } else if (!httpHeaderProperties.equals(other.httpHeaderProperties))
            return false;
        if (migratedVersion == null) {
            if (other.migratedVersion != null)
                return false;
        } else if (!migratedVersion.equals(other.migratedVersion))
            return false;
        if (restParameters == null) {
            if (other.restParameters != null)
                return false;
        } else if (!restParameters.equals(other.restParameters))
            return false;
        if (restRequestMethod == null) {
            if (other.restRequestMethod != null)
                return false;
        } else if (!restRequestMethod.equals(other.restRequestMethod))
            return false;
        if (restUrl == null) {
            if (other.restUrl != null)
                return false;
        } else if (!restUrl.equals(other.restUrl))
            return false;
        if (serviceType == null) {
            if (other.serviceType != null)
                return false;
        } else if (!serviceType.equals(other.serviceType))
            return false;
        if (soapBody == null) {
            if (other.soapBody != null)
                return false;
        } else if (!soapBody.equals(other.soapBody))
            return false;
        if (soapHeader == null) {
            if (other.soapHeader != null)
                return false;
        } else if (!soapHeader.equals(other.soapHeader))
            return false;
        if (soapParameters == null) {
            if (other.soapParameters != null)
                return false;
        } else if (!soapParameters.equals(other.soapParameters))
            return false;
        if (soapRequestMethod == null) {
            if (other.soapRequestMethod != null)
                return false;
        } else if (!soapRequestMethod.equals(other.soapRequestMethod))
            return false;
        if (soapServiceFunction == null) {
            if (other.soapServiceFunction != null)
                return false;
        } else if (!soapServiceFunction.equals(other.soapServiceFunction))
            return false;
        if (variables == null) {
            if (other.variables != null)
                return false;
        } else if (!variables.equals(other.variables))
            return false;
        if (verificationScript == null) {
            if (other.verificationScript != null)
                return false;
        } else if (!verificationScript.equals(other.verificationScript))
            return false;
        if (wsdlAddress == null) {
            if (other.wsdlAddress != null)
                return false;
        } else if (!wsdlAddress.equals(other.wsdlAddress))
            return false;
        return true;
    }
}
