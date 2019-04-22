package com.kms.katalon.composer.report.handlers;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.ReportCollectionTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.report.menu.ExportReportDynamicMenuContribution;
import com.kms.katalon.composer.report.menu.ExportReportMenuItemObject;
import com.kms.katalon.composer.report.platform.ExportReportProviderPlugin;
import com.kms.katalon.composer.report.platform.ExportReportProviderReflection;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.report.ReportCollectionEntity;

public class ExportTestSuiteCollectionReportHandler {
    @Inject
    ESelectionService selectionService;

    @Execute
    public void execute(@Optional MDirectMenuItem directMenuItem, Shell shell) {
        if (directMenuItem == null) {
            return;
        }
        ExportReportMenuItemObject menuItemObject = (ExportReportMenuItemObject) directMenuItem.getTransientData()
                .get(ExportReportDynamicMenuContribution.REPORT_ITEM_TRANSIENT_DATA);
        ExportReportProviderPlugin exportReportProviderPlugin = menuItemObject.getExportReportPlugin();
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
                    if (selectedObject instanceof ReportCollectionTreeEntity
                            && ((ReportCollectionTreeEntity) selectedObject)
                                    .getObject() instanceof ReportCollectionEntity) {
                        ReportCollectionEntity report = (ReportCollectionEntity) ((ReportCollectionTreeEntity) selectedObject)
                                .getObject();

                        ExportReportProviderReflection reflection = new ExportReportProviderReflection(
                                exportReportProviderPlugin);
                        File exportedFile = (File) reflection.exportTestSuiteCollection(report,
                                menuItemObject.getFormatType(), exportDirectory);
                        Program.launch(exportedFile.toURI().toString());
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    UISynchronizeService.syncExec(() -> MessageDialog.openError(shell, "Error",
                            "Unable to export report (" + e.getMessage() + ")"));
                }
            }
        }
    }
}
