package com.kms.katalon.composer.execution.tree;

import com.kms.katalon.core.logging.XmlLogRecord;

public interface ILogTreeNode {
	String getMessage();
	ILogParentTreeNode getParent();
	XmlLogRecord getLogRecord();
}
