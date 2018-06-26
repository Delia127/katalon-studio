package com.kms.katalon.composer.webui.recorder.dialog.provider;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.testcase.ast.treetable.AstAbstractKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.support.TestObjectEditingSupport;
import com.kms.katalon.composer.webui.recorder.ast.RecordedElementMethodCallWrapper;
import com.kms.katalon.composer.webui.recorder.dialog.RecordedStepsView;
import com.kms.katalon.objectspy.element.WebElement;

public class CapturedElementEditingSupport extends TestObjectEditingSupport {

    private RecordedStepsView recordedStepsView;

    public CapturedElementEditingSupport(TreeViewer treeViewer, RecordedStepsView recordedStepView) {
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
                && value instanceof WebElement) {
            AstAbstractKeywordTreeTableNode node = (AstAbstractKeywordTreeTableNode) element;
            ExpressionWrapper exprs = (ExpressionWrapper) node.getTestObject();
            node.setTestObject(new RecordedElementMethodCallWrapper(exprs.getParent(), (WebElement) value));
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

        protected CapturedTestObjectCellEditor(Composite parent, AstAbstractKeywordTreeTableNode e) {
            super(parent, e.getTestObjectText());
        }

        @Override
        protected Object openDialogBox(Control cellEditorWindow) {
            Object input = recordedStepsView.getCapturedObjectsView().getTreeViewer().getInput();
            @SuppressWarnings("unchecked")
            List<WebElement> webElements = input != null ? (List<WebElement>) input : Collections.emptyList();
            CapturedElementBrowserDialog dialog = new CapturedElementBrowserDialog(getParentShell(),
                    webElements, null);
            if (dialog.open() == CapturedElementBrowserDialog.OK) {
                return dialog.getReturnValue();
            }
            return null;
        }
        
        @Override
        protected void updateContents(Object value) {
            String webElementName = "";
            if (value instanceof WebElement) {
                webElementName = ((WebElement) value).getName();
            }
            if (value instanceof RecordedElementMethodCallWrapper) {
                webElementName = ((RecordedElementMethodCallWrapper) value).getWebElement().getName();
            }
            super.updateContents(webElementName);
        }
    }
}
