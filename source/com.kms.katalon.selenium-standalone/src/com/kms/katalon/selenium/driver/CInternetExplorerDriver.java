package com.kms.katalon.selenium.driver;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.Response;

public class CInternetExplorerDriver extends InternetExplorerDriver implements IDelayableDriver {
    private int actionDelayInMiliseconds;

    public CInternetExplorerDriver(InternetExplorerDriverService service, Capabilities capabilities, int actionDelay) {
        super(service, new InternetExplorerOptions(capabilities));
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
