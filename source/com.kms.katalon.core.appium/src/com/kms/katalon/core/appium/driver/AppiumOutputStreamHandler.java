package com.kms.katalon.core.appium.driver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import com.kms.katalon.core.configuration.RunConfiguration;

public class AppiumOutputStreamHandler {

    private Process appiumProcess;

    private AppiumOutputStreamHandler(Process appiumProcess) {
        this.appiumProcess = appiumProcess;
    }

    public void start() {
        try {
            PrintStream filePrintStream = new PrintStream(new FileOutputStream(RunConfiguration.getAppiumLogFilePath(), true));

            StreamHandler.create(appiumProcess.getInputStream(), Arrays.asList(filePrintStream, System.out)).start();
            StreamHandler.create(appiumProcess.getErrorStream(), Arrays.asList(filePrintStream, System.err)).start();
        } catch (FileNotFoundException ignored) {}
    }

    public static AppiumOutputStreamHandler create(Process appiumProcess) {
        return new AppiumOutputStreamHandler(appiumProcess);
    }
}
