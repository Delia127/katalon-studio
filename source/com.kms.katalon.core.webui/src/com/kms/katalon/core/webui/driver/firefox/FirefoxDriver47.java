package com.kms.katalon.core.webui.driver.firefox;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.ExtensionConnection;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.internal.Lock;
import org.openqa.selenium.remote.DesiredCapabilities;

public class FirefoxDriver47 extends FirefoxDriver {

    public FirefoxDriver47() {
        super();
    }

    public FirefoxDriver47(DesiredCapabilities desiredCapabilities) {
        super(desiredCapabilities);
    }

    @Override
    protected ExtensionConnection connectTo(FirefoxBinary binary, FirefoxProfile profile, String host) {
        Lock lock = obtainLock(profile);
        try {
            FirefoxBinary bin = binary == null ? new FirefoxBinary() : binary;
            return new NewProfileExtensionConnection47(lock, bin, profile, host);
        } catch (Exception e) {
            throw new WebDriverException(e);
        }
    }
}
