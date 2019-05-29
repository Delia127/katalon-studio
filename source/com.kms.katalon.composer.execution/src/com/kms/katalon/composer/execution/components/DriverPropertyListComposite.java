package com.kms.katalon.composer.execution.components;

import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.provider.ListPropertyLabelProvider;
import com.kms.katalon.core.setting.DriverPropertyValueType;

public class DriverPropertyListComposite extends Composite {
    private Table table;
    private TableViewer tableViewer;

    private ToolItem tltmAddProperty;
    private ToolItem tltmRemoveProperty;
    private ToolItem tltmClearProperty;

    public DriverPropertyListComposite(Composite parent) {
        super(parent, SWT.NONE);

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite composite = new Composite(this, SWT.NONE);
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        composite.setLayout(glComposite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));


        Composite toolbarComposite = new Composite(composite, SWT.NONE);
        toolbarComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        ToolBar toolBar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);

        tltmAddProperty = new ToolItem(toolBar, SWT.NONE);
        tltmAddProperty.setText(StringConstants.SETT_TOOLITEM_ADD);
        tltmAddProperty.setImage(ImageConstants.IMG_16_ADD);

        tltmRemoveProperty = new ToolItem(toolBar, SWT.NONE);
        tltmRemoveProperty.setText(StringConstants.SETT_TOOLITEM_REMOVE);
        tltmRemoveProperty.setImage(ImageConstants.IMG_16_REMOVE);

        tltmClearProperty = new ToolItem(toolBar, SWT.NONE);
        tltmClearProperty.setText(StringConstants.SETT_TOOLITEM_CLEAR);
        tltmClearProperty.setImage(ImageConstants.IMG_16_CLEAR);
        
        Composite tableComposite = new Composite(composite, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);
        
        addTableColumn(tableViewer, tableColumnLayout, StringConstants.SETT_COL_PREFERENCE_TYPE, 100, 45, new EditingSupport(tableViewer) {

            @SuppressWarnings("unchecked")
            @Override
            protected void setValue(Object element, Object value) {
                if (!(value instanceof Integer)) {
                    return;
                }
                int selection = (int) value;
                if (selection >= 0 && tableViewer.getInput() instanceof List) {
                    DriverPropertyValueType newType = DriverPropertyValueType.valueOf(DriverPropertyValueType
                            .stringValues()[selection]);
                    DriverPropertyValueType valueType = DriverPropertyValueType.fromValue(element);
                    if (valueType != newType) {
                        List<Object> inputList = (List<Object>) tableViewer.getInput();
                        int elementIndex = indexOfUsingObject(inputList, element);
                        if (elementIndex >= 0 && elementIndex < inputList.size()) {
                            inputList.set(elementIndex, newType.getDefaultValue());
                            tableViewer.refresh();
                        }
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                DriverPropertyValueType valueType = DriverPropertyValueType.fromValue(element);
                for (int i = 0; i < DriverPropertyValueType.values().length; i++) {
                    if (DriverPropertyValueType.values()[i] == valueType) {
                        return i;
                    }
                }
                return 0;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new ComboBoxCellEditor(tableViewer.getTable(), DriverPropertyValueType.stringValues());
            }

            @Override
            protected boolean canEdit(Object element) {
                return element != null;
            }
        });
        addTableColumn(tableViewer, tableColumnLayout, StringConstants.SETT_COL_PREFERENCE_VALUE, 100, 45, new EditingSupport(tableViewer) {

            @SuppressWarnings("unchecked")
            @Override
            protected void setValue(Object element, Object value) {
                if (tableViewer.getInput() instanceof List) {
                    List<Object> inputList = (List<Object>) tableViewer.getInput();
                    int elementIndex = indexOfUsingObject(inputList, element);
                    if (elementIndex >= 0 && elementIndex < inputList.size()) {
                        DriverPropertyValueType propertyType = DriverPropertyValueType.fromValue(element);
                        switch (propertyType) {
                        case Boolean:
                            if (value instanceof Integer) {
                                if (((Integer) value) == 1) {
                                    inputList.set(elementIndex, Boolean.TRUE);
                                } else {
                                    inputList.set(elementIndex, Boolean.FALSE);
                                }
                            }
                            break;
                        case Number:
                            if (value instanceof String) {
                                try {
                                    inputList.set(elementIndex, Double.valueOf(String.valueOf(value)));
                                } catch (NumberFormatException e) {
                                    // not a number, so not setting value
                                }
                            }
                        case List:
                        case Dictionary:
                            DriverPropertyValueType newPropertyType = DriverPropertyValueType.fromValue(value);
                            if (newPropertyType == propertyType) {
                                inputList.set(elementIndex, value);
                            }
                            break;
                        case String:
                            if (value instanceof String) {
                                inputList.set(elementIndex, value);
                            }
                            break;
                        }
                        tableViewer.refresh();
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                DriverPropertyValueType propertyType = DriverPropertyValueType.fromValue(element);
                switch (propertyType) {
                case Boolean:
                    Boolean booleanValue = (Boolean) element;
                    return booleanValue == true ? 0 : 1;
                case List:
                case Dictionary:
                    return element;
                case Number:
                case String:
                    return String.valueOf(element);
                }
                return null;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                DriverPropertyValueType propertyType = DriverPropertyValueType.fromValue(element);
                switch (propertyType) {
                case Boolean:
                    return new ComboBoxCellEditor(tableViewer.getTable(), new String[] {
                            Boolean.TRUE.toString().toLowerCase(), Boolean.FALSE.toString().toLowerCase() });
                case List:
                    return new ListPropertyValueCellEditor(tableViewer.getTable());
                case Dictionary:
                    return new MapPropertyValueCellEditor(tableViewer.getTable());
                case Number:
                case String:
                    return new TextCellEditor(tableViewer.getTable());
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                return element != null;
            }
        });

        tableViewer.setLabelProvider(new ListPropertyLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());
    }
    
    public static int indexOfUsingObject(List<?> objectList, Object object) {
        for (int i = 0; i < objectList.size(); i++) {
            if (objectList.get(i) == object) {
                return i;
            }
        }
        return -1;
    }

    private void addTableColumn(TableViewer parent, TableColumnLayout tableColumnLayout, String headerText, int width,
            int weight, EditingSupport editingSupport) {
        TableViewerColumn tableColumn = new TableViewerColumn(parent, SWT.NONE);
        tableColumn.getColumn().setWidth(width);
        tableColumn.getColumn().setMoveable(true);
        tableColumn.getColumn().setText(headerText);
        tableColumn.setEditingSupport(editingSupport);
        tableColumnLayout.setColumnData(tableColumn.getColumn(), new ColumnWeightData(weight, tableColumn
                .getColumn().getWidth()));
    }

    public void setInput(List<Object> propertyValueList) {
        addToolItemListeners(propertyValueList);
        tableViewer.setInput(propertyValueList);
    }

    private void addToolItemListeners(final List<Object> propertyValueList) {
        tltmAddProperty.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                propertyValueList.add(new String(""));
                tableViewer.refresh();
            }
        });

        tltmRemoveProperty.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
                if (!selection.isEmpty()) {
                    for (Object selectedObject : selection.toList()) {
                        int selectedObjectIndex = indexOfUsingObject(propertyValueList, selectedObject);
                        if (selectedObjectIndex >= 0 && selectedObjectIndex < propertyValueList.size()) {
                            propertyValueList.remove(selectedObjectIndex);
                        }
                    }
                    tableViewer.refresh();
                }
            }
        });

        tltmClearProperty.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                propertyValueList.clear();
                tableViewer.refresh();
            }
        });
    }
}
