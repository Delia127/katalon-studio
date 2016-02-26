package com.kms.katalon.composer.testcase.providers;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.keywords.IKeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserControlTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransferData;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
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
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
        if (event.item != null) {
            event.feedback |= getFeedBackByLocation(event.display.map(null, treeViewer.getTree(), event.x, event.y),
                    (TreeItem) event.item);
        }
    }

    private static int getFeedBackByLocation(Point point, TreeItem treeItem) {
        if (treeItem == null || point == null) {
            return 0;
        }
        Rectangle bounds = treeItem.getBounds();
        if (point.y < bounds.y + bounds.height / 3) {
            return DND.FEEDBACK_INSERT_BEFORE;
        } else if (point.y > bounds.y + 2 * bounds.height / 3) {
            return DND.FEEDBACK_INSERT_AFTER;
        } else {
            return DND.FEEDBACK_SELECT;
        }
    }

    @Override
    public void drop(DropTargetEvent event) {
        try {
            event.detail = DND.DROP_COPY;
            if (event.data instanceof ITreeEntity[]) {
                handleDropForTreeEntity(event);
            } else if (event.data instanceof IKeywordBrowserTreeEntity[]) {
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
        ScriptNodeWrapper scriptNode = GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(snippet);
        if (scriptNode == null) {
            return;
        }
        addNewStatementWrappersToTreeTable(event, scriptNode.getBlock().getStatements());

    }

    private void handleDropForKeywordBrowserTreeEntity(DropTargetEvent event) throws Exception {
        IKeywordBrowserTreeEntity[] treeEntities = (IKeywordBrowserTreeEntity[]) event.data;
        for (int i = 0; i < treeEntities.length; i++) {
            if (treeEntities[i] instanceof KeywordBrowserTreeEntity) {
                KeywordBrowserTreeEntity keywordBrowserTreeEntity = (KeywordBrowserTreeEntity) treeEntities[i];
                ExpressionStatementWrapper newStatementWrapper = null;
                if (keywordBrowserTreeEntity.isCustom()) {
                    newStatementWrapper = AstTreeTableInputUtil.createCustomKeywordMethodCall(
                            keywordBrowserTreeEntity.getClassName(), keywordBrowserTreeEntity.getName(), null);
                } else {
                    newStatementWrapper = AstTreeTableInputUtil.createBuiltInKeywordMethodCall(
                            keywordBrowserTreeEntity.getClassName(), keywordBrowserTreeEntity.getName(), null);
                }
                if (newStatementWrapper != null) {
                    addNewStatementWrapperToTreeTable(event, newStatementWrapper);
                }

            } else if (treeEntities[i] instanceof KeywordBrowserControlTreeEntity) {
                KeywordBrowserControlTreeEntity keywordBrowserControlTreeEntity = (KeywordBrowserControlTreeEntity) treeEntities[i];
                AstTreeTableNode node = getHoveredTreeTableNode(event);
                NodeAddType addType = getNodeAddType(event);
                getTestCaseTreeTableInput().addNewAstObject(keywordBrowserControlTreeEntity.getControlStatementId(), node,
                        addType);
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
        if (!testCasePart.getTreeTableInput().qualify(dropedTestCase))
            return;

        ExpressionStatementWrapper statement = AstEntityInputUtil.generateCallTestCaseExpresionStatement(dropedTestCase, null);
        List<VariableEntity> variableEntities = TestCaseTreeTableInput
                .getCallTestCaseVariables((ArgumentListExpressionWrapper) ((MethodCallExpressionWrapper) statement
                        .getExpression()).getArguments());
        testCasePart.addVariables(variableEntities.toArray(new VariableEntity[0]));
        addNewStatementWrapperToTreeTable(event, statement);
    }

    protected void addNewStatementWrapperToTreeTable(DropTargetEvent event, StatementWrapper statement) throws Exception {
        AstTreeTableNode node = getHoveredTreeTableNode(event);
        NodeAddType addType = getNodeAddType(event);
        getTestCaseTreeTableInput().addNewAstObject(statement, node, addType);
    }

    private NodeAddType getNodeAddType(DropTargetEvent event) throws Exception {
        switch (getFeedBackByLocation(event.display.map(null, treeViewer.getTree(), event.x, event.y), (TreeItem) event.item)) {
        case DND.FEEDBACK_INSERT_BEFORE:
            return NodeAddType.InserBefore;
        case DND.FEEDBACK_INSERT_AFTER:
            return NodeAddType.InserAfter;
        default:
            return NodeAddType.Add;
        }
    }

    private AstTreeTableNode getHoveredTreeTableNode(DropTargetEvent event) {
        Point pt = Display.getCurrent().map(null, treeViewer.getTree(), event.x, event.y);
        TreeItem treeItem = treeViewer.getTree().getItem(pt);
        return (treeItem != null) ? (AstTreeTableNode) treeItem.getData() : null;
    }

    protected TestCaseTreeTableInput getTestCaseTreeTableInput() {
        return testCasePart.getTreeTableInput();
    }

    protected void addNewStatementWrappersToTreeTable(DropTargetEvent event, List<StatementWrapper> statements) throws Exception {
        AstTreeTableNode node = getHoveredTreeTableNode(event);
        NodeAddType addType = getNodeAddType(event);
        getTestCaseTreeTableInput().addNewAstObjects(statements, node, addType);
    }
}
