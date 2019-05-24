package com.kms.katalon.composer.execution.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.execution.components.DriverConnectorListCellEditor;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.collector.DriverConnectorCollector;
import com.kms.katalon.execution.collector.RunConfigurationCollector;
import com.kms.katalon.execution.configuration.CustomRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.webui.keyword.CustomKeywordRunConfigurationCollector;

public class CustomExecutionSettingPage extends PreferencePageWithHelp {
    private static final String DEFAULT_CUSTOM_CONFIGURATION_NAME = "custom";

    private List<CustomRunConfiguration> customRunConfigurationList;

    private Table table;

    private TableViewer tableViewer;

    private ToolItem tltmAddProperty;

    private ToolItem tltmRemoveProperty;

    private ToolItem tltmClearProperty;

    public CustomExecutionSettingPage() {
        customRunConfigurationList = new ArrayList<CustomRunConfiguration>();
        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
        for (String customRunConfigurationId : RunConfigurationCollector.getInstance()
                .getAllCustomRunConfigurationIds()) {
            try {
                customRunConfigurationList.add(new CustomRunConfiguration(projectDir, customRunConfigurationId));
            } catch (IOException | ExecutionException e) {
                LoggerSingleton.logError(e);
            }
        }

        noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
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
        GridLayout gl_composite = new GridLayout(1, false);
        gl_composite.marginWidth = 0;
        gl_composite.marginHeight = 0;
        composite.setLayout(gl_composite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite tableComposite = new Composite(composite, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);

        addTableColumn(tableViewer, tableColumnLayout, StringConstants.SETT_COL_PREFERENCE_NAME, 100, 30,
                new EditingSupport(tableViewer) {

                    @Override
                    protected void setValue(Object element, Object value) {
                        if (element instanceof CustomRunConfiguration && value instanceof String) {
                            try {
                                CustomRunConfiguration customRunConfig = (CustomRunConfiguration) element;
                                String newName = (String) value;
                                if (!newName.equalsIgnoreCase(customRunConfig.getName())) {
                                    ((CustomRunConfiguration) element).setName(newName.toLowerCase());
                                    tableViewer.refresh();
                                }
                            } catch (IOException | ExecutionException e) {
                                LoggerSingleton.logError(e);
                            }
                        }
                    }

                    @Override
                    protected Object getValue(Object element) {
                        if (element instanceof CustomRunConfiguration) {
                            return ((CustomRunConfiguration) element).getName();
                        }
                        return null;
                    }

                    @Override
                    protected CellEditor getCellEditor(Object element) {
                        return new TextCellEditor(tableViewer.getTable());
                    }

                    @Override
                    protected boolean canEdit(Object element) {
                        if (element instanceof CustomRunConfiguration) {
                            return true;
                        }
                        return false;
                    }
                }, new ColumnLabelProvider() {
                    public String getText(Object element) {
                        if (element instanceof CustomRunConfiguration) {
                            return ((CustomRunConfiguration) element).getName();
                        }
                        return "";
                    };
                });
        addTableColumn(tableViewer, tableColumnLayout, StringConstants.SETT_COL_PREFERENCE_VALUE, 100, 70,
                new EditingSupport(tableViewer) {

                    @Override
                    protected CellEditor getCellEditor(Object element) {
                        if (element instanceof CustomRunConfiguration) {
                            CustomRunConfiguration customRunConfig = (CustomRunConfiguration) element;
                            return new DriverConnectorListCellEditor(tableViewer.getTable(), customRunConfig.toString(),
                                    customRunConfig);
                        }
                        return null;
                    }

                    @Override
                    protected boolean canEdit(Object element) {
                        if (element instanceof CustomRunConfiguration) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected Object getValue(Object element) {
                        if (element instanceof CustomRunConfiguration) {
                            CustomRunConfiguration runConfiguration = (CustomRunConfiguration) element;
                            List<IDriverConnector> driverConnectorList = new ArrayList<IDriverConnector>();
                            for (IDriverConnector driverConnector : runConfiguration.getDriverConnectors().values()) {
                                driverConnectorList.add(driverConnector);
                            }
                            return driverConnectorList;
                        }
                        return null;
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    protected void setValue(Object element, Object value) {
                        if (element instanceof CustomRunConfiguration && value instanceof List) {
                            CustomRunConfiguration runConfiguration = (CustomRunConfiguration) element;
                            String configFolderPath = runConfiguration.getConfigFolder().getAbsolutePath();
                            List<IDriverConnector> driverConnectorList = (List<IDriverConnector>) value;
                            runConfiguration.clearAllDriverConnectors();
                            try {
                                for (IDriverConnector driverConnector : driverConnectorList) {

                                    String name = DriverConnectorCollector.getInstance()
                                            .getContributorName(driverConnector, configFolderPath);

                                    runConfiguration.addDriverConnector(name, driverConnector);
                                }
                                tableViewer.refresh();
                            } catch (IOException e) {
                                LoggerSingleton.logError(e);
                                MultiStatusErrorDialog.showErrorDialog(e, "Unable to update custom configuration",
                                        e.getMessage());
                            }

                        }
                    }

                }, new ColumnLabelProvider() {
                    public String getText(Object element) {
                        if (element instanceof CustomRunConfiguration) {
                            return ((CustomRunConfiguration) element).toString();
                        }
                        return "";
                    };
                });

        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(customRunConfigurationList);

        return container;
    }

    private void addTableColumn(TableViewer parent, TableColumnLayout tableColumnLayout, String headerText, int width,
            int weight, EditingSupport editingSupport, CellLabelProvider labelProvider) {
        TableViewerColumn tableColumn = new TableViewerColumn(parent, SWT.NONE);
        tableColumn.getColumn().setWidth(width);
        tableColumn.getColumn().setMoveable(true);
        tableColumn.getColumn().setText(headerText);
        tableColumn.setLabelProvider(labelProvider);
        tableColumn.setEditingSupport(editingSupport);
        tableColumnLayout.setColumnData(tableColumn.getColumn(),
                new ColumnWeightData(weight, tableColumn.getColumn().getWidth()));
    }

    private void addToolItemListeners() {
        tltmAddProperty.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    customRunConfigurationList.add(new CustomRunConfiguration(
                            ProjectController.getInstance().getCurrentProject().getFolderLocation(),
                            generateNewCustomConfigurationName()));
                    tableViewer.refresh();
                } catch (IOException | ExecutionException exception) {
                    LoggerSingleton.logError(exception);
                }
            }
        });

