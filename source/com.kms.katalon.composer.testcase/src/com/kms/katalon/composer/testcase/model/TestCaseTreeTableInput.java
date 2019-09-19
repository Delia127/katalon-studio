package com.kms.katalon.composer.testcase.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.operation.AbstractCompositeOperation;
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
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.exceptions.GroovyParsingException;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.FieldNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ImportNodeWrapper;
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
import com.kms.katalon.composer.testcase.groovy.ast.statements.EmptyStatementWrapper;
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
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.testcase.providers.AstTestScriptGeneratorProvider;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransfer;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransferData;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.execution.setting.TestCaseSettingStore;
import com.kms.katalon.tracking.service.Trackings;

public class TestCaseTreeTableInput {
    private static final String OPERATION_LABEL_DRAG_AND_DROP_AST_NODES = "dragAndDropAstNodes";

    private static final String GROOVY_NEW_LINE_CHARACTER = "\n";

    private static final String WEB_SERVICE_KEYWORDS_CLASS_ALIAS_NAME = "WS";

    /**
     * Enum for adding items into tree table
     * <p>
     * <b>Add</b> - Add new object into the selected node's children list if the
     * selected node has children; otherwise insert new object after the
     * selected node
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

    private ITestCasePart parentPart;

    private boolean isChanged;

    private IUndoContext undoContext;

    private IOperationHistory operationHistory;

    public ScriptNodeWrapper getMainClassNode() {
        return mainClassNodeWrapper;
    }

    public TestCaseTreeTableInput(ScriptNodeWrapper scriptNode, TreeViewer treeTableViewer, ITestCasePart parentPart) {
        this.treeTableViewer = treeTableViewer;
        this.mainClassNodeWrapper = scriptNode;
        this.parentPart = parentPart;
        this.undoContext = new ObjectUndoContext(parentPart);
        setChanged(false);
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

    public List<ASTNodeWrapper> getSelectedNodeWrappers() {
        List<ASTNodeWrapper> selectedNodeWrappers = new ArrayList<>();
        for (AstTreeTableNode node : getSelectedNodes()) {
            selectedNodeWrappers.add(node.getASTObject());
        }
        return selectedNodeWrappers;
    }

    private int[] getIndex(AstTreeTableNode node) {
        AstTreeTableNode parent = node.getParent();
        int index = parent.getChildren().indexOf(node);
        if (parent.getParent() == null) {
            return new int[] { index };
        }
        return ArrayUtils.add(getIndex(node.getParent()), index);
    }

    public List<ASTNodeWrapper> getNodeWrappersFromFirstSelected() {
        int[] selectedIndices = getIndex(getSelectedNode());
        int fisrtSelected = selectedIndices[0];
        return mainClassNodeWrapper.getBlock()
                .getAstChildren()
                .stream()
                .skip(fisrtSelected)
                .collect(Collectors.toList());
    }

    /**
     * Add new ast object into tree table
     * 
     * @param astObject
     * ast object
     * @param destinationNode
     * destination node to add or null if add to end of script
     * @param addType
     * see {@link NodeAddType}
     */
    public boolean addNewAstObject(ASTNodeWrapper astObject, AstTreeTableNode destinationNode, NodeAddType addType) {
        List<ASTNodeWrapper> astObjects = new ArrayList<ASTNodeWrapper>();
        astObjects.add(astObject);
        return addNewAstObjects(astObjects, destinationNode, addType);
    }

    /**
     * Add new ast objects into tree table
     * 
     * @param astObjects
     * list of ast objects
     * @param destinationNode
     * destination node to add or null if adding to end of script
     * @param addType
     * see {@link NodeAddType}
     */
    public boolean addNewAstObjects(List<? extends ASTNodeWrapper> astObjects, AstTreeTableNode destinationNode,
            NodeAddType addType) {
        IStatus status = executeOperation(new AddAstObjectsOperation(astObjects, destinationNode, addType));
        return status == Status.OK_STATUS;
    }

