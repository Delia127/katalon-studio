package com.kms.katalon.composer.objectrepository.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.part.TestObjectPart;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertyNameEditingSupport extends EditingSupport {

    private TableViewer viewer;

    private IEventBroker eventBroker;

    private TestObjectPart testObjectPart;

    public PropertyNameEditingSupport(TableViewer viewer, IEventBroker eventBroker, TestObjectPart testObjectPart) {
        super(viewer);
        this.viewer = viewer;
        this.eventBroker = eventBroker;
        this.testObjectPart = testObjectPart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
    	return  new CustomTextCellEditor(viewer.getTable());
    }
    
    private class CustomTextCellEditor extends TextCellEditor {
    	
        public CustomTextCellEditor(Composite parent) {
            super(parent);
        }

        @Override
        public LayoutData getLayoutData() {
            LayoutData result = super.getLayoutData();
            result.minimumHeight = viewer.getTable().getItemHeight();
            return result;
        }
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof WebElementPropertyEntity) {
            WebElementPropertyEntity property = (WebElementPropertyEntity) element;
            return property.getName();
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof WebElementPropertyEntity && value instanceof String) {
            testObjectPart.executeOperation(
                    new PropertyNameChangeOperation((WebElementPropertyEntity) element, (String) value));
        }
    }

    public class PropertyNameChangeOperation extends AbstractOperation {

        private WebElementPropertyEntity property;

        private String value;

        private String oldValue;

        public PropertyNameChangeOperation(WebElementPropertyEntity property, String value) {
            super(PropertyNameChangeOperation.class.getName());
            this.property = property;
            this.oldValue = property.getName();
            this.value = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (value.equals(StringUtils.EMPTY) || value.equals(oldValue)) {
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
            return doSetItemValue(oldValue);
        }

        protected IStatus doSetItemValue(String itemValue) {
            property.setName((String) itemValue);
            viewer.update(property, null);
            eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, viewer);
            return Status.OK_STATUS;
        }
    }

}
