package com.kms.katalon.core.webservice.definition;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DefinitionFileLoader extends AbstractDefinitionLoader {

    public DefinitionFileLoader(String location) {
        this.definitionLocation = location;
    }

    @Override
    public InputStream load() {
        try {
            try {
                URL url = new URL(definitionLocation);
                return new FileInputStream(url.toURI().getPath());
            } catch (MalformedURLException e) {
                return new FileInputStream(definitionLocation);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
