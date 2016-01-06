package com.kms.katalon.composer.report.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.report.preference.ReportPreferenceInitializer;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;

public class ReportTestStepTableViewerFilter extends ReportTestCaseTableViewerFilter {

    /**
     * Used to store element that has been sorted
     */
    private List<Object> sortedElements;
    
    /*package*/ void resetLookup() {
        sortedElements = new ArrayList<Object>();
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (viewer.getInput().equals(parentElement)) {
            sortedElements = new ArrayList<Object>();
        }

        if (sortedElements.contains(element)) {
            return true;
        }

        if (parentElement != null && parentElement instanceof ILogRecord
                && ReportPreferenceInitializer.isChildLogForFirstMatchIncluded()
                && sortedElements.contains(parentElement)) {

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
        if (searchString == null || searchString.isEmpty()) {
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
            sortedElements.add(element);
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

}
