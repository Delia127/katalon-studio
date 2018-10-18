package com.kms.katalon.composer.testcase.support;

import java.util.List;

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
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.IAstObjectEditableNode;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestObjectEditingSupport extends EditingSupport {

    private TreeViewer treeViewer;

    private ITestCasePart parentTestCasePart;

    public TestObjectEditingSupport(TreeViewer treeViewer, ITestCasePart parentTestCasePart) {
        super(treeViewer);
        this.treeViewer = treeViewer;
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        IAstObjectEditableNode editableNode = (IAstObjectEditableNode) element;
        editableNode.setTestCasePart(parentTestCasePart);
        return editableNode.getCellEditorForTestObject(treeViewer.getTree());
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof IAstObjectEditableNode && ((IAstObjectEditableNode) element).canEditTestObject());
    }

    @Override
    protected Object getValue(Object element) {
        return ((IAstObjectEditableNode) element).getTestObject();
    }

    @Override
    protected void setValue(Object element, Object value) {
        new OperationExecutor(parentTestCasePart).executeOperation(new NodeObjectValueChangeOperation((IAstObjectEditableNode) element, value));
    }
    
    private class NodeObjectValueChangeOperation extends AbstractOperation {
        private IAstObjectEditableNode node;
        private Object value;
        private Object oldValue;
        private List<VariableEntity> testCaseVariables;
        
        public NodeObjectValueChangeOperation(IAstObjectEditableNode node, Object value) {
            super(NodeObjectValueChangeOperation.class.getName());
            this.node = node;
            this.value = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            oldValue = node.getTestObject();
            if (oldValue instanceof ASTNodeWrapper) {
                oldValue = ((ASTNodeWrapper) oldValue).clone();
            }
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            IStatus status = doSetTestObjectValue(value);
            if (status == Status.CANCEL_STATUS) {
                return status;
            }
            if (node instanceof AstCallTestCaseKeywordTreeTableNode) {
                testCaseVariables = ((AstCallTestCaseKeywordTreeTableNode) node).getCallTestCaseVariables();
                parentTestCasePart.addVariables(testCaseVariables.toArray(new VariableEntity[testCaseVariables.size()]));
            }
            return status;
        }

        protected IStatus doSetTestObjectValue(Object objectValue) {
            if (!node.setTestObject(objectValue)) {
                return Status.CANCEL_STATUS;
            }
            parentTestCasePart.getTreeTableInput().setDirty(true);
            treeViewer.setSelection(new StructuredSelection(node));
            treeViewer.refresh(node);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            IStatus status = doSetTestObjectValue(oldValue);
            if (status == Status.CANCEL_STATUS) {
                return status;
            }
            if (node instanceof AstCallTestCaseKeywordTreeTableNode) {
                parentTestCasePart.deleteVariables(testCaseVariables);
            }
            return status;
        }
    }
}
