package com.kms.katalon.composer.testcase.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

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
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransfer;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransferData;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;
import com.kms.katalon.core.groovy.GroovyParser;
import com.kms.katalon.core.model.FailureHandling;

public class TestCaseTreeTableInput {
    private static final String GROOVY_NEW_LINE_CHARACTER = "\n";

    public enum NodeAddType {
        Add, InserBefore, InserAfter
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

    public List<AstTreeTableNode> getInput() {
        return astTreeTableNodes;
    }

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

    public AstTreeTableNode getSelectedNode() {
        if (treeTableViewer.getSelection() instanceof ITreeSelection) {
            ITreeSelection selection = (ITreeSelection) treeTableViewer.getSelection();
            if (selection.getFirstElement() instanceof AstTreeTableNode) {
                return (AstTreeTableNode) selection.getFirstElement();
            }
        }
        return null;
    }

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

    public void addNewAstObjects(List<? extends ASTNode> astObjects, NodeAddType addType) throws Exception {
        addNewAstObjects(astObjects, getSelectedNode(), addType);
    }

    public void addNewAstObjects(List<? extends ASTNode> astObjects, AstTreeTableNode destinationNode,
            NodeAddType addType) throws Exception {
        if (astObjects != null) {
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            if (destinationNode != null) {
                if (addType == NodeAddType.Add) {
                    if (destinationNode.hasChildren()) {
                        for (ASTNode astObject : astObjects) {
                            addNewAstObjectToParentNode(destinationNode, astObject, -1);
                        }
                    } else {
                        addASTObjectsAfter(astObjects, destinationNode);
                    }
                } else if (addType == NodeAddType.InserAfter) {
                    addASTObjectsAfter(astObjects, destinationNode);
                } else if (addType == NodeAddType.InserBefore) {
                    addASTObjectsBefore(astObjects, destinationNode);
                }
            } else {
                addAstObjectsIntoScriptMainBlock(astObjects);
            }
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
        }
    }

    public void addNewAstObject(ASTNode astObject, NodeAddType addType) throws Exception {
        addNewAstObject(astObject, getSelectedNode(), addType);
    }

    public void addNewAstObject(ASTNode astObject, AstTreeTableNode destinationNode, NodeAddType addType)
            throws Exception {
        if (astObject != null) {
            AstTreeTableNode topItem = getTopItem();
            Object[] expandedElements = saveExpandedState();
            if (destinationNode != null) {
                if (addType == NodeAddType.Add) {
                    if (destinationNode.hasChildren()) {
                        addNewAstObjectToParentNode(destinationNode, astObject, -1);
                    } else {
                        addASTObjectAfter(astObject, destinationNode);
                    }
                } else if (addType == NodeAddType.InserAfter) {
                    addASTObjectAfter(astObject, destinationNode);
                } else if (addType == NodeAddType.InserBefore) {
                    addASTObjectBefore(astObject, destinationNode);
                }
            } else {
                addAstObjectIntoScriptMainBlock(astObject);
            }
            reloadExpandedState(expandedElements);
            setTopItem(topItem);
        }
    }

    private void addAstObjectIntoScriptMainBlock(ASTNode astObject) throws Exception {
        for (AstTreeTableNode astTreeTableNode : astTreeTableNodes) {
            if (astTreeTableNode instanceof AstScriptMainBlockStatmentTreeTableNode) {
                astTreeTableNode.addChildObject(astObject, -1);
                refreshObjectWithoutReloading(astTreeTableNode);
                setSelection(astTreeTableNode, astObject);
                setEdit(astTreeTableNode, astObject);
                setDirty(true);
                break;
            }
        }
    }

    private void addAstObjectsIntoScriptMainBlock(List<? extends ASTNode> astObjects) throws Exception {
        for (AstTreeTableNode astTreeTableNode : astTreeTableNodes) {
            if (astTreeTableNode instanceof AstScriptMainBlockStatmentTreeTableNode) {
                for (ASTNode astObject : astObjects) {
                    astTreeTableNode.addChildObject(astObject, -1);
                }
                refreshObjectWithoutReloading(astTreeTableNode);
                setSelection(astTreeTableNode, astObjects.get(astObjects.size() - 1));
                setEdit(astTreeTableNode, astObjects.get(astObjects.size() - 1));
                setDirty(true);
                break;
            }
        }
    }

    public void addASTObjectBefore(ASTNode astObject, AstTreeTableNode selectedTreeTableNode) throws Exception {
        if (selectedTreeTableNode != null && astObject != null) {
            insertAstObject(astObject, selectedTreeTableNode, NodeAddType.InserBefore);
        }
    }

    public void addASTObjectsBefore(List<? extends ASTNode> astObjects, AstTreeTableNode selectedTreeTableNode)
            throws Exception {
        if (selectedTreeTableNode != null) {
            for (ASTNode astObject : astObjects) {
                addASTObjectBefore(astObject, selectedTreeTableNode);
            }
        }
    }

