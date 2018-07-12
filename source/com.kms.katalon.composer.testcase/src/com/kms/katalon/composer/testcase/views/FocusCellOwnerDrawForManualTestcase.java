package com.kms.katalon.composer.testcase.views;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;

import com.kms.katalon.composer.components.viewer.FocusCellOwnerDrawHighlighterForMultiSelection;

public class FocusCellOwnerDrawForManualTestcase extends FocusCellOwnerDrawHighlighterForMultiSelection {

    public FocusCellOwnerDrawForManualTestcase(ColumnViewer viewer) {
        super(viewer);
    }
    
    @Override
    protected Color getBackgroundLostFocusCell(ViewerCell cell) {
//        Object element = cell.getElement();
//        
//        if (!(element instanceof AstStatementTreeTableNode)) {
//            return null;
//        }
//        AstStatementTreeTableNode statementNode = (AstStatementTreeTableNode)element;
//        return statementNode.isDisabled() ? ColorUtil.getDisabledItemBackgroundColor() : null;
        // Disabled in KAT-3316 because of changing disabled table item approach
        return null;
    }
}
