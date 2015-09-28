package com.kms.katalon.composer.report.provider;

import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;

public class ReportTestStepTableViewerFilter extends ReportTestCaseTableViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!isElementMatchFilter(element)) return false;

		ReportTestStepTreeViewer tablerView = (ReportTestStepTreeViewer) viewer;
		ILogRecord logRecord = (ILogRecord) element;
		String searchString = tablerView.getSearchedString();
		if (searchString == null || searchString.isEmpty()) return true;

		String searchedStringTrimmed = searchString.toLowerCase().trim();
		
		if (logRecord instanceof MessageLogRecord) {
			if (logRecord.getMessage() != null) {
				if (logRecord.getMessage().toLowerCase().contains(searchedStringTrimmed)) return true;
			}
		} else {
			if (logRecord.getName() != null) {
				if (logRecord.getName().toLowerCase().contains(searchedStringTrimmed)) return true;
			}
			
			if (logRecord.getDescription() != null) {
				if (logRecord.getDescription().toLowerCase().contains(searchedStringTrimmed)) return true;
			}
		}
		
		ReportTreeTableContentProvider treeContentProvider = (ReportTreeTableContentProvider) tablerView
				.getContentProvider();
		
		boolean isMatched = false;
		if (treeContentProvider.getChildren(element) != null) {
			for (Object childElement : treeContentProvider.getChildren(element)) {
				isMatched |= select(viewer, element, childElement);
			}
		}
		return isMatched;
	}

}
