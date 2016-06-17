package com.kms.katalon.composer.testsuite.collection.menu;

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
import com.kms.katalon.composer.testsuite.collection.handler.TestSuiteTreeRootCatcher;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewTestSuiteCollectionPopupMenuContribution extends TestSuiteTreeRootCatcher {
    private static final String NEW_TEST_RUN_POPUP_MENUITEM_LABEL = "Test Suite Collection";

    private static final String NEW_TEST_RUN_COMMAND_ID = "com.kms.katalon.composer.testsuite.collection.command.new";

    
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
            if (getParentTestRunTreeFolder(selectionService, false) == null) {
                return;
            }

            MHandledMenuItem newTestSuitePopupMenuItem = MenuFactory.createPopupMenuItem(
                    commandService.createCommand(NEW_TEST_RUN_COMMAND_ID, null), NEW_TEST_RUN_POPUP_MENUITEM_LABEL,
                    ConstantsHelper.getApplicationURI());
            if (newTestSuitePopupMenuItem != null) {
                menuItems.add(newTestSuitePopupMenuItem);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
