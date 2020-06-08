package com.kms.katalon.core.webservice.wsdl.support.wsdl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class WsdlDefinitionFileLocator extends BaseWsdlDefinitionLocator {

    public WsdlDefinitionFileLocator(String wsdlLocation, Map<String, Object> params) {
        this.wsdlLocation = wsdlLocation;
    }

    @Override
    protected boolean isAbsoluteUrl(String url) {
        File wsdlFile = new File(url);
        return wsdlFile.isAbsolute();
    }

    @Override
    public String getBaseURI() {
        try {
            File file = new File(wsdlLocation);
            return file.toURI().toURL().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected InputStream load(String location) {
        try {
            try {
                URL url = new URL(location);
                return new FileInputStream(url.toURI().getPath());
            } catch (MalformedURLException e) {
                return new FileInputStream(location);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
