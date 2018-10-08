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
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;

@SuppressWarnings("restriction")
public class NewTestObjectPopupMenuContribution {
	private static final String NEW_TESTOBJECT_POPUP_MENUITEM_LABEL = StringConstants.MENU_CONTEXT_NEW_TEST_OBJ;
	private static final String NEW_TESTOBJECT_COMMAND_ID = "com.kms.katalon.composer.objectrepository.command.add";

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
	    ProjectEntity project = ProjectController.getInstance().getCurrentProject();
	    if (project.getType() == ProjectType.WEBSERVICE) {
	        return;
	    }
	    
		try {
			Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
			if (selectedObjects == null) return;
			if (NewTestObjectHandler.getParentTreeEntity(selectedObjects) != null) {
				MHandledMenuItem newTestObjectPopupMenuItem = MenuFactory.createPopupMenuItem(
						commandService.createCommand(NEW_TESTOBJECT_COMMAND_ID, null),
						NEW_TESTOBJECT_POPUP_MENUITEM_LABEL, ConstantsHelper.getApplicationURI());
				if (newTestObjectPopupMenuItem != null) {
					menuItems.add(newTestObjectPopupMenuItem);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
}