    public void addASTObjectAfter(ASTNode astObject, AstTreeTableNode selectedTreeTableNode) throws Exception {
        if (selectedTreeTableNode != null && astObject != null) {
            insertAstObject(astObject, selectedTreeTableNode, NodeAddType.InserAfter);
        }
    }

    public void addASTObjectsAfter(List<? extends ASTNode> astObjects, AstTreeTableNode selectedTreeTableNode)
            throws Exception {
        if (selectedTreeTableNode != null) {
            for (int i = astObjects.size() - 1; i >= 0; i--) {
                addASTObjectAfter(astObjects.get(i), selectedTreeTableNode);
            }
        }
    }

    private void insertAstObject(ASTNode astObject, AstTreeTableNode sibblingNode, NodeAddType addType)
            throws Exception {
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
                addNewAstObjectToParentNode(sibblingNode.getParent(), astObject, sibblingIndex
                        + ((addType == NodeAddType.InserAfter) ? 1 : 0) + commentOffset);
            }
        }
    }

    private void addNewAstObjectToParentNode(AstTreeTableNode parentNode, ASTNode astObject, int index)
            throws Exception {
        parentNode.addChildObject(astObject, index);
        refreshObjectWithoutReloading(parentNode);
        setSelection(parentNode, astObject);
        setEdit(parentNode, astObject);
        setDirty(true);
    }

    public void addElseStatement() throws Exception {
        if (treeTableViewer.getSelection() instanceof ITreeSelection) {
            ITreeSelection selection = (ITreeSelection) treeTableViewer.getSelection();
            if (!selection.isEmpty()) {
                Statement newElseStatement = null;
                AstTreeTableNode parentNode = null;
                if (selection.getFirstElement() instanceof AstIfStatementTreeTableNode) {
                    AstIfStatementTreeTableNode ifStatementTreeTableNode = (AstIfStatementTreeTableNode) selection
                            .getFirstElement();
                    if (ifStatementTreeTableNode.getASTObject() instanceof IfStatement) {
                        newElseStatement = addElseBlockForIfStatement((IfStatement) ifStatementTreeTableNode
                                .getASTObject());
                        parentNode = ifStatementTreeTableNode.getParent();

                    }
                } else if (selection.getFirstElement() instanceof AstElseIfStatementTreeTableNode) {
                    AstElseIfStatementTreeTableNode elseIfStatementTreeTableNode = (AstElseIfStatementTreeTableNode) selection
                            .getFirstElement();
                    if (elseIfStatementTreeTableNode.getASTObject() instanceof IfStatement) {
                        newElseStatement = addElseBlockForIfStatement((IfStatement) elseIfStatementTreeTableNode
                                .getASTObject());
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

    public void addElseIfStatement(NodeAddType addType) throws Exception {
        AstTreeTableNode selectedTreeTableNode = getSelectedNode();
        if (selectedTreeTableNode != null) {
            if (addType == NodeAddType.Add) {
                addElseIfStatement(selectedTreeTableNode);
            } else if (addType == NodeAddType.InserAfter) {
                insertAfterElseIfStatement(selectedTreeTableNode);
            } else if (addType == NodeAddType.InserBefore) {
                insertBeforeElseIfStatement(selectedTreeTableNode);
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

    public void addCaseStatement(CaseStatement caseStatement, NodeAddType addType) throws Exception {
        if (caseStatement == null) {
            return;
        }
        AstTreeTableNode selectedTreeTableNode = getSelectedNode();
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

    public void addDefaultStatement() throws Exception {
        AstTreeTableNode selectedTreeTableNode = getSelectedNode();
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

    public void addCatchStatement(CatchStatement catchStatement, NodeAddType addType) throws Exception {
        if (catchStatement == null) {
            return;
        }
        AstTreeTableNode selectedTreeTableNode = getSelectedNode();
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
    }

    public void addFinallyStatement() throws Exception {
        AstTreeTableNode selectedTreeTableNode = getSelectedNode();
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
        if (object == null || object instanceof AstScriptMainBlockStatmentTreeTableNode) {
            reloadTreeTableNode();
        } else {
            treeTableViewer.refresh(object);
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

    private void reloadTreeTableNode() throws Exception {
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
        treeTableViewer.setInput(astTreeTableNodes);
    }

    public void removeSelectedRows() throws Exception {
        removeRows(getSelectedNodes());
    }

    public void removeRows(List<AstTreeTableNode> treeTableNodes) throws Exception {
        AstTreeTableNode topItem = getTopItem();
        Object[] expandedElements = saveExpandedState();
        for (int i = treeTableNodes.size() - 1; i >= 0; i--) {
            removeRow(treeTableNodes.get(i));
        }
        reloadExpandedState(expandedElements);
        setTopItem(topItem);
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

    public void removeRow(AstTreeTableNode treeTableNode) throws Exception {
        if (treeTableNode instanceof AstElseStatementTreeTableNode
                && treeTableNode.getParentASTObject() instanceof IfStatement) {
            ((IfStatement) treeTableNode.getParentASTObject()).setElseBlock(new EmptyStatement());
            refreshObjectWithoutReloading(treeTableNode.getParent());
            setDirty(true);
        } else if (treeTableNode instanceof AstElseIfStatementTreeTableNode
                && treeTableNode.getASTObject() instanceof IfStatement
                && treeTableNode.getParentASTObject() instanceof IfStatement) {
            ((IfStatement) treeTableNode.getParentASTObject())
                    .setElseBlock(((IfStatement) treeTableNode.getASTObject()).getElseBlock());
            refreshObjectWithoutReloading(treeTableNode.getParent());
            setDirty(true);
        } else if (treeTableNode instanceof AstCatchStatementTreeTableNode
                && treeTableNode.getParentASTObject() instanceof TryCatchStatement) {
            TryCatchStatement tryCatchStatement = (TryCatchStatement) treeTableNode.getParentASTObject();
            if ((tryCatchStatement.getFinallyStatement() == null || tryCatchStatement.getFinallyStatement() instanceof EmptyStatement)
                    && tryCatchStatement.getCatchStatements().size() == 1) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.WARN, StringConstants.WARN_TRY_STATEMENT_MUST_HAVE_CATCH_OR_FINALLY);
                return;
            }
            tryCatchStatement.getCatchStatements().remove(
                    treeTableNode.getASTObject());
            refreshObjectWithoutReloading(treeTableNode.getParent());
            setDirty(true);
        } else if (treeTableNode instanceof AstSwitchDefaultStatementTreeTableNode
                && treeTableNode.getParentASTObject() instanceof SwitchStatement) {
            ((SwitchStatement) treeTableNode.getParentASTObject()).setDefaultStatement(new EmptyStatement());
            refreshObjectWithoutReloading(treeTableNode.getParent());
            setDirty(true);
        } else if (treeTableNode instanceof AstFinallyStatementTreeTableNode
                && treeTableNode.getParentASTObject() instanceof TryCatchStatement) {
            TryCatchStatement tryCatchStatement = (TryCatchStatement) treeTableNode.getParentASTObject();
            if (tryCatchStatement.getCatchStatements().isEmpty()) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.WARN, StringConstants.WARN_TRY_STATEMENT_MUST_HAVE_CATCH_OR_FINALLY);
                return;
            }
            ((TryCatchStatement) treeTableNode.getParentASTObject()).setFinallyStatement(new EmptyStatement());
            refreshObjectWithoutReloading(treeTableNode.getParent());
            setDirty(true);
        } else {
            if (treeTableNode.getParent() != null) {
                AstTreeTableNode parentNode = treeTableNode.getParent();
                if (treeTableNode instanceof AstStatementTreeTableNode) {
                    AstStatementTreeTableNode statementNode = (AstStatementTreeTableNode) treeTableNode;
                    if (statementNode.getDescription() != null) {
                        parentNode.removeChildObject(statementNode.getDescription());
                    }
                }
                parentNode.removeChildObject(treeTableNode.getASTObject());
                refreshObjectWithoutReloading(parentNode);
                setDirty(true);
            } else {
                if (treeTableNode.getASTObject() instanceof ClassNode && treeTableNode.getASTObject() != mainClassNode) {
                    if (astNodes.remove(treeTableNode.getASTObject())) {
                        refreshObjectWithoutReloading(null);
                        setDirty(true);
                    }
                } else if (treeTableNode.getASTObject() instanceof MethodNode) {
                    if (mainClassNode.getMethods().remove(treeTableNode.getASTObject())) {
                        refreshObjectWithoutReloading(null);
                        setDirty(true);
                    }
                }
            }
        }
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
        List<AstTreeTableNode> nodeList = new ArrayList<AstTreeTableNode>();
        nodeList.add(sourceNode);
        cut(nodeList);
        paste(destinationNode, addType);
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
                    refreshObjectWithoutReloading(keywordNode);
                    setDirty(true);
                }
            }
        }
    }

    public void copy() throws Exception {
        copy(getSelectedNodes());
    }

    private String parseAstObjectToString(ASTNode astObject) {
        StringBuilder stringBuilder = new StringBuilder();
        GroovyParser groovyParser = new GroovyParser(stringBuilder);
        groovyParser.parse(astObject);
        return stringBuilder.toString();
    }

    private void copy(List<AstTreeTableNode> copyNodes) {
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

    public void cut() throws Exception {
        cut(getSelectedNodes());
    }

    private void cut(List<AstTreeTableNode> cutNodes) throws Exception {
        copy(cutNodes);
        removeRows(cutNodes);
    }

    public void paste() throws Exception {
        paste(getSelectedNode(), NodeAddType.Add);
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

}
