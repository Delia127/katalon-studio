package com.kms.katalon.composer.webservice.response.body;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;

import com.kms.katalon.composer.webservice.parts.VerificationScriptEventHandler;
import com.kms.katalon.composer.webservice.util.JsonPathUtils;
import com.kms.katalon.composer.webservice.util.XPathUtils;

import com.kms.katalon.composer.components.impl.constants.TextContentType;
import com.kms.katalon.composer.components.impl.editors.MirrorEditor;
import com.kms.katalon.composer.components.impl.editors.MirrorEditor.EditorChangeListener;
import com.kms.katalon.composer.components.impl.handler.DocumentReadyHandler;

import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.entity.webservice.TextBodyContent;

public class PrettyEditor extends Composite implements ResponseBodyEditor, EditorChangeListener {

    private TextBodyContent textBodyContent;

    private MirrorEditor mirrorEditor;

    Composite tbBodyType;

    private Map<String, Button> TEXT_MODE_SELECTION_BUTTONS = new HashMap<>();

    private static final int JSON_TOKEN_SIZE = 4;
    
    private static final int XML_TOKEN_SIZE = 5;

    private static final int JSON_PROP_VALUE_TOKEN_IDX = 3;

    private static final int XML_PROP_VALUE_TOKEN_IDX = 4;

    private Button chckWrapLine;

    // List of TextContentType by name
    private static final String[] TEXT_MODE_NAMES;

    static {
        TEXT_MODE_NAMES = TextContentType.getTextValues();
    }

    /**
     * Key: mode name: {@link TextContentType#JSON} or {@link TextContentType#XPATH}
     * Value: a HashMap<Integer, String> that indices JSON PATH, XPATH of a line number on editor.
     */
    private Map<TextContentType, Map<Integer, String>> lineIndexing = new HashMap<>();

    private TextContentType preferedContentType;


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
        mirrorEditor.addEventListener(this);
        
        Label lblInstruction = new Label(this, SWT.WRAP);
        lblInstruction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        lblInstruction.setText(ComposerWebserviceMessageConstants.HOW_TO_ADD_JSON_AND_XML_PATH_TO_VERIFICATION_SCRIPT);
        
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
                            String mode = textContentType.getText();
                            mirrorEditor.changeMode(mode);
                            if (textBodyContent != null) {
                                textBodyContent.setContentType(textContentType.getContentType());
                                mirrorEditor.setHintText("");
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

        String text = textBodyContent.getText();
        mirrorEditor.setText(text);
        updateRadioStatus();
        mirrorEditor.beautify();

        switch (preferedContentType) {
            case JSON:
                lineIndexing.put(preferedContentType, JsonPathUtils.evaluateJsonProperty(text));
                break;
            case XML:
                lineIndexing.put(preferedContentType, XPathUtils.evaluateXmlProperty(text));
                break;
            default:
                break;
        }
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
        preferedContentType = TextContentType.evaluateContentType(textBodyContent.getContentType());
        Button selectionButton = TEXT_MODE_SELECTION_BUTTONS.get(preferedContentType.getText());
        TEXT_MODE_SELECTION_BUTTONS.entrySet().forEach(e -> e.getValue().setSelection(false));
        if (selectionButton != null) {
            selectionButton.setSelection(true);
            selectionButton.notifyListeners(SWT.Selection, new Event());
            mirrorEditor.changeMode(preferedContentType.getText());
        }
    }

    @Override
    public void handleEditorEvent(String event, Object[] objects) {
        switch (event) {
            case "handleMouseOverChanged": {
                if (objects == null || objects.length != 1 || !(objects[0] instanceof String)) {
                    return;
                }
                if (!preferedContentType.getContentType().equals(textBodyContent.getContentType())) {
                    return;
                }
                JsonObject positionJsonObject = new Gson().fromJson((String) objects[0], JsonObject.class);
                int line = positionJsonObject.get("line").getAsInt();
                Map<Integer, String> jsonPathCollection = lineIndexing.get(preferedContentType);
                if (jsonPathCollection.containsKey(line)) {
                    mirrorEditor.setHintText(jsonPathCollection.get(line));
                } else {
                    mirrorEditor.setHintText("");
                }
                break;
            }
            case "handleGenerateVerificationEvent": {
                if (objects == null || objects.length != 2) {
                    return;
                }
                if (!preferedContentType.getContentType().equals(textBodyContent.getContentType())) {
                    return;
                }
                int line = ((Number) objects[0]).intValue();
                Map<Integer, String> propertyCollection = lineIndexing.get(preferedContentType);
                if (!propertyCollection.containsKey(line)) {
                    return;
                }
                String propertyName = propertyCollection.get(line);
                String propertyValue = "";
                JsonArray lineTokensJson = new Gson().fromJson((String) objects[1], JsonArray.class);
                switch (preferedContentType) {
                    case JSON:
                        if (lineTokensJson.size() >= JSON_TOKEN_SIZE) {
                            JsonObject jsObject = lineTokensJson.get(JSON_PROP_VALUE_TOKEN_IDX)
                                    .getAsJsonObject();
                            if (!jsObject.get("type").isJsonNull()) {
                                propertyValue = jsObject.get("string").getAsString();
                            }
                        }
                        break;
                    case XML:
                        if (lineTokensJson.size() == XML_TOKEN_SIZE) {
                            propertyValue = "'" + lineTokensJson.get(XML_PROP_VALUE_TOKEN_IDX)
                                    .getAsJsonObject()
                                    .get("string")
                                    .getAsString().replace("'", "\\'") + "'";
                        }
                        break;
                    default:
                        break;
                }
                
                String script = String.format("WS.verifyElementPropertyValue(response, '%s', %s)", 
                        propertyName.replace("'", "\\'"), StringUtils.defaultIfEmpty(propertyValue, "''"));
                Iterator<VerificationScriptEventHandler> iterator = eventHanders.iterator();
                while (iterator.hasNext()) {
                    iterator.next().insertScript(script);
                }
                break;
            }
        }
    }

    private Collection<VerificationScriptEventHandler> eventHanders = new LinkedList<>();

    public void addHandler(VerificationScriptEventHandler handler) {
        eventHanders.add(handler);
    }
}
