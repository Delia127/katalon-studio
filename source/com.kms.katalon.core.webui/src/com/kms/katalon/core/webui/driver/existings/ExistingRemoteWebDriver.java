package com.kms.katalon.core.webui.driver.existings;

import java.net.URL;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

public class ExistingRemoteWebDriver extends RemoteWebDriver implements IExistingRemoteWebDriver {
    private String oldSessionId;

    public ExistingRemoteWebDriver(URL remoteAddress, String oldSessionId) {
        super(remoteAddress, new DesiredCapabilities());
        setSessionId(oldSessionId);
        this.oldSessionId = oldSessionId;
        startSession(new DesiredCapabilities());
    }

    public ExistingRemoteWebDriver(String oldSessionId, CommandExecutor executor, Capabilities desiredCapabilities) {
        super(executor, desiredCapabilities);
        this.oldSessionId = oldSessionId;
    }

    @Override
    protected void startSession(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        if (this.oldSessionId == null) {
            return;
        }
        super.startSession(desiredCapabilities, requiredCapabilities);
    }

    @Override
    protected Response execute(String driverCommand, Map<String, ?> parameters) {
        if (DriverCommand.NEW_SESSION.equals(driverCommand)) {
            return createResponseForNewSession(oldSessionId);
        }
        return super.execute(driverCommand, parameters);
    }
}