    private boolean internalAddNewAstObjects(List<? extends ASTNodeWrapper> astObjects,
            AstTreeTableNode destinationNode, NodeAddType addType, boolean setEdit) {
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
            processAfterEdits(astNodeAddedEdits, setEdit, true);
            return true;
        }
        return false;
    }

    private void processAfterEdits(List<AstNodeAddedEdit> astNodeAddedEdits, boolean setEdit, boolean multiSelect) {
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
        ASTNodeWrapper newAstObject = lastEdit.getNewAstObject();
        if (newAstObject == null) {
            return;
        }
        if (!multiSelect) {
            setSelection(lastEdit.getParentNode(), newAstObject);
        } else {
            List<AstTreeTableNode> selectedTreeTableNodes = new ArrayList<>();
            for (AstNodeAddedEdit astNodeAddedEdit : astNodeAddedEdits) {
                selectedTreeTableNodes.add(getTreeTableNodeOfAstObjectFromParentNode(astNodeAddedEdit.getParentNode(),
                        astNodeAddedEdit.getNewAstObject()));
            }
            setSelection(selectedTreeTableNodes);
        }
        if (setEdit) {
            setEdit(lastEdit.getParentNode(), newAstObject);
        }
    }

    private List<AstNodeAddedEdit> insertASTObjects(List<? extends ASTNodeWrapper> astObjects,
            AstTreeTableNode selectedTreeTableNode, NodeAddType addType) {
        if (astObjects == null || astObjects.isEmpty() || selectedTreeTableNode == null) {
            return Collections.emptyList();
        }
        List<AstNodeAddedEdit> astNodeAddedEdits = new ArrayList<AstNodeAddedEdit>();
        if (addType == NodeAddType.InserAfter) {
            astObjects = new ArrayList<>(astObjects);
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

    private List<AstNodeAddedEdit> addAstObjects(List<? extends ASTNodeWrapper> astObjects,
            AstTreeTableNode parentNode) {
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
     * @return the tree table node of the ast object from a parent node, or null
     * if not found
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

    private void setSelection(List<AstTreeTableNode> treeTableNodes) {
        if (treeTableNodes == null || treeTableNodes.isEmpty()) {
            return;
        }
        treeTableViewer.setSelection(new StructuredSelection(treeTableNodes));
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

    // Check for avoiding NullPointerException in
    // TreeViewerFocusCellManager.getInitialFocusCell()
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
            try {
                reloadTreeTableNodes();
            } catch (InvocationTargetException | InterruptedException e) {
                LoggerSingleton.logError(e);
            }
            return;
        }
        if (object instanceof AstScriptTreeTableNode) {
            treeTableViewer.refresh();
            return;
        }
        treeTableViewer.refresh(object);
        if (!(object instanceof AstTreeTableNode)) {
            return;
        }
        AstTreeTableNode treeTableNode = (AstTreeTableNode) object;
        if (treeTableNode.canHaveChildren()) {
            treeTableNode.reloadChildren();
        }
    }

    // refresh treetable root
    public void reloadTreeTableNodes() throws InvocationTargetException, InterruptedException {
        final List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        mainClassTreeNode = new AstScriptTreeTableNode(mainClassNodeWrapper, null);
        astTreeTableNodes.add(mainClassTreeNode);
        reloadTestCaseVariables(parentPart.getVariables());
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

    public void reloadTestCaseVariables(VariableEntity[] variables) {
        mainClassNodeWrapper.clearFields();
        for (VariableEntity variable : variables) {
            FieldNodeWrapper field = new FieldNodeWrapper(variable.getName(), Object.class, mainClassNodeWrapper);
            ExpressionWrapper expression = GroovyWrapperParser
                    .parseGroovyScriptAndGetFirstExpression(variable.getDefaultValue());
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

    public void clearRows() {
        mainClassTreeNode.getChildren().clear();
    }

    public void removeRows(List<AstTreeTableNode> treeTableNodes) {
        executeOperation(new RemoveAstTreeTableNodesOperation(treeTableNodes));
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
                if (otherAstTreeTableNode != null && (astTreeTableNode.equals(otherAstTreeTableNode)
                        || otherAstTreeTableNode.isDescendantNode(astTreeTableNode))) {
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
        if (treeTableViewer == null) {
            return null;
        }
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
        executeOperation(new MoveNodeOperation(selectedNode, offset));
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
        executeOperation(new ChangeFailureHandlingOperation(failureHandling, treeTableNodes));
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
        ScriptTransferData transferData = new ScriptTransferData(scriptSnippets.toString(),
                parentPart.getTestCase().getId());
        cb.setContents(new Object[] { new ScriptTransferData[] { transferData } },
                new Transfer[] { new ScriptTransfer() });
        return rowsToBeRemoved;
    }

    private String getTestCaseId() {
        TestCaseEntity testCase = parentPart.getTestCase();
        if (testCase != null) {
            return testCase.getId();
        }
        return mainClassNodeWrapper.getTestCaseId();
    }

    public static boolean isNodeMoveable(AstTreeTableNode astTreeTableNode) {
        return (!(astTreeTableNode.getASTObject() instanceof ComplexLastStatementWrapper)
                && !(astTreeTableNode.getASTObject() instanceof ComplexChildStatementWrapper));
    }

    public boolean canPaste() {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        Object data = clipboard.getContents(new ScriptTransfer());
        if (data == null) {
            return false;
        }
        String snippet = null;
        if (data instanceof String) {
            snippet = (String) data;
        } else if (data instanceof ScriptTransferData[]) {
            snippet = ((ScriptTransferData[]) data)[0].getScriptSnippet();
        }
        try {
            ScriptNodeWrapper scriptNode = GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(snippet);
            return scriptNode != null;
        } catch (Exception e) {
            return false;
        }
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
        executeOperation(new ToogleDisableStepsOperation(treeTableNodes, isDisableMode));
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
        addDefaultImports();
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
                if (parentPart instanceof TestCasePart) {
                    addNewMethod(destinationNode, addType);
                } else {
                    MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                            ComposerTestcaseMessageConstants.DIA_WARNING_METHOD_IN_CLOSURE);
                }
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

        if (StringUtils.equals(getTestCaseId(), calledTestCase.getId())
                || StringUtils.equals(getTestCaseId(), calledTestCase.getRelativePathForUI())) {
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

    public void addCallTestCases(AstTreeTableNode destinationNode, NodeAddType addType,
            TestCaseEntity[] testCaseArray) {
        if (testCaseArray == null || testCaseArray.length <= 0) {
            return;
        }
        List<StatementWrapper> statementsToAdd = new ArrayList<StatementWrapper>();
        List<VariableEntity> variablesToAdd = new ArrayList<VariableEntity>();
        ASTNodeWrapper parentNodeWrapper = getParentNodeForNewMethodCall(destinationNode);
        for (TestCaseEntity testCase : testCaseArray) {
            statementsToAdd.add(AstEntityInputUtil.generateCallTestCaseExpresionStatement(testCase, variablesToAdd,
                    parentNodeWrapper));
        }

        executeOperation(new AddCallTestCaseStepsOperation(statementsToAdd, destinationNode, addType, variablesToAdd));
        Trackings.trackAddNewTestStep("callTestCase");
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
                for (TestCaseEntity testCase : TestCaseEntityUtil
                        .getTestCasesFromFolderTree((FolderTreeEntity) treeEntity)) {
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
        executeOperation(new AddMethodOperation(addType, destinationNode));
    }

    public void addNewDefaultBuiltInKeyword(NodeAddType addType) {
        addNewAstObject(
                TreeTableMenuItemConstants.getMenuItemID(
                        TestCasePreferenceDefaultValueInitializer.getDefaultKeywordType().getAliasName()),
                getSelectedNode(), addType);
    }

    public void addNewDefaultWebServiceKeyword(NodeAddType addType) {
        addNewAstObject(TreeTableMenuItemConstants.getMenuItemID(WEB_SERVICE_KEYWORDS_CLASS_ALIAS_NAME),
                getSelectedNode(), addType);
    }

    private void addNewBuiltInKeyword(AstTreeTableNode destinationNode, NodeAddType addType, String className) {
        addNewBuiltInKeyword(destinationNode, addType,
                KeywordController.getInstance().getBuiltInKeywordClassByName(className));
        Trackings.trackAddNewTestStep(className);
    }

    private void addNewBuiltInKeyword(AstTreeTableNode destinationNode, NodeAddType addType,
            KeywordClass keywordClass) {
        if (keywordClass == null) {
            return;
        }
        String defaultSettingKeywordName = TestCasePreferenceDefaultValueInitializer.getDefaultKeywords()
                .get(keywordClass.getName());
        StatementWrapper newBuiltinKeywordStatement = null;

        ASTNodeWrapper parentNodeWrapper = getParentNodeForNewMethodCall(destinationNode);
        if (StringUtils.isNotBlank(defaultSettingKeywordName) && (KeywordController.getInstance()
                .getBuiltInKeywordByName(keywordClass.getName(), defaultSettingKeywordName, null)) != null) {
            MethodCallExpressionWrapper keywordMethodCallExpression = new MethodCallExpressionWrapper(
                    keywordClass.getAliasName(), defaultSettingKeywordName, parentNodeWrapper);

            AstKeywordsInputUtil.generateMethodCallArguments(keywordMethodCallExpression, KeywordController
                    .getInstance().getBuiltInKeywordByName(keywordClass.getName(), defaultSettingKeywordName, null));

            newBuiltinKeywordStatement = new ExpressionStatementWrapper(keywordMethodCallExpression, null);

        } else {
            newBuiltinKeywordStatement = AstKeywordsInputUtil
                    .createBuiltInKeywordStatement(keywordClass.getAliasName(),
                            KeywordController.getInstance()
                                    .getBuiltInKeywords(keywordClass.getAliasName(), true)
                                    .get(0)
                                    .getName(),
                            parentNodeWrapper);
        }
        addNewAstObject(newBuiltinKeywordStatement, destinationNode, addType);
    }

    public void addDefaultImports() {
        if (isChanged) {
            return;
        }
        mainClassNodeWrapper.addDefaultImports();
    }

    public void addImports(List<ImportNodeWrapper> imports) {
        mainClassNodeWrapper.addImportNodes(imports);
    }

    public void addNewCustomKeyword(AstTreeTableNode destinationNode, NodeAddType addType) {
        ASTNodeWrapper parentNodeWrapper = getParentNodeForNewMethodCall(destinationNode);
        StatementWrapper customKeywordStatement = AstKeywordsInputUtil
                .createNewCustomKeywordStatement(parentNodeWrapper);
        if (customKeywordStatement == null) {
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE, StringConstants.PA_ERROR_MSG_NO_CUSTOM_KEYWORD);
            return;
        }
        addNewAstObject(customKeywordStatement, destinationNode, addType);
        Trackings.trackAddNewTestStep("custom");
    }

    public ASTNodeWrapper getParentNodeForNewMethodCall(AstTreeTableNode destinationNode) {
        return destinationNode != null ? destinationNode.getASTObject() : mainClassNodeWrapper;
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
        ASTNodeWrapper parentNodeWrapper = getParentNodeForNewMethodCall(destinationNode);
        addNewAstObject(new ExpressionStatementWrapper(new MethodCallExpressionWrapper(parentNodeWrapper)),
                destinationNode, addType);
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

    public void dragAndDropAstObjects(List<AstTreeTableNode> draggedNodes, List<StatementWrapper> droppedNodes,
            AstTreeTableNode destinationNode, NodeAddType addType) {
        AbstractCompositeOperation dragAndDropOperation = new AbstractCompositeOperation(
                OPERATION_LABEL_DRAG_AND_DROP_AST_NODES);
        dragAndDropOperation.add(new AddAstObjectsOperation(droppedNodes, destinationNode, addType));
        dragAndDropOperation.add(new RemoveAstTreeTableNodesOperation(draggedNodes));
        executeOperation(dragAndDropOperation);
    }

    /**
     * @return raw script that disabled all the steps before the currently
     * selected step
     */
    public String generateRawScriptFromSelectedStep() {
        AstTreeTableNode selectedNode = getSelectedNode();
        if (selectedNode == null || !(selectedNode.getASTObject() instanceof StatementWrapper)) {
            return null;
        }
        return AstTestScriptGeneratorProvider.generateScriptForExecuteFromTestStep(mainClassNodeWrapper,
                (StatementWrapper) selectedNode.getASTObject());
    }

    private IOperationHistory getOperationHistory() {
        if (operationHistory == null) {
            operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
        }
        return operationHistory;
    }

    public IStatus executeOperation(IUndoableOperation operation, IProgressMonitor progressMonitor,
            IAdaptable adaptable) {
        IOperationHistory operationHistory = getOperationHistory();
        try {
            operation.addContext(undoContext);
            return operationHistory.execute(operation, progressMonitor, adaptable);
        } catch (ExecutionException e) {
            LoggerSingleton.logError(e);
        }
        return Status.CANCEL_STATUS;
    }

    public IStatus executeOperation(IUndoableOperation operation, IAdaptable adaptable) {
        return executeOperation(operation, new NullProgressMonitor(), adaptable);
    }

    public IStatus executeOperation(IUndoableOperation operation) {
        return executeOperation(operation, null);
    }

    private class AddAstObjectsOperation extends AbstractOperation {
        private List<? extends ASTNodeWrapper> astObjects;

        private AstTreeTableNode destinationNode;

        private NodeAddType addType;

        public AddAstObjectsOperation(List<? extends ASTNodeWrapper> astObjects, AstTreeTableNode destinationNode,
                NodeAddType addType) {
            super(AddAstObjectsOperation.class.getName());
            this.astObjects = astObjects;
            this.destinationNode = destinationNode;
            this.addType = addType;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (astObjects == null) {
                return Status.CANCEL_STATUS;
            }
            return doAddAstObjects(true);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doAddAstObjects(false);
        }

        protected IStatus doAddAstObjects(boolean doSetEdit) {
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            boolean addSuccessfully = internalAddNewAstObjects(astObjects, destinationNode, addType, doSetEdit);
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
            if (addSuccessfully) {
                // Scroll down and show the new added item
                treeTableViewer.getTree().showSelection();
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            boolean removeSuccessfully = internalRemoveAstObjects(astObjects, destinationNode, addType);
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
            if (removeSuccessfully) {
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }

        private boolean internalRemoveAstObjects(List<? extends ASTNodeWrapper> astObjects,
                AstTreeTableNode destinationNode, NodeAddType addType) {
            List<AstTreeTableNode> refreshNodeList = new ArrayList<AstTreeTableNode>();
            if (destinationNode == null) {
                refreshNodeList.addAll(removeChildObjectFromParentNode(astObjects, mainClassTreeNode));
            } else if (addType == NodeAddType.Add) {
                if (destinationNode.canHaveChildren()) {
                    refreshNodeList.addAll(removeChildObjectFromParentNode(astObjects, destinationNode));
                } else {
                    refreshNodeList.addAll(removeChildObjectFromParentNode(astObjects, destinationNode.getParent()));
                }
            } else {
                refreshNodeList.addAll(removeChildObjectFromParentNode(astObjects, destinationNode.getParent()));
            }
            if (!refreshNodeList.isEmpty()) {
                processAfterRemove(refreshNodeList);
                return true;
            }
            return false;
        }

        protected List<AstTreeTableNode> removeChildObjectFromParentNode(List<? extends ASTNodeWrapper> astObjects,
                AstTreeTableNode parentNode) {
            if (parentNode == null || astObjects == null || astObjects.isEmpty()) {
                return Collections.emptyList();
            }
            List<AstTreeTableNode> refreshNodeList = new ArrayList<AstTreeTableNode>();
            for (ASTNodeWrapper childObject : astObjects) {
                parentNode.removeChild(childObject);
                refreshNodeList.add(parentNode);
            }
            return refreshNodeList;
        }
    }

    private class AddCallTestCaseStepsOperation extends AddAstObjectsOperation {
        private List<VariableEntity> variableList;

        public AddCallTestCaseStepsOperation(List<? extends ASTNodeWrapper> astObjects,
                AstTreeTableNode destinationNode, NodeAddType addType, List<VariableEntity> variableList) {
            super(astObjects, destinationNode, addType);
            this.variableList = variableList;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            IStatus status = super.execute(monitor, info);
            if (status == Status.CANCEL_STATUS || variableList.isEmpty()) {
                return status;
            }
            doAddVariables();
            return status;
        }

        protected void doAddVariables() {
            parentPart.addVariables(variableList.toArray(new VariableEntity[variableList.size()]));
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            IStatus status = super.redo(monitor, info);
            doAddVariables();
            return status;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            IStatus status = super.undo(monitor, info);
            parentPart.deleteVariables(variableList);
            return status;
        }

    }

    private class RemoveAstTreeTableNodesOperation extends AbstractOperation {
        public class AstNodeRemovedEdit {
            private AstTreeTableNode parentNode;

            private ASTNodeWrapper parentAstObject;

            private ASTNodeWrapper astObject;

            private int originalIndex;

            public AstNodeRemovedEdit(AstTreeTableNode parentNode, ASTNodeWrapper parentAstObject,
                    ASTNodeWrapper astObject, int originalIndex) {
                this.parentNode = parentNode;
                this.astObject = astObject;
                this.originalIndex = originalIndex;
                this.parentAstObject = parentAstObject;
            }

            public AstTreeTableNode getParentNode() {
                return parentNode;
            }

            public ASTNodeWrapper getAstObject() {
                return astObject;
            }

            protected int getOriginalIndex() {
                return originalIndex;
            }

            protected ASTNodeWrapper getParentAstObject() {
                return parentAstObject;
            }
        }

        private List<AstTreeTableNode> treeTableNodes;

        private List<AstNodeRemovedEdit> removedAstNodeEdits;

        public RemoveAstTreeTableNodesOperation(List<AstTreeTableNode> treeTableNodes) {
            super(RemoveAstTreeTableNodesOperation.class.getName());
            this.treeTableNodes = treeTableNodes;
            this.removedAstNodeEdits = new ArrayList<AstNodeRemovedEdit>();
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (treeTableNodes == null || treeTableNodes.isEmpty()) {
                return Status.CANCEL_STATUS;
            }
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            removedAstNodeEdits.clear();
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            List<AstTreeTableNode> refreshNodeList = new ArrayList<AstTreeTableNode>();
            for (int i = 0; i < treeTableNodes.size(); i++) {
                AstTreeTableNode treeTableNode = treeTableNodes.get(i);
                ASTNodeWrapper astObject = treeTableNode.getASTObject();
                if (treeTableNode == null || astObject == null) {
                    continue;
                }
                ASTNodeWrapper parentAstObject = astObject.getParent();
                if (parentAstObject == null) {
                    continue;
                }
                int childIndex = parentAstObject.indexOf(astObject);
                if (childIndex < 0) {
                    continue;
                }
                if (parentAstObject.removeChild(astObject) && treeTableNode.getParent() != null) {
                    refreshNodeList.add(treeTableNode.getParent());
                    removedAstNodeEdits.add(
                            new AstNodeRemovedEdit(treeTableNode.getParent(), parentAstObject, astObject, childIndex));
                }
            }
            processAfterRemove(refreshNodeList);
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            List<AstNodeAddedEdit> refreshNodeList = new ArrayList<AstNodeAddedEdit>();
            Collections.reverse(removedAstNodeEdits);
            for (AstNodeRemovedEdit astNodeRemovedEdit : removedAstNodeEdits) {
                ASTNodeWrapper astObject = astNodeRemovedEdit.getAstObject();
                astNodeRemovedEdit.getParentAstObject().addChild(astObject, astNodeRemovedEdit.getOriginalIndex());
                refreshNodeList.add(new AstNodeAddedEdit(astNodeRemovedEdit.getParentNode(), astObject));
            }
            processAfterEdits(refreshNodeList, false, true);
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
            return Status.OK_STATUS;
        }
    }

    private class MoveNodeOperation extends AbstractOperation {
        private AstTreeTableNode selectedNode;

        private ASTNodeWrapper selectedAstObject;

        private AstTreeTableNode parentNode;

        private int offset;

        public MoveNodeOperation(AstTreeTableNode selectedNode, int offset) {
            super(MoveNodeOperation.class.getName());
            this.selectedNode = selectedNode;
            this.offset = offset;
            this.selectedAstObject = selectedNode.getASTObject();
            this.parentNode = selectedNode.getParent();
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (isUnmoveableAstNode(selectedNode)) {
                return Status.CANCEL_STATUS;
            }
            return doMove(offset);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doMove(offset);
        }

        protected IStatus doMove(int moveOffset) {
            ASTNodeWrapper parentASTNode = selectedAstObject.getParent();
            int currentIndex = parentASTNode.indexOf(selectedAstObject);
            int newIndex = currentIndex + moveOffset;
            if (newIndex < 0) {
                return Status.CANCEL_STATUS;
            }
            ASTNodeWrapper tempNode = new EmptyStatementWrapper(parentASTNode);
            if (selectedAstObject instanceof MethodNodeWrapper) {
                tempNode = new MethodNodeWrapper(parentASTNode);
            }
            if (!(parentASTNode.addChild(tempNode, newIndex) || parentASTNode.addChild(tempNode))) {
                return Status.CANCEL_STATUS;
            }
            parentASTNode.removeChild(selectedAstObject);
            parentASTNode.addChild(selectedAstObject, parentASTNode.indexOf(tempNode));
            parentASTNode.removeChild(tempNode);
            setDirty(true);
            refreshObjectWithoutReloading(parentNode);
            setSelection(parentNode, selectedAstObject);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doMove(1 - offset);
        }
    }

    private class ToogleDisableStepsOperation extends AbstractOperation {
        private List<AstTreeTableNode> treeTableNodes;

        private boolean isDisableMode;

        private List<AstStatementTreeTableNode> changedNodes = new ArrayList<>();

        public ToogleDisableStepsOperation(List<AstTreeTableNode> treeTableNodes, boolean isDisableMode) {
            super(ToogleDisableStepsOperation.class.getName());
            this.treeTableNodes = treeTableNodes;
            this.isDisableMode = isDisableMode;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (treeTableNodes == null || treeTableNodes.isEmpty()) {
                return Status.CANCEL_STATUS;
            }
            changedNodes.clear();
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            for (AstTreeTableNode treeTableNode : treeTableNodes) {
                if (!(treeTableNode instanceof AstStatementTreeTableNode)
                        || !((AstStatementTreeTableNode) treeTableNode).canBeDisabled()) {
                    continue;
                }
                AstStatementTreeTableNode statementNode = (AstStatementTreeTableNode) treeTableNode;
                if (isDisableMode ? statementNode.disable() : statementNode.enable()) {
                    changedNodes.add(statementNode);
                    treeTableViewer.update(statementNode, null);
                    setDirty(true);
                }
            }
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
            if (changedNodes.isEmpty()) {
                return Status.CANCEL_STATUS;
            }
            treeTableViewer.setSelection(null);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doToogleEnableDisableMode();
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return doToogleEnableDisableMode();
        }

        private IStatus doToogleEnableDisableMode() {
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            for (AstStatementTreeTableNode statementNode : changedNodes) {
                statementNode.toogleEnable();
                treeTableViewer.update(statementNode, null);
                setDirty(true);
            }
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
            treeTableViewer.setSelection(null);
            return Status.OK_STATUS;
        }
    }

    private class ChangeFailureHandlingOperation extends AbstractOperation {
        private class FailureChangeEdit {
            private AstAbstractKeywordTreeTableNode treeTableNode;

            private FailureHandling oldFailureHandling;

            public FailureChangeEdit(AstAbstractKeywordTreeTableNode treeTableNode,
                    FailureHandling oldFailureHandling) {
                super();
                this.treeTableNode = treeTableNode;
                this.oldFailureHandling = oldFailureHandling;
            }

            public AstAbstractKeywordTreeTableNode getTreeTableNode() {
                return treeTableNode;
            }

            public FailureHandling getOldFailureHandling() {
                return oldFailureHandling;
            }
        }

        private FailureHandling failureHandling;

        private List<AstTreeTableNode> treeTableNodes;

        private List<FailureChangeEdit> failureChangeEdits = new ArrayList<>();

        public ChangeFailureHandlingOperation(FailureHandling failureHandling, List<AstTreeTableNode> treeTableNodes) {
            super(ChangeFailureHandlingOperation.class.getName());
            this.failureHandling = failureHandling;
            this.treeTableNodes = treeTableNodes;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (failureHandling == null || treeTableNodes == null || treeTableNodes.isEmpty()) {
                return Status.CANCEL_STATUS;
            }
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            failureChangeEdits.clear();
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            for (int i = treeTableNodes.size() - 1; i >= 0; i--) {
                if (!(treeTableNodes.get(i) instanceof AstAbstractKeywordTreeTableNode)) {
                    continue;
                }
                AstAbstractKeywordTreeTableNode keywordNode = (AstAbstractKeywordTreeTableNode) treeTableNodes.get(i);
                FailureHandling failureHandlingValue = keywordNode.getFailureHandlingValue();
                if (!failureHandling.equals(failureHandlingValue)
                        && keywordNode.setFailureHandlingValue(failureHandling)) {
                    failureChangeEdits.add(new FailureChangeEdit(keywordNode, failureHandlingValue));
                    treeTableViewer.update(keywordNode, null);
                    setDirty(true);
                }
            }
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            for (FailureChangeEdit failureChangeEdit : failureChangeEdits) {
                AstAbstractKeywordTreeTableNode treeTableNode = failureChangeEdit.getTreeTableNode();
                FailureHandling oldFailureHandling = failureChangeEdit.getOldFailureHandling();
                if (oldFailureHandling == null) {
                    oldFailureHandling = getDefaultFailureHandling();
                }
                treeTableNode.setFailureHandlingValue(oldFailureHandling);
                treeTableViewer.update(treeTableNode, null);
                setDirty(true);
            }
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
            return Status.OK_STATUS;
        }

        private FailureHandling getDefaultFailureHandling() {
            return new TestCaseSettingStore(ProjectController.getInstance().getCurrentProject().getFolderLocation())
                    .getDefaultFailureHandling();
        }
    }

    private class AddMethodOperation extends AbstractOperation {
        private NodeAddType addType;

        private AstTreeTableNode destinationNode;

        private MethodNodeWrapper method;

        public AddMethodOperation(NodeAddType addType, AstTreeTableNode destinationNode) {
            super(AddMethodOperation.class.getName());
            this.addType = addType;
            this.destinationNode = destinationNode;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            MethodObjectBuilderDialog dialog = new MethodObjectBuilderDialog(Display.getCurrent().getActiveShell(),
                    null, null);
            if (dialog.open() != Window.OK || dialog.getReturnValue() == null) {
                return Status.CANCEL_STATUS;
            }
            method = dialog.getReturnValue();
            doAddMethod(method);
            return Status.OK_STATUS;

        }

        public void doAddMethod(MethodNodeWrapper method) {
            int selectedMethodIndex = -1;
            if (destinationNode instanceof AstMethodTreeTableNode) {
                selectedMethodIndex = mainClassNodeWrapper
                        .indexOfMethod(((AstMethodTreeTableNode) destinationNode).getASTObject());
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

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doAddMethod(method);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            mainClassNodeWrapper.removeChild(method);
            setDirty(true);
            refresh();
            return Status.OK_STATUS;
        }
    }

}
