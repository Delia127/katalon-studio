package com.kms.katalon.composer.report.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.report.ReportEntity;

public class ExportJUnitReportHandler {

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
        Object[] selectedReports = getSelectedReports();
        List<String> exceptions = new ArrayList<>();
        for (Object selectedObject : selectedReports) {
            try {
                if (!(selectedObject instanceof ReportTreeEntity)) {
                    continue;
                }
                ReportEntity report = ((ReportTreeEntity) selectedObject).getObject();
                if (report == null) {
                    continue;
                }
                File junitFile = new File(report.getJUnitFile());
                if (!junitFile.exists()) {
                    ReportUtil.writeLogRecordToJUnitFile(report.getLocation());
                }
                String destReportName = ReportEntity.JUNIT_REPORT_NAME;
                if (selectedReports.length > 1) {
                    destReportName = report.getName() + File.separator + destReportName;
                }
                FileUtils.copyFile(junitFile, new File(destDir, destReportName));
                Program.launch(destDir.toURI().toString());
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                exceptions.add(e.getMessage());
            }
        }

        if (exceptions.isEmpty()) {
            return;
        }
        String errorMsgDetails = StringUtils.join(exceptions, "\n");
        MultiStatusErrorDialog.showErrorDialog("Unable to export JUnit report", errorMsgDetails, errorMsgDetails);
    }

    public Object[] getSelectedReports() {
        Object o = SelectionServiceSingleton.getInstance()
                .getSelectionService()
                .getSelection(IdConstants.EXPLORER_PART_ID);

        if (o == null || !o.getClass().isArray()) {
            return new Object[0];
        }

        return (Object[]) o;
    }

}
