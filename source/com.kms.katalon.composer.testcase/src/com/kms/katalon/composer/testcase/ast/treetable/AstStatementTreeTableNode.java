package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import org.eclipse.swt.widgets.Composite;


public abstract class AstStatementTreeTableNode extends AstAbstractTreeTableNode implements AstInputEditableNode {
    protected StatementWrapper statement;
    private Image icon;
    private String itemText;

    public AstStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode) {
        this(statement, parentNode, ImageConstants.IMG_16_FAILED_CONTINUE);
    }

    protected AstStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode, Image icon) {
        this(statement, parentNode, icon, StringConstants.TREE_STATEMENT);
    }

    public AstStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode, String itemText) {
        this(statement, parentNode, ImageConstants.IMG_16_FAILED_CONTINUE, itemText);
    }

    public AstStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode, Image icon, String itemText) {
        super(parentNode);
        this.statement = statement;
        this.icon = icon;
        this.itemText = itemText;
    }

    @Override
    public StatementWrapper getASTObject() {
        return statement;
    }

    @Override
    public String getItemText() {
        return itemText;
    }

    @Override
    public Image getIcon() {
        return icon;
    }
    
    public String getDescription() {
        return statement.getDescription();
    }

    public void setDescription(String description) {
        this.statement.setDescription(description);
    }

    @Override
    public boolean canEditInput() {
        return false;
    }

    @Override
    public String getInputText() {
        return statement.getInputText();
    }

    @Override
    public String getInputTooltipText() {
        return getInputText();
    }

    @Override
    public Object getInput() {
        return statement.getInput();
    }

    @Override
    public boolean setInput(Object input) {
        return false;
    }

    @Override
    public CellEditor getCellEditorForInput(Composite parent) {
        return null;
    }
}
