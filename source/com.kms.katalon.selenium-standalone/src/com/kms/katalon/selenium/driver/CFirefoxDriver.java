package com.kms.katalon.selenium.driver;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.Response;

public class CFirefoxDriver extends FirefoxDriver implements IDelayableDriver {
    private int actionDelayInMiliseconds;

    public CFirefoxDriver(Capabilities capabilities, int actionDelay) {
        super(new FirefoxOptions(capabilities));
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
