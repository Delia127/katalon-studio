package com.kms.katalon.ansiconsole.listeners;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.ide.IDE;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;

public class HarLinkyConsolePatternMatchListenerDelegate implements IPatternMatchListenerDelegate {
    
    private static final String HAR = "HAR: ";
    
    private static final String HAR_EXTENSION = ".har";

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
            String harLog = console.getDocument().get(event.getOffset(), event.getLength());
            String harFilePath = harLog.substring(HAR.length() - 1, 
                    harLog.lastIndexOf(HAR_EXTENSION))  + HAR_EXTENSION;
            harFilePath = harFilePath.trim();
            int harFilePathOffset = event.getOffset() + HAR.length();
            
            IHyperlink hyperlink = makeHyperlink(harFilePath);
            console.addHyperlink(hyperlink, harFilePathOffset, harFilePath.length());
        } catch (BadLocationException ignored) {
        }
    }

    private static IHyperlink makeHyperlink(String harFilePath) {
        return new IHyperlink() {

            @Override
            public void linkExited() {
            }

            @Override
            public void linkEntered() {
            }

            @Override
            public void linkActivated() {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try
                {
                    IDE.openEditorOnFileStore(page, EFS.getStore(new File(harFilePath).toURI()));
                } catch (Exception exception) {
                    MultiStatusErrorDialog.showErrorDialog(exception,
                            "Unable to open HAR file",
                            exception.getClass().getSimpleName());
                }
            }
        };
    }
}
