package com.kms.katalon.composer.webservice.menu;

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
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.helper.ConstantsHelper;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WindowsElementEntity;

@SuppressWarnings("restriction")
public class NewWebServiceRequestObjectPopupMenuContribution {
	private static final String WEBSERVICE_REQ_POPUP_MENUITEM_LABEL = StringConstants.MENU_CONTEXT_WEBSERVICE_REQ;
	private static final String NEW_WEBSERVICE_REQ_COMMAND_ID = "com.kms.katalon.composer.webservice.command.add";
	
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
			if (getParentTreeEntity(selectedObjects) != null) {
				MHandledMenuItem newTestObjectPopupMenuItem = MenuFactory.createPopupMenuItem(
						commandService.createCommand(NEW_WEBSERVICE_REQ_COMMAND_ID, null), 
						WEBSERVICE_REQ_POPUP_MENUITEM_LABEL, ConstantsHelper.getApplicationURI());
				if (newTestObjectPopupMenuItem != null) {
					menuItems.add(newTestObjectPopupMenuItem);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
	
	
	private static ITreeEntity getParentTreeEntity(Object[] selectedObjects) throws Exception {
		if(selectedObjects == null){
			return null;
		}
		for (Object object : selectedObjects) {
			if (object instanceof ITreeEntity) {
				if (((ITreeEntity) object).getObject() instanceof FolderEntity) {
					FolderEntity folder = (FolderEntity) ((ITreeEntity) object).getObject();
					if (folder.getFolderType() == FolderType.WEBELEMENT) {
						return (ITreeEntity) object;
					}
				} else if (((ITreeEntity) object).getObject() instanceof WebElementEntity) {
					return (ITreeEntity) ((ITreeEntity) object).getParent();
				} else if (((ITreeEntity) object).getObject() instanceof WindowsElementEntity) {
					return (ITreeEntity) ((ITreeEntity) object).getParent();
				}
			}
		}
		return null;
	}

}
