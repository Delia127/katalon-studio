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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.objectrepository.constant.ObjectEventConstants;
import com.kms.katalon.composer.objectrepository.part.TestObjectPart;
import com.kms.katalon.entity.repository.WebElementXpathEntity;

public class XpathValueEditingSupport extends EditingSupport {
	
    private TableViewer viewer;

    private IEventBroker eventBroker;

    private TestObjectPart testObjectPart;
    
    public XpathValueEditingSupport(TableViewer viewer, IEventBroker eventBroker, TestObjectPart testObjectPart) {
        super(viewer);
        this.viewer = viewer;
        this.eventBroker = eventBroker;
        this.testObjectPart = testObjectPart;
    }
    
    @Override
    protected CellEditor getCellEditor(Object element) {
        return new MultilineTextCellEditor(viewer.getTable());
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof WebElementXpathEntity) {
        	WebElementXpathEntity property = (WebElementXpathEntity) element;
            return property.getValue();
        }
        return StringUtils.EMPTY;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof WebElementXpathEntity && value instanceof String) {
            testObjectPart.executeOperation(
                    new XpathValueChangeOperation((WebElementXpathEntity) element, (String) value));
        }
    }

    private class MultilineTextCellEditor extends TextCellEditor {

        public MultilineTextCellEditor(Composite parent) {
            super(parent, SWT.WRAP | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        }

        @Override
        public LayoutData getLayoutData() {
            LayoutData data = new LayoutData();
            data.minimumHeight = 100;
            data.verticalAlignment = SWT.TOP;
            return data;
        }
    }

    private class XpathValueChangeOperation extends AbstractOperation {

        private WebElementXpathEntity xpath;

        private String value;

        private String oldValue;

        public XpathValueChangeOperation(WebElementXpathEntity xpath, String value) {
            super(XpathValueChangeOperation.class.getName());
            this.xpath = xpath;
            this.oldValue = xpath.getValue();
            this.value = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (value.equals(oldValue)) {
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
            xpath.setValue((String) itemValue);
            viewer.update(xpath, null);
            eventBroker.post(ObjectEventConstants.OBJECT_UPDATE_DIRTY, viewer);
            return Status.OK_STATUS;
        }
    }
}
