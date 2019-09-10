package com.kms.katalon.selenium.driver;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.Response;

public class CFirefoxDriver extends FirefoxDriver implements IDelayableDriver {
    private int actionDelayInMiliseconds;
    private GeckoDriverService geckoDriverService;
    
    public CFirefoxDriver(GeckoDriverService driverService, Capabilities capabilities) {
        super(driverService, new FirefoxOptions(capabilities));
        this.setGeckoDriverService(driverService);
    }

    public CFirefoxDriver(Capabilities capabilities, int actionDelay) {
        super(new FirefoxOptions(capabilities));
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

    public GeckoDriverService getGeckoDriverService() {
        return geckoDriverService;
    }

    private void setGeckoDriverService(GeckoDriverService geckoDriverService) {
        this.geckoDriverService = geckoDriverService;
    }
}
