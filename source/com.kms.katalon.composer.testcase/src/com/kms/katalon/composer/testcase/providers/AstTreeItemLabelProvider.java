package com.kms.katalon.composer.testcase.providers;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.DefaultCellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleTreeCellLabelProvider;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;

public class AstTreeItemLabelProvider extends TypeCheckedStyleTreeCellLabelProvider<AstTreeTableNode> {

    private static final int CLMN_ITEM_IDX = 0;

    public AstTreeItemLabelProvider() {
        super(CLMN_ITEM_IDX);
    }

    /**
     * Add 10 pixels for the space between image and text because of ELSE-IF label's is not 16x16.
     * 
     * @see <a href="https://incubation.kms-technology.com/browse/KAT-969">KAT-969</a>
     */
    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return new DefaultCellLayoutInfo() {
//            @Override
//            public int getSpace() {
//                return 3;
//            }
        };
    }

    @Override
    protected Class<AstTreeTableNode> getElementType() {
        return AstTreeTableNode.class;
    }

    @Override
    protected Image getImage(AstTreeTableNode element) {
        return element.getIcon();
    }

    @Override
    protected String getText(AstTreeTableNode element) {
        return ((element instanceof AstStatementTreeTableNode) ? (getStringIndex(element) + " - ") : "")
                + element.getItemText();
    }

    private String getStringIndex(AstTreeTableNode node) {
        if (node.getParent() == null || !(node.getParent().canHaveChildren()) || node instanceof AstMethodTreeTableNode) {
            return "";
        }
        String parentIndex = getStringIndex(node.getParent());
        String thisIndex = String.valueOf(node.getParent().getChildren().indexOf(node) + 1);
        if (!parentIndex.isEmpty()) {
            return parentIndex + "." + thisIndex;
        }
        return thisIndex;
    }

    @Override
    public String getElementToolTipText(AstTreeTableNode element) {
        return element.getItemTooltipText();
    }
    
    @Override
    protected StyleRange[] getStyleRanges(ViewerCell cell, AstTreeTableNode element) {
        String text = cell.getText();
        if (isDisabledStatement(element)) {
            StyleRange disableText = new StyleRange();
            disableText.start = 0;
            disableText.length = text.length();
            disableText.foreground = ColorUtil.getDisabledTextColor();
            return new StyleRange[] {  disableText };
        } else {
            return new StyleRange[0];
        }
    }

    private boolean isDisabledStatement(AstTreeTableNode treeNode) {
        return treeNode instanceof AstStatementTreeTableNode && ((AstStatementTreeTableNode) treeNode).isDisabled();
    }
}
