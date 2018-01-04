package com.kms.katalon.composer.testlistener.menu;

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
import com.kms.katalon.composer.testlistener.constant.ComposerTestListenerMessageConstants;
import com.kms.katalon.composer.testlistener.handler.TestListenerTreeRootCatcher;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewTestListenerPopupMenuContribution extends TestListenerTreeRootCatcher {

    private static final String NEW_TEST_LISTENER_COMMAND_ID = "com.kms.katalon.composer.testlistener.command.new";

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
            if (getParentTestListenerTreeFolder(selectionService, false) == null) {
                return;
            }

            MHandledMenuItem newTestSuitePopupMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(NEW_TEST_LISTENER_COMMAND_ID, null),
                    ComposerTestListenerMessageConstants.ITEM_LBL_NEW_TEST_LISTENER,
                    ConstantsHelper.getApplicationURI());
            if (newTestSuitePopupMenuItem != null) {
                menuItems.add(newTestSuitePopupMenuItem);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
