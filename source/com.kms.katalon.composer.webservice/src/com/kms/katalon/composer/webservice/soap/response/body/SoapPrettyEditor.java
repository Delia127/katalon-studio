package com.kms.katalon.composer.webservice.soap.response.body;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.TextContentType;
import com.kms.katalon.composer.webservice.editor.DocumentReadyHandler;
import com.kms.katalon.composer.webservice.editor.MirrorEditor;
import com.kms.katalon.composer.webservice.response.body.ResponseBodyEditor;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.entity.webservice.TextBodyContent;

public class SoapPrettyEditor extends Composite implements ResponseBodyEditor {

    private TextBodyContent textBodyContent;

    private MirrorEditor mirrorEditor;

    // List of TextContentType by name
    private static final String[] TEXT_MODE_NAMES;

    static {
        TEXT_MODE_NAMES = TextContentType.getTextValues();
    }

    private Button chckWrapLine;

    public SoapPrettyEditor(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);

        mirrorEditor = new MirrorEditor(this, SWT.NONE);
        mirrorEditor.setEditable(false);
        mirrorEditor.registerDocumentHandler(new DocumentReadyHandler() {

            @Override
            public void onDocumentReady() {
                handleControlModifyListener();
                mirrorEditor.changeMode(TextContentType.XML.getText());
                mirrorEditor.beautify();
            }
        });
        Composite bottomComposite = new Composite(this, SWT.NONE);
        GridLayout bottomLayout = new GridLayout(2, false);
        bottomLayout.marginWidth = bottomLayout.marginHeight = 0;
        bottomLayout.marginBottom = 10;
        bottomComposite.setLayout(bottomLayout);
        bottomComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        chckWrapLine = new Button(bottomComposite, SWT.CHECK);
        chckWrapLine.setText(ComposerWebserviceMessageConstants.PA_LBL_WRAP_LINE);
        chckWrapLine.setSelection(true);

    }

    private void handleControlModifyListener() {
        chckWrapLine.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                mirrorEditor.wrapLine(chckWrapLine.getSelection());
            }
        });
    }

    @Override
    public String getContentType() {
        return textBodyContent.getContentType();
    }

    @Override
    public void setContentBody(ResponseObject responseObject) throws IOException {
        textBodyContent = new TextBodyContent();
        textBodyContent.setText(responseObject.getResponseText());

        textBodyContent.setContentType(responseObject.getContentType());
        mirrorEditor.setText(textBodyContent.getText());
        mirrorEditor.changeMode(TextContentType.XML.getText());
        mirrorEditor.beautify();
    }

    @Override
    public void switchModeContentBody(ResponseObject responseObject) throws IOException {
        if (responseObject != null) {
            if (textBodyContent == null) {
                setContentBody(responseObject);
            } else {
                textBodyContent.setContentType(responseObject.getContentType());
            }
        }
    }

}
