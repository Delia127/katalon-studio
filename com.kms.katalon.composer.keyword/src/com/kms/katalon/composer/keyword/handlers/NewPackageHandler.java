package com.kms.katalon.composer.keyword.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.wizards.NewPackageCreationWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class NewPackageHandler {

	@Inject
	IEventBroker eventBroker;
	
    @Inject
    private ESelectionService selectionService;
    
    private FolderTreeEntity keywordFolderTreeRoot;

	@CanExecute
	private boolean canExecute() {
		if (ProjectController.getInstance().getCurrentProject() != null) {
			return true;
		} else {
		    return false;
		}
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
		try {
		    Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
			if (selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] instanceof ITreeEntity) {
				ITreeEntity selectedTreeEntity = (ITreeEntity) selectedObjects[0];
				Object parentTreeEntity = null;
				Object parent = null;
				if (selectedTreeEntity != null && findParentTreeEntity(selectedObjects) == null) {
				    selectedTreeEntity = keywordFolderTreeRoot;
				}
				if (selectedTreeEntity != null) {
					if (selectedTreeEntity instanceof FolderTreeEntity) {
						parent = GroovyUtil.getKeywordSourceRootFolder(((FolderEntity) selectedTreeEntity.getObject()).getProject());
						parentTreeEntity = selectedTreeEntity;
					} else if (selectedTreeEntity instanceof KeywordTreeEntity) {
						if (selectedTreeEntity.getParent() != null && 
								selectedTreeEntity.getParent() instanceof PackageTreeEntity) {
							parent = ((PackageTreeEntity) selectedTreeEntity.getParent()).getObject();
							parentTreeEntity = ((PackageTreeEntity) selectedTreeEntity.getParent()).getParent();
						}
					} 
					else if (selectedTreeEntity instanceof PackageTreeEntity) {
						parent = ((PackageTreeEntity) selectedTreeEntity).getObject();
						parentTreeEntity = selectedTreeEntity.getParent();
					}
				}
        		
				if (parent != null) {
					NewPackageCreationWizard newPackageCreationWizard = new NewPackageCreationWizard();
					newPackageCreationWizard.init(PlatformUI.getWorkbench(), new StructuredSelection(parent));
	        		WizardDialog wizardDialog = new WizardDialog(parentShell, newPackageCreationWizard);
	        		wizardDialog.open();
	        		if (wizardDialog.getReturnCode() == Window.OK) {
	        			eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
	        			//remove any working copy of child complicationUnit that exists in the current package 
                        IPackageFragment packageFragment = (IPackageFragment) newPackageCreationWizard
                                .getCreatedElement();
                        for (ICompilationUnit compicationUnit : packageFragment.getCompilationUnits()) {
                            compicationUnit.discardWorkingCopy();
                        }
                        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, new PackageTreeEntity(
                                packageFragment, (ITreeEntity) parentTreeEntity));
	        		}
				}
			}

		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
			MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, 
					StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_PACKAGE);
		}
	}
	
	public static ITreeEntity findParentTreeEntity(Object[] selectedObjects) throws Exception {
	    if (selectedObjects != null && selectedObjects.length > 0 && selectedObjects[0] instanceof ITreeEntity) {
            ITreeEntity parentTreeEntity = (ITreeEntity) selectedObjects[0];
            if (parentTreeEntity instanceof FolderTreeEntity) {
                FolderEntity parentFolder = (FolderEntity) parentTreeEntity.getObject();
                if (parentFolder.getFolderType() == FolderType.KEYWORD) {
                    return parentTreeEntity;
                }
            } else if (parentTreeEntity instanceof KeywordTreeEntity || 
                    parentTreeEntity instanceof PackageTreeEntity) {
                return parentTreeEntity;
            }
        }
	    return null;
	}
	
	@Inject
    @Optional
    private void catchKeywordTreeEntitiesRoot(
            @UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) List<Object> treeEntities) {
        try {
            for (Object o : treeEntities) {
                Object entityObject = ((ITreeEntity) o).getObject();
                if (entityObject instanceof FolderEntity) {
                    FolderEntity folder = (FolderEntity) entityObject;
                    if (folder.getFolderType() == FolderType.KEYWORD) {
                        keywordFolderTreeRoot = (FolderTreeEntity) o;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }
}
