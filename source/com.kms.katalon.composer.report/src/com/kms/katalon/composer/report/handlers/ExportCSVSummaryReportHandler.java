package com.kms.katalon.composer.report.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Shell;

public class ExportCSVSummaryReportHandler extends ExportCSVReportHandler {

    @Override
    @Execute
    public void execute(ESelectionService selectionService, Shell shell) {
        execute(selectionService, shell, true);
    }
}
