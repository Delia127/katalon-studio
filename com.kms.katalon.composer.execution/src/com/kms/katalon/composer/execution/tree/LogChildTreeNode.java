package com.kms.katalon.composer.execution.tree;

import com.kms.katalon.core.logging.XmlLogRecord;

public class LogChildTreeNode implements ILogTreeNode {
	
	private XmlLogRecord record;
	private ILogParentTreeNode parent;
	
	public LogChildTreeNode(ILogParentTreeNode parent, XmlLogRecord record) {
		this.record = record;
		this.parent = parent;
	}

	@Override
	public String getMessage() {
		return record.getMessage();
	}

	@Override
	public ILogParentTreeNode getParent() {
		return parent;
	}

}
