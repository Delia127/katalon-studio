package com.kms.katalon.composer.webservice.response.body;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kms.katalon.composer.components.log.LoggerSingleton;
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

    /**
     * Key: mode name: {@link TextContentType#JSON} or {@link TextContentType#XPATH}
     * Value: a HashMap<Integer, String> that indices JSON PATH, XPATH of a line number on editor.
     */
    private Map<TextContentType, Object> lineIndexing = new HashMap<>();

    private Button chckWrapLine;

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
                            }
                            PrettyEditor.this.notifyListeners(SWT.Modify, new Event());
                            if (TextContentType.valueOf(mode) == TextContentType.JSON) {
                                String editorValue = mirrorEditor.getText();

                                ObjectMapper mapper = new ObjectMapper();

                                try {
                                    JsonNode node = mapper.readTree(editorValue);
                                    IndexedJsonNode indexedNode = new IndexedJsonNode();
                                    indexedNode.key = "";
                                    indexedNode.index = 0;
                                    indexedNode.jsonPath = "$";
                                    indexedNode.endLine = indexedNode.startLine = 0;
                                    IndexedJsonNode indexed = walk(null, indexedNode, node);
                                } catch (IOException ex) {
                                    LoggerSingleton.logError(ex);
                                }

                            }
                        }
                    }

                    private IndexedJsonNode walk(IndexedJsonNode parentIndexedNode, IndexedJsonNode indexedNode, JsonNode node) {
                        if (parentIndexedNode == null) {
                            indexedNode.endLine = indexedNode.startLine = 0;
                        } else {
                            indexedNode.endLine = indexedNode.startLine = parentIndexedNode.endLine + 1;
                        }

                        switch (node.getNodeType()) {
                            case ARRAY: {
                                Iterator<JsonNode> childrenIterator = node.iterator();
                                int index = 0;
                                while (childrenIterator.hasNext()) {
                                    JsonNode childNode = childrenIterator.next();
                                    IndexedJsonNode indexedChild = new IndexedJsonNode();
                                    indexedChild.index = index;
                                    indexedChild.jsonPath = indexedNode.jsonPath + "[" + index + "]";
                                    indexedChild.key = "";
                                    walk(indexedNode, indexedChild, childNode);
                                    indexedNode.endLine = indexedChild.endLine;

                                    indexedNode.children.add(indexedChild);
                                    index++;
                                }
                                //indexedNode.previousLine++;
                                break;
                            }
                            case OBJECT: {
                                Iterator<Entry<String, JsonNode>> childrenIterator = node.fields();
                                int index = 0;
                                while (childrenIterator.hasNext()) {
                                    Entry<String, JsonNode> childEntry = childrenIterator.next();
                                    JsonNode childNode = childEntry.getValue();
                                    IndexedJsonNode indexedChild = new IndexedJsonNode();
                                    indexedChild.index = index;
                                    indexedChild.jsonPath = indexedNode.jsonPath + "." + childEntry.getKey();
                                    indexedChild.key = childEntry.getKey();
                                    walk(indexedNode, indexedChild, childNode);

                                    indexedNode.endLine = indexedChild.endLine;

                                    indexedNode.children.add(indexedChild);
                                    index++;
                                }
                                indexedNode.endLine++;
                                break;
                            }
                            default: {
                                break;
                            }

                        }

                        return indexedNode;
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
        
        switch (preferedContentType) {
            case JSON:
                break;
            case XML:
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

    private class IndexedJsonNode {

        @Override
        public String toString() {
            return "IndexedJsonNode [key=" + key + ", index=" + index + ", jsonPath=" + jsonPath + ", startLine="
                    + startLine + ", endLine=" + endLine + ", children=" + children + "]\n";
        }

        private String key;
        
        private int index;

        private String jsonPath;

        private int startLine;

        private int endLine;

        private List<IndexedJsonNode> children = new ArrayList<>();
    }
}
