package com.kms.katalon.core.webui.driver.existings;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

public class ExistingRemoteWebDriver extends RemoteWebDriver implements IExistingRemoteWebDriver {

    private static final int REMOTE_BROWSER_CONNECT_TIMEOUT = 60000;

    private String oldSessionId;

    public ExistingRemoteWebDriver(URL remoteAddress, String oldSessionId) throws ConnectException {
        super(remoteAddress, new DesiredCapabilities());
        waitForRemoteBrowserReady(remoteAddress);
        setSessionId(oldSessionId);
        this.oldSessionId = oldSessionId;
        startClient();
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
    protected void startClient() {
        if (this.oldSessionId == null) {
            return;
        }
        super.startClient();
    }

    @Override
    protected Response execute(String driverCommand, Map<String, ?> parameters) {
        if (DriverCommand.NEW_SESSION.equals(driverCommand)) {
            return createResponseForNewSession(oldSessionId);
        }
        return super.execute(driverCommand, parameters);
    }

    private static void waitForRemoteBrowserReady(URL url) throws ConnectException {
        long waitUntil = System.currentTimeMillis() + REMOTE_BROWSER_CONNECT_TIMEOUT;
        boolean connectable = false;
        while (!connectable) {
            try {
                url.openConnection().connect();
                connectable = true;
            } catch (IOException e) {
                // Cannot connect yet.
            }

            if (waitUntil < System.currentTimeMillis()) {
                // This exception is meant for devs to see, not user so no need to externalize string
                throw new ConnectException(
                        String.format("Unable to connect to browser on host %s and port %s after %s seconds.", //$NON-NLS-1$
                                url.getHost(), url.getPort(), REMOTE_BROWSER_CONNECT_TIMEOUT));
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }
}
