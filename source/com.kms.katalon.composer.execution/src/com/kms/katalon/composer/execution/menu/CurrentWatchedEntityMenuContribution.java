package com.kms.katalon.composer.execution.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.kms.katalon.composer.execution.handlers.ChangeWatchedExecutionEntityHandler;
import com.kms.katalon.composer.execution.launcher.IDEObservableLauncher;
import com.kms.katalon.composer.execution.part.LogViewerPart;
import com.kms.katalon.constants.IdConstants;

public class CurrentWatchedEntityMenuContribution {
    private static final String CHANGE_WATCHED_ENTITY_HANDLER_URI = "bundleclass://com.kms.katalon.composer.execution/" + ChangeWatchedExecutionEntityHandler.class.getName();
    
    @Inject
    private EPartService partService;

    @AboutToShow
    public void aboutToShow(List<MMenuElement> items) {
        if (items == null || partService == null) {
            return;
        }
        MPart logViewerMPart = partService.findPart(IdConstants.IDE_CONSOLE_LOG_PART_ID);
        if (logViewerMPart == null || logViewerMPart.getObject() == null
                || !(logViewerMPart.getObject() instanceof LogViewerPart)) {
            return;
        }
        LogViewerPart logViewerPart = (LogViewerPart) logViewerMPart.getObject();
        List<IDEObservableLauncher> launchersWatched = logViewerPart.getLaunchersWatched();
        IDEObservableLauncher selectedLauncherWatched = logViewerPart.getLauncherWatched();
        items.clear();
        if (launchersWatched == null || launchersWatched.isEmpty()) {
            return;
        }
        for (IDEObservableLauncher launcherWatched : launchersWatched) {
            final MDirectMenuItem directMenuItem = MMenuFactory.INSTANCE.createDirectMenuItem();
            directMenuItem.setLabel(launcherWatched.getName());
            directMenuItem.setType(ItemType.CHECK);
            directMenuItem.setElementId(launcherWatched.getName());
            directMenuItem.setVisible(true);
            directMenuItem.setToBeRendered(true);
            if (selectedLauncherWatched == launcherWatched) {
                directMenuItem.setSelected(true);
            }
            directMenuItem.setContributionURI(CHANGE_WATCHED_ENTITY_HANDLER_URI);
            items.add(directMenuItem);
        }
    }
}
