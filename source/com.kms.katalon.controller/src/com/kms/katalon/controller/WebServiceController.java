package com.kms.katalon.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.net.UrlEscapers;
import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.testobject.ConditionType;
import com.kms.katalon.core.testobject.HttpBodyContent;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.testobject.TestObjectProperty;
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent;
import com.kms.katalon.core.testobject.internal.impl.HttpBodyContentReader;
import com.kms.katalon.core.util.StrSubstitutor;
import com.kms.katalon.core.webservice.common.ServiceRequestFactory;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.webservice.RequestHistoryEntity;
import com.kms.katalon.util.URLBuilder;
import com.kms.katalon.util.collections.NameValuePair;

public class WebServiceController extends EntityController {

    public static final String KATALON_VERSION_NUMBER_KEY = "katalon.versionNumber";

    private static EntityController _instance;

    private WebServiceController() {
        super();
    }

    public static WebServiceController getInstance() {
        if (_instance == null) {
            _instance = new WebServiceController();
        }
        return (WebServiceController) _instance;
    }

    public static RequestObject getRequestObject(WebServiceRequestEntity entity, String projectDir,
            Map<String, Object> variables) {
        RequestObject requestObject = new RequestObject(entity.getId());
        String serviceType = entity.getServiceType();
        requestObject.setServiceType(serviceType);
        
        requestObject.setName(entity.getName());
        StrSubstitutor substitutor = new StrSubstitutor(variables);
        
        requestObject.setConnectionTimeout(entity.getConnectionTimeout());
        requestObject.setSocketTimeout(entity.getSocketTimeout());
        requestObject.setMaxResponseSize(entity.getMaxResponseSize());
        
        if ("SOAP".equals(serviceType)) {
            requestObject.setWsdlAddress(substitutor.replace(entity.getWsdlAddress()));
            requestObject.setSoapRequestMethod(entity.getSoapRequestMethod());
            requestObject.setSoapServiceFunction(entity.getSoapServiceFunction());
            requestObject.setHttpHeaderProperties(parseProperties(entity.getHttpHeaderProperties(), substitutor));
            requestObject.setSoapBody(substitutor.replace(entity.getSoapBody()));
        } else if ("RESTful".equals(serviceType)) {
            String rawUrl = entity.getRestUrl();
            String url = buildUrlFromRaw(rawUrl, substitutor);
            requestObject.setRestUrl(url);
            requestObject.setRestRequestMethod(entity.getRestRequestMethod());
            requestObject.setRestParameters(parseProperties(entity.getRestParameters(), new StrSubstitutor()));
            requestObject
                    .setHttpHeaderProperties(parseProperties(entity.getHttpHeaderProperties(), substitutor));
            requestObject.setHttpBody(entity.getHttpBody());

            String httpBodyType = entity.getHttpBodyType();
            if (StringUtils.isBlank(httpBodyType)) {
                // migrated from 5.3.1 (KAT-3200)
                httpBodyType = "text";
                String body = entity.getHttpBody();
                HttpTextBodyContent httpBodyContent = new HttpTextBodyContent(body);
                requestObject.setBodyContent(httpBodyContent);
            } else if (isBodySupported(requestObject)) {
                String httpBodyContent = entity.getHttpBodyContent();
                HttpBodyContent bodyContent = HttpBodyContentReader.fromSource(httpBodyType, httpBodyContent,
                        projectDir, substitutor);
                requestObject.setBodyContent(bodyContent);
            }
        }
        
        requestObject.setVariables(variables);
        
        boolean followRedirects = entity.isFollowRedirects();
        requestObject.setFollowRedirects(followRedirects);
        
        return requestObject;
    }
    
    private static String buildUrlFromRaw(String rawUrl, StrSubstitutor substitutor) {
        URLBuilder urlBuilder = new URLBuilder(rawUrl);
        
        List<NameValuePair> rawQueryParams = urlBuilder.getQueryParams();

        List<NameValuePair> processedQueryParams = new ArrayList<>();

        rawQueryParams.stream()
            .forEach(p -> {
                String variableExpandedName = substitutor.replace(p.getName());
                String variableExpandedValue = substitutor.replace(p.getValue());
                String escapedName = UrlEscapers.urlFormParameterEscaper().escape(variableExpandedName);
                String escapedValue = UrlEscapers.urlFormParameterEscaper().escape(variableExpandedValue);
                processedQueryParams.add(new NameValuePair(escapedName, escapedValue));
            });
        
        urlBuilder.setParameters(processedQueryParams);
        
        String url = urlBuilder.buildString();
        url = substitutor.replace(url); //replace the last time if there is still variable expansions in other parts of the URL
        
        return url;
    }

    private static boolean isBodySupported(RequestObject requestObject) {
        String restRequestMethod = requestObject.getRestRequestMethod();
        return !("GET".contains(restRequestMethod));
    }

    private static List<TestObjectProperty> parseProperties(List<WebElementPropertyEntity> objects,
            StrSubstitutor substitutor) {
        List<TestObjectProperty> props = new ArrayList<TestObjectProperty>();
        for (WebElementPropertyEntity propertyElementObject : objects) {
            TestObjectProperty objectProperty = new TestObjectProperty();

            objectProperty.setName(substitutor.replace(propertyElementObject.getName()));
            objectProperty.setCondition(ConditionType.fromValue(propertyElementObject.getMatchCondition()));
            objectProperty.setValue(substitutor.replace(propertyElementObject.getValue()));
            objectProperty.setActive(propertyElementObject.getIsSelected());

            props.add(objectProperty);
        }
        return props;
    }

    public ResponseObject sendRequest(WebServiceRequestEntity entity, String projectDir,
            ProxyInformation proxyInformation, Map<String, Object> variables, boolean calledFromKeyword) throws Exception {
        RequestObject requestObject = getRequestObject(entity, projectDir, variables);
        return ServiceRequestFactory.getInstance(requestObject, projectDir, proxyInformation, calledFromKeyword).send(requestObject);
    }

    public List<RequestHistoryEntity> getRequestHistories() {
        return Collections.emptyList();
    }
	
	public static String extractParamFromRestUrl(String key, String restUrl) {
		// matches {key=value}
		String pattern = "(" + key + ")\\=([^&]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(restUrl);
		if (m.find()) {
			return m.group(0).split("=")[1];
		}
		return StringUtils.EMPTY;
	}
}
