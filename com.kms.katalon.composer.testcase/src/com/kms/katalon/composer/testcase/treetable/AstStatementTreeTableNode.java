package com.kms.katalon.composer.testcase.treetable;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.core.groovy.GroovyParser;

public class AstStatementTreeTableNode extends AstAbstractTreeTableNode {
	protected Statement statement;
	protected AstTreeTableNode parentNode;
	protected ASTNode parentAstObject;
	protected ExpressionStatement descriptionStatement;
	protected ClassNode scriptClass;

	public AstStatementTreeTableNode(Statement statement, AstTreeTableNode parentNode, ASTNode parentAstObject,
			ClassNode scriptClass) {
		this.statement = statement;
		this.parentNode = parentNode;
		this.parentAstObject = parentAstObject;
		this.scriptClass = scriptClass;
	}

	@Override
	public ASTNode getASTObject() {
		return statement;
	}

	@Override
	public String getItemText() {
		return StringConstants.TREE_STATEMENT;
	}

	@Override
	public AstTreeTableNode getParent() {
		return parentNode;
	}

	@Override
	public ASTNode getParentASTObject() {
		return parentAstObject;
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_FAILED_CONTINUE;
	}

	@Override
	public AstTreeTableNode clone() {
		return new AstStatementTreeTableNode(GroovyParser.cloneStatement(statement), parentNode, parentAstObject,
				scriptClass);
	}

	@Override
	public boolean isInputEditable() {
		if (statement != null) {
			return true;
		}
		return false;
	}

	@Override
	public Object getInput() {
		return AstTreeTableValueUtil.getValue(statement, scriptClass);
	}

	@Override
	public String getInputText() {
		Object input = getInput();
		if (input instanceof String) {
			return String.valueOf(input);
		} else if (input instanceof ASTNode) {
			return AstTreeTableTextValueUtil.getTextValue(input);
		}
		return "";
	}

	@Override
	public CellEditor getCellEditorForInput(Composite parent) {
		return AstTreeTableInputUtil.getCellEditorForStatement(parent, statement, scriptClass);
	}

	@Override
	public boolean setInput(Object input) {
		return AstTreeTableValueUtil.setValue(statement, input, scriptClass);
	}

	public boolean hasDescription() {
		return descriptionStatement != null;
	}

	public ExpressionStatement getDescription() {
		return descriptionStatement;
	}

	public void setDescription(ExpressionStatement descriptionStatement) {
		this.descriptionStatement = descriptionStatement;
	}

	@Override
	public boolean hasChildren() {
		if (statement instanceof BlockStatement || statement instanceof IfStatement
				|| statement instanceof TryCatchStatement || statement instanceof SwitchStatement) {
			return true;
		}
		return false;
	}

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		try {
			return AstTreeTableUtil.getChildren(statement, this, statement, scriptClass);
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return Collections.emptyList();
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return AstTreeTableUtil.getIndex(statement, astObject);
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		AstTreeTableUtil.addChild(statement, astObject, index);
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(statement, astObject);
	}

	public ClassNode getScriptClass() {
		return scriptClass;
	}
}
