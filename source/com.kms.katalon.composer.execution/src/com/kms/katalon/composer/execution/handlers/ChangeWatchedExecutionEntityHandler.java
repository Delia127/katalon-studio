package com.kms.katalon.composer.execution.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.kms.katalon.composer.execution.part.LogViewerPart;
import com.kms.katalon.constants.IdConstants;

public class ChangeWatchedExecutionEntityHandler {

    @Inject
    private EPartService partService;

    @Execute
    public void execute(MDirectMenuItem menuItem) {
        if (menuItem == null || partService == null) {
            return;
        }
        MPart logViewerMPart = partService.findPart(IdConstants.IDE_CONSOLE_LOG_PART_ID);
        if (logViewerMPart == null || logViewerMPart.getObject() == null
                || !(logViewerMPart.getObject() instanceof LogViewerPart)) {
            return;
        }
        LogViewerPart logViewerPart = (LogViewerPart) logViewerMPart.getObject();
        logViewerPart.changeSelectedLaucherWatchedById(menuItem.getElementId());
    }

}
