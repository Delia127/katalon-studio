package com.kms.katalon.composer.testdata.menu;

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
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.handlers.NewTestDataHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class NewTestDataPopupMenuContribution {
	private static final String NEW_TESTDATA_POPUP_MENUITEM_LABEL = StringConstants.MENU_CONTEXT_TEST_DATA;
	
	private static final String NEW_TESTDATA_COMMAND_ID = "com.kms.katalon.composer.testdata.command.add";

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
			if (NewTestDataHandler.findParentTreeEntity(selectedObjects) != null) {
				MHandledMenuItem newTestDataPopupMenuItem = MenuFactory.createPopupMenuItem(
						commandService.createCommand(NEW_TESTDATA_COMMAND_ID, null), NEW_TESTDATA_POPUP_MENUITEM_LABEL,
						ConstantsHelper.getApplicationURI());
				if (newTestDataPopupMenuItem != null) {
					menuItems.add(newTestDataPopupMenuItem);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
}
