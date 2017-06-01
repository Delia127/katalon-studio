package com.kms.katalon.composer.execution.provider;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.execution.constants.ComposerExecutionPreferenceConstants;
import com.kms.katalon.composer.execution.tree.ILogParentTreeNode;
import com.kms.katalon.composer.execution.tree.ILogTreeNode;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class LogTreeViewerFilter extends LogViewerFilter {
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (!(element instanceof ILogTreeNode)) {
            return true;
        }
        ILogTreeNode logTreeNode = (ILogTreeNode) element;
        if (!(logTreeNode instanceof ILogParentTreeNode)) {
            return ((evaluteLog(logTreeNode.getLogRecord()) & getPreferenceShowedValue()) != 0);
        }
        final ILogParentTreeNode logParentTreeNode = (ILogParentTreeNode) logTreeNode;
        if (!isLogEnded(logParentTreeNode)) {
            return true;
        }
        if (isGeneralStep(logParentTreeNode)) {
            return false;
        }
        return ((evaluteLog(logParentTreeNode.getResult()) & getPreferenceShowedValue()) != 0);
    }

    protected boolean isGeneralStep(final ILogParentTreeNode logParentTreeNode) {
        return logParentTreeNode.getResult() == null;
    }

    protected boolean isLogEnded(final ILogParentTreeNode logParentTreeNode) {
        return logParentTreeNode.getRecordEnd() != null;
    }

    // Only allow to filter failed logs for now
    @Override
    protected int getPreferenceShowedValue() {
        ScopedPreferenceStore store = getPreferenceStore(LogViewerFilter.class);
        final boolean isShowedFailedLogs = store
                .getBoolean(ComposerExecutionPreferenceConstants.EXECUTION_TREE_VIEW_SHOW_FAILED_LOGS);
        return (isShowedFailedLogs) ? FAILED : ALL;
    }
}
