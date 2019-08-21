package com.kms.katalon.composer.windows.spy;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.testcase.ast.treetable.AstAbstractKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.support.TestObjectEditingSupport;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.record.WindowsElementMethodCallWrapper;

public class CapturedWindowsElementEdittingSupport extends TestObjectEditingSupport {

    private WindowsRecordedStepsView recordedStepsView;

    public CapturedWindowsElementEdittingSupport(TreeViewer treeViewer, WindowsRecordedStepsView recordedStepView) {
        super(treeViewer, recordedStepView);
        this.recordedStepsView = recordedStepView;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element instanceof AstAbstractKeywordTreeTableNode
                && !(element instanceof AstCallTestCaseKeywordTreeTableNode)) {
            CapturedTestObjectCellEditor capturedTestObjectCellEditor = new CapturedTestObjectCellEditor(
                    (Composite) getViewer().getControl(), (AstAbstractKeywordTreeTableNode) element);
            return capturedTestObjectCellEditor;
        }
        return super.getCellEditor(element);
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof AstAbstractKeywordTreeTableNode
                && !(element instanceof AstCallTestCaseKeywordTreeTableNode)
                && value instanceof CapturedWindowsElement) {
            AstAbstractKeywordTreeTableNode node = (AstAbstractKeywordTreeTableNode) element;
            ExpressionWrapper exprs = (ExpressionWrapper) node.getTestObject();
            node.setTestObject(new WindowsElementMethodCallWrapper(exprs.getParent(), (CapturedWindowsElement) value));
            getViewer().refresh(node);
        } else {
            super.setValue(element, value);
        }
    }

    @Override
    protected Object getValue(Object element) {
        return super.getValue(element);
    }

    private class CapturedTestObjectCellEditor extends AbstractDialogCellEditor {

        private AstAbstractKeywordTreeTableNode selectedNode;

        protected CapturedTestObjectCellEditor(Composite parent, AstAbstractKeywordTreeTableNode e) {
            super(parent, e.getTestObjectText());
            selectedNode = e;
        }

        @Override
        protected Object openDialogBox(Control cellEditorWindow) {
            Object input = recordedStepsView.getCapturedElementsTableViewer().getCapturedElements();
            @SuppressWarnings("unchecked")
            List<CapturedWindowsElement> webElements = input != null ? (List<CapturedWindowsElement>) input
                    : Collections.emptyList();
            CapturedWindowsElementBrowserDialog dialog = new CapturedWindowsElementBrowserDialog(getParentShell(),
                    webElements, getSelectedCapturedWindowsElement());
            if (dialog.open() == CapturedWindowsElementBrowserDialog.OK) {
                return dialog.getReturnValue();
            }
            return null;
        }

        @Override
        protected void updateContents(Object value) {
            String webElementName = "";
            if (value instanceof CapturedWindowsElement) {
                webElementName = ((CapturedWindowsElement) value).getName();
            }
            if (value instanceof WindowsElementMethodCallWrapper) {
                webElementName = ((WindowsElementMethodCallWrapper) value).getWindowsElement().getName();
            }
            super.updateContents(webElementName);
        }

        private CapturedWindowsElement getSelectedCapturedWindowsElement() {
            if (selectedNode == null) {
                return null;
            }
            if (selectedNode instanceof AstBuiltInKeywordTreeTableNode) {
                AstBuiltInKeywordTreeTableNode kwNode = (AstBuiltInKeywordTreeTableNode) selectedNode;
                Object testObjectMethodCall = kwNode.getTestObject();
                if (testObjectMethodCall instanceof WindowsElementMethodCallWrapper) {
                    WindowsElementMethodCallWrapper methodCallExprs = (WindowsElementMethodCallWrapper) testObjectMethodCall;
                    return methodCallExprs.getWindowsElement();
                }
            }
            return null;
        }
    }
}
