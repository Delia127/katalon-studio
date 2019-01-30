package com.kms.katalon.selenium.driver;

import java.net.URL;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

public class CRemoteWebDriver extends RemoteWebDriver implements IDelayableDriver {
    private int actionDelayInMiliseconds;

    public CRemoteWebDriver(URL remoteAddress, Capabilities capabilities, int actionDelay) {
        super(remoteAddress, capabilities);
        setActionDelayInMilliseconds(actionDelay);
    }
    
    public CRemoteWebDriver(CommandExecutor executor, Capabilities capabilities, int actionDelay) {
        super(executor, capabilities);
        setActionDelayInMilliseconds(actionDelay);
    }
    
    private void setActionDelayInMilliseconds(int actionDelay) {
        this.actionDelayInMiliseconds = actionDelay * 1000;
    }

    @Override
    protected Response execute(String driverCommand, Map<String, ?> parameters) {
        delay();
        return super.execute(driverCommand, parameters);
    }

    @Override
    public int getActionDelay() {
        return actionDelayInMiliseconds;
    }
}
