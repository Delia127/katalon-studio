 
package com.kms.katalon.composer.objectrepository.menu;

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
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.objectrepository.handler.NewTestObjectHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;

@SuppressWarnings("restriction")
public class AddToObjectSpyMenuContribution {

	private static final String ADD_TO_OBJECTSPY_POPUP_MENUITEM_LABEL = StringConstants.MENU_CONTEXT_ADD_TO_OBJECTSPY;
	private static final String ADD_TO_OBJECTSPY_COMMAND_ID = "com.kms.katalon.composer.objectrepository.command.addToObjectSpy";
	
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
					selectionService.setSelection(selection);
				} else {
					selectionService.setSelection(null);
				}
			}
		});
	}

	@AboutToShow
	public void aboutToShow(List<MMenuElement> menuItems) {
		try {
			Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
			
			if (selectedObjects == null || selectedObjects.length == 0) return;
			
			if (NewTestObjectHandler.getParentTreeEntity(selectedObjects) != null) {
				MHandledMenuItem newTestObjectPopupMenuItem = MenuFactory.createPopupMenuItem(
						commandService.createCommand(ADD_TO_OBJECTSPY_COMMAND_ID, null),
						ADD_TO_OBJECTSPY_POPUP_MENUITEM_LABEL, ConstantsHelper.getApplicationURI());
				if (newTestObjectPopupMenuItem != null) {
					menuItems.add(newTestObjectPopupMenuItem);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
}