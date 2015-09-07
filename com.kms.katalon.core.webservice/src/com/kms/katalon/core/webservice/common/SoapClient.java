package com.kms.katalon.core.webservice.common;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.extensions.http.HTTPOperation;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

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
import com.kms.katalon.core.testobject.TestObjectProperty;

public class SoapClient implements Requestor{
	
	private static String nsPrefix = "com.kms.katalon";
	private String ns;
	private String serviceName;
	private String protocol = "SOAP"; //Default is SOAP
	private String endPoint;
	private String actionUri; //Can be SOAPAction, HTTP location URL
	
	private RequestObject requestObject;
	
	public SoapClient(){
	}
	
	private void parseWsdl(){
		try {
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLReader reader = factory.newWSDLReader();
			reader.setFeature("javax.wsdl.verbose", false);
			reader.setFeature("javax.wsdl.importDocuments", true);
			Definition wsdlDefinition = reader.readWSDL(null, requestObject.getWsdlAddress());
			
			lookForService(wsdlDefinition);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//Look for the Service, but for now just consider the first one
	private void lookForService(Definition wsdlDefinition){
		ServiceImpl service = null;
		Map<?,?> services = wsdlDefinition.getAllServices();
		for(Object sKey : services.keySet()){
			service = (ServiceImpl)services.get(sKey);
			ns = ((QName)sKey).getNamespaceURI();
			setServiceName(((QName)sKey).getLocalPart());
			break;
		}	
		
		parseService(service);
	}
	
	private void parseService(ServiceImpl service){
		Map<?,?> ports = service.getPorts();
		for(Object pKey : ports.keySet()){
			PortImpl port = (PortImpl)ports.get(pKey);
			
			Object objBinding = port.getBinding().getExtensibilityElements().get(0);
			String proc = "";
			BindingOperationImpl operation = (BindingOperationImpl)port.getBinding().getBindingOperation(requestObject.getSoapServiceFunction(), null, null);
			
			if(objBinding != null && objBinding instanceof SOAPBindingImpl){
				proc = "SOAP";
				endPoint = ((SOAPAddressImpl)port.getExtensibilityElements().get(0)).getLocationURI();
				actionUri = ((SOAPOperation)operation.getExtensibilityElements().get(0)).getSoapActionURI();
			}
			else if(objBinding != null && objBinding instanceof SOAP12BindingImpl){
				proc = "SOAP12";
				endPoint = ((SOAP12AddressImpl)port.getExtensibilityElements().get(0)).getLocationURI();
				actionUri = ((SOAP12Operation)operation.getExtensibilityElements().get(0)).getSoapActionURI();
			}
			else if(objBinding != null &&objBinding instanceof HTTPBindingImpl){
				proc = ((HTTPBindingImpl)objBinding).getVerb();
				endPoint = ((HTTPAddressImpl)port.getExtensibilityElements().get(0)).getLocationURI();
				actionUri = ((HTTPOperation)operation.getExtensibilityElements().get(0)).getLocationURI();
			}
			
			if(protocol.equals(proc)){
				break;
			}
		}		
	}

	public ResponseObject send(RequestObject request){
		this.requestObject = request;
		parseWsdl();
		ResponseObject responseObject = new ResponseObject();
		try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapMessage = createSOAPRequest();
            
            SOAPMessage soapResponse = soapConnection.call(soapMessage, endPoint);

            // Process the SOAP Response
            String responseText = handleSOAPResponse(soapResponse);

            soapConnection.close();
            
            //SOAP is HTTP-XML protocol
            responseObject.setContentType("application/xml");
            responseObject.setResponseText(responseText);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return responseObject;
	}
	
    private SOAPMessage createSOAPRequest() throws Exception {
    	
        MessageFactory messageFactory = MessageFactory.newInstance();
        
        SOAPMessage soapMessage = messageFactory.createMessage();
        
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(nsPrefix, ns);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement(requestObject.getSoapServiceFunction(), nsPrefix);
        
        //Parameters
        for(TestObjectProperty prop :requestObject.getSoapParameters()){
        	SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(prop.getName(), nsPrefix);
            soapBodyElem1.addTextNode(prop.getValue());
        }

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", actionUri);

        soapMessage.saveChanges();

        return soapMessage;
    }

    private static String handleSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(os);
        transformer.transform(sourceContent, result);
        String resultString = new String(os.toByteArray(),"UTF-8");
        os.close();
        return resultString;
    }

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
    /*
	public static void main(String[] args) {
		RequestObject requestObject = new RequestObject(null);
		requestObject.setWsdlAddress("http://ws.cdyne.com/emailverify/Emailvernotestemail.asmx?WSDL");
		requestObject.setSoapRequestMethod("SOAP");
		requestObject.setSoapServiceFunction("VerifyEmail");
		requestObject.getSoapParameters().add(new TestObjectProperty("email", ConditionType.EQUALS, "hieuphan@kms-technology.com"));
		requestObject.getSoapParameters().add(new TestObjectProperty("LicenseKey", ConditionType.EQUALS, "12121221212"));
		
		SoapClient sc2 = new SoapClient();
		sc2.send(requestObject);
	}
	*/
}
