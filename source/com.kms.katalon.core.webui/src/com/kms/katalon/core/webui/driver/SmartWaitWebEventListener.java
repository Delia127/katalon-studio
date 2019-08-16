package com.kms.katalon.core.webui.driver;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.webui.common.internal.SmartWaitHelper;

/**
 * Event listener that triggers smart waiting on
 * <ul>
 * <li>before click</li>
 * <li>before find element</li>
 * <li>before get text</li>
 * </ul>
 * 
 * @author thanhto
 *
 */
public class SmartWaitWebEventListener extends AbstractWebDriverEventListener {

    @Override
    public void beforeClickOn(WebElement arg0, WebDriver arg1) {
        doSmartWait();
    }

    @Override
    public void beforeFindBy(By arg0, WebElement arg1, WebDriver arg2) {
        doSmartWait();
    }

    @Override
    public void beforeGetText(WebElement arg0, WebDriver arg1) {
        doSmartWait();
    }

    public void doSmartWait() {
        boolean smartWaitEnabled = (boolean) Optional
                .ofNullable(RunConfiguration.getExecutionProperties().get("smartWaitEnabled")).orElse(false);
        if (smartWaitEnabled) {
            SmartWaitHelper.doSmartWait();
        }
    }

}
