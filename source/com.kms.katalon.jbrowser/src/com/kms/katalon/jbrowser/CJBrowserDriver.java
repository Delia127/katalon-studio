package com.kms.katalon.jbrowser;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.Response;

import com.kms.katalon.selenium.driver.IDelayableDriver;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;

public class CJBrowserDriver extends JBrowserDriver implements IDelayableDriver {
    private int actionDelayInMiliseconds;

    public CJBrowserDriver(Capabilities capabilities, int actionDelay) {
        super(capabilities);
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
