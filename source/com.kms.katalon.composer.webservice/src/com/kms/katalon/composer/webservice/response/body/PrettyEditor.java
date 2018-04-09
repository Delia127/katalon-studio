package com.kms.katalon.composer.webservice.response.body;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.webservice.components.MirrorEditor;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.TextContentType;
import com.kms.katalon.composer.webservice.editor.DocumentReadyHandler;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.entity.webservice.TextBodyContent;

public class PrettyEditor extends Composite implements ResponseBodyEditor {

    private TextBodyContent textBodyContent;

    private MirrorEditor mirrorEditor;

    Composite tbBodyType;

    private Map<String, Button> TEXT_MODE_SELECTION_BUTTONS = new HashMap<>();

    // List of TextContentType by name
    private static final String[] TEXT_MODE_NAMES;

    static {
        TEXT_MODE_NAMES = TextContentType.getTextValues();
    }

    private Button chckWrapLine;

    public PrettyEditor(Composite parent, int style) {
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
            }
        });
        Composite bottomComposite = new Composite(this, SWT.NONE);
        GridLayout bottomLayout = new GridLayout(2, false);
        bottomLayout.marginWidth = bottomLayout.marginHeight = 0;
        bottomComposite.setLayout(bottomLayout);
        bottomComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        tbBodyType = new Composite(bottomComposite, SWT.NONE);
        tbBodyType.setLayout(new GridLayout(TEXT_MODE_NAMES.length, false));

        Arrays.asList(TextContentType.values()).forEach(textContentType -> {
            if (textContentType != TextContentType.TEXT) {
                Button btnTextMode = new Button(tbBodyType, SWT.RADIO);
                btnTextMode.setText(textContentType.getText());
                TEXT_MODE_SELECTION_BUTTONS.put(textContentType.getText(), btnTextMode);

                btnTextMode.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Button source = (Button) e.getSource();
                        if (source.getSelection()) {
                            mirrorEditor.changeMode(textContentType.getText());
                            if (textBodyContent != null) {
                                textBodyContent.setContentType(textContentType.getContentType());
                            }
                            PrettyEditor.this.notifyListeners(SWT.Modify, new Event());
                        }
                    }
                });
            }
        });

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
        updateRadioStatus();
        mirrorEditor.beautify();
    }

    @Override
    public void switchModeContentBody(ResponseObject responseObject) throws IOException {
        if (responseObject != null) {
            if (textBodyContent == null) {
                setContentBody(responseObject);
            } else {
                textBodyContent.setContentType(responseObject.getContentType());
                updateRadioStatus();
            }
        }
    }

    private void updateRadioStatus() {
        TextContentType preferedContentType = TextContentType.evaluateContentType(textBodyContent.getContentType());
        Button selectionButton = TEXT_MODE_SELECTION_BUTTONS.get(preferedContentType.getText());
        TEXT_MODE_SELECTION_BUTTONS.entrySet().forEach(e -> e.getValue().setSelection(false));
        if (selectionButton != null) {
            selectionButton.setSelection(true);
            selectionButton.notifyListeners(SWT.Selection, new Event());
            mirrorEditor.changeMode(preferedContentType.getText());
        }
    }

}
