package com.kms.katalon.composer.execution.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.tree.ILogParentTreeNode;
import com.kms.katalon.composer.execution.tree.ILogTreeNode;
import com.kms.katalon.composer.execution.tree.LogChildTreeNode;
import com.kms.katalon.composer.execution.tree.LogParentTreeNode;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;

public class LogRecordTreeViewer extends TreeViewer {

    private ILogParentTreeNode currentParentTreeNode;
    private List<ILogParentTreeNode> rootNodes;
    private IEventBroker eventBroker;
    private IPreferenceStore store;

    public LogRecordTreeViewer(Composite parent, int style, IEventBroker eventBroker) {
        super(parent, style);
        rootNodes = new ArrayList<ILogParentTreeNode>();
        this.eventBroker = eventBroker;
        store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
    }

    public void reset(List<XmlLogRecord> records) {
        currentParentTreeNode = null;
        rootNodes.clear();
        setInput(rootNodes);
        refresh();
        addRecords(records);
    }

    public void addRecords(List<XmlLogRecord> records) {
        for (XmlLogRecord record : records) {
            addRecord(record);
        }
    }

    private void select(IStructuredSelection selection) {
        if (isScrollLogEnable()) {
            setSelection(selection);
        }
    }

    public void addRecord(XmlLogRecord record) {
        if (record.getLevel() == LogLevel.START) {
            LogParentTreeNode newParentTreeNode = new LogParentTreeNode(currentParentTreeNode, record);

            if (currentParentTreeNode == null) {
                rootNodes.add(newParentTreeNode);
                refresh(rootNodes);
            } else {
                currentParentTreeNode.addChild(newParentTreeNode);
                refresh(currentParentTreeNode);
            }
            currentParentTreeNode = newParentTreeNode;
            select(new StructuredSelection(newParentTreeNode));

        } else if (record.getLevel() == LogLevel.END) {
            currentParentTreeNode.setRecordEnd(record);
            LogParentTreeNode currentParentNodeImpl = (LogParentTreeNode) currentParentTreeNode;
            refresh(currentParentNodeImpl);

            // if a node is passed, collapse it, otherwise keep its current
            // state.
            if (currentParentNodeImpl.getResult() == null
                    || currentParentNodeImpl.getResult().getLevel() == LogLevel.PASSED) {
                if (isScrollLogEnable()) {
                    setExpandedState(currentParentNodeImpl, false);
                }
            }
            refresh(currentParentNodeImpl);
            select(new StructuredSelection(currentParentNodeImpl));

            // update progress bar if a main test case (that is not a called
            // test case) completed.
            if (((currentParentNodeImpl.getParent() == null || ((LogParentTreeNode) currentParentNodeImpl.getParent())
                    .getParent() == null))
                    && record.getSourceMethodName().equals(
                            com.kms.katalon.core.constants.StringConstants.LOG_END_TEST_METHOD)) {
                eventBroker.post(EventConstants.CONSOLE_LOG_UPDATE_PROGRESS_BAR, currentParentNodeImpl.getResult());
            }

            // switch to parent node
            currentParentTreeNode = currentParentNodeImpl.getParent();
        } else if (record.getLevel() == LogLevel.PASSED || record.getLevel() == LogLevel.FAILED
                || record.getLevel() == LogLevel.ERROR) {
            currentParentTreeNode.setResult(record);
            refresh(currentParentTreeNode);

            LogParentTreeNode currentParentNodeImpl = (LogParentTreeNode) currentParentTreeNode;

            // update progress bar if error occurs if a test case has invalid
            // variables
            if (record.getLevel() == LogLevel.ERROR
                    && currentParentNodeImpl.getParent() == null
                    && currentParentNodeImpl.getRecordStart().getMessage()
                            .startsWith(com.kms.katalon.core.constants.StringConstants.LOG_START_SUITE_METHOD)) {
                eventBroker.post(EventConstants.CONSOLE_LOG_UPDATE_PROGRESS_BAR, currentParentNodeImpl.getResult());
            }

        } else {
            ILogTreeNode newChildTreeNode = new LogChildTreeNode(currentParentTreeNode, record);
            currentParentTreeNode.addChild(newChildTreeNode);
            refresh(currentParentTreeNode);
            setExpandedState(currentParentTreeNode, true);
            select(new StructuredSelection(newChildTreeNode));
        }
    }

    private boolean isFailureNode(ILogParentTreeNode treeNode) {
        if (treeNode.getResult() != null && !treeNode.getElapsedTime().isEmpty()) {
            LogLevel logLevel = (LogLevel) treeNode.getResult().getLevel();
            return (logLevel == LogLevel.FAILED || logLevel == LogLevel.ERROR);
        }
        return false;
    }

    private ILogParentTreeNode findFirstFailureNodeInBranch(ILogTreeNode selectedNode, ILogParentTreeNode parentNode) {
        if (isFailureNode(parentNode) && !parentNode.equals(selectedNode))
            return parentNode;
        for (ILogTreeNode childNode : parentNode.getChildren()) {
            if (childNode instanceof ILogParentTreeNode) {
                ILogParentTreeNode foundNode = findFirstFailureNodeInBranch(selectedNode,
                        (ILogParentTreeNode) childNode);
                if (foundNode != null)
                    return foundNode;
            }
        }
        return null;
    }

