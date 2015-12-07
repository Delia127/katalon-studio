package com.kms.katalon.composer.testcase.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;

import com.kms.katalon.composer.testcase.ast.treetable.AstAbstractKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstAssertStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstBinaryStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstBreakStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCaseStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCatchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstClassTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCommentStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstContinueStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCustomKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseIfStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstFieldTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstFinallyStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstForStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstIfStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodCallStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstReturnStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstScriptMainBlockStatmentTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstSwitchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstThrowStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTryStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstWhileStatementTreeTableNode;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;

public class AstTreeTableUtil {
    private static final String GROOVY_SCRIPT_RUN_METHOD_NAME = "run";

    public static List<AstTreeTableNode> getChildren(ClassNode classNode, AstTreeTableNode parentNode) throws Exception {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        for (MethodNode methodNode : classNode.getMethods()) {
            if (methodNode.getLineNumber() < 0 && !methodNode.getName().equals(GROOVY_SCRIPT_RUN_METHOD_NAME)) {
                continue;
            }
            astTreeTableNodes.addAll(parseAstObjectIntoTreeTableNode(methodNode, classNode, parentNode));
        }
        for (FieldNode fieldNode : classNode.getFields()) {
            if (fieldNode.getLineNumber() < 0) {
                continue;
            }
            astTreeTableNodes.add(new AstFieldTreeTableNode(fieldNode, parentNode, classNode));
        }
        Iterator<InnerClassNode> innerClassIterator = classNode.getInnerClasses();
        while (innerClassIterator.hasNext()) {
            InnerClassNode innerClassNode = innerClassIterator.next();
            astTreeTableNodes.add(new AstClassTreeTableNode((ClassNode) innerClassNode, parentNode));
        }
        return astTreeTableNodes;
    }

    public static List<AstTreeTableNode> getChildren(MethodNode methodNode, AstTreeTableNode parentNode,
            ClassNode scriptClass) throws Exception {
        return getChildren(methodNode.getCode(), parentNode, methodNode.getCode(), scriptClass);
    }

    public static List<AstTreeTableNode> getChildren(Statement statement, AstTreeTableNode parentNode,
            ASTNode parentObject, ClassNode scriptClass) throws Exception {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        if (statement instanceof BlockStatement) {
            astTreeTableNodes.addAll(getChildren((BlockStatement) statement, parentNode, parentObject, scriptClass));
        } else if (statement instanceof TryCatchStatement) {
            astTreeTableNodes.addAll(getChildren((TryCatchStatement) statement, parentNode, parentObject, scriptClass));
        } else {
            astTreeTableNodes.addAll(parseAstObjectIntoTreeTableNode(statement, parentNode, parentObject, scriptClass));
        }
        processDescriptionsForStatements(astTreeTableNodes);
        return astTreeTableNodes;
    }

    private static void processDescriptionsForStatements(List<AstTreeTableNode> astTreeTableNodes) {
        int i = 0;
        while (i < astTreeTableNodes.size() - 1) {
            if (astTreeTableNodes.get(i) instanceof AstCommentStatementTreeTableNode
                    && astTreeTableNodes.get(i).getASTObject() instanceof ExpressionStatement
                    && astTreeTableNodes.get(i + 1) instanceof AstStatementTreeTableNode
                    && !(astTreeTableNodes.get(i + 1) instanceof AstCommentStatementTreeTableNode)) {
                AstStatementTreeTableNode statementNode = (AstStatementTreeTableNode) astTreeTableNodes.get(i + 1);
                statementNode.setDescription((ExpressionStatement) astTreeTableNodes.get(i).getASTObject());
                astTreeTableNodes.remove(i);
                continue;
            } else {
                i++;
            }
        }
    }

