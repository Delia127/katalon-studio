package com.kms.katalon.composer.testcase.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.exceptions.GroovyParsingException;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.keywords.IKeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserControlTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransferData;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestStepTableDropListener extends TreeDropTargetEffect {

    private TreeViewer treeViewer;

    private ITestCasePart testCasePart;

    public TestStepTableDropListener(TreeViewer viewer, ITestCasePart parentPart) {
        super(viewer.getTree());
        this.treeViewer = viewer;
        this.testCasePart = parentPart;
    }

    @Override
    public void dragOver(DropTargetEvent event) {
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
        if (event.item == null || !(event.item instanceof TreeItem)) {
            return;
        }
        event.feedback |= getFeedBackByLocation(event.display.map(null, treeViewer.getTree(), event.x, event.y),
                (TreeItem) event.item);
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
                handleDropForTreeEntity(event, (ITreeEntity[]) event.data);
            } else if (event.data instanceof IKeywordBrowserTreeEntity[]) {
                handleDropForKeywordBrowserTreeEntities(event, (IKeywordBrowserTreeEntity[]) event.data);
            } else if (event.data instanceof String) {
                handleDropForScriptSnippet(event, (String) event.data);
            } else if (event.data instanceof ScriptTransferData[]) {
                handleDropForScriptTransferData(event, (ScriptTransferData[]) event.data);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
                    StringConstants.ERR_CANNOT_DROP_ON_TEST_STEP_TABLE);
        }
    }

    private void handleDropForScriptTransferData(DropTargetEvent event, ScriptTransferData[] scriptTransferDatas)
            throws GroovyParsingException {
        if (scriptTransferDatas.length <= 0) {
            return;
        }

        List<AstTreeTableNode> dragNodes = testCasePart.getDragNodes();
        AstTreeTableNode hoveredTreeTableNode = getHoveredTreeTableNode(event);
        if (dragNodes != null && dragNodes.contains(hoveredTreeTableNode)) {
            event.detail = DND.DROP_NONE;
            return;
        }

        ScriptTransferData firstScriptTransferData = scriptTransferDatas[0];
        String testCaseId = firstScriptTransferData.getTestCaseId();
        NodeAddType addType = getNodeAddType(event);
        List<StatementWrapper> dropStatements = getStatementsFromScriptSnippet(
                firstScriptTransferData.getScriptSnippet());
        if (StringUtils.equals(testCaseId, testCasePart.getTestCase().getId())) {
            getTestCaseTreeTableInput().dragAndDropAstObjects(new ArrayList<>(dragNodes), dropStatements, hoveredTreeTableNode, addType);
            return;
        }
        handleDropForScriptSnippet(dropStatements, hoveredTreeTableNode, addType);
    }

    private boolean handleDropForScriptSnippet(DropTargetEvent event, String snippet) throws GroovyParsingException {
        List<StatementWrapper> dropStatements = getStatementsFromScriptSnippet(snippet);
        AstTreeTableNode hoveredTreeTableNode = getHoveredTreeTableNode(event);
        NodeAddType nodeAddType = getNodeAddType(event);
        return handleDropForScriptSnippet(dropStatements, hoveredTreeTableNode, nodeAddType);
    }

    private boolean handleDropForScriptSnippet(List<StatementWrapper> dropStatements,
            AstTreeTableNode hoveredTreeTableNode, NodeAddType nodeAddType) {
        return getTestCaseTreeTableInput().addNewAstObjects(dropStatements, hoveredTreeTableNode, nodeAddType);
    }

    private List<StatementWrapper> getStatementsFromScriptSnippet(String snippet) throws GroovyParsingException {
        ScriptNodeWrapper scriptNode = GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(snippet);
        if (scriptNode == null) {
            return Collections.emptyList();
        }
        ArrayList<StatementWrapper> dropStatements = new ArrayList<StatementWrapper>(
                scriptNode.getBlock().getStatements());
        return dropStatements;
    }

    private void handleDropForKeywordBrowserTreeEntities(DropTargetEvent event,
            IKeywordBrowserTreeEntity[] treeEntities) throws Exception {
        if (treeEntities == null || treeEntities.length <= 0) {
            return;
        }
        for (int i = 0; i < treeEntities.length; i++) {
            if (treeEntities[i] instanceof KeywordBrowserTreeEntity) {
                handleDropForKeywordBrowserTreeEntity(event, (KeywordBrowserTreeEntity) treeEntities[i]);
            } else if (treeEntities[i] instanceof KeywordBrowserControlTreeEntity) {
                handleDropForKeywordBrowserControlEntity(event, (KeywordBrowserControlTreeEntity) treeEntities[i]);
            }
        }
    }

    private void handleDropForKeywordBrowserControlEntity(DropTargetEvent event,
            KeywordBrowserControlTreeEntity keywordBrowserControlTreeEntity) {
        getTestCaseTreeTableInput().addNewAstObject(keywordBrowserControlTreeEntity.getControlStatementId(),
                getHoveredTreeTableNode(event), getNodeAddType(event));
    }

    private void handleDropForKeywordBrowserTreeEntity(DropTargetEvent event,
            KeywordBrowserTreeEntity keywordBrowserTreeEntity) {
        TestCaseTreeTableInput testCaseTableInput = getTestCaseTreeTableInput();
        testCaseTableInput.addDefaultImports();
        AstTreeTableNode hoveredTreeTableNode = getHoveredTreeTableNode(event);
        ASTNodeWrapper parentWrapper = hoveredTreeTableNode != null ? hoveredTreeTableNode.getASTObject()
                : testCaseTableInput.getMainClassNode();
        addNewStatementWrapperToTreeTable(event,
                createNewKeywordStatementFromKeywordBrowserEntity(keywordBrowserTreeEntity, parentWrapper));
    }

    private ExpressionStatementWrapper createNewKeywordStatementFromKeywordBrowserEntity(
            KeywordBrowserTreeEntity keywordBrowserTreeEntity, ASTNodeWrapper parentWrapper) {
        if (keywordBrowserTreeEntity.isCustom()) {
            return AstKeywordsInputUtil.createNewCustomKeywordStatement(keywordBrowserTreeEntity.getClassName(),
                    keywordBrowserTreeEntity.getName(), parentWrapper);
        }
        return AstKeywordsInputUtil.createBuiltInKeywordStatement(
                KeywordController.getInstance()
                        .getBuiltInKeywordClassByName(keywordBrowserTreeEntity.getClassName())
                        .getAliasName(),
                keywordBrowserTreeEntity.getName(), parentWrapper);
    }

    private void handleDropForTreeEntity(DropTargetEvent event, ITreeEntity[] treeEntities) throws Exception {
        Widget item = event.item;
        if (item != null && (item.getData() instanceof AstBuiltInKeywordTreeTableNode)) {
            handleDropTestObject(getTestObjectFromSelectionIfAvailable(treeEntities),
                    (AstBuiltInKeywordTreeTableNode) item.getData());
        }
        Set<TestCaseEntity> calledTestCases = collectCallTestCasesFromTreeEntities(treeEntities);
        if (calledTestCases.isEmpty()) {
            return;
        }
        TestCaseTreeTableInput testCaseTableInput = getTestCaseTreeTableInput();
        testCaseTableInput.addDefaultImports();
        testCaseTableInput.addCallTestCases(getHoveredTreeTableNode(event), getNodeAddType(event),
                calledTestCases.toArray(new TestCaseEntity[calledTestCases.size()]));
    }

    private WebElementTreeEntity getTestObjectFromSelectionIfAvailable(ITreeEntity[] treeEntities) {
        for (ITreeEntity treeEntity : treeEntities) {
            if (treeEntity instanceof WebElementTreeEntity) {
                return (WebElementTreeEntity) treeEntity;
            }
        }
        return null;
    }

    private void handleDropTestObject(WebElementTreeEntity webElementTreeEntity,
            AstBuiltInKeywordTreeTableNode tableNode) {
        if (webElementTreeEntity == null) {
            return;
        }
        ExpressionWrapper findTestObject = convertWebElementToTestObject(webElementTreeEntity, tableNode);
        if (findTestObject == null) {
            return;
        }
        if (!tableNode.setTestObject(findTestObject)) {
            return;
        }
        testCasePart.getTreeTableInput().setDirty(true);
        treeViewer.refresh(tableNode);
    }

    private ExpressionWrapper convertWebElementToTestObject(WebElementTreeEntity webElementTreeEntity,
            AstBuiltInKeywordTreeTableNode node) {
        String objectPk = null;
        try {
            if (!(webElementTreeEntity.getObject() instanceof WebElementEntity)) {
                return null;
            }
            objectPk = ((WebElementEntity) webElementTreeEntity.getObject()).getIdForDisplay();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (objectPk == null) {
            return null;
        }
        return AstEntityInputUtil.createNewFindTestObjectMethodCall(objectPk, node.getASTObject());

    }

    private Set<TestCaseEntity> collectCallTestCasesFromTreeEntities(ITreeEntity[] treeEntities) throws Exception {
        if (treeEntities == null || treeEntities.length <= 0) {
            return Collections.emptySet();
        }
        TestCaseTreeTableInput treeTableInput = getTestCaseTreeTableInput();
        Set<TestCaseEntity> callTestCases = new LinkedHashSet<TestCaseEntity>();
        for (int i = 0; i < treeEntities.length; i++) {
            if (treeEntities[i] instanceof TestCaseTreeEntity) {
                TestCaseEntity testCase = ((TestCaseTreeEntity) treeEntities[i]).getObject();
                if (treeTableInput.validateTestCase(testCase)) {
                    callTestCases.add(testCase);
                }

            } else if (treeEntities[i] instanceof FolderTreeEntity) {
                List<TestCaseEntity> testCases = TestCaseEntityUtil
                        .getTestCasesFromFolderTree((FolderTreeEntity) treeEntities[i]);
                for (TestCaseEntity testCase : testCases) {
                    if (treeTableInput.validateTestCase(testCase)) {
                        callTestCases.add(testCase);
                    }
                }
            }
        }
        return callTestCases;
    }

    protected boolean addNewStatementWrapperToTreeTable(DropTargetEvent event, StatementWrapper statement) {
        if (statement == null) {
            return false;
        }
        TestCaseTreeTableInput testCaseTreeTableInput = getTestCaseTreeTableInput();
        testCaseTreeTableInput.addDefaultImports();
        return testCaseTreeTableInput.addNewAstObject(statement, getHoveredTreeTableNode(event), getNodeAddType(event));
    }

    private NodeAddType getNodeAddType(DropTargetEvent event) {
        switch (getFeedBackByLocation(event.display.map(null, treeViewer.getTree(), event.x, event.y),
                (TreeItem) event.item)) {
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
}