    private ILogParentTreeNode findLastFailureNodeInBranch(ILogTreeNode selectedNode, ILogParentTreeNode parentNode) {
        if (parentNode.equals(selectedNode))
            return null;

        for (int index = parentNode.getChildren().size() - 1; index >= 0; index--) {
            ILogTreeNode childNode = parentNode.getChildren().get(index);
            if (childNode instanceof ILogParentTreeNode) {
                ILogParentTreeNode foundNode = findLastFailureNodeInBranch(selectedNode, (ILogParentTreeNode) childNode);
                if (foundNode != null)
                    return foundNode;
            }
        }

        return (isFailureNode(parentNode) && !parentNode.equals(selectedNode)) ? parentNode : null;
    }

    private ILogParentTreeNode findSiblingNode(ILogTreeNode treeNode, boolean previousFlag) {
        if (treeNode.getParent() == null) {
            int index = rootNodes.indexOf(treeNode);
            if (index < 0)
                return null;
            if (previousFlag && index > 0)
                return rootNodes.get(index - 1);
            if (!previousFlag && index < rootNodes.size() - 1)
                return rootNodes.get(index + 1);
        } else {
            ILogParentTreeNode parentNode = treeNode.getParent();
            int index = parentNode.getChildren().indexOf(treeNode);
            if (previousFlag) {
                for (int i = index - 1; i >= 0; i--) {
                    ILogTreeNode siblingNode = parentNode.getChildren().get(i);
                    if (siblingNode instanceof ILogParentTreeNode) {
                        return (ILogParentTreeNode) siblingNode;
                    }
                }
            } else {
                for (int i = index + 1; i < parentNode.getChildren().size(); i++) {
                    ILogTreeNode siblingNode = parentNode.getChildren().get(i);
                    if (siblingNode instanceof ILogParentTreeNode) {
                        return (ILogParentTreeNode) siblingNode;
                    }
                }
            }
        }
        return null;
    }

    private boolean isAncentor(ILogParentTreeNode parentNode, ILogTreeNode treeNode) {
        if (parentNode == null)
            return false;

        if (parentNode.equals(treeNode.getParent()))
            return true;

        if (treeNode.getParent() != null) {
            return isAncentor(parentNode, (ILogTreeNode) treeNode.getParent());
        }
        return false;
    }

    private ILogParentTreeNode selectFailureRecursively(ILogTreeNode selectedNode, ILogTreeNode treeNode,
            boolean previousFlag) {
        ILogParentTreeNode qualifiedNodeFound = null;

        if (treeNode instanceof ILogParentTreeNode) {
            ILogParentTreeNode parentNode = (ILogParentTreeNode) treeNode;

            if (previousFlag) {
                if (isAncentor(parentNode, selectedNode)) {
                    if (isFailureNode(parentNode))
                        return parentNode;
                } else {
                    qualifiedNodeFound = findLastFailureNodeInBranch(selectedNode, parentNode);
                }

            } else {
                if (!isAncentor(parentNode, selectedNode)) {
                    qualifiedNodeFound = findFirstFailureNodeInBranch(selectedNode, parentNode);
                }
            }
        }

        if (qualifiedNodeFound == null) {
            ILogParentTreeNode siblingNodeFound = findSiblingNode(treeNode, previousFlag);
            if (siblingNodeFound != null) {
                qualifiedNodeFound = selectFailureRecursively(selectedNode, (ILogTreeNode) siblingNodeFound,
                        previousFlag);
            }

            if (qualifiedNodeFound == null && treeNode.getParent() != null) {
                ILogParentTreeNode parentNode = treeNode.getParent();
                return selectFailureRecursively(selectedNode, (ILogTreeNode) parentNode, previousFlag);
            }
        }
        return qualifiedNodeFound;
    }

    public void selectPreviousFailure() {
        StructuredSelection selection = (StructuredSelection) getSelection();

        if (selection != null) {
            ILogTreeNode selectedNode = (ILogTreeNode) selection.getFirstElement();
            ILogParentTreeNode foundNode = selectFailureRecursively(selectedNode, selectedNode, true);
            if (foundNode != null)
                setSelection(new StructuredSelection(foundNode));
        }
    }

    public void selectNextFailure() {
        StructuredSelection selection = (StructuredSelection) getSelection();
        ILogParentTreeNode foundNode = null;

        if (selection != null) {
            ILogTreeNode selectedNode = (ILogTreeNode) selection.getFirstElement();
            foundNode = selectFailureRecursively(selectedNode, selectedNode, false);
        } else {
            foundNode = selectFailureRecursively(null, (ILogTreeNode) rootNodes.get(0), false);
        }

        if (foundNode != null)
            setSelection(new StructuredSelection(foundNode));
    }

    private void expandParentFailureRecursively(ILogParentTreeNode parentNode) {

        if ((parentNode.getResult() == null && !parentNode.getElapsedTime().isEmpty()) || isFailureNode(parentNode)) {
            setExpandedState(parentNode, true);

            for (ILogTreeNode childNode : parentNode.getChildren()) {
                if (childNode instanceof ILogParentTreeNode) {
                    expandParentFailureRecursively((ILogParentTreeNode) childNode);
                }
            }
        }
    }

    public void expandAllFailures() {
        collapseAll();
        for (ILogParentTreeNode parentNode : rootNodes) {
            expandParentFailureRecursively(parentNode);
        }
    }

    private boolean isScrollLogEnable() {
        return !store.getBoolean(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_PIN_LOG);
    }

}
