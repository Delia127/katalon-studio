package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.Comment;

public class CommentWrapper extends ASTNodeWrapper {
    private String comment;
    private boolean isMultiLine = false;

    public CommentWrapper(CommentWrapper commentWrapper, ASTNodeWrapper parentNode) {
        super(commentWrapper, parentNode);
        this.comment = commentWrapper.getComment();
        this.isMultiLine = commentWrapper.isMultiLine();
    }

    public CommentWrapper(Comment comment, ASTNodeWrapper parentNode) {
        super(parentNode);
        this.comment = comment.toString();
        this.lineNumber = comment.sline;
        this.columnNumber = comment.scol;
        this.lastLineNumber = comment.eline;
        this.lastColumnNumber = comment.ecol;
        isMultiLine = comment.getClass().getName().equals("org.codehaus.groovy.ast.MultiLineComment");
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getText() {
        return comment;
    }

    @Override
    public boolean hasAstChildren() {
        return false;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        return Collections.emptyList();
    }

    public boolean isMultiLine() {
        return isMultiLine;
    }

    public void setMultiLine(boolean isMultiLine) {
        this.isMultiLine = isMultiLine;
    }

    @Override
    public CommentWrapper clone() {
        return new CommentWrapper(this, getParent());
    }
}
