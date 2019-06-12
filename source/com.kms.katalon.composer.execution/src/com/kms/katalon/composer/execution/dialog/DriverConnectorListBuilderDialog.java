package com.kms.katalon.composer.execution.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.execution.collector.DriverConnectorEditorCollector;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.execution.collector.DriverConnectorCollector;
import com.kms.katalon.execution.configuration.CustomRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.configuration.contributor.IDriverConnectorContributor;
import com.kms.katalon.execution.exception.ExecutionException;

public class DriverConnectorListBuilderDialog extends Dialog {
    private List<IDriverConnector> driverConnectorList;
    private IDriverConnectorContributor[] driverConnectorContributonList;
    private TableViewer tableViewer;

    private ToolItem tltmAddProperty;
    private ToolItem tltmRemoveProperty;
    private ToolItem tltmClearProperty;
    private File settingFolder;

    public DriverConnectorListBuilderDialog(Shell parentShell, List<IDriverConnector> driverConnectorList,
            CustomRunConfiguration customRunConfig) {
        super(parentShell);
        this.driverConnectorList = driverConnectorList;
        driverConnectorContributonList = DriverConnectorCollector.getInstance()
                .getAllBuiltinDriverConnectorContributors();

        settingFolder = customRunConfig.getConfigFolder();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite formComposite = new Composite(container, SWT.NONE);
        formComposite.setLayout(new GridLayout(1, false));
        formComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite toolbarComposite = new Composite(formComposite, SWT.NONE);
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

        Composite composite = new Composite(formComposite, SWT.NONE);
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        composite.setLayout(glComposite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite tableComposite = new Composite(composite, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);

        addTableColumn(tableViewer, tableColumnLayout, StringConstants.SETT_COL_DRIVER_PREFERENCE_NAME, 100, 30,
                new EditingSupport(tableViewer) {
                    List<String> driverConnectorNames = new ArrayList<String>();

                    @Override
                    protected void setValue(Object element, Object value) {
                        if (element instanceof IDriverConnector && value instanceof Integer
                                && driverConnectorNames != null && (int) value >= 0
                                && (int) value <= driverConnectorNames.size()) {
                            try {
                                IDriverConnector existingDriverConnector = (IDriverConnector) element;
                                IDriverConnector newDriverConnector = null;
                                String newDriverConnectorName = driverConnectorNames.get((int) value);

                                for (IDriverConnectorContributor driverConnectorContributor : driverConnectorContributonList) {
                                    for (IDriverConnector driverConnector : driverConnectorContributor
                                            .getDriverConnector(settingFolder.getAbsolutePath())) {
                                        if (driverConnector.getDriverType().toString().equals(newDriverConnectorName)) {
                                            newDriverConnector = driverConnector;
                                        }
                                    }
                                }

                                int index = driverConnectorList.indexOf(existingDriverConnector);
                                if (index >= 0 && index < driverConnectorList.size() && newDriverConnector != null) {
                                    driverConnectorList.set(index, newDriverConnector);
                                    tableViewer.refresh();
                                }
                            } catch (IOException e) {
                                LoggerSingleton.logError(e);
                            }
                        }
                    }

                    @Override
                    protected Object getValue(Object element) {
                        if (element instanceof IDriverConnector) {
                            IDriverConnector existingDriverConnector = (IDriverConnector) element;
                            for (int i = 0; i < driverConnectorNames.size(); i++) {
                                if (driverConnectorNames.get(i).equals(
                                        existingDriverConnector.getDriverType().toString())) {
                                    return i;
                                }
                            }

                        }
                        return 0;
                    }

                    @Override
                    protected CellEditor getCellEditor(Object element) {
                        driverConnectorNames.clear();
                        try {
                            if (element instanceof IDriverConnector) {
                                IDriverConnector selectedDriverConnector = (IDriverConnector) element; 
                                for (IDriverConnectorContributor driverConnectorContributor : driverConnectorContributonList) {
                                    boolean isExists = false;
                                    for (IDriverConnector existingDriverConnector : driverConnectorList) {
                                        if (selectedDriverConnector.getDriverType() == existingDriverConnector.getDriverType()) {
                                            continue;
                                        }
                                        for (IDriverConnector driverConnector : driverConnectorContributor
                                                .getDriverConnector(settingFolder.getAbsolutePath())) {
                                            if (existingDriverConnector.getDriverType() == driverConnector.getDriverType()) {
                                                isExists = true;
                                            }
                                        }
                                    }
                                    if (!isExists) {
                                        for (IDriverConnector driverConnector : driverConnectorContributor
                                                .getDriverConnector(settingFolder.getAbsolutePath())) {
                                            driverConnectorNames.add(driverConnector.getDriverType().toString());
                                        }
                                    }
                                }
                                return new ComboBoxCellEditor(tableViewer.getTable(), driverConnectorNames
                                        .toArray(new String[driverConnectorNames.size()]));
                            }
                        } catch (IOException e) {
                            LoggerSingleton.logError(e);
                        }
                        return null;
                    }

                    @Override
                    protected boolean canEdit(Object element) {
                        if (element instanceof IDriverConnector) {
                            return true;
                        }
                        return false;
                    }
                }, new ColumnLabelProvider() {
                    public String getText(Object element) {
                        if (element instanceof IDriverConnector) {
                            return ((IDriverConnector) element).getDriverType().toString();
                        }
                        return "";
                    };
                });
        addTableColumn(tableViewer, tableColumnLayout, StringConstants.SETT_COL_PREFERENCE, 100, 70,
                new EditingSupport(tableViewer) {

                    @Override
                    protected CellEditor getCellEditor(Object element) {
                        try {
                            return DriverConnectorEditorCollector.getInstance().getDriverConnector(element.getClass(),
                                    tableViewer.getTable());
                        } catch (IOException | ExecutionException e) {
                            LoggerSingleton.logError(e);
                        }
                        return null;
                    }

                    @Override
                    protected boolean canEdit(Object element) {
                        if (element instanceof IDriverConnector) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected Object getValue(Object element) {
                        if (element instanceof IDriverConnector) {
                            return (IDriverConnector) element;
                        }
                        return null;
                    }

                    @Override
                    protected void setValue(Object element, Object value) {
                        if (element instanceof IDriverConnector && value instanceof IDriverConnector) {
                            int index = driverConnectorList.indexOf(element);
                            if (index >= 0 && index < driverConnectorList.size()) {
                                driverConnectorList.set(index, (IDriverConnector) value);
                                tableViewer.refresh();
                            }
                        }
                    }

                }, new ColumnLabelProvider() {
                    public String getText(Object element) {
                        if (element instanceof IDriverConnector) {
                            return ((IDriverConnector) element).toString();
                        }
                        return "";
                    };
                });

        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(driverConnectorList);

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_DRIVER_LIST_CONNECTOR_BUILDER);
    }

    private void addTableColumn(TableViewer parent, TableColumnLayout tableColumnLayout, String headerText, int width,
            int weight, EditingSupport editingSupport, CellLabelProvider labelProvider) {
        TableViewerColumn tableColumn = new TableViewerColumn(parent, SWT.NONE);
        tableColumn.getColumn().setWidth(width);
        tableColumn.getColumn().setMoveable(true);
        tableColumn.getColumn().setText(headerText);
        tableColumn.setLabelProvider(labelProvider);
        tableColumn.setEditingSupport(editingSupport);
        tableColumnLayout.setColumnData(tableColumn.getColumn(), new ColumnWeightData(weight, tableColumn.getColumn()
                .getWidth()));
    }

    public List<IDriverConnector> getDriverConnectorList() {
        return driverConnectorList;
    }

    private void addToolItemListeners() {
        tltmAddProperty.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (driverConnectorContributonList.length > 0) {
                    try {
                        for (IDriverConnectorContributor driverConnectorContributor : driverConnectorContributonList) {
                            boolean isExists = false;
                            for (IDriverConnector existingDriverConnector : driverConnectorList) {
                                for (IDriverConnector driverConnector : driverConnectorContributor
                                        .getDriverConnector(settingFolder.getAbsolutePath())) {
                                    if (existingDriverConnector.getDriverType() == driverConnector.getDriverType()) {
                                        isExists = true;
                                    }
                                }
                            }
                            if (!isExists) {
                                IDriverConnector[] newDriverConnectorList = driverConnectorContributor
                                        .getDriverConnector(settingFolder.getAbsolutePath());
                                if (newDriverConnectorList.length > 0) {
                                    driverConnectorList.add(newDriverConnectorList[0]);
                                    tableViewer.refresh();
                                    return;
                                }
                            }
                        }
                    } catch (IOException exception) {
                        LoggerSingleton.logError(exception);
                    }
                }
            }
        });

        tltmRemoveProperty.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
                if (!selection.isEmpty()) {
                    for (Object selectedObject : selection.toList()) {
                        if (selectedObject instanceof IDriverConnector) {
                            IDriverConnector selectedDriverConnector = (IDriverConnector) selectedObject;
                            driverConnectorList.remove(selectedDriverConnector);
                        }
                    }
                    tableViewer.refresh();
                }
            }
        });

        tltmClearProperty.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                driverConnectorList.clear();
                tableViewer.refresh();
            }
        });
    }

    @Override
    protected Point getInitialSize() {
        return new Point(700, 500);
    }
}
