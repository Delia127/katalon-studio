package com.kms.katalon.core.webservice.wsdl.support.wsdl;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wsdl.BindingOperationImpl;
import com.kms.katalon.constants.SystemProperties;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;
import com.kms.katalon.core.webservice.helper.SafeHelper;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;

public class WsdlImporter {

    private WsdlParser wsdlParser;

    private WsdlDefinitionLocator wsdlLocator;

    public WsdlImporter(WsdlDefinitionLocator wsdlLocator) {
        this.wsdlLocator = wsdlLocator;
        this.wsdlParser = new WsdlParser(wsdlLocator);
    }

    public List<WebServiceRequestEntity> getImportedEtities(String requestMethod) throws Exception {
        List<WebServiceRequestEntity> imports = new ArrayList<>();
        List<String> operations = wsdlParser.getOperationNamesByRequestMethod(requestMethod);
        for (Object operationObject : SafeHelper.safeList(operations)) {
            if (operationObject != null) {
                String operation = (String) operationObject;
                WebServiceRequestEntity newEntity = getImportedEntity(requestMethod, operation, false);
                imports.add(newEntity);
            }
        }
        return imports;
    }

    public WebServiceRequestEntity getImportedEntity(String method, String operation, boolean isDraft)
            throws Exception {

        WebServiceRequestEntity entity;
        if (!isDraft) {
            entity = new WebServiceRequestEntity();
        } else {
            entity = new DraftWebServiceRequestEntity();
        }

        entity.setElementGuidId(Util.generateGuid());
        entity.setName(operation);
        entity.setSoapRequestMethod(method);
        entity.setSoapServiceFunction(operation);
        entity.setKatalonVersion(System.getProperty(SystemProperties.KATALON_VERSION));
        entity.setUseServiceInfoFromWsdl(false);

        String serviceEndpoint = wsdlParser.getPortAddressLocation(method);
        entity.setSoapServiceEndpoint(serviceEndpoint);

        List<WebElementPropertyEntity> headers = new ArrayList<>();

        BindingOperationImpl bindingOperation = wsdlParser.getBindingOperationByRequestMethodAndName(method, operation);
        String soapAction = wsdlParser.getOperationURI(bindingOperation, method);
        if (WebServiceRequestEntity.SOAP.equals(method) && soapAction != null) {
            WebElementPropertyEntity soapActionHeader = new WebElementPropertyEntity();
            soapActionHeader.setName(RequestHeaderConstants.SOAP_ACTION);
            soapActionHeader.setValue(soapAction);
            headers.add(soapActionHeader);
        }

        WebElementPropertyEntity contentTypeHeader = new WebElementPropertyEntity();
        contentTypeHeader.setName(RequestHeaderConstants.CONTENT_TYPE);
        contentTypeHeader.setValue(RequestHeaderConstants.CONTENT_TYPE_TEXT_XML_UTF_8);
        headers.add(contentTypeHeader);

        entity.setHttpHeaderProperties(headers);

        String requestMessage = wsdlParser.generateInputSOAPMessage(method, operation);
        entity.setSoapBody(requestMessage != null ? requestMessage : "");

        String wsdlAddress = wsdlLocator.getWsdlLocation();
        String projectLocation = System.getProperty(SystemProperties.PROJECT_LOCATION);
        if (isFile(wsdlAddress) && wsdlAddress.startsWith(projectLocation)) {
            wsdlAddress = PathUtil.absoluteToRelativePath(wsdlAddress, projectLocation);
        }
        entity.setWsdlAddress(wsdlAddress);

        return entity;
    }

    private static boolean isFile(String url) {
        return !isWebUrl(url);
    }

    private static boolean isWebUrl(String url) {
        return url.startsWith(RequestHeaderConstants.HTTP) || url.startsWith(RequestHeaderConstants.HTTPS);
    }

    public interface WebServiceRequestEntityCreator {
        WebServiceRequestEntity create();
    }
}
