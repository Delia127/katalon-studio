package com.kms.katalon.composer.objectrepository.support;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.part.TestObjectPart;
import com.kms.katalon.composer.objectrepository.provider.ObjectPropetiesTableViewer;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertySelectedEditingSupport extends EditingSupport {

    private ObjectPropetiesTableViewer viewer;

    private IEventBroker eventBroker;

    private TestObjectPart testObjectPart;

    public PropertySelectedEditingSupport(ObjectPropetiesTableViewer viewer, IEventBroker eventBroker,
            TestObjectPart testObjectPart) {
        super(viewer);
        this.viewer = viewer;
        this.eventBroker = eventBroker;
        this.testObjectPart = testObjectPart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new CheckboxCellEditor(viewer.getTable());
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof WebElementPropertyEntity) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            return property.getIsSelected();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof WebElementPropertyEntity && value instanceof Boolean) {
            testObjectPart.executeOperation(
                    new PropertySelectChangeOperation((WebElementPropertyEntity) element, (boolean) value));
        }
    }

    private class PropertySelectChangeOperation extends AbstractOperation {

        private WebElementPropertyEntity property;

        private boolean value;

        public PropertySelectChangeOperation(WebElementPropertyEntity property, boolean value) {
            super(PropertySelectChangeOperation.class.getName());
            this.property = property;
            this.value = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (value == property.getIsSelected()) {
                return Status.CANCEL_STATUS;
            }
            return doSetItemValue(value);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetItemValue(value);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetItemValue(!value);
        }

        protected IStatus doSetItemValue(boolean itemValue) {
            property.setIsSelected(itemValue);
            viewer.refreshIsSelected();
            viewer.update(property, null);
            eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, viewer);
            return Status.OK_STATUS;
        }
    }

}
