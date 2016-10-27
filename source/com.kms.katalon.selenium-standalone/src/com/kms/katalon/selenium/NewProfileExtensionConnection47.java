package com.kms.katalon.selenium;

import java.io.File;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.FileExtension;
import org.openqa.selenium.firefox.internal.NewProfileExtensionConnection;
import org.openqa.selenium.internal.Lock;

public class NewProfileExtensionConnection47 extends NewProfileExtensionConnection {

    private static final String EXTENSION_FILE_NAME = "{5AB8F5AC-9644-11E6-89DF-D9D3C6AD2208}.xpi";

    private static final String WEB_DRIVER_PROP = "webdriver";

    private final FirefoxProfile profile;

    public NewProfileExtensionConnection47(Lock lock, FirefoxBinary binary, FirefoxProfile profile, String host)
            throws Exception {
        super(lock, binary, profile, host);
        this.profile = profile;
    }

    @Override
    protected void addWebDriverExtensionIfNeeded() {
        if (profile.containsWebDriverExtension()) {
            return;
        }
        String xpiProperty = System.getProperty(FirefoxDriver.SystemProperty.DRIVER_XPI_PROPERTY);
        if (xpiProperty != null) {
            File xpi = new File(xpiProperty);
            profile.addExtension(WEB_DRIVER_PROP, new FileExtension(xpi));
        } else {
            profile.addExtension(WEB_DRIVER_PROP, new SignedWebDriverExtension(TempClass.class, EXTENSION_FILE_NAME));
        }
    }
}
