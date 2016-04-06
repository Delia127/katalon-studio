package com.kms.katalon.composer.testcase.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testcase.ast.dialogs.MethodObjectBuilderDialog;
import com.kms.katalon.composer.testcase.ast.treetable.AstAbstractKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCaseStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCatchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseIfStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstFinallyStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstIfStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstScriptTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstSwitchDefaultStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstSwitchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTryStatementTreeTableNode;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.FieldNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapEntryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.AssertStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BreakStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ContinueStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ElseIfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
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
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.entity.folder.FolderEntity;
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
     * Add new ast objects into tree table
     * 
     * @param astObjects
     * list of ast objects
     * @param destinationNode
     * destination node to add or null if adding to end of script
     * @param addType
     * {@link NodeAddType}
     * @throws Exception
     */
    public void addNewAstObjects(List<? extends ASTNodeWrapper> astObjects, AstTreeTableNode destinationNode,
            NodeAddType addType) {
        if (astObjects == null) {
            return;
        }
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        List<AstNodeAddedEdit> astNodeAddedEdits = new ArrayList<AstNodeAddedEdit>();
        if (destinationNode != null) {
            if (addType == NodeAddType.Add) {
                if (destinationNode.canHaveChildren()) {
                    for (ASTNodeWrapper astObject : astObjects) {
                        addNewAstObjectToParentNode(astObject, destinationNode.getASTObject(), -1, astNodeAddedEdits,
                                destinationNode);
                    }
                } else {
                    addASTObjectsAfter(astObjects, destinationNode, astNodeAddedEdits);
                }
            } else if (addType == NodeAddType.InserAfter) {
                addASTObjectsAfter(astObjects, destinationNode, astNodeAddedEdits);
            } else if (addType == NodeAddType.InserBefore) {
                addASTObjectsBefore(astObjects, destinationNode, astNodeAddedEdits);
            }
        } else {
            addAstObjectsIntoScriptMainBlock(astObjects, astNodeAddedEdits);
        }
        List<AstTreeTableNode> needRefreshNodes = new ArrayList<AstTreeTableNode>();
        for (AstNodeAddedEdit astNodeAddedEdit : astNodeAddedEdits) {
            needRefreshNodes.add(astNodeAddedEdit.getParentNode());
        }
        filterRelatedNodeList(needRefreshNodes);
        if (!needRefreshNodes.isEmpty()) {
            setDirty(true);
            for (AstTreeTableNode needRefreshNode : needRefreshNodes) {
                refreshObjectWithoutReloading(needRefreshNode);
            }
            AstNodeAddedEdit lastEdit = astNodeAddedEdits.get(astNodeAddedEdits.size() - 1);
            setSelection(lastEdit.getParentNode(), lastEdit.getNewAstObject());
            setEdit(lastEdit.getParentNode(), lastEdit.getNewAstObject());
        }
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
    }

    private void addAstObjectsIntoScriptMainBlock(List<? extends ASTNodeWrapper> astObjects,
            List<AstNodeAddedEdit> astNodeAddedEdits) {
        for (ASTNodeWrapper astObject : astObjects) {
            AstTreeTableUtil.addChild(mainClassNodeWrapper, astObject, -1);
        }
        astNodeAddedEdits.add(new AstNodeAddedEdit(mainClassTreeNode, astObjects.get(astObjects.size() - 1)));
    }

    private void addASTObjectsBefore(List<? extends ASTNodeWrapper> astObjects, AstTreeTableNode selectedTreeTableNode,
            List<AstNodeAddedEdit> astNodeAddedEdits) {
        if (astObjects == null || astObjects.isEmpty() || selectedTreeTableNode == null) {
            return;
        }
        for (ASTNodeWrapper astObject : astObjects) {
            insertAstObject(astObject, selectedTreeTableNode, NodeAddType.InserBefore, astNodeAddedEdits);
        }
    }

    private void addASTObjectsAfter(List<? extends ASTNodeWrapper> astObjects, AstTreeTableNode selectedTreeTableNode,
            List<AstNodeAddedEdit> astNodeAddedEdits) {
        if (astObjects == null || astObjects.isEmpty() || selectedTreeTableNode == null) {
            return;
        }
        for (int i = astObjects.size() - 1; i >= 0; i--) {
            insertAstObject(astObjects.get(i), selectedTreeTableNode, NodeAddType.InserAfter, astNodeAddedEdits);
        }
    }

    /**
     * Add new ast object into tree table
     * 
     * @param astObject
     * ast object
     * @param destinationNode
     * destination node to add or null if add to end of script
     * @param addType
     * {@link NodeAddType}
     * @throws Exception
     */
    public void addNewAstObject(ASTNodeWrapper astObject, AstTreeTableNode destinationNode, NodeAddType addType) {
        List<ASTNodeWrapper> astObjects = new ArrayList<ASTNodeWrapper>();
        astObjects.add(astObject);
        addNewAstObjects(astObjects, destinationNode, addType);
    }

    private void insertAstObject(ASTNodeWrapper astObject, AstTreeTableNode sibblingNode, NodeAddType addType,
            List<AstNodeAddedEdit> astNodeAddedEdits) {
        if (sibblingNode.getParent() == null) {
            return;
        }
        int sibblingIndex = getAstObjectIndex(sibblingNode);
        if (sibblingIndex == -1) {
            return;
        }
        addNewAstObjectToParentNode(astObject, sibblingNode.getASTObject().getParent(), sibblingIndex
                + ((addType == NodeAddType.InserAfter) ? 1 : 0), astNodeAddedEdits, sibblingNode.getParent());
    }

    /**
     * Add new ast object into a tree table node
     * 
     * @param astObject
     * ast object
     * @param parentNode
     * tree table node to add
     * @param index
     * the new index, or -1 to add at the end of the list
     * @throws Exception
     */
    private void addNewAstObjectToParentNode(ASTNodeWrapper astObject, ASTNodeWrapper parentObject, int index,
            List<AstNodeAddedEdit> astNodeAddedEdits, AstTreeTableNode parentNode) {
        AstTreeTableUtil.addChild(parentObject, astObject, index);
        astNodeAddedEdits.add(new AstNodeAddedEdit(parentNode, astObject));
    }

    public AstTreeTableNode getTreeTableNodeOfAstObjectFromParentNode(AstTreeTableNode parentNode,
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
        treeTableViewer.getTree().setFocus();
        treeTableViewer.editElement(treeTableNode, 0);
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

    // refresh treetable root
    private void reloadTreeTableNodes() {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        mainClassTreeNode = new AstScriptTreeTableNode(mainClassNodeWrapper, null);
        astTreeTableNodes.add(mainClassTreeNode);
        reloadTestCaseVariables();
        treeTableViewer.setInput(astTreeTableNodes);
    }

    public void reloadTestCaseVariables() {
        mainClassNodeWrapper.getFields().clear();
        for (VariableEntity variable : parentPart.getVariables()) {
            FieldNodeWrapper field = new FieldNodeWrapper(variable.getName(), Object.class, mainClassNodeWrapper);
            ExpressionWrapper expression = GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(variable.getDefaultValue());
            if (expression != null) {
                expression.setParent(field);
                field.setInitialValueExpression(expression);
            }
            mainClassNodeWrapper.getFields().add(field);
        }
    }

    public void removeSelectedRows() throws Exception {
        removeRows(getSelectedNodes());
    }

    public void removeRows(List<AstTreeTableNode> treeTableNodes) {
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        List<AstTreeTableNode> refreshNodeList = new ArrayList<AstTreeTableNode>();
        for (int i = treeTableNodes.size() - 1; i >= 0; i--) {
            removeRow(treeTableNodes.get(i), refreshNodeList);
        }
        filterRelatedNodeList(refreshNodeList);
        if (!refreshNodeList.isEmpty()) {
            setDirty(true);
            for (AstTreeTableNode treeTableNode : refreshNodeList) {
                refreshObjectWithoutReloading(treeTableNode);
            }
        }
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
    }

    private void filterRelatedNodeList(List<AstTreeTableNode> treeTableNodes) {
        if (treeTableNodes == null || treeTableNodes.isEmpty()) {
            return;
        }
        int count = 0;
        while (count < treeTableNodes.size() - 1) {
            boolean foundFlag = false;
            for (int index = 0; index < treeTableNodes.size(); index++) {
                if (count == index) {
                    continue;
                }
                if ((treeTableNodes.get(count) != null && treeTableNodes.get(count).equals(treeTableNodes.get(index)))
                        || isDescendant(treeTableNodes.get(count), treeTableNodes.get(index))) {
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

    /**
     * Check if treeTableNode_1 is descendant of treeTableNode_2
     * 
     * @param treeTableNode_1
     * @param treeTableNode_2
     * @return boolean
     */
    private static boolean isDescendant(AstTreeTableNode treeTableNode_1, AstTreeTableNode treeTableNode_2) {
        // null means root node
        if (treeTableNode_2 == null) {
            return true;
        } else if (treeTableNode_1 == null) {
            return false;
        } else if (!(treeTableNode_2.canHaveChildren())) {
            return false;
        }
        boolean isDescendant = false;
        try {
            for (AstTreeTableNode childNode : treeTableNode_2.getChildren()) {
                if (treeTableNode_1.equals(childNode)) {
                    isDescendant = true;
                    break;
                }
                isDescendant = isDescendant && (isDescendant(treeTableNode_1, childNode));
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return isDescendant;
    }

    private void setTopItem(AstTreeTableNode treeTableNode) {
        if (treeTableNode != null) {
            setTopItem(treeTableNode, treeTableViewer.getTree().getItems());
        }
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

    private void removeRow(AstTreeTableNode treeTableNode, List<AstTreeTableNode> refreshNodeList) {
        if (treeTableNode == null) {
            return;
        }
        if (treeTableNode instanceof AstElseStatementTreeTableNode) {
            removeElseStatement((AstElseStatementTreeTableNode) treeTableNode);
            refreshNodeList.add(treeTableNode.getParent());
        } else if (treeTableNode instanceof AstSwitchDefaultStatementTreeTableNode) {
            removeSwitchDefaultStatement((AstSwitchDefaultStatementTreeTableNode) treeTableNode);
            refreshNodeList.add(treeTableNode.getParent());
        } else if (treeTableNode instanceof AstFinallyStatementTreeTableNode) {
            removeFinallyStatement((AstFinallyStatementTreeTableNode) treeTableNode);
            refreshNodeList.add(treeTableNode.getParent());
        } else {
            removeStatement(treeTableNode, refreshNodeList);
        }
    }

    private void removeStatement(AstTreeTableNode treeTableNode, List<AstTreeTableNode> refreshNodeList) {
        AstTreeTableNode parentNode = treeTableNode.getParent();
        AstTreeTableUtil.removeChild(treeTableNode.getASTObject().getParent(), treeTableNode.getASTObject());
        refreshNodeList.add(parentNode);
    }

    private void removeFinallyStatement(AstFinallyStatementTreeTableNode treeTableNode) {
        BlockStatementWrapper finallyStatementWrapper = (BlockStatementWrapper) treeTableNode.getASTObject();
        if (!(finallyStatementWrapper.getParent() instanceof TryCatchStatementWrapper)) {
            return;
        }
        ((TryCatchStatementWrapper) finallyStatementWrapper.getParent()).setFinallyStatement(null);
    }

    private void removeSwitchDefaultStatement(AstSwitchDefaultStatementTreeTableNode treeTableNode) {
        BlockStatementWrapper switchDefault = (BlockStatementWrapper) treeTableNode.getASTObject();
        if (!(switchDefault.getParent() instanceof SwitchStatementWrapper)) {
            return;
        }
        ((SwitchStatementWrapper) switchDefault.getParent()).setDefaultStatement(null);
    }

    private void removeElseStatement(AstElseStatementTreeTableNode treeTableNode) {
        BlockStatementWrapper elseStatement = (BlockStatementWrapper) treeTableNode.getASTObject();
        if (!(elseStatement.getParent() instanceof IfStatementWrapper)) {
            return;
        }
        ((IfStatementWrapper) elseStatement.getParent()).setElseStatement(null);
    }

    public void setDirty(boolean isDirty) {
        parentPart.setDirty(isDirty);
        setChanged(isDirty);
    }

    public void moveUp() {
        AstTreeTableNode selectedNode = getSelectedNode();
        if (selectedNode == null || !(selectedNode.getParent().canHaveChildren())
                || selectedNode instanceof AstElseStatementTreeTableNode
                || selectedNode instanceof AstSwitchDefaultStatementTreeTableNode
                || selectedNode instanceof AstFinallyStatementTreeTableNode) {
            return;
        }
        AstTreeTableNode parentNode = selectedNode.getParent();
        List<AstTreeTableNode> childNodes = parentNode.getChildren();
        int nodeIndex = childNodes.indexOf(selectedNode);
        if (nodeIndex - 1 < 0) {
            return;
        }
        move(selectedNode, childNodes.get(nodeIndex - 1), NodeAddType.InserBefore);
    }

    public void moveDown() {
        AstTreeTableNode selectedNode = getSelectedNode();
        if (selectedNode == null || !(selectedNode.getParent().canHaveChildren())
                || selectedNode instanceof AstElseStatementTreeTableNode
                || selectedNode instanceof AstSwitchDefaultStatementTreeTableNode
                || selectedNode instanceof AstFinallyStatementTreeTableNode) {
            return;
        }
        AstTreeTableNode parentNode = selectedNode.getParent();
        List<AstTreeTableNode> childNodes = parentNode.getChildren();
        int nodeIndex = getAstObjectIndex(selectedNode);
        if (nodeIndex + 1 >= childNodes.size()) {
            return;
        }
        move(selectedNode, childNodes.get(nodeIndex + 1), NodeAddType.InserAfter);
    }

    public void move(AstTreeTableNode sourceNode, AstTreeTableNode destinationNode, NodeAddType addType) {
        if (sourceNode == null
                || destinationNode == null
                || (sourceNode instanceof AstElseIfStatementTreeTableNode && !(destinationNode instanceof AstElseIfStatementTreeTableNode))
                || (sourceNode instanceof AstCatchStatementTreeTableNode && !(destinationNode instanceof AstCatchStatementTreeTableNode))
                || (sourceNode instanceof AstCaseStatementTreeTableNode && !(destinationNode instanceof AstCaseStatementTreeTableNode))) {
            return;
        }
        if (destinationNode instanceof AstElseIfStatementTreeTableNode
                && !(sourceNode instanceof AstElseIfStatementTreeTableNode)) {
            destinationNode = getTreeTableNodeOfAstObjectFromParentNode(destinationNode.getParent(),
                    ((AstElseIfStatementTreeTableNode) destinationNode).getASTObject().getParent());
        } else if (destinationNode instanceof AstCatchStatementTreeTableNode
                && !(sourceNode instanceof AstCatchStatementTreeTableNode)) {
            destinationNode = getTreeTableNodeOfAstObjectFromParentNode(destinationNode.getParent(),
                    ((AstCatchStatementTreeTableNode) destinationNode).getASTObject().getParent());
        } else if (destinationNode instanceof AstElseStatementTreeTableNode) {
            destinationNode = getTreeTableNodeOfAstObjectFromParentNode(destinationNode.getParent(),
                    ((AstElseStatementTreeTableNode) destinationNode).getASTObject().getParent());
        } else if (destinationNode instanceof AstFinallyStatementTreeTableNode) {
            destinationNode = getTreeTableNodeOfAstObjectFromParentNode(destinationNode.getParent(),
                    ((AstFinallyStatementTreeTableNode) destinationNode).getASTObject().getParent());
        }
        if (destinationNode == null || !(destinationNode.getParent().canHaveChildren())
                || sourceNode == destinationNode) {
            return;
        }
        List<AstTreeTableNode> destinationNodeList = destinationNode.getParent().getChildren(); // TODO: Weird
        int destinationNodeIndex = destinationNodeList.indexOf(destinationNode);
        int listSize = destinationNodeList.size() - 1;
        List<AstTreeTableNode> removeRow = new ArrayList<AstTreeTableNode>();
        removeRow.add(sourceNode);
        removeRows(removeRow);

        if (destinationNodeIndex == listSize) {
            addNewAstObject(sourceNode.getASTObject(), destinationNode, NodeAddType.InserAfter);
        } else {
            addNewAstObject(sourceNode.getASTObject(), destinationNode, addType);
        }
    }

    public void updateMethod(MethodNodeWrapper oldMethod, MethodNodeWrapper newMethod) throws Exception {
        int methodIndex = mainClassNodeWrapper.getMethods().indexOf(oldMethod);
        mainClassNodeWrapper.getMethods().remove(oldMethod);
        addMethod(newMethod, methodIndex);
        setDirty(true);
        refresh();
        setSelection(null, newMethod);
    }

    private void addMethod(MethodNodeWrapper newMethod, int methodIndex) {
        newMethod.setParent(mainClassNodeWrapper);
        if (methodIndex >= 0 && methodIndex < mainClassNodeWrapper.getMethods().size()) {
            mainClassNodeWrapper.getMethods().add(methodIndex, newMethod);
        } else {
            mainClassNodeWrapper.getMethods().add(newMethod);
        }
    }

    private int getAstObjectIndex(AstTreeTableNode node) {
        if (node == null || node.getASTObject() == null || node.getASTObject().getParent() == null) {
            return -1;
        }
        return AstTreeTableUtil.getIndex(node.getASTObject().getParent(), node.getASTObject());
    }

    public void changeFailureHandling(FailureHandling failureHandling) throws Exception {
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
        StringBuilder stringBuilder = new StringBuilder();
        GroovyWrapperParser groovyParser = new GroovyWrapperParser(stringBuilder);
        groovyParser.parse(astObject);
        return stringBuilder.toString();
    }

    public void copy(List<AstTreeTableNode> copyNodes) {
        StringBuilder scriptSnippets = new StringBuilder();
        for (AstTreeTableNode astTreeTableNode : copyNodes) {
            scriptSnippets.append(parseAstObjectToString(astTreeTableNode.getASTObject()));
            scriptSnippets.append(GROOVY_NEW_LINE_CHARACTER);
        }
        if (scriptSnippets.length() == 0) {
            return;
        }
        final Clipboard cb = new Clipboard(Display.getCurrent());
        ScriptTransferData transferData = new ScriptTransferData(scriptSnippets.toString(), parentPart.getTestCase()
                .getId());
        cb.setContents(new Object[] { new ScriptTransferData[] { transferData } },
                new Transfer[] { new ScriptTransfer() });
    }

    public void cut(List<AstTreeTableNode> cutNodes) throws Exception {
        copy(cutNodes);
        removeRows(cutNodes);
    }

    public void paste(AstTreeTableNode destinationNode, NodeAddType addType) throws Exception {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        Object data = clipboard.getContents(new ScriptTransfer());
        String snippet = null;
        if (data instanceof String) {
            snippet = (String) data;
        } else if (data instanceof ScriptTransferData[]) {
            snippet = ((ScriptTransferData[]) data)[0].getScriptSnippet();
        }
        ScriptNodeWrapper scriptNode = GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(snippet);
        if (scriptNode == null) {
            return;
        }
        addNewAstObjects(scriptNode.getBlock().getStatements(), destinationNode, addType);
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
     * @param astObjectId
     * menu item id, see {@link TreeTableMenuItemConstants}
     * @param destinationNode
     * destination node to add
     * @param addType
     * see {@link NodeAddType}
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
                addNewElseStatement(destinationNode);
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
                callTestCase(destinationNode, addType);
                break;
            default:
                if (TreeTableMenuItemConstants.isBuildInKeywordID(astObjectId)) {
                    addNewBuiltInKeyword(astObjectId, destinationNode, addType);
                }
                break;
        }
    }

    public boolean qualify(TestCaseEntity calledTestCase) {
        if (calledTestCase == null)
            return false;

        if (parentPart.getTestCase().getId().equals(calledTestCase.getId())) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_TEST_CASE_CANNOT_CALL_ITSELF);
            return false;
        }

        return true;
    }

    public void callTestCase(AstTreeTableNode destinationNode, NodeAddType addType) {
        if (ProjectController.getInstance().getCurrentProject() == null) {
            return;
        }
        try {
            List<TestCaseEntity> testCaseList = openDialogAndCollectTestCases();
            if (testCaseList.isEmpty()) {
                return;
            }
            List<StatementWrapper> statementsToAdd = new ArrayList<StatementWrapper>();
            List<VariableEntity> variablesToAdd = new ArrayList<VariableEntity>();
            for (TestCaseEntity testCase : testCaseList) {
                statementsToAdd.add(generateStatementWrapperForCalledTestCase(testCase, variablesToAdd));
            }
            addNewAstObjects(statementsToAdd, destinationNode, addType);
            if (variablesToAdd.isEmpty()) {
                return;
            }
            parentPart.addVariables(variablesToAdd.toArray(new VariableEntity[variablesToAdd.size()]));
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_CALL_TEST_CASE);
            LoggerSingleton.logError(e);
        }
    }

    private List<TestCaseEntity> openDialogAndCollectTestCases() throws Exception {
        TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(Display.getCurrent().getActiveShell(),
                new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(new EntityProvider()));
        dialog.setAllowMultiple(false);
        dialog.setTitle(StringConstants.EDI_TITLE_TEST_CASE_BROWSER);
        dialog.setInput(TreeEntityUtil.getChildren(null,
                FolderController.getInstance().getTestCaseRoot(ProjectController.getInstance().getCurrentProject())));
        if (dialog.open() != Window.OK) {
            return Collections.emptyList();
        }
        List<TestCaseEntity> testCaseList = new ArrayList<TestCaseEntity>();
        for (Object object : dialog.getResult()) {
            if (!(object instanceof ITreeEntity))
                continue;

            ITreeEntity treeEntity = (ITreeEntity) object;
            if (treeEntity.getObject() instanceof FolderEntity) {
                for (TestCaseEntity testCase : TestCaseEntityUtil.getTestCasesFromFolderTree((FolderTreeEntity) treeEntity)) {
                    if (qualify(testCase)) {
                        testCaseList.add(testCase);
                    }
                }
            } else if (treeEntity.getObject() instanceof TestCaseEntity) {
                TestCaseEntity calledTestCase = (TestCaseEntity) treeEntity.getObject();
                if (qualify(calledTestCase)) {
                    testCaseList.add(calledTestCase);
                }
            }
        }
        return testCaseList;
    }

    private StatementWrapper generateStatementWrapperForCalledTestCase(TestCaseEntity testCase,
            List<VariableEntity> variablesToAdd) {
        ExpressionStatementWrapper statement = AstEntityInputUtil.generateCallTestCaseExpresionStatement(testCase, null);
        variablesToAdd.addAll(getCallTestCaseVariables((ArgumentListExpressionWrapper) ((MethodCallExpressionWrapper) statement.getExpression()).getArguments()));
        return statement;
    }

    public static List<VariableEntity> getCallTestCaseVariables(
            ArgumentListExpressionWrapper argumentListExpressionWrapper) {
        if (TestCasePreferenceDefaultValueInitializer.isSetGenerateVariableDefaultValue()
                || !TestCasePreferenceDefaultValueInitializer.isSetAutoExportVariables()) {
            return Collections.emptyList();
        }
        MethodCallExpressionWrapper methodCallExpressionWrapper = (MethodCallExpressionWrapper) argumentListExpressionWrapper.getExpression(0);
        ConstantExpressionWrapper constantExpressionWrapper = (ConstantExpressionWrapper) AstEntityInputUtil.getCallTestCaseParam(methodCallExpressionWrapper);
        String calledTestCaseId = String.valueOf(constantExpressionWrapper.getValue());
        MapExpressionWrapper mapExpressionWrapper = (MapExpressionWrapper) argumentListExpressionWrapper.getExpression(1);
        List<VariableEntity> variableEntities = new ArrayList<VariableEntity>();
        for (MapEntryExpressionWrapper entryExpressionWrapper : mapExpressionWrapper.getMapEntryExpressions()) {
            String variableName = (entryExpressionWrapper.getKeyExpression() instanceof ConstantExpressionWrapper)
                    ? String.valueOf(((ConstantExpressionWrapper) entryExpressionWrapper.getKeyExpression()).getValue())
                    : entryExpressionWrapper.getKeyExpression().getText();

            VariableEntity variableInCalledTestCase = null;
            try {
                variableInCalledTestCase = TestCaseController.getInstance().getVariable(calledTestCaseId, variableName);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
            if (variableInCalledTestCase == null) {
                continue;
            }

            VariableEntity newVariable = new VariableEntity();
            newVariable.setName(variableName);
            newVariable.setDefaultValue(variableInCalledTestCase.getDefaultValue());
            variableEntities.add(newVariable);
        }

        return variableEntities;
    }

    private void addNewThrowStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new ThrowStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_THROW_STATEMENT);
        }
    }

    public void addNewMethod(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            MethodObjectBuilderDialog dialog = new MethodObjectBuilderDialog(Display.getCurrent().getActiveShell(),
                    null, null);
            int result = dialog.open();
            if (result != Window.OK || dialog.getReturnValue() == null) {
                return;
            }
            MethodNodeWrapper method = dialog.getReturnValue();
            int methodIndex = -1;
            AstTreeTableNode selectedNode = getSelectedNode();
            if (selectedNode instanceof AstMethodTreeTableNode) {
                methodIndex = mainClassNodeWrapper.getMethods().indexOf(selectedNode.getASTObject());
            }
            if (methodIndex == -1) {
                addMethod(method, -1);
            } else {
                switch (addType) {
                    case Add:
                    case InserAfter:
                        addMethod(method, methodIndex + 1);
                        break;
                    case InserBefore:
                        addMethod(method, methodIndex);
                        break;
                }
            }
            setDirty(true);
            refresh();
            setSelection(mainClassTreeNode, method);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_ADD_METHOD);
        }
    }

    public void addNewBuiltInKeyword(int id, AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            String className = TreeTableMenuItemConstants.getContributingClassName(id);
            KeywordClass keywordClass = KeywordController.getInstance().getBuiltInKeywordClassByName(className);
            addImport(keywordClass.getType());
            addImport(ObjectRepository.class);
            addImport(TestCaseFactory.class);
            addImport(FailureHandling.class);
            String defaultSettingKeywordName = TestCasePreferenceDefaultValueInitializer.getDefaultKeywords().get(
                    className);
            ASTNodeWrapper astNode = null;
            if (!StringUtils.isBlank(defaultSettingKeywordName)
                    && KeywordController.getInstance().getBuiltInKeywordByName(className, defaultSettingKeywordName) != null) {
                astNode = AstTreeTableInputUtil.createBuiltInKeywordMethodCall(keywordClass.getSimpleName(),
                        defaultSettingKeywordName, null);
            } else {
                astNode = AstTreeTableInputUtil.createBuiltInKeywordMethodCall(keywordClass.getSimpleName(),
                        KeywordController.getInstance().getBuiltInKeywords(keywordClass.getName()).get(0).getName(),
                        null);
            }
            addNewAstObject(astNode, destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_ADD_KEYWORD);
        }
    }

    public void addNewCustomKeyword(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            StatementWrapper statemt = AstTreeTableInputUtil.getNewCustomKeyword(null);
            if (statemt == null) {
                MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                        StringConstants.PA_ERROR_MSG_NO_CUSTOM_KEYWORD);
                return;
            }
            addImport(ObjectRepository.class);
            addImport(TestCaseFactory.class);
            addImport(FailureHandling.class);
            addNewAstObject(statemt, destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_ADD_KEYWORD);
        }
    }

    public void addNewIfStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new IfStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_IF_STATEMENT);
        }
    }

    public void addNewElseStatement(AstTreeTableNode destinationNode) {
        if (destinationNode == null
                || !(destinationNode instanceof AstIfStatementTreeTableNode || destinationNode instanceof AstElseIfStatementTreeTableNode)) {
            return;
        }
        try {
            AstTreeTableNode parentNode = null;
            BlockStatementWrapper newElseStatement = null;
            if (destinationNode instanceof AstIfStatementTreeTableNode
                    && ((AstIfStatementTreeTableNode) destinationNode).getASTObject() != null) {
                IfStatementWrapper ifStatement = (IfStatementWrapper) destinationNode.getASTObject();
                if (ifStatement.getElseStatement() != null) {
                    return;
                }
                newElseStatement = new BlockStatementWrapper(ifStatement);
                ifStatement.setElseStatement(newElseStatement);
                parentNode = destinationNode.getParent();
            } else if (destinationNode instanceof AstElseIfStatementTreeTableNode
                    && ((AstElseIfStatementTreeTableNode) destinationNode).getASTObject() != null) {
                ElseIfStatementWrapper elseIfStatement = (ElseIfStatementWrapper) destinationNode.getASTObject();
                if (elseIfStatement.getParent().getElseStatement() != null) {
                    return;
                }
                newElseStatement = new BlockStatementWrapper(elseIfStatement.getParent());
                elseIfStatement.getParent().setElseStatement(newElseStatement);
                parentNode = destinationNode.getParent();
            }
            if (parentNode == null || newElseStatement == null) {
                return;
            }
            refresh(parentNode);
            setSelection(parentNode, newElseStatement);
            setEdit(parentNode, newElseStatement);
            setDirty(true);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_ELSE_STATEMENT);
        }
    }

    public void addNewElseIfStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        if (destinationNode == null
                || !(destinationNode instanceof AstIfStatementTreeTableNode || destinationNode instanceof AstElseIfStatementTreeTableNode)) {
            return;
        }
        try {
            IfStatementWrapper parentIfStatement = null;
            int newIndex = -1;
            if (destinationNode instanceof AstIfStatementTreeTableNode) {
                parentIfStatement = ((AstIfStatementTreeTableNode) destinationNode).getASTObject();
            } else if (destinationNode instanceof AstElseIfStatementTreeTableNode) {
                ElseIfStatementWrapper selectedElseIfStatmenet = ((AstElseIfStatementTreeTableNode) destinationNode).getASTObject();
                parentIfStatement = selectedElseIfStatmenet.getParent();
                List<ElseIfStatementWrapper> elseIfStatementList = parentIfStatement.getElseIfStatements();
                int selectElseIfStatementIndex = elseIfStatementList.indexOf(selectedElseIfStatmenet);
                if (addType == NodeAddType.InserAfter && selectElseIfStatementIndex < elseIfStatementList.size()) {
                    newIndex = selectElseIfStatementIndex + 1;
                }
                if (addType == NodeAddType.InserBefore && selectElseIfStatementIndex >= 0) {
                    newIndex = selectElseIfStatementIndex;
                }
            }

            ElseIfStatementWrapper newElseIfStatement = new ElseIfStatementWrapper(parentIfStatement);
            if (newIndex == -1) {
                parentIfStatement.addElseIfStatement(newElseIfStatement);
            } else {
                parentIfStatement.addElseIfStatement(newElseIfStatement, newIndex);
            }
            refresh(destinationNode.getParent());
            setDirty(true);
            setSelection(destinationNode.getParent(), newElseIfStatement);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_ELSE_IF_STATEMENT);
        }
    }

    public void addNewWhileStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new WhileStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_WHILE_STATEMENT);
        }
    }

    public void addNewForStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new ForStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_FOR_STATEMENT);
        }
    }

    public void addNewBinaryStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new ExpressionStatementWrapper(new BinaryExpressionWrapper(null), null), destinationNode,
                    addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_BINARY_STATEMENT);
        }
    }

    public void addNewAssertStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new AssertStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_ASSERT_STATEMENT);
        }
    }

    public void addNewMethodCall(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new ExpressionStatementWrapper(new MethodCallExpressionWrapper(null), null),
                    destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_ASSERT_STATEMENT);
        }
    }

    public void addNewBreakStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new BreakStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_BREAK_STATEMENT);
        }
    }

    public void addNewContinueStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new ContinueStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_CONTINUE_STATEMENT);
        }
    }

    public void addNewReturnStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new ReturnStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_RETURN_STATEMENT);
        }
    }

    public void addNewSwitchStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new SwitchStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_SWITCH_STATEMENT);
        }
    }

    public void addNewCaseStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        if (destinationNode == null
                || !(destinationNode instanceof AstSwitchStatementTreeTableNode || destinationNode instanceof AstCaseStatementTreeTableNode)) {
            return;
        }
        try {
            if (destinationNode instanceof AstCaseStatementTreeTableNode && addType == NodeAddType.Add) {
                addType = NodeAddType.InserAfter;
            }
            addNewAstObject(new CaseStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_CASE_STATEMENT);
        }
    }

    public void addNewDefaultStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        if (destinationNode == null
                || !(destinationNode instanceof AstSwitchStatementTreeTableNode || destinationNode instanceof AstCaseStatementTreeTableNode)) {
            return;
        }
        try {
            SwitchStatementWrapper swichStatementWrapper = null;
            AstTreeTableNode parentNode = null;
            if (destinationNode instanceof AstSwitchStatementTreeTableNode && destinationNode.getASTObject() != null) {
                swichStatementWrapper = (SwitchStatementWrapper) destinationNode.getASTObject();
                parentNode = destinationNode;
            } else if (destinationNode instanceof AstCaseStatementTreeTableNode
                    && destinationNode.getASTObject() != null) {
                swichStatementWrapper = (SwitchStatementWrapper) destinationNode.getASTObject().getParent();
                parentNode = destinationNode.getParent();
            }
            if (swichStatementWrapper == null || parentNode == null
                    || swichStatementWrapper.getDefaultStatement() != null) {
                return;
            }
            BlockStatementWrapper newDefaultStatementWrapper = new BlockStatementWrapper(swichStatementWrapper);
            newDefaultStatementWrapper.addStatement(new BreakStatementWrapper(newDefaultStatementWrapper));
            swichStatementWrapper.setDefaultStatement(newDefaultStatementWrapper);
            refresh(parentNode);
            setSelection(parentNode, newDefaultStatementWrapper);
            setEdit(parentNode, newDefaultStatementWrapper);
            setDirty(true);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_DEFAULT_STATEMENT);
        }
    }

    public void addNewTryStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new TryCatchStatementWrapper(null), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_TRY_STATEMENT);
        }
    }

    public void addNewCatchStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        if (destinationNode == null
                || !(destinationNode instanceof AstTryStatementTreeTableNode || destinationNode instanceof AstCatchStatementTreeTableNode)) {
            return;
        }
        try {
            TryCatchStatementWrapper parentTryCatchStatement = null;
            int newIndex = -1;
            if (destinationNode instanceof AstTryStatementTreeTableNode) {
                parentTryCatchStatement = ((AstTryStatementTreeTableNode) destinationNode).getASTObject();
            } else if (destinationNode instanceof AstCatchStatementTreeTableNode) {
                CatchStatementWrapper selectedCatchStatement = ((AstCatchStatementTreeTableNode) destinationNode).getASTObject();
                parentTryCatchStatement = selectedCatchStatement.getParent();
                List<CatchStatementWrapper> catchStatementList = parentTryCatchStatement.getCatchStatements();
                int selectElseIfStatementIndex = catchStatementList.indexOf(selectedCatchStatement);
                if (addType == NodeAddType.InserAfter && selectElseIfStatementIndex < catchStatementList.size()) {
                    newIndex = selectElseIfStatementIndex + 1;
                }
                if (addType == NodeAddType.InserBefore && selectElseIfStatementIndex >= 0) {
                    newIndex = selectElseIfStatementIndex;
                }
            }

            CatchStatementWrapper newCatchStatement = new CatchStatementWrapper(parentTryCatchStatement);
            if (newIndex == -1) {
                parentTryCatchStatement.addCatchStatement(newCatchStatement);
            } else {
                parentTryCatchStatement.addCatchStatement(newCatchStatement, newIndex);
            }
            refresh(destinationNode.getParent());
            setDirty(true);
            setSelection(destinationNode.getParent(), newCatchStatement);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_CATCH_STATEMENT);
        }
    }

    public void addNewFinallyStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        if (destinationNode == null
                || !(destinationNode instanceof AstTryStatementTreeTableNode || destinationNode instanceof AstCatchStatementTreeTableNode)) {
            return;
        }
        try {
            TryCatchStatementWrapper tryStatementWrapper = null;
            AstTreeTableNode parentNode = null;
            if (destinationNode instanceof AstTryStatementTreeTableNode && destinationNode.getASTObject() != null) {
                tryStatementWrapper = (TryCatchStatementWrapper) destinationNode.getASTObject();
                parentNode = destinationNode.getParent();
            } else if (destinationNode instanceof AstCatchStatementTreeTableNode
                    && destinationNode.getASTObject() != null) {
                tryStatementWrapper = (TryCatchStatementWrapper) destinationNode.getASTObject().getParent();
                parentNode = destinationNode.getParent();
            }
            if (tryStatementWrapper == null || tryStatementWrapper.getFinallyStatement() != null) {
                return;
            }
            BlockStatementWrapper newFinallyStatementWrapper = new BlockStatementWrapper(tryStatementWrapper);
            tryStatementWrapper.setFinallyStatement(newFinallyStatementWrapper);
            refresh(parentNode);
            setSelection(parentNode, newFinallyStatementWrapper);
            setEdit(parentNode, newFinallyStatementWrapper);
            setDirty(true);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_FINALLY_STATEMENT);
        }
    }
}
