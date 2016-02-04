package com.kms.katalon.composer.testcase.ast.treetable;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;

public class AstElseStatementTreeTableNode extends AstStatementTreeTableNode {
	private IfStatement rootIfStatement;

	public AstElseStatementTreeTableNode(Statement statement, AstTreeTableNode parentNode,
			IfStatement parentIfStatement, IfStatement rootIfStatement, ClassNode scriptClass) {
		super(statement, parentNode, parentIfStatement, scriptClass);
		this.rootIfStatement = rootIfStatement;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_ELSE_STATEMENT;
	}
    
    @Override
    public void reloadChildren() {
        try {
            children = AstTreeTableUtil.getChildren(statement, this, statement, scriptClass);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		AstTreeTableUtil.addChild(statement, astObject, index);
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(statement, astObject);
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return AstTreeTableUtil.getIndex(statement, astObject);
	}

	public IfStatement getRootIfStatement() {
		return rootIfStatement;
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_ELSE;
	}
}
