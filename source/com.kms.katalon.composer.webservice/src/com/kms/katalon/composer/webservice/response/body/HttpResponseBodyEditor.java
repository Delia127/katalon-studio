package com.kms.katalon.composer.webservice.response.body;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.webservice.editor.MirrorEditor;
import com.kms.katalon.composer.webservice.editor.TextBodyEditor;
import com.kms.katalon.composer.webservice.editor.TextBodyEditor.TextContentType;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.webservice.TextBodyContent;

public class HttpResponseBodyEditor extends Composite {

    private Map<EditorMode, Composite> bodyEditors = new HashMap<>();

    private Map<EditorMode, Button> bodySelectionButtons = new HashMap<>();

    private Button prettyRadio;

    private Button rawRadio;

    private Button previewRadio;

    private MirrorEditor rawEditor;

    private StackLayout slBodyContent;

    private String bodyContent;

    private EditorMode selectedEditorMode;
    
    private String contentType;

    private Browser previewEditor;

    private enum EditorMode {
        PRETTY, RAW, PREVIEW
    };

    public HttpResponseBodyEditor(Composite parent, int style) {

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
        prettyRadio.setText(EditorMode.PRETTY.toString().toLowerCase());
        bodySelectionButtons.put(EditorMode.PRETTY, prettyRadio);

        TextBodyEditor textBodyEditor = new TextBodyEditor(bodyContentComposite, SWT.NONE, false);
        bodyEditors.put(EditorMode.PRETTY, textBodyEditor);

        // Raw Mode
        rawRadio = new Button(tbBodyType, SWT.RADIO);
        rawRadio.setText(EditorMode.RAW.toString().toLowerCase());
        bodySelectionButtons.put(EditorMode.RAW, rawRadio);

        rawEditor = new MirrorEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put(EditorMode.RAW, rawEditor);

        // Preview Mode
        previewRadio = new Button(tbBodyType, SWT.RADIO);
        previewRadio.setText(EditorMode.PREVIEW.toString().toLowerCase());
        bodySelectionButtons.put(EditorMode.PREVIEW, previewRadio);

        previewEditor = new Browser(bodyContentComposite, SWT.NONE);
        bodyEditors.put(EditorMode.PREVIEW, previewEditor);

        handleControlModifyListeners();
    }

    public void setInput(String responseBodyContent, String contentType) {
        this.bodyContent = responseBodyContent;
        this.selectedEditorMode = detectEditorMode(contentType);
        Button selectedButton = bodySelectionButtons.get(selectedEditorMode);
        
        bodySelectionButtons.entrySet().forEach(e -> e.getValue().setSelection(false));
        selectedButton.setSelection(true);
        selectedButton.notifyListeners(SWT.Selection, new Event());
    }
    
    
    private EditorMode detectEditorMode(String contentType) {
        this.contentType = StringUtils.substringBefore(contentType, ";");
        if (StringUtils.isNotEmpty(contentType)) {
            if (contentType.startsWith(TextContentType.XML.getContentType())
                    || contentType.startsWith(TextContentType.JAVASCRIPT.getContentType())
                    || contentType.startsWith(TextContentType.JSON.getContentType())
                    || contentType.startsWith(TextContentType.HTML.getContentType())) {
                return EditorMode.PRETTY;
            }
        }
        return EditorMode.RAW;
    }

    private void handleControlModifyListeners() {
        SelectionAdapter bodyTypeSelectedListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                if (source.getSelection()) {
                    selectedEditorMode = EditorMode.valueOf(source.getText().toUpperCase());
                    Composite editorComposite = bodyEditors.get(selectedEditorMode);

                    if (StringUtils.isNotEmpty(bodyContent)) {
                        switch (selectedEditorMode) {
                            case PRETTY:
                                TextBodyContent textBodyContent = new TextBodyContent();
                                textBodyContent.setText(bodyContent);
                                textBodyContent.setContentType(contentType);
                                ((TextBodyEditor) editorComposite).setInput(JsonUtil.toJson(textBodyContent));
                                break;
                            case RAW:
                                ((MirrorEditor) editorComposite).setText(bodyContent);
                                break;
                            case PREVIEW:
                                ((Browser) editorComposite).setText(bodyContent);
                            default:
                                break;
                        }
                    }
                    slBodyContent.topControl = editorComposite;
                    editorComposite.getParent().layout();
                }
            }
        };

        bodySelectionButtons.values().forEach(button -> {
            button.addSelectionListener(bodyTypeSelectedListener);
        });

        bodyEditors.values().forEach(editor -> {
            if (editor instanceof TextBodyEditor) {
                editor.addListener(SWT.Modify, event -> {
                    contentType = ((TextBodyEditor) editor).getContentType();
                });
            }
        });

    }
}
