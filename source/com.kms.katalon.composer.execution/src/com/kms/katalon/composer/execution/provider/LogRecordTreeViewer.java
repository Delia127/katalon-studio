package com.kms.katalon.composer.execution.provider;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.execution.constants.ComposerExecutionPreferenceConstants;
import com.kms.katalon.composer.execution.tree.ILogParentTreeNode;
import com.kms.katalon.composer.execution.tree.ILogTreeNode;
import com.kms.katalon.composer.execution.tree.LogChildTreeNode;
import com.kms.katalon.composer.execution.tree.LogParentTreeNode;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class LogRecordTreeViewer extends TreeViewer {

    private ILogParentTreeNode currentParentTreeNode;

    private List<ILogParentTreeNode> rootNodes;

    private ScopedPreferenceStore store;

    public LogRecordTreeViewer(Composite parent, int style) {
        super(parent, style);
        rootNodes = new ArrayList<ILogParentTreeNode>();
        store = getPreferenceStore(LogRecordTreeViewer.class);
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
        LogLevel logLevel = LogLevel.valueOf(record.getLevel());
        if (logLevel == null) {
            return;
        }

        switch (logLevel) {
            case START: {
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
                break;
            }
            case END: {
                currentParentTreeNode.setRecordEnd(record);
                LogParentTreeNode currentParentNodeImpl = (LogParentTreeNode) currentParentTreeNode;

                refresh(currentParentNodeImpl);
                update(currentParentNodeImpl, new String[] { LogTreeViewerFilter.PROPERTY_FILTER });
                // if a node is passed, collapse it, otherwise keep its current
                // state.
                if (currentParentNodeImpl.getResult() == null
                        || LogLevel.valueOf(currentParentNodeImpl.getResult().getLevel()) == LogLevel.PASSED) {
                    if (isScrollLogEnable()) {
                        if (currentParentNodeImpl.getParent() != null) { // don't collapse root
                            setExpandedState(currentParentNodeImpl, false);
                        }
                    }
                }
                select(new StructuredSelection(currentParentNodeImpl));

                // switch to parent node
                currentParentTreeNode = currentParentNodeImpl.getParent();
                break;
            }
            case PASSED:
            case FAILED:
            case WARNING:
            case NOT_RUN:
            case ERROR: {
                if (currentParentTreeNode == null) {
                    break;
                }
                currentParentTreeNode.setResult(record);
                refresh(currentParentTreeNode);
                break;
            }
            default: {
                ILogTreeNode newChildTreeNode = new LogChildTreeNode(currentParentTreeNode, record);
                currentParentTreeNode.addChild(newChildTreeNode);
                refresh(currentParentTreeNode);
                setExpandedState(currentParentTreeNode, true);
                select(new StructuredSelection(newChildTreeNode));
                break;
            }
        }
    }

    private boolean isFailureNode(ILogParentTreeNode treeNode) {
        if (treeNode.getResult() != null && !treeNode.getElapsedTime().isEmpty()) {
            LogLevel logLevel = LogLevel.valueOf(treeNode.getResult().getLevel());
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
                ILogParentTreeNode foundNode = findLastFailureNodeInBranch(selectedNode,
                        (ILogParentTreeNode) childNode);
                if (foundNode != null)
                    return foundNode;
            }
        }

        return (isFailureNode(parentNode) && !parentNode.equals(selectedNode)) ? parentNode : null;
    }

    private ILogParentTreeNode findSiblingNode(ILogTreeNode treeNode, boolean previousFlag) {
        if (treeNode == null) {
            return null;
        }
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

            if (qualifiedNodeFound == null && treeNode != null && treeNode.getParent() != null) {
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

        if (foundNode != null) {
            setSelection(new StructuredSelection(foundNode));
        }
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
        return !store.getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_PIN_LOG);
    }

}