        tltmRemoveProperty.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
                if (!selection.isEmpty()) {
                    for (Object selectedObject : selection.toList()) {
                        if (selectedObject instanceof CustomRunConfiguration) {
                            CustomRunConfiguration selectedCustomRunConfig = (CustomRunConfiguration) selectedObject;
                            customRunConfigurationList.remove(selectedCustomRunConfig);
                        }
                    }
                    tableViewer.refresh();
                }
            }
        });

        tltmClearProperty.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                customRunConfigurationList.clear();
                tableViewer.refresh();
            }
        });
    }

    private String generateNewCustomConfigurationName() {
        String name = DEFAULT_CUSTOM_CONFIGURATION_NAME;
        boolean exists = false;
        for (CustomRunConfiguration customRunConfig : customRunConfigurationList) {
            if (customRunConfig.getName().equalsIgnoreCase(name)) {
                exists = true;
            }
        }
        if (!exists) {
            return name;
        }
        int index = 0;
        boolean isUnique = false;
        while (!isUnique) {
            index++;
            String newName = name + "_" + index;
            isUnique = true;
            for (CustomRunConfiguration customRunConfig : customRunConfigurationList) {
                if (customRunConfig.getName().equalsIgnoreCase(newName)) {
                    isUnique = false;
                }
            }
        }
        return name + "_" + index;
    }

    @Override
    public boolean performOk() {
        File customConfigFolder = new File(ProjectController.getInstance().getCurrentProject().getFolderLocation()
                + File.separator + RunConfigurationCollector.CUSTOM_EXECUTION_CONFIG_ROOT_FOLDER_RELATIVE_PATH);
        if (customConfigFolder.exists() && customConfigFolder.isDirectory()) {
            try {
                FileUtils.deleteDirectory(customConfigFolder);
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }
        customRunConfigurationList
                .addAll(CustomKeywordRunConfigurationCollector.getInstance().getCustomKeywordRunConfigurations());
        for (CustomRunConfiguration customRunConfiguration : customRunConfigurationList) {
            try {
                customRunConfiguration.save();
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }
        return true;
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_EXECUTION;
    }
}
