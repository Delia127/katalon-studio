package com.kms.katalon.core.webui.driver;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

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
public class SmartWaitWebEventListener implements WebDriverEventListener {

    @Override
    public void afterAlertAccept(WebDriver arg0) {
    }

    @Override
    public void afterAlertDismiss(WebDriver arg0) {
    }

    @Override
    public void afterChangeValueOf(WebElement arg0, WebDriver arg1, CharSequence[] arg2) {

    }

    @Override
    public void afterClickOn(WebElement arg0, WebDriver arg1) {
    }

    @Override
    public void afterFindBy(By arg0, WebElement arg1, WebDriver arg2) {
    }

    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> arg0, X arg1) {

    }

    @Override
    public void afterGetText(WebElement arg0, WebDriver arg1, String arg2) {
    }

    @Override
    public void afterNavigateBack(WebDriver arg0) {
    }

    @Override
    public void afterNavigateForward(WebDriver arg0) {
    }

    @Override
    public void afterNavigateRefresh(WebDriver arg0) {
    }

    @Override
    public void afterNavigateTo(String arg0, WebDriver arg1) {
    }

    @Override
    public void afterScript(String arg0, WebDriver arg1) {
    }

    @Override
    public void afterSwitchToWindow(String arg0, WebDriver arg1) {
    }

    @Override
    public void beforeAlertAccept(WebDriver arg0) {
    }

    @Override
    public void beforeAlertDismiss(WebDriver arg0) {
    }

    @Override
    public void beforeChangeValueOf(WebElement arg0, WebDriver arg1, CharSequence[] arg2) {
    }

    @Override
    public void beforeClickOn(WebElement arg0, WebDriver arg1) {
        doSmartWait();
    }

    @Override
    public void beforeFindBy(By arg0, WebElement arg1, WebDriver arg2) {
        doSmartWait();
    }

    @Override
    public <X> void beforeGetScreenshotAs(OutputType<X> arg0) {
    }

    @Override
    public void beforeGetText(WebElement arg0, WebDriver arg1) {
        doSmartWait();
    }

    @Override
    public void beforeNavigateBack(WebDriver arg0) {
    }

    @Override
    public void beforeNavigateForward(WebDriver arg0) {
    }

    @Override
    public void beforeNavigateRefresh(WebDriver arg0) {
    }

    @Override
    public void beforeNavigateTo(String arg0, WebDriver arg1) {
    }

    @Override
    public void beforeScript(String arg0, WebDriver arg1) {
    }

    @Override
    public void beforeSwitchToWindow(String arg0, WebDriver arg1) {
    }

    @Override
    public void onException(Throwable arg0, WebDriver arg1) {
    }

    public void doSmartWait() {
        boolean smartWaitEnabled = (boolean) Optional
                .ofNullable(RunConfiguration.getExecutionProperties().get("smartWaitEnabled")).orElse(false);
        if (smartWaitEnabled) {
            SmartWaitHelper.doSmartWait();
        }
    }

}
