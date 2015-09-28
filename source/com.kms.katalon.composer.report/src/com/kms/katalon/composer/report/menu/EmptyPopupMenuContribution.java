package com.kms.katalon.composer.report.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

@SuppressWarnings("restriction")
public class EmptyPopupMenuContribution {

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
			if (canExecute(selectedObjects)) {
				MHandledMenuItem newFolderPopupMenuItem = MenuFactory.createPopupMenuItem(
						commandService.createCommand("com.kms.katalon.composer.report.command.empty", null),
						"(empty)", ConstantsHelper.getApplicationURI());
				if (newFolderPopupMenuItem != null) {
					menuItems.add(newFolderPopupMenuItem);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	private boolean canExecute(Object[] selectedObjects) throws Exception {
		if (selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] instanceof ITreeEntity) {
			ITreeEntity parentTreeEntity = (ITreeEntity) selectedObjects[0];
			if (parentTreeEntity instanceof FolderTreeEntity) {
				FolderEntity parentFolder = (FolderEntity) parentTreeEntity.getObject();
				if (parentFolder.getFolderType() == FolderType.REPORT) {
					return true;
				}
			} else if (parentTreeEntity instanceof ReportTreeEntity) {
				return true;
			}
		}
		return false;
	}
}
