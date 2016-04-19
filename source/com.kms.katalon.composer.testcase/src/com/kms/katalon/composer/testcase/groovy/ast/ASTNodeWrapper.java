package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;

import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;

/**
 * Wrapper to convert a groovy ast object into editable object that can be parsed into groovy script
 *
 */
public abstract class ASTNodeWrapper {
    protected ASTNodeWrapper parentNodeWrapper;

    protected List<CommentWrapper> preceddingComments = new ArrayList<CommentWrapper>();

    protected List<CommentWrapper> followingComments;

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
        if (nodeWrapper.preceddingComments != null) {
            getPreceddingComments().addAll(nodeWrapper.getPreceddingComments());
        }
        if (nodeWrapper.followingComments != null) {
            getFollowingComments().addAll(nodeWrapper.getFollowingComments());
        }
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
        if (preceddingComments == null) {
            preceddingComments = new ArrayList<CommentWrapper>();
        }
        return preceddingComments;
    }

    public void setPreceddingComments(List<CommentWrapper> preceddingComments) {
        for (CommentWrapper commentWrapper : preceddingComments) {
            commentWrapper.setParent(this);
        }
        getPreceddingComments().addAll(preceddingComments);
    }

    public List<CommentWrapper> getFollowingComments() {
        if (followingComments == null) {
            followingComments = new ArrayList<CommentWrapper>();
        }
        return followingComments;
    }

    public void setFollowingComments(List<CommentWrapper> followingComments) {
        for (CommentWrapper commentWrapper : followingComments) {
            commentWrapper.setParent(this);
        }
        getFollowingComments().addAll(followingComments);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public int getLastLineNumber() {
        return lastLineNumber;
    }

    public int getLastColumnNumber() {
        return lastColumnNumber;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
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
                if (isLeadingComment(childNode, commentWrapper)) {
                    preceddingCommentWrappers.add(commentWrapper);
                    commentWrapperList.remove(commentWrapper);
                    continue;
                } else if (containsComment(childNode, commentWrapper)) {
                    insideCommentWrappers.add(commentWrapper);
                    commentWrapperList.remove(commentWrapper);
                    continue;
                } else if (isInlineTrailingComment(childNode, commentWrapper)) {
                    if (index < childNodes.size() - 1) {
                        ASTNodeWrapper nextChildNode = childNodes.get(index + 1);
                        if (containsComment(nextChildNode, commentWrapper)
                                || isInlineTrailingComment(nextChildNode, commentWrapper)) {
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
    
    private static boolean containsComment(ASTNodeWrapper astObject, CommentWrapper comment) {
        return ((astObject.getLineNumber() < comment.getLineNumber() || (astObject.getLineNumber() == comment
                .getLineNumber() && astObject.getColumnNumber() < comment.getColumnNumber())) && (astObject
                .getLastLineNumber() > comment.getLastLineNumber() || (astObject.getLastLineNumber() == comment
                .getLastLineNumber() && astObject.getLastColumnNumber() >= comment.getLastColumnNumber())));
    }

    private static boolean isInlineTrailingComment(ASTNodeWrapper astObject, CommentWrapper comment) {
        return (astObject.getLastLineNumber() == comment.getLineNumber() && astObject.getLastColumnNumber() <= comment
                .getColumnNumber());
    }

    private static boolean isLeadingComment(ASTNodeWrapper astObject, CommentWrapper comment) {
        return (astObject.getLineNumber() > comment.getLastLineNumber())
                || (astObject.getLineNumber() == comment.getLastLineNumber() && astObject.getColumnNumber() >= comment
                        .getLastColumnNumber());
    }

    @Override
    public String toString() {
        return getText();
    }
    
    @Override
    public abstract ASTNodeWrapper clone();

    public ASTNodeWrapper copy(ASTNodeWrapper newParent) {
        ASTNodeWrapper newInstance = clone();
        newInstance.setParent(newParent);
        return newInstance;
    }

    /**
     * Text that can be use to display the object for preview
     */
    public abstract String getText();

    /**
     * Return true if this node has child ast nodes; otherwise false
     */
    public abstract boolean hasAstChildren();

    /**
     * Return the child ast nodes of this nodes
     */
    public abstract List<? extends ASTNodeWrapper> getAstChildren();

    /**
     * Check if the input of the ast object can be edit. Default implementation set to false.
     */
    public boolean isInputEditatble() {
        return false;
    }

    /**
     * Get input for editing, by default it's immutable
     * 
     * @return input for editing
     */
    public ASTNodeWrapper getInput() {
        return null;
    }

    /**
     * Get input text for preview
     * 
     * @return input text for preview
     */
    public String getInputText() {
        return "";
    }

    /**
     * Update input for ast node
     * 
     * @param input for an ast node
     * @return true if update successfully; otherwise false
     */
    public boolean updateInputFrom(ASTNodeWrapper input) {
        return false;
    }

    /**
     * Compare this ast node wrapper with another ast node wrapper as raw scripts
     * 
     * @param anotherNode
     * @return true if raw scripts of the two node wrappers is equal; otherwise false
     */
    public boolean isEqualsTo(ASTNodeWrapper anotherNode) {
        if (anotherNode == null) {
            return false;
        }
        StringBuilder stringBuilder = new StringBuilder();
        new GroovyWrapperParser(stringBuilder).parse(this);
        String scriptValue = stringBuilder.toString();

        stringBuilder = new StringBuilder();
        new GroovyWrapperParser(stringBuilder).parse(anotherNode);
        String anotherScriptValue = stringBuilder.toString();
        return scriptValue.equals(anotherScriptValue);
    }

    /**
     * Check if the input ast node can be added as a child to this node. Default implementation return false.
     * 
     * @param astNode
     * @return true if the input ast node can be added as a child to this node; otherwise false
     */
    public boolean isChildAssignble(ASTNodeWrapper astNode) {
        return false;
    }

    /**
     * Add the child ast node into this node. Default implementation do nothing and return false;
     * 
     * @param childObject
     * @return true if adding child ast node successfully; otherwise false
     */
    public boolean addChild(ASTNodeWrapper childObject) {
        return false;
    }

    /**
     * Add the child ast node into this node at the specific index. Default implementation do nothing and return false;
     * 
     * @param childObject
     * @param index
     * @return true if adding child ast node successfully; otherwise false
     */
    public boolean addChild(ASTNodeWrapper childObject, int index) {
        return false;
    }

    /**
     * Remove a ast node into from this node. Default implementation do nothing and return false;
     * 
     * @param childObject
     * @return true if removing child ast node successfully; otherwise false
     */
    public boolean removeChild(ASTNodeWrapper childObject) {
        return false;
    }

    /**
     * Get the index of the child ast node. Default implementation do nothing and return -1;
     * 
     * @param index
     * @return the index of the child ast node; if not found then return -1
     */
    public int indexOf(ASTNodeWrapper childObject) {
        return -1;
    }
    
    /**
     * Replace a child with a new child
     * @param oldChild
     * @param newChild
     * @return true if replace successfuly; otherwise false
     */
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        return false;
    }
}
