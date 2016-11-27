package com.kms.katalon.execution.session;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriverException;

import com.kms.katalon.core.appium.driver.ExistingAndroidDriver;
import com.kms.katalon.core.appium.driver.ExistingIosDriver;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

import io.appium.java_client.AppiumDriver;

public class MobileExecutionSession extends ExecutionSession {
    public MobileExecutionSession(String title, String sessionId, String remoteUrl, String driverTypeName,
            String logFolderPath) {
        super(sessionId, remoteUrl, driverTypeName, logFolderPath);
        this.title = title;
    }

    @Override
    public void startWatcher() throws MalformedURLException {
        new Thread(new MobileExecutionSessionWatcher()).start();
    }

    protected class MobileExecutionSessionWatcher extends ExecutionSessionWatcher {
        private AppiumDriver<?> existingMobileDriver;

        @Override
        protected AppiumDriver<?> getExistingDriver() throws MalformedURLException {
            if (existingMobileDriver != null) {
                return existingMobileDriver;
            }
            if (MobileDriverType.ANDROID_DRIVER.toString().equals(driverTypeName)) {
                existingMobileDriver = new ExistingAndroidDriver(new URL(remoteUrl), sessionId);
            } else if (MobileDriverType.IOS_DRIVER.toString().equals(driverTypeName)) {
                existingMobileDriver = new ExistingIosDriver(new URL(remoteUrl), sessionId);
            }
            return existingMobileDriver;
        }

        @Override
        protected void checkStatusAndUpdateTitle() throws MalformedURLException, WebDriverException {
            getExistingDriver().findElementsById("id");
        }
    }
}
