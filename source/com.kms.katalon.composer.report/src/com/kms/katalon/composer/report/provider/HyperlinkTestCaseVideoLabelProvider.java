package com.kms.katalon.composer.report.provider;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.providers.CellLayoutInfo;
import com.kms.katalon.composer.components.impl.providers.HyperLinkColumnLabelProvider;
import com.kms.katalon.composer.components.impl.providers.TableCellLayoutInfo;
import com.kms.katalon.composer.report.constants.ComposerReportMessageConstants;
import com.kms.katalon.composer.report.constants.ImageConstants;
import com.kms.katalon.composer.report.parts.ReportPart;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.report.ReportTestCaseEntity;

public class HyperlinkTestCaseVideoLabelProvider extends HyperLinkColumnLabelProvider<ILogRecord> {

    private ReportPart reportPart;

    public HyperlinkTestCaseVideoLabelProvider(ReportPart reportPart) {
        super(ReportPartTestCaseLabelProvider.CLMN_TEST_CASE_VIDEO);
        this.reportPart = reportPart;
    }

    @Override
    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        File file = getVideoFile((ILogRecord) cell.getElement());
        if (file == null || !file.exists()) {
            return;
        }
        Program.launch(file.getAbsolutePath());
    }

    @Override
    protected Class<ILogRecord> getElementType() {
        return ILogRecord.class;
    }

    private int getOrder(ILogRecord[] testCaseLogRecords, ILogRecord testCaseLogRecord) {
        for (int index = 0; index < testCaseLogRecords.length; index++) {
            if (testCaseLogRecord.equals(testCaseLogRecords[index])) {
                return index;
            }
        }
        return -1;
    }

    @Override
    protected Image getImage(ILogRecord logRecord) {
        return getVideoFile(logRecord) != null ? ImageConstants.IMG_16_VIDEO : null;
    }

    private File getVideoFile(ILogRecord logRecord) {
        ReportEntity report = reportPart.getReport();
        if (report == null) {
            return null;
        }

        int index = getOrder((ILogRecord[]) getViewer().getInput(), logRecord);
        TestSuiteLogRecord testSuiteLog = (TestSuiteLogRecord) logRecord.getParentLogRecord();

        List<ReportTestCaseEntity> reportTestCases = report.getReportTestCases();
        if (index >= reportTestCases.size()) {
            return null;
        }
        String location = reportTestCases.get(index).getVideoLocation();
        if (StringUtils.isEmpty(location)) {
            return null;
        }
        return new File(testSuiteLog.getLogFolder(), location);
    }

    @Override
    protected String getText(ILogRecord element) {
        return StringUtils.EMPTY;
    }

    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return new TableCellLayoutInfo() {
            @Override
            public int getLeftMargin() {
                return 15;
            }
        };
    }

    @Override
    protected boolean isPlacedMouseHover(ViewerCell cell) {
        return cell != null && cell.getColumnIndex() == columnIndex && cell.getImage() != null;
    }

    @Override
    protected boolean shouldShowCursor(ViewerCell cell, Point currentMouseLocation) {
        return cell.getBounds().contains(currentMouseLocation);
    }

    @Override
    protected String getElementToolTipText(ILogRecord logRecord) {
        File videoFile = getVideoFile(logRecord);
        return (videoFile != null && videoFile.exists())
                ? ComposerReportMessageConstants.PROVIDER_TOOLTIP_OPEN_RECORDED_VIDEO : null;
    }
}
