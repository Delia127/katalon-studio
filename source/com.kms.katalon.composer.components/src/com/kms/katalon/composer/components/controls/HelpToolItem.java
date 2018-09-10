package com.kms.katalon.composer.components.controls;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.constants.ComposerComponentsMessageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.tracking.service.Trackings;

public class HelpToolItem extends ToolItem {
    private String documentationLink;

    public HelpToolItem(ToolBar parent, String documentationLink) {
        this(parent, documentationLink, "");
    }
    
    public HelpToolItem(ToolBar parent, String documentationLink, String label) {
        super(parent, SWT.PUSH);
        this.documentationLink = documentationLink;
        setImage(ImageManager.getImage(IImageKeys.HELP_16));
        setToolTipText(ComposerComponentsMessageConstants.TOOLTIP_HELP_WITH_DOCUMENTATION);
        setText(label);
        addSelectionListener(getSelectionListener());
    }

    protected SelectionListener getSelectionListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openBrowserToLink(getDocumentationUrl());
            }
        };
    }

    protected void openBrowserToLink(String url) {
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        try {
            Desktop.getDesktop().browse(new URI(url));
            Trackings.trackOpenHelp(url);
        } catch (IOException | URISyntaxException exception) {
            LoggerSingleton.logError(exception);
        }
    }

    private String getDocumentationUrl() {
        return documentationLink;
    }
    
    @Override
    protected void checkSubclass() {
        // Override this to subclass
    }

}
