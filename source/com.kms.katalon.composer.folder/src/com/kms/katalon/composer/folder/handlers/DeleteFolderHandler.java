package com.kms.katalon.composer.folder.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.AbstractDeleteReferredEntityHandler;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.folder.constants.StringConstants;
import com.kms.katalon.composer.folder.handlers.deletion.DeleteFolderHandlerFactory;
import com.kms.katalon.composer.folder.handlers.deletion.IDeleteFolderHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.dal.exception.EntityIsReferencedException;
import com.kms.katalon.entity.dal.exception.TestCaseIsReferencedByTestSuiteExepception;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class DeleteFolderHandler extends AbstractDeleteReferredEntityHandler implements IDeleteEntityHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

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
                            .getTestSuiteCompositePartId(((TestSuiteEntity) ((TestSuiteTreeEntity) child).getObject())
                                    .getId());
                } else if (child instanceof WebElementTreeEntity) {
                    partId = EntityPartUtil.getTestObjectPartId(((WebElementEntity) ((WebElementTreeEntity) child)
                            .getObject()).getId());
                } else if (child instanceof TestDataTreeEntity) {
                    partId = EntityPartUtil.getTestDataPartId(((DataFileEntity) ((TestDataTreeEntity) child)
                            .getObject()).getId());
                } else if (child instanceof ReportTreeEntity) {
                    partId = EntityPartUtil.getReportPartId(((ReportEntity) ((ReportTreeEntity) child).getObject())
                            .getId());
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

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return FolderTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity entity, IProgressMonitor monitor) {
        try {
            if (!(entity instanceof FolderTreeEntity)) {
                return false;
            }

            if ((entity.getObject() != null) && (entity.getObject() instanceof FolderEntity)) {
                List<String> childEntitiesPartId = new ArrayList<String>();
                getChildrenId((FolderTreeEntity) entity, childEntitiesPartId);
                FolderEntity folderEntity = (FolderEntity) entity.getObject();

                // Check Delete Folder Handler from registry first,
                // If it exists, call its execute method. Otherwise, use default delete.
                FolderType folderType = folderEntity.getFolderType();
                IDeleteFolderHandler handler = DeleteFolderHandlerFactory.getInstance().getDeleteHandler(folderType);
                if (handler != null) {
                    if (handler instanceof AbstractDeleteReferredEntityHandler) {
                        ((AbstractDeleteReferredEntityHandler) handler)
                                .setDeletePreferenceOption(getDeletePreferenceOption());
                    }
                    handler.execute((FolderTreeEntity) entity, monitor);

                    if (handler instanceof AbstractDeleteReferredEntityHandler) {
                        setDeletePreferenceOption(((AbstractDeleteReferredEntityHandler) handler)
                                .getDeletePreferenceOption());
                    }
                } else {
                    FolderController.getInstance().deleteFolder(folderEntity);
                    removeFromExplorer(childEntitiesPartId);
                }

                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, folderEntity.getRelativePathForUI()
                        .replace(File.separatorChar, IPath.SEPARATOR) + IPath.SEPARATOR);
            }
            return true;
        } catch (EntityIsReferencedException | TestCaseIsReferencedByTestSuiteExepception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, e.getMessage());
            return false;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_FOLDER);
            return false;
        }
    }
}
