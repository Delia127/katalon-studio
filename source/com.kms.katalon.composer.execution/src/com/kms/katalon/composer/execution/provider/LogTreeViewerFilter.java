package com.kms.katalon.composer.execution.provider;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.execution.constants.ComposerExecutionPreferenceConstants;
import com.kms.katalon.composer.execution.tree.ILogParentTreeNode;
import com.kms.katalon.composer.execution.tree.ILogTreeNode;
import com.kms.katalon.core.constants.StringConstants;
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
        if (!isLogEnded(logParentTreeNode) || isTestSuiteLog(logParentTreeNode)) {
            return true;
        }
        return ((evaluteLog(logParentTreeNode.getResult()) & getPreferenceShowedValue()) != 0);
    }
    
    @Override
    public boolean isFilterProperty(Object element, String property) {
        if ("filter".equals(property)) {
            return true;
        }
        return super.isFilterProperty(element, property);
    }

    private boolean isTestSuiteLog(ILogParentTreeNode logParentTreeNode) {
        return StringConstants.LOG_START_SUITE_METHOD.equals(logParentTreeNode.getRecordStart().getSourceMethodName());
    }

    protected boolean isGeneralStep(final ILogParentTreeNode logParentTreeNode) {
        return logParentTreeNode.getResult() == null;
    }

    protected boolean isLogEnded(final ILogParentTreeNode logParentTreeNode) {
        return logParentTreeNode.getRecordEnd() != null || logParentTreeNode.getResult() != null;
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
