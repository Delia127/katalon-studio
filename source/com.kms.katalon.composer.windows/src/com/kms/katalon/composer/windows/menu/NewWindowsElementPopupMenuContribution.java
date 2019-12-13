package com.kms.katalon.composer.windows.menu;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WindowsElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;
import com.kms.katalon.composer.windows.handler.NewWindowsElementHandler;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;

public class NewWindowsElementPopupMenuContribution {
	private static final String CONTRIBUTION_CLASS_URI = "bundleclass://com.kms.katalon.composer.windows/"
			+ NewWindowsElementHandler.class.getName();

	@AboutToShow
	public void aboutToShow(List<MMenuElement> menuItems) {
		try {
			ExplorerPart explorerPart = ExplorerPart.getInstance();
			List<Object> selectedObjects = explorerPart.getSelectedTreeEntities();
			if (selectedObjects == null || selectedObjects.size() != 1) {
				return;
			}
			Object selected = selectedObjects.get(0);
			FolderEntity folderEntity = null;
			if (selected instanceof FolderTreeEntity) {
				folderEntity = ((FolderTreeEntity) selected).getObject();
			} else if (selected instanceof WindowsElementTreeEntity) {
				folderEntity = ((WindowsElementTreeEntity) selected).getObject().getParentFolder();
			} else if (selected instanceof ITreeEntity
					&& ((ITreeEntity) selected).getObject() instanceof WebElementEntity) {
				folderEntity = ((WebElementEntity) ((ITreeEntity) selected).getObject()).getParentFolder();
			}
			if (folderEntity != null && folderEntity.getFolderType() == FolderType.WEBELEMENT) {
				MDirectMenuItem newWindowsElementToolItem = MenuFactory.createDirectMenuItem("Windows Object",
						ConstantsHelper.getApplicationURI());
				newWindowsElementToolItem.setContributionURI(CONTRIBUTION_CLASS_URI);
				menuItems.add(newWindowsElementToolItem);
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
}
