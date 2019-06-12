package com.kms.katalon.composer.webservice.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.dialogs.AddOrEditWebServiceMethodDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webservice.common.WebServiceMethod;
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore;
import com.kms.katalon.entity.project.ProjectEntity;

public class WebServiceMethodSettingPage extends PreferencePageWithHelp {
    
    private ToolItem tiAddMethod;
    
    private ToolItem tiRemoveMethod;
    
    private ToolItem tiEditMethod;

    private TableViewer methodTable;
    
    private List<WebServiceMethod> methods;
    
    private WebServiceSettingStore store;
    
    private IEventBroker eventBroker;

    public WebServiceMethodSettingPage() {
        eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        store = getStore();
        loadMethods();
        noDefaultButton();
    }
    
    @Override
    protected Control createContents(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        body.setLayout(new GridLayout(1, false));
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        loadMethods();
        createToolBar(body);
        createMethodsTable(body);
        
        return body;
    }

    private void createToolBar(Composite parent) {
        Composite toolbarComposite = new Composite(parent, SWT.NONE);
        toolbarComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
       
        ToolBar toolBar = new ToolBar(toolbarComposite, SWT.FLAT | SWT.RIGHT);
        toolBar.setForeground(ColorUtil.getToolBarForegroundColor());

        tiAddMethod = new ToolItem(toolBar, SWT.NONE);
        tiAddMethod.setText(StringConstants.ADD);
        tiAddMethod.setImage(ImageConstants.IMG_16_ADD);
        tiAddMethod.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addMethod();
            }
        });
        
        tiRemoveMethod = new ToolItem(toolBar, SWT.NONE);
        tiRemoveMethod.setText(StringConstants.REMOVE);
        tiRemoveMethod.setImage(ImageConstants.IMG_16_REMOVE);
        tiRemoveMethod.setEnabled(false);
        tiRemoveMethod.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedMethod();
            }
        });
        tiRemoveMethod.setEnabled(false);

        tiEditMethod = new ToolItem(toolBar, SWT.NONE);
        tiEditMethod.setText(StringConstants.EDIT);
        tiEditMethod.setImage(ImageConstants.IMG_16_EDIT);
        tiEditMethod.setEnabled(false);
        tiEditMethod.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editSelectedMethod();
            }
        });
        tiEditMethod.setEnabled(false);
    }
    
    private void createMethodsTable(Composite parent) {
        Composite tableComposite = new Composite(parent, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tableComposite.setLayout(new FillLayout());
        
        methodTable = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        Table table = methodTable.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        
        TableViewerColumn tvcMethod = new TableViewerColumn(methodTable, SWT.NONE);
        TableColumn tcMethod = tvcMethod.getColumn();
        tcMethod.setWidth(200);
        tcMethod.setText(StringConstants.METHOD_TABLE_COL_METHOD);
        tvcMethod.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                WebServiceMethod method = (WebServiceMethod) element;
                return method.getName();
            }
        });
        
        TableViewerColumn tvcType = new TableViewerColumn(methodTable, SWT.NONE);
        TableColumn tcType = tvcType.getColumn();
        tcType.setWidth(100);
        tcType.setText(StringConstants.METHOD_TABLE_COL_TYPE);
        tvcType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                WebServiceMethod method = (WebServiceMethod) element;
                return method.getType();
            }
        });
        
        TableViewerColumn tvcDescription = new TableViewerColumn(methodTable, SWT.NONE);
        TableColumn tcDescription = tvcDescription.getColumn();
        tcDescription.setWidth(300);
        tcDescription.setText(StringConstants.METHOD_TABLE_COL_DESCRIPTION);
        tvcDescription.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                WebServiceMethod method = (WebServiceMethod) element;
                return method.getDescription();
            }
        });
        
        methodTable.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                WebServiceMethod selectedMethod = getSelectedMethod();
                boolean allowEditOrRemove;
                if (selectedMethod != null && WebServiceMethod.TYPE_CUSTOM.equals(selectedMethod.getType())) {
                    allowEditOrRemove = true;
                } else {
                    allowEditOrRemove = false;
                }
                tiEditMethod.setEnabled(allowEditOrRemove);
                tiRemoveMethod.setEnabled(allowEditOrRemove);
            }
        });
        
        methodTable.setContentProvider(ArrayContentProvider.getInstance());
        methodTable.setInput(methods);
        methodTable.refresh();
    }
    
    private void loadMethods() {
        try {
            methods = store.getWebServiceMethods();
        } catch (IOException e) {
            methods = new ArrayList<>();
        }
    }

   
    
    private void addMethod() {
        AddOrEditWebServiceMethodDialog dialog = new AddOrEditWebServiceMethodDialog(
                Display.getCurrent().getActiveShell(), null, methods, false);
        if (dialog.open() == Dialog.OK) {
            WebServiceMethod newMethod = dialog.getMethod();
            methods.add(newMethod);
            methodTable.refresh();
        }
        
    }
    
    private void editSelectedMethod() {
        WebServiceMethod selectedMethod = getSelectedMethod();
        if (selectedMethod != null) {
            int index = findMethodIndex(selectedMethod);
            AddOrEditWebServiceMethodDialog dialog = new AddOrEditWebServiceMethodDialog(
                    Display.getCurrent().getActiveShell(), selectedMethod, methods, true);
            if (dialog.open() == Dialog.OK) {
                WebServiceMethod editedMethod = dialog.getMethod();
                methods.set(index, editedMethod);
                methodTable.refresh();
            }
        }
    }
    
    private void removeSelectedMethod() {
        WebServiceMethod selectedMethod = getSelectedMethod();
        if (selectedMethod != null) {
            int index = findMethodIndex(selectedMethod);
            methods.remove(index);
            methodTable.refresh();
        }
    }
    
    private WebServiceMethod getSelectedMethod() {
        StructuredSelection selection = (StructuredSelection) methodTable.getSelection();
        if (selection != null && selection.getFirstElement() != null) {
            return (WebServiceMethod) selection.getFirstElement();
        } else {
            return null;
        }
    }
    
    private int findMethodIndex(WebServiceMethod method) {
        for (int i = 0; i < methods.size(); i++) {
            if (methods.get(i).getName().equals(method.getName())) {
                return i;
            }
        }
        return -1;
    }
    
    private WebServiceSettingStore getStore() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        return WebServiceSettingStore.create(project.getFolderLocation());
    }
    
    @Override
    protected void performApply() {
        try {
            store.saveWebServiceMethods(methods);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
    
    @Override
    public boolean performOk() {
        if (super.performOk() && isValid()) {
            performApply();
            eventBroker.post(EventConstants.UPDATE_WEBSERVICE_METHODS, null);
        }
        return true;
    }
}
