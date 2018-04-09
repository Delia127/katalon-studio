package com.kms.katalon.composer.global.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.global.constants.ComposerGlobalMessageConstants;
import com.kms.katalon.composer.global.handler.ExecutionProfileTreeRootCatcher;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewExecutionProfilePopupMenu extends ExecutionProfileTreeRootCatcher {
    private static final String NEW_TEST_EXECUTION_PROFILE_COMMAND_ID = "com.kms.katalon.composer.global.command.newExecutionProfile";

    @Inject
    private ECommandService commandService;

    @Inject
    private ESelectionService selectionService;

    @Inject
    public void init() {
        selectionService.addSelectionListener(new ISelectionListener() {
            @Override
            public void selectionChanged(MPart part, Object selection) {
                if (IdConstants.EXPLORER_PART_ID.equals(part.getElementId())) {
                    selectionService.setSelection(null);
                    selectionService.setSelection(selection);
                }
            }
        });
    }

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            if (getProfileTreeFolder(selectionService) == null) {
                return;
            }
            MHandledMenuItem newTestSuitePopupMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(NEW_TEST_EXECUTION_PROFILE_COMMAND_ID, null),
                    ComposerGlobalMessageConstants.ITEM_LBL_NEW_EXECUTION_PROFILE, ConstantsHelper.getApplicationURI());
            if (newTestSuitePopupMenuItem != null) {
                menuItems.add(newTestSuitePopupMenuItem);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
