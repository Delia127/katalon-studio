package com.kms.katalon.ansiconsole.listeners;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;

public class SmartXPathPatternMatchListenerDelegate implements IPatternMatchListenerDelegate {

    private static final String LOG_PREFIX = "[SMART_XPATH] Screenshot: ";
    
    private static final String SCREENSHOT_EXTENSION = ".png";
    
    private TextConsole console;
    
    @Override
    public void connect(TextConsole console) {
        this.console = console;
    }

    @Override
    public void disconnect() {
        console = null;
    }

    @Override
    public void matchFound(PatternMatchEvent event) {
        try {
            String log = console.getDocument().get(event.getOffset(), event.getLength());
            String screenshotPath = log.substring(LOG_PREFIX.length() - 1, 
                log.lastIndexOf(SCREENSHOT_EXTENSION))  + SCREENSHOT_EXTENSION;
            screenshotPath = screenshotPath.trim();
            int screenshotPathOffset = event.getOffset() + LOG_PREFIX.length();
            
            IHyperlink hyperlink = makeHyperlink(screenshotPath);
            console.addHyperlink(hyperlink, screenshotPathOffset, screenshotPath.length());
        } catch (BadLocationException ignored) {
        }
    }
    
    private static IHyperlink makeHyperlink(String screenshotPath) {
        return new IHyperlink() {

            @Override
            public void linkExited() {
            }

            @Override
            public void linkEntered() {
            }

            @Override
            public void linkActivated() {
                try {
                    if (Desktop.isDesktopSupported()) {
                        File screenshotFile = new File(screenshotPath);
                        Desktop.getDesktop().open(screenshotFile.getParentFile());
                    }
                } catch (IOException e) {
                    MultiStatusErrorDialog.showErrorDialog(e,
                        "Unable to open screenshot",
                        e.getClass().getSimpleName());
                }
            }
        };
    }

}
