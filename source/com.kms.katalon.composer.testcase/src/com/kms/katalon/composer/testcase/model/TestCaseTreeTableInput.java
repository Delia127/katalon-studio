package com.kms.katalon.composer.testcase.model;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
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
import com.kms.katalon.composer.testcase.ast.treetable.AstClassTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseIfStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstFinallyStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstIfStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstScriptMainBlockStatmentTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstSwitchDefaultStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstSwitchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTryStatementTreeTableNode;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransfer;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransferData;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
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
        private ASTNode newAstObject;

        public AstNodeAddedEdit(AstTreeTableNode parentNode, ASTNode newAstObject) {
            this.parentNode = parentNode;
            this.newAstObject = newAstObject;
        }

        public AstTreeTableNode getParentNode() {
            return parentNode;
        }

        public ASTNode getNewAstObject() {
            return newAstObject;
        }
    }

    private List<ASTNode> astNodes;
    private List<AstTreeTableNode> astTreeTableNodes;
    private TreeViewer treeTableViewer;
    private ClassNode mainClassNode;
    private TestCasePart parentPart;
    private boolean isChanged;

    public ClassNode getMainClassNode() {
        return mainClassNode;
    }

    public TestCaseTreeTableInput(List<ASTNode> astNodes, TreeViewer treeTableViewer, TestCasePart parentPart) {
        this.treeTableViewer = treeTableViewer;
        this.astNodes = astNodes;
        this.parentPart = parentPart;
        setChanged(false);
    }

    /**
     * Add import to script
     * 
     * @param importClass
     *            class to add import
     */
    public void addImport(Class<?> importClass) {
        boolean isAlreadyImported = false;
        for (ImportNode importNode : mainClassNode.getModule().getImports()) {
            if (importNode.getClassName().equals(importClass.getName())) {
                isAlreadyImported = true;
            }
        }
        if (!isAlreadyImported) {
            mainClassNode.getModule().addImport(importClass.getSimpleName(), new ClassNode(importClass));
        }
    }

    /**
     * Get the first currently selected tree table node
     * 
     * @return the first currently selected tree table nodes
     */
    public AstTreeTableNode getSelectedNode() {
        if (treeTableViewer.getSelection() instanceof ITreeSelection) {
            ITreeSelection selection = (ITreeSelection) treeTableViewer.getSelection();
            if (selection.getFirstElement() instanceof AstTreeTableNode) {
                return (AstTreeTableNode) selection.getFirstElement();
            }
        }
        return null;
    }

    /**
     * Get currently selected tree table nodes
     * 
     * @return list of currently selected tree table nodes
     */
    public List<AstTreeTableNode> getSelectedNodes() {
        if (treeTableViewer.getSelection() instanceof ITreeSelection) {
            ITreeSelection selection = (ITreeSelection) treeTableViewer.getSelection();
            if (selection.size() > 0) {
                List<AstTreeTableNode> selectedNodes = new ArrayList<AstTreeTableNode>();
                Object[] selectedObjects = selection.toArray();
                for (int i = 0; i < selection.size(); i++) {
                    if (selectedObjects[i] instanceof AstTreeTableNode) {
                        selectedNodes.add((AstTreeTableNode) selectedObjects[i]);
                    }
                }
                return selectedNodes;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Add new ast objects into tree table
     * 
     * @param astObjects
     *            list of ast objects
     * @param destinationNode
     *            destination node to add or null if adding to end of script
     * @param addType
     *            {@link NodeAddType}
     * @throws Exception
     */
    public void addNewAstObjects(List<? extends ASTNode> astObjects, AstTreeTableNode destinationNode,
            NodeAddType addType) throws Exception {
        if (astObjects != null) {
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            List<AstNodeAddedEdit> astNodeAddedEdits = new ArrayList<AstNodeAddedEdit>();
            if (destinationNode != null) {
                if (addType == NodeAddType.Add) {
                    if (destinationNode.hasChildren()) {
                        for (ASTNode astObject : astObjects) {
                            addNewAstObjectToParentNode(astObject, destinationNode, -1, astNodeAddedEdits);
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
    }

    private void addAstObjectsIntoScriptMainBlock(List<? extends ASTNode> astObjects,
            List<AstNodeAddedEdit> astNodeAddedEdits) throws Exception {
        for (AstTreeTableNode astTreeTableNode : astTreeTableNodes) {
            if (astTreeTableNode instanceof AstScriptMainBlockStatmentTreeTableNode) {
                for (ASTNode astObject : astObjects) {
                    astTreeTableNode.addChildObject(astObject, -1);
                }
                astNodeAddedEdits.add(new AstNodeAddedEdit(astTreeTableNode, astObjects.get(astObjects.size() - 1)));
                break;
            }
        }
    }

    private void addASTObjectsBefore(List<? extends ASTNode> astObjects, AstTreeTableNode selectedTreeTableNode,
            List<AstNodeAddedEdit> astNodeAddedEdits) throws Exception {
        if (selectedTreeTableNode != null) {
            for (ASTNode astObject : astObjects) {
                insertAstObject(astObject, selectedTreeTableNode, NodeAddType.InserBefore, astNodeAddedEdits);
            }
        }
    }

    private void addASTObjectsAfter(List<? extends ASTNode> astObjects, AstTreeTableNode selectedTreeTableNode,
            List<AstNodeAddedEdit> astNodeAddedEdits) throws Exception {
        if (selectedTreeTableNode != null) {
            for (int i = astObjects.size() - 1; i >= 0; i--) {
                insertAstObject(astObjects.get(i), selectedTreeTableNode, NodeAddType.InserAfter, astNodeAddedEdits);
            }
        }
    }

    /**
     * Add new ast object into tree table
     * 
     * @param astObject
     *            ast object
     * @param destinationNode
     *            destination node to add or null if add to end of script
     * @param addType
     *            {@link NodeAddType}
     * @throws Exception
     */
    public void addNewAstObject(ASTNode astObject, AstTreeTableNode destinationNode, NodeAddType addType)
            throws Exception {
        List<ASTNode> astObjects = new ArrayList<ASTNode>();
        astObjects.add(astObject);
        addNewAstObjects(astObjects, destinationNode, addType);
    }

    private void insertAstObject(ASTNode astObject, AstTreeTableNode sibblingNode, NodeAddType addType,
            List<AstNodeAddedEdit> astNodeAddedEdits) throws Exception {
        int commentOffset = 0;
        if (sibblingNode instanceof AstStatementTreeTableNode) {
            AstStatementTreeTableNode statementSibblingNode = (AstStatementTreeTableNode) sibblingNode;
            if (statementSibblingNode.getDescription() != null && addType == NodeAddType.InserBefore) {
                commentOffset = -1;
            }
        }
        if (sibblingNode.getParent() != null) {
            int sibblingIndex = getAstObjectIndexFromParentNode(sibblingNode.getParent(), sibblingNode);
            if (sibblingIndex > -1) {
                addNewAstObjectToParentNode(astObject, sibblingNode.getParent(), sibblingIndex
                        + ((addType == NodeAddType.InserAfter) ? 1 : 0) + commentOffset, astNodeAddedEdits);
            }
        }
    }

    /**
     * Add new ast object into a tree table node
     * 
     * @param astObject
     *            ast object
     * @param parentNode
     *            tree table node to add
     * @param index
     *            the new index, or -1 to add at the end of the list
     * @throws Exception
     */
    private void addNewAstObjectToParentNode(ASTNode astObject, AstTreeTableNode parentNode, int index,
            List<AstNodeAddedEdit> astNodeAddedEdits) throws Exception {
        parentNode.addChildObject(astObject, index);
        astNodeAddedEdits.add(new AstNodeAddedEdit(parentNode, astObject));
    }

    public void addElseStatement(AstTreeTableNode selectedNode) throws Exception {
        Statement newElseStatement = null;
        AstTreeTableNode parentNode = null;
        if (selectedNode instanceof AstIfStatementTreeTableNode) {
            AstIfStatementTreeTableNode ifStatementTreeTableNode = (AstIfStatementTreeTableNode) selectedNode;
            if (ifStatementTreeTableNode.getASTObject() instanceof IfStatement) {
                newElseStatement = addElseBlockForIfStatement((IfStatement) ifStatementTreeTableNode.getASTObject());
                parentNode = ifStatementTreeTableNode.getParent();

            }
        } else if (selectedNode instanceof AstElseIfStatementTreeTableNode) {
            AstElseIfStatementTreeTableNode elseIfStatementTreeTableNode = (AstElseIfStatementTreeTableNode) selectedNode;
            if (elseIfStatementTreeTableNode.getASTObject() instanceof IfStatement) {
                newElseStatement = addElseBlockForIfStatement((IfStatement) elseIfStatementTreeTableNode.getASTObject());
                parentNode = elseIfStatementTreeTableNode.getParent();
            }
        }
        if (newElseStatement != null) {
            refresh(parentNode);
            setSelection(parentNode, newElseStatement);
            setEdit(parentNode, newElseStatement);
            setDirty(true);
        }
    }

    private Statement addElseBlockForIfStatement(IfStatement ifStatement) throws Exception {
        if (ifStatement.getElseBlock() == null || ifStatement.getElseBlock() instanceof EmptyStatement) {
            BlockStatement blockStatement = new BlockStatement();
            ifStatement.setElseBlock(blockStatement);
            return blockStatement;
        } else if (ifStatement.getElseBlock() instanceof IfStatement) {
            return addElseBlockForIfStatement((IfStatement) ifStatement.getElseBlock());
        }
        return null;
    }

    public void addElseIfStatement(AstTreeTableNode selectedNode, NodeAddType addType) throws Exception {
        if (selectedNode != null) {
            if (addType == NodeAddType.Add) {
                addElseIfStatement(selectedNode);
            } else if (addType == NodeAddType.InserAfter) {
                insertAfterElseIfStatement(selectedNode);
            } else if (addType == NodeAddType.InserBefore) {
                insertBeforeElseIfStatement(selectedNode);
            }
        }
    }

    private void addElseIfStatement(AstTreeTableNode selectedTreeTableNode) throws Exception {
        Statement newElseIfStatement = null;
        AstTreeTableNode parentNode = null;
        if (selectedTreeTableNode instanceof AstIfStatementTreeTableNode) {
            AstIfStatementTreeTableNode ifStatementTreeTableNode = (AstIfStatementTreeTableNode) selectedTreeTableNode;
            if (ifStatementTreeTableNode.getASTObject() instanceof IfStatement) {
                newElseIfStatement = addElseIfStatementForIfStatement((IfStatement) ifStatementTreeTableNode
                        .getASTObject());
                parentNode = ifStatementTreeTableNode.getParent();
            }
        } else if (selectedTreeTableNode instanceof AstElseIfStatementTreeTableNode) {
            AstElseIfStatementTreeTableNode elseIfStatementTreeTableNode = (AstElseIfStatementTreeTableNode) selectedTreeTableNode;
            if (elseIfStatementTreeTableNode.getASTObject() instanceof IfStatement) {
                newElseIfStatement = addElseIfStatementForIfStatement((IfStatement) elseIfStatementTreeTableNode
                        .getASTObject());
                parentNode = elseIfStatementTreeTableNode.getParent();
            }
        }
        if (newElseIfStatement != null) {
            refresh(parentNode);
            setSelection(parentNode, newElseIfStatement);
            setEdit(parentNode, newElseIfStatement);
            setDirty(true);
        }
    }

    private void insertBeforeElseIfStatement(AstTreeTableNode selectedTreeTableNode) throws Exception {
        if (selectedTreeTableNode instanceof AstElseIfStatementTreeTableNode) {
            Statement newElseIfStatement = null;
            AstTreeTableNode parentNode = null;
            AstElseIfStatementTreeTableNode elseIfStatementTreeTableNode = (AstElseIfStatementTreeTableNode) selectedTreeTableNode;
            if (elseIfStatementTreeTableNode.getParentASTObject() instanceof IfStatement) {
                newElseIfStatement = insertElseIfStatementForIfStatement((IfStatement) elseIfStatementTreeTableNode
                        .getParentASTObject());
                parentNode = elseIfStatementTreeTableNode.getParent();
            }
            if (newElseIfStatement != null) {
                refresh(parentNode);
                setSelection(parentNode, newElseIfStatement);
                setEdit(parentNode, newElseIfStatement);
                setDirty(true);
            }
        }
    }

    private void insertAfterElseIfStatement(AstTreeTableNode selectedTreeTableNode) throws Exception {
        if (selectedTreeTableNode instanceof AstElseIfStatementTreeTableNode) {
            Statement newElseIfStatement = null;
            AstTreeTableNode parentNode = null;
            AstElseIfStatementTreeTableNode elseIfStatementTreeTableNode = (AstElseIfStatementTreeTableNode) selectedTreeTableNode;
            if (elseIfStatementTreeTableNode.getASTObject() instanceof IfStatement) {
                newElseIfStatement = insertElseIfStatementForIfStatement((IfStatement) elseIfStatementTreeTableNode
                        .getASTObject());
                parentNode = elseIfStatementTreeTableNode.getParent();
            }
            if (newElseIfStatement != null) {
                refresh(parentNode);
                setSelection(parentNode, newElseIfStatement);
                setEdit(parentNode, newElseIfStatement);
                setDirty(true);
            }
        }
    }

    private Statement addElseIfStatementForIfStatement(IfStatement ifStatement) throws Exception {
        if (ifStatement.getElseBlock() == null || ifStatement.getElseBlock() instanceof EmptyStatement
                || ifStatement.getElseBlock() instanceof BlockStatement) {
            Statement elseStatement = ifStatement.getElseBlock();
            IfStatement elseIfStatement = AstTreeTableEntityUtil.getNewIfStatement();
            elseIfStatement.setElseBlock(elseStatement);
            ifStatement.setElseBlock(elseIfStatement);
            return elseIfStatement;
        } else if (ifStatement.getElseBlock() instanceof IfStatement) {
            return addElseIfStatementForIfStatement((IfStatement) ifStatement.getElseBlock());
        }
        return null;
    }

    private Statement insertElseIfStatementForIfStatement(IfStatement ifStatement) throws Exception {
        if (ifStatement.getElseBlock() == null || ifStatement.getElseBlock() instanceof EmptyStatement
                || ifStatement.getElseBlock() instanceof BlockStatement
                || ifStatement.getElseBlock() instanceof IfStatement) {
            Statement elseStatement = ifStatement.getElseBlock();
            IfStatement elseIfStatement = AstTreeTableEntityUtil.getNewIfStatement();
            elseIfStatement.setElseBlock(elseStatement);
            ifStatement.setElseBlock(elseIfStatement);
            return elseIfStatement;
        }
        return null;
    }

    public void addCaseStatement(CaseStatement caseStatement, AstTreeTableNode selectedTreeTableNode,
            NodeAddType addType) throws Exception {
        if (caseStatement == null) {
            return;
        }
        if (selectedTreeTableNode instanceof AstSwitchStatementTreeTableNode
                && selectedTreeTableNode.getASTObject() instanceof SwitchStatement) {
            SwitchStatement swichStatement = (SwitchStatement) selectedTreeTableNode.getASTObject();
            swichStatement.addCase(caseStatement);
            refresh(selectedTreeTableNode);
            setSelection(selectedTreeTableNode, caseStatement);
            setEdit(selectedTreeTableNode, caseStatement);
            setDirty(true);
        } else if (selectedTreeTableNode instanceof AstCaseStatementTreeTableNode
                && selectedTreeTableNode.getASTObject() instanceof CaseStatement
                && selectedTreeTableNode.getParentASTObject() instanceof SwitchStatement) {
            SwitchStatement swichStatement = (SwitchStatement) selectedTreeTableNode.getParentASTObject();
            CaseStatement selectedCaseStatement = (CaseStatement) selectedTreeTableNode.getASTObject();
            int selectedIndex = swichStatement.getCaseStatements().indexOf(selectedCaseStatement);
            if (addType == NodeAddType.InserBefore) {
                swichStatement.getCaseStatements().add(selectedIndex, caseStatement);
            } else if (selectedIndex == swichStatement.getCaseStatements().size() - 1) {
                swichStatement.getCaseStatements().add(caseStatement);
            } else {
                swichStatement.getCaseStatements().add(selectedIndex + 1, caseStatement);
            }
            refresh(selectedTreeTableNode.getParent());
            setSelection(selectedTreeTableNode.getParent(), caseStatement);
            setEdit(selectedTreeTableNode.getParent(), caseStatement);
            setDirty(true);
        }
    }

    public void addDefaultStatement(AstTreeTableNode selectedTreeTableNode) throws Exception {
        SwitchStatement swichStatement = null;
        AstTreeTableNode parentNode = null;
        if (selectedTreeTableNode instanceof AstSwitchStatementTreeTableNode
                && selectedTreeTableNode.getASTObject() instanceof SwitchStatement) {
            swichStatement = (SwitchStatement) selectedTreeTableNode.getASTObject();
            parentNode = selectedTreeTableNode;
        } else if (selectedTreeTableNode instanceof AstCaseStatementTreeTableNode
                && selectedTreeTableNode.getParentASTObject() instanceof SwitchStatement) {
            swichStatement = (SwitchStatement) selectedTreeTableNode.getParentASTObject();
            parentNode = selectedTreeTableNode.getParent();
        }
        if (swichStatement != null
                && (swichStatement.getDefaultStatement() == null || swichStatement.getDefaultStatement() instanceof EmptyStatement)) {
            Statement newDefaultStatement = AstTreeTableEntityUtil.getNewDefaultStatement();
            swichStatement.setDefaultStatement(newDefaultStatement);
            refresh(parentNode);
            setSelection(parentNode, newDefaultStatement);
            setEdit(parentNode, newDefaultStatement);
            setDirty(true);
        }
    }

    public AstTreeTableNode getTreeTableNodeOfAstObjectFromParentNode(AstTreeTableNode parentNode, ASTNode astObject)
            throws Exception {
        if (astObject == null) {
            return null;
        }
        List<AstTreeTableNode> nodeList = (parentNode != null) ? parentNode.getChildren() : astTreeTableNodes;
        for (AstTreeTableNode astNode : nodeList) {
            if (astNode.getASTObject().equals(astObject)
                    || (astNode instanceof AstStatementTreeTableNode && astObject
                            .equals(((AstStatementTreeTableNode) astNode).getDescription()))) {
                return astNode;
            }
        }
        return null;
    }

    private void setSelection(AstTreeTableNode parentNode, ASTNode astObject) throws Exception {
        setSelection(getTreeTableNodeOfAstObjectFromParentNode(parentNode, astObject));
    }

    private void setSelection(AstTreeTableNode treeTableNode) throws Exception {
        if (treeTableNode != null) {
            treeTableViewer.setSelection(new StructuredSelection(treeTableNode));
        }
    }

    private void setEdit(AstTreeTableNode parentNode, ASTNode astObject) throws Exception {
        setEdit(getTreeTableNodeOfAstObjectFromParentNode(parentNode, astObject));
    }

    private void setEdit(AstTreeTableNode treeTableNode) throws Exception {
        if (treeTableNode != null) {
            treeTableViewer.getTree().setFocus();
            treeTableViewer.editElement(treeTableNode, 0);
        }
    }

    public int getNumberOfMethodAndClassNode() {
        int count = 0;
        for (AstTreeTableNode treeTableNode : astTreeTableNodes) {
            if (treeTableNode instanceof AstMethodTreeTableNode || treeTableNode instanceof AstClassTreeTableNode) {
                count++;
            }
        }
        return count;
    }

    public void refresh() throws Exception {
        refresh(null);
    }

    public void refresh(Object object) throws Exception {
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        refreshObjectWithoutReloading(object);
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
    }

    private void refreshObjectWithoutReloading(Object object) throws Exception {
        if (object == null) {
            reloadTreeTableNodes();
        } else {
            if (object instanceof AstScriptMainBlockStatmentTreeTableNode) {
                treeTableViewer.refresh();
            } else {
                treeTableViewer.refresh(object);
            }
        }
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
    private void reloadTreeTableNodes() throws Exception {
        astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        for (ASTNode astNode : astNodes) {
            if (astNode instanceof ClassNode) {
                if (((ClassNode) astNode).isScript()) {
                    mainClassNode = ((ClassNode) astNode);
                    astTreeTableNodes.addAll(AstTreeTableUtil.getChildren(((ClassNode) astNode), null));
                } else {
                    astTreeTableNodes.add(new AstClassTreeTableNode((ClassNode) astNode, null));
                }
            }
        }
        reloadTestCaseVariables();
        treeTableViewer.setInput(astTreeTableNodes);
    }

    public void reloadTestCaseVariables() {
        mainClassNode.getFields().clear();
        for (VariableEntity variable : parentPart.getVariables()) {
            Expression defaultExpression = new ConstantExpression(null);
            try {
                ASTNode variableDefaultValue = GroovyParser
                        .parseGroovyScriptAndGetFirstItem(variable.getDefaultValue());
                if (variableDefaultValue instanceof ExpressionStatement) {
                    defaultExpression = ((ExpressionStatement) variableDefaultValue).getExpression();
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
            mainClassNode.addField(new FieldNode(variable.getName(), Modifier.PUBLIC, new ClassNode(Object.class),
                    mainClassNode, defaultExpression));
        }
    }

    public void removeSelectedRows() throws Exception {
        removeRows(getSelectedNodes());
    }

    public void removeRows(List<AstTreeTableNode> treeTableNodes) throws Exception {
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

    private void removeRow(AstTreeTableNode treeTableNode, List<AstTreeTableNode> refreshNodeList) throws Exception {
        if (treeTableNode == null) {
            return;
        }
        if (treeTableNode instanceof AstElseStatementTreeTableNode
                && treeTableNode.getParentASTObject() instanceof IfStatement) {
            removeElseStatement(treeTableNode);
            refreshNodeList.add(treeTableNode.getParent());
        } else if (treeTableNode instanceof AstElseIfStatementTreeTableNode
                && treeTableNode.getASTObject() instanceof IfStatement
                && treeTableNode.getParentASTObject() instanceof IfStatement) {
            removeElseIfStatement(treeTableNode);
            refreshNodeList.add(treeTableNode.getParent());
        } else if (treeTableNode instanceof AstCatchStatementTreeTableNode
                && treeTableNode.getParentASTObject() instanceof TryCatchStatement) {
            removeCatchStatement(treeTableNode);
            refreshNodeList.add(treeTableNode.getParent());
        } else if (treeTableNode instanceof AstSwitchDefaultStatementTreeTableNode
                && treeTableNode.getParentASTObject() instanceof SwitchStatement) {
            removeSwitchStatement(treeTableNode);
            refreshNodeList.add(treeTableNode.getParent());
        } else if (treeTableNode instanceof AstFinallyStatementTreeTableNode
                && treeTableNode.getParentASTObject() instanceof TryCatchStatement) {
            removeFinallyStatement(treeTableNode);
            refreshNodeList.add(treeTableNode.getParent());
        } else {
            removeStatement(treeTableNode, refreshNodeList);
        }
    }

    private void removeStatement(AstTreeTableNode treeTableNode, List<AstTreeTableNode> refreshNodeList)
            throws Exception {
        if (treeTableNode.getParent() != null) {
            AstTreeTableNode parentNode = treeTableNode.getParent();
            if (treeTableNode instanceof AstStatementTreeTableNode) {
                AstStatementTreeTableNode statementNode = (AstStatementTreeTableNode) treeTableNode;
                removeDescription(parentNode, statementNode);
            }
            parentNode.removeChildObject(treeTableNode.getASTObject());
            refreshNodeList.add(parentNode);
        } else {
            if (treeTableNode.getASTObject() instanceof ClassNode && treeTableNode.getASTObject() != mainClassNode) {
                if (astNodes.remove(treeTableNode.getASTObject())) {
                    refreshNodeList.add(null);
                }
            } else if (treeTableNode.getASTObject() instanceof MethodNode) {
                if (mainClassNode.getMethods().remove(treeTableNode.getASTObject())) {
                    refreshNodeList.add(null);
                }
            }
        }
    }

    public void removeDescription(AstTreeTableNode parentNode, AstStatementTreeTableNode statementNode) {
        if (parentNode != null && statementNode != null && statementNode.getDescription() != null) {
            parentNode.removeChildObject(statementNode.getDescription());
        }
    }

    private void removeFinallyStatement(AstTreeTableNode treeTableNode) throws Exception {
        TryCatchStatement tryCatchStatement = (TryCatchStatement) treeTableNode.getParentASTObject();
        if (tryCatchStatement.getCatchStatements().isEmpty()) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                    StringConstants.WARN_TRY_STATEMENT_MUST_HAVE_CATCH_OR_FINALLY);
            return;
        }
        ((TryCatchStatement) treeTableNode.getParentASTObject()).setFinallyStatement(new EmptyStatement());
    }

    private void removeSwitchStatement(AstTreeTableNode treeTableNode) throws Exception {
        ((SwitchStatement) treeTableNode.getParentASTObject()).setDefaultStatement(new EmptyStatement());
    }

    private void removeCatchStatement(AstTreeTableNode treeTableNode) throws Exception {
        TryCatchStatement tryCatchStatement = (TryCatchStatement) treeTableNode.getParentASTObject();
        if ((tryCatchStatement.getFinallyStatement() == null || tryCatchStatement.getFinallyStatement() instanceof EmptyStatement)
                && tryCatchStatement.getCatchStatements().size() == 1) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                    StringConstants.WARN_TRY_STATEMENT_MUST_HAVE_CATCH_OR_FINALLY);
            return;
        }
        tryCatchStatement.getCatchStatements().remove(treeTableNode.getASTObject());
    }

    private void removeElseIfStatement(AstTreeTableNode treeTableNode) throws Exception {
        ((IfStatement) treeTableNode.getParentASTObject()).setElseBlock(((IfStatement) treeTableNode.getASTObject())
                .getElseBlock());
    }

    private void removeElseStatement(AstTreeTableNode treeTableNode) throws Exception {
        ((IfStatement) treeTableNode.getParentASTObject()).setElseBlock(new EmptyStatement());
    }

    public void setDirty(boolean isDirty) {
        parentPart.setDirty(isDirty);
        setChanged(isDirty);
    }

    public void moveUp() throws Exception {
        AstTreeTableNode selectedNode = getSelectedNode();
        if (selectedNode != null && selectedNode.getParent() != null) {
            AstTreeTableNode parentNode = selectedNode.getParent();
            List<AstTreeTableNode> childNodes = parentNode.getChildren();
            int nodeIndex = childNodes.indexOf(selectedNode);
            if (nodeIndex - 1 < 0) {
                return;
            }
            move(selectedNode, childNodes.get(nodeIndex - 1), NodeAddType.InserBefore);
        }
    }

    public void moveDown() throws Exception {
        AstTreeTableNode selectedNode = getSelectedNode();
        if (selectedNode != null && selectedNode.getParent() != null) {
            AstTreeTableNode parentNode = selectedNode.getParent();
            List<AstTreeTableNode> childNodes = parentNode.getChildren();
            int nodeIndex = childNodes.indexOf(selectedNode);
            if (nodeIndex + 1 >= childNodes.size()) {
                return;
            }
            move(selectedNode, childNodes.get(nodeIndex + 1), NodeAddType.InserAfter);
        }
    }

    public void move(AstTreeTableNode sourceNode, AstTreeTableNode destinationNode, NodeAddType addType)
            throws Exception {
        if (sourceNode == null || destinationNode == null) {
            return;
        }
        List<AstTreeTableNode> destinationNodeList = getNodeList(destinationNode);
        int destinationNodeIndex = destinationNodeList.indexOf(destinationNode);
        List<AstTreeTableNode> removeRow = new ArrayList<AstTreeTableNode>();
        removeRow.add(sourceNode);
        removeRows(removeRow);

        if (destinationNodeIndex == destinationNodeList.size() - 1) {
            addNewAstObject(sourceNode.getASTObject(), destinationNode, NodeAddType.InserAfter);
        } else {
            addNewAstObject(sourceNode.getASTObject(), destinationNode, addType);
        }
    }

    private List<AstTreeTableNode> getNodeList(AstTreeTableNode node) throws Exception {
        if (node == null) {
            return null;
        }
        if (node.getParent() == null) {
            return astTreeTableNodes;
        } else {
            return node.getParent().getChildren();
        }
    }

    public int getAstObjectIndexFromParentNode(AstTreeTableNode parentTreeTableNode, ASTNode astObject)
            throws Exception {
        if (parentTreeTableNode != null) {
            List<AstTreeTableNode> childNodes = parentTreeTableNode.getChildren();
            for (AstTreeTableNode childNode : childNodes) {
                if ((childNode.getASTObject().equals(astObject))
                        || (childNode instanceof AstStatementTreeTableNode
                                && ((AstStatementTreeTableNode) childNode).getDescription() != null && ((AstStatementTreeTableNode) childNode)
                                .getDescription().equals(astObject))) {
                    return childNodes.indexOf(childNode);
                }
            }
        } else {
            for (AstTreeTableNode childNode : astTreeTableNodes) {
                if ((childNode.getASTObject().equals(astObject))) {
                    return astTreeTableNodes.indexOf(childNode);
                }
            }
        }
        return -1;
    }

    public void addMethod(MethodNode method, NodeAddType addType) throws Exception {
        int methodIndex = -1;
        AstTreeTableNode selectedNode = getSelectedNode();
        if (selectedNode instanceof AstMethodTreeTableNode) {
            methodIndex = mainClassNode.getMethods().indexOf(selectedNode.getASTObject());
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
        setSelection(null, method);
    }

    public void updateMethod(MethodNode oldMethod, MethodNode newMethod) throws Exception {
        int methodIndex = mainClassNode.getMethods().indexOf(oldMethod);
        mainClassNode.getMethods().remove(oldMethod);
        addMethod(newMethod, methodIndex);
        setDirty(true);
        refresh();
        setSelection(null, newMethod);
    }

    private void addMethod(MethodNode newMethod, int methodIndex) {
        if (methodIndex >= 0 && methodIndex < mainClassNode.getMethods().size()) {
            mainClassNode.getMethods().add(methodIndex, newMethod);
        } else {
            mainClassNode.getMethods().add(newMethod);
        }
    }

    private int getAstObjectIndexFromParentNode(AstTreeTableNode parentNode, AstTreeTableNode node) {
        if (parentNode == null) {
            return 0;
        }
        if (node instanceof AstElseStatementTreeTableNode) {
            return parentNode.getChildObjectIndex(((AstElseStatementTreeTableNode) node).getRootIfStatement());
        } else if (node instanceof AstElseIfStatementTreeTableNode) {
            return parentNode.getChildObjectIndex(((AstElseIfStatementTreeTableNode) node).getRootIfStatement());
        } else if (node instanceof AstCatchStatementTreeTableNode) {
            return parentNode.getChildObjectIndex(((AstCatchStatementTreeTableNode) node).getTryCatchStatement());
        }
        return parentNode.getChildObjectIndex(node.getASTObject());
    }

    public void changeFailureHandling(FailureHandling failureHandling) throws Exception {
        changeFailureHandling(failureHandling, getSelectedNodes());
    }

    private void changeFailureHandling(FailureHandling failureHandling, List<AstTreeTableNode> treeTableNodes)
            throws Exception {
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        for (int i = treeTableNodes.size() - 1; i >= 0; i--) {
            changeFailureHandling(failureHandling, treeTableNodes.get(i));
        }
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
    }

    private void changeFailureHandling(FailureHandling failureHandling, AstTreeTableNode treeTableNode)
            throws Exception {
        if (treeTableNode instanceof AstAbstractKeywordTreeTableNode) {
            AstAbstractKeywordTreeTableNode keywordNode = (AstAbstractKeywordTreeTableNode) treeTableNode;
            if (keywordNode.getFailureHandlingValue() != null
                    && !keywordNode.getFailureHandlingValue().equals(failureHandling)) {
                if (keywordNode.setFailureHandlingValue(failureHandling)) {
                    treeTableViewer.update(keywordNode, null);
                    setDirty(true);
                }
            }
        }
    }

    private String parseAstObjectToString(ASTNode astObject) {
        StringBuilder stringBuilder = new StringBuilder();
        GroovyParser groovyParser = new GroovyParser(stringBuilder);
        groovyParser.parse(astObject);
        return stringBuilder.toString();
    }

    public void copy(List<AstTreeTableNode> copyNodes) {
        StringBuilder scriptSnippets = new StringBuilder();
        for (AstTreeTableNode astTreeTableNode : copyNodes) {
            if (astTreeTableNode instanceof AstStatementTreeTableNode) {
                AstStatementTreeTableNode statementNode = (AstStatementTreeTableNode) astTreeTableNode;
                if (statementNode.getDescription() != null) {
                    scriptSnippets.append(parseAstObjectToString(statementNode.getDescription()));
                    scriptSnippets.append(GROOVY_NEW_LINE_CHARACTER);
                }
            }
            scriptSnippets.append(parseAstObjectToString(astTreeTableNode.getASTObject()));
            scriptSnippets.append(GROOVY_NEW_LINE_CHARACTER);
        }
        if (scriptSnippets.length() > 0) {
            final Clipboard cb = new Clipboard(Display.getCurrent());
            ScriptTransferData transferData = new ScriptTransferData(scriptSnippets.toString(), parentPart
                    .getTestCase().getId());
            cb.setContents(new Object[] { new ScriptTransferData[] { transferData } },
                    new Transfer[] { new ScriptTransfer() });
        }
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
        List<ASTNode> astNodes = GroovyParser.parseGroovyScriptIntoAstNodes(snippet);
        for (ASTNode astNode : astNodes) {
            if (astNode instanceof BlockStatement) {
                List<Statement> childStatements = ((BlockStatement) astNode).getStatements();
                addNewAstObjects(childStatements, destinationNode, addType);
                break;
            }
        }
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
     *            menu item id, see {@link TreeTableMenuItemConstants}
     * @param destinationNode
     *            destination node to add
     * @param addType
     *            see {@link NodeAddType}
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
        try {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (currentProject != null) {
                TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(Display.getCurrent().getActiveShell(),
                        new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(new EntityProvider()));

                dialog.setAllowMultiple(false);
                dialog.setTitle(StringConstants.EDI_TITLE_TEST_CASE_BROWSER);
                dialog.setInput(TreeEntityUtil.getChildren(null,
                        FolderController.getInstance().getTestCaseRoot(currentProject)));
                if (dialog.open() == Window.OK) {
                    Object[] selectedObjects = dialog.getResult();
                    for (Object object : selectedObjects) {
                        if (!(object instanceof ITreeEntity))
                            continue;

                        ITreeEntity treeEntity = (ITreeEntity) object;
                        List<TestCaseEntity> testCaseList = new ArrayList<TestCaseEntity>();
                        if (treeEntity.getObject() instanceof FolderEntity) {
                            for (TestCaseEntity testCase : TestCaseEntityUtil
                                    .getTestCasesFromFolderTree((FolderTreeEntity) treeEntity)) {
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
                        List<Statement> statementsToAdd = new ArrayList<Statement>();
                        List<VariableEntity> variablesToAdd = new ArrayList<VariableEntity>();
                        if (addType == NodeAddType.InserBefore) {
                            for (TestCaseEntity testCase : testCaseList) {
                                statementsToAdd.add(generateStatementForCalledTestCase(testCase, variablesToAdd));
                            }
                        } else {
                            for (int index = testCaseList.size() - 1; index >= 0; index--) {
                                statementsToAdd.add(generateStatementForCalledTestCase(testCaseList.get(index),
                                        variablesToAdd));
                            }
                        }

                        addNewAstObjects(statementsToAdd, destinationNode, addType);
                    }
                }
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_CALL_TEST_CASE);
            LoggerSingleton.logError(e);
        }
    }

    private Statement generateStatementForCalledTestCase(TestCaseEntity testCase, List<VariableEntity> variablesToAdd)
            throws Exception {
        ExpressionStatement statement = AstTreeTableInputUtil.generateCallTestCaseExpresionStatement(testCase);
        MethodCallExpression methodCallExpression = (MethodCallExpression) statement.getExpression();
        ArgumentListExpression argumentList = (ArgumentListExpression) methodCallExpression.getArguments();
        variablesToAdd.addAll(TestCaseEntityUtil.getCallTestCaseVariables(argumentList));
        return statement;
    }

    private void addNewThrowStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(AstTreeTableEntityUtil.getNewThrowStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_THROW_STATEMENT);
        }
    }

    public void addNewMethod(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            MethodObjectBuilderDialog dialog = new MethodObjectBuilderDialog(Display.getCurrent().getActiveShell(),
                    null);
            int result = dialog.open();
            if (result == Window.OK && dialog.getReturnValue() != null) {
                addMethod(dialog.getReturnValue(), addType);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_ADD_METHOD);
        }
    }

    public void addNewBuiltInKeyword(int id, AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            String className = TreeTableMenuItemConstants.getContributingClassName(id);
            Class<?> clazz = KeywordController.getInstance().getBuiltInKeywordClass(className);
            addImport(clazz);
            addImport(ObjectRepository.class);
            addImport(TestCaseFactory.class);
            addImport(FailureHandling.class);
            String defaultSettingKeywordName = TestCasePreferenceDefaultValueInitializer.getDefaultKeywords().get(
                    className);
            ASTNode astNode = null;
            if (!StringUtils.isBlank(defaultSettingKeywordName)
                    && KeywordController.getInstance().getBuiltInKeywordByName(className, defaultSettingKeywordName) != null) {
                astNode = AstTreeTableInputUtil.createBuiltInKeywordMethodCall(clazz.getSimpleName(),
                        defaultSettingKeywordName);
            } else {
                astNode = AstTreeTableEntityUtil.getNewKeyword(false, clazz.getSimpleName());
            }
            addNewAstObject(astNode, destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_ADD_KEYWORD);
        }
    }

    public void addNewCustomKeyword(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            Statement statemt = AstTreeTableEntityUtil.getNewKeyword(true, null);
            if (statemt != null) {
                addImport(ObjectRepository.class);
                addImport(TestCaseFactory.class);
                addImport(FailureHandling.class);

                addNewAstObject(statemt, destinationNode, addType);
            } else {
                MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                        StringConstants.PA_ERROR_MSG_NO_CUSTOM_KEYWORD);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_ADD_KEYWORD);
        }
    }

    public void addNewIfStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(AstTreeTableEntityUtil.getNewIfStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_IF_STATEMENT);
        }
    }

    public void addNewElseStatement(AstTreeTableNode destinationNode) {
        try {
            addElseStatement(destinationNode);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_ELSE_STATEMENT);
        }
    }

    public void addNewElseIfStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addElseIfStatement(destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_ELSE_IF_STATEMENT);
        }
    }

    public void addNewWhileStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(AstTreeTableEntityUtil.getNewWhileStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_WHILE_STATEMENT);
        }
    }

    public void addNewForStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(AstTreeTableEntityUtil.getNewForStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_FOR_STATEMENT);
        }
    }

    public void addNewBinaryStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new ExpressionStatement(AstTreeTableEntityUtil.getNewBinaryExpression()), destinationNode,
                    addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_BINARY_STATEMENT);
        }
    }

    public void addNewAssertStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new AssertStatement(AstTreeTableEntityUtil.getNewBooleanExpression()), destinationNode,
                    addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_ASSERT_STATEMENT);
        }
    }

    public void addNewMethodCall(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new ExpressionStatement(AstTreeTableEntityUtil.getNewMethodCallExpression()),
                    destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_ASSERT_STATEMENT);
        }
    }

    public void addNewBreakStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new BreakStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_BREAK_STATEMENT);
        }
    }

    public void addNewContinueStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(new ContinueStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_CONTINUE_STATEMENT);
        }
    }

    public void addNewReturnStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(AstTreeTableEntityUtil.getNewReturnStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_RETURN_STATEMENT);
        }
    }

    public void addNewSwitchStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(AstTreeTableEntityUtil.getNewSwitchStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_SWITCH_STATEMENT);
        }
    }

    public void addNewCaseStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addCaseStatement(AstTreeTableEntityUtil.getNewCaseStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_CASE_STATEMENT);
        }
    }

    public void addNewDefaultStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addDefaultStatement(destinationNode);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_DEFAULT_STATEMENT);
        }
    }

    public void addNewTryStatement(AstTreeTableNode destinationNode, NodeAddType addType) {
        try {
            addNewAstObject(AstTreeTableEntityUtil.getNewTryCatchStatement(), destinationNode, addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_TRY_STATEMENT);
        }
    }

    public void addNewCatchStatement(AstTreeTableNode selectedTreeTableNode, NodeAddType addType) {
        try {
            CatchStatement catchStatement = AstTreeTableEntityUtil.getNewCatchStatement();
            if (selectedTreeTableNode instanceof AstTryStatementTreeTableNode
                    && selectedTreeTableNode.getASTObject() instanceof TryCatchStatement) {
                TryCatchStatement tryStatement = (TryCatchStatement) selectedTreeTableNode.getASTObject();
                tryStatement.addCatch(catchStatement);
                refresh(selectedTreeTableNode.getParent());
                setSelection(selectedTreeTableNode.getParent(), catchStatement);
                setEdit(selectedTreeTableNode.getParent(), catchStatement);
                setDirty(true);
            } else if (selectedTreeTableNode instanceof AstCatchStatementTreeTableNode
                    && selectedTreeTableNode.getASTObject() instanceof CatchStatement
                    && selectedTreeTableNode.getParentASTObject() instanceof TryCatchStatement) {
                TryCatchStatement tryStatement = (TryCatchStatement) selectedTreeTableNode.getParentASTObject();
                CatchStatement selectedCatchStatement = (CatchStatement) selectedTreeTableNode.getASTObject();
                int selectedIndex = tryStatement.getCatchStatements().indexOf(selectedCatchStatement);
                if (addType == NodeAddType.InserBefore) {
                    tryStatement.getCatchStatements().add(selectedIndex, catchStatement);
                } else if (selectedIndex == tryStatement.getCatchStatements().size() - 1) {
                    tryStatement.getCatchStatements().add(catchStatement);
                } else {
                    tryStatement.getCatchStatements().add(selectedIndex + 1, catchStatement);
                }
                refresh(selectedTreeTableNode.getParent());
                setSelection(selectedTreeTableNode.getParent(), catchStatement);
                setEdit(selectedTreeTableNode.getParent(), catchStatement);
                setDirty(true);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_CATCH_STATEMENT);
        }
    }

    public void addNewFinallyStatement(AstTreeTableNode selectedTreeTableNode, NodeAddType addType) {
        try {
            TryCatchStatement tryStatement = null;
            AstTreeTableNode parentNode = null;
            if (selectedTreeTableNode instanceof AstTryStatementTreeTableNode
                    && selectedTreeTableNode.getASTObject() instanceof TryCatchStatement) {
                tryStatement = (TryCatchStatement) selectedTreeTableNode.getASTObject();
                parentNode = selectedTreeTableNode.getParent();
            } else if (selectedTreeTableNode instanceof AstCatchStatementTreeTableNode
                    && selectedTreeTableNode.getParentASTObject() instanceof TryCatchStatement) {
                tryStatement = (TryCatchStatement) selectedTreeTableNode.getParentASTObject();
                parentNode = selectedTreeTableNode.getParent();
            }
            if (tryStatement != null
                    && (tryStatement.getFinallyStatement() == null || tryStatement.getFinallyStatement() instanceof EmptyStatement)) {
                Statement newFinallyStatement = AstTreeTableEntityUtil.getNewFinallyStatement();
                tryStatement.setFinallyStatement(newFinallyStatement);
                refresh(parentNode);
                setSelection(parentNode, newFinallyStatement);
                setEdit(parentNode, newFinallyStatement);
                setDirty(true);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_FINALLY_STATEMENT);
        }
    }
}
