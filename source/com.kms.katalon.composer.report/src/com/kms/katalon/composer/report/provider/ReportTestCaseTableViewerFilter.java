package com.kms.katalon.composer.report.provider;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;

public class ReportTestCaseTableViewerFilter extends ViewerFilter {
	
	public static final int INFO       = 1 << 0;
    public static final int PASSED     = 1 << 1;
    public static final int FAILED     = 1 << 2;
    public static final int ERROR      = 1 << 3;
    public static final int INCOMPLETE = 1 << 4;
    
    private boolean showInfo;
    private boolean showPassed;
    private boolean showFailed;
    private boolean showError;
    private boolean showIncomplete;
    
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!isElementMatchFilter(element)) return false;
		
		ReportTestCaseTableViewer tablerView = (ReportTestCaseTableViewer) viewer;
		ILogRecord logRecord = (ILogRecord) element;
		String searchString = tablerView.getSearchedString();
		if (searchString == null || searchString.isEmpty()) return true;
		
		if (logRecord.getName() == null) {
		    return false;
		}
		
		String testCaseId = logRecord.getName();
		String testCaseName = testCaseId.substring(testCaseId.lastIndexOf("/") + 1, testCaseId.length());
		
		return testCaseName.toLowerCase().contains(searchString.toLowerCase().trim());
	}
	
	protected boolean isElementMatchFilter(Object element) {
		return (getLogValue((ILogRecord) element) & getFilterValue()) != 0;
	}
	
	private int getLogValue(ILogRecord logRecord) {		
		if (logRecord.getStatus() != null) {
			if (logRecord instanceof MessageLogRecord) {
				return INFO;
			} else {
				switch (logRecord.getStatus().getStatusValue()) {
				case INCOMPLETE:
                    return INCOMPLETE;
				case ERROR:
					return ERROR;
				case FAILED:
					return FAILED;
				case PASSED:
					return PASSED;
				default:
					return 0;
				}
			}
		}
		return 0;
	}
	
	private int getFilterValue() {
		int filterInfo = (showInfo) ? INFO : 0;
		int filterPassed = (showPassed) ? PASSED : 0;
		int filterFailed = (showFailed) ? FAILED : 0;
		int filterError = (showError) ? ERROR : 0;
		int filterIncomplete = (showIncomplete) ? INCOMPLETE : 0;
		
		return  (filterInfo & INFO) | 
				(filterPassed & PASSED) | 
				(filterFailed & FAILED) | 
				(filterError & ERROR) |
				(filterIncomplete & INCOMPLETE);
	}

	public boolean isInfoShown() {
		return showInfo;
	}

	public void showInfo(boolean showInfo) {
		this.showInfo = showInfo;
	}

	public boolean isPassedShown() {
		return showPassed;
	}

	public void showPassed(boolean showPassed) {
		this.showPassed = showPassed;
	}

	public boolean isErrorShown() {
		return showError;
	}

	public void showError(boolean showError) {
		this.showError = showError;
	}

	public boolean isFailedShown() {
		return showFailed;
	}

	public void showFailed(boolean showFailed) {
		this.showFailed = showFailed;
	}

    public boolean isIncompleteShown() {
        return showIncomplete;
    }

    public void showIncomplete(boolean showIncomplete) {
        this.showIncomplete = showIncomplete;
    }
}
