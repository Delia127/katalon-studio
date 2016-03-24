package com.kms.katalon.composer.execution.tree;

import java.util.List;

import com.kms.katalon.core.logging.XmlLogRecord;

public interface ILogParentTreeNode extends ILogTreeNode {
	String getElapsedTime();
	String getFullElapsedTime();
	XmlLogRecord getResult();
	List<ILogTreeNode> getChildren();
	XmlLogRecord getRecordEnd();
	XmlLogRecord getRecordStart();
	void addChild(ILogTreeNode childNode);
	void setRecordEnd(XmlLogRecord recordEnd);
	void setResult(XmlLogRecord result);
    String getIndexString();
}
