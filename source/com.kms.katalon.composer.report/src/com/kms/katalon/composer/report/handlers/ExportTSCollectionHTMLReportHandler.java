package com.kms.katalon.composer.report.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ReportCollectionTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.report.ReportItemDescription;

public class ExportTSCollectionHTMLReportHandler {

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute(Shell shell) {
        DirectoryDialog dirDialog = new DirectoryDialog(shell);
        dirDialog.open();

        if (dirDialog.getFilterPath() == null) {
            MessageDialog.openWarning(null, StringConstants.WARN, "Directory not found.");
            return;
        }

        File destDir = new File(dirDialog.getFilterPath());
        if (destDir == null || !destDir.exists() || !destDir.isDirectory()) {
            return;
        }

        List<String> exceptions = new ArrayList<>();
        try {
            ReportCollectionTreeEntity reportTreeEntity = getSelectedReport();
            if (reportTreeEntity == null) {
                return;
            }
            ReportCollectionEntity report = reportTreeEntity.getObject();
            if (report == null) {
                return;
            }

            Map<String, String> tsInfoItem;
            List<Map<String, String>> tsInfoItems = new ArrayList<>();
            String projectDirLocation = ProjectController.getInstance().getCurrentProject().getFolderLocation();
            for (ReportItemDescription item : report.getReportItemDescriptions()) {
                try {
                    String reportRelativeLocation = item.getReportLocation();
                    String reportDirLocation = projectDirLocation + File.separator + reportRelativeLocation;
                    TestSuiteLogRecord testSuiteLogRecord = ReportUtil.generate(reportDirLocation);
                    int reportDirLocationHashCode = reportDirLocation.toString().hashCode();
                    
                    String htmlFileName = StringUtils.substringAfterLast(reportRelativeLocation,
                            StringConstants.ENTITY_ID_SEPARATOR) + reportDirLocationHashCode + ReportEntity.EXTENSION_HTML_REPORT;
                    
                    File htmlFile = new File(reportDirLocation + File.separator + htmlFileName);
                    if (!htmlFile.exists()) {
                        if (testSuiteLogRecord == null) {
                            throw new FileNotFoundException(htmlFile.getPath());
                        }
                        ReportUtil.writeHtmlReportAppendHashCodeToName(testSuiteLogRecord, new File(reportDirLocation), reportDirLocationHashCode);
                    }
                    FileUtils.copyFileToDirectory(htmlFile, destDir);

                    tsInfoItem = new HashMap<>();
                    tsInfoItem.put("report_location", reportRelativeLocation);
                    tsInfoItem.put("report", htmlFileName);
                    tsInfoItem.put("id", getTestSuiteId(reportRelativeLocation));
                    tsInfoItem.put("environment", item.getRunConfigDescription().getRunConfigurationId());
                    String status = getStatus(testSuiteLogRecord);
                    tsInfoItem.put("status", status);
                    tsInfoItem.put("fail_on_total", getFailOnTotal(testSuiteLogRecord));
                    tsInfoItem.put("status_color", getStatusColor(status));
                    tsInfoItem.put("fail_color", getFailColor(testSuiteLogRecord));
                    tsInfoItems.add(tsInfoItem);
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    exceptions.add(e.getMessage());
                }
            }
            ReportUtil.writeTSCollectionHTMLReport(reportTreeEntity.getParent().getText(),
                    JsonUtil.toJson(tsInfoItems, false), destDir);
            Program.launch(destDir.toURI().toString());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            exceptions.add(e.getMessage());
        }

        if (exceptions.isEmpty()) {
            return;
        }
        String errorMsgDetails = StringUtils.join(exceptions, "\n");
        MultiStatusErrorDialog.showErrorDialog("Unable to export Test Suite Collection report", errorMsgDetails,
                errorMsgDetails);
    }

    private int getTotalFailedTestCases(TestSuiteLogRecord testSuiteLogRecord) {
        if (testSuiteLogRecord == null) {
            return 0;
        }
        return testSuiteLogRecord.getTotalFailedTestCases() + testSuiteLogRecord.getTotalErrorTestCases();
    }

    private String getFailOnTotal(TestSuiteLogRecord testSuiteLogRecord) {
        if (testSuiteLogRecord == null) {
            return StringConstants.EMPTY;
        }
        return getTotalFailedTestCases(testSuiteLogRecord) + " / " + testSuiteLogRecord.getTotalTestCases();
    }

    private String getFailColor(TestSuiteLogRecord testSuiteLogRecord) {
        if (testSuiteLogRecord == null) {
            return StringConstants.EMPTY;
        }
        if (getTotalFailedTestCases(testSuiteLogRecord) > 0) {
            return ColorUtil.FAILED_STATUS_BACKGROUND_COLOR;
        }
        return StringConstants.EMPTY;
    }

    private String getTestSuiteId(String reportRelativeLocation) {
        String testSuiteId = reportRelativeLocation.replaceFirst(StringConstants.ROOT_FOLDER_NAME_REPORT,
                StringConstants.ROOT_FOLDER_NAME_TEST_SUITE);
        testSuiteId = StringUtils.substringBeforeLast(testSuiteId, StringConstants.ENTITY_ID_SEPARATOR);
        return testSuiteId;
    }

    private String getStatus(TestSuiteLogRecord logRecord) {
        if (logRecord == null) {
            return StringConstants.NOT_STARTED;
        }
        if (logRecord.getTotalIncompleteTestCases() > 0) {
            return StringConstants.INCOMPLETE;
        }
        return StringConstants.COMPLETE;
    }

    private String getStatusColor(String status) {
        if (StringUtils.equals(status, StringConstants.NOT_STARTED)) {
            return String.valueOf(SWT.COLOR_GRAY);
        }
        if (StringUtils.equals(status, StringConstants.INCOMPLETE)) {
            return ColorUtil.INCOMPLETE_STATUS_BACKGROUND_COLOR;
        }
        return StringConstants.EMPTY;
    }

    public ReportCollectionTreeEntity getSelectedReport() {
        Object o = SelectionServiceSingleton.getInstance()
                .getSelectionService()
                .getSelection(IdConstants.EXPLORER_PART_ID);

        if (o == null || !o.getClass().isArray()) {
            return null;
        }

        Object[] selectedObjects = (Object[]) o;
        if (selectedObjects.length == 0 || !(selectedObjects[0] instanceof ReportCollectionTreeEntity)) {
            return null;
        }

        return (ReportCollectionTreeEntity) selectedObjects[0];
    }
}
