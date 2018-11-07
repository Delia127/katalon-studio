package com.kms.katalon.composer.report.handlers;

import java.io.File;
import java.util.Arrays;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.report.ReportEntity;

public class ExportCSVReportHandler {
    @CanExecute
    public boolean canExecute(ESelectionService selectionService) {
        return true;
    }

    @Execute
    public void execute(ESelectionService selectionService, Shell shell) {
        execute(selectionService, shell, false);
    }

    protected void execute(ESelectionService selectionService, Shell shell, boolean isSummaryReport) {
        DirectoryDialog directoryDialog = new DirectoryDialog(shell);
        directoryDialog.open();

        if (directoryDialog.getFilterPath() == null) {
            MessageDialog.openWarning(null, "Warning", "Directory not found.");
            return;
        }

        File exportDirectory = new File(directoryDialog.getFilterPath());
        if (exportDirectory != null && exportDirectory.exists() && exportDirectory.isDirectory()) {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            for (Object selectedObject : selectedObjects) {
                try {
                    if (selectedObject instanceof ReportTreeEntity
                            && ((ReportTreeEntity) selectedObject).getObject() instanceof ReportEntity) {
                        ReportEntity report = ((ReportTreeEntity) selectedObject).getObject();
                        TestSuiteLogRecord suiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(report);
                        if (suiteLogRecord == null) {
                            MessageDialog.openWarning(null, StringConstants.WARN, "Report not found.");
                            return;
                        }

                        File exportedFile = new File(exportDirectory,
                                report.getDisplayName() + ReportEntity.EXTENSION_CSV_REPORT);
                        ReportUtil.writeLogRecordToCSVFile(suiteLogRecord, exportedFile,
                                Arrays.asList(suiteLogRecord.getChildRecords()), !isSummaryReport);
                        Program.launch(exportedFile.toURI().toString());
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    MessageDialog.openError(shell, "Error", "Unable to export csv report (" + e.getMessage() + ")");
                }
            }
        }
    }
}
