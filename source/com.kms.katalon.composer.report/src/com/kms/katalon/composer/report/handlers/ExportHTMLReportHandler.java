package com.kms.katalon.composer.report.handlers;

import java.io.File;
import java.net.URLClassLoader;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.report.integration.ReportComposerIntegrationFactory;
import com.kms.katalon.composer.report.platform.ExportReportProviderPlugin;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.report.ReportEntity;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class ExportHTMLReportHandler {
    @CanExecute
    public static boolean canExecute(ESelectionService selectionService) {
        return true;
    }

    @Execute
    public static void execute(ESelectionService selectionService, Shell shell) {
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
                        ReportEntity report = (ReportEntity) ((ReportTreeEntity) selectedObject).getObject();

                        File exportedFile = new File(exportDirectory,
                                report.getDisplayName() + ReportEntity.EXTENSION_HTML_REPORT);
                        URLClassLoader projectClassLoader = ProjectController.getInstance()
                                .getProjectClassLoader(ProjectController.getInstance().getCurrentProject());

                        Binding binding = new Binding();
                        ExportReportProviderPlugin exportReportProviderPlugin = ReportComposerIntegrationFactory
                                .getInstance().getExportReportPluginProviders().get(0);
                        binding.setVariable("exportProvider", exportReportProviderPlugin.getProvider());
                        binding.setVariable("fileLocation", exportedFile);
                        binding.setVariable("reportId", report.getId());
                        binding.setVariable("formatType", "HTML");
                        GroovyShell groovyShell = new GroovyShell(projectClassLoader, binding);
                        groovyShell.evaluate("exportProvider.exportTestSuite(fileLocation, reportId, formatType)");
                        Program.launch(exportedFile.toURI().toString());
                    }
                } catch (Throwable e) {
                    LoggerSingleton.logError(e);
                    MessageDialog.openError(shell, "Error", "Unable to export html report (" + e.getMessage() + ")");
                }
            }
        }
    }
}
