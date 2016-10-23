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
import com.kms.katalon.composer.testcase.ast.editors.TestStepDescriptionBuilderCellEditor;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.parts.TestCasePart;

public class DescriptionColumnEditingSupport extends EditingSupport {
    private TreeViewer treeViewer;

    private TestCasePart parentTestCasePart;

    public DescriptionColumnEditingSupport(TreeViewer viewer, TestCasePart parentTestCasePart) {
        super(viewer);
        this.treeViewer = viewer;
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new TestStepDescriptionBuilderCellEditor(treeViewer.getTree());
    }

    @Override
    protected boolean canEdit(Object element) {
        return element instanceof AstStatementTreeTableNode
                && ((AstStatementTreeTableNode) element).canHaveDescription();
    }

    @Override
    protected Object getValue(Object element) {
        return ((AstStatementTreeTableNode) element).getDescription();
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!(value instanceof String)) {
            return;
        }
        new OperationExecutor(parentTestCasePart).executeOperation(new NodeDescriptionValueChangeOperation(
                (AstStatementTreeTableNode) element, (String) value));
    }

    private class NodeDescriptionValueChangeOperation extends AbstractOperation {
        private AstStatementTreeTableNode node;

        private String value;

        private String oldValue;

        public NodeDescriptionValueChangeOperation(AstStatementTreeTableNode node, String value) {
            super(NodeDescriptionValueChangeOperation.class.getName());
            this.node = node;
            this.value = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            oldValue = node.getDescription();
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetDescriptionValue(value);
        }

        protected IStatus doSetDescriptionValue(String description) {
            if (!node.setDescription(description)) {
                return Status.CANCEL_STATUS;
            }
            TestCaseTreeTableInput treeTableInput = parentTestCasePart.getTreeTableInput();
            treeTableInput.setDirty(true);
            treeViewer.setSelection(new StructuredSelection(node));
            treeTableInput.refresh(node);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetDescriptionValue(oldValue);
        }
    }
}
