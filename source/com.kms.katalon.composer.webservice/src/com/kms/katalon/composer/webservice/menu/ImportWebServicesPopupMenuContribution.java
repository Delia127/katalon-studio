package com.kms.katalon.composer.webservice.menu;

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
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.handlers.ImportWebServiceRequestObjectsFromSwaggerHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;

public class ImportWebServicesPopupMenuContribution {
    private static final String CONTRIBUTOR_URI = FrameworkUtil.getBundle(ImportWebServicesPopupMenuContribution.class)
            .getSymbolicName();

    public static final String CM_IMPORT_COMPOSER_BUNDLE_URI = "bundleclass://com.kms.katalon.composer.webservice/";

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
            if (selectedObject instanceof FolderTreeEntity
                    && ((FolderTreeEntity) selectedObject).getObject() instanceof FolderEntity
                    && FolderType.WEBELEMENT.equals(((FolderTreeEntity) selectedObject).getObject().getFolderType())) {

                MMenu importMenu = getImportMenu();
                MDirectMenuItem folderMenuItem = getSwaggerMenu();

                folderMenuItem.setContributionURI(CM_IMPORT_COMPOSER_BUNDLE_URI + ImportWebServiceRequestObjectsFromSwaggerHandler.class.getName());
                importMenu.getChildren().add(folderMenuItem);
                
                if (importMenu.getChildren().size() > 0) {
                    menuItems.add(0, importMenu);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private MMenu getImportMenu() {
        MMenu dynamicItem = modelService.createModelElement(MMenu.class);
        dynamicItem.setLabel(StringConstants.MSG_IMPORT);
        dynamicItem.setContributorURI(CONTRIBUTOR_URI);
        return dynamicItem;
    }

    private MDirectMenuItem getSwaggerMenu() {
        MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
        dynamicItem.setLabel(StringConstants.MENU_CONTEXT_WEBSERVICE_REQ_SWAGGER);
        dynamicItem.setContributorURI(CONTRIBUTOR_URI);
        return dynamicItem;
    }

}
