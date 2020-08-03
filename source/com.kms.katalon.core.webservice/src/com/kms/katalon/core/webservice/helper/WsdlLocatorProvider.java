package com.kms.katalon.core.webservice.helper;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.webservice.definition.DefinitionLoader;
import com.kms.katalon.core.webservice.definition.DefinitionLoaderProvider;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.BasicWsdlDefinitionLocator;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.WsdlDefinitionLocator;

public class WsdlLocatorProvider {

    public static WsdlDefinitionLocator getLocator(String wsdlLocation) {
        return getLocator(wsdlLocation, url -> DefinitionLoaderProvider.getLoader(url));
    }

    public static WsdlDefinitionLocator getLocator(String wsdlLocation,
            Function<String, DefinitionLoader> loaderGenFunc) {
        if (StringUtils.isBlank(wsdlLocation)) {
            throw new IllegalArgumentException("WSDL location must not be null or empty.");
        }

        return new BasicWsdlDefinitionLocator(wsdlLocation, loaderGenFunc);
    }
}
