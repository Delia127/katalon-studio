package com.kms.katalon.composer.report.provider;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.report.preference.ReportPreferenceInitializer;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;

public class ReportTestStepTableViewerFilter extends ReportTestCaseTableViewerFilter {
    public static final int NOT_RUN    = 1 << 6;
    
    private boolean showNotRun;
    
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (parentElement instanceof ILogRecord
                && ReportPreferenceInitializer.isChildLogForFirstMatchIncluded()) {
            return true;
        }
        return internallySelect(viewer, parentElement, element);
    }

    public boolean internallySelect(Viewer viewer, Object parentElement, Object element) {
        if (!isElementMatchFilter(element)) {
            return false;
        }

        ReportTestStepTreeViewer tablerView = (ReportTestStepTreeViewer) viewer;
        ILogRecord logRecord = (ILogRecord) element;
        String searchString = tablerView.getSearchedString();
        if (StringUtils.isEmpty(searchString)) {
            return true;
        }
        
        String queryTrimmed = searchString.toLowerCase().trim();
        boolean isMatched = false;
        
        if (logRecord instanceof MessageLogRecord) {
            if (logRecord.getMessage() != null && logRecord.getMessage().toLowerCase().contains(queryTrimmed)) {
                isMatched = true;
            }
        } else {
            if (logRecord.getName() != null && logRecord.getName().toLowerCase().contains(queryTrimmed)) {
                isMatched = true;
            }

            if (logRecord.getDescription() != null && logRecord.getDescription().toLowerCase().contains(queryTrimmed)) {
                isMatched = true;
            }
        }
        
        if (isMatched) {
            return true;
        }

        ReportTreeTableContentProvider treeContentProvider = (ReportTreeTableContentProvider) tablerView
                .getContentProvider();

        if (treeContentProvider.getChildren(element) != null) {
            for (Object childElement : treeContentProvider.getChildren(element)) {
                isMatched |= internallySelect(viewer, element, childElement);
            }
        }
        return isMatched;
    }
    
    @Override
    protected int getLogValue(ILogRecord logRecord) {
        if (logRecord.getStatus().getStatusValue() == TestStatusValue.NOT_RUN) {
            return NOT_RUN;
        }
        return super.getLogValue(logRecord);
    }
    
    @Override
    protected int getFilterValue() {
        int filterNotRun = showNotRun ? NOT_RUN : 0;
        return super.getFilterValue() | (filterNotRun & NOT_RUN);
    }

    public boolean isNotRunShown() {
        return showNotRun;
    }

    public void showNotRun(boolean showNotRun) {
        this.showNotRun = showNotRun;
    }

}
