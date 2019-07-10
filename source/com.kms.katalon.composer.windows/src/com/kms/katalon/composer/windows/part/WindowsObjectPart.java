package com.kms.katalon.composer.windows.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.tree.WindowsElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.windows.dialog.NewWindowsElementPropertyDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.WindowsElementController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public class WindowsObjectPart {

    private WindowsElementEntity entity;

    private StyledText txtLocator;

    private CTableViewer tableViewerObjectProperties;

    private Combo cbbLocatorStrategy;

    private MPart mpart;

    private Composite mainComposite;

    private static String[] strategies = WindowsElementEntity.LocatorStrategy.getStrategies();

    private List<WebElementPropertyEntity> properties;

    private WindowsElementEntity editingEntity;

    @Inject
    IEventBroker eventBroker;

    @PostConstruct
    public void onCreatePart(MPart mpart, Composite parent) {
        this.mpart = mpart;
        entity = (WindowsElementEntity) mpart.getObject();

        createControls(parent);

        setInput();

        addListeners();
    }

    private EventServiceAdapter eventHandler = new EventServiceAdapter() {

        @Override
        public void handleEvent(Event event) {
            Object[] objects = getObjects(event);
            if (objects == null || objects.length != 2) {
                return;
            }
            try {
                String oldWindowsObjectId = (String) objects[0];
                String newWindowsObjectId = (String) objects[1];
                if (editingEntity.getIdForDisplay().equals(oldWindowsObjectId)) {
                    entity = WindowsElementController.getInstance().getWindowsElementByDisplayId(newWindowsObjectId);
                    editingEntity = (WindowsElementEntity) entity.clone();
                    editingEntity.setName(entity.getName());
                    mpart.setLabel(editingEntity.getName());
                }
            } catch (ControllerException e) {
                LoggerSingleton.logError(e);
            }
        }
    };

    @PreDestroy
    public void onDestroyPart() {
        eventBroker.unsubscribe(eventHandler);
    }

    private void addListeners() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, eventHandler);

        eventBroker.subscribe(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM, eventHandler);

        txtLocator.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                mpart.setDirty(true);
            }
        });

        cbbLocatorStrategy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                mpart.setDirty(true);
            }
        });
    }

    private void setInput() {
        editingEntity = (WindowsElementEntity) entity.clone();
        mpart.setLabel(editingEntity.getName());
        WindowsElementEntity.LocatorStrategy selectedLocator = editingEntity.getLocatorStrategy();
        int selectedIndex = Arrays.asList(strategies).indexOf(selectedLocator.getLocatorStrategy());

        cbbLocatorStrategy.select(selectedIndex);
        txtLocator.setText(StringUtils.defaultString(editingEntity.getLocator()));

        properties = new ArrayList<>(editingEntity.getProperties());
        tableViewerObjectProperties.setInput(properties);
    }

    private Shell getShell() {
        return mainComposite.getShell();
    }

    private void createControls(Composite parent) {
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setBackground(ColorUtil.getPartBackgroundColor());
        mainComposite.setLayout(new GridLayout(1, false));

        Composite locatorComposite = new Composite(mainComposite, SWT.NONE);
        locatorComposite.setLayout(new GridLayout(1, false));
        locatorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label lblLocatorTitle = new Label(locatorComposite, SWT.NONE);
        lblLocatorTitle.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblLocatorTitle.setText("Locator");
        ControlUtils.setFontToBeBold(lblLocatorTitle);

        Composite locatorDetailsComposite = new Composite(locatorComposite, SWT.NONE);
        locatorDetailsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout glLocatorDetails = new GridLayout(2, false);
        glLocatorDetails.marginWidth = 0;
        glLocatorDetails.marginHeight = 0;
        locatorDetailsComposite.setLayout(glLocatorDetails);

        Label lblLocatorStrategy = new Label(locatorDetailsComposite, SWT.NONE);
        lblLocatorStrategy.setText("Locator Strategy");
        lblLocatorStrategy.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        cbbLocatorStrategy = new Combo(locatorDetailsComposite, SWT.READ_ONLY);
        cbbLocatorStrategy.setItems(strategies);

        Label lblLocator = new Label(locatorDetailsComposite, SWT.NONE);
        lblLocator.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        lblLocator.setText("Locator");

        txtLocator = new StyledText(locatorDetailsComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData gdTxtEditor = new GridData(SWT.FILL, SWT.FILL, true, false);
        gdTxtEditor.heightHint = 100;
        txtLocator.setLayoutData(gdTxtEditor);

        Composite tableComposite = new Composite(mainComposite, SWT.NONE);
        tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glTableComposite = new GridLayout();
        glTableComposite.marginTop = 15;
        tableComposite.setLayout(glTableComposite);

        Label lblObjectPropertiesTitle = new Label(tableComposite, SWT.NONE);
        lblObjectPropertiesTitle.setText("Object Properties");
        ControlUtils.setFontToBeBold(lblObjectPropertiesTitle);

        ToolBar tbProperties = new ToolBar(tableComposite, SWT.FLAT | SWT.RIGHT);
        tbProperties.setForeground(ColorUtil.getToolBarForegroundColor());
        tbProperties.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        ToolItem tltmAdd = new ToolItem(tbProperties, SWT.PUSH);
        tltmAdd.setText("Add");
        tltmAdd.setImage(ImageManager.getImage(IImageKeys.ADD_16));

        tltmAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                NewWindowsElementPropertyDialog windowsElementDialog = new NewWindowsElementPropertyDialog(getShell());
                if (windowsElementDialog.open() == NewWindowsElementPropertyDialog.OK) {
                    properties.add(windowsElementDialog.getNewProperty());
                    tableViewerObjectProperties.setInput(properties);
                    tableViewerObjectProperties.refresh();
                    mpart.setDirty(true);
                }
            }
        });

        ToolItem tltmEdit = new ToolItem(tbProperties, SWT.PUSH);
        tltmEdit.setText("Edit");
        tltmEdit.setImage(ImageManager.getImage(IImageKeys.EDIT_16));

        ToolItem tltmDelete = new ToolItem(tbProperties, SWT.PUSH);
        tltmDelete.setText("Delete");
        tltmDelete.setImage(ImageManager.getImage(IImageKeys.DELETE_16));
        tltmDelete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                @SuppressWarnings("unchecked")
                List<Object> objects = tableViewerObjectProperties.getStructuredSelection().toList();
                if (objects == null || objects.isEmpty()) {
                    return;
                }
                properties.removeAll(objects);
                tableViewerObjectProperties.setInput(properties);
                tableViewerObjectProperties.refresh();
                mpart.setDirty(true);
            }
        });

        ToolItem tltmClear = new ToolItem(tbProperties, SWT.PUSH);
        tltmClear.setText("Clear");
        tltmClear.setImage(ImageManager.getImage(IImageKeys.DELETE_16));
        tltmClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (properties.isEmpty()) {
                    return;
                }
                if (!MessageDialog.openConfirm(getShell(), GlobalStringConstants.CONFIRMATION,
                        "Do you want to clear all these properties?")) {
                    return;
                }
                properties.clear();
                tableViewerObjectProperties.setInput(properties);
                tableViewerObjectProperties.refresh();
                mpart.setDirty(true);
            }
        });

        tableViewerObjectProperties = new CTableViewer(tableComposite, SWT.BORDER);
        Table table = tableViewerObjectProperties.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));

        TableViewerColumn treeViewerColumnName = new TableViewerColumn(tableViewerObjectProperties, SWT.NONE);
        TableColumn trclmnColumnName = treeViewerColumnName.getColumn();
        trclmnColumnName.setText("Name");
        trclmnColumnName.setWidth(175);
        // treeViewerColumnName
        // .setEditingSupport(new PropertyNameEditingSupport(propertyTableViewer, eventBroker, testObjectPart));
        treeViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getName();
            }
        });

        TableViewerColumn treeViewerColumnValue = new TableViewerColumn(tableViewerObjectProperties, SWT.NONE);
        TableColumn trclmnColumnValue = treeViewerColumnValue.getColumn();
        trclmnColumnValue.setText("Value");
        trclmnColumnValue.setWidth(350);
        // treeViewerColumnValue
        // .setEditingSupport(new PropertyValueEditingSupport(propertyTableViewer, eventBroker, testObjectPart));
        treeViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getValue();
            }
        });

        tableViewerObjectProperties.setContentProvider(ArrayContentProvider.getInstance());
        tableViewerObjectProperties.setInput(Collections.emptyList());
    }

    @Persist
    public void onSave() {
        WindowsElementEntity.LocatorStrategy selectedLocator = WindowsElementEntity.LocatorStrategy
                .valueOfStrategy(cbbLocatorStrategy.getText());
        editingEntity.setLocatorStrategy(selectedLocator);
        editingEntity.setLocator(txtLocator.getText());
        editingEntity.setProperties(properties);

        try {
            WindowsElementController.getInstance().updateWindowsElementEntity(editingEntity);
            entity = editingEntity;
            editingEntity = (WindowsElementEntity) editingEntity.clone();

            WindowsElementTreeEntity treeEntity = TreeEntityUtil.getWindowsElementTreeEntity(entity,
                    entity.getParentFolder());
            ExplorerPart.getInstance().refreshTreeEntity(treeEntity);
            mpart.setDirty(false);
        } catch (ControllerException e) {
            MultiStatusErrorDialog.showErrorDialog(e, "Error", "Unable to save Windows Object");
        }
    }
}
