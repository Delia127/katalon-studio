package com.kms.katalon.composer.testcase.providers;

import groovy.json.StringEscapeUtils;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.IAstInputEditableNode;
import com.kms.katalon.composer.testcase.ast.treetable.IAstObjectEditableNode;
import com.kms.katalon.composer.testcase.ast.treetable.IAstOutputEditableNode;

public class AstTreeLabelProvider extends StyledCellLabelProvider {

    private static final int CLMN_ITEM_IDX = 0;

    private static final int CLMN_OBJECT_IDX = 1;

    private static final int CLMN_INPUT_IDX = 2;

    private static final int CLMN_OUTPUT_IDX = 3;

    private static final int CLMN_DESCRIPTION_IDX = 4;

    private int columnIndex;

    private Image getImage(Object element) {
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

    private String getText(Object element) {
        if (element == null || !(element instanceof AstTreeTableNode)) {
            return "";
        }
        AstTreeTableNode treeTableNode = (AstTreeTableNode) element;
        switch (columnIndex) {
            case CLMN_ITEM_IDX:
                return ((treeTableNode instanceof AstStatementTreeTableNode) ? (getStringIndex(treeTableNode) + " - ")
                        : "") + treeTableNode.getItemText();
            case CLMN_OBJECT_IDX:
                if (treeTableNode instanceof IAstObjectEditableNode) {
                    return ((IAstObjectEditableNode) treeTableNode).getTestObjectText();
                }
                return "";
            case CLMN_INPUT_IDX:
                if (treeTableNode instanceof IAstInputEditableNode) {
                    return ((IAstInputEditableNode) treeTableNode).getInputText();
                }
                return "";
            case CLMN_OUTPUT_IDX:
                if (treeTableNode instanceof IAstOutputEditableNode) {
                    return ((IAstOutputEditableNode) treeTableNode).getOutputText();
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
        Object element = cell.getElement();
        if (element == null || !(element instanceof AstTreeTableNode)) {
            super.update(cell);
            return;
        }
        columnIndex = cell.getColumnIndex();
        cell.setText(getText(element));
        cell.setImage(getImage(element));
        if (isDisabledStatement(element)) {
            handleDisableStatement(cell);
        } else {
            handleDefaultStatement(cell);
        }
        super.update(cell);
    }

    private boolean isDisabledStatement(Object element) {
        return element instanceof AstStatementTreeTableNode && ((AstStatementTreeTableNode) element).isDisabled();
    }

    private void handleDisableStatement(ViewerCell cell) {
        cell.setBackground(ColorUtil.getDisabledItemBackgroundColor());
    }

    private void handleDefaultStatement(ViewerCell cell) {
        cell.setBackground(null);
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
                if (treeTableNode instanceof IAstObjectEditableNode) {
                    return ((IAstObjectEditableNode) treeTableNode).getTestObjectTooltipText();
                }
                return "";
            case CLMN_INPUT_IDX:
                if (treeTableNode instanceof IAstInputEditableNode) {
                    return ((IAstInputEditableNode) treeTableNode).getInputTooltipText();
                }
                return "";
            case CLMN_OUTPUT_IDX:
                if (treeTableNode instanceof IAstOutputEditableNode) {
                    return ((IAstOutputEditableNode) treeTableNode).getOutputTooltipText();
                }
                return "";
            case CLMN_DESCRIPTION_IDX:
                return StringEscapeUtils.escapeJava(((AstStatementTreeTableNode) treeTableNode).getDescription());
            default:
                return "";
        }
    }

}
