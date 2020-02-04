package com.kms.katalon.composer.webservice.editor;

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
import org.eclipse.swt.widgets.Listener;

import com.kms.katalon.composer.components.impl.constants.TextContentType;
import com.kms.katalon.composer.components.impl.editors.MirrorEditor;
import com.kms.katalon.composer.components.impl.handler.DocumentReadyHandler;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.webservice.TextBodyContent;

public class TextBodyEditor extends HttpBodyEditor {

    private TextBodyContent textBodyContent;

    private MirrorEditor mirrorEditor;

    private Button chckWrapLine;
    
    private Button chckAutoUpdateContentType;

    Composite tbBodyType;

    private Map<String, Button> TEXT_MODE_SELECTION_BUTTONS = new HashMap<>();
    
    // List of TextContentType by name
    private static final String[] TEXT_MODE_NAMES;
    static {
        TEXT_MODE_NAMES = TextContentType.getTextValues();
    }
    

    public TextBodyEditor(Composite parent, int style) {
        super(parent, style);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);

        mirrorEditor = new MirrorEditor(this, SWT.NONE);
        mirrorEditor.registerDocumentHandler(new DocumentReadyHandler() {

            @Override
            public void onDocumentReady() {
                handleControlModifyListener();
            }
        });
        
        mirrorEditor.addListener(SWT.Modify, new Listener() {
            
            @Override
            public void handleEvent(Event event) {
                TextBodyEditor.this.notifyListeners(SWT.Modify, new Event());
            }
        });
        
        Composite bottomComposite = new Composite(this, SWT.NONE);
        GridLayout bottomLayout = new GridLayout(3, false);
        bottomLayout.marginWidth = bottomLayout.marginHeight = 0;
        bottomComposite.setLayout(bottomLayout);
        bottomComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        tbBodyType = new Composite(bottomComposite, SWT.NONE);
        tbBodyType.setLayout(new GridLayout(TEXT_MODE_NAMES.length, false));

        Arrays.asList(TextContentType.values()).forEach(textContentType -> {
            Button btnTextMode = new Button(tbBodyType, SWT.RADIO);
            btnTextMode.setText(textContentType.getText());
            TEXT_MODE_SELECTION_BUTTONS.put(textContentType.getText(), btnTextMode);

            btnTextMode.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    mirrorEditor.changeMode(textContentType.getText());
                    if (textBodyContent != null) {
                        textBodyContent.setContentType(textContentType.getContentType());
                    }
                    TextBodyEditor.this.setContentTypeUpdated(true);
                    TextBodyEditor.this.notifyListeners(SWT.Modify, new Event());
                }
            });
        });

        chckWrapLine = new Button(bottomComposite, SWT.CHECK);
        chckWrapLine.setText(ComposerWebserviceMessageConstants.PA_LBL_WRAP_LINE);
        chckWrapLine.setSelection(true);

        chckAutoUpdateContentType = new Button(bottomComposite, SWT.CHECK);
        chckAutoUpdateContentType.setText(ComposerWebserviceMessageConstants.PA_LBL_AUTO_UPDATE_CONTENT_TYPE);
        chckAutoUpdateContentType.setSelection(true);

    }

    private void handleControlModifyListener() {
        chckWrapLine.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                mirrorEditor.wrapLine(chckWrapLine.getSelection());
            }
        });
		if (textBodyContent != null) {
			mirrorEditor.setText(textBodyContent.getText());
		}
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
        if (StringUtils.isEmpty(rawBodyContentData)) {
            textBodyContent = new TextBodyContent();
        } else {
            textBodyContent = JsonUtil.fromJson(rawBodyContentData, TextBodyContent.class);
        }
        updateRadioStatus();
    }

    @Override
    public void onBodyTypeChanged() {
        if (textBodyContent == null) {
            textBodyContent = new TextBodyContent();
        }

        setContentTypeUpdated(true);
    }
    
    @Override
    public void setContentTypeUpdated(boolean contentTypeUpdated) {
        boolean autoUpdateContentTypeEnabled = chckAutoUpdateContentType.getSelection();
        super.setContentTypeUpdated(autoUpdateContentTypeEnabled && contentTypeUpdated);
    }

    private void updateRadioStatus() {
        TextContentType preferedContentType = TextContentType.evaluateContentType(textBodyContent.getContentType());
        Button selectionButton = TEXT_MODE_SELECTION_BUTTONS.get(preferedContentType.getText());
        TEXT_MODE_SELECTION_BUTTONS.entrySet().forEach(e -> e.getValue().setSelection(false));
        if (selectionButton != null) {
            selectionButton.setSelection(true);
            mirrorEditor.changeMode(preferedContentType.getText());
        }
    }
}
