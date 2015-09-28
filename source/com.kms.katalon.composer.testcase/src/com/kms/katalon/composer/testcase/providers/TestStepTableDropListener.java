package com.kms.katalon.composer.testcase.providers;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransferData;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.core.groovy.GroovyParser;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestStepTableDropListener extends TreeDropTargetEffect {

	private TreeViewer treeViewer;
	private TestCasePart testCasePart;

	public TestStepTableDropListener(TreeViewer viewer, TestCasePart testCasePart) {
		super(viewer.getTree());
		treeViewer = (TreeViewer) viewer;
		this.testCasePart = testCasePart;
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		super.dragOver(event);
	}

	@Override
	public void drop(DropTargetEvent event) {
		try {
			event.detail = DND.DROP_COPY;
			if (event.data instanceof ITreeEntity[]) {
				handleDropForTreeEntity(event);
			} else if (event.data instanceof KeywordBrowserTreeEntity[]) {
				handleDropForKeywordBrowserTreeEntity(event);
			} else if (event.data instanceof String) {
				handleDropForScriptSnippet(event);
			} else if (event.data instanceof ScriptTransferData[]) {
				handleDropForScriptSnippet(event);
				String testCaseId = ((ScriptTransferData[]) event.data)[0].getTestCaseId();
				if (testCaseId.equals(testCasePart.getTestCase().getId())) {
					event.detail = DND.DROP_MOVE;
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
					"Error dropping on test step table: " + e.getMessage());
		}
	}

	private void handleDropForScriptSnippet(DropTargetEvent event) throws Exception {
		String snippet = null;
		if (event.data instanceof String) {
			snippet = (String) event.data;
		} else if (event.data instanceof ScriptTransferData[]) {
			snippet = ((ScriptTransferData[]) event.data)[0].getScriptSnippet();
		}
		List<ASTNode> astNodes = GroovyParser.parseGroovyScriptIntoAstNodes(snippet);
		for (ASTNode astNode : astNodes) {
			if (astNode instanceof BlockStatement) {
				List<Statement> childStatements = ((BlockStatement) astNode).getStatements();
				addNewStatementsToTreeTable(event, childStatements);
			}
		}

	}

	private void handleDropForKeywordBrowserTreeEntity(DropTargetEvent event) throws Exception {
		KeywordBrowserTreeEntity[] treeEntities = (KeywordBrowserTreeEntity[]) event.data;
		for (int i = 0; i < treeEntities.length; i++) {
			if (treeEntities[i] instanceof KeywordBrowserTreeEntity) {
				KeywordBrowserTreeEntity keywordBrowserTreeEntity = treeEntities[i];
				ExpressionStatement newStatement = null;
				if (keywordBrowserTreeEntity.isCustom()) {
					newStatement = AstTreeTableInputUtil.createCustomKeywordMethodCall(
							keywordBrowserTreeEntity.getClassName(), keywordBrowserTreeEntity.getName());
				} else {
					newStatement = AstTreeTableInputUtil.createBuiltInKeywordMethodCall(
							keywordBrowserTreeEntity.getClassName(), keywordBrowserTreeEntity.getName());
				}
				if (newStatement != null) {
					addNewStatementToTreeTable(event, newStatement);
				}

			}
		}
	}

	private void handleDropForTreeEntity(DropTargetEvent event) throws Exception {
		ITreeEntity[] treeEntities = (ITreeEntity[]) event.data;
		for (int i = 0; i < treeEntities.length; i++) {
			if (treeEntities[i] instanceof TestCaseTreeEntity) {
				TestCaseEntity droppedTestCase = (TestCaseEntity) treeEntities[i].getObject();
				dropTestCaseIntoTree(event, droppedTestCase);

			} else if (treeEntities[i] instanceof FolderTreeEntity) {
				for (TestCaseEntity droppedTestCase : TestCaseEntityUtil
						.getTestCasesFromFolderTree((FolderTreeEntity) treeEntities[i])) {
					dropTestCaseIntoTree(event, droppedTestCase);
				}
			}
		}
	}

	private void dropTestCaseIntoTree(DropTargetEvent event, TestCaseEntity dropedTestCase) throws Exception {
		if (!testCasePart.qualify(dropedTestCase)) return;

		ExpressionStatement statement = AstTreeTableInputUtil.generateCallTestCaseExpresionStatement(dropedTestCase);

		MethodCallExpression methodCallExpression = (MethodCallExpression) statement.getExpression();
		ArgumentListExpression argumentList = (ArgumentListExpression) methodCallExpression.getArguments();

		List<VariableEntity> variableEntities = TestCaseEntityUtil.getCallTestCaseVariables(argumentList);
		testCasePart.addVariables(variableEntities.toArray(new VariableEntity[0]));

		addNewStatementToTreeTable(event, statement);
	}

	protected void addNewStatementToTreeTable(DropTargetEvent event, Statement statement) throws Exception {
		Point pt = Display.getCurrent().map(null, treeViewer.getTree(), event.x, event.y);
		TreeItem treeItem = treeViewer.getTree().getItem(pt);
		AstTreeTableNode node = (treeItem != null) ? (AstTreeTableNode) treeItem.getData() : null;
		switch (event.feedback) {
			case DND.FEEDBACK_INSERT_BEFORE:
				getTestCaseTreeTableInput().addASTObjectBefore(statement, node);
				break;
			default:
				getTestCaseTreeTableInput().addNewAstObject(statement, node, NodeAddType.Add);
				break;
		}
	}

	protected TestCaseTreeTableInput getTestCaseTreeTableInput() {
		return testCasePart.getTreeTableInput();
	}

	protected void addNewStatementsToTreeTable(DropTargetEvent event, List<Statement> statements) throws Exception {
		Point pt = Display.getCurrent().map(null, treeViewer.getTree(), event.x, event.y);
		TreeItem treeItem = treeViewer.getTree().getItem(pt);
		AstTreeTableNode node = (treeItem != null) ? (AstTreeTableNode) treeItem.getData() : null;
		switch (event.feedback) {
			case DND.FEEDBACK_INSERT_BEFORE:
				getTestCaseTreeTableInput().addASTObjectsBefore(statements, node);
				break;
			default:
				getTestCaseTreeTableInput().addNewAstObjects(statements, node, NodeAddType.Add);
				break;
		}
	}
}
