package com.kms.katalon.composer.testcase.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.dialogs.NewTestCaseDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class NewTestCaseHandler {
    @Inject
    IEventBroker eventBroker;

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

    @Inject
    private ESelectionService selectionService;

    private FolderTreeEntity testCaseTreeRoot;

    private static final String DEFAULT_NEW_TEST_CASE_NAME = StringConstants.HAND_NEW_TEST_CASE;

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            ITreeEntity parentTreeEntity = findParentTreeEntity(selectedObjects);
            if (parentTreeEntity == null) {
                parentTreeEntity = testCaseTreeRoot;
            }
            doCreateNewTestCase(parentTreeEntity, eventBroker);
        } catch (FilePathTooLongException e) {
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, e.getMessage());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_CASE);
        }
    }
    public static TestCaseEntity doCreateNewTestCase(ITreeEntity parentTreeEntity, IEventBroker eventBroker)
            throws Exception {
        if (parentTreeEntity == null) {
            return null;
        }
        FolderEntity parentFolderEntity = (FolderEntity) parentTreeEntity.getObject();
        TestCaseController tcController = TestCaseController.getInstance();
        String suggestedName = tcController.getAvailableTestCaseName(parentFolderEntity, DEFAULT_NEW_TEST_CASE_NAME);

        final Shell activeShell = Display.getCurrent().getActiveShell();
        NewTestCaseDialog dialog = new NewTestCaseDialog(activeShell, parentFolderEntity, suggestedName);
        if (dialog.open() != Dialog.OK) {
            return null;
        }

        // create new test case
        TestCaseEntity testCaseEntity = tcController.saveNewTestCase(dialog.getEntity());

        if (testCaseEntity == null) {
            // No project found. This case won't happen but need to handle.
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_CASE);
            return null;
        }

        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
        eventBroker.send(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, parentTreeEntity);
        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM,
                new TestCaseTreeEntity(testCaseEntity, parentTreeEntity));
        eventBroker.send(EventConstants.TESTCASE_OPEN, testCaseEntity);
        return testCaseEntity;
    }

    public static ITreeEntity findParentTreeEntity(Object[] selectedObjects) throws Exception {
        if (selectedObjects != null) {
            for (Object entity : selectedObjects) {
                if (entity instanceof ITreeEntity) {
                    Object entityObject = ((ITreeEntity) entity).getObject();
                    if (entityObject instanceof FolderEntity) {
                        FolderEntity folder = (FolderEntity) entityObject;
                        if (folder.getFolderType() == FolderType.TESTCASE) {
                            return (ITreeEntity) entity;
                        }
                    } else if (entityObject instanceof TestCaseEntity) {
                        return ((ITreeEntity) entity).getParent();
                    }
                }
            }
        }
        return null;
    }

    @Inject
    @Optional
    private void catchTestCaseTreeEntitiesRoot(
            @UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) List<Object> treeEntities) {
        try {
            testCaseTreeRoot = findTestCaseTreeRoot(treeEntities);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private static FolderTreeEntity findTestCaseTreeRoot(List<Object> treeEntities) throws Exception {
        for (Object o : treeEntities) {
            Object entityObject = ((ITreeEntity) o).getObject();
            if (!(entityObject instanceof FolderEntity)) {
                return null;
            }
            FolderEntity folder = (FolderEntity) entityObject;
            if (folder.getFolderType() == FolderType.TESTCASE) {
                return (FolderTreeEntity) o;
            }
        }
        return null;
    }

    @Inject
    @Optional
    private void execute(@UIEventTopic(EventConstants.TESTCASE_NEW) Object eventData) {
        if (!canExecute()) {
            return;
        }
        execute(Display.getCurrent().getActiveShell());
    }

}
