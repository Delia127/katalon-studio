package com.kms.katalon.composer.windows.spy;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.windows.dialog.WindowsObjectDialog;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.helper.WindowsElementHelper;
import com.kms.katalon.entity.repository.WindowsElementEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity.LocatorStrategy;

public class WindowsElementPropertiesComposite {
    private static final int DF_LABEL_WIDTH_HINT = 120;

    private Text txtObjectName;

    private CapturedWindowsElement editingElement;

    private TableViewer attributesTableViewer;

    private WindowsObjectDialog dialog;

    private Combo cbbLocatorStrategy;

    private StyledText txtLocator;

    private static String[] strategies = WindowsElementEntity.LocatorStrategy.getStrategies();

    public WindowsElementPropertiesComposite(WindowsObjectDialog dialog) {
        this.dialog = dialog;
    }

    public void setEditingElement(CapturedWindowsElement editingElement) {
        this.editingElement = editingElement;
        refreshAttributesTable();
    }

    public Composite createObjectPropertiesComposite(Composite parent) {
        Composite objectPropertiesComposite = new Composite(parent, SWT.NONE);
        GridLayout glObjectPropertiesComposite = new GridLayout(2, false);
        objectPropertiesComposite.setLayout(glObjectPropertiesComposite);

        Label lblObjectProperties = new Label(objectPropertiesComposite, SWT.NONE);
        lblObjectProperties.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        ControlUtils.setFontToBeBold(lblObjectProperties);
        lblObjectProperties.setText(StringConstants.DIA_LBL_OBJECT_PROPERTIES);

        // Object Name
        Label objectNameLabel = new Label(objectPropertiesComposite, SWT.NONE);
        GridData gdObjectNameLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdObjectNameLabel.widthHint = DF_LABEL_WIDTH_HINT;
        objectNameLabel.setLayoutData(gdObjectNameLabel);
        objectNameLabel.setText(StringConstants.DIA_LBL_OBJECT_NAME);

        txtObjectName = new Text(objectPropertiesComposite, SWT.BORDER);
        txtObjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtObjectName.setToolTipText(StringConstants.DIA_TOOLTIP_OBJECT_NAME);

        // Locator Strategy
        Composite locatorComposite = new Composite(objectPropertiesComposite, SWT.NONE);
        GridLayout glLocatorComposite = new GridLayout(2, false);
        glLocatorComposite.marginWidth = 0;
        glLocatorComposite.marginHeight = 0;
        locatorComposite.setLayout(glLocatorComposite);
        locatorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        Label lblLocatorStrategy = new Label(locatorComposite, SWT.NONE);
        lblLocatorStrategy.setText("Locator Strategy");
        GridData gdLocatorStrategy = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLocatorStrategy.widthHint = DF_LABEL_WIDTH_HINT;
        lblLocatorStrategy.setLayoutData(gdLocatorStrategy);

        Composite locatorStrategyComposite = new Composite(locatorComposite, SWT.NONE);
        GridLayout glLocatorStrategyComposite = new GridLayout(2, false);
        glLocatorStrategyComposite.marginWidth = 0;
        glLocatorStrategyComposite.marginHeight = 0;
        locatorStrategyComposite.setLayout(glLocatorStrategyComposite);
        locatorStrategyComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        
        cbbLocatorStrategy = new Combo(locatorStrategyComposite, SWT.READ_ONLY);
        cbbLocatorStrategy.setItems(strategies);

        Button btnRestoreDefaultLocator = new Button(locatorStrategyComposite, SWT.PUSH);
        btnRestoreDefaultLocator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
        btnRestoreDefaultLocator.setText(StringConstants.DIA_BTN_DEFAULT);
        btnRestoreDefaultLocator.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setLocatorByLocatorStrategy();
            }
        });

        // Locator
        Label lblLocator = new Label(locatorComposite, SWT.NONE);
        GridData gdLocator = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLocator.widthHint = DF_LABEL_WIDTH_HINT;
        lblLocator.setLayoutData(gdLocator);
        lblLocator.setText("Locator");

        txtLocator = new StyledText(locatorComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData gdTxtEditor = new GridData(SWT.FILL, SWT.FILL, true, false);
        gdTxtEditor.heightHint = 50;
        txtLocator.setLayoutData(gdTxtEditor);

        // Object Properties
        Composite attributesTableComposite = new Composite(objectPropertiesComposite, SWT.NONE);
        attributesTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        attributesTableComposite.setLayout(tableColumnLayout);

        attributesTableViewer = new CTableViewer(attributesTableComposite,
                SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

        createColumns(attributesTableViewer, tableColumnLayout);

        // make lines and header visible
        Table attributesTable = attributesTableViewer.getTable();

        attributesTable.setHeaderVisible(true);
        attributesTable.setLinesVisible(ControlUtils.shouldLineVisble(attributesTable.getDisplay()));
        attributesTableViewer.setContentProvider(ArrayContentProvider.getInstance());

        attributesTableViewer.setInput(Collections.emptyList());

        registerControlModifyListeners();
        return objectPropertiesComposite;
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
        txtLocator.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (editingElement != null) {
                    editingElement.setLocator(txtLocator.getText());
                }
            }
        });

        cbbLocatorStrategy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (editingElement == null) {
                    return;
                }
                int index = cbbLocatorStrategy.getSelectionIndex();
                editingElement.setLocatorStrategy(LocatorStrategy.valueOfStrategy(strategies[index]));
            }
        });

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
                    Map<String, String> allAttributes = editingElement.getLink().getProperties();
                    for (Object selectedEntry : selection.toArray()) {
                        allAttributes.remove(((Entry<String, String>) selectedEntry).getKey());
                    }
                    refreshAttributesTable();
                }
            }
        });
    }
    
    private void setLocatorByLocatorStrategy() {
        LocatorStrategy locatorStrategy = editingElement.getLocatorStrategy();
        String locator = WindowsElementHelper.getLocatorByStrategy(locatorStrategy, editingElement.getSnapshotWindowsElement());
        editingElement.setLocator(locator);
        txtLocator.setText(locator);
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

        tableColumnLayout.setColumnData(keyColumn.getColumn(), new ColumnWeightData(20, 150, true));
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
            txtLocator.setText(editingElement.getLocator());
            int selectedIndex = Arrays.asList(strategies)
                    .indexOf(editingElement.getLocatorStrategy().getLocatorStrategy());
            cbbLocatorStrategy.select(selectedIndex);
            attributesTableViewer.setInput(new ArrayList<>(editingElement.getProperties().entrySet()));
        } else {
            txtObjectName.setText(StringUtils.EMPTY);
            attributesTableViewer.setInput(Collections.emptyList());
            txtLocator.setText("");
        }
        attributesTableViewer.refresh();
    }

    public void focusAndEditCapturedElementName() {
        txtObjectName.setFocus();
        txtObjectName.selectAll();
    }

    public CapturedWindowsElement getEditingElement() {
        return editingElement;
    }
}
