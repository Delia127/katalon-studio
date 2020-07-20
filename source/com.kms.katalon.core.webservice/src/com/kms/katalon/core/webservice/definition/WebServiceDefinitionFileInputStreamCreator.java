package com.kms.katalon.core.webservice.definition;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class WebServiceDefinitionFileInputStreamCreator implements WebServiceDefinitionInputStreamCreator {

    @Override
    public InputStream createInputStream(String location, Map<String, Object> params) {
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
