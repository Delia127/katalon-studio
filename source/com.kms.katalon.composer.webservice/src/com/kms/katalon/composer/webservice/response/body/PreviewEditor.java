package com.kms.katalon.composer.webservice.response.body;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.core.testobject.ResponseObject;

public class PreviewEditor extends Composite implements ResponseBodyEditor {
    private Browser previewEditor;
    private ResponseObject responseObjects;
    
    public PreviewEditor(Composite parent, int style) {
        super(parent, style);
        previewEditor = new Browser(parent, style);
    }
    
    @Override
    public void updateContentBody(ResponseObject responseOb) {
        this.responseObjects = responseOb;
        previewEditor.setText(responseObjects.getResponseText());
        
    }

    @Override
    public void switchModeContentBody(ResponseObject responseOb) {
        if (responseOb == null) {
            updateContentBody(responseOb);
        } else {
            this.responseObjects = responseOb;
        }
    }

    @Override
    public String getContentType() {
        return responseObjects.getContentType();
    }


    
}
