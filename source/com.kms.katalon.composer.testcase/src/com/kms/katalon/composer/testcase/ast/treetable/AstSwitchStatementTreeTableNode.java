package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;

public class AstSwitchStatementTreeTableNode extends AstStatementTreeTableNode {
	private SwitchStatement switchStatement;

	public AstSwitchStatementTreeTableNode(SwitchStatement statement, AstTreeTableNode parentNode,
			ASTNode parentObject, ClassNode scriptClass) {
		super(statement, parentNode, parentObject, scriptClass);
		switchStatement = statement;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_SWITCH_STATEMENT;
	}

	@Override
	public boolean hasChildren() {
		return ((switchStatement.getCaseStatements() != null && switchStatement.getCaseStatements().size() > 0) || switchStatement
				.getDefaultStatement() != null);
	}

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
		for (CaseStatement caseStatement : switchStatement.getCaseStatements()) {
			astTreeTableNodes.addAll(AstTreeTableUtil.parseAstObjectIntoTreeTableNode(caseStatement, this,
					switchStatement, scriptClass));
		}
		if (switchStatement.getDefaultStatement() != null
				&& !(switchStatement.getDefaultStatement() instanceof EmptyStatement)) {
			astTreeTableNodes.add(new AstSwitchDefaultStatementTreeTableNode(switchStatement.getDefaultStatement(),
					this, switchStatement, scriptClass));
		}
		return astTreeTableNodes;
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(switchStatement, astObject);
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return -1;
	}
	
}
