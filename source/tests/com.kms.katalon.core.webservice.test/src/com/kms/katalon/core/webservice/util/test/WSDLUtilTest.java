package com.kms.katalon.core.webservice.util.test;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.junit.Before;
import org.junit.Test;

import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.ServiceImpl;
import com.kms.katalon.core.webservice.util.WSDLUtil;

public class WSDLUtilTest {
    
    private Definition wsdlDefinition;
    
    @Before
    public void setUp() throws WSDLException, URISyntaxException {
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setFeature("javax.wsdl.importDocuments", true);
        
        URL wsdlDefinitionUrl = this.getClass().getClassLoader().getResource("resource/soap-test/calculator.wsdl");
        wsdlDefinition = reader.readWSDL(wsdlDefinitionUrl.toURI().toString());
    }

    @Test
    public void testGetAllExtensibilityElementsByClass() {
       Service service = getService(wsdlDefinition);
       Map<?, ?> ports = service.getPorts();
       PortImpl port = (PortImpl) ports.get("CalculatorSoap");
       List<SOAPAddress> soapAddresses = WSDLUtil.getExtensiblityElements(port.getExtensibilityElements(), SOAPAddress.class);
       assertNotNull(soapAddresses);
       assertEquals(soapAddresses.size(), 1);
    }
    
    @Test
    public void testGetExtensibilityElementByClass() {
        Service service = getService(wsdlDefinition);
        Map<?, ?> ports = service.getPorts();
        PortImpl port = (PortImpl) ports.get("CalculatorSoap");
        SOAPAddress soapAddress = WSDLUtil.getExtensiblityElement(port.getExtensibilityElements(), SOAPAddress.class);
        assertNotNull(soapAddress);
    }
    
    private Service getService(Definition wsdlDefinition) {
        ServiceImpl service = null;
        Map<?, ?> services = wsdlDefinition.getAllServices();
        for (Object sKey : services.keySet()) {
            service = (ServiceImpl) services.get(sKey);
            break;
        }
        return service;
    }
}
