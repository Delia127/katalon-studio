package com.kms.katalon.composer.testcase.support;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.components.operation.OperationExecutor;
import com.kms.katalon.composer.testcase.ast.treetable.IAstItemEditableNode;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.parts.TestCasePart;

public class ItemColumnEditingSupport extends EditingSupport {
    private TreeViewer treeViewer;

    private TestCasePart parentTestCasePart;

    public ItemColumnEditingSupport(TreeViewer viewer, TestCasePart parentTestCasePart) {
        super(viewer);
        this.treeViewer = viewer;
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element instanceof IAstItemEditableNode) {
            return ((IAstItemEditableNode) element).getCellEditorForItem(treeViewer.getTree());
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof IAstItemEditableNode && ((IAstItemEditableNode) element).canEditItem());
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof IAstItemEditableNode) {
            return ((IAstItemEditableNode) element).getItem();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!(element instanceof IAstItemEditableNode)) {
            return;
        }
        new OperationExecutor(parentTestCasePart).executeOperation(new NodeItemValueChangeOperation(
                (IAstItemEditableNode) element, value));
    }

    private class NodeItemValueChangeOperation extends AbstractOperation {
        private IAstItemEditableNode node;

        private Object value;

        private Object oldValue;

        public NodeItemValueChangeOperation(IAstItemEditableNode node, Object value) {
            super(NodeItemValueChangeOperation.class.getName());
            this.node = node;
            this.value = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            oldValue = cloneValue(node.getItem());
            value = cloneValue(value);
            return redo(monitor, info);
        }

        private Object cloneValue(Object value) {
            if (value instanceof ASTNodeWrapper) {
                return ((ASTNodeWrapper) value).clone();
            }
            return value;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetItemValue(value);
        }

        protected IStatus doSetItemValue(final Object itemValue) {
            Object tempValue = cloneValue(itemValue);
            if (!node.setItem(tempValue)) {
                return Status.CANCEL_STATUS;
            }
            parentTestCasePart.getTreeTableInput().setDirty(true);
            treeViewer.setSelection(new StructuredSelection(node));
            treeViewer.refresh(node);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetItemValue(oldValue);
        }
    }
}
