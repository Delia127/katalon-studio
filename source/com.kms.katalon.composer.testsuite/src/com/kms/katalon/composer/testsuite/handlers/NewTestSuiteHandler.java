package com.kms.katalon.composer.testsuite.handlers;

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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.dialogs.NewTestSuiteDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.tracking.service.Trackings;

public class NewTestSuiteHandler {

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private ESelectionService selectionService;

	private FolderTreeEntity testSuiteTreeRoot;

	private String newDefaultName = StringConstants.HAND_DEFAULT_NAME_NEW_TEST_SUITE;

	private String getNameTestSuite = "";

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
				if (testSuiteTreeRoot == null) {
					return;
				}
				parentTreeEntity = testSuiteTreeRoot;
			}

			if (parentTreeEntity == null) {
				return;
			}

			FolderEntity parentFolderEntity = (FolderEntity) parentTreeEntity.getObject();
			TestSuiteController tsController = TestSuiteController.getInstance();
			String suggestedName = tsController.getAvailableTestSuiteName(parentFolderEntity, newDefaultName);

			NewTestSuiteDialog dialog = new NewTestSuiteDialog(parentShell, parentFolderEntity, suggestedName);
			if (dialog.open() != Dialog.OK) {
				return;
			}

			// save new test suite
			TestSuiteEntity testSuite = tsController.saveNewTestSuite(dialog.getEntity());
			if (testSuite == null) {
				MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
						StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_SUITE);
				return;
			}

			Trackings.trackCreatingObject("testSuite");

			eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
			eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM,
					new TestSuiteTreeEntity(testSuite, parentTreeEntity));
			eventBroker.send(EventConstants.TEST_SUITE_OPEN, testSuite);
			getNameTestSuite = testSuite.getId();

		} catch (FilePathTooLongException e) {
			MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, e.getMessage());
		} catch (Exception e) {
			LoggerSingleton.logError(e);
			MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
					StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_SUITE);
		}
	}

	public static ITreeEntity findParentTreeEntity(Object[] selectedObjects) throws Exception {
		if (selectedObjects != null) {
			for (Object entity : selectedObjects) {
				if (entity instanceof ITreeEntity) {
					Object entityObject = ((ITreeEntity) entity).getObject();
					if (entityObject instanceof FolderEntity) {
						FolderEntity folder = (FolderEntity) entityObject;
						if (folder.getFolderType() == FolderType.TESTSUITE) {
							return (ITreeEntity) entity;
						}
					} else if (entityObject instanceof TestSuiteEntity) {
						return ((ITreeEntity) entity).getParent();
					}
				}
			}
		}
		return null;
	}

	@Inject
	@Optional
	private void catchTestSuiteFolderTreeEntitiesRoot(
			@UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) List<Object> treeEntities) {
		try {
			for (Object o : treeEntities) {
				Object entityObject = ((ITreeEntity) o).getObject();
				if (entityObject instanceof FolderEntity) {
					FolderEntity folder = (FolderEntity) entityObject;
					if (folder.getFolderType() == FolderType.TESTSUITE) {
						testSuiteTreeRoot = (FolderTreeEntity) o;
						return;
					}
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	@Inject
	@Optional
	private void execute(@UIEventTopic(EventConstants.TEST_SUITE_NEW) Object eventData) {
		if (!canExecute()) {
			return;
		}else{
			execute(Display.getCurrent().getActiveShell());

		}
			}

	@Inject
	@Optional
	private void executeNewAndAdd(@UIEventTopic(EventConstants.TEST_SUITE_NEW_FROM_TEST_CASE) Object eventData) {
		if (!canExecute()) {
			return;
		}
		execute(Display.getCurrent().getActiveShell());

		TestCaseEntity testCase = (TestCaseEntity) eventData;

		EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.ADD_TEST_CASE_FROM_TEST_CASE,
				new Object[] { testCase, getNameTestSuite });
	}
}
