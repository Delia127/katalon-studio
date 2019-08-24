package com.kms.katalon.core.webui.driver;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.webui.common.internal.SmartWait;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;

/**
 * Event listener that triggers smart wait functionality on
 * <ul>
 * <li>before finding element</li>
 * </ul>
 *
 */
public class SmartWaitWebEventListener extends AbstractWebDriverEventListener {

    @Override
    public void beforeFindBy(By arg0, WebElement arg1, WebDriver arg2) {
        doSmartWait();
    }

    /**
     * Check and invoke smart wait functionality if enabled (either globally or via
     * {@link WebUiBuiltInKeywords#enableSmartWait()} keyword)
     */
    public void doSmartWait() {
        boolean smartWaitEnabled = (boolean) Optional
                .ofNullable(RunConfiguration.getExecutionProperties().get("smartWaitEnabled")).orElse(false);
        if (smartWaitEnabled) {
            SmartWait.doSmartWait();
        }
    }

}
