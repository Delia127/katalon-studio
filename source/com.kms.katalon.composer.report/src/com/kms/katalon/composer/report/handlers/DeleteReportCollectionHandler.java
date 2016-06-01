package com.kms.katalon.composer.report.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ReportCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.report.ReportCollectionEntity;

public class DeleteReportCollectionHandler implements IDeleteEntityHandler {
    @Inject
    private UISynchronize sync;

    @Inject
    private IEventBroker eventBroker;

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return ReportCollectionTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (!(treeEntity instanceof ReportCollectionTreeEntity)) {
                return false;
            }

            String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText() + "'...";
            monitor.beginTask(taskName, 1);

            ReportCollectionEntity reportCollection = (ReportCollectionEntity) treeEntity.getObject();

            if (reportCollection == null) {
                return false;
            }

            EntityPartUtil.closePart(reportCollection);

            String reportId = reportCollection.getId();
            ReportController.getInstance().deleteReportCollection(reportCollection);

            eventBroker.send(EventConstants.REPORT_DELETED, reportId);
            return true;
        } catch (final Exception e) {
            LoggerSingleton.logError(e);
            sync.syncExec(new Runnable() {

                @Override
                public void run() {
                    MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_REPORT,
                            e.getMessage());
                }
            });
            return false;
        } finally {
            monitor.done();
        }
    }
}
