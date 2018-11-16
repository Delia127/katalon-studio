package com.kms.katalon.composer.components.impl.components;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;

public class SettingOptionsThread extends Thread {
    private Browser browser;

    private String[] commands;

    private boolean documentReady;

    public SettingOptionsThread(Browser browser, String... commands) {
        this.browser = browser;
        this.commands = commands;

        browser.addProgressListener(new ProgressListener() {

            @Override
            public void completed(ProgressEvent event) {
                documentReady = true;
            }

            @Override
            public void changed(ProgressEvent event) {
            }
        });
    }

    public void run() {
        while (!documentReady) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ignored) {}
        }

        UISynchronizeService.syncExec(() -> {
			try {
				executeCommand();
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
		});                

    }

    private void executeCommand() throws Exception{
        for (String command : commands) {
            browser.evaluate(command);
        }
    }
}
