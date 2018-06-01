package com.kms.katalon.composer.keyword.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.keyword.handlers.ExportFolderHandler;
import com.kms.katalon.composer.keyword.handlers.ExportJarHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;

public class ExportKeyWordsPopupMenuContribution {
    private static final String CONTRIBUTOR_URI = FrameworkUtil.getBundle(ExportKeyWordsPopupMenuContribution.class)
            .getSymbolicName();

    public static final String CM_EXPORT_COMPOSER_BUNDLE_URI = "bundleclass://com.kms.katalon.composer.keyword/";

    @Inject
    private ESelectionService selectionService;

    @Inject
    private EModelService modelService;

    /**
     * Creates a {@link MMenu} that has label qTest and provides some qTest integration's function.
     * 
     * @param menuItems
     */
    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            if (project == null)
                return;

            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            if (selectedObjects == null || selectedObjects.length != 1) {
                return;
            }

            Object selectedObject = selectedObjects[0];
            
            FolderEntity keywordRootFolder = FolderController.getInstance().getKeywordRoot(ProjectController.getInstance().getCurrentProject());
            
            
            if ((selectedObject instanceof FolderTreeEntity
                    && ((FolderTreeEntity) selectedObject).getObject() instanceof FolderEntity
                    && FolderType.KEYWORD.equals(((FolderTreeEntity) selectedObject).getObject().getFolderType()))
                    
                    || ((selectedObject instanceof PackageTreeEntity) 
                         && ((PackageTreeEntity) selectedObject).getParent().getObject().equals(keywordRootFolder))) {

                MMenu importMenu = getExportMenu();
                MDirectMenuItem folderMenuItem = getFolderMenuItem();

                folderMenuItem.setContributionURI(CM_EXPORT_COMPOSER_BUNDLE_URI + ExportFolderHandler.class.getName());
                importMenu.getChildren().add(folderMenuItem);

                MDirectMenuItem gitMenuItem = getGitMenuItem();
                gitMenuItem.setContributionURI(CM_EXPORT_COMPOSER_BUNDLE_URI + ExportJarHandler.class.getName());
                importMenu.getChildren().add(gitMenuItem);

                if (importMenu.getChildren().size() > 0) {
                    menuItems.add(0, importMenu);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private MMenu getExportMenu() {
        MMenu dynamicItem = modelService.createModelElement(MMenu.class);
        // Import
        dynamicItem.setLabel("Export");
        dynamicItem.setContributorURI(CONTRIBUTOR_URI);
        return dynamicItem;
    }

    private MDirectMenuItem getFolderMenuItem() {
        MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
        dynamicItem.setLabel("Folder");
        dynamicItem.setContributorURI(CONTRIBUTOR_URI);
        return dynamicItem;
    }

    private MDirectMenuItem getGitMenuItem() {
        MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
        dynamicItem.setLabel("Git");
        dynamicItem.setContributorURI(CONTRIBUTOR_URI);
        return dynamicItem;
    }
}
