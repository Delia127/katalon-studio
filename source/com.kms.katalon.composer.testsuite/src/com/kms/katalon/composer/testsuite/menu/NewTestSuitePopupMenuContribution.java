package com.kms.katalon.composer.testsuite.menu;

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
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.handlers.NewTestSuiteHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewTestSuitePopupMenuContribution {
    private static final String NEW_TESTSUITE_POPUP_MENUITEM_LABEL = StringConstants.MENU_CONTEXT_TEST_SUITE;
    private static final String NEW_TESTSUITE_COMMAND_ID = StringConstants.COMMAND_ID_ADD_TEST_SUITE;

    @Inject
    private ECommandService commandService;

    @Inject
    private ESelectionService selectionService;

    @Inject
    public void init() {
        selectionService.addSelectionListener(new ISelectionListener() {
            @Override
            public void selectionChanged(MPart part, Object selection) {
                if (part.getElementId().equals(IdConstants.EXPLORER_PART_ID)) {
                    selectionService.setSelection(null);
                    selectionService.setSelection(selection);
                }
            }
        });
    }

    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            
            if (selectedObjects == null) {
                return;
            }
            
            if (NewTestSuiteHandler.findParentTreeEntity(selectedObjects) != null) {
                MHandledMenuItem newTestSuitePopupMenuItem = MenuFactory.createPopupMenuItem(
                        commandService.createCommand(NEW_TESTSUITE_COMMAND_ID, null),
                        NEW_TESTSUITE_POPUP_MENUITEM_LABEL, ConstantsHelper.getApplicationURI());
                if (newTestSuitePopupMenuItem != null) {
                    menuItems.add(newTestSuitePopupMenuItem);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
