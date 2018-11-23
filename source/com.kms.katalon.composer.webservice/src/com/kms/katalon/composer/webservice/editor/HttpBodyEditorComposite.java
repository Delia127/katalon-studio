package com.kms.katalon.composer.webservice.editor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.webservice.parts.RestServicePart;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.webservice.TextBodyContent;

public class HttpBodyEditorComposite extends Composite {

    private Map<String, HttpBodyEditor> bodyEditors = new HashMap<>();

    private Map<String, Button> bodySelectionButtons = new HashMap<>();

    private Button tltmText;

    private Button tltmUrlEncoded;

    private Button tltmFormData;

    private Button tltmBinary;

    private StackLayout slBodyContent;

    private WebServiceRequestEntity webServiceEntity;

    private RestServicePart servicePart;

    private String selectedBodyType;

    private boolean isInputReady;

    public HttpBodyEditorComposite(Composite parent, int style, RestServicePart servicePart) {
        super(parent, style);

        this.servicePart = servicePart;

        setLayout(new GridLayout());

        Composite bodyTypeComposite = new Composite(this, SWT.NONE);
        bodyTypeComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout glBodyType = new GridLayout(2, false);
        glBodyType.marginWidth = glBodyType.marginHeight = 0;
        bodyTypeComposite.setLayout(glBodyType);

        Composite tbBodyType = new Composite(bodyTypeComposite, SWT.NONE);
        tbBodyType.setLayout(new GridLayout(4, false));
        tltmText = new Button(tbBodyType, SWT.RADIO);
        tltmText.setText("text");
        bodySelectionButtons.put("text", tltmText);

        tltmUrlEncoded = new Button(tbBodyType, SWT.RADIO);
        tltmUrlEncoded.setText("x-www-form-urlencoded");
        bodySelectionButtons.put("x-www-form-urlencoded", tltmUrlEncoded);

        tltmFormData = new Button(tbBodyType, SWT.RADIO);
        tltmFormData.setText("form-data");
        bodySelectionButtons.put("form-data", tltmFormData);

        tltmBinary = new Button(tbBodyType, SWT.RADIO);
        tltmBinary.setText("file");
        bodySelectionButtons.put("file", tltmBinary);

        Composite bodyContentComposite = new Composite(this, SWT.NONE);
        bodyContentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        slBodyContent = new StackLayout();
        bodyContentComposite.setLayout(slBodyContent);

        TextBodyEditor textBodyEditor = new TextBodyEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put("text", textBodyEditor);

        UrlEncodedBodyEditor urlEncodedEditor = new UrlEncodedBodyEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put("x-www-form-urlencoded", urlEncodedEditor);

        FormDataBodyEditor formDataEditor = new FormDataBodyEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put("form-data", formDataEditor);

        FileBodyEditor fileBodyEditor = new FileBodyEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put("file", fileBodyEditor);

        handleControlModifyListeners();
    }

    private void migrateFromOldVersion(WebServiceRequestEntity requestEntity) {
        if (StringUtils.isEmpty(requestEntity.getHttpBodyContent())
                && StringUtils.isNotEmpty(requestEntity.getHttpBody())) {
            selectedBodyType = "text";
            TextBodyContent textBodyContent = new TextBodyContent();
            textBodyContent.setText(requestEntity.getHttpBody());

            WebElementPropertyEntity contentTypeProperty = findContentTypeProperty();
            if (contentTypeProperty != null) {
                textBodyContent.setContentType(contentTypeProperty.getValue());
            }
        }
    }

    public void setInput(WebServiceRequestEntity requestEntity) {
        isInputReady = false;

        this.webServiceEntity = requestEntity;

        migrateFromOldVersion(webServiceEntity);

        selectedBodyType = StringUtils.defaultIfEmpty(webServiceEntity.getHttpBodyType(), "text");
        Button selectedButton = bodySelectionButtons.get(selectedBodyType);

        bodyEditors.get(selectedBodyType).setInput(webServiceEntity.getHttpBodyContent());

        selectedButton.setSelection(true);
        selectedButton.notifyListeners(SWT.Selection, new Event());

        isInputReady = true;
    }

    private void handleControlModifyListeners() {
        SelectionAdapter bodyTypeSelectedListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                selectedBodyType = source.getText();
                HttpBodyEditor httpBodyEditor = bodyEditors.get(selectedBodyType);
                httpBodyEditor.onBodyTypeChanged();

                slBodyContent.topControl = httpBodyEditor;
                httpBodyEditor.getParent().layout();

                if (!isInputReady) {
                    return;
                }
                servicePart.setDirty(true);
                updateContentTypeByEditor(httpBodyEditor);
            }
        };

        bodySelectionButtons.values().forEach(button -> {
            button.addSelectionListener(bodyTypeSelectedListener);
        });

        bodyEditors.values().forEach(editor -> {
            editor.addListener(SWT.Modify, event -> {
                if (isInputReady) {
                    servicePart.setDirty(true);
                    updateContentTypeByEditor(editor);
                }
            });
        });
    }

    private void updateContentTypeByEditor(HttpBodyEditor editor) {
        if (editor.isContentTypeUpdated()) {
            WebElementPropertyEntity propertyEntity = findContentTypeProperty();
            String newContentType = editor.getContentType();
            if (propertyEntity != null) {
                propertyEntity.setValue(newContentType);
            } else {
                propertyEntity = new WebElementPropertyEntity("Content-Type", newContentType);
                webServiceEntity.getHttpHeaderProperties().add(0, propertyEntity);
            }
            servicePart.updateHeaders(webServiceEntity);

            editor.setContentTypeUpdated(false);
        }
    }

    private WebElementPropertyEntity findContentTypeProperty() {
        WebElementPropertyEntity propertyEntity = webServiceEntity.getHttpHeaderProperties()
                .stream()
                .filter(h -> "Content-Type".equals(h.getName()))
                .findFirst()
                .orElse(null);
        return propertyEntity;
    }

    public String getHttpBodyType() {
        return selectedBodyType;
    }

    public String getHttpBodyContent() {
        return bodyEditors.get(selectedBodyType).getContentData();
    }

}
