package com.kms.katalon.composer.global.part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
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
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.operation.OperationExecutor;
import com.kms.katalon.composer.components.part.SavableCompositePart;
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
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;
import com.kms.katalon.tracking.service.Trackings;

public class GlobalVariablePart extends CPart implements TableViewerProvider, EventHandler, SavableCompositePart{

    private Table table;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    private TableViewer tableViewer;

    private ToolItem tltmAdd, tltmRemove, tltmClear, tltmEdit, tltmMoveUp, tltmMoveDown;

    private MPart mpart;

    private Composite composite;

    private Map<GlobalVariableEntity, String> needToUpdateVariables = new HashMap<>();

    private List<GlobalVariableEntity> globalVariables = new ArrayList<GlobalVariableEntity>();

    private ExecutionProfileEntity executionProfileEntity;
    
    private ExecutionProfileCompositePart parentExecutionProfileCompositePart; 
    
    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        this.mpart = mpart;
        this.executionProfileEntity = (ExecutionProfileEntity) mpart.getObject();

        if (mpart.getParent().getParent() instanceof MGenericTile
                && ((MGenericTile<?>) mpart.getParent().getParent()) instanceof MCompositePart) {
            MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) mpart.getParent().getParent();
            if (compositePart.getObject() instanceof ExecutionProfileCompositePart) {
            	parentExecutionProfileCompositePart = ((ExecutionProfileCompositePart) compositePart.getObject());
            }
        }
        initialize(mpart, partService);
        createComposite(parent);
        registerEventListeners();
    }


    private void createComposite(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));

        Composite compositeToolbar = new Composite(composite, SWT.NONE);
        compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_compositeToolbar = new GridLayout(1, false);
        gl_compositeToolbar.marginWidth = 0;
        gl_compositeToolbar.marginHeight = 0;
        compositeToolbar.setLayout(gl_compositeToolbar);
        compositeToolbar.setBackground(ColorUtil.getCompositeBackgroundColor());

        ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT | SWT.RIGHT);
        toolBar.setForeground(ColorUtil.getToolBarForegroundColor());

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
        
        tltmMoveUp = new ToolItem(toolBar, SWT.NONE);
        tltmMoveUp.setText(StringConstants.PA_BTN_TIP_UP);
        tltmMoveUp.setImage(ImageConstants.IMG_16_MOVE_UP);
        
        tltmMoveDown = new ToolItem(toolBar, SWT.NONE);
        tltmMoveDown.setText(StringConstants.PA_BTN_TIP_DOWN);
        tltmMoveDown.setImage(ImageConstants.IMG_16_MOVE_DOWN);

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
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
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
        tblclmnName.setWidth(200);
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
        tblclmnValue.setWidth(350);
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
        tblclmnDescription.setWidth(250);
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
        Trackings.trackCreatingObject("profileVariable");
    }

    private void deleteSelectedVariables() {
        executeOperation(new DeleteVariablesOperation());
    }

    private void clearVariables() {
        executeOperation(new ClearVariableOperation());
    }

    private void editVariable() {
        executeOperation(new EditVariableOperation());
    }
    
    private void moveUpVariable() {
        executeOperation(new MoveUpVariableOperation());
    }
    
    private void moveDownVariable() {
        executeOperation(new MoveDownVariableOperation());
    }

    private void registerControlModifyListeners() {
        tltmAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                GlobalVariableBuilderDialog dialog = new GlobalVariableBuilderDialog(composite.getShell(), null,
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
        
        tltmMoveUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveUpVariable();
            }
        });
        
        tltmMoveDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveDownVariable();
            }
        });
    }

    private void setInput() {
        try {
            setInput(executionProfileEntity.getGlobalVariableEntities());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
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
    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.GLOBAL_VARIABLE_REFRESH, this);
        eventBroker.subscribe(EventConstants.EXECUTION_PROFILE_RENAMED, this);
        eventBroker.subscribe(EventConstants.EXECUTION_PROFILE_DELETED, this);
    }

	@Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.GLOBAL_VARIABLE_REFRESH: {
                setInput();
                clearUndoRedoHistory();
                needToUpdateVariables.clear();
                break;
            }
            case EventConstants.EXECUTION_PROFILE_RENAMED: {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (!(object instanceof Object)) {
                    return;
                }
                ExecutionProfileEntity renamedProfile = (ExecutionProfileEntity) object;
                if (executionProfileEntity.equals(renamedProfile)) {
                    executionProfileEntity = renamedProfile;
                    updateMPart();
                }
                break;
            }
            case EventConstants.EXECUTION_PROFILE_DELETED: {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (!(object instanceof ExecutionProfileEntity)) {
                    return;
                }
                ExecutionProfileEntity deletedProfile = (ExecutionProfileEntity) object;
                if (executionProfileEntity.equals(deletedProfile)) {
                    dispose();
                }
                break;
            }
        }
    }

    private void clearUndoRedoHistory() {
        operationExecutor.dispose();
        operationExecutor = new OperationExecutor(this);
    }

    public void setDirty(boolean isDirty) {
    	parentExecutionProfileCompositePart.setDirty(isDirty);
    }
    
    private void updateMPart() {
        mpart.setLabel(executionProfileEntity.getName());
        mpart.setElementId(EntityPartUtil.getExecutionProfilePartId(executionProfileEntity.getIdForDisplay()));
    }
	

	public void updateProfileEntityFrom(ExecutionProfileEntity entity){
		ExecutionProfileEntity oldExecutionProfileEntity = executionProfileEntity;
    	if(entity != null){
    		executionProfileEntity = entity;
			executionProfileEntity.setName(oldExecutionProfileEntity.getName());
    		executionProfileEntity.setProject(oldExecutionProfileEntity.getProject());
    		executionProfileEntity.setParentFolder(oldExecutionProfileEntity.getParentFolder());	
    		setInput();
    	}
    }


    @Persist
    @Override
    public void save() {
    	
    }

	public void updateProfilEntityWithCurrentVariables() {
		executionProfileEntity.setGlobalVariableEntities(globalVariables);	
	}
	
    public void updateVariableReferences() {
    	if(needToUpdateVariableReferences()){
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
        partService.hidePart(mpart);
        eventBroker.unsubscribe(this);
    }

    public boolean needToUpdateVariableReferences() {
        return !needToUpdateVariables.isEmpty();
    }
    

	@Override
	public List<MPart> getChildParts() {
		List<MPart> res = new ArrayList<>();
		res.add(getMPart());
		return res;
	}
	
    @Override
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    @Override
    public void markDirty() {
        setDirty(true);
    }

    @Override
    public Map<GlobalVariableEntity, String> getNeedToUpdateVariables() {
        return needToUpdateVariables;
    }

    @Override
    public void performOperation(AbstractOperation operation) {
        executeOperation(operation);
    }

    public ExecutionProfileEntity getEntity(){
    	 return executionProfileEntity;
    }

	public MPart getMPart() {
		return mpart;
	}
	
	public List<GlobalVariableEntity> getGlobalVariables(){
		return globalVariables;
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
    
    private class MoveUpVariableOperation extends AbstractOperation {
        private Integer moveUpVariable;

        public MoveUpVariableOperation() {
            super(MoveUpVariableOperation.class.getName());
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
                int indexMove = globalVariables.indexOf(selectedVariable) - 1;
                if (indexMove >= 0) {
                    GlobalVariableEntity variableBefore = globalVariables.get(indexMove);
                    if (!variableBefore.equals(selectedVariable)) {
                        Collections.swap(globalVariables, indexMove, indexMove + 1);
                    }                    
                    moveUpVariable = indexMove;
                    tableViewer.setSelection(new StructuredSelection(globalVariables.get(indexMove)));
                    break;
                }
            }
            if (moveUpVariable == null) {
                return Status.CANCEL_STATUS;
            }
            refresh();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            refresh();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (moveUpVariable != null) {
                Collections.swap(globalVariables, moveUpVariable + 1, moveUpVariable);
                tableViewer.setSelection(new StructuredSelection(globalVariables.get(moveUpVariable + 1)));
            }
            refresh();
            setDirty(true);
            return Status.OK_STATUS;
        }
    }
    
    private class MoveDownVariableOperation extends AbstractOperation {
    	private Integer moveDownVariable;

        public MoveDownVariableOperation() {
            super(MoveDownVariableOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
            if (selection.size() <= 0) {
                return Status.CANCEL_STATUS;
            }
            List<GlobalVariableEntity> selections = selection.toList();
            Collections.reverse(selections);
            for (Object selectedItem : selections) {
                if (!(selectedItem instanceof GlobalVariableEntity)) {
                    continue;
                }		
                GlobalVariableEntity selectedVariable = (GlobalVariableEntity) selectedItem;
                int indexMove = globalVariables.indexOf(selectedVariable) + 1;
                if (indexMove < globalVariables.size()) {
                    GlobalVariableEntity variableAfter = globalVariables.get(indexMove);
                    if (variableAfter.equals(selectedVariable)) {
                    	continue;
                    }
                    Collections.swap(globalVariables, indexMove - 1, indexMove);
                    moveDownVariable = indexMove;
                    tableViewer.setSelection(new StructuredSelection(globalVariables.get(indexMove)));
                    break;
                }
            }
            if (moveDownVariable == null) {
                return Status.CANCEL_STATUS;
            }
            refresh();
            setDirty(true);
            return Status.OK_STATUS;
        }
        
        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            refresh();
            setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (moveDownVariable != null) {
                Collections.swap(globalVariables, moveDownVariable - 1, moveDownVariable);
                tableViewer.setSelection(new StructuredSelection(globalVariables.get(moveDownVariable - 1)));
            }
            refresh();
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

            Point centerLocation = null;
            GlobalVariableBuilderDialog dialog = new GlobalVariableBuilderDialog(composite.getShell(), variable,
                    centerLocation, getAllGlobalVariableName());
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

    public void setVariablesFromScriptContent(String scriptContent) throws Exception {
        ExecutionProfileEntity newVariableEntityWrapper = getVariableEntityWrapperFromScriptContent(scriptContent);
        if (newVariableEntityWrapper != null) {
            globalVariables.clear();
            globalVariables.addAll(newVariableEntityWrapper.getGlobalVariableEntities());
        }else{
            globalVariables.clear();
            globalVariables.addAll(new ArrayList<GlobalVariableEntity>());
        }
        tableViewer.setInput(globalVariables);
        refresh();
    }
    
    public ExecutionProfileEntity getVariableEntityWrapperFromScriptContent(String scriptContent) throws Exception{
        ExecutionProfileEntity newVariableEntityWrapper = null;
        if(scriptContent != null&& scriptContent != ""){
            newVariableEntityWrapper = GlobalVariableController.getInstance().toExecutionProfileEntity(scriptContent);
        }
        return newVariableEntityWrapper;
    }

    public ExecutionProfileEntity getExecutionProfileEntity() {
        updateProfilEntityWithCurrentVariables();
        return executionProfileEntity;
    }

    @Override
    public boolean isDirty() {
        return mpart.isDirty();
    }
}
