package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;

public class AstCaseStatementTreeTableNode extends AstStatementTreeTableNode {
	private CaseStatement caseStatement;

	public AstCaseStatementTreeTableNode(CaseStatement statement, AstTreeTableNode parentNode,
			SwitchStatement switchStatement, ClassNode scriptClass) {
		super(statement, parentNode, switchStatement, scriptClass);
		this.caseStatement = statement;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_CASE_STATEMENT;
	}

	@Override
	public boolean hasChildren() {
		return caseStatement.getCode() != null;
	}

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		return AstTreeTableUtil.getChildren(caseStatement.getCode(), this, caseStatement.getCode(), scriptClass);
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		AstTreeTableUtil.addChild(caseStatement.getCode(), astObject, index);
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(caseStatement.getCode(), astObject);
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return AstTreeTableUtil.getIndex(caseStatement.getCode(), astObject);
	}

}