    private static List<AstTreeTableNode> getChildren(TryCatchStatement tryCatchStatement, AstTreeTableNode parentNode,
            ASTNode parentObject, ClassNode scriptClass) {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        astTreeTableNodes
                .add(new AstTryStatementTreeTableNode(tryCatchStatement, parentNode, parentObject, scriptClass));
        for (CatchStatement catchStatement : tryCatchStatement.getCatchStatements()) {
            astTreeTableNodes.add(new AstCatchStatementTreeTableNode(catchStatement, parentNode, tryCatchStatement,
                    scriptClass));
        }
        if (tryCatchStatement.getFinallyStatement() != null
                && !(tryCatchStatement.getFinallyStatement() instanceof EmptyStatement)) {
            if (tryCatchStatement.getFinallyStatement() instanceof BlockStatement) {
                BlockStatement blockStatement = (BlockStatement) tryCatchStatement.getFinallyStatement();
                if (blockStatement.getStatements().size() == 1
                        && blockStatement.getStatements().get(0) instanceof BlockStatement) {
                    astTreeTableNodes.add(new AstFinallyStatementTreeTableNode(blockStatement.getStatements().get(0),
                            parentNode, tryCatchStatement, scriptClass));
                }
            } else {
                astTreeTableNodes.add(new AstFinallyStatementTreeTableNode(tryCatchStatement.getFinallyStatement(),
                        parentNode, tryCatchStatement, scriptClass));
            }
        }
        return astTreeTableNodes;
    }

    private static List<AstTreeTableNode> parseAstObjectIntoTreeTableNode(IfStatement ifStatement,
            AstTreeTableNode parentNode, boolean isElseIf, ASTNode parentObject, IfStatement rootIfStatement,
            ClassNode scriptClass) {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        if (!isElseIf) {
            AstIfStatementTreeTableNode ifStatementTreeTableNode = new AstIfStatementTreeTableNode(ifStatement,
                    parentNode, parentObject, scriptClass);
            astTreeTableNodes.add(ifStatementTreeTableNode);
        } else {
            AstElseIfStatementTreeTableNode elseIfStatementTreeTableNode = new AstElseIfStatementTreeTableNode(
                    ifStatement, parentNode, parentObject, rootIfStatement, scriptClass);
            astTreeTableNodes.add(elseIfStatementTreeTableNode);
        }

        if (ifStatement.getElseBlock() != null && !(ifStatement.getElseBlock() instanceof EmptyStatement)) {
            if (ifStatement.getElseBlock() instanceof IfStatement) {
                astTreeTableNodes.addAll(parseAstObjectIntoTreeTableNode((IfStatement) ifStatement.getElseBlock(),
                        parentNode, true, ifStatement, rootIfStatement, scriptClass));
            } else {
                astTreeTableNodes.add(new AstElseStatementTreeTableNode(ifStatement.getElseBlock(), parentNode,
                        ifStatement, rootIfStatement, scriptClass));
            }
        }
        return astTreeTableNodes;
    }

    private static List<AstTreeTableNode> getChildren(BlockStatement blockStatement, AstTreeTableNode parentNode,
            ASTNode parentObject, ClassNode scriptClass) throws Exception {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        for (Statement statement : blockStatement.getStatements()) {
            astTreeTableNodes
                    .addAll(parseAstObjectIntoTreeTableNode(statement, parentNode, blockStatement, scriptClass));
        }
        return astTreeTableNodes;
    }

    private static List<AstTreeTableNode> parseAstObjectIntoTreeTableNode(MethodNode methodNode, ClassNode classNode,
            AstTreeTableNode parentNode) {
        if (methodNode.getCode() instanceof BlockStatement) {
            List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
            BlockStatement blockStatement = (BlockStatement) methodNode.getCode();
            if (blockStatement.getStatements().size() == 1
                    && blockStatement.getStatements().get(0) instanceof ReturnStatement
                    && (((ReturnStatement) blockStatement.getStatements().get(0)).getExpression().getText()
                            .equals("null"))) {
                blockStatement.getStatements().clear();
            }
            if (methodNode.getName().equals(GROOVY_SCRIPT_RUN_METHOD_NAME) && classNode.isScript()) {
                astTreeTableNodes.add(new AstScriptMainBlockStatmentTreeTableNode(blockStatement, null, classNode,
                        classNode));
            } else {
                astTreeTableNodes.add(new AstMethodTreeTableNode(methodNode, parentNode, classNode));
            }
            return astTreeTableNodes;
        }
        return null;
    }

