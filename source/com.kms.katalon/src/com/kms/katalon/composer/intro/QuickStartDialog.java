package com.kms.katalon.composer.intro;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;

public class QuickStartDialog extends Dialog {
    
    public static final int NEW_PROJECT = 1000;
    
    public static final int OPEN_PROJECT = 1001;

    private Browser browser;

    public QuickStartDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 960;
        gdBody.heightHint = 539;
        body.setLayoutData(gdBody);
        GridLayout glBody = new GridLayout(1, false);
        glBody.marginHeight = 0;
        glBody.marginWidth = 0;
        body.setLayout(glBody);

        browser = new Browser(body, SWT.NONE);
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        browser.setJavascriptEnabled(true);
        browser.setUrl(getHTMLTemplateFileUrl().toString());

        registerBrowserListeners();
        return body;
    }

    private void registerBrowserListeners() {
        browser.addLocationListener(new LocationListener() {

            @Override
            public void changing(LocationEvent event) {
            }

            @Override
            public void changed(LocationEvent event) {
                new BrowserFunction(browser, "handleCreatingProject") {
                    @Override
                    public Object function(Object[] objects) {
                        QuickStartDialog.this.handleCreatingProject();
                        return null;
                    }
                };

                new BrowserFunction(browser, "handleOpeningProject") {
                    @Override
                    public Object function(Object[] objects) {
                        QuickStartDialog.this.handleOpeningProject();
                        return null;
                    }
                };
            }

        });
    }

    private void handleCreatingProject() {
        setReturnCode(NEW_PROJECT);
        close();
    }

    private void handleOpeningProject() {
        setReturnCode(OPEN_PROJECT);
        close();
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        Composite parent = getShell().getParent();

        Monitor monitor = getShell().getDisplay().getPrimaryMonitor();
        if (parent != null) {
            monitor = parent.getMonitor();
        }

        Rectangle monitorBounds = monitor.getClientArea();
        Point centerPoint;
        if (parent != null) {
            centerPoint = Geometry.centerPoint(parent.getBounds());
        } else {
            centerPoint = Geometry.centerPoint(monitorBounds);
        }

        return new Point(centerPoint.x - (initialSize.x / 2), Math.max(monitorBounds.y,
                Math.min(centerPoint.y - (initialSize.y / 2), monitorBounds.y + monitorBounds.height - initialSize.y)));
    }

    private URL getHTMLTemplateFileUrl() {
        URL templateFileUrl = null;
        try {
            Bundle bundle = FrameworkUtil.getBundle(UserFeedbackDialog.class);
            Path templateFolderPath = new Path("/resources/quickstart");

            URL templateFolderUrl = FileLocator.find(bundle, templateFolderPath, null);
            File templateFolder = FileUtils.toFile(FileLocator.toFileURL(templateFolderUrl));

            File templateFile = FileUtils.getFile(templateFolder, "quickstart_template.html");

            templateFileUrl = templateFile.toURI().toURL();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }

        return templateFileUrl;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.DIA_TITLE_QUICKSTART);
    }
}
