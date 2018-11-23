package com.kms.katalon.composer.webservice.handlers;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.webservice.view.NewHistoryRequestDialog;
import com.kms.katalon.composer.webservice.view.NewHistoryRequestDialog.NewHistoryRequestResult;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.tracking.service.Trackings;

public class SaveDraftRequestHandler {

    private static IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    public static void saveDraftRequest(Shell shell, WebServiceRequestEntity draftEntity) {
        Trackings.trackClickSavingDraftRequest();
        NewHistoryRequestDialog dialog = new NewHistoryRequestDialog(shell, draftEntity);
        if (dialog.open() != NewHistoryRequestDialog.OK) {
            return;
        }

        try {
            NewHistoryRequestResult result = dialog.getResult();

            WebServiceRequestEntity entity = JsonUtil.fromJson(JsonUtil.toJson(draftEntity.clone()),
                    WebServiceRequestEntity.class);
            entity.setName(result.getName());
            entity.setParentFolder(result.getParentFolder());
            entity.setDescription(result.getDescription());
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            entity.setProject(currentProject);

            entity = (WebServiceRequestEntity) ObjectRepositoryController.getInstance().saveNewTestObject(entity);

            Trackings.trackSaveDraftRequest();

            WebElementTreeEntity treeEntity = new WebElementTreeEntity(entity,
                    TreeEntityUtil.createSelectedTreeEntityHierachy(entity.getParentFolder(),
                            FolderController.getInstance().getObjectRepositoryRoot(currentProject)));
            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, treeEntity);
            eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, entity);
        } catch (Exception ex) {
            MultiStatusErrorDialog.showErrorDialog("Unable to save this request", ex.getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(ex));
        }
    }
}
