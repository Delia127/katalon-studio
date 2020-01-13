package com.kms.katalon.composer.webservice.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.http.HTTPOperation;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.javalite.http.Http;
import org.javalite.http.Post;
import org.javalite.http.Request;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ibm.wsdl.BindingImpl;
import com.ibm.wsdl.BindingOperationImpl;
import com.ibm.wsdl.PartImpl;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.ServiceImpl;
import com.ibm.wsdl.extensions.http.HTTPAddressImpl;
import com.ibm.wsdl.extensions.http.HTTPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12AddressImpl;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

/**
 * @see <a href="https://www.w3.org/TR/wsdl">Web Services Description Language</a>
 */
@SuppressWarnings("unchecked")
public class WSDLHelper {

    /**
     * The "this namespace" (tns) prefix is used as a convention to refer to the current document
     */
    private static final String THIS_NAMESPACE = "tns";

    private static final String SOAP_HEADER_CONTENT_TYPE_VALUE = "text/xml; charset=utf-8";

    private static final String SOAP12_HEADER_CONTENT_TYPE_VALUE = "application/soap+xml; charset=utf-8";

    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    private static final String SOAP_HEADER_ACTION = "SOAPAction";

    private static final String SOAP = "SOAP";

    private static final String SOAP12 = "SOAP12";

    private static final String GET = "GET";

    private static final String POST = "POST";

    private String url;

    private String authorizationValue;

    private Definition definition;

    private Map<String, PortImpl> portMap;

    private Wsdl wsdl;

    private WSDLHelper(String url, String authorizationValue) {
        this.url = url;
        this.authorizationValue = authorizationValue;
        try {
            this.wsdl = parseWsdl(url);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            this.wsdl = null;
        }
    }
    
    private Wsdl parseWsdl(String url) {
        Wsdl wsdl;
        if (!isWebUrl(url)) {
            File file = new File(url);
            String fileUrl = file.toURI().toString();
            wsdl = Wsdl.parse(fileUrl);
        } else {
            wsdl = Wsdl.parse(url);
        }
        return wsdl;
    }
    
    private boolean isWebUrl(String string) {
        return string.startsWith("http") || string.startsWith("https");
    }

    /**
     * Create WSDL helper for URL without authentication
     * 
     * @param url WSDL URL
     * @return WSDLHelper
     */
    public static WSDLHelper newInstance(String url) {
        return new WSDLHelper(url, null);
    }

    /**
     * Create WSDL helper for URL with authentication
     * 
     * @param url WSDL URL
     * @param authorizationValue The value for HTTP Authorization header
     * @return WSDLHelper
     */
    public static WSDLHelper newInstance(String url, String authorizationValue) {
        return new WSDLHelper(url, authorizationValue);
    }

