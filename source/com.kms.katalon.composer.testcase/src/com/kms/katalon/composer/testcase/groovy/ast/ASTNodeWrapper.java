package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;

/**
 * Wrapper to convert a groovy ast object into editable object that can be parsed into groovy script
 *
 */
public abstract class ASTNodeWrapper {
    protected ASTNodeWrapper parentNodeWrapper;
    protected List<CommentWrapper> preceddingComments = new ArrayList<CommentWrapper>();
    protected List<CommentWrapper> followingComments = new ArrayList<CommentWrapper>();
    protected int lineNumber = -1;
    protected int columnNumber = -1;
    protected int lastLineNumber = -1;
    protected int lastColumnNumber = -1;
    protected int start = 0;
    protected int end = 0;

    public ASTNodeWrapper(ASTNodeWrapper parentNodeWrapper) {
        this(new ASTNode(), parentNodeWrapper);
    }

    public ASTNodeWrapper(ASTNodeWrapper nodeWrapper, ASTNodeWrapper parentNodeWrapper) {
        this.parentNodeWrapper = parentNodeWrapper;
        copyProperties(nodeWrapper);
    }

    public ASTNodeWrapper(ASTNode node, ASTNodeWrapper parentNodeWrapper) {
        this.parentNodeWrapper = parentNodeWrapper;
        copyProperties(node);
    }

    public void copyProperties(ASTNodeWrapper nodeWrapper) {
        if (nodeWrapper == null) {
            return;
        }
        this.lineNumber = nodeWrapper.getLineNumber();
        this.columnNumber = nodeWrapper.getColumnNumber();
        this.lastLineNumber = nodeWrapper.getLastLineNumber();
        this.lastColumnNumber = nodeWrapper.getLastColumnNumber();
        this.start = nodeWrapper.getStart();
        this.end = nodeWrapper.getEnd();
        this.preceddingComments.addAll(nodeWrapper.getPreceddingComments());
        this.followingComments.addAll(nodeWrapper.getFollowingComments());
    }

    protected void copyProperties(ASTNode node) {
        if (node == null) {
            return;
        }
        this.lineNumber = node.getLineNumber();
        this.columnNumber = node.getColumnNumber();
        this.lastLineNumber = node.getLastLineNumber();
        this.lastColumnNumber = node.getLastColumnNumber();
        this.start = node.getStart();
        this.end = node.getEnd();
    }

    public ASTNodeWrapper getParent() {
        return parentNodeWrapper;
    }

    public void setParent(ASTNodeWrapper parentNodeWrapper) {
        this.parentNodeWrapper = parentNodeWrapper;
    }

    public ScriptNodeWrapper getScriptClass() {
        ASTNodeWrapper candidate = this;

        while (candidate != null && candidate.getParent() != null) {
            candidate = candidate.getParent();
        }
        if (candidate instanceof ScriptNodeWrapper) {
            return (ScriptNodeWrapper) candidate;
        }
        return null;
    }

    public List<CommentWrapper> getPreceddingComments() {
        return preceddingComments;
    }

    public void setPreceddingComments(List<CommentWrapper> preceddingComments) {
        for (CommentWrapper commentWrapper : preceddingComments) {
            commentWrapper.setParent(this);
        }
        this.preceddingComments.addAll(preceddingComments);
    }

    public List<CommentWrapper> getFollowingComments() {
        return followingComments;
    }

    public void setFollowingComments(List<CommentWrapper> followingComments) {
        for (CommentWrapper commentWrapper : followingComments) {
            commentWrapper.setParent(this);
        }
        this.followingComments.addAll(followingComments);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public int getLastLineNumber() {
        return lastLineNumber;
    }

    public void setLastLineNumber(int lastLineNumber) {
        this.lastLineNumber = lastLineNumber;
    }

    public int getLastColumnNumber() {
        return lastColumnNumber;
    }

    public void setLastColumnNumber(int lastColumnNumber) {
        this.lastColumnNumber = lastColumnNumber;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getLength() {
        return end >= 0 && start >= 0 ? end - start : -1;
    }

    /**
     * Recursively set inside comments
     */
    public void setInsideComments(List<CommentWrapper> commentWrapperList) {
        if (!hasAstChildren() || commentWrapperList.isEmpty()) {
            return;
        }
        List<? extends ASTNodeWrapper> childNodes = getAstChildren();
        for (int index = 0; index < childNodes.size(); index++) {
            ASTNodeWrapper childNode = childNodes.get(index);
            if (commentWrapperList.isEmpty()) {
                break;
            }
            List<CommentWrapper> insideCommentWrappers = new ArrayList<CommentWrapper>();
            List<CommentWrapper> preceddingCommentWrappers = new ArrayList<CommentWrapper>();
            List<CommentWrapper> followingCommentWrappers = new ArrayList<CommentWrapper>();
            int commentCount = 0;
            while (commentCount < commentWrapperList.size()) {
                CommentWrapper commentWrapper = commentWrapperList.get(commentCount);
                if (ASTNodeWrapHelper.isLeadingComment(childNode, commentWrapper)) {
                    preceddingCommentWrappers.add(commentWrapper);
                    commentWrapperList.remove(commentWrapper);
                    continue;
                } else if (ASTNodeWrapHelper.containsComment(childNode, commentWrapper)) {
                    insideCommentWrappers.add(commentWrapper);
                    commentWrapperList.remove(commentWrapper);
                    continue;
                } else if (ASTNodeWrapHelper.isInlineTrailingComment(childNode, commentWrapper)) {
                    if (index < childNodes.size() - 1) {
                        ASTNodeWrapper nextChildNode = childNodes.get(index + 1);
                        if (ASTNodeWrapHelper.containsComment(nextChildNode, commentWrapper)
                                || ASTNodeWrapHelper.isInlineTrailingComment(nextChildNode, commentWrapper)) {
                            commentCount++;
                            continue;
                        }
                    }
                    followingCommentWrappers.add(commentWrapper);
                    commentWrapperList.remove(commentWrapper);
                    continue;
                } else if (index == childNodes.size() - 1) {
                    followingCommentWrappers.add(commentWrapper);
                    commentWrapperList.remove(commentWrapper);
                    continue;
                }
                commentCount++;
            }
            childNode.setPreceddingComments(preceddingCommentWrappers);
            childNode.setInsideComments(insideCommentWrappers);
            childNode.setFollowingComments(followingCommentWrappers);
        }
    }

    @Override
    public String toString() {
        return getText();
    }

    /**
     * Text that can be use to display the object for preview
     */
    public abstract String getText();

    public abstract boolean hasAstChildren();

    public abstract List<? extends ASTNodeWrapper> getAstChildren();
}
