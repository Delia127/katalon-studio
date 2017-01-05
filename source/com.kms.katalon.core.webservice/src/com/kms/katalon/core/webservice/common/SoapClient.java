package com.kms.katalon.core.webservice.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.http.HTTPOperation;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.bsf.util.IOUtils;

import com.ibm.wsdl.BindingOperationImpl;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.ServiceImpl;
import com.ibm.wsdl.extensions.http.HTTPAddressImpl;
import com.ibm.wsdl.extensions.http.HTTPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12AddressImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12BindingImpl;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.webservice.constants.CoreWebserviceMessageConstants;
import com.kms.katalon.core.webservice.exception.WebServiceException;

public class SoapClient implements Requestor {

    private String serviceName;

    private String protocol = "SOAP"; // Default is SOAP

    private String endPoint;

    private String actionUri; // Can be SOAPAction, HTTP location URL

    private RequestObject requestObject;

    public SoapClient() {
    }

    private void parseWsdl() throws WSDLException, WebServiceException {
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setFeature("javax.wsdl.importDocuments", true);
        Definition wsdlDefinition = reader.readWSDL(null, requestObject.getWsdlAddress());
        lookForService(wsdlDefinition);
    }

    // Look for the Service, but for now just consider the first one
    private void lookForService(Definition wsdlDefinition) throws WebServiceException {
        ServiceImpl service = null;
        Map<?, ?> services = wsdlDefinition.getAllServices();
        for (Object sKey : services.keySet()) {
            service = (ServiceImpl) services.get(sKey);
            setServiceName(((QName) sKey).getLocalPart());
            break;
        }

        parseService(service);
    }

    private void parseService(ServiceImpl service) throws WebServiceException {
        Map<?, ?> ports = service.getPorts();
        for (Object pKey : ports.keySet()) {

            PortImpl port = (PortImpl) ports.get(pKey);

            Object objBinding = port.getBinding().getExtensibilityElements().get(0);
            String proc = "";
            BindingOperationImpl operation = (BindingOperationImpl) port.getBinding()
                    .getBindingOperation(requestObject.getSoapServiceFunction(), null, null);
            if (operation == null) {
                throw new WebServiceException(CoreWebserviceMessageConstants.MSG_NO_SERVICE_OPERATION);
            }

            if (objBinding != null && objBinding instanceof SOAPBindingImpl) {
                proc = "SOAP";
                endPoint = ((SOAPAddressImpl) port.getExtensibilityElements().get(0)).getLocationURI();
                actionUri = ((SOAPOperation) operation.getExtensibilityElements().get(0)).getSoapActionURI();
            } else if (objBinding != null && objBinding instanceof SOAP12BindingImpl) {
                proc = "SOAP12";
                endPoint = ((SOAP12AddressImpl) port.getExtensibilityElements().get(0)).getLocationURI();
                actionUri = ((SOAP12Operation) operation.getExtensibilityElements().get(0)).getSoapActionURI();
            } else if (objBinding != null && objBinding instanceof HTTPBindingImpl) {
                proc = ((HTTPBindingImpl) objBinding).getVerb();
                endPoint = ((HTTPAddressImpl) port.getExtensibilityElements().get(0)).getLocationURI();
                actionUri = ((HTTPOperation) operation.getExtensibilityElements().get(0)).getLocationURI();
            }

            if (protocol.equals(proc)) {
                break;
            }
        }
    }

    @Override
    public ResponseObject send(RequestObject request) throws IOException, WSDLException, WebServiceException {
        this.requestObject = request;
        parseWsdl();
        ResponseObject responseObject = new ResponseObject();

        URL oURL = new URL(endPoint);
        HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
        con.setRequestProperty("SOAPAction", actionUri);

        OutputStream reqStream = con.getOutputStream();
        reqStream.write(request.getSoapBody().getBytes());

        InputStream resStream = con.getInputStream();
        String responseText = IOUtils.getStringFromReader(new InputStreamReader(resStream));

        // SOAP is HTTP-XML protocol
        responseObject.setContentType("application/xml");
        responseObject.setResponseText(responseText);
        return responseObject;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
