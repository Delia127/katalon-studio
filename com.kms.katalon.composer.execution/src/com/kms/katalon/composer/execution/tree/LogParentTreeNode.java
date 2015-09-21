package com.kms.katalon.composer.execution.tree;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.components.util.DateUtil;
import com.kms.katalon.core.logging.XmlLogRecord;

public class LogParentTreeNode extends LogChildTreeNode implements ILogParentTreeNode {
	
	private XmlLogRecord recordStart;
	private XmlLogRecord recordEnd;
	private List<ILogTreeNode> children;
	private XmlLogRecord recordResult;
	
	public LogParentTreeNode(ILogParentTreeNode parentTreeNode, XmlLogRecord recordStart) {
	    super(parentTreeNode, recordStart);
		this.recordStart = recordStart;
		children = new ArrayList<ILogTreeNode>();
	}

	@Override
	public XmlLogRecord getResult() {		
		return recordResult;
	}
	
	@Override
	public void setResult(XmlLogRecord result) {		
		recordResult = result;
	}

	@Override
	public String getMessage() {
		int index = recordStart.getMessage().indexOf(":");
		return recordStart.getMessage().substring(index + 1).trim();
	}

	@Override
	public List<ILogTreeNode> getChildren() {
		if (children == null) {
			children = new ArrayList<ILogTreeNode>();
		}
		return children;
	}

	public XmlLogRecord getRecordEnd() {
		return recordEnd;
	}

	public void setRecordEnd(XmlLogRecord recordEnd) {
		this.recordEnd = recordEnd;
	}

	@Override
	public String getElapsedTime() {
		if (recordEnd == null) {
			return "";
		} else {
			double elapsedSeconds = ((double) (recordEnd.getMillis() - recordStart.getMillis())) / 1000;
			return Double.toString(elapsedSeconds) + " s";
		}		
	}

	@Override
	public void addChild(ILogTreeNode childNode) {
		children.add(childNode);
	}
	
	public XmlLogRecord getRecordStart() {
		return recordStart;
	}

	@Override
	public String getFullElapsedTime() {
		if (recordEnd == null) {
			return "";
		} else {
			return DateUtil.getElapsedTime(recordStart.getMillis(), recordEnd.getMillis());
		}		
	}
}
