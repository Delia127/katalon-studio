package com.kms.katalon.core.webservice.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.http.HTTPOperation;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;

import com.ibm.wsdl.BindingOperationImpl;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.ServiceImpl;
import com.ibm.wsdl.extensions.http.HTTPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12BindingImpl;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.core.webservice.constants.CoreWebserviceMessageConstants;
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;
import com.kms.katalon.core.webservice.exception.WSConnectionTimeoutException;
import com.kms.katalon.core.webservice.exception.WSSocketTimeoutException;
import com.kms.katalon.core.webservice.exception.WebServiceException;
import com.kms.katalon.core.webservice.helper.WebServiceCommonHelper;
import com.kms.katalon.core.webservice.util.WSDLUtil;
import com.kms.katalon.util.Tools;

public class SoapClient extends BasicRequestor {

    private static final String SSL = RequestHeaderConstants.SSL;

    private static final String HTTPS = RequestHeaderConstants.HTTPS;

    private static final String SOAP = RequestHeaderConstants.SOAP;

    private static final String SOAP12 = RequestHeaderConstants.SOAP12;

    private static final String SOAP_ACTION = RequestHeaderConstants.SOAP_ACTION;

    private static final String CONTENT_TYPE = RequestHeaderConstants.CONTENT_TYPE;

    private static final String TEXT_XML_CHARSET_UTF_8 = RequestHeaderConstants.CONTENT_TYPE_TEXT_XML_UTF_8;

    private static final String APPLICATION_XML = RequestHeaderConstants.CONTENT_TYPE_APPLICATION_XML;
    
    private String serviceName;

    private String protocol = SOAP; // Default is SOAP

    private String endPoint;

    private String actionUri; // Can be SOAPAction, HTTP location URL

    private RequestObject requestObject;

    public SoapClient(String projectDir, ProxyInformation proxyInformation) {
        super(projectDir, proxyInformation);
    }

