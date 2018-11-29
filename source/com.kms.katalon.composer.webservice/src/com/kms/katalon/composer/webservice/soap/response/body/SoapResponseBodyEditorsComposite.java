package com.kms.katalon.composer.webservice.soap.response.body;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.constants.TextContentType;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.parts.VerificationScriptEventHandler;
import com.kms.katalon.composer.webservice.response.body.RawEditor;
import com.kms.katalon.composer.webservice.response.body.ResponseBodyEditor;
import com.kms.katalon.core.testobject.ResponseObject;

public class SoapResponseBodyEditorsComposite extends Composite {

    protected final String WS_BUNDLE_NAME = FrameworkUtil.getBundle(SoapResponseBodyEditorsComposite.class).getSymbolicName();
    
    private Map<SoapEditorMode, ResponseBodyEditor> bodyEditors = new HashMap<>();

    private Map<SoapEditorMode, Button> bodySelectionButtons = new HashMap<>();

    private Button prettyRadio;

    private Button rawRadio;

    private StackLayout slBodyContent;

    private ResponseObject responseObject;

    private SoapEditorMode selectedEditorMode;
    
    private final String PRETTY_MODE_DEFAULT_CONTENT_TYPE = TextContentType.XML.getContentType().toString();

    private final String PRETTY_MODE_DEFAULT_INITAL_MESSAGE = StringUtils.EMPTY;

    private enum SoapEditorMode {
        PRETTY, RAW
    };

    public SoapResponseBodyEditorsComposite(Composite parent, int style, VerificationScriptEventHandler eventHandler) {

        super(parent, style);
        setLayout(new GridLayout());

        Composite bodyTypeComposite = new Composite(this, SWT.NONE);
        bodyTypeComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout glBodyType = new GridLayout(2, false);
        glBodyType.marginWidth = glBodyType.marginHeight = 0;
        bodyTypeComposite.setLayout(glBodyType);

        Composite bodyContentComposite = new Composite(this, SWT.NONE);
        bodyContentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        slBodyContent = new StackLayout();
        bodyContentComposite.setLayout(slBodyContent);

        // Pretty Mode
        Composite tbBodyType = new Composite(bodyTypeComposite, SWT.NONE);
        tbBodyType.setLayout(new GridLayout(3, false));
        prettyRadio = new Button(tbBodyType, SWT.RADIO);
        prettyRadio.setText(SoapEditorMode.PRETTY.toString().toLowerCase());
        bodySelectionButtons.put(SoapEditorMode.PRETTY, prettyRadio);

        SoapPrettyEditor mirrorEditor = new SoapPrettyEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put(SoapEditorMode.PRETTY, mirrorEditor);
        mirrorEditor.addHandler(eventHandler);

        // Raw Mode
        rawRadio = new Button(tbBodyType, SWT.RADIO);
        rawRadio.setText(SoapEditorMode.RAW.toString().toLowerCase());
        bodySelectionButtons.put(SoapEditorMode.RAW, rawRadio);

        RawEditor rawEditor = new RawEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put(SoapEditorMode.RAW, rawEditor);

        handleControlModifyListeners();
        ResponseObject defaultResponseOb = new ResponseObject();
        defaultResponseOb.setContentType(PRETTY_MODE_DEFAULT_CONTENT_TYPE);
        defaultResponseOb.setResponseText(PRETTY_MODE_DEFAULT_INITAL_MESSAGE);
        setInput(defaultResponseOb);
    }

    public void setInput(ResponseObject responseOb) {
        try {
            this.responseObject = new ResponseObject();
            this.responseObject.setResponseText(responseOb.getResponseText());
            this.responseObject.setBodyContent(responseObject.getBodyContent());
            this.responseObject.setContentType(responseOb.getContentType());
            this.selectedEditorMode = SoapEditorMode.PRETTY;

            // Mark radio is selected.
            bodySelectionButtons.entrySet().forEach(e -> e.getValue().setSelection(false));
            Button selectedButton = bodySelectionButtons.get(selectedEditorMode);
            selectedButton.setSelection(true);

            // Init body content.
            for (ResponseBodyEditor childEditor : bodyEditors.values()) {
                childEditor.setContentBody(responseObject);
            }
            Composite selectedEditor = (Composite) bodyEditors.get(selectedEditorMode);
            slBodyContent.topControl = selectedEditor;
            selectedEditor.getParent().layout();
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            ErrorDialog.openError(getShell(), StringConstants.ERROR_TITLE, "There was problem while parsing the response object.",
                    new Status(Status.ERROR, WS_BUNDLE_NAME, ex.getMessage(), ex));
        }
    }

    private void handleControlModifyListeners() {
        SelectionAdapter bodyTypeSelectedListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                if (source.getSelection()) {
                    try {
                        selectedEditorMode = SoapEditorMode.valueOf(source.getText().toUpperCase());
                        ResponseBodyEditor editorComposite = bodyEditors.get(selectedEditorMode);
                        editorComposite.switchModeContentBody(responseObject);

                        slBodyContent.topControl = (Composite) editorComposite;
                        ((Composite) editorComposite).getParent().layout();
                    } catch (Exception ex) {
                        LoggerSingleton.logError(ex);
                        ErrorDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                                "There was problem while parsing the response object.",
                                new Status(Status.ERROR, WS_BUNDLE_NAME, ex.getMessage(), ex));
                    }
                }
            };
        };

        bodySelectionButtons.values().forEach(button -> {
            button.addSelectionListener(bodyTypeSelectedListener);
        });

        bodyEditors.values().forEach(editor -> {
            ((Composite) editor).addListener(SWT.Modify, event -> {
                responseObject.setContentType(editor.getContentType());
            });
        });

    }
}
