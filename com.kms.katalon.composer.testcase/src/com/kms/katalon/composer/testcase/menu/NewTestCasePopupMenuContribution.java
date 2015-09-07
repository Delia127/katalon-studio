package com.kms.katalon.composer.testcase.menu;

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
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.handlers.NewTestCaseHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewTestCasePopupMenuContribution {
	private static final String NEW_TESTCASE_POPUP_MENUITEM_LABEL = StringConstants.MENU_CONTEXT_TEST_CASE;
	private static final String NEW_TESTCASE_COMMAND_ID = StringConstants.COMMAND_ID_ADD_TEST_CASE;
	
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
			if (NewTestCaseHandler.findParentTreeEntity(
			        (Object[])selectionService.getSelection(IdConstants.EXPLORER_PART_ID)) != null) {
				MHandledMenuItem newTestCasePopupMenuItem = MenuFactory.createPopupMenuItem(
						commandService.createCommand(NEW_TESTCASE_COMMAND_ID, null), 
						NEW_TESTCASE_POPUP_MENUITEM_LABEL, ConstantsHelper.getApplicationURI());
				if (newTestCasePopupMenuItem != null) {
					menuItems.add(newTestCasePopupMenuItem);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}
	}
}
