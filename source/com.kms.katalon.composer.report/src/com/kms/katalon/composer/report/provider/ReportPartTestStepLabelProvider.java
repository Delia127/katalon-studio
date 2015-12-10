package com.kms.katalon.composer.report.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.report.constants.ImageConstants;
import com.kms.katalon.composer.report.parts.ReportPartTestLogView;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.MessageLogRecord;
import com.kms.katalon.core.logging.model.TestStepLogRecord;
import com.kms.katalon.core.util.DateUtil;

public class ReportPartTestStepLabelProvider extends StyledCellLabelProvider {

	
	public static final int CLMN_TEST_LOG_ITEM_IDX = 0;
	public static final int CLMN_TEST_LOG_DESCRIPTION_IDX = 1;
	public static final int CLMN_TEST_LOG_ELAPSED_IDX = 2;
	public static final int CLMN_TEST_LOG_ATTACHMENT_IDX = 3;
	
	private int columnIndex;
	private ReportPartTestLogView reportPart;
	
	public ReportPartTestStepLabelProvider(int columnIndex, ReportPartTestLogView part) {
		this.columnIndex = columnIndex;
		reportPart = part;
	}

	
	public Image getImage(Object element) {
		if (element == null || !(element instanceof ILogRecord) ||
				columnIndex < CLMN_TEST_LOG_ITEM_IDX || columnIndex > CLMN_TEST_LOG_ATTACHMENT_IDX) return null;
		if (columnIndex == CLMN_TEST_LOG_ITEM_IDX) {
			ILogRecord logRecord = (ILogRecord) element;
			if (logRecord instanceof MessageLogRecord) {
				return ImageConstants.IMG_16_INFO;
			} else {			
				switch (logRecord.getStatus().getStatusValue()) {
				case ERROR:
					return ImageConstants.IMG_16_ERROR;
				case FAILED:
					return ImageConstants.IMG_16_FAILED;
				case NOT_RUN:
					return ImageConstants.IMG_16_FAILED;
				case PASSED:
					return ImageConstants.IMG_16_PASSED;
				}	
			}
		}
		
		if (columnIndex == CLMN_TEST_LOG_ATTACHMENT_IDX && element instanceof MessageLogRecord) {
			MessageLogRecord messageLog = (MessageLogRecord) element;
			if (messageLog.getAttachment() != null && !messageLog.getAttachment().isEmpty()) {
				return ImageConstants.IMG_16_ATTACHMENT;
			}			
		}
		return null;
	}	
	

	public String getText(Object element) {
		if (element == null || !(element instanceof ILogRecord) ||
				columnIndex < CLMN_TEST_LOG_ITEM_IDX || columnIndex > CLMN_TEST_LOG_ATTACHMENT_IDX) return "";
		ILogRecord logRecord = (ILogRecord) element;
		switch (columnIndex) {
		case CLMN_TEST_LOG_ITEM_IDX:
			if (logRecord instanceof MessageLogRecord) {
				return ((MessageLogRecord) element).getMessage();
			} else {
				String testStepName = ((ILogRecord) element).getName();
				if (logRecord instanceof TestStepLogRecord) {
					return ((TestStepLogRecord) logRecord).getIndexString() + ". " + testStepName;
				} else {
					return testStepName;
				}				
			}
		case CLMN_TEST_LOG_DESCRIPTION_IDX:
			return (logRecord.getDescription() != null) ? logRecord.getDescription() : "";
		case CLMN_TEST_LOG_ELAPSED_IDX:
			if (logRecord.getStartTime() > 0 && logRecord.getEndTime() > 0) {
				return DateUtil.getElapsedTime(logRecord.getStartTime(), logRecord.getEndTime());
			}
		case CLMN_TEST_LOG_ATTACHMENT_IDX:
			break;
		default:
			break;
		}
		return "";
		
	}
	
	private StyledString getStyleString(Object element) {
		StyledString styledString = new StyledString();
		switch (columnIndex) {
		case CLMN_TEST_LOG_ELAPSED_IDX:
			styledString.append(getText(element), StyledString.COUNTER_STYLER);
			break;
		default:
			styledString.append(getText(element));
			break;
		}
		return styledString;
	}
	
	
	@Override
	public void update(ViewerCell cell) {
		if (cell.getElement() != null) {
			StyledString styledString = getStyleString(cell.getElement());	
			cell.setText(styledString.toString());			
			
			cell.setImage(getImage(cell.getElement()));
			
			List<StyleRange> range = new ArrayList<>();
			range.addAll(Arrays.asList(styledString.getStyleRanges()));
			
			//highlight searched string
			if (columnIndex == CLMN_TEST_LOG_ITEM_IDX || columnIndex == CLMN_TEST_LOG_DESCRIPTION_IDX) {
				ReportTestStepTreeViewer tableViewer = (ReportTestStepTreeViewer) getViewer();
				String searchedString = tableViewer.getSearchedString().toLowerCase().trim();
				
				if (!searchedString.isEmpty())  {				
					Matcher m = Pattern.compile(Pattern.quote(searchedString)).matcher(cell.getText().toLowerCase());
					while (m.find()) {
						StyleRange myStyledRange = new StyleRange(m.start(), searchedString.length(), null,
								ColorUtil.getHighlightBackgroundColor());
						range.add(myStyledRange);
					}				
				}
			}
			cell.setStyleRanges(range.toArray(new StyleRange[0]));
		}
		super.update(cell);
	}
	
	@Override
	public String getToolTipText(Object element) {
		if (element == null) return null;
		return getText((ILogRecord) element);
	}
	
	@Override
	public Image getToolTipImage(Object element) {
		switch (columnIndex) {
		case CLMN_TEST_LOG_ATTACHMENT_IDX:
			if (element instanceof MessageLogRecord && (reportPart.getReport() != null)) {
				MessageLogRecord messageLog = (MessageLogRecord) element;
				return new Image(getColumn().getViewer().getControl().getDisplay(), 
						reportPart.getReport().getLocation() + File.separator
						+ messageLog.getAttachment());
			}
			return null;
		}
		return null;
	}

}
