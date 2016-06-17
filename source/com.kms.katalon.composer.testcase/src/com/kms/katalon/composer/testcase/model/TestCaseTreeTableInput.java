package com.kms.katalon.composer.testcase.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testcase.ast.dialogs.MethodObjectBuilderDialog;
import com.kms.katalon.composer.testcase.ast.treetable.AstAbstractKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstScriptTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.exceptions.GroovyParsingException;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.FieldNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.AssertStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BreakStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ComplexChildStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ComplexLastStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ContinueStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.DefaultStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ElseIfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ElseStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.FinallyStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ForStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.IfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ReturnStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SwitchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ThrowStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.TryCatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.WhileStatementWrapper;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransfer;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransferData;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCaseTreeTableInput {
    private static final String GROOVY_NEW_LINE_CHARACTER = "\n";

    /**
     * Enum for adding items into tree table
     * <p>
     * <b>Add</b> - Add new object into the selected node's children list if the selected node has children; otherwise
     * insert new object after the selected node
     * <p>
     * <b>InserBefore</b> - Insert new object before the selected node
     * <p>
     * <b>InserAfter</b> - Insert new object before the selected node
     */
    public enum NodeAddType {
        Add, InserBefore, InserAfter
    }

    private class AstNodeAddedEdit {
        private AstTreeTableNode parentNode;

        private ASTNodeWrapper newAstObject;

        public AstNodeAddedEdit(AstTreeTableNode parentNode, ASTNodeWrapper newAstObject) {
            this.parentNode = parentNode;
            this.newAstObject = newAstObject;
        }

        public AstTreeTableNode getParentNode() {
            return parentNode;
        }

        public ASTNodeWrapper getNewAstObject() {
            return newAstObject;
        }
    }

    private TreeViewer treeTableViewer;

    private ScriptNodeWrapper mainClassNodeWrapper;

    private AstScriptTreeTableNode mainClassTreeNode;

    private TestCasePart parentPart;

    private boolean isChanged;

    public ScriptNodeWrapper getMainClassNode() {
        return mainClassNodeWrapper;
    }

    public TestCaseTreeTableInput(ScriptNodeWrapper scriptNode, TreeViewer treeTableViewer, TestCasePart parentPart) {
        this.treeTableViewer = treeTableViewer;
        this.mainClassNodeWrapper = scriptNode;
        this.parentPart = parentPart;
        setChanged(false);
    }

    /**
     * Add import to script
     * 
     * @param importClass
     * class to add import
     */
    public void addImport(Class<?> importClass) {
        mainClassNodeWrapper.addImport(importClass);
    }

    /**
     * Get the first currently selected tree table node
     * 
     * @return the first currently selected tree table nodes
     */
    public AstTreeTableNode getSelectedNode() {
        if (treeTableViewer.getSelection() instanceof ITreeSelection
                && ((ITreeSelection) treeTableViewer.getSelection()).getFirstElement() instanceof AstTreeTableNode) {
            return (AstTreeTableNode) ((ITreeSelection) treeTableViewer.getSelection()).getFirstElement();
        }
        return null;
    }

    /**
     * Get currently selected tree table nodes
     * 
     * @return list of currently selected tree table nodes
     */
    public List<AstTreeTableNode> getSelectedNodes() {
        if (!(treeTableViewer.getSelection() instanceof ITreeSelection)
                || ((ITreeSelection) treeTableViewer.getSelection()).isEmpty()) {
            return Collections.emptyList();
        }
        List<AstTreeTableNode> selectedNodes = new ArrayList<AstTreeTableNode>();
        Object[] selectedObjects = ((ITreeSelection) treeTableViewer.getSelection()).toArray();
        for (int i = 0; i < selectedObjects.length; i++) {
            if (selectedObjects[i] instanceof AstTreeTableNode) {
                selectedNodes.add((AstTreeTableNode) selectedObjects[i]);
            }
        }
        return selectedNodes;
    }

    /**
     * Add new ast object into tree table
     * 
     * @param astObject ast object
     * @param destinationNode destination node to add or null if add to end of script
     * @param addType see {@link NodeAddType}
     */
    public boolean addNewAstObject(ASTNodeWrapper astObject, AstTreeTableNode destinationNode, NodeAddType addType) {
        List<ASTNodeWrapper> astObjects = new ArrayList<ASTNodeWrapper>();
        astObjects.add(astObject);
        return addNewAstObjects(astObjects, destinationNode, addType);
    }

    /**
     * Add new ast objects into tree table
     * 
     * @param astObjects list of ast objects
     * @param destinationNode destination node to add or null if adding to end of script
     * @param addType see {@link NodeAddType}
     */
    public boolean addNewAstObjects(List<? extends ASTNodeWrapper> astObjects, AstTreeTableNode destinationNode,
            NodeAddType addType) {
        if (astObjects == null) {
            return false;
        }
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        boolean addSuccessfully = internalAddNewAstObjects(astObjects, destinationNode, addType);
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
        return addSuccessfully;
    }

    private boolean internalAddNewAstObjects(List<? extends ASTNodeWrapper> astObjects,
            AstTreeTableNode destinationNode, NodeAddType addType) {
        List<AstNodeAddedEdit> astNodeAddedEdits = new ArrayList<AstNodeAddedEdit>();
        if (destinationNode == null) {
            astNodeAddedEdits.addAll(addAstObjects(astObjects, mainClassTreeNode));
        } else if (addType == NodeAddType.Add) {
            if (destinationNode.canHaveChildren()) {
                astNodeAddedEdits.addAll(addAstObjects(astObjects, destinationNode));
            } else {
                astNodeAddedEdits.addAll(insertASTObjects(astObjects, destinationNode, NodeAddType.InserAfter));
            }
        } else {
            astNodeAddedEdits.addAll(insertASTObjects(astObjects, destinationNode, addType));
        }
        if (!astNodeAddedEdits.isEmpty()) {
            processAfterEdits(astNodeAddedEdits);
            return true;
        }
        return false;
    }

    private void processAfterEdits(List<AstNodeAddedEdit> astNodeAddedEdits) {
        List<AstTreeTableNode> needRefreshNodes = new ArrayList<AstTreeTableNode>();
        for (AstNodeAddedEdit astNodeAddedEdit : astNodeAddedEdits) {
            needRefreshNodes.add(astNodeAddedEdit.getParentNode());
        }
        filterRelatedNodeList(needRefreshNodes);
        if (needRefreshNodes.isEmpty()) {
            return;
        }
        setDirty(true);
        for (AstTreeTableNode needRefreshNode : needRefreshNodes) {
            refreshObjectWithoutReloading(needRefreshNode);
        }
        AstNodeAddedEdit lastEdit = astNodeAddedEdits.get(astNodeAddedEdits.size() - 1);
        setSelection(lastEdit.getParentNode(), lastEdit.getNewAstObject());
        setEdit(lastEdit.getParentNode(), lastEdit.getNewAstObject());
    }

    private List<AstNodeAddedEdit> insertASTObjects(List<? extends ASTNodeWrapper> astObjects,
            AstTreeTableNode selectedTreeTableNode, NodeAddType addType) {
        if (astObjects == null || astObjects.isEmpty() || selectedTreeTableNode == null) {
            return Collections.emptyList();
        }
        List<AstNodeAddedEdit> astNodeAddedEdits = new ArrayList<AstNodeAddedEdit>();
        if (addType == NodeAddType.InserAfter) {
            Collections.reverse(astObjects);
        }
        for (ASTNodeWrapper astObject : astObjects) {
            AstNodeAddedEdit astNodeAddedEdit = insertAstObject(astObject, selectedTreeTableNode, addType);
            if (astNodeAddedEdit != null) {
                astNodeAddedEdits.add(astNodeAddedEdit);
            }
        }
        return astNodeAddedEdits;
    }

    private AstNodeAddedEdit insertAstObject(ASTNodeWrapper astObject, AstTreeTableNode sibblingNode,
            NodeAddType addType) {
        if (sibblingNode == null || sibblingNode.getASTObject() == null || sibblingNode.getParent() == null) {
            return null;
        }
        int sibblingIndex = getAstObjectIndex(sibblingNode);
        if (sibblingIndex == -1) {
            return null;
        }
        int newIndex = sibblingIndex + ((addType == NodeAddType.InserAfter) ? 1 : 0);
        ASTNodeWrapper parentAstNode = sibblingNode.getASTObject().getParent();
        if (parentAstNode.isChildAssignble(astObject) && parentAstNode.addChild(astObject, newIndex)) {
            return new AstNodeAddedEdit(sibblingNode.getParent(), astObject);
        }
        return null;
    }

    private AstNodeAddedEdit addAstObject(ASTNodeWrapper astObject, AstTreeTableNode parentNode) {
        if (parentNode == null) {
            return null;
        }
        if (parentNode.isChildAssignble(astObject) && parentNode.addChild(astObject)) {
            if (isComplexStatementOnTheSameLevel(astObject)) {
                parentNode = parentNode.getParent();
            }
            return new AstNodeAddedEdit(parentNode, astObject);
        }
        return null;
    }

    private List<AstNodeAddedEdit> addAstObjects(List<? extends ASTNodeWrapper> astObjects, AstTreeTableNode parentNode) {
        if (parentNode == null) {
            return null;
        }
        List<AstNodeAddedEdit> astNodeAddedEdits = new ArrayList<AstNodeAddedEdit>();
        for (ASTNodeWrapper astObject : astObjects) {
            AstNodeAddedEdit astNodeAddedEdit = addAstObject(astObject, parentNode);
            if (astNodeAddedEdit != null) {
                astNodeAddedEdits.add(astNodeAddedEdit);
            }
        }
        return astNodeAddedEdits;
    }

    /**
     * Get the tree table node of the ast object from a parent node
     * 
     * @param parentNode
     * @param astObject
     * @return the tree table node of the ast object from a parent node, or null if not found
     */
    private AstTreeTableNode getTreeTableNodeOfAstObjectFromParentNode(AstTreeTableNode parentNode,
            ASTNodeWrapper astObject) {
        if (astObject == null) {
            return null;
        }
        for (AstTreeTableNode astNode : parentNode.getChildren()) {
            if (astNode.getASTObject().equals(astObject)) {
                return astNode;
            }
        }
        return null;
    }

    private boolean isComplexStatementOnTheSameLevel(ASTNodeWrapper newAstObject) {
        return newAstObject instanceof ElseStatementWrapper || newAstObject instanceof ElseIfStatementWrapper
                || newAstObject instanceof CatchStatementWrapper || newAstObject instanceof FinallyStatementWrapper;
    }

    private void setSelection(AstTreeTableNode parentNode, ASTNodeWrapper astObject) {
        if (parentNode != null) {
            treeTableViewer.setExpandedState(parentNode, true);
        }
        setSelection(getTreeTableNodeOfAstObjectFromParentNode(parentNode, astObject));
    }

    private void setSelection(AstTreeTableNode treeTableNode) {
        if (treeTableNode == null) {
            return;
        }
        treeTableViewer.setSelection(new StructuredSelection(treeTableNode));
    }

    private void setEdit(AstTreeTableNode parentNode, ASTNodeWrapper astObject) {
        setEdit(getTreeTableNodeOfAstObjectFromParentNode(parentNode, astObject));
    }

    private void setEdit(AstTreeTableNode treeTableNode) {
        if (treeTableNode == null) {
            return;
        }
        setFocus(treeTableNode);
        treeTableViewer.editElement(treeTableNode, 0);
    }

    private void setFocus(AstTreeTableNode treeTableNode) {
        Tree tree = treeTableViewer.getTree();
        ensureTreeGotTopItem(treeTableNode, tree);
        tree.setFocus();
    }

    // Check for avoiding NullPointerException in TreeViewerFocusCellManager.getInitialFocusCell()
    private void ensureTreeGotTopItem(AstTreeTableNode treeTableNode, Tree tree) {
        if (tree.getTopItem() != null) {
            return;
        }
        TreeItem treeItem = new TreeItem(tree, SWT.NONE);
        treeItem.setData(treeTableNode);
        tree.setTopItem(treeItem);
    }

    public void refresh() {
        refresh(null);
    }

    public void refresh(Object object) {
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        refreshObjectWithoutReloading(object);
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
    }

    private void refreshObjectWithoutReloading(Object object) {
        if (object == null) {
            reloadTreeTableNodes();
            return;
        }
        if (object instanceof AstScriptTreeTableNode) {
            treeTableViewer.refresh();
            return;
        }
        treeTableViewer.refresh(object);
    }

    // refresh treetable root
    private void reloadTreeTableNodes() {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        mainClassTreeNode = new AstScriptTreeTableNode(mainClassNodeWrapper, null);
        astTreeTableNodes.add(mainClassTreeNode);
        reloadTestCaseVariables();
        treeTableViewer.setInput(astTreeTableNodes);
    }

    private void reloadExpandedState(Object[] expandedElements) {
        for (Object element : expandedElements) {
            treeTableViewer.setExpandedState(element, true);
        }
        treeTableViewer.getControl().setRedraw(true);
    }

    private Object[] saveExpandedState() {
        treeTableViewer.getControl().setRedraw(false);
        Object[] expandedElements = treeTableViewer.getExpandedElements();
        return expandedElements;
    }

    public void reloadTestCaseVariables() {
        mainClassNodeWrapper.clearFields();
        String testCaseId = parentPart.getTestCase().getIdForDisplay();
        for (VariableEntity variable : parentPart.getVariables()) {
            FieldNodeWrapper field = new FieldNodeWrapper(variable.getName(), Object.class, mainClassNodeWrapper);
            ExpressionWrapper expression = GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(
                    variable.getDefaultValue(), testCaseId);
            if (expression != null) {
                expression.setParent(field);
                field.setInitialValueExpression(expression);
            }
            mainClassNodeWrapper.addField(field);
        }
    }

    public void removeSelectedRows() {
        removeRows(getSelectedNodes());
    }

    public void removeRows(List<AstTreeTableNode> treeTableNodes) {
        if (treeTableNodes == null || treeTableNodes.isEmpty()) {
            return;
        }
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        Collections.reverse(treeTableNodes);
        List<AstTreeTableNode> refreshNodeList = new ArrayList<AstTreeTableNode>();
        for (int i = treeTableNodes.size() - 1; i >= 0; i--) {
            AstTreeTableNode needRefreshNode = removeRow(treeTableNodes.get(i));
            if (needRefreshNode != null) {
                refreshNodeList.add(needRefreshNode);
            }
        }
        processAfterRemove(refreshNodeList);
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
    }

    private void processAfterRemove(List<AstTreeTableNode> refreshNodeList) {
        filterRelatedNodeList(refreshNodeList);
        if (refreshNodeList.isEmpty()) {
            return;
        }
        setDirty(true);
        for (AstTreeTableNode treeTableNode : refreshNodeList) {
            refreshObjectWithoutReloading(treeTableNode);
        }
    }

    private AstTreeTableNode removeRow(AstTreeTableNode treeTableNode) {
        ASTNodeWrapper astObject = treeTableNode.getASTObject();
        if (treeTableNode == null || astObject == null || astObject.getParent() == null) {
            return null;
        }
        if (removeASTNode(astObject.getParent(), astObject)) {
            return treeTableNode.getParent();
        }
        return null;
    }

    private boolean removeASTNode(ASTNodeWrapper parentNode, ASTNodeWrapper childNode) {
        return parentNode.removeChild(childNode);
    }

    private void filterRelatedNodeList(List<AstTreeTableNode> treeTableNodes) {
        if (treeTableNodes == null || treeTableNodes.isEmpty()) {
            return;
        }
        int count = 0;
        while (count < treeTableNodes.size() - 1) {
            boolean foundFlag = false;
            AstTreeTableNode astTreeTableNode = treeTableNodes.get(count);
            if (astTreeTableNode == null) {
                count++;
                continue;
            }
            for (int index = 0; index < treeTableNodes.size(); index++) {
                if (count == index) {
                    continue;
                }
                AstTreeTableNode otherAstTreeTableNode = treeTableNodes.get(index);
                if (otherAstTreeTableNode != null
                        && (astTreeTableNode.equals(otherAstTreeTableNode) || otherAstTreeTableNode.isDescendantNode(astTreeTableNode))) {
                    treeTableNodes.remove(count);
                    foundFlag = true;
                    break;
                }
            }
            if (!foundFlag) {
                count++;
            }
        }
    }

    private void setTopItem(AstTreeTableNode treeTableNode) {
        if (treeTableNode == null) {
            return;
        }
        setTopItem(treeTableNode, treeTableViewer.getTree().getItems());
    }

    private boolean setTopItem(AstTreeTableNode treeTableNode, TreeItem[] treeItems) {
        for (TreeItem treeItem : treeItems) {
            if (treeItem.getData() != null && treeItem.getData().equals(treeTableNode)) {
                treeTableViewer.getTree().setTopItem(treeItem);
                return true;
            }
            if (setTopItem(treeTableNode, treeItem.getItems())) {
                return true;
            }
        }
        return false;
    }

    private AstTreeTableNode getTopItem() {
        TreeItem topItem = treeTableViewer.getTree().getTopItem();
        if (topItem != null && topItem.getData() instanceof AstTreeTableNode) {
            return (AstTreeTableNode) topItem.getData();
        }
        return null;
    }

    public void setDirty(boolean isDirty) {
        parentPart.setDirty(isDirty);
        setChanged(isDirty);
    }

    public void moveUp() {
        move(-1);
    }

    public void moveDown() {
        move(2);
    }

    private void move(int offset) {
        move(offset, getSelectedNode());
    }

    private void move(int offset, AstTreeTableNode selectedNode) {
        if (isUnmoveableAstNode(selectedNode)) {
            return;
        }
        ASTNodeWrapper astNode = selectedNode.getASTObject();
        ASTNodeWrapper parentASTNode = astNode.getParent();
        int newIndex = parentASTNode.indexOf(astNode) + offset;
        if (newIndex < 0) {
            return;
        }
        ASTNodeWrapper cloneNode = astNode.clone();
        if (!(parentASTNode.addChild(cloneNode, newIndex) || parentASTNode.addChild(cloneNode))) {
            return;
        }
        parentASTNode.removeChild(astNode);
        setDirty(true);
        AstTreeTableNode parentNode = selectedNode.getParent();
        refreshObjectWithoutReloading(parentNode);
        setSelection(parentNode, cloneNode);
    }

    private boolean isUnmoveableAstNode(AstTreeTableNode selectedNode) {
        return selectedNode == null || (selectedNode.getASTObject() instanceof ComplexLastStatementWrapper);
    }

    public void updateMethod(MethodNodeWrapper oldMethod, MethodNodeWrapper newMethod) {
        if (mainClassNodeWrapper.setMethod(newMethod, mainClassNodeWrapper.indexOfMethod(oldMethod))) {
            setDirty(true);
            refresh();
            setSelection(mainClassTreeNode, newMethod);
        }
    }

    private int getAstObjectIndex(AstTreeTableNode node) {
        if (node == null || node.getASTObject() == null || node.getASTObject().getParent() == null) {
            return -1;
        }
        ASTNodeWrapper childNode = node.getASTObject();
        return childNode.getParent().indexOf(childNode);

    }

    public void changeFailureHandling(FailureHandling failureHandling) {
        changeFailureHandling(failureHandling, getSelectedNodes());
    }

    private void changeFailureHandling(FailureHandling failureHandling, List<AstTreeTableNode> treeTableNodes) {
        if (failureHandling == null || treeTableNodes == null || treeTableNodes.isEmpty()) {
            return;
        }
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        for (int i = treeTableNodes.size() - 1; i >= 0; i--) {
            if (!(treeTableNodes.get(i) instanceof AstAbstractKeywordTreeTableNode)) {
                continue;
            }
            AstAbstractKeywordTreeTableNode keywordNode = (AstAbstractKeywordTreeTableNode) treeTableNodes.get(i);
            if (!failureHandling.equals(keywordNode.getFailureHandlingValue())
                    && keywordNode.setFailureHandlingValue(failureHandling)) {
                treeTableViewer.update(keywordNode, null);
                setDirty(true);
            }
        }
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
    }

    private String parseAstObjectToString(ASTNodeWrapper astObject) {
        GroovyWrapperParser groovyParser = new GroovyWrapperParser(new StringBuilder());
        groovyParser.parse(astObject);
        return groovyParser.getValue();
    }

    public void copy(List<AstTreeTableNode> copyNodes) {
        collectMarkedRowAndSetToClipboard(copyNodes);
    }

    public void cut(List<AstTreeTableNode> cutNodes) {
        List<AstTreeTableNode> rowsToBeRemoved = collectMarkedRowAndSetToClipboard(cutNodes);
        removeRows(rowsToBeRemoved);
    }

    private List<AstTreeTableNode> collectMarkedRowAndSetToClipboard(List<AstTreeTableNode> markedRows) {
        List<AstTreeTableNode> rowsToBeRemoved = new ArrayList<AstTreeTableNode>();
        StringBuilder scriptSnippets = new StringBuilder();
        for (AstTreeTableNode astTreeTableNode : markedRows) {
            if (!isNodeMoveable(astTreeTableNode)) {
                continue;
            }
            scriptSnippets.append(parseAstObjectToString(astTreeTableNode.getASTObject()));
            scriptSnippets.append(GROOVY_NEW_LINE_CHARACTER);
            rowsToBeRemoved.add(astTreeTableNode);
        }
        if (scriptSnippets.length() == 0) {
            return rowsToBeRemoved;
        }
        final Clipboard cb = new Clipboard(Display.getCurrent());
        ScriptTransferData transferData = new ScriptTransferData(scriptSnippets.toString(), parentPart.getTestCase()
                .getId());
        cb.setContents(new Object[] { new ScriptTransferData[] { transferData } },
                new Transfer[] { new ScriptTransfer() });
        return rowsToBeRemoved;
    }

    public static boolean isNodeMoveable(AstTreeTableNode astTreeTableNode) {
        return (!(astTreeTableNode.getASTObject() instanceof ComplexLastStatementWrapper) && !(astTreeTableNode.getASTObject() instanceof ComplexChildStatementWrapper));
    }

    public void paste(AstTreeTableNode destinationNode, NodeAddType addType) {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        Object data = clipboard.getContents(new ScriptTransfer());
        String snippet = null;
        if (data instanceof String) {
            snippet = (String) data;
        } else if (data instanceof ScriptTransferData[]) {
            snippet = ((ScriptTransferData[]) data)[0].getScriptSnippet();
        }
        try {
            ScriptNodeWrapper scriptNode = GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(snippet);
            if (scriptNode == null) {
                return;
            }
            addNewAstObjects(new ArrayList<StatementWrapper>(scriptNode.getBlock().getStatements()), destinationNode,
                    addType);
        } catch (GroovyParsingException e) {
            LoggerSingleton.logError(e);
        }
    }

    public void disable() {
        disable(getSelectedNodes());
    }

    public void disable(List<AstTreeTableNode> treeTableNodes) {
        toogleDisabledMode(treeTableNodes, true);
    }

    public void enable() {
        enable(getSelectedNodes());
    }

    public void enable(List<AstTreeTableNode> treeTableNodes) {
        toogleDisabledMode(treeTableNodes, false);
    }

    private void toogleDisabledMode(List<AstTreeTableNode> treeTableNodes, boolean isDisableMode) {
        if (treeTableNodes == null || treeTableNodes.isEmpty()) {
            return;
        }
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        for (AstTreeTableNode treeTableNode : treeTableNodes) {
            if (!(treeTableNode instanceof AstStatementTreeTableNode)
                    || !((AstStatementTreeTableNode) treeTableNode).canBeDisabled()) {
                continue;
            }
            AstStatementTreeTableNode statementNode = (AstStatementTreeTableNode) treeTableNode;
            boolean changeFlag = (isDisableMode) ? statementNode.disable() : statementNode.enable();
            if (changeFlag) {
                treeTableViewer.update(statementNode, null);
                setDirty(true);
            }
        }
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
        treeTableViewer.setSelection(null);
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    /**
     * Add new ast object base on menu item id
     * 
     * @param astObjectId menu item id, see {@link TreeTableMenuItemConstants}
     * @param destinationNode destination node to add
     * @param addType see {@link NodeAddType}
     */
    public void addNewAstObject(int astObjectId, AstTreeTableNode destinationNode, NodeAddType addType) {
        switch (astObjectId) {
            case TreeTableMenuItemConstants.CUSTOM_KEYWORD_MENU_ITEM_ID:
                addNewCustomKeyword(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_ID:
                addNewIfStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.ELSE_STATEMENT_MENU_ITEM_ID:
                addNewElseStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.ELSE_IF_STATEMENT_MENU_ITEM_ID:
                addNewElseIfStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.WHILE_STATEMENT_MENU_ITEM_ID:
                addNewWhileStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_ID:
                addNewForStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_ID:
                addNewBinaryStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_ID:
                addNewAssertStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.CALL_METHOD_STATEMENT_MENU_ITEM_ID:
                addNewMethodCall(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.BREAK_STATMENT_MENU_ITEM_ID:
                addNewBreakStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.CONTINUE_STATMENT_MENU_ITEM_ID:
                addNewContinueStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.RETURN_STATMENT_MENU_ITEM_ID:
                addNewReturnStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.SWITCH_STATMENT_MENU_ITEM_ID:
                addNewSwitchStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.CASE_STATMENT_MENU_ITEM_ID:
                addNewCaseStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.DEFAULT_STATMENT_MENU_ITEM_ID:
                addNewDefaultStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_ID:
                addNewTryStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.CATCH_STATMENT_MENU_ITEM_ID:
                addNewCatchStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.FINALLY_STATMENT_MENU_ITEM_ID:
                addNewFinallyStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.THROW_STATMENT_MENU_ITEM_ID:
                addNewThrowStatement(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.METHOD_MENU_ITEM_ID:
                addNewMethod(destinationNode, addType);
                break;
            case TreeTableMenuItemConstants.CALL_TEST_CASE_MENU_ITEM_ID:
                addCallTestCases(destinationNode, addType);
                break;
            default:
                if (TreeTableMenuItemConstants.isBuildInKeywordID(astObjectId)) {
                    addNewBuiltInKeyword(destinationNode, addType,
                            TreeTableMenuItemConstants.getContributingClassName(astObjectId));
                }
                break;
        }
    }

    public boolean validateTestCase(TestCaseEntity calledTestCase) {
        if (calledTestCase == null)
            return false;

        if (StringUtils.equals(parentPart.getTestCase().getId(), calledTestCase.getId())) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_TEST_CASE_CANNOT_CALL_ITSELF);
            return false;
        }

        return true;
    }

    public void addCallTestCases(AstTreeTableNode destinationNode, NodeAddType addType) {
        if (ProjectController.getInstance().getCurrentProject() == null) {
            return;
        }
        try {
            addCallTestCases(destinationNode, addType, collectCalledTestCases());
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_CALL_TEST_CASE);
            LoggerSingleton.logError(e);
        }
    }

    public void addCallTestCases(AstTreeTableNode destinationNode, NodeAddType addType, TestCaseEntity[] testCaseArray) {
        if (testCaseArray == null || testCaseArray.length <= 0) {
            return;
        }
        List<StatementWrapper> statementsToAdd = new ArrayList<StatementWrapper>();
        List<VariableEntity> variablesToAdd = new ArrayList<VariableEntity>();
        for (TestCaseEntity testCase : testCaseArray) {
            statementsToAdd.add(AstEntityInputUtil.generateCallTestCaseExpresionStatement(testCase, variablesToAdd));
        }
        addNewAstObjects(statementsToAdd, destinationNode, addType);
        if (variablesToAdd.isEmpty()) {
            return;
        }
        parentPart.addVariables(variablesToAdd.toArray(new VariableEntity[variablesToAdd.size()]));
    }

    private TestCaseEntity[] collectCalledTestCases() throws Exception {
        TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(Display.getCurrent().getActiveShell(),
                new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(new EntityProvider()));
        dialog.setAllowMultiple(false);
        dialog.setTitle(StringConstants.EDI_TITLE_TEST_CASE_BROWSER);
        dialog.setInput(TreeEntityUtil.getChildren(null,
                FolderController.getInstance().getTestCaseRoot(ProjectController.getInstance().getCurrentProject())));
        if (dialog.open() != Window.OK) {
            return new TestCaseEntity[0];
        }
        Object[] selectedObjects = dialog.getResult();
        if (selectedObjects == null || selectedObjects.length == 0) {
            return new TestCaseEntity[0];
        }
        Set<TestCaseEntity> testCaseSet = new LinkedHashSet<TestCaseEntity>();
        for (Object object : selectedObjects) {
            if (!(object instanceof FolderTreeEntity) && !(object instanceof TestCaseTreeEntity)) {
                continue;
            }

            ITreeEntity treeEntity = (ITreeEntity) object;
            if (treeEntity instanceof FolderTreeEntity) {
                for (TestCaseEntity testCase : TestCaseEntityUtil.getTestCasesFromFolderTree((FolderTreeEntity) treeEntity)) {
                    if (validateTestCase(testCase)) {
                        testCaseSet.add(testCase);
                    }
                }
            } else if (treeEntity instanceof TestCaseTreeEntity) {
                TestCaseEntity calledTestCase = ((TestCaseTreeEntity) treeEntity).getObject();
                if (validateTestCase(calledTestCase)) {
                    testCaseSet.add(calledTestCase);
                }
            }
        }
        return testCaseSet.toArray(new TestCaseEntity[testCaseSet.size()]);
    }

    public void addNewMethod(AstTreeTableNode destinationNode, NodeAddType addType) {
        MethodObjectBuilderDialog dialog = new MethodObjectBuilderDialog(Display.getCurrent().getActiveShell(), null,
                null);
        if (dialog.open() != Window.OK || dialog.getReturnValue() == null) {
            return;
        }
        MethodNodeWrapper method = dialog.getReturnValue();
        int selectedMethodIndex = -1;
        AstTreeTableNode selectedNode = getSelectedNode();
        if (selectedNode instanceof AstMethodTreeTableNode) {
            selectedMethodIndex = mainClassNodeWrapper.indexOfMethod(((AstMethodTreeTableNode) selectedNode).getASTObject());
        }
        if (selectedMethodIndex == -1) {
            mainClassNodeWrapper.addMethod(method);
        } else {
            if (addType == NodeAddType.Add || addType == NodeAddType.InserAfter) {
                selectedMethodIndex++;
            }
            mainClassNodeWrapper.addMethod(method, selectedMethodIndex);
        }
        setDirty(true);
        refresh();
        setSelection(mainClassTreeNode, method);
        setFocus(getTreeTableNodeOfAstObjectFromParentNode(mainClassTreeNode, method));
    }

    public void addNewDefaultBuiltInKeyword(NodeAddType addType) {
        addNewBuiltInKeyword(getSelectedNode(), addType,
                TestCasePreferenceDefaultValueInitializer.getDefaultKeywordType());
    }

    private void addNewBuiltInKeyword(AstTreeTableNode destinationNode, NodeAddType addType, String className) {
        addNewBuiltInKeyword(destinationNode, addType,
                KeywordController.getInstance().getBuiltInKeywordClassByName(className));
    }

    private void addNewBuiltInKeyword(AstTreeTableNode destinationNode, NodeAddType addType, KeywordClass keywordClass) {
        if (keywordClass == null) {
            return;
        }
        addDefaultImports();
        String defaultSettingKeywordName = TestCasePreferenceDefaultValueInitializer.getDefaultKeywords().get(
                keywordClass.getName());
        StatementWrapper newBuiltinKeywordStatement = null;
        if (!StringUtils.isBlank(defaultSettingKeywordName)
                && (KeywordController.getInstance().getBuiltInKeywordByName(keywordClass.getName(),
                        defaultSettingKeywordName, null)) != null) {

            MethodCallExpressionWrapper keywordMethodCallExpression = new MethodCallExpressionWrapper(
                    keywordClass.getSimpleName(), defaultSettingKeywordName);

            AstKeywordsInputUtil.generateMethodCallArguments(
                    keywordMethodCallExpression,
                    KeywordController.getInstance().getBuiltInKeywordByName(keywordClass.getName(),
                            defaultSettingKeywordName, null));

            newBuiltinKeywordStatement = new ExpressionStatementWrapper(keywordMethodCallExpression, null);

        } else {
            newBuiltinKeywordStatement = AstKeywordsInputUtil.createBuiltInKeywordStatement(
                    keywordClass.getSimpleName(),
                    KeywordController.getInstance().getBuiltInKeywords(keywordClass.getSimpleName(), true).get(0).getName());
        }
        addNewAstObject(newBuiltinKeywordStatement, destinationNode, addType);
    }

    public void addDefaultImports() {
        for (KeywordClass keywordClass : KeywordController.getInstance().getBuiltInKeywordClasses()) {
            addImport(keywordClass.getType());
        }
        addImport(ObjectRepository.class);
        addImport(TestCaseFactory.class);
        addImport(TestDataFactory.class);
        addImport(FailureHandling.class);
        addImport(TestCase.class);
        addImport(TestData.class);
        addImport(TestObject.class);
    }

    public void addNewCustomKeyword(AstTreeTableNode destinationNode, NodeAddType addType) {
        StatementWrapper customKeywordStatement = AstKeywordsInputUtil.createNewCustomKeywordStatement();
        if (customKeywordStatement == null) {
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE, StringConstants.PA_ERROR_MSG_NO_CUSTOM_KEYWORD);
            return;
        }
        addDefaultImports();
        addNewAstObject(customKeywordStatement, destinationNode, addType);
    }

    private void addNewThrowStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new ThrowStatementWrapper(), destinationNode, addType);
    }

    public void addNewIfStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new IfStatementWrapper(), destinationNode, addType);
    }

    public void addNewElseStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new ElseStatementWrapper(), destinationNode, addType);
    }

    public void addNewElseIfStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new ElseIfStatementWrapper(), destinationNode, addType);
    }

    public void addNewWhileStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new WhileStatementWrapper(), destinationNode, addType);
    }

    public void addNewForStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new ForStatementWrapper(), destinationNode, addType);
    }

    public void addNewBinaryStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new ExpressionStatementWrapper(new BinaryExpressionWrapper()), destinationNode, addType);
    }

    public void addNewAssertStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new AssertStatementWrapper(), destinationNode, addType);
    }

    public void addNewMethodCall(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new ExpressionStatementWrapper(new MethodCallExpressionWrapper()), destinationNode, addType);
    }

    public void addNewBreakStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new BreakStatementWrapper(), destinationNode, addType);
    }

    public void addNewContinueStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new ContinueStatementWrapper(), destinationNode, addType);
    }

    public void addNewReturnStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new ReturnStatementWrapper(), destinationNode, addType);
    }

    public void addNewSwitchStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new SwitchStatementWrapper(), destinationNode, addType);
    }

    public void addNewCaseStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new CaseStatementWrapper(), destinationNode, addType);
    }

    public void addNewDefaultStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new DefaultStatementWrapper(), destinationNode, addType);
    }

    public void addNewTryStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new TryCatchStatementWrapper(), destinationNode, addType);
    }

    public void addNewCatchStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new CatchStatementWrapper(), destinationNode, addType);
    }

    public void addNewFinallyStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        addNewAstObject(new FinallyStatementWrapper(), destinationNode, addType);
    }
}
