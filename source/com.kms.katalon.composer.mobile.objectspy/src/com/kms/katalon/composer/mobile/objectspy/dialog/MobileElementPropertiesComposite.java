package com.kms.katalon.composer.mobile.objectspy.dialog;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;

public class MobileElementPropertiesComposite {
    private Text txtObjectName;

    private CapturedMobileElement editingElement;

    private TableViewer attributesTableViewer;

    private MobileObjectSpyDialog dialog;

    public MobileElementPropertiesComposite(MobileObjectSpyDialog dialog) {
        this.dialog = dialog;
    }

    public void setEditingElement(CapturedMobileElement editingElement) {
        this.editingElement = editingElement;
        refreshAttributesTable();
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void createObjectPropertiesComposite(Composite parent) {
        Composite objectPropertiesComposite = new Composite(parent, SWT.NONE);
        GridLayout glObjectPropertiesComposite = new GridLayout(2, false);
        glObjectPropertiesComposite.horizontalSpacing = 10;
        objectPropertiesComposite.setLayout(glObjectPropertiesComposite);

        Label lblObjectProperties = new Label(objectPropertiesComposite, SWT.NONE);
        lblObjectProperties.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        ControlUtils.setFontToBeBold(lblObjectProperties);
        lblObjectProperties.setText(StringConstants.DIA_LBL_OBJECT_PROPERTIES);

        // Object Name
        Label objectNameLabel = new Label(objectPropertiesComposite, SWT.NONE);
        GridData gdObjectNameLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdObjectNameLabel.widthHint = 90;
        objectNameLabel.setLayoutData(gdObjectNameLabel);
        objectNameLabel.setText(StringConstants.DIA_LBL_OBJECT_NAME);

        txtObjectName = new Text(objectPropertiesComposite, SWT.BORDER);
        txtObjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtObjectName.setToolTipText(StringConstants.DIA_TOOLTIP_OBJECT_NAME);

        Composite attributesTableComposite = new Composite(objectPropertiesComposite, SWT.NONE);
        attributesTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        attributesTableComposite.setLayout(tableColumnLayout);

        attributesTableViewer = new CTableViewer(attributesTableComposite, SWT.MULTI | SWT.FULL_SELECTION
                | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

        createColumns(attributesTableViewer, tableColumnLayout);

        // make lines and header visible
        Table attributesTable = attributesTableViewer.getTable();

        attributesTable.setHeaderVisible(true);
        attributesTable.setLinesVisible(ControlUtils.shouldLineVisble(attributesTable.getDisplay()));
        attributesTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        attributesTableViewer.setInput(Collections.emptyList());

        registerControlModifyListeners();
    }

    private void commitEditingName() {
        if (editingElement == null || txtObjectName.isDisposed()) {
            return;
        }

        String objectName = txtObjectName.getText();

        if (isNotBlank(objectName) && !StringUtils.equals(editingElement.getName(), objectName)) {
            editingElement.setName(objectName);
            dialog.updateSelectedElement(editingElement);
        }
    }

    private void registerControlModifyListeners() {
        txtObjectName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
               commitEditingName();
            }
        });

        txtObjectName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (editingElement == null) {
                    return;
                }

                int keyCode = e.keyCode;
                switch (keyCode) {
                    case SWT.CR:
                    case SWT.KEYPAD_CR:
                        commitEditingName();
                    default:
                        break;
                }
            }
        });

        attributesTableViewer.getTable().addKeyListener(new KeyAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void keyPressed(KeyEvent e) {
                StructuredSelection selection = (StructuredSelection) attributesTableViewer.getSelection();
                if (selection == null || selection.isEmpty()) {
                    return;
                }
                if (e.keyCode == SWT.DEL) {
                    Map<String, String> allAttributes = editingElement.getAttributes();
                    for (Object selectedEntry : selection.toArray()) {
                        allAttributes.remove(((Entry<String, String>) selectedEntry).getKey());
                    }
                    refreshAttributesTable();
                }
            }
        });
    }

    private void createColumns(TableViewer viewer, TableColumnLayout tableColumnLayout) {
        TableViewerColumn keyColumn = new TableViewerColumn(attributesTableViewer, SWT.NONE);
        keyColumn.getColumn().setText(StringConstants.DIA_COL_NAME);
        keyColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return getTextForEntryKey(element);
            }
        });

        TableViewerColumn valueColumn = new TableViewerColumn(attributesTableViewer, SWT.NONE);
        valueColumn.getColumn().setText(StringConstants.DIA_COL_VALUE);
        valueColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return getTextForEntryValue(element);
            }
        });

        valueColumn.setEditingSupport(new EditingSupport(attributesTableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof Entry && value instanceof String) {
                    @SuppressWarnings("unchecked")
                    Entry<String, String> entry = (Entry<String, String>) element;
                    entry.setValue(String.valueOf(value));
                    attributesTableViewer.refresh(element);
                }
            }

            @Override
            protected Object getValue(Object element) {
                return getTextForEntryValue(element);
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element instanceof Entry) {
                    return new TextCellEditor(attributesTableViewer.getTable());
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                return element instanceof Entry;
            }
        });

        tableColumnLayout.setColumnData(keyColumn.getColumn(), new ColumnWeightData(20, 100, true));
        tableColumnLayout.setColumnData(valueColumn.getColumn(), new ColumnWeightData(80, 150, true));
    }

    private String getTextForEntryKey(Object element) {
        if (!(element instanceof Entry)) {
            return StringUtils.EMPTY;
        }
        @SuppressWarnings("unchecked")
        Entry<String, String> entry = (Entry<String, String>) element;
        String entryKey = entry.getKey();
        if (entryKey != null) {
            return entryKey.toString();
        }
        return StringUtils.EMPTY;
    }

    private String getTextForEntryValue(Object element) {
        if (!(element instanceof Entry)) {
            return StringUtils.EMPTY;
        }
        @SuppressWarnings("unchecked")
        Entry<String, String> entry = (Entry<String, String>) element;
        String entryValue = entry.getValue();
        if (entryValue != null) {
            return entryValue.toString();
        }
        return StringUtils.EMPTY;
    }

    private void refreshAttributesTable() {
        if (attributesTableViewer == null || attributesTableViewer.getTable().isDisposed()) {
            return;
        }

        if (editingElement != null) {
            txtObjectName.setText(editingElement.getName());
            attributesTableViewer.setInput(new ArrayList<>(editingElement.getAttributes().entrySet()));
        } else {
            txtObjectName.setText(StringUtils.EMPTY);
            attributesTableViewer.setInput(Collections.emptyList());
        }
        attributesTableViewer.refresh();
    }

    /* package */void focusAndEditCapturedElementName() {
        txtObjectName.setFocus();
        txtObjectName.selectAll();
    }
}
