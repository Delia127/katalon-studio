package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Comment;

import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;

import groovy.lang.Script;

public class ScriptNodeWrapper extends ClassNodeWrapper implements ASTHasBlock {

    private BlockStatementWrapper mainBlock;

    private String testCaseId;

    public ScriptNodeWrapper() {
        super(Script.class, null);
        mainBlock = new BlockStatementWrapper(this);
    }

    public ScriptNodeWrapper(ClassNode scriptClass) {
        super(scriptClass, null);
        mainBlock = getRunMethod().getBlock();
        List<CommentWrapper> commentWrappers = new ArrayList<CommentWrapper>();
        for (Comment comment : scriptClass.getModule().getContext().getComments()) {
            commentWrappers.add(new CommentWrapper(comment, this));
        }
        setInsideComments(commentWrappers);
    }

    public ScriptNodeWrapper(ScriptNodeWrapper scriptNodeWrapper) {
        super(scriptNodeWrapper, null);
        mainBlock = getRunMethod().getBlock();
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(getImports());
        astNodeWrappers.add(mainBlock);
        astNodeWrappers.addAll(fields);
        astNodeWrappers.addAll(methods);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return mainBlock;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setMainBlock(BlockStatementWrapper mainBlock) {
        this.mainBlock = mainBlock;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    @Override
    public ScriptNodeWrapper clone() {
        return new ScriptNodeWrapper(this);
    }

    public MethodNodeWrapper getRunMethod() {
        for (MethodNodeWrapper methodNode : getMethods()) {
            if (methodNode.getName().equals(RUN_METHOD_NAME)) {
                return methodNode;
            }
        }
        return null;
    }
}
