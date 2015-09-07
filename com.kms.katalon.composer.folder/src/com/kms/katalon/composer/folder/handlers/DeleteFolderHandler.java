package com.kms.katalon.composer.folder.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.folder.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.dal.exception.EntityIsReferencedException;
import com.kms.katalon.entity.dal.exception.TestCaseIsReferencedByTestSuiteExepception;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

@SuppressWarnings("restriction")
public class DeleteFolderHandler {

	@Inject
	IEventBroker eventBroker;

	@Inject
	EModelService modelService;

	@Inject
	MApplication application;

	@PostConstruct
	private void registerEventHandler() {
		eventBroker.subscribe(EventConstants.EXPLORER_DELETE_SELECTED_ITEM, new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
				if (object != null && object instanceof FolderTreeEntity) {
					excute((FolderTreeEntity) object);
				}
			}
		});
	}

	private void excute(FolderTreeEntity folderTreeEntity) {
		try {
			if ((folderTreeEntity.getObject() != null) && (folderTreeEntity.getObject() instanceof FolderEntity)) {
				List<String> childEntitiesPartId = new ArrayList<String>();
				getChildrenId(folderTreeEntity, childEntitiesPartId);
				FolderEntity folderEntity = (FolderEntity) folderTreeEntity.getObject();
				FolderController.getInstance().deleteFolder(folderEntity);
				removeFromExplorer(childEntitiesPartId);
				eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, folderEntity.getRelativePathForUI()
						.replace('\\', IPath.SEPARATOR) + IPath.SEPARATOR);
				eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity.getParent());
			}
		} catch (EntityIsReferencedException | TestCaseIsReferencedByTestSuiteExepception e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, e.getMessage());
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, 
					StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_FOLDER);
		}
	}

	private void getChildrenId(FolderTreeEntity folderTreeEntity, List<String> children) throws Exception {
		for (Object child : folderTreeEntity.getChildren()) {
			if (child instanceof FolderTreeEntity) {
				getChildrenId((FolderTreeEntity) child, children);
			} else {
				String partId = null;
				if (child instanceof TestCaseTreeEntity) {
					partId = EntityPartUtil.getTestCaseCompositePartId(((TestCaseEntity) ((TestCaseTreeEntity) child)
							.getObject()).getId());
				} else if (child instanceof TestSuiteTreeEntity) {
					partId = EntityPartUtil
							.getTestSuiteCompositePartId(((TestSuiteEntity) ((TestSuiteTreeEntity) child).getObject()).getId());
				} else if (child instanceof WebElementTreeEntity) {
					partId = EntityPartUtil.getTestObjectPartId(((WebElementEntity) ((WebElementTreeEntity) child)
							.getObject()).getId());
				} else if (child instanceof TestDataTreeEntity) {
					partId = EntityPartUtil.getTestDataPartId(((DataFileEntity) ((TestDataTreeEntity) child).getObject())
							.getId());
				} else if (child instanceof ReportTreeEntity) {
					partId = EntityPartUtil
							.getReportPartId(((ReportEntity) ((ReportTreeEntity) child).getObject()).getId());
				}
				children.add(partId);
			}
		}

	}

	private void removeFromExplorer(List<String> childrenEntityId) throws Exception {
		for (String childId : childrenEntityId) {
			if (childId != null) {
				MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
						application);
				MPart mPart = (MPart) modelService.find(childId, application);
				if (mPart != null) {
					mStackPart.getChildren().remove(mPart);
				}
			}
		}
	}
}
