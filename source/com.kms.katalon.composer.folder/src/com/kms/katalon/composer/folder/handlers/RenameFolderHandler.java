package com.kms.katalon.composer.folder.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

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
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

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
                List<String> existingNames = FolderController.getInstance().getSibblingFolderNames(oldFolder);

                for (TestCaseEntity siblingTestCase : FolderController.getInstance().getTestCaseChildren(
                        oldFolder.getParentFolder())) {
                    existingNames.add(siblingTestCase.getName());
                }

                RenameWizard renameWizard = new RenameWizard(folderTreeEntity, existingNames);
                CWizardDialog wizardDialog = new CWizardDialog(parentShell, renameWizard);
                int code = wizardDialog.open();
                if (code == Window.OK) {
                    FolderEntity folder = (FolderEntity) folderTreeEntity.getObject();
                    String oldName = folder.getName();
                    try {
                        if (renameWizard.getNewNameValue() != null && !renameWizard.getNewNameValue().equals("")
                                && !renameWizard.getNewNameValue().equals(oldName)) {

                            // preSave
                            // get object and oldLocation of all children of folder
                            // to notify to all object that refer to them
                            // get collection of descendant entities that
                            // doesn't include descendant folder entity
                            List<Object> allDescendantEntites = new ArrayList<Object>();
                            for (Object descendantEntity : FolderController.getInstance().getAllDescentdantEntities(
                                    folder)) {
                                if (!(descendantEntity instanceof FolderEntity)) {
                                    allDescendantEntites.add(descendantEntity);
                                }
                            }
                            List<String> lstDescendantEntityLocations = new ArrayList<>();
                            if (folder.getFolderType() == FolderType.TESTCASE) {
                                for (Object child : allDescendantEntites) {
                                    if (child != null && child instanceof TestCaseEntity) {
                                        lstDescendantEntityLocations.add(((TestCaseEntity) child).getId());
                                    }
                                }
                            } else if (folder.getFolderType() == FolderType.DATAFILE) {
                                for (Object child : allDescendantEntites) {
                                    if (child != null && child instanceof DataFileEntity) {
                                        lstDescendantEntityLocations.add(((DataFileEntity) child).getId());
                                    }
                                }
                            } else if (folder.getFolderType() == FolderType.TESTSUITE) {
                                for (Object child : allDescendantEntites) {
                                    if (child != null && child instanceof TestSuiteEntity) {
                                        lstDescendantEntityLocations.add(((TestSuiteEntity) child).getId());
                                    }
                                }
                            } else if (folder.getFolderType() == FolderType.WEBELEMENT) {
                                for (Object child : allDescendantEntites) {
                                    if (child != null && child instanceof WebElementEntity) {
                                        lstDescendantEntityLocations.add(((WebElementEntity) child).getId());
                                    }
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
                            if (folder.getFolderType() == FolderType.TESTCASE) {
                                for (int i = 0; i < lstDescendantEntityLocations.size(); i++) {
                                    eventBroker.post(EventConstants.TESTCASE_UPDATED, new Object[] {
                                            lstDescendantEntityLocations.get(i), allDescendantEntites.get(i) });
                                }
                            } else if (folder.getFolderType() == FolderType.DATAFILE) {
                                for (int i = 0; i < lstDescendantEntityLocations.size(); i++) {
                                    eventBroker.post(EventConstants.TEST_DATA_UPDATED, new Object[] {
                                            lstDescendantEntityLocations.get(i), allDescendantEntites.get(i) });
                                }
                            } else if (folder.getFolderType() == FolderType.TESTSUITE) {
                                for (int i = 0; i < lstDescendantEntityLocations.size(); i++) {
                                    eventBroker.post(EventConstants.TEST_SUITE_UPDATED, new Object[] {
                                            lstDescendantEntityLocations.get(i), allDescendantEntites.get(i) });
                                }
                            } else if (folder.getFolderType() == FolderType.WEBELEMENT) {
                                for (int i = 0; i < lstDescendantEntityLocations.size(); i++) {
                                    eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, new Object[] {
                                            lstDescendantEntityLocations.get(i), allDescendantEntites.get(i) });
                                }
                            }

                            // refresh the explorer tree after successfully deleting
                            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity);
                            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());

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
}
