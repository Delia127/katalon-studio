package com.kms.katalon.composer.testcase.groovy.ast;

import groovy.lang.Script;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Comment;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.stmt.BlockStatement;

import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;

public class ScriptNodeWrapper extends ClassNodeWrapper implements ASTHasBlock {
    private BlockStatementWrapper mainBlock;

    private String testCaseId;

    public ScriptNodeWrapper(String testCaseId) {
        super(Script.class, null);
        mainBlock = new BlockStatementWrapper(this);
        this.testCaseId = testCaseId;
    }

    public ScriptNodeWrapper(String testCaseId, ClassNode scriptClass) {
        super(scriptClass, null);
        this.testCaseId = testCaseId;
        for (MethodNode methodNode : scriptClass.getMethods()) {
            if (methodNode.getLineNumber() < 0 || !methodNode.getName().equals("run")
                    || !(methodNode.getCode() instanceof BlockStatement)) {
                continue;
            }
            mainBlock = new BlockStatementWrapper((BlockStatement) methodNode.getCode(), this);
        }
        List<CommentWrapper> commentWrappers = new ArrayList<CommentWrapper>();
        for (Comment comment : scriptClass.getModule().getContext().getComments()) {
            commentWrappers.add(new CommentWrapper(comment, this));
        }
        setInsideComments(commentWrappers);
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
}
