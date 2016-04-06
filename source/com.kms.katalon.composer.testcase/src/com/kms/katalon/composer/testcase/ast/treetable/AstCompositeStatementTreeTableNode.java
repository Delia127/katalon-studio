package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.groovy.ast.ASTHasBlock;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.util.WrapperToAstTreeConverter;
import org.eclipse.swt.graphics.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by taittle on 3/17/16.
 */
public abstract class AstCompositeStatementTreeTableNode extends AstStatementTreeTableNode {
    protected List<AstTreeTableNode> childNodes = new ArrayList<>();

    public AstCompositeStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode) {
        super(statement, parentNode);
        reloadChildren();
    }

    protected AstCompositeStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode, Image icon) {
        super(statement, parentNode, icon);
        reloadChildren();
    }

    public AstCompositeStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode, String itemText) {
        super(statement, parentNode, itemText);
        reloadChildren();
    }

    public AstCompositeStatementTreeTableNode(StatementWrapper statement, AstTreeTableNode parentNode, Image icon, String itemText) {
        super(statement, parentNode, icon, itemText);
        reloadChildren();
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }

    @Override
    public List<AstTreeTableNode> getChildren() {
        return childNodes;
    }

    @Override
    public boolean hasChildren() {
        return !childNodes.isEmpty();
    }

    @Override
    public void reloadChildren() {
        childNodes.clear();
        childNodes.addAll(WrapperToAstTreeConverter.getInstance().convert(getStatements(), this));
    }

    protected List<StatementWrapper> getStatements() {
        if (getASTObject() instanceof ASTHasBlock) {
            return ((ASTHasBlock) getASTObject()).getBlock().getStatements();
        }

        return Collections.emptyList();
    }


}
