package com.kms.katalon.core.webservice.wsdl.support.wsdl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import com.kms.katalon.constants.SystemProperties;
import com.kms.katalon.core.util.internal.PathUtil;

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
            File file = new File(getAbsolutePath(wsdlLocation));
            return file.toURI().toURL().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getAbsolutePath(String filePath) {
        String projectLocation = System.getProperty(SystemProperties.PROJECT_LOCATION);
        String absolutePath = PathUtil.relativeToAbsolutePath(wsdlLocation, projectLocation);
        return absolutePath;
    }

    @Override
    protected InputStream load(String url) {
        try {
            FileInputStream inputStream = new FileInputStream(getAbsolutePath(url));
            return inputStream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
