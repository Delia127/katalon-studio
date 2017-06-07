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
    public XmlLogRecord getLogRecord() {
        return record;
    }

}
