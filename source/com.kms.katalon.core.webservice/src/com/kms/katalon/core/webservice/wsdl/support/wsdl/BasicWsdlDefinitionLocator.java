package com.kms.katalon.core.webservice.wsdl.support.wsdl;

import java.io.File;
import java.io.InputStream;
import java.util.function.Function;

import org.xml.sax.InputSource;

import com.kms.katalon.core.webservice.constants.RequestHeaderConstants;
import com.kms.katalon.core.webservice.definition.DefinitionLoader;
import com.kms.katalon.util.Tools;

public class BasicWsdlDefinitionLocator implements WsdlDefinitionLocator {

    private String last;

    private String wsdlLocation;

    private Function<String, DefinitionLoader> loaderGenFunc;

    public BasicWsdlDefinitionLocator(String wsdlLocation, Function<String, DefinitionLoader> loaderGenFunc) {
        this.wsdlLocation = wsdlLocation;
        this.loaderGenFunc = loaderGenFunc;
    }

    public String getBaseURI() {
        return wsdlLocation;
    }

    public InputSource getBaseInputSource() {
        try {
            InputStream is = load(wsdlLocation);
            return new InputSource(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    private InputStream load(String location) {
        DefinitionLoader definitionLoader = loaderGenFunc.apply(location);
        return definitionLoader.load();
    }

    private boolean isAbsoluteUrl(String url) {
        if (isWeb()) {
            return url.startsWith(RequestHeaderConstants.HTTP) || url.startsWith(RequestHeaderConstants.HTTPS);
        } else {
            File wsdlFile = new File(url);
            return wsdlFile.isAbsolute();
        }
    }

    private boolean isWeb() {
        return wsdlLocation.startsWith(RequestHeaderConstants.HTTP)
                || wsdlLocation.startsWith(RequestHeaderConstants.HTTPS);
    }

    public String getLatestImportURI() {
        String result = last == null ? wsdlLocation : last;
        return result;
    }

    public void close() {
    }

    public String getWsdlLocation() {
        return wsdlLocation;
    }
}
