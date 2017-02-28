package com.kms.katalon.composer.global.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import com.kms.katalon.composer.global.editor.GlobalVariableSelectionCellEditor;
import com.kms.katalon.composer.global.provider.TableViewerProvider;
import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableEdittingSupport extends EditingSupport {

    private TableViewerProvider provider;

    private GlobalVariableEntity edittedVariable;

    public GlobalVariableEdittingSupport(TableViewerProvider provider) {
        super(provider.getTableViewer());
        this.provider = provider;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (!(element instanceof GlobalVariableEntity)) {
            return null;
        }
        return new GlobalVariableSelectionCellEditor((Composite) getViewer().getControl(),
                (GlobalVariableEntity) element, getAllGlobalVariableName());
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof GlobalVariableEntity) {
            return ((GlobalVariableEntity) element);
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!(value instanceof GlobalVariableEntity)) {
            return;
        }
        this.edittedVariable = (GlobalVariableEntity) value;
        provider.performOperation(new EditVariableOperation());
    }

    private List<String> getAllGlobalVariableName() {
        TableViewer tableViewer = provider.getTableViewer();
        if (tableViewer == null || tableViewer.getTable().isDisposed()) {
            return Collections.emptyList();
        }
        List<String> varNames = new ArrayList<String>();
        for (TableItem item : tableViewer.getTable().getItems()) {
            varNames.add(((GlobalVariableEntity) item.getData()).getName());
        }
        return varNames;
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
            variable = (GlobalVariableEntity) ((StructuredSelection) provider.getTableViewer().getSelection())
                    .getFirstElement();

            newVariable = GlobalVariableEdittingSupport.this.edittedVariable;
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
                GlobalVariableEdittingSupport.this.provider.getNeedToUpdateVariables().put(variable,
                        oldVariable.getName());
            }
            doEditVariable(newVariable);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (isNameChanged) {
                Map<GlobalVariableEntity, String> needToUpdateVariables = GlobalVariableEdittingSupport.this.provider
                        .getNeedToUpdateVariables();
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
            getViewer().refresh(variable);
            provider.markDirty();
        }

    }

}
