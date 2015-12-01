package com.kms.katalon.composer.testcase.providers;

import groovy.json.StringEscapeUtils;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

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
                return treeTableNode.getNodeIcon();
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
                return ((treeTableNode instanceof AstStatementTreeTableNode) ? (treeTableNode.getIndex() + " - ") : "")
                        + treeTableNode.getItemText();
            case CLMN_OBJECT_IDX:
                return treeTableNode.getTestObjectText();
            case CLMN_INPUT_IDX:
                return treeTableNode.getInputText();
            case CLMN_OUTPUT_IDX:
                return treeTableNode.getOutputText();
            case CLMN_DESCRIPTION_IDX:
                if (element instanceof AstStatementTreeTableNode
                        && ((AstStatementTreeTableNode) element).hasDescription()) {
                    AstStatementTreeTableNode statementNode = ((AstStatementTreeTableNode) element);
                    Object descriptionValue = AstTreeTableValueUtil.getValue(statementNode.getDescription(),
                            statementNode.getScriptClass());
                    if (descriptionValue instanceof String) {
                        String description = (String) descriptionValue;
                        return StringEscapeUtils.escapeJava(description);
                    }
                }

            default:
                return "";
        }
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
        return getText(element);
    }

}
