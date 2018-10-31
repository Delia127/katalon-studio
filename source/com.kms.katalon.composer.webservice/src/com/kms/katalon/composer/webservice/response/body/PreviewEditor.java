package com.kms.katalon.composer.webservice.response.body;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.testobject.ResponseObject;

public class PreviewEditor extends Composite implements ResponseBodyEditor {
    private Browser browser;

    private ResponseObject responseObjects;

    private File tempFile;

    public PreviewEditor(Composite parent, int style) {
        super(parent, style);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);
        this.setLayoutData(new GridData(GridData.FILL_BOTH));

        browser = new Browser(this, SWT.NONE);
        browser.setJavascriptEnabled(true);
        browser.setLayoutData(new GridData(GridData.FILL_BOTH));

        tempFile = new File(ProjectController.getInstance().getTempDir(),
                "editor/mirror/Temp_" + System.currentTimeMillis() + ".html");
        tempFile.getParentFile().mkdirs();
        
        this.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (tempFile != null && tempFile.exists()) {
                    FileUtils.deleteQuietly(tempFile);
                }
            }
        });
    }

    @Override
    public void setContentBody(ResponseObject responseOb) throws IOException {
        if (responseOb != null) {
            this.responseObjects = responseOb;
            FileUtils.write(tempFile, responseObjects.getResponseText());
            browser.setUrl(tempFile.toURI().toURL().toString());
        }
    }

    @Override
    public void switchModeContentBody(ResponseObject responseOb) throws IOException {
        if (this.responseObjects != responseOb) {
            setContentBody(responseOb);
        }
    }

    @Override
    public String getContentType() {
        return responseObjects.getContentType();
    }

}
