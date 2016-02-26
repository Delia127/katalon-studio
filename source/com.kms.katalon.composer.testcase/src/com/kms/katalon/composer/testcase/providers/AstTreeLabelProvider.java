package com.kms.katalon.composer.testcase.providers;

import groovy.json.StringEscapeUtils;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.ast.treetable.AstInputEditableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstObjectEditableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstOutputEditableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;

public class AstTreeLabelProvider extends StyledCellLabelProvider {

    private static final int CLMN_ITEM_IDX = 0;

    private static final int CLMN_OBJECT_IDX = 1;

    private static final int CLMN_INPUT_IDX = 2;

    private static final int CLMN_OUTPUT_IDX = 3;

    private static final int CLMN_DESCRIPTION_IDX = 4;

    private int columnIndex;

    public Image getImage(Object element) {
        if (element == null || !(element instanceof AstTreeTableNode)) {
            return null;
        }
        AstTreeTableNode treeTableNode = (AstTreeTableNode) element;

        switch (columnIndex) {
        case CLMN_ITEM_IDX:
            return treeTableNode.getIcon();
        default:
            return null;
        }
    }

    public String getText(Object element) {
        if (element == null || !(element instanceof AstTreeTableNode)) {
            return "";
        }
        AstTreeTableNode treeTableNode = (AstTreeTableNode) element;
        switch (columnIndex) {
        case CLMN_ITEM_IDX:
            return ((treeTableNode instanceof AstStatementTreeTableNode) ? (getStringIndex(treeTableNode) + " - ") : "")
                    + treeTableNode.getItemText();
        case CLMN_OBJECT_IDX:
            if (treeTableNode instanceof AstObjectEditableNode) {
                return ((AstObjectEditableNode) treeTableNode).getTestObjectText();
            }
            return "";
        case CLMN_INPUT_IDX:
            if (treeTableNode instanceof AstInputEditableNode) {
                return ((AstInputEditableNode) treeTableNode).getInputText();
            }
            return "";
        case CLMN_OUTPUT_IDX:
            if (treeTableNode instanceof AstOutputEditableNode) {
                return ((AstOutputEditableNode) treeTableNode).getOutputText();
            }
            return "";
        case CLMN_DESCRIPTION_IDX:
            if (!(treeTableNode instanceof AstStatementTreeTableNode)) {
                return "";
            }
            return StringEscapeUtils.escapeJava(((AstStatementTreeTableNode) treeTableNode).getDescription());
        default:
            return "";
        }
    }

    private String getStringIndex(AstTreeTableNode node) {
        if (node == null || node.getParent() == null || !(node.getParent().canHaveChildren())
                || node instanceof AstMethodTreeTableNode) {
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
    public void update(ViewerCell cell) {
        columnIndex = cell.getColumnIndex();
        cell.setText(getText(cell.getElement()));
        cell.setImage(getImage(cell.getElement()));

        super.update(cell);
    }

    @Override
    public String getToolTipText(Object element) {
        if (element == null || !(element instanceof AstTreeTableNode)) {
            return "";
        }
        AstTreeTableNode treeTableNode = (AstTreeTableNode) element;
        switch (columnIndex) {
        case CLMN_ITEM_IDX:
            return treeTableNode.getItemTooltipText();
        case CLMN_OBJECT_IDX:
            if (treeTableNode instanceof AstObjectEditableNode) {
                return ((AstObjectEditableNode) treeTableNode).getTestObjectTooltipText();
            }
            return "";
        case CLMN_INPUT_IDX:
            if (treeTableNode instanceof AstInputEditableNode) {
                return ((AstInputEditableNode) treeTableNode).getInputTooltipText();
            }
            return "";
        case CLMN_OUTPUT_IDX:
            if (treeTableNode instanceof AstOutputEditableNode) {
                return ((AstOutputEditableNode) treeTableNode).getOutputTooltipText();
            }
            return "";
        case CLMN_DESCRIPTION_IDX:
            return StringEscapeUtils.escapeJava(((AstStatementTreeTableNode) treeTableNode).getDescription());
        default:
            return "";
        }
    }

}
