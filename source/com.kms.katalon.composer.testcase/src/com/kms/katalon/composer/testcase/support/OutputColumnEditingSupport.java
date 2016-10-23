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
import com.kms.katalon.composer.testcase.ast.treetable.IAstOutputEditableNode;
import com.kms.katalon.composer.testcase.parts.TestCasePart;

public class OutputColumnEditingSupport extends EditingSupport {

    private TreeViewer treeViewer;

    private TestCasePart parentTestCasePart;

    public OutputColumnEditingSupport(TreeViewer treeViewer, TestCasePart parentTestCasePart) {
        super(treeViewer);
        this.treeViewer = treeViewer;
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element instanceof IAstOutputEditableNode) {
            return ((IAstOutputEditableNode) element).getCellEditorForOutput(treeViewer.getTree());
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof IAstOutputEditableNode && ((IAstOutputEditableNode) element).canEditOutput());
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof IAstOutputEditableNode) {
            return ((IAstOutputEditableNode) element).getOutput();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof IAstOutputEditableNode) {
            new OperationExecutor(parentTestCasePart).executeOperation(new NodeOutputValueChangeOperation(
                    (IAstOutputEditableNode) element, value));
        }
    }

    private class NodeOutputValueChangeOperation extends AbstractOperation {
        private IAstOutputEditableNode node;

        private Object value;

        private Object oldValue;

        public NodeOutputValueChangeOperation(IAstOutputEditableNode node, Object value) {
            super(NodeOutputValueChangeOperation.class.getName());
            this.node = node;
            this.value = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            oldValue = node.getOutput();
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetOutputValue(value);
        }

        protected IStatus doSetOutputValue(final Object outputValue) {
            if (!node.setOutput(outputValue)) {
                return Status.CANCEL_STATUS;
            }
            parentTestCasePart.getTreeTableInput().setDirty(true);
            treeViewer.setSelection(new StructuredSelection(node));
            treeViewer.refresh(node);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetOutputValue(oldValue);
        }
    }
}
