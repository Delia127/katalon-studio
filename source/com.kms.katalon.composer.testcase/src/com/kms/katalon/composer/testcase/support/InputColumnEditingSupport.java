package com.kms.katalon.composer.testcase.support;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.operation.OperationExecutor;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.IAstInputEditableNode;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.util.WrapperToAstTreeConverter;

public class InputColumnEditingSupport extends TypeCheckedEditingSupport<IAstInputEditableNode> {

    private ITestCasePart parentTestCasePart;

    public InputColumnEditingSupport(TreeViewer viewer, ITestCasePart parentTestCasePart) {
        super(viewer);
        this.parentTestCasePart = parentTestCasePart;
    }
    
    @Override
    protected Class<IAstInputEditableNode> getElementType() {
        return IAstInputEditableNode.class;
    }

    @Override
    protected CellEditor getCellEditorByElement(IAstInputEditableNode element) {
        return element.getCellEditorForInput(getComposite());
    }

    @Override
    protected boolean canEditElement(IAstInputEditableNode element) {
        return element.canEditInput();
    }

    @Override
    protected Object getElementValue(IAstInputEditableNode element) {
        return element.getInput();
    }

    @Override
    protected void setElementValue(IAstInputEditableNode element, Object value) {
        new OperationExecutor(parentTestCasePart).executeOperation(new NodeItemValueChangeOperation(element, value));
    }

    
    private class NodeItemValueChangeOperation extends AbstractOperation {
        private IAstInputEditableNode node;
        private Object value;
        private Object oldValue;
        
        public NodeItemValueChangeOperation(IAstInputEditableNode node, Object value) {
            super(NodeItemValueChangeOperation.class.getName());
            this.node = node;
            this.value = value;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            oldValue = node.getInput();
            if (oldValue instanceof ASTNodeWrapper) {
                oldValue = ((ASTNodeWrapper) oldValue).clone();
            }
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetInputValue(value);
        }

        protected IStatus doSetInputValue(Object inputValue) {
            if (!node.setInput(inputValue)) {
                return Status.CANCEL_STATUS;
            }

            TestCaseTreeTableInput treeTableInput = parentTestCasePart.getTreeTableInput();
            treeTableInput.setDirty(true);
            getViewer().setSelection(new StructuredSelection(node));
            if (isNodeTransformed(node, inputValue)) {
                reloadTableIfNodeTransformed(node, treeTableInput);
                return Status.OK_STATUS;
            }
            treeTableInput.refresh(node);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doSetInputValue(oldValue);
        }

        private void reloadTableIfNodeTransformed(IAstInputEditableNode element, TestCaseTreeTableInput treeTableInput) {
            AstTreeTableNode parentNode = ((AstTreeTableNode) element).getParent();
            if (parentNode != null) {
                treeTableInput.refresh(parentNode);
                return;
            }
            try {
                treeTableInput.reloadTreeTableNodes();
            } catch (InvocationTargetException | InterruptedException e) {
                LoggerSingleton.logError(e);
            }
        }

        private boolean isNodeTransformed(IAstInputEditableNode element, Object value) {
            if (!(element instanceof AstStatementTreeTableNode)) {
                return false;
            }
            AstStatementTreeTableNode statementTreeTableNode = (AstStatementTreeTableNode) element;
            List<AstTreeTableNode> newNodes = WrapperToAstTreeConverter.getInstance()
                    .convert(Arrays.asList(new StatementWrapper[] { statementTreeTableNode.getASTObject() }),
                            statementTreeTableNode);
            if (newNodes.size() != 1) {
                return true;
            }
            AstTreeTableNode newElementNode = newNodes.get(0);
            return !element.equals(newElementNode) || !element.getClass().equals(newElementNode.getClass());
        }
        
    }
}
