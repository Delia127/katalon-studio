package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.util.StatementInputCellEditorProvider;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * Created by taittle on 3/24/16.
 */
public class AstInputEditableStatementTreeTableNode extends AstStatementTreeTableNode implements IAstInputEditableNode {
    public AstInputEditableStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode) {
        super(statement, parentNode);
    }

    protected AstInputEditableStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode, Image icon) {
        super(statement, parentNode, icon);
    }

    protected AstInputEditableStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode, String itemText) {
        super(statement, parentNode, itemText);
    }

    public AstInputEditableStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode, Image icon, String itemText) {
        super(statement, parentNode, icon, itemText);
    }

    @Override
    public boolean canEditInput() {
        return true;
    }

    @Override
    public boolean setInput(Object input) {
        return input instanceof ASTNodeWrapper && statement.updateInputFrom((ASTNodeWrapper) input);
    }

    @Override
    public CellEditor getCellEditorForInput(Composite parent) {
        return StatementInputCellEditorProvider.getEditorForInput(parent, statement);
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
}
