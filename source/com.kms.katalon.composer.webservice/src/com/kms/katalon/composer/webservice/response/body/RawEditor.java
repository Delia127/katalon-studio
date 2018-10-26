package com.kms.katalon.composer.webservice.response.body;

import java.io.IOException;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


import com.kms.katalon.composer.components.impl.editors.MirrorEditor;

import com.kms.katalon.core.testobject.ResponseObject;

public class RawEditor extends Composite implements ResponseBodyEditor {

    private MirrorEditor mirrorEditor;

    private ResponseObject responseObject;

    public RawEditor(Composite parent, int style) {
        super(parent, style);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);

        mirrorEditor = new MirrorEditor(this, style);
        mirrorEditor.setEditable(false);
    }

    @Override
    public void setContentBody(ResponseObject responseOb) throws IOException {
        if (responseOb != null) {
            this.responseObject = responseOb;
            mirrorEditor.setText(responseOb.getResponseText());
        }
    }

    @Override
    public void switchModeContentBody(ResponseObject responseOb) throws IOException {
        if (responseObject == null) {
            setContentBody(responseOb);
        } else {
            this.responseObject = responseOb;
        }
    }

    @Override
    public String getContentType() {
        return responseObject.getContentType();
    }

}