    public static List<AstTreeTableNode> parseAstObjectIntoTreeTableNode(Statement statement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) throws Exception {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<AstTreeTableNode>();
        if (statement instanceof ExpressionStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((ExpressionStatement) statement, parentNode,
                    parentObject, scriptClass));
        } else if (statement instanceof ForStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((ForStatement) statement, parentNode, parentObject,
                    scriptClass));
        } else if (statement instanceof WhileStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((WhileStatement) statement, parentNode, parentObject,
                    scriptClass));
        } else if (statement instanceof SwitchStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((SwitchStatement) statement, parentNode,
                    parentObject, scriptClass));
        } else if (statement instanceof CaseStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((CaseStatement) statement, parentNode, parentObject,
                    scriptClass));
        } else if (statement instanceof AssertStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((AssertStatement) statement, parentNode,
                    parentObject, scriptClass));
        } else if (statement instanceof IfStatement) {
            astTreeTableNodes.addAll(parseAstObjectIntoTreeTableNode((IfStatement) statement, parentNode, false,
                    parentObject, (IfStatement) statement, scriptClass));
        } else if (statement instanceof TryCatchStatement) {
            astTreeTableNodes.addAll(getChildren((TryCatchStatement) statement, parentNode, parentObject, scriptClass));
        } else if (statement instanceof BreakStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((BreakStatement) statement, parentNode, parentObject,
                    scriptClass));
        } else if (statement instanceof ContinueStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((ContinueStatement) statement, parentNode,
                    parentObject, scriptClass));
        } else if (statement instanceof ReturnStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((ReturnStatement) statement, parentNode,
                    parentObject, scriptClass));
        } else if (statement instanceof ThrowStatement) {
            astTreeTableNodes.add(parseAstObjectIntoTreeTableNode((ThrowStatement) statement, parentNode,
                    parentObject, scriptClass));
        } else if (!(statement instanceof EmptyStatement)
                && !(statement instanceof ReturnStatement && ((ReturnStatement) statement).getExpression() == null)) {
            astTreeTableNodes.add(new AstStatementTreeTableNode(statement, parentNode, parentObject, scriptClass));
        }
        return astTreeTableNodes;
    }

    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(ForStatement forStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
        return new AstForStatementTreeTableNode(forStatement, parentNode, parentObject, scriptClass);
    }

    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(WhileStatement whileStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
        return new AstWhileStatementTreeTableNode(whileStatement, parentNode, parentObject, scriptClass);
    }

    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(SwitchStatement switchStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
        return new AstSwitchStatementTreeTableNode(switchStatement, parentNode, parentObject, scriptClass);
    }

    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(CaseStatement caseStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
        if (parentObject instanceof SwitchStatement) {
            return new AstCaseStatementTreeTableNode(caseStatement, parentNode, (SwitchStatement) parentObject,
                    scriptClass);
        }
        return null;
    }

    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(AssertStatement assertStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
        return new AstAssertStatementTreeTableNode(assertStatement, parentNode, parentObject, scriptClass);
    }

    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(BreakStatement breakStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
        return new AstBreakStatementTreeTableNode(breakStatement, parentNode, parentObject, scriptClass);
    }

    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(ContinueStatement continueStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
        return new AstContinueStatementTreeTableNode(continueStatement, parentNode, parentObject, scriptClass);
    }

    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(ReturnStatement returnStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
        return new AstReturnStatementTreeTableNode(returnStatement, parentNode, parentObject, scriptClass);
    }
    
    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(ThrowStatement throwStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) {
        return new AstThrowStatementTreeTableNode(throwStatement, parentNode, parentObject, scriptClass);
    }

    private static AstTreeTableNode parseAstObjectIntoTreeTableNode(ExpressionStatement expressionStatement,
            AstTreeTableNode parentNode, ASTNode parentObject, ClassNode scriptClass) throws Exception {
        if (expressionStatement.getExpression() instanceof MethodCallExpression) {
            AstAbstractKeywordTreeTableNode keywordNode = null;
            if (isBuiltInKeywordMethodCall((MethodCallExpression) expressionStatement.getExpression())) {
                MethodCallExpression methodCallExpression = (MethodCallExpression) expressionStatement.getExpression();
                if (methodCallExpression.getMethodAsString()
                        .equals(BuiltInMethodNodeFactory.CALL_TEST_CASE_METHOD_NAME)) {
                    keywordNode = new AstCallTestCaseKeywordTreeTableNode(expressionStatement, parentNode,
                            parentObject, scriptClass);
                } else {
                    keywordNode = new AstBuiltInKeywordTreeTableNode(expressionStatement, parentNode, parentObject,
                            scriptClass);
                }
            } else if (isCustomKeywordMethodCall((MethodCallExpression) expressionStatement.getExpression())) {
                keywordNode = new AstCustomKeywordTreeTableNode(expressionStatement, parentNode, parentObject,
                        scriptClass);
            }
            if (keywordNode != null) {
                keywordNode.generateArguments();
                return keywordNode;
            }
            return new AstMethodCallStatementTreeTableNode(expressionStatement, parentNode, parentObject, scriptClass);
        } else if (expressionStatement.getExpression() instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) expressionStatement.getExpression();
            if (binaryExpression.getRightExpression() instanceof MethodCallExpression) {
                AstAbstractKeywordTreeTableNode keywordNode = null;
                if (isBuiltInKeywordMethodCall((MethodCallExpression) binaryExpression.getRightExpression())) {
                    keywordNode = new AstBuiltInKeywordTreeTableNode(expressionStatement, parentNode, parentObject,
                            scriptClass);
                } else if (isCustomKeywordMethodCall((MethodCallExpression) binaryExpression.getRightExpression())) {
                    keywordNode = new AstCustomKeywordTreeTableNode(expressionStatement, parentNode, parentObject,
                            scriptClass);
                }
                if (keywordNode != null) {
                    keywordNode.generateArguments();
                    return keywordNode;
                }
            }
            return new AstBinaryStatementTreeTableNode(expressionStatement, parentNode, parentObject, scriptClass);
        } else if (expressionStatement.getExpression() instanceof ConstantExpression) {
            ConstantExpression constantExpression = (ConstantExpression) expressionStatement.getExpression();
            if (constantExpression.getValue() instanceof String) {
                return new AstCommentStatementTreeTableNode(expressionStatement, parentNode, parentObject, scriptClass);
            }
        }
        return new AstStatementTreeTableNode(expressionStatement, parentNode, parentObject, scriptClass);
    }

    public static boolean isBuiltInKeywordMethodCall(MethodCallExpression methodCallExpression) {
        for (Class<?> clazz : KeywordController.getInstance().getBuiltInKeywordClasses()) {
            if (methodCallExpression.getObjectExpression().getText().equals(clazz.getName())
                    || methodCallExpression.getObjectExpression().getText().equals(clazz.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCustomKeywordMethodCall(MethodCallExpression methodCallExpression) {
        if (methodCallExpression.getObjectExpression().getText().equals("CustomKeywords")) {
            return true;
        }
        return false;
    }

    public static int getIndex(ASTNode parentObject, ASTNode childObject) {
        if (parentObject instanceof Statement && childObject instanceof Statement) {
            return getIndex((Statement) parentObject, (Statement) childObject);
        } else if (parentObject instanceof ClassNode) {
            if (childObject instanceof MethodNode) {
                return getIndex((ClassNode) parentObject, (MethodNode) childObject);
            } else if (childObject instanceof FieldNode) {
                return getIndex((ClassNode) parentObject, (FieldNode) childObject);
            }
        }
        return -1;
    }

    private static int getIndex(Statement parentStatement, Statement childStatement) {
        if (parentStatement instanceof BlockStatement) {
            return getIndex((BlockStatement) parentStatement, childStatement);
        } else if (parentStatement instanceof SwitchStatement && childStatement instanceof CaseStatement) {
            return getIndex((SwitchStatement) parentStatement, (CaseStatement) childStatement);
        } else if (parentStatement instanceof TryCatchStatement && childStatement instanceof CatchStatement) {
            return getIndex((TryCatchStatement) parentStatement, (CatchStatement) childStatement);
        }
        return -1;
    }

    private static int getIndex(SwitchStatement parentSwitchStatement, CaseStatement caseStatement) {
        return parentSwitchStatement.getCaseStatements().indexOf(caseStatement);
    }

    private static int getIndex(TryCatchStatement tryCatchStatement, CatchStatement catchStatement) {
        return tryCatchStatement.getCatchStatements().indexOf(catchStatement);
    }

    private static int getIndex(BlockStatement parentBlockStatement, Statement childStatement) {
        return parentBlockStatement.getStatements().indexOf(childStatement);
    }

    private static int getIndex(ClassNode classNode, MethodNode methodNode) {
        return classNode.getMethods().indexOf(methodNode);
    }

    private static int getIndex(ClassNode classNode, FieldNode fieldNode) {
        return classNode.getFields().indexOf(fieldNode);
    }

    public static void addChild(ASTNode parentObject, ASTNode childObject, int index) {
        if (parentObject instanceof Statement && childObject instanceof Statement) {
            addChild((Statement) parentObject, (Statement) childObject, index);
        } else if (parentObject instanceof ClassNode) {
            if (childObject instanceof MethodNode) {
                addChild((ClassNode) parentObject, (MethodNode) childObject, index);
            } else if (childObject instanceof FieldNode) {
                addChild((ClassNode) parentObject, (FieldNode) childObject, index);
            }
        }
    }

    private static void addChild(Statement parentStatement, Statement childStatement, int index) {
        if (parentStatement instanceof BlockStatement) {
            addChild((BlockStatement) parentStatement, childStatement, index);
        } else if (parentStatement instanceof SwitchStatement && childStatement instanceof CaseStatement) {
            addChild((SwitchStatement) parentStatement, (CaseStatement) childStatement, index);
        } else if (parentStatement instanceof TryCatchStatement && childStatement instanceof CatchStatement) {
            addChild((TryCatchStatement) parentStatement, (CatchStatement) childStatement, index);
        }
    }

    private static void addChild(SwitchStatement parentSwitchStatement, CaseStatement caseStatement, int index) {
        if (index >= 0 && index < parentSwitchStatement.getCaseStatements().size()) {
            parentSwitchStatement.getCaseStatements().add(index, caseStatement);
        } else {
            parentSwitchStatement.getCaseStatements().add(caseStatement);
        }
    }

    private static void addChild(TryCatchStatement tryCatchStatement, CatchStatement catchStatement, int index) {
        if (index >= 0 && index < tryCatchStatement.getCatchStatements().size()) {
            tryCatchStatement.getCatchStatements().add(index, catchStatement);
        } else {
            tryCatchStatement.getCatchStatements().add(catchStatement);
        }
    }

    private static void addChild(BlockStatement parentBlockStatement, Statement childStatement, int index) {
        if (index >= 0 && index < parentBlockStatement.getStatements().size()) {
            parentBlockStatement.getStatements().add(index, childStatement);
        } else {
            parentBlockStatement.getStatements().add(childStatement);
        }
    }

    private static void addChild(ClassNode classNode, MethodNode methodNode, int index) {
        if (index >= 0 && index < classNode.getMethods().size()) {
            classNode.getMethods().add(index, methodNode);
        } else {
            classNode.getMethods().add(methodNode);
        }
    }

    private static void addChild(ClassNode classNode, FieldNode fieldNode, int index) {
        if (index >= 0 && index < classNode.getFields().size()) {
            classNode.getFields().add(index, fieldNode);
        } else {
            classNode.getFields().add(fieldNode);
        }
    }

    public static void removeChild(ASTNode parentObject, ASTNode childObject) {
        if (parentObject instanceof Statement && childObject instanceof Statement) {
            removeChild((Statement) parentObject, (Statement) childObject);
        } else if (parentObject instanceof ClassNode) {
            if (childObject instanceof MethodNode) {
                removeChild((ClassNode) parentObject, (MethodNode) childObject);
            } else if (childObject instanceof FieldNode) {
                removeChild((ClassNode) parentObject, (FieldNode) childObject);
            }
        }
    }

    private static void removeChild(Statement parentStatement, Statement childStatement) {
        if (parentStatement instanceof BlockStatement) {
            removeChild((BlockStatement) parentStatement, childStatement);
        } else if (parentStatement instanceof SwitchStatement && childStatement instanceof CaseStatement) {
            removeChild((SwitchStatement) parentStatement, (CaseStatement) childStatement);
        } else if (parentStatement instanceof TryCatchStatement && childStatement instanceof CatchStatement) {
            removeChild((TryCatchStatement) parentStatement, (CatchStatement) childStatement);
        }
    }

    private static void removeChild(SwitchStatement parentSwitchStatement, CaseStatement caseStatement) {
        parentSwitchStatement.getCaseStatements().remove(caseStatement);
    }

    private static void removeChild(TryCatchStatement tryCatchStatement, CatchStatement catchStatement) {
        tryCatchStatement.getCatchStatements().remove(catchStatement);
    }

    private static void removeChild(BlockStatement parentBlockStatement, Statement childStatement) {
        parentBlockStatement.getStatements().remove(childStatement);
    }

    private static void removeChild(ClassNode classNode, MethodNode methodNode) {
        classNode.getMethods().remove(methodNode);
    }

    private static void removeChild(ClassNode classNode, FieldNode fieldNode) {
        classNode.getFields().remove(fieldNode);
    }

    public static void removeChild(ClassNode classNode, MethodNode methodNode, int newIndex) {
        int oldIndex = classNode.getMethods().indexOf(methodNode);
        if (oldIndex >= 0 && oldIndex < classNode.getMethods().size() && newIndex >= 0
                && newIndex < classNode.getMethods().size()) {
            classNode.getMethods().remove(oldIndex);
            classNode.getMethods().add(newIndex, methodNode);
        }
    }

    public static void removeChild(ClassNode classNode, FieldNode fieldNode, int newIndex) {
        int oldIndex = classNode.getFields().indexOf(fieldNode);
        if (oldIndex >= 0 && oldIndex < classNode.getFields().size() && newIndex >= 0
                && newIndex < classNode.getFields().size()) {
            classNode.getFields().remove(oldIndex);
            classNode.getFields().add(newIndex, fieldNode);
        }
    }

    public static void moveChild(ASTNode parentObject, ASTNode childObject, int newIndex) {
        if (parentObject instanceof Statement && childObject instanceof Statement) {
            moveChild((Statement) parentObject, (Statement) childObject, newIndex);
        } else if (parentObject instanceof ClassNode) {
            if (childObject instanceof MethodNode) {
                moveChild((ClassNode) parentObject, (MethodNode) childObject, newIndex);
            } else if (childObject instanceof FieldNode) {
                moveChild((ClassNode) parentObject, (FieldNode) childObject, newIndex);
            }
        }
    }

    private static void moveChild(Statement parentStatement, Statement childStatement, int newIndex) {
        if (parentStatement instanceof BlockStatement) {
            moveChild((BlockStatement) parentStatement, childStatement, newIndex);
        } else if (parentStatement instanceof SwitchStatement && childStatement instanceof CaseStatement) {
            moveChild((SwitchStatement) parentStatement, (CaseStatement) childStatement, newIndex);
        } else if (parentStatement instanceof TryCatchStatement && childStatement instanceof CatchStatement) {
            moveChild((TryCatchStatement) parentStatement, (CatchStatement) childStatement, newIndex);
        }
    }

    private static void moveChild(TryCatchStatement tryCatchStatement, CatchStatement catchStatement, int newIndex) {
        int oldIndex = tryCatchStatement.getCatchStatements().indexOf(catchStatement);
        if (oldIndex >= 0 && oldIndex < tryCatchStatement.getCatchStatements().size() && newIndex >= 0
                && newIndex < tryCatchStatement.getCatchStatements().size()) {
            tryCatchStatement.getCatchStatements().remove(oldIndex);
            tryCatchStatement.getCatchStatements().add(newIndex, catchStatement);
        }
    }

    private static void moveChild(BlockStatement parentBlockStatement, Statement childStatement, int newIndex) {
        int oldIndex = parentBlockStatement.getStatements().indexOf(childStatement);
        if (oldIndex >= 0 && oldIndex < parentBlockStatement.getStatements().size() && newIndex >= 0
                && newIndex < parentBlockStatement.getStatements().size()) {
            parentBlockStatement.getStatements().remove(oldIndex);
            parentBlockStatement.getStatements().add(newIndex, childStatement);
        }
    }
}
