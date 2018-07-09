package com.kms.katalon.composer.webservice.response.body;

import java.io.IOException;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.core.testobject.ResponseObject;

public class PreviewEditor extends Composite implements ResponseBodyEditor {
    private Browser browser;

    private ResponseObject responseObjects;

    public PreviewEditor(Composite parent, int style) {
        super(parent, style);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);
        this.setLayoutData(new GridData(GridData.FILL_BOTH));

        browser = new Browser(this, style);
        browser.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    @Override
    public void setContentBody(ResponseObject responseOb) throws IOException {
        if (responseOb != null) {
            this.responseObjects = responseOb;
            browser.setText(responseObjects.getResponseText());
        }
    }

    @Override
    public void switchModeContentBody(ResponseObject responseOb) throws IOException {
        if (responseObjects == null) {
            setContentBody(responseOb);
        } else {
            this.responseObjects = responseOb;
        }
    }

    @Override
    public String getContentType() {
        return responseObjects.getContentType();
    }

}
