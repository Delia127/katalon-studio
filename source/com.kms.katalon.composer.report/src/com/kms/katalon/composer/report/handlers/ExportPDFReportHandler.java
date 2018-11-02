package com.kms.katalon.composer.report.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.jasper.pdf.TestSuitePdfGenerator;
import com.kms.katalon.jasper.pdf.exception.JasperReportException;

public class ExportPDFReportHandler {
    @Inject
    private UISynchronize sync;

    @CanExecute
    public boolean canExecute(ESelectionService selectionService) {
        return true;
    }

    @Execute
    public void execute(ESelectionService selectionService, Shell shell) {
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
                        ReportEntity reportEntity = (ReportEntity) ((ReportTreeEntity) selectedObject).getObject();
                        exportToPDF(reportEntity, exportDirectory, shell);
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    MultiStatusErrorDialog.showErrorDialog(e, "Unable to export pdf report ("
                            + e.getClass().getSimpleName() + ")", e.getMessage());
                }
            }
        }
    }

    private void exportToPDF(final ReportEntity reportEntity, final File exportDirectory, Shell shell)
            throws InvocationTargetException, InterruptedException {
        ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(shell);
        monitorDialog.run(true, false, new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    monitor.beginTask("Exporting report to PDF...", IProgressMonitor.UNKNOWN);
                    TestSuiteLogRecord suiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(
                            reportEntity);
                    TestSuitePdfGenerator generator = new TestSuitePdfGenerator(suiteLogRecord);
                    File exportedFile = generator.exportToPDF(
                            new File(exportDirectory, FilenameUtils.getBaseName(reportEntity.getDisplayName())
                                    + ReportEntity.EXTENSION_PDF_REPORT).getAbsolutePath());
                    Program.launch(exportedFile.toURI().toString());
                } catch (final JasperReportException | IOException e) {
                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            MultiStatusErrorDialog.showErrorDialog(e, "Unable to export pdf report ("
                                    + e.getClass().getSimpleName() + ")", e.getMessage());
                            LoggerSingleton.logError(e);
                        }
                    });
                } finally {
                    monitor.done();
                }
            }
        });

    }
}
