package com.kms.katalon.execution.session;

import java.net.MalformedURLException;

import org.openqa.selenium.remote.RemoteWebDriver;

import com.kms.katalon.core.webui.driver.safari.CSafariDriver;
import com.kms.katalon.core.webui.driver.safari.CSafariOptions;

public class SafariExecutionSession extends ExecutionSession {
    private String port;

    private SafariExecutionSessionWatcher safariExecutionSessionWatcher;

    public SafariExecutionSession(String port, String sessionId, String remoteUrl, String driverTypeName,
            String logFolderPath) {
        super(sessionId, remoteUrl, driverTypeName, logFolderPath);
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    @Override
    public void startWatcher() throws MalformedURLException {
        safariExecutionSessionWatcher = new SafariExecutionSessionWatcher();
        new Thread(safariExecutionSessionWatcher).start();
    }

    @Override
    public void pause() {
        super.pause();
        safariExecutionSessionWatcher.closeDriver();
    }

    protected class SafariExecutionSessionWatcher extends ExecutionSessionWatcher {
        @Override
        protected RemoteWebDriver getExistingDriver() throws MalformedURLException {
            if (existingDriver == null) {
                CSafariOptions options = new CSafariOptions();
                options.setPort(Integer.parseInt(port));
                existingDriver = new CSafariDriver(options);
            }
            return existingDriver;
        }
        
        protected void closeDriver() {
            if (existingDriver != null) {
                existingDriver.quit();
                existingDriver = null;
            }
        }
    }
}
