package com.kms.katalon.core.webui.driver.safari;

import java.io.IOException;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;

/**
 * This class copy code from SafariDriver class to overcome it's package-protected fields
 * This class should not be re-factor
 * TODO: This class should be change when our com.kms.katalon.selenium-standalone project is updated
 *
 */
public class CSafariDriver extends RemoteWebDriver {
    /**
     * Initializes a new SafariDriver} class with default {@link SafariOptions}.
     */
    public CSafariDriver() {
        this(new CSafariOptions());
    }

    /**
     * Converts the specified {@link DesiredCapabilities} to a {@link SafariOptions}
     * instance and initializes a new SafariDriver using these options.
     * 
     * @see SafariOptions#fromCapabilities(org.openqa.selenium.Capabilities)
     *
     * @param desiredCapabilities capabilities requested of the driver
     */
    public CSafariDriver(Capabilities desiredCapabilities) {
        this(CSafariOptions.fromCapabilities(desiredCapabilities));
    }

    /**
     * Initializes a new SafariDriver using the specified {@link SafariOptions}.
     *
     * @param safariOptions safari specific options / capabilities for the driver
     */
    public CSafariDriver(CSafariOptions safariOptions) {
        super(new CSafariDriverCommandExecutor(safariOptions), safariOptions.toCapabilities());
        setSessionId("");
    }

    @Override
    public void setFileDetector(FileDetector detector) {
        throw new WebDriverException(
                "Setting the file detector only works on remote webdriver instances obtained " + "via RemoteWebDriver");
    }

    @Override
    protected void startClient() {
        CSafariDriverCommandExecutor executor = (CSafariDriverCommandExecutor) this.getCommandExecutor();
        try {
            executor.start();
        } catch (IOException e) {
            throw new WebDriverException(e);
        }
    }

    @Override
    protected void stopClient() {
        CSafariDriverCommandExecutor executor = (CSafariDriverCommandExecutor) this.getCommandExecutor();
        executor.stop();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        // Get the screenshot as base64.
        String base64 = (String) execute(DriverCommand.SCREENSHOT).getValue();
        // ... and convert it.
        return target.convertFromBase64Png(base64);
    }
}
