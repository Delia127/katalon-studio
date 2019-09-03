package com.kms.katalon.selenium.driver;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

public class CSafariDriver extends SafariDriver implements IDelayableDriver {
    private int actionDelayInMiliseconds;

    public CSafariDriver(Capabilities capabilities, int actionDelay) {
        super(SafariOptions.fromCapabilities(capabilities));
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
