package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstCatchStatementTreeTableNode extends AstStatementTreeTableNode {
	private CatchStatement catchStatement;
	private TryCatchStatement tryCatchStatement;

	public AstCatchStatementTreeTableNode(CatchStatement statement, AstTreeTableNode parentNode,
			TryCatchStatement parentTryCatchStatement, ClassNode scriptClass) {
		super(statement, parentNode, parentTryCatchStatement, scriptClass);
		catchStatement = statement;
		setTryCatchStatement(parentTryCatchStatement);
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_CATCH_STATEMENT;
	}

	@Override
	public boolean hasChildren() {
		return catchStatement.getCode() != null;
	}

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		return AstTreeTableUtil.getChildren(catchStatement.getCode(), this, catchStatement.getCode(), scriptClass);
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		AstTreeTableUtil.addChild(catchStatement.getCode(), astObject, index);
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(catchStatement.getCode(), astObject);
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return AstTreeTableUtil.getIndex(catchStatement.getCode(), astObject);
	}

	public Statement getTryCatchStatement() {
		return tryCatchStatement;
	}

	private void setTryCatchStatement(TryCatchStatement tryCatchStatement) {
		this.tryCatchStatement = tryCatchStatement;
	}
	
	@Override
    public boolean setInput(Object input) {
        if (input instanceof CatchStatement) {
            CatchStatement newCatchStatement = (CatchStatement) input;
            if (!AstTreeTableValueUtil.compareAstNode(catchStatement, newCatchStatement) && getParent() != null) {
                int index = tryCatchStatement.getCatchStatements().indexOf(catchStatement);
                tryCatchStatement.getCatchStatements().remove(catchStatement);
                tryCatchStatement.getCatchStatements().add(index, newCatchStatement);
                return true;
            }
        }
        return false;
    }
}
