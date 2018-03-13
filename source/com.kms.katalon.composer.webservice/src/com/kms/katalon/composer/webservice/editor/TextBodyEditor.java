package com.kms.katalon.composer.webservice.editor;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
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

import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.webservice.TextBodyContent;

public class TextBodyEditor extends HttpBodyEditor {

    public enum TextContentType {
        TEXT("Text", "text/plain"),
        JSON("JSON", "application/json"),
        XML("XML", "application/xml"),
        HTML("HTML", "text/html"),
        JAVASCRIPT("JavaScript", "application/javascript");

        private String text;

        private String contentType;

        private TextContentType(String text, String contentType) {
            this.text = text;
            this.contentType = contentType;
        }

        public String getText() {
            return text;
        }

        public String getContentType() {
            return contentType;
        }

        public static String[] getTextValues() {
            return Arrays.asList(values()).stream().map(t -> t.getText()).toArray(String[]::new);
        }

        public static TextContentType evaluateContentType(String contentType) {
            switch (contentType) {
                case "application/json":
                case "application/ld+json":
                    return TextContentType.JSON;
                case "application/javascript":
                case "application/ecmascript":
                    return TextContentType.JAVASCRIPT;
                case "application/xml":
                case "application/atom+xml":
                case "application/soap+xml":
                    return TextContentType.XML;
                case "text/html":
                case "application/xhtml+xml":
                    return TextContentType.HTML;
                default:
                    return TextContentType.TEXT;
            }
        }
    }

    private TextBodyContent textBodyContent;

    private MirrorEditor mirrorEditor;

    private File templateFile;

    Composite tbBodyType;

    // A collection of mirror modes for some text types
    private static final Map<String, String> TEXT_MODE_COLLECTION;

    // List of TextContentType by name
    private static final String[] TEXT_MODE_NAMES;

    private Map<String, Button> TEXT_MODE_SELECTION_BUTTONS = new HashMap<>();

    private Button chckWrapLine;

    static {
        TEXT_MODE_COLLECTION = new HashMap<>();
        TEXT_MODE_COLLECTION.put(TextContentType.TEXT.getText(), "text/plain");
        TEXT_MODE_COLLECTION.put(TextContentType.JSON.getText(), "application/ld+json");
        TEXT_MODE_COLLECTION.put(TextContentType.XML.getText(), "application/xml");
        TEXT_MODE_COLLECTION.put(TextContentType.HTML.getText(), "text/html");
        TEXT_MODE_COLLECTION.put(TextContentType.JAVASCRIPT.getText(), "application/javascript");

        TEXT_MODE_NAMES = TextContentType.getTextValues();
    }

    public TextBodyEditor(Composite parent, int style, boolean showTextType) {
        super(parent, style);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);

        mirrorEditor = new MirrorEditor(this, SWT.NONE);
        mirrorEditor.registerDocumentHandler(new DocumentReadyHandler() {

            @Override
            public void onDocumentReady() {
                mirrorEditor.setText(textBodyContent.getText());
                TextContentType preferedContentType = TextContentType
                        .evaluateContentType(textBodyContent.getContentType());
                Button selectionButton = TEXT_MODE_SELECTION_BUTTONS.get(preferedContentType.getText());
                TEXT_MODE_SELECTION_BUTTONS.entrySet().forEach(e -> e.getValue().setSelection(false));
                if (selectionButton != null) {
                    selectionButton.setSelection(true);
                    changeMode(preferedContentType.getText());
                    mirrorEditor.wrapLine(chckWrapLine.getSelection());
                }
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
            if (showTextType || (!showTextType && textContentType != TextContentType.TEXT)) {
            Button btnTextMode = new Button(tbBodyType, SWT.RADIO);
            btnTextMode.setText(textContentType.getText());
            TEXT_MODE_SELECTION_BUTTONS.put(textContentType.getText(), btnTextMode);

            btnTextMode.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    changeMode(textContentType.getText());
                    if (textBodyContent != null) {
                        textBodyContent.setContentType(textContentType.getContentType());
                    }
                    TextBodyEditor.this.setContentTypeUpdated(true);

                    TextBodyEditor.this.notifyListeners(SWT.Modify, new Event());
                }
            });}
        });

        chckWrapLine = new Button(bottomComposite, SWT.CHECK);
        chckWrapLine.setText(ComposerWebserviceMessageConstants.PA_LBL_WRAP_LINE);
        chckWrapLine.setSelection(true);

    }

    private void handleControlModifyListener() {
        addDisposeListener(e -> {
            if (templateFile != null && templateFile.exists()) {
                templateFile.delete();
            }
        });

        chckWrapLine.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                mirrorEditor.wrapLine(chckWrapLine.getSelection());
            }
        });
    }

    private void changeMode(String text) {
        String textType = TEXT_MODE_COLLECTION.keySet()
                .stream()
                .filter(key -> text.toLowerCase().startsWith(key.toLowerCase()))
                .findFirst()
                .orElse(TextContentType.TEXT.getText());

        String mode = TEXT_MODE_COLLECTION.get(textType);
        mirrorEditor.evaluate(MessageFormat.format("changeMode(editor, \"{0}\");", mode));
    }

    @Override
    public String getContentType() {
        return textBodyContent.getContentType();
    }

    @Override
    public String getContentData() {
        textBodyContent.setText((String) mirrorEditor.evaluate("return editor.getValue();"));
        return JsonUtil.toJson(textBodyContent);
    }

    @Override
    public void setInput(String rawBodyContentData) {
        if (textBodyContent != null) {
            updateBodyContent(rawBodyContentData);
            return;
        }
        // Request object - content type was included in rawBodyContentData.
        if (StringUtils.isEmpty(rawBodyContentData)) {
            textBodyContent = new TextBodyContent();
        } else {
            textBodyContent = JsonUtil.fromJson(rawBodyContentData, TextBodyContent.class);
        }
        // Response object. todo remove to child.
        mirrorEditor.sleepForLoadingDocumentReady();
    }

    private void updateBodyContent(String rawBodyContentData) {
        this.textBodyContent = JsonUtil.fromJson(rawBodyContentData, TextBodyContent.class);
        Button selectionButton = TEXT_MODE_SELECTION_BUTTONS.get(TextContentType.evaluateContentType (textBodyContent.getContentType()).getText());
        TEXT_MODE_SELECTION_BUTTONS.entrySet().forEach(e -> e.getValue().setSelection(false));
        if (selectionButton != null) { 
            selectionButton.setSelection(true);
            selectionButton.notifyListeners(SWT.Selection, new Event());
        }
        mirrorEditor.setText(textBodyContent.getText());
        mirrorEditor.wrapLine(chckWrapLine.getSelection());
    }

}
