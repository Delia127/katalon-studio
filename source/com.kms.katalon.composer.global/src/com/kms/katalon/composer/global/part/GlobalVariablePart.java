package com.kms.katalon.composer.global.part;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.operation.OperationExecutor;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.global.constants.StringConstants;
import com.kms.katalon.composer.global.dialog.GlobalVariableBuilderDialog;
import com.kms.katalon.composer.global.provider.TableViewerProvider;
import com.kms.katalon.composer.global.support.GlobalVariableEdittingSupport;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.custom.parser.GlobalVariableParser;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class GlobalVariablePart extends CPart implements EventHandler, TableViewerProvider {

    private Table table;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    private TableViewer tableViewer;

    private ToolItem tltmAdd, tltmRemove;

    private MPart mpart;

    private Composite composite;

    private ToolItem tltmClear;

    private ToolItem tltmEdit;

    private ToolItem tltmRefresh;

    private Map<GlobalVariableEntity, String> needToUpdateVariables = new HashMap<>();
    
    private List<GlobalVariableEntity> globalVariables = new ArrayList<GlobalVariableEntity>();

    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        this.mpart = mpart;
        initialize(mpart, partService);
        createComposite(parent);
        registerEventListeners();
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.GLOBAL_VARIABLE_REFRESH, this);
    }

    private void createComposite(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

        Composite compositeToolbar = new Composite(composite, SWT.NONE);
        compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_compositeToolbar = new GridLayout(1, false);
        gl_compositeToolbar.marginWidth = 0;
        gl_compositeToolbar.marginHeight = 0;
        compositeToolbar.setLayout(gl_compositeToolbar);
        compositeToolbar.setBackground(ColorUtil.getCompositeBackgroundColor());

        ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT);

        tltmAdd = new ToolItem(toolBar, SWT.NONE);
        tltmAdd.setText(StringConstants.PA_BTN_TIP_ADD);
        tltmAdd.setImage(ImageConstants.IMG_16_ADD);

        tltmEdit = new ToolItem(toolBar, SWT.NONE);
        tltmEdit.setText(StringConstants.PA_BTN_TIP_EDIT);
        tltmEdit.setImage(ImageConstants.IMG_16_EDIT);
        tltmEdit.setDisabledImage(ImageConstants.IMG_16_EDIT_DISABLED);

        tltmRemove = new ToolItem(toolBar, SWT.NONE);
        tltmRemove.setText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemove.setImage(ImageConstants.IMG_16_REMOVE);
        tltmRemove.setDisabledImage(ImageConstants.IMG_16_REMOVE_DISABLED);

        tltmClear = new ToolItem(toolBar, SWT.NONE);
        tltmClear.setText(StringConstants.PA_BTN_TIP_CLEAR);
        tltmClear.setImage(ImageConstants.IMG_16_CLEAR);

        tltmRefresh = new ToolItem(toolBar, SWT.NONE);
        tltmRefresh.setText(StringConstants.PA_BTN_TIP_REFRESH);
        tltmRefresh.setImage(ImageConstants.IMG_16_REFRESH);

        Composite compositeTable = new Composite(composite, SWT.NONE);
        GridLayout gl_compositeTable = new GridLayout(1, false);
        gl_compositeTable.marginWidth = 0;
        gl_compositeTable.marginHeight = 0;
        compositeTable.setLayout(gl_compositeTable);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        compositeTable.setBounds(0, 0, 64, 64);

        tableViewer = new TableViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                enableDisableBtns();
            }
        });
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        Menu popupMenu = new Menu(table);
        MenuItem showPreferenceMenuItem = new MenuItem(popupMenu, SWT.CASCADE);
        showPreferenceMenuItem.setText(StringConstants.PA_MENU_CONTEXT_SHOW_PREFERENCES);
        showPreferenceMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

                if (selection == null || selection.getFirstElement() == null)
                    return;

                eventBroker.post(EventConstants.GLOBAL_VARIABLE_SHOW_REFERENCES, selection.getFirstElement());
            }
        });
        table.setMenu(popupMenu);

        TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnName = tableViewerColumnName.getColumn();
        tblclmnName.setWidth(100);
        tblclmnName.setText(StringConstants.PA_COL_NAME);
        tableViewerColumnName.setEditingSupport(new GlobalVariableEdittingSupport(this));
        tableViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof GlobalVariableEntity) {
                    return ((GlobalVariableEntity) element).getName();
                }
                return "";
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
        tblclmnValue.setWidth(150);
        tblclmnValue.setText(StringConstants.PA_COL_VALUE);
        tableViewerColumnValue.setEditingSupport(new GlobalVariableEdittingSupport(this));
        tableViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof GlobalVariableEntity) {
                    return ((GlobalVariableEntity) element).getInitValue();
                }
                return "";
            }
        });

        TableViewerColumn tableViewerColumnDescription = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnDescription = tableViewerColumnDescription.getColumn();
        tblclmnDescription.setWidth(150);
        tblclmnDescription.setText(StringConstants.PA_COL_DESCRIPTION);
        tableViewerColumnDescription.setEditingSupport(new GlobalVariableEdittingSupport(this));
        tableViewerColumnDescription.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof GlobalVariableEntity) {
                    return ((GlobalVariableEntity) element).getDescription();
                }
                return "";
            }
        });
        
        ColumnViewerUtil.setTableActivation(tableViewer);
        setInput();
        registerControlModifyListeners();
    }

    private void addNewVariable(GlobalVariableEntity variableEntity) {
        executeOperation(new AddNewVariableOperation(variableEntity));
    }

    private void deleteSelectedVariables() {
        executeOperation(new DeleteVariablesOperation());
    }

    private void clearVariables() {
        executeOperation(new ClearVariableOperation());
    }

    private void refreshVariables() {
        executeOperation(new RefreshOperation());
    }

    private void editVariable() {
        executeOperation(new EditVariableOperation());
    }

    private void registerControlModifyListeners() {
        tltmAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Point pt = composite.toDisplay(1, 1);
                GlobalVariableBuilderDialog dialog = new GlobalVariableBuilderDialog(composite.getShell(), pt,
                        getAllGlobalVariableName());
                if (dialog.open() == Dialog.OK) {
                    addNewVariable(dialog.getVariableEntity());
                }
            }
        });

        tltmEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                editVariable();
            }
        });

        tltmRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                deleteSelectedVariables();
            }
        });

        tltmClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearVariables();
            }
        });

        tltmRefresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                refreshVariables();
            }
        });
    }

    @SuppressWarnings("restriction")
    private void setInput() {
        try {
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            if (project != null) {
                composite.setVisible(true);
                List<GlobalVariableEntity> globalVariables = GlobalVariableController.getInstance()
                        .getAllGlobalVariables(project);
                setInput(globalVariables);
            } else {
                composite.setVisible(false);
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

    private void setInput(List<GlobalVariableEntity> newInput) {
        globalVariables.clear();
        globalVariables.addAll(newInput);
        tableViewer.setInput(globalVariables);
        refresh();
    }

    protected void refresh() {
        tableViewer.refresh();
        enableDisableBtns();
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.GLOBAL_VARIABLE_REFRESH)) {
            setInput();
            clearUndoRedoHistory();
            needToUpdateVariables.clear();
        }
    }

    private void clearUndoRedoHistory() {
        operationExecutor.dispose();
        operationExecutor = new OperationExecutor(this);
    }

    private void setDirty(boolean isDirty) {
        mpart.setDirty(isDirty);
    }

    @SuppressWarnings("restriction")
    @Persist
    public void save() {
        List<GlobalVariableEntity> variables = new ArrayList<GlobalVariableEntity>();
        List<String> names = new ArrayList<String>();
        for (TableItem item : tableViewer.getTable().getItems()) {
            GlobalVariableEntity globalVariable = (GlobalVariableEntity) item.getData();
            if (!isValidName(globalVariable.getName())) {
                MessageDialog.openWarning(null, StringConstants.PA_WARN_TITLE_INVALID_VAR,
                        MessageFormat.format(StringConstants.PA_WARN_MSG_INVALID_VAR_NAME, globalVariable.getName()));
                return;
            }
            if (names.contains(globalVariable.getName())) {
                MessageDialog.openWarning(null, StringConstants.PA_WARN_TITLE_INVALID_VAR,
                        MessageFormat.format(StringConstants.PA_WARN_MSG_DUPLICATE_VAR_NAME, globalVariable.getName()));
                return;
            }
            if (globalVariable.getInitValue().isEmpty()) {
                globalVariable.setInitValue("''");
            }
            names.add(globalVariable.getName());
            variables.add(globalVariable);
        }
        try {

            GlobalVariableController.getInstance().updateVariables(variables,
                    ProjectController.getInstance().getCurrentProject());
            if (needToUpdateVariableReferences()) {
                updateVariableReferences();
            }
            setDirty(false);
        } catch (Exception e) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_ALL_VAR);
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

    private void updateVariableReferences() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        try {
            for (Entry<GlobalVariableEntity, String> needToUpdateVariable : needToUpdateVariables.entrySet()) {
                String globalVariablePrefix = GlobalVariableParser.GLOBAL_VARIABLE_CLASS_NAME + ".";
                String oldValue = globalVariablePrefix + needToUpdateVariable.getValue();
                String newValue = globalVariablePrefix + needToUpdateVariable.getKey().getName();
                GroovyRefreshUtil.updateScriptReferencesInTestCaseAndCustomScripts(oldValue, newValue, currentProject);
            }
            needToUpdateVariables.clear();
        } catch (CoreException | IOException e) {
            MessageDialog.openWarning(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_UPDATE_VAR_REFERENCES);
        }
    }

    private boolean isValidName(String name) {
        return GroovyConstants.isValidVariableName(name);
    }

    /**
     * Enable/Disable Edit and Delete button
     */
    private void enableDisableBtns() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        boolean isEnabled = !(selection == null || selection.getFirstElement() == null);
        tltmEdit.setEnabled(isEnabled);
        tltmRemove.setEnabled(isEnabled);
    }

    private List<String> getAllGlobalVariableName() {
        List<String> varNames = new ArrayList<String>();
        if (tableViewer == null || tableViewer.getTable().isDisposed())
            return varNames;
        for (TableItem item : tableViewer.getTable().getItems()) {
            varNames.add(((GlobalVariableEntity) item.getData()).getName());
        }
        return varNames;
    }

    @PreDestroy
    @Override
    public void dispose() {
        super.dispose();
    }

    private boolean needToUpdateVariableReferences() {
        return !needToUpdateVariables.isEmpty();
    }

    private class AddNewVariableOperation extends AbstractOperation {
        private GlobalVariableEntity variable;

        public AddNewVariableOperation(GlobalVariableEntity variable) {
            super(AddNewVariableOperation.class.getName());
            this.variable = variable;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (variable == null) {
                return Status.CANCEL_STATUS;
            }
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            globalVariables.add(variable);
            refresh();
            tableViewer.setSelection(new StructuredSelection(variable));
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            globalVariables.remove(variable);
            refresh();
            setDirty(true);
            return Status.OK_STATUS;
        }
    }

    private class DeleteVariablesOperation extends AbstractOperation {
        private Map<GlobalVariableEntity, Integer> deletedVariables = new LinkedHashMap<>();

        public DeleteVariablesOperation() {
            super(DeleteVariablesOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
            if (selection.size() <= 0) {
                return Status.CANCEL_STATUS;
            }
            for (Object selectedItem : selection.toArray()) {
                if (!(selectedItem instanceof GlobalVariableEntity)) {
                    continue;
                }
                GlobalVariableEntity selectedVariable = (GlobalVariableEntity) selectedItem;
                int index = globalVariables.indexOf(selectedVariable);
                if (index != -1) {
                    deletedVariables.put(selectedVariable, index);
                }
            }
            globalVariables.removeAll(selection.toList());
            refresh();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            globalVariables.removeAll(deletedVariables.keySet());
            refresh();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            for (Entry<GlobalVariableEntity, Integer> deletedVariable : deletedVariables.entrySet()) {
                globalVariables.add(deletedVariable.getValue(), deletedVariable.getKey());
            }
            refresh();
            tableViewer.setSelection(new StructuredSelection(deletedVariables.keySet().toArray()));
            setDirty(true);
            return Status.OK_STATUS;
        }
    }

    private class ClearVariableOperation extends AbstractOperation {
        private List<GlobalVariableEntity> oldInput;

        public ClearVariableOperation() {
            super(ClearVariableOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (globalVariables.isEmpty()) {
                return Status.CANCEL_STATUS;
            }
            oldInput = new ArrayList<>(globalVariables);
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            globalVariables.clear();
            refresh();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setInput(oldInput);
            setDirty(true);
            return Status.OK_STATUS;
        }
    }

    private class RefreshOperation extends AbstractOperation {
        private List<GlobalVariableEntity> oldInput;

        private List<GlobalVariableEntity> newGlobalVariables;

        public RefreshOperation() {
            super(RefreshOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            try {
                ProjectEntity project = ProjectController.getInstance().getCurrentProject();
                if (project == null) {
                    return Status.CANCEL_STATUS;
                }
                if (mpart.isDirty()) {
                    MessageDialog.openWarning(tltmRefresh.getDisplay().getActiveShell(), StringConstants.WARN,
                            StringConstants.PA_INFO_MSG_REQUIRE_SAVE_B4_REFRESH);
                    return Status.CANCEL_STATUS;
                }
                oldInput = new ArrayList<>(globalVariables);
                newGlobalVariables = GlobalVariableController.getInstance().getAllGlobalVariables(project);

                if (newGlobalVariables == oldInput) {
                    return Status.CANCEL_STATUS;
                }
                return redo(monitor, info);
            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
                return Status.CANCEL_STATUS;
            }
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setInput(newGlobalVariables);
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            setInput(oldInput);
            setDirty(true);
            return Status.OK_STATUS;
        }
    }

    private class EditVariableOperation extends AbstractOperation {
        private GlobalVariableEntity oldVariable;
        private GlobalVariableEntity variable;
        private GlobalVariableEntity newVariable;
        private boolean isNameChanged;

        public EditVariableOperation() {
            super(EditVariableOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (tableViewer.getSelection().isEmpty()) {
                return Status.CANCEL_STATUS;
            }
            Object firstElement = ((StructuredSelection) tableViewer.getSelection()).getFirstElement();
            if (firstElement == null || !(firstElement instanceof GlobalVariableEntity)) {
                return Status.CANCEL_STATUS;
            }
            variable = (GlobalVariableEntity) firstElement;

            Point pt = composite.toDisplay(1, 1);
            GlobalVariableBuilderDialog dialog = new GlobalVariableBuilderDialog(composite.getShell(), variable, pt,
                    getAllGlobalVariableName());
            if (dialog.open() != Dialog.OK) {
                return Status.CANCEL_STATUS;
            }
            newVariable = dialog.getVariableEntity();
            boolean isChanged = (!variable.getDescription().equals(newVariable.getDescription())
                    || !variable.getInitValue().equals(newVariable.getInitValue())
                    || !variable.getName().equals(newVariable.getName()));

            if (!isChanged) {
                return Status.CANCEL_STATUS;
            }
            oldVariable = variable.clone();
            isNameChanged = !variable.getName().equals(newVariable.getName());
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (isNameChanged) {
                needToUpdateVariables.put(variable, oldVariable.getName());
            }
            doEditVariable(newVariable);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (isNameChanged) {
                if (needToUpdateVariables.containsKey(variable)) {
                    needToUpdateVariables.remove(variable);
                } else {
                    needToUpdateVariables.put(variable, newVariable.getName());
                }
            }
            doEditVariable(oldVariable);
            return Status.OK_STATUS;
        }

        private void doEditVariable(GlobalVariableEntity changedVariable) {
            variable.setDescription(changedVariable.getDescription());
            variable.setInitValue(changedVariable.getInitValue());
            variable.setName(changedVariable.getName());
            tableViewer.refresh(variable);
            setDirty(true);
        }

    }

    @Override
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    @Override
    public void markDirty() {
       setDirty(true); 
    }
}