    public Definition getDefinition() throws WSDLException {
        if (definition == null) {
            CustomWSDLReader reader = new CustomWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);

            if (authorizationValue == null) {
                definition = reader.readWSDL(url);
            } else {
                reader.setAuthorizationValue(authorizationValue);
                InputStream inputStream = Http.get(url).header(HttpHeaders.AUTHORIZATION, authorizationValue).getInputStream();
                definition = reader.readWSDL(url, new InputSource(inputStream));
            }
        }
        return definition;
    }

    public List<QName> getServiceNames() throws WSDLException {
        return new ArrayList<QName>(getDefinition().getServices().keySet());
    }

    public ServiceImpl getService() throws WSDLException {
        Collection<ServiceImpl> services = getDefinition().getAllServices().values();
        if (services.isEmpty()) {
            return null;
        }
        return new ArrayList<ServiceImpl>(services).get(0);
    }

    public List<PortImpl> getPorts() throws WSDLException {
        ServiceImpl service = getService();
        if (service == null) {
            return Collections.emptyList();
        }

        Collection<PortImpl> ports = service.getPorts().values();
        if (ports.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<PortImpl>(ports);
    }

    public PortImpl getPortByRequestMethod(String requestMethod) throws WSDLException {
        return getPortMap().get(requestMethod);
    }

    public List<BindingImpl> getBindings() throws WSDLException {
        return new ArrayList<BindingImpl>(getDefinition().getAllBindings().values());
    }

    public List<QName> getBindingNames() throws WSDLException {
        return new ArrayList<QName>(getDefinition().getAllBindings().keySet());
    }

    public Binding getBindingByName(QName bindingName) throws WSDLException {
        return getDefinition().getBinding(bindingName);
    }

    public BindingImpl getBindingByRequestMethod(String requestMethod) throws WSDLException {
        PortImpl port = getPortByRequestMethod(requestMethod);
        return (BindingImpl) port.getBinding();
    }

    public List<String> getOperationNamesByRequestMethod(String requestMethod) throws WSDLException {
        List<String> operationNames = new ArrayList<>();
        getOperationsByRequestMethod(requestMethod).forEach(o -> operationNames.add(o.getName()));
        return operationNames;
    }

    public List<Operation> getOperationsByRequestMethod(String requestMethod) throws WSDLException {
        List<Operation> operations = new ArrayList<>();
        getBindingOperationsByRequestMethod(requestMethod).forEach(o -> operations.add(o.getOperation()));
        return operations;
    }

    public List<BindingOperationImpl> getBindingOperationsByRequestMethod(String requestMethod) throws WSDLException {
        PortImpl port = getPortMap().get(requestMethod);
        if (port == null) {
            return Collections.emptyList();
        }

        List<BindingOperationImpl> bindingOperations = new ArrayList<>();
        bindingOperations.addAll(port.getBinding().getBindingOperations());
        return bindingOperations;
    }

    public Operation getOperationByRequestMethodNName(String requestMethod, String operationName) throws WSDLException {
        return getOperationsByRequestMethod(requestMethod).stream()
                .filter(o -> o.getName().equals(operationName))
                .findFirst()
                .get();
    }

    public String getOperationURI(BindingOperationImpl bindingOperation, String requestMethod) {
        Object action = bindingOperation.getExtensibilityElements().get(0);
        switch (requestMethod.toUpperCase()) {
            case SOAP:
                return ((SOAPOperation) action).getSoapActionURI();

            case SOAP12:
                return ((SOAP12Operation) action).getSoapActionURI();

            case GET:
            case POST:
                return ((HTTPOperation) action).getLocationURI();

            default:
                return null;
        }
    }

    public String getPortAddressLocation(String requestMethod) throws WSDLException {
        Object address = getPortByRequestMethod(requestMethod).getExtensibilityElements().get(0);
        switch (requestMethod.toUpperCase()) {
            case SOAP:
                return ((SOAPAddressImpl) address).getLocationURI();

            case SOAP12:
                return ((SOAP12AddressImpl) address).getLocationURI();

            case GET:
            case POST:
                return ((HTTPAddressImpl) address).getLocationURI();

            default:
                return null;
        }
    }

    public Map<String, PortImpl> getPortMap() throws WSDLException {
        if (portMap == null) {
            portMap = new HashMap<>();
            for (PortImpl p : getPorts()) {
                /**
                 * <pre>
                 *  <wsdl:service name="CurrencyConvertor">
                 *      <wsdl:port name="CurrencyConvertorSoap" binding="tns:CurrencyConvertorSoap">
                 *          <soap:address location="http://www.webservicex.net/CurrencyConvertor.asmx"/>
                 *      </wsdl:port>
                 *      <wsdl:port name="CurrencyConvertorSoap12" binding="tns:CurrencyConvertorSoap12">
                 *          <soap12:address location="http://www.webservicex.net/CurrencyConvertor.asmx"/>
                 *      </wsdl:port>
                 *      <wsdl:port name="CurrencyConvertorHttpGet" binding="tns:CurrencyConvertorHttpGet">
                 *          <http:address location="http://www.webservicex.net/CurrencyConvertor.asmx"/>
                 *      </wsdl:port>
                 *      <wsdl:port name="CurrencyConvertorHttpPost" binding="tns:CurrencyConvertorHttpPost">
                 *          <http:address location="http://www.webservicex.net/CurrencyConvertor.asmx"/>
                 *      </wsdl:port>
                 *  </wsdl:service>
                 * </pre>
                 * 
                 * In the script below, p.getExtensibilityElements().get(0) matches <soap:address ... />,
                 * <soap12:address ... /> and <http:address ... />. It's the first element of Port.
                 */
                Object portAddress = p.getExtensibilityElements().get(0);
                if (portAddress instanceof SOAPAddressImpl) {
                    portMap.put(SOAP, p);
                    continue;
                }

                if (portAddress instanceof SOAP12AddressImpl) {
                    portMap.put(SOAP12, p);
                    continue;
                }

                if (portAddress instanceof HTTPAddressImpl) {
                    // HTTP (GET, POST)
                    HTTPBindingImpl httpBindingImpl = (HTTPBindingImpl) p.getBinding()
                            .getExtensibilityElements()
                            .get(0);
                    portMap.put(httpBindingImpl.getVerb().toUpperCase(), p);
                }
            }
        }
        return portMap;
    }

    public SOAPMessage generateInputSOAPMessage(String requestMethod, String namespaceURI, String actionUri,
            String operationName, Map<String, List<String>> paramMap) throws SOAPException, WSDLException {
        SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(THIS_NAMESPACE, namespaceURI);

        // SOAP Body
        SOAPElement soapBodyElemment = envelope.getBody().addChildElement(operationName, THIS_NAMESPACE);
        if(paramMap.get(operationName) != null){
            
        	List<String> params = paramMap.get(operationName);
            for(String param: params){
            	soapBodyElemment.addChildElement(param, THIS_NAMESPACE).addTextNode("?");
            }
            
        } else {
            // Input Parameters
            Input input = getOperationByRequestMethodNName(requestMethod, operationName).getInput();
            Collection<PartImpl> parts = input.getMessage().getParts().values();
            
            for (PartImpl part : parts) {
                soapBodyElemment.addChildElement(part.getName(), THIS_NAMESPACE).addTextNode("?");
            }   
        }

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader(SOAP_HEADER_ACTION, actionUri);

        soapMessage.saveChanges();
        return soapMessage;
    }

	public  Map<String, List<String>> getParamMap(){
    	Map<String, List<String>> paramMap = new HashMap<>();
    	try{
	    	List<String> operationNames = getOperationNamesByRequestMethod(SOAP);
            Types types = getDefinition().getTypes();
        	for(Object obj : SafeUtils.safeList(types.getExtensibilityElements())){
        		if(obj != null && obj instanceof Schema){
        			Schema schema = (Schema) obj;
        			NodeList nodeList = schema.getElement().getChildNodes();
        			if(nodeList != null){
            			for(String name: operationNames){
            			    if (StringUtils.isEmpty(name)) {
            			        continue;
            			    }
            				List<String> params =
                			XmlUtils.wrapNodeList(nodeList).stream()
                			.filter(a -> a.getNodeType() == Node.ELEMENT_NODE)
                			.filter(a -> {
                			    Node node = a.getAttributes().getNamedItem("name");
                			    return node != null && node.getNodeValue().equals(name);
                			})
                			.flatMap(WSDLHelper::flatten)
                			.filter(a -> a.getNodeType() == Node.ELEMENT_NODE)
                			.filter(a -> a.getAttributes().getNamedItem("name") != null)
                			.filter(a -> !a.getAttributes().getNamedItem("name").getNodeValue().equals(name))
                			.map(a -> a.getAttributes().getNamedItem("name").getNodeValue())
                			.filter(a -> a != null)
                			.collect(Collectors.toList());
                			paramMap.put(name, params);
            			}
        			}
        		}
        	}
        	return paramMap;
    	} catch (Exception ex){
    		// Do nothing
    	    return Collections.emptyMap();
    	}
    }
    
	public Wsdl getWsdl() {
	    return wsdl;
	}
	
    public static Stream<Node> flatten(Node node){
    	return Stream.concat(Stream.of(node),
    			XmlUtils.wrapNodeList(node.getChildNodes()).stream().flatMap(WSDLHelper::flatten));
    }

    public static String generateInputSOAPMessageText(String url, String authorizationValue, String requestMethod,
            String operationName, Map<String, List<String>> paramMap) throws WSDLException, SOAPException, IOException {
        WSDLHelper helper = WSDLHelper.newInstance(url, authorizationValue);
        try {
            BindingImpl binding = helper.getBindingByRequestMethod(requestMethod);
            SoapBuilder builder = helper.getWsdl().binding().name(binding.getQName()).find();
            SoapOperation operation = builder.operation().name(operationName).find();
            String message = builder.buildInputMessage(operation);
            return message;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            //fall back to old mechanism to generate input message in case of exception
            BindingOperationImpl bindingOperation = helper.getBindingOperationsByRequestMethod(requestMethod)
              .stream()
              .filter(bo -> bo.getName().equals(operationName))
              .findFirst()
              .get();
            String operationURI = helper.getOperationURI(bindingOperation, requestMethod);

            String namespaceURI = helper.getService().getQName().getNamespaceURI();
            SOAPMessage soapMessage = helper.generateInputSOAPMessage(requestMethod, namespaceURI, operationURI,
                    operationName, paramMap);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapMessage.writeTo(out);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }
    
    public static String generateInputSOAPMessageText(WSDLHelper helper, String requestMethod,
            String operationName, Map<String, List<String>> paramMap) throws WSDLException, SOAPException, IOException {
        try {
            BindingImpl binding = helper.getBindingByRequestMethod(requestMethod);
            SoapBuilder builder = helper.getWsdl().binding().name(binding.getQName()).find();
            SoapOperation operation = builder.operation().name(operationName).find();
            String message = builder.buildInputMessage(operation);
            return message;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            //fall back to old mechanism to generate input message in case of exception
            BindingOperationImpl bindingOperation = helper.getBindingOperationsByRequestMethod(requestMethod)
                .stream()
                .filter(bo -> bo.getName().equals(operationName))
                .findFirst()
                .get();
            String operationURI = helper.getOperationURI(bindingOperation, requestMethod);

            String namespaceURI = helper.getService().getQName().getNamespaceURI();
            SOAPMessage soapMessage = helper.generateInputSOAPMessage(requestMethod, namespaceURI, operationURI,
                    operationName, paramMap);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapMessage.writeTo(out);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    public Request<?> sendSOAPRequest(String requestMethod, String operationName,
            List<WebElementPropertyEntity> httpHeaders, String soapMessage)
            throws WSDLException {
        Optional<BindingOperationImpl> bindingOperationOpt = getBindingOperationsByRequestMethod(requestMethod).stream()
                .filter(bo -> bo.getName().equals(operationName))
                .findFirst();
        if (!bindingOperationOpt.isPresent()) {
            return null;
        }

        BindingOperationImpl bindingOperation = bindingOperationOpt.get();
        String soapAction = getOperationURI(bindingOperation, requestMethod);

        // Endpoint and soapAction should use by SOAP method. But the message should be loaded from GET or POST
        String endPoint = getPortAddressLocation(requestMethod);
        Post post = Http.post(endPoint, soapMessage.getBytes(), 10000, 10000);

        httpHeaders.forEach(header -> post.header(header.getName(), header.getValue()));

        switch (requestMethod) {
            case SOAP:
                post.header(HTTP_HEADER_CONTENT_TYPE, SOAP_HEADER_CONTENT_TYPE_VALUE);
                post.header(SOAP_HEADER_ACTION, soapAction);
                break;

            case SOAP12:
                post.header(HTTP_HEADER_CONTENT_TYPE, SOAP12_HEADER_CONTENT_TYPE_VALUE);
                post.header(SOAP_HEADER_ACTION, soapAction);
                break;

            default:
                post.header(HTTP_HEADER_CONTENT_TYPE, SOAP_HEADER_CONTENT_TYPE_VALUE);
                break;
        }

        return post;
    }
}
