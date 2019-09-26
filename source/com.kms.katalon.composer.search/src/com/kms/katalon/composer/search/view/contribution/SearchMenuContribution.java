package com.kms.katalon.composer.search.view.contribution;

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
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WindowsElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.menu.MenuFactory;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.search.constants.StringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.entity.folder.FolderEntity;

@SuppressWarnings("restriction")
public class SearchMenuContribution {
	
	private static final String SHOW_REFERENCES_POPUP_MENUITEM_LABEL = StringConstants.CONTR_MENU_CONTEXT_SHOW_REFERENCES;
	public static final String SHOW_REFERENCES_COMMAND_ID = "com.kms.katalon.composer.search.command.showReferences";
	
	@Inject
	private ECommandService commandService;
	
    @Inject
    private ESelectionService selectionService;
    
    /**
     * Initializes {@link ISelectionListener} for context menu of the Explorer 
     */
    @Inject
    public void init() {
        selectionService.addSelectionListener(new ISelectionListener() {
            @Override
            public void selectionChanged(MPart part, Object selection) {
            	if (selection == null) return;
                if (part.getElementId().equals(IdConstants.EXPLORER_PART_ID)) {                    
                    selectionService.setSelection(selection);
                }
            }
        });
    }
    
    /**
     * Creates <b>Show Preferences</b> {@link MHandledMenuItem}
     * @param menuItems
     */
	@AboutToShow
	public void aboutToShow(List<MMenuElement> menuItems) {
		try {
		    Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
			if (canExecute(selectedObjects)) {
				MHandledMenuItem newFolderPopupMenuItem = MenuFactory.createPopupMenuItem(
						commandService.createCommand(SHOW_REFERENCES_COMMAND_ID, null), 
						SHOW_REFERENCES_POPUP_MENUITEM_LABEL, ConstantsHelper.getApplicationURI());
				if (newFolderPopupMenuItem != null) {
					menuItems.add(newFolderPopupMenuItem);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}
	}

	/**
	 * 
	 * @param selectedObjects
	 * @return true if the given selectedObjects can be showed references
	 * @throws Exception
	 */
	private boolean canExecute(Object[] selectedObjects) throws Exception {
		if (selectedObjects == null || selectedObjects.length != 1) return false;
		ITreeEntity treeEntity = (ITreeEntity) selectedObjects[0];
		if (treeEntity instanceof TestCaseTreeEntity ||
		        treeEntity instanceof WindowsElementTreeEntity ||
				treeEntity instanceof WebElementTreeEntity ||
				treeEntity instanceof TestDataTreeEntity ||
				treeEntity instanceof KeywordTreeEntity ||
				treeEntity instanceof PackageTreeEntity) return true;
		
		if (treeEntity instanceof FolderTreeEntity) {
			FolderEntity folder = (FolderEntity) treeEntity.getObject();
			if (folder == null) return false;
			
			switch (folder.getFolderType()) {
			case DATAFILE:
				return true;
			case KEYWORD:
				return true;
			case TESTCASE:
				return true;
			case WEBELEMENT:
				return true;
			default:
				break;			
			}
			
		}
		return false;
	}
}
