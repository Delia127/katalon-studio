package com.kms.katalon.composer.mobile.recorder.types;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.types.MobileElementMethodCallWrapper;
import com.kms.katalon.composer.mobile.recorder.components.CapturedMobileElementBrowserDialog;
import com.kms.katalon.composer.mobile.recorder.composites.MobileRecordedStepsViewComposite;
import com.kms.katalon.composer.testcase.ast.treetable.AstAbstractKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.support.TestObjectEditingSupport;

public class CapturedMobileElementEdittingSupport extends TestObjectEditingSupport {
    
    private MobileRecordedStepsViewComposite recordedStepsView;
    
    public CapturedMobileElementEdittingSupport(TreeViewer treeViewer, MobileRecordedStepsViewComposite recordedStepView) {
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
                && value instanceof CapturedMobileElement) {
            AstAbstractKeywordTreeTableNode node = (AstAbstractKeywordTreeTableNode) element;
            ExpressionWrapper exprs = (ExpressionWrapper) node.getTestObject();
            node.setTestObject(new MobileElementMethodCallWrapper(exprs.getParent(), (CapturedMobileElement) value));
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
            List<CapturedMobileElement> webElements = input != null ? (List<CapturedMobileElement>) input
                    : Collections.emptyList();
            CapturedMobileElementBrowserDialog dialog = new CapturedMobileElementBrowserDialog(getParentShell(),
                    webElements, getSelectedCapturedMobileElement());
            if (dialog.open() == CapturedMobileElementBrowserDialog.OK) {
                return dialog.getReturnValue();
            }
            return null;
        }

        @Override
        protected void updateContents(Object value) {
            String webElementName = "";
            if (value instanceof CapturedMobileElement) {
                webElementName = ((CapturedMobileElement) value).getName();
            }
            if (value instanceof MobileElementMethodCallWrapper) {
                webElementName = ((MobileElementMethodCallWrapper) value).getMobileElement().getName();
            }
            super.updateContents(webElementName);
        }

        private CapturedMobileElement getSelectedCapturedMobileElement() {
            if (selectedNode == null) {
                return null;
            }
            if (selectedNode instanceof AstBuiltInKeywordTreeTableNode) {
                AstBuiltInKeywordTreeTableNode kwNode = (AstBuiltInKeywordTreeTableNode) selectedNode;
                Object testObjectMethodCall = kwNode.getTestObject();
                if (testObjectMethodCall instanceof MobileElementMethodCallWrapper) {
                    MobileElementMethodCallWrapper methodCallExprs = (MobileElementMethodCallWrapper) testObjectMethodCall;
                    return methodCallExprs.getMobileElement();
                }
            }
            return null;
        }
    }
}
