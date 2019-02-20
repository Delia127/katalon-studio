package com.kms.katalon.composer.integration.cucumber.handler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.SystemFileTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.cucumber.dialog.NewFeatureEntityDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.SystemFileController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewFeatureEntityHandler extends FeatureTreeRootCatcher {

    private static final String RESOURCES_TEMPLATE_TPL_PATH = "resources/template/feature_template.tpl";

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
            FolderTreeEntity parentFeatureTreeFolder = getParentFeatureTreeFolder();
            FolderEntity rootFolder = parentFeatureTreeFolder.getObject();
            List<FileEntity> currentFeatures = SystemFileController.getInstance().getChildren(rootFolder);
            NewFeatureEntityDialog dialog = new NewFeatureEntityDialog(parentShell, currentFeatures);
            if (dialog.open() == NewFeatureEntityDialog.OK) {
                NewFeatureEntityDialog.NewFeatureResult result = dialog.getResult();
                String content = result.isGenerateTemplateAllowed() ? getFileContent(RESOURCES_TEMPLATE_TPL_PATH)
                        : StringUtils.EMPTY;

                SystemFileEntity feature = SystemFileController.getInstance().newFile(result.getNewName(), content,
                        rootFolder);

                Trackings.trackCreatingObject("bddFeatureFile");

                OpenFeatureEntityHandler openHandler = new OpenFeatureEntityHandler();
                openHandler.openEditor(feature);

                eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentFeatureTreeFolder);
                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM,
                        new SystemFileTreeEntity(feature, parentFeatureTreeFolder));
            }
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, "Unable to new Feature file", e.getMessage());
            LoggerSingleton.logError(e);
        }
    }

    private FolderTreeEntity getParentFeatureTreeFolder() throws DALException, ControllerException {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();

        FolderTreeEntity parentFeatureTreeFolder = getSelectedTreeEntity(
                (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID));
        if (parentFeatureTreeFolder == null) {
            FolderEntity includeRootFolder = FolderController.getInstance().getIncludeRoot(project);
            FolderEntity featureRootFolder = FolderController.getInstance().getFeatureRoot(project);
            FolderTreeEntity featureRootFolderEntity = new FolderTreeEntity(featureRootFolder, TreeEntityUtil
                    .createSelectedTreeEntityHierachy(featureRootFolder.getParentFolder(), includeRootFolder));
            parentFeatureTreeFolder = featureRootFolderEntity;
        }

        return parentFeatureTreeFolder;
    }

    private String getFileContent(String filePath) {
        URL url = FileLocator.find(FrameworkUtil.getBundle(NewFeatureEntityHandler.class), new Path(filePath), null);
        try {
            return StringUtils.join(
                    IOUtils.readLines(new BufferedInputStream(url.openStream()), GlobalStringConstants.DF_CHARSET),
                    "\n");
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return StringUtils.EMPTY;
        }
    }
}
