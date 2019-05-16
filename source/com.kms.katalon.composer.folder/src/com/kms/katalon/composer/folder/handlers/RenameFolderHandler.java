package com.kms.katalon.composer.folder.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.dialogs.CWizardDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.wizard.RenameWizard;
import com.kms.katalon.composer.folder.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

@SuppressWarnings("restriction")
public class RenameFolderHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    EPartService partService;

    @Named(IServiceConstants.ACTIVE_SHELL)
    Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof FolderTreeEntity) {
                    execute((FolderTreeEntity) object);
                }
            }
        });
    }

    private void execute(FolderTreeEntity folderTreeEntity) {
        try {
            FolderEntity oldFolder = (FolderEntity) folderTreeEntity.getObject();
            if (oldFolder != null) {
                if (oldFolder.getParentFolder() == null) {
                    renameRootFolder(folderTreeEntity);
                    return;
                }
                RenameWizard renameWizard = new RenameWizard(folderTreeEntity, FolderController.getInstance()
                        .getChildrenNames(oldFolder.getParentFolder()));
                CWizardDialog wizardDialog = new CWizardDialog(parentShell, renameWizard);
                int code = wizardDialog.open();
                if (code == Window.OK) {
                    FolderEntity folder = (FolderEntity) folderTreeEntity.getObject();
                    String oldName = folder.getName();
                    try {
                        if (renameWizard.getNewNameValue() != null && !renameWizard.getNewNameValue().equals("")
                                && !renameWizard.getNewNameValue().equalsIgnoreCase(oldName)) {

                            // preSave
                            // get object and oldLocation of all children of folder
                            // to notify to all object that refer to them
                            // get collection of descendant entities that
                            // doesn't include descendant folder entity
                            List<FileEntity> allDescendantEntites = new ArrayList<>();
                            for (Object descendantEntity : FolderController.getInstance().getAllDescentdantEntities(
                                    folder)) {
                                if (!(descendantEntity instanceof FolderEntity)) {
                                    allDescendantEntites.add((FileEntity) descendantEntity);
                                }
                            }
                            String folderParentPath = folder.getParentFolder().getRelativePathForUI()
                                    .replace('\\', IPath.SEPARATOR)
                                    + IPath.SEPARATOR;
                            FolderController.getInstance().updateFolderName(folder, renameWizard.getNewNameValue());
                            eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM,
                                    new Object[] { folderParentPath + oldName + IPath.SEPARATOR,
                                            folderParentPath + renameWizard.getNewNameValue() + IPath.SEPARATOR });
                            folder.setName(renameWizard.getNewNameValue());
                            // afterSaving
                            // send notification events
                            String eventTopic = null;
                            switch (folder.getFolderType()) {
                                case TESTCASE:
                                    eventTopic = EventConstants.TESTCASE_UPDATED;
                                    break;
                                case DATAFILE:
                                    eventTopic = EventConstants.TEST_DATA_UPDATED;
                                    break;
                                case TESTSUITE:
                                    eventTopic = EventConstants.TEST_SUITE_UPDATED;
                                    break;
                                case WEBELEMENT:
                                    eventTopic = EventConstants.TEST_OBJECT_UPDATED;
                                    break;
                                case CHECKPOINT:
                                    eventTopic = EventConstants.CHECKPOINT_UPDATED;
                                    break;
                                default:
                                    break;
                            }
                            if (eventTopic != null) {
                                for (FileEntity entity : allDescendantEntites) {
                                    eventBroker.post(eventTopic, new Object[] { entity.getId(), entity });
                                }
                            }

                            // refresh the explorer tree after successfully deleting
                            // eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity);
                            if (folderTreeEntity.getParent() != null) {
                                eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                                        folderTreeEntity.getParent());
                                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM,
                                        folderTreeEntity);
                            }

                            partService.saveAll(false);
                        }
                    } catch (Exception ex) {
                        // Restore old name
                        folder.setName(oldName);
                        LoggerSingleton.getInstance().getLogger().error(ex);
                        MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                                StringConstants.HAND_ERROR_MSG_UNABLE_TO_RENAME_FOLDER);
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }

    }
    
    private void renameRootFolder(FolderTreeEntity folderTreeEntity) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        List<String> existingNames = FolderController.getInstance().getRootFileOrFolderNames(project);
        FolderEntity oldFolder = folderTreeEntity.getObject();
        RenameWizard renameWizard = new RenameWizard(folderTreeEntity, existingNames);
        CWizardDialog wizardDialog = new CWizardDialog(parentShell, renameWizard);
        if (wizardDialog.open() == Window.OK) {
            String newNameValue = renameWizard.getNewNameValue();
            String oldName = oldFolder.getName();
            if (!StringUtils.isBlank(newNameValue) && !newNameValue.equalsIgnoreCase(oldName)) {
                File newFolder = new File(project.getFolderLocation(), newNameValue);
                oldFolder.toFile().renameTo(newFolder);
                folderTreeEntity.getObject().setName(newNameValue);
                eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, folderTreeEntity);
                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, folderTreeEntity);
            }
        }
    }
}
