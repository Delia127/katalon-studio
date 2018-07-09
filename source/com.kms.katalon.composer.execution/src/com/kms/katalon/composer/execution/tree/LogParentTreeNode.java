package com.kms.katalon.composer.execution.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.util.internal.DateUtil;

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
            return DateUtil.SECOND_FORMAT.format(elapsedSeconds) + "s";
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

    @Override
    public String getIndexString() {
        if (!record.getMessage().startsWith(com.kms.katalon.core.constants.StringConstants.LOG_START_KEYWORD)) {
            return "";
        }
        String parentIdxAsString = getParent().getIndexString();
        if (!StringUtils.isBlank(parentIdxAsString)) {
            parentIdxAsString += ".";
        }
        return parentIdxAsString + getIndex();
    }

    public int getIndex() {
        int stepIndex = record.getIndex();
        if (stepIndex < 0) {
            stepIndex = 1;
            for (ILogTreeNode siblingNode : getParent().getChildren()) {
                if (siblingNode instanceof ILogParentTreeNode) {
                    if (siblingNode.equals(this)) {
                        break;
                    } else {
                        stepIndex++;
                    }
                }
            }
        }
        return stepIndex;
    }
}
