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

public class HttpResponseBodyEditor extends Composite {

    private Map<EditorType, Composite> bodyEditors = new HashMap<>();

    private Map<EditorType, Button> bodySelectionButtons = new HashMap<>();

    private Button prettyRadio;

    private Button rawRadio;

    private Button previewRadio;

    private MirrorEditor rawEditor;

    private StackLayout slBodyContent;

    private String bodyContent;

    private EditorType selectedBodyType;

    private Browser previewEditor;

    private enum EditorType {
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
        prettyRadio.setText(EditorType.PRETTY.toString().toLowerCase());
        bodySelectionButtons.put(EditorType.PRETTY, prettyRadio);

        TextBodyEditor textBodyEditor = new TextBodyEditor(bodyContentComposite, SWT.NONE);
        textBodyEditor.setContentTypeVisible(TextContentType.TEXT, false);
        bodyEditors.put(EditorType.PRETTY, textBodyEditor);

        // Raw Mode
        rawRadio = new Button(tbBodyType, SWT.RADIO);
        rawRadio.setText(EditorType.RAW.toString().toLowerCase());
        bodySelectionButtons.put(EditorType.RAW, rawRadio);

        rawEditor = new MirrorEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put(EditorType.RAW, rawEditor);

        // Preview Mode
        previewRadio = new Button(tbBodyType, SWT.RADIO);
        previewRadio.setText(EditorType.PREVIEW.toString().toLowerCase());
        bodySelectionButtons.put(EditorType.PREVIEW, previewRadio);

        previewEditor = new Browser(bodyContentComposite, SWT.NONE);
        bodyEditors.put(EditorType.PREVIEW, previewEditor);

        handleControlModifyListeners();
    }

    public void setInput(String bodyContent) {
        this.bodyContent = bodyContent;
        selectedBodyType = EditorType.PRETTY;
        Button selectedButton = bodySelectionButtons.get(selectedBodyType);
        selectedButton.setSelection(true);
        selectedButton.notifyListeners(SWT.Selection, new Event());
    }

    private void handleControlModifyListeners() {
        SelectionAdapter bodyTypeSelectedListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                selectedBodyType = EditorType.valueOf(source.getText().toUpperCase());
                Composite editorComposite = bodyEditors.get(selectedBodyType);

                if (StringUtils.isNotEmpty(bodyContent)) {
                    switch (selectedBodyType) {
                        case PRETTY:
                            ((TextBodyEditor) editorComposite).setText(bodyContent);
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
        };

        bodySelectionButtons.values().forEach(button -> {
            button.addSelectionListener(bodyTypeSelectedListener);
        });

    }

}
