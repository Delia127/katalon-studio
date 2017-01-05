package com.kms.katalon.composer.objectrepository.support;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.part.TestObjectPart;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertyConditionEditingSupport extends EditingSupport {

    private TableViewer viewer;

    private IEventBroker eventBroker;

    private TestObjectPart testObjectPart;

    public PropertyConditionEditingSupport(TableViewer viewer, IEventBroker eventBroker,
            TestObjectPart testObjectPart) {
        super(viewer);
        this.viewer = viewer;
        this.eventBroker = eventBroker;
        this.testObjectPart = testObjectPart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element != null && element instanceof WebElementPropertyEntity) {
            return new ComboBoxCellEditor(viewer.getTable(), WebElementPropertyEntity.MATCH_CONDITION.getTextVlues(),
                    SWT.NONE);
        } else {
            return null;
        }
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof WebElementPropertyEntity) {
            WebElementPropertyEntity webProperty = (WebElementPropertyEntity) element;
            return WebElementPropertyEntity.MATCH_CONDITION.indexOf(webProperty.getMatchCondition());
        }
        return -1;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof WebElementPropertyEntity && value instanceof Integer) {
            int index = (Integer) value;
            if (index < 0) {
                return;
            }
            testObjectPart.executeOperation(new PropertyConditionChangeOperation((WebElementPropertyEntity) element,
                    WebElementPropertyEntity.MATCH_CONDITION.values()[index].getText()));
        }
    }

    private class PropertyConditionChangeOperation extends AbstractOperation {

        private WebElementPropertyEntity property;

        private String newValue;

        private String oldValue;

        public PropertyConditionChangeOperation(WebElementPropertyEntity property, String value) {
            super(PropertyConditionChangeOperation.class.getName());
            this.property = property;
            this.oldValue = property.getMatchCondition();
            this.newValue = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (newValue.equals(oldValue)) {
                return Status.CANCEL_STATUS;
            }
            return doSetItemValue(newValue);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetItemValue(newValue);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetItemValue(oldValue);
        }

        protected IStatus doSetItemValue(String itemValue) {
            property.setMatchCondition(itemValue);
            viewer.update(property, null);
            eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, viewer);
            return Status.OK_STATUS;
        }
    }

}
