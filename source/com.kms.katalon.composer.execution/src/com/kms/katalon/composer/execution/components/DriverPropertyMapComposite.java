package com.kms.katalon.composer.execution.components;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.provider.MapPropertyLabelProvider;
import com.kms.katalon.composer.execution.provider.MapPropertyTableViewerContentProvider;

public class DriverPropertyMapComposite extends Composite {
    private static final String DEFAULT_DRIVER_PROPERTY_NAME = "property";
    private Table table;
    private TableViewer tableViewer;

    private ToolItem tltmAddProperty;
    private ToolItem tltmRemoveProperty;
    private ToolItem tltmClearProperty;

    private Map<String, Object> driverPropertyList;

    public DriverPropertyMapComposite(Composite parent) {
        super(parent, SWT.NONE);
        setBackground(ColorUtil.getCompositeBackgroundColorForDialog());
        setBackgroundMode(SWT.INHERIT_FORCE);

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite composite = new Composite(this, SWT.NONE);
        GridLayout gl_composite = new GridLayout(1, false);
        gl_composite.marginWidth = 0;
        gl_composite.marginHeight = 0;
        composite.setLayout(gl_composite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite toolbarComposite = new Composite(composite, SWT.NONE);
        toolbarComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        ToolBar toolBar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        toolBar.setForeground(ColorUtil.getToolBarForegroundColor());

        tltmAddProperty = new ToolItem(toolBar, SWT.NONE);
        tltmAddProperty.setText(StringConstants.SETT_TOOLITEM_ADD);
        tltmAddProperty.setImage(ImageConstants.IMG_16_ADD);

        tltmRemoveProperty = new ToolItem(toolBar, SWT.NONE);
        tltmRemoveProperty.setText(StringConstants.SETT_TOOLITEM_REMOVE);
        tltmRemoveProperty.setImage(ImageConstants.IMG_16_REMOVE);

        tltmClearProperty = new ToolItem(toolBar, SWT.NONE);
        tltmClearProperty.setText(StringConstants.SETT_TOOLITEM_CLEAR);
        tltmClearProperty.setImage(ImageConstants.IMG_16_CLEAR);
        addToolItemListeners();

        Composite tableComposite = new Composite(composite, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);

        addTableColumn(tableViewer, tableColumnLayout, StringConstants.SETT_COL_PREFERENCE_NAME, 100, 30,
                new DriverPropertyNameEditingSupport(tableViewer));
        addTableColumn(tableViewer, tableColumnLayout, StringConstants.SETT_COL_PREFERENCE_TYPE, 100, 30,
                new DriverPropertyTypeEditingSupport(tableViewer));
        addTableColumn(tableViewer, tableColumnLayout, StringConstants.SETT_COL_PREFERENCE_VALUE, 100, 30,
                new DriverPropertyValueEditingSupport(tableViewer));

        tableViewer.setLabelProvider(new MapPropertyLabelProvider());
        tableViewer.setContentProvider(new MapPropertyTableViewerContentProvider());
    }

    private void addTableColumn(TableViewer parent, TableColumnLayout tableColumnLayout, String headerText, int width,
            int weight, EditingSupport editingSupport) {
        TableViewerColumn tableColumn = new TableViewerColumn(parent, SWT.NONE);
        tableColumn.getColumn().setWidth(width);
        tableColumn.getColumn().setMoveable(true);
        tableColumn.getColumn().setText(headerText);
        tableColumn.setEditingSupport(editingSupport);
        tableColumnLayout.setColumnData(tableColumn.getColumn(), new ColumnWeightData(weight, tableColumn.getColumn()
                .getWidth()));
    }

    public void setInput(Map<String, Object> driverPropertyList) {
        this.driverPropertyList = driverPropertyList;
        tableViewer.setInput(driverPropertyList);
    }

    private void addToolItemListeners() {
        tltmAddProperty.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                driverPropertyList.put(generateNewPropertyName(driverPropertyList), "");
                tableViewer.refresh();
            }
        });

        tltmRemoveProperty.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
                if (!selection.isEmpty()) {
                    for (Object selectedObject : selection.toList()) {
                        if (selectedObject instanceof Entry<?, ?>) {
                            driverPropertyList.remove(((Entry<?, ?>) selectedObject).getKey());
                        }
                    }
                    tableViewer.refresh();
                }
            }
        });

        tltmClearProperty.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                driverPropertyList.clear();
                tableViewer.refresh();
            }
        });
    }

    public static String generateNewPropertyName(Map<String, Object> driverPropertyDictionary) {
        String name = DEFAULT_DRIVER_PROPERTY_NAME;
        if (driverPropertyDictionary.get(name) == null) {
            return name;
        }
        int index = 0;
        boolean isUnique = false;
        while (!isUnique) {
            index++;
            String newName = name + "_" + index;
            isUnique = driverPropertyDictionary.get(newName) == null;
        }
        return name + "_" + index;
    }

    public Map<String, Object> getDriverProperties() {
        return driverPropertyList;
    }
}
