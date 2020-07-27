package com.kms.katalon.composer.webservice.handlers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.openapi.OpenApiImportNode;
import com.kms.katalon.composer.webservice.openapi.OpenApiImporter;
import com.kms.katalon.composer.webservice.openapi.OpenApiProjectImportResult;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;

public class ImportOpenApiHandler {

    private IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();

    private FolderTreeEntity objectRepositoryTreeRoot;

    @CanExecute
    public boolean canExecute() {
        return (ProjectController.getInstance().getCurrentProject() != null)
                && !LauncherManager.getInstance().isAnyLauncherRunning();
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
        if (featureService.canUse(KSEFeature.IMPORT_OPENAPI)) {
            try {
                FileDialog fileDialog = new FileDialog(shell, SWT.SYSTEM_MODAL);
                fileDialog.setFilterPath(Platform.getLocation().toString());
                String selectedFilePath = fileDialog.open();
                if (selectedFilePath != null && selectedFilePath.length() > 0) {
                    FolderEntity parentFolderEntity = (FolderEntity) objectRepositoryTreeRoot.getObject();
                    OpenApiProjectImportResult projectImportResult = OpenApiImporter.getInstance()
                            .importServices(selectedFilePath, parentFolderEntity);
                    saveImportedArtifacts(projectImportResult);
                }
            } catch (Exception e) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
                        ComposerWebserviceMessageConstants.ERROR_MSG_FAIL_TO_IMPORT_OPENAPI);
                LoggerSingleton.logError(e);
            }
        } else {
            KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.IMPORT_OPENAPI);
        }
    }

    private void saveImportedArtifacts(OpenApiProjectImportResult projectImportResult) throws Exception {
        List<OpenApiImportNode> importNodes = flatten(projectImportResult).collect(Collectors.toList());
        for (OpenApiImportNode importNode : importNodes) {
            FileEntity fileEntity = importNode.getFileEntity();
            if (fileEntity != null && fileEntity instanceof FolderEntity) {
                FolderController.getInstance().saveFolder((FolderEntity) fileEntity);
            }
            if (fileEntity != null && fileEntity instanceof WebServiceRequestEntity) {
                ObjectRepositoryController.getInstance().saveNewTestObject((WebServiceRequestEntity) fileEntity);
            }
        }
    }

    private Stream<? extends OpenApiImportNode> flatten(OpenApiImportNode importNode) {
        return Stream.concat(Stream.of(importNode),
                Stream.of(importNode.getChildImportNodes()).flatMap(n -> flatten(n)));
    }

    @Inject
    @Optional
    private void catchTestDataFolderTreeEntitiesRoot(
            @UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) List<Object> treeEntities) {
        try {
            for (Object o : treeEntities) {
                Object entityObject = ((ITreeEntity) o).getObject();
                if (entityObject instanceof FolderEntity) {
                    FolderEntity folder = (FolderEntity) entityObject;
                    if (folder.getFolderType() == FolderType.WEBELEMENT) {
                        objectRepositoryTreeRoot = (FolderTreeEntity) o;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
