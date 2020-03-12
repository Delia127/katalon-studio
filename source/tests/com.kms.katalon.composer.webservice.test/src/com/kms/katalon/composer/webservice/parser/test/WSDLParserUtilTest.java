package com.kms.katalon.composer.webservice.parser.test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;

import javax.wsdl.WSDLException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Assert;
import org.junit.Test;

import com.kms.katalon.composer.webservice.parser.WSDLParserUtil;
import com.kms.katalon.composer.webservice.util.WSDLHelper;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class WSDLParserUtilTest {

    private WSDLHelper wsdlHelperInstance;

    private final String actual = "http://www.dneonline.com/calculator.asmx?wsdl";

    @Test
    public void testNewWSTestObjectsFromWSDL() {
        File soapFile;
        try {
            soapFile = getExtensionsDirectory("resources/soap/calculator.wsdl");
        } catch (IOException e) {
            Assert.fail("WSDL file not found");
            return;
        }

        wsdlHelperInstance = WSDLHelper.newInstance(soapFile.getAbsolutePath(), null);

        String location;
        try {
            location = WSDLParserUtil.getLocation(wsdlHelperInstance);
        } catch (WSDLException e) {
            Assert.fail("Cannot get loaction");
            return;
        }

        assertThat("Locations are not equal", location.equals(actual));
    }

    private File getExtensionsDirectory(String path) throws IOException {
        File bundleFile = FileLocator.getBundleFile(Platform.getBundle("com.kms.katalon.composer.webservice.test"));
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + path);
        } else { // run as product
            return new File(ClassPathResolver.getConfigurationFolder(), path);
        }
    }

}
