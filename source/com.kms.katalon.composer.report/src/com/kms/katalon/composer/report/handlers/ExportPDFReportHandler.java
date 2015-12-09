package com.kms.katalon.composer.report.handlers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
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
                        exportToPDF(reportEntity, exportDirectory);
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    MessageDialog.openError(shell, "Error", "Unable to export pdf report (" + e.getMessage() + ")");
                }
            }
        }
    }

    private void exportToPDF(final ReportEntity reportEntity, final File exportDirectory) {
        Job job = new Job("Exporting report to PDF") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask("Exporting report...", IProgressMonitor.UNKNOWN);
                    TestSuiteLogRecord suiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(
                            reportEntity);
                    TestSuitePdfGenerator generator = new TestSuitePdfGenerator(suiteLogRecord);
                    File exportedFile = generator.exportToPDF(new File(exportDirectory, FilenameUtils
                            .getBaseName(reportEntity.getName()) + ".pdf").getAbsolutePath());
                    Desktop.getDesktop().open(exportedFile);
                    return Status.OK_STATUS;
                } catch (final JasperReportException | IOException e) {
                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            MultiStatusErrorDialog.showErrorDialog(e, "Unable to export pdf report (" + e.getMessage()
                                    + ")", e.getClass().getSimpleName());
                        }
                    });
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };

        job.setUser(true);
        job.schedule();

    }
}
