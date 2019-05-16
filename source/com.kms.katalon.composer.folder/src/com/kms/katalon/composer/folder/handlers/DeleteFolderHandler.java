package com.kms.katalon.composer.folder.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static org.eclipse.jface.dialogs.MessageDialog.openError;

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
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
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
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class DeleteFolderHandler implements IDeleteEntityHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

    private List<String> getChildrenId(FolderTreeEntity folderTreeEntity) throws Exception {
        List<String> childEntitiesPartId = new ArrayList<String>();
        for (Object child : folderTreeEntity.getChildren()) {
            if (child instanceof FolderTreeEntity) {
                childEntitiesPartId.addAll(getChildrenId((FolderTreeEntity) child));
                continue;
            }

            String partId = null;
            if (child instanceof TestCaseTreeEntity) {
                partId = EntityPartUtil.getTestCaseCompositePartId(((TestCaseEntity) ((TestCaseTreeEntity) child).getObject()).getId());
            } else if (child instanceof TestSuiteTreeEntity) {
                partId = EntityPartUtil.getTestSuiteCompositePartId(((TestSuiteEntity) ((TestSuiteTreeEntity) child).getObject()).getId());
            } else if (child instanceof WebElementTreeEntity) {
                partId = EntityPartUtil.getTestObjectPartId(((WebElementEntity) ((WebElementTreeEntity) child).getObject()).getId());
            } else if (child instanceof TestDataTreeEntity) {
                partId = EntityPartUtil.getTestDataPartId(((DataFileEntity) ((TestDataTreeEntity) child).getObject()).getId());
            } else if (child instanceof ReportTreeEntity) {
                partId = EntityPartUtil.getReportPartId(((ReportEntity) ((ReportTreeEntity) child).getObject()).getId());
            }
            if (partId == null) {
                continue;
            }
            childEntitiesPartId.add(partId);
        }
        return childEntitiesPartId;
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
            if (!(entity instanceof FolderTreeEntity) || !(entity.getObject() instanceof FolderEntity)) {
                return false;
            }

            List<String> childEntitiesPartId = getChildrenId((FolderTreeEntity) entity);
            FolderEntity folderEntity = (FolderEntity) entity.getObject();
            boolean showYesNoToAllOptions = childEntitiesPartId.size() > 1;

            // Check Delete Folder Handler from registry first,
            // If it exists, call its execute method. Otherwise, use default delete.
            IDeleteFolderHandler handler = DeleteFolderHandlerFactory.getInstance().getDeleteHandler(
                    folderEntity.getFolderType());
            if (handler != null) {
                if (handler instanceof AbstractDeleteReferredEntityHandler) {
                    ((AbstractDeleteReferredEntityHandler) handler).setNeedYesNoToAllButtons(showYesNoToAllOptions);
                }

                handler.execute((FolderTreeEntity) entity, monitor);
            } else {
                FolderController.getInstance().deleteFolder(folderEntity);
                removeFromExplorer(childEntitiesPartId);
            }

            if (folderEntity.getParentFolder() == null) { //root-level
                eventBroker.post(EventConstants.EXPLORER_RELOAD_DATA, null);
            } else {
                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, folderEntity.getIdForDisplay()
                    + IPath.SEPARATOR);
            }
            return true;
        } catch (EntityIsReferencedException | TestCaseIsReferencedByTestSuiteExepception e) {
            openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, e.getMessage());
        } catch (Exception e) {
            logError(e);
            openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_FOLDER);
        }
        return false;
    }
}
