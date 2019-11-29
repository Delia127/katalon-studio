package com.kms.katalon.composer.webservice.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.constants.TextContentType;
import com.kms.katalon.composer.components.impl.editors.MirrorEditor;
import com.kms.katalon.composer.components.impl.handler.DocumentReadyHandler;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.parts.SoapServicePart;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class SoapRequestMessageEditor extends Composite {

    private SoapServicePart servicePart;

    private MirrorEditor mirrorEditor;

    private Button chckWrapLine;

    private WebServiceRequestEntity requestEntity;

    public SoapRequestMessageEditor(Composite parent, int style, SoapServicePart servicePart) {
        super(parent, style);
        this.servicePart = (SoapServicePart) servicePart;
        setLayout(new GridLayout());

        Composite bodyContentComposite = new Composite(this, SWT.NONE);
        bodyContentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        mirrorEditor = new MirrorEditor(bodyContentComposite, SWT.NONE);
        mirrorEditor.registerDocumentHandler(new DocumentReadyHandler() {

            @Override
            public void onDocumentReady() {
                mirrorEditor.changeMode(TextContentType.XML.getText());

                handleControlModifyListeners();
            }
        });
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginTop = 5;
        bodyContentComposite.setLayout(gridLayout);

        chckWrapLine = new Button(bodyContentComposite, SWT.CHECK);
        chckWrapLine.setText(ComposerWebserviceMessageConstants.PA_LBL_WRAP_LINE);
        chckWrapLine.setSelection(true);
    }

    public void setInput(WebServiceRequestEntity requestEntity) {
        this.requestEntity = requestEntity;

        if (requestEntity != null) {
            mirrorEditor.setText(requestEntity.getSoapBody());
        }
    }

    private void handleControlModifyListeners() {
        chckWrapLine.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                mirrorEditor.wrapLine(chckWrapLine.getSelection());
            }
        });

        mirrorEditor.addListener(SWT.Modify, event -> {
            WebServiceRequestEntity requestBodySoap = (WebServiceRequestEntity) servicePart.getOriginalWsObject()
                    .clone();
            if (requestBodySoap.getSoapBody().equals(mirrorEditor.getText())) {
                servicePart.setDirty(false);
            } else {
                servicePart.setDirty(true);
            }
        });
    }

    public String getHttpBodyContent() {
        return mirrorEditor.getText();
    }

}
