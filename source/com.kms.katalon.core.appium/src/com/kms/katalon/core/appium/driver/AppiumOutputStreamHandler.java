package com.kms.katalon.core.appium.driver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

import com.kms.katalon.core.configuration.RunConfiguration;

public class AppiumOutputStreamHandler {

    private Process appiumProcess;

    private AppiumOutputStreamHandler(Process appiumProcess) {
        this.appiumProcess = appiumProcess;
    }

    public void start() {
        try {
            FileOutputStream fileOs = new FileOutputStream(RunConfiguration.getAppiumLogFilePath(), true);

            StreamHandler.create(appiumProcess.getInputStream(), Arrays.asList(fileOs, System.out)).start();
            StreamHandler.create(appiumProcess.getErrorStream(), Arrays.asList(fileOs, System.err)).start();
        } catch (FileNotFoundException ignored) {}
    }

    public static AppiumOutputStreamHandler create(Process appiumProcess) {
        return new AppiumOutputStreamHandler(appiumProcess);
    }
}
