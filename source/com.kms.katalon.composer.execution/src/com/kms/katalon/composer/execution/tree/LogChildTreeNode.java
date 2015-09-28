package com.kms.katalon.composer.execution.tree;

import com.kms.katalon.core.logging.XmlLogRecord;

public class LogChildTreeNode implements ILogTreeNode {

    protected XmlLogRecord record;
    protected ILogParentTreeNode parentTreeNode;

    public LogChildTreeNode(ILogParentTreeNode parent, XmlLogRecord record) {
        this.record = record;
        this.parentTreeNode = parent;
    }

    @Override
    public String getMessage() {
        return record.getMessage();
    }

    @Override
    public ILogParentTreeNode getParent() {
        return parentTreeNode;
    }

    @Override
    public String getIndexString() {
        if (!record.getMessage().startsWith(com.kms.katalon.core.constants.StringConstants.LOG_START_KEYWORD)) {
            return "";
        }
        int stepIndex = record.getIndex();
        if (stepIndex == -1) {
            if (getParent() != null) {
                stepIndex = getParent().getChildren().indexOf(this);
            } else {
                return "";
            }
        }
        return (getParent() == null ? "" : (getParent().getIndexString().isEmpty() ? "" : getParent().getIndexString() + ".")) + String.valueOf(stepIndex);
    }

}
