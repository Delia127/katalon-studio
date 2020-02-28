package com.kms.katalon.selenium.driver;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

public class CEdgeChromiumDriver extends RemoteWebDriver implements IDelayableDriver {

    private int actionDelayInMiliseconds;

    public CEdgeChromiumDriver(EdgeDriverService service, Capabilities capabilities, int actionDelay) {
        super(service.getUrl(), capabilities);
        this.actionDelayInMiliseconds = actionDelay * 1000;
    }

    @Override
    protected Response execute(String driverCommand, Map<String, ?> parameters) {
        return super.execute(driverCommand, parameters);
    }

    @Override
    public int getActionDelay() {
        return actionDelayInMiliseconds;
    }
}