    private void parseWsdl() throws WebServiceException {
        try {
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setFeature("javax.wsdl.importDocuments", true);
            
            Definition wsdlDefinition = reader.readWSDL(new CustomWSDLLocator(requestObject));
            
            lookForService(wsdlDefinition);
        } catch (Exception e) {
            throw new WebServiceException(e);
        }
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

            Object objBinding = getBindingObject(port.getBinding());
            String proc = "";
            BindingOperationImpl operation = (BindingOperationImpl) port.getBinding()
                    .getBindingOperation(requestObject.getSoapServiceFunction(), null, null);
            if (operation == null) {
                throw new WebServiceException(CoreWebserviceMessageConstants.MSG_NO_SERVICE_OPERATION);
            }

            if (objBinding != null && objBinding instanceof SOAPBindingImpl) {
                proc = SOAP;
                SOAPAddress soapAddress = WSDLUtil.getExtensiblityElement(port.getExtensibilityElements(), SOAPAddress.class);
                endPoint = soapAddress.getLocationURI();
                SOAPOperation soapOperation = WSDLUtil.getExtensiblityElement(operation.getExtensibilityElements(), SOAPOperation.class);
                actionUri = soapOperation.getSoapActionURI();
            } else if (objBinding != null && objBinding instanceof SOAP12BindingImpl) {
                proc = SOAP12;
                SOAP12Address soap12Address = WSDLUtil.getExtensiblityElement(port.getExtensibilityElements(), SOAP12Address.class);
                endPoint = soap12Address.getLocationURI();
                SOAP12Operation soap12Operation = WSDLUtil.getExtensiblityElement(operation.getExtensibilityElements(), SOAP12Operation.class);
                actionUri = soap12Operation.getSoapActionURI();
            } else if (objBinding != null && objBinding instanceof HTTPBindingImpl) {
                proc = ((HTTPBindingImpl) objBinding).getVerb();
                HTTPAddress httpAddress = WSDLUtil.getExtensiblityElement(port.getExtensibilityElements(), HTTPAddress.class);
                endPoint = httpAddress.getLocationURI();
                HTTPOperation httpOperation = WSDLUtil.getExtensiblityElement(port.getExtensibilityElements(), HTTPOperation.class);
                actionUri = httpOperation.getLocationURI();
            }

            if (protocol.equals(proc)) {
                break;
            }
        }
    }
    
    private Object getBindingObject(Binding binding) {
        List<?> extensibilityElements = binding.getExtensibilityElements();
        Object objBinding = WSDLUtil.getExtensiblityElement(extensibilityElements, SOAPBinding.class);
        if (objBinding == null) {
            objBinding = WSDLUtil.getExtensiblityElement(extensibilityElements, SOAP12Binding.class);
        }
        if (objBinding == null) {
            objBinding = WSDLUtil.getExtensiblityElement(extensibilityElements, HTTPBinding.class);
        }
        return objBinding;
    }

    @Override
    public ResponseObject send(RequestObject request)
            throws Exception {
        protocol = request.getSoapRequestMethod();
        HttpClientBuilder clientBuilder = HttpClients.custom();
        
        if (!request.isFollowRedirects()) {
            clientBuilder.disableRedirectHandling();
        } else {
            clientBuilder.setRedirectStrategy(new WebServiceRedirectStrategy());
        }
        
        configTimeout(clientBuilder, request);
        
        clientBuilder.setConnectionManager(connectionManager);
        clientBuilder.setConnectionManagerShared(true);
        
        this.requestObject = request;
        parseWsdl();
       
        ProxyInformation proxyInfo = request.getProxy() != null ? request.getProxy() : proxyInformation;
        URL url = new URL(endPoint);
        Proxy proxy = proxyInfo == null ? Proxy.NO_PROXY : ProxyUtil.getProxy(proxyInfo, url);
        if (!Proxy.NO_PROXY.equals(proxy) || proxy.type() != Proxy.Type.DIRECT) {
            configureProxy(clientBuilder, proxyInfo);
        }
        
//        HttpURLConnection con = (HttpURLConnection) oURL.openConnection(proxy);
        if (StringUtils.defaultString(endPoint).toLowerCase().startsWith(HTTPS)) {
            //this will be overridden by setting connection manager for clientBuilder
            clientBuilder.setSSLHostnameVerifier(getHostnameVerifier());
        }
        HttpPost post = new HttpPost(endPoint);
        post.addHeader(CONTENT_TYPE, TEXT_XML_CHARSET_UTF_8);
        post.addHeader(SOAP_ACTION, actionUri);
        
        setHttpConnectionHeaders(post, request);
        
        clientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
        // copied from source
                Args.notNull(response, "HTTP response");
                final HeaderElementIterator it = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    final HeaderElement he = it.nextElement();
                    final String param = he.getName();
                    final String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (final NumberFormatException ignore) {}
                    }
                }
                // If the server indicates no timeout, then let it be 1ms so that connection is not kept alive
                // indefinitely
                return 1;
            }
        });

        ByteArrayEntity entity = new ByteArrayEntity(request.getSoapBody().getBytes(StandardCharsets.UTF_8));
        entity.setChunked(false);
        post.setEntity(entity);
        
        CloseableHttpClient httpClient = clientBuilder.build();
        
        long startTime = System.currentTimeMillis();

        CloseableHttpResponse response;
        try {
            response = httpClient.execute(post, getHttpContext());
        } catch (ConnectTimeoutException exception) {
            throw new WSConnectionTimeoutException(exception);
        } catch (SocketTimeoutException exception) {
            throw new WSSocketTimeoutException(exception);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        long waitingTime = System.currentTimeMillis() - startTime;
        
        long headerLength = WebServiceCommonHelper.calculateHeaderLength(response);
        long contentDownloadTime = 0L;
        String responseBody = StringUtils.EMPTY;

        long bodyLength = 0L;
        
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            bodyLength = responseEntity.getContentLength();
            startTime = System.currentTimeMillis();
            try {
                responseBody = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            } catch (Exception e) {
                responseBody = ExceptionUtils.getFullStackTrace(e);
            }
            contentDownloadTime = System.currentTimeMillis() - startTime;
        }

        ResponseObject responseObject = new ResponseObject(responseBody);
        
        // SOAP is HTTP-XML protocol

        responseObject.setContentType(APPLICATION_XML);
        responseObject.setHeaderFields(getResponseHeaderFields(response));
        responseObject.setStatusCode(statusCode);
        responseObject.setResponseBodySize(bodyLength);
        responseObject.setResponseHeaderSize(headerLength);
        responseObject.setWaitingTime(waitingTime);
        responseObject.setContentDownloadTime(contentDownloadTime);
        
        setBodyContent(response, responseBody, responseObject);
        
        return responseObject;
    }
    
    

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    // Refer to
    // https://github.com/SmartBear/soapui/blob/fe41852da853365c8be1ab331f713462695fd519/soapui/src/main/java/com/eviware/soapui/impl/wsdl/support/wsdl/AbstractWsdlDefinitionLoader.java
    private class CustomWSDLLocator implements WSDLLocator {

        private RequestObject request;

        private String last;

        public CustomWSDLLocator(RequestObject request) {
            this.request = request;
        }

        @Override
        public InputSource getBaseInputSource() {
            try {
                InputStream is = load(requestObject.getWsdlAddress());
                return new InputSource(is);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getBaseURI() {
            return requestObject.getWsdlAddress();
        }

        @Override
        public InputSource getImportInputSource(String parent, String imp) {
            if (isAbsoluteUrl(imp)) {
                last = imp;
            } else {
                last = Tools.joinRelativeUrl(parent, imp);
            }
            try {
                InputStream input = load(last);
                return input == null ? null : new InputSource(input);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private boolean isAbsoluteUrl(String url) {
            url = url.toLowerCase();
            return url.startsWith("http:") || url.startsWith("https:") || url.startsWith("file:");
        }

        private InputStream load(String url) throws GeneralSecurityException, IOException, WebServiceException, URISyntaxException {
            HttpClientBuilder clientBuilder = HttpClients.custom();
            
            clientBuilder.disableRedirectHandling();

            clientBuilder.setConnectionManager(connectionManager);
            clientBuilder.setConnectionManagerShared(true);
            
            boolean isHttps = isHttps(url);
            if (isHttps) {
                SSLContext sc = SSLContext.getInstance(SSL);
                sc.init(null, getTrustManagers(), new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            }

            ProxyInformation proxyInfo = request.getProxy() != null ? request.getProxy() : proxyInformation;
            Proxy proxy = proxyInfo == null ? Proxy.NO_PROXY : ProxyUtil.getProxy(proxyInfo, new URL(url));
            if (!Proxy.NO_PROXY.equals(proxy) || proxy.type() != Proxy.Type.DIRECT) {
                configureProxy(clientBuilder, proxyInfo);
            }
            
            if (StringUtils.defaultString(url).toLowerCase().startsWith(HTTPS)) {
                //this will be overridden by setting connection manager for clientBuilder
                clientBuilder.setSSLHostnameVerifier(getHostnameVerifier());
            }
            
            clientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
                @Override
                public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
            // copied from source
                    Args.notNull(response, "HTTP response");
                    final HeaderElementIterator it = new BasicHeaderElementIterator(
                            response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                    while (it.hasNext()) {
                        final HeaderElement he = it.nextElement();
                        final String param = he.getName();
                        final String value = he.getValue();
                        if (value != null && param.equalsIgnoreCase("timeout")) {
                            try {
                                return Long.parseLong(value) * 1000;
                            } catch (final NumberFormatException ignore) {}
                        }
                    }
                    // If the server indicates no timeout, then let it be 1ms so that connection is not kept alive
                    // indefinitely
                    return 1;
                }
            });
            
            HttpGet get = new HttpGet(url);

            setHttpConnectionHeaders(get, requestObject);

            CloseableHttpClient httpClient = clientBuilder.build();
            CloseableHttpResponse response = httpClient.execute(get, getHttpContext());
            HttpEntity responseEntity = response.getEntity();
            InputStream is = null;
            if (responseEntity != null) {
                is = responseEntity.getContent();
            }
            
            IOUtils.closeQuietly(httpClient);
            
            return is;
        }

        private boolean isHttps(String url) {
            url = url.toLowerCase();
            return url.startsWith("https");
        }

        @Override
        public String getLatestImportURI() {
            String result = last == null ? request.getWsdlAddress() : last;
            return result;
        }

        @Override
        public void close() {
        }
    }
}
