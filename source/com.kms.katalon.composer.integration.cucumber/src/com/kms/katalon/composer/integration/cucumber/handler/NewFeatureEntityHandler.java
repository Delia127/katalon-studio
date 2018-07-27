package com.kms.katalon.composer.integration.cucumber.handler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FeatureFolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FeatureTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.cucumber.dialog.NewFeatureEntityDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FeatureController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FeatureEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewFeatureEntityHandler extends FeatureTreeRootCatcher {
    
    @Inject
    IEventBroker eventBroker;

    @Inject
    ESelectionService selectionService;

    @CanExecute
    public boolean canExcute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            FeatureFolderTreeEntity parentFeatureTreeFolder = getParentFeatureTreeFolder(selectionService, false);
            FolderEntity rootFolder = parentFeatureTreeFolder.getObject();
            List<FeatureEntity> currentFeatures = FeatureController.getInstance().getFeatures(rootFolder);
            NewFeatureEntityDialog dialog = new NewFeatureEntityDialog(parentShell, currentFeatures);
            if (dialog.open() == NewFeatureEntityDialog.OK) {
                String newName = dialog.getNewName();
                FeatureEntity feature = FeatureController.getInstance().newFeature(newName, rootFolder);
                OpenFeatureEntityHandler openHandler = new OpenFeatureEntityHandler();
                openHandler.openEditor(feature);

                eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentFeatureTreeFolder);
                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, new FeatureTreeEntity(feature, parentFeatureTreeFolder));
            }
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, "Unable to new Feature file", e.getMessage());
            LoggerSingleton.logError(e);
        }
    }
}
