package com.kms.katalon.composer.mobile.recorder.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecoderMessagesConstants;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecorderStringConstants;

public class MobileReadonlyElementPropertiesComposite {
    private Text txtObjectName;

    private MobileElement editingElement;

    private TableViewer attributesTableViewer;

    public MobileReadonlyElementPropertiesComposite(Composite parent) {
        createObjectPropertiesComposite(parent);    
    }

    public void setEditingElement(MobileElement editingElement) {
        this.editingElement = editingElement;
        refreshAttributesTable();
    }
    
    public MobileElement getEditingElement() {
        return editingElement;
    }

    /**
     * @wbp.parser.entryPoint
     */
    private void createObjectPropertiesComposite(Composite parent) {
        Composite objectPropertiesComposite = new Composite(parent, SWT.NONE);
        GridLayout glObjectPropertiesComposite = new GridLayout();
        glObjectPropertiesComposite.horizontalSpacing = 10;
        glObjectPropertiesComposite.numColumns = 2;
        objectPropertiesComposite.setLayout(glObjectPropertiesComposite);

        Label lblObjectProperties = new Label(objectPropertiesComposite, SWT.NONE);
        lblObjectProperties.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        ControlUtils.setFontToBeBold(lblObjectProperties);
        lblObjectProperties.setText(MobileRecoderMessagesConstants.LBL_OBJECT_PROPERTIES);

        // Object Name
        Label objectNameLabel = new Label(objectPropertiesComposite, SWT.NONE);
        GridData gdObjectNameLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdObjectNameLabel.widthHint = 90;
        objectNameLabel.setLayoutData(gdObjectNameLabel);
        objectNameLabel.setText("Object Name");

        txtObjectName = new Text(objectPropertiesComposite, SWT.BORDER | SWT.READ_ONLY);
        txtObjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

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
    }

    private void createColumns(TableViewer viewer, TableColumnLayout tableColumnLayout) {
        TableViewerColumn keyColumn = new TableViewerColumn(attributesTableViewer, SWT.NONE);
        keyColumn.getColumn().setText(MobileRecorderStringConstants.NAME);
        keyColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return getTextForEntryKey(element);
            }
        });

        TableViewerColumn valueColumn = new TableViewerColumn(attributesTableViewer, SWT.NONE);
        valueColumn.getColumn().setText(MobileRecorderStringConstants.VALUE);
        valueColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return getTextForEntryValue(element);
            }
        });

        tableColumnLayout.setColumnData(keyColumn.getColumn(), new ColumnWeightData(20, 80, true));
        tableColumnLayout.setColumnData(valueColumn.getColumn(), new ColumnWeightData(80, 120, true));
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
