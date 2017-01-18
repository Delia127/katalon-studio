package com.kms.katalon.composer.testcase.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class RefreshTestCaseHandler {

	@Inject
	private IEventBroker eventBroker;

	@PostConstruct
	public void registerEventHandler() {
		eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new EventHandler() {

			@Override
			public void handleEvent(Event event) {
                if (ProjectController.getInstance().getCurrentProject() == null) {
                    return;
                }
				Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
				if (object != null && object instanceof TestCaseTreeEntity) {
					try {
						execute((TestCaseTreeEntity) object);
					} catch (Exception e) {
						LoggerSingleton.logError(e);
					}
				}
			}
		});
	}

	/**
	 * Refresh test case's folder if its fragment doesn't exist in project's
	 * class path
	 * 
	 * @param testCaseTreeEntity
	 * @throws Exception
	 */
	private void execute(TestCaseTreeEntity testCaseTreeEntity) throws Exception {
		TestCaseEntity testCase = (TestCaseEntity) testCaseTreeEntity.getObject();
		ProjectEntity project = ProjectController.getInstance().getCurrentProject();
		ITreeEntity parentEntity = testCaseTreeEntity.getParent();
		if (testCase != null && TestCaseController.getInstance().getTestCase(testCase.getId()) != null) {
			IProject groovyProject = GroovyUtil.getGroovyProject(project);
			IFolder testCaseScriptFolder = groovyProject.getFolder(GroovyUtil
					.getScriptPackageRelativePathForTestCase(testCase));
			testCaseScriptFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
		} else {
			refreshParentTestCaseFolder(parentEntity, project);
		}
	}

	private void refreshParentTestCaseFolder(ITreeEntity parentTestCaseFolderTreeEntity, ProjectEntity project)
			throws Exception {
		if (parentTestCaseFolderTreeEntity != null && parentTestCaseFolderTreeEntity instanceof FolderTreeEntity) {
			eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, parentTestCaseFolderTreeEntity);
		} else {
			FolderEntity folder = FolderController.getInstance().getTestCaseRoot(project);
			eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new FolderTreeEntity(folder, null));
		}
	}
}
