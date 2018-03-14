package com.kms.katalon.composer.webservice.response.body;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.webservice.editor.MirrorEditor;
import com.kms.katalon.core.testobject.ResponseObject;

public class RawEditor extends Composite implements ResponseBodyEditor {

    private MirrorEditor mirrorEditor;
    private ResponseObject responseObject;
    
    RawEditor(Composite parent, int style) {
        super(parent, style);
        mirrorEditor = new MirrorEditor(parent, style);
    }

    @Override
    public void updateContentBody(ResponseObject responseOb) {
        this.responseObject = responseOb;
        mirrorEditor.setText(responseOb.getResponseText());
    }
    
    @Override
    public void switchModeContentBody(ResponseObject responseOb) {
        if (responseOb == null) {
            updateContentBody(responseOb);
        } else {
            this.responseObject = responseOb;
        }
    }

    @Override
    public String getContentType() {
        return responseObject.getContentType();
    }

}
