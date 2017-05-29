package com.kms.katalon.composer.testcase.ast.treetable;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;


public class AstStatementTreeTableNode extends AstAbstractTreeTableNode {
    protected StatementWrapper statement;
    private Image icon;
    private String itemText = StatementWrapper.TEXT;

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
    
    public boolean canHaveDescription() {
        return statement.canHaveDescription();
    }
    
    public boolean hasDecription() {
        return statement.hasDescription();
    }
    
    public String getDescription() {
        final String description = statement.getDescription();
        if (description == null) {
            return description;
        }
        return StringEscapeUtils.unescapeJava(description);
    }

    public boolean setDescription(String description) {
        return statement.setDescription(description);
    }
    
    public boolean canBeDisabled() {
        return statement.canHaveLabel();
    }
    
    public boolean disable() {
        return statement.disable();
    }
    
    public boolean enable() {
        return statement.enable();
    }
    
    public void toogleEnable() {
        statement.toogleEnable();
    }

    public boolean isDisabled() {
        return statement.isDisabled();
    }
}
