package com.kms.katalon.composer.report.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.report.ReportEntity;

public class DeleteReportHandler implements IDeleteEntityHandler {

    @Inject
    private UISynchronize sync;

    @Inject
    private IEventBroker eventBroker;

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return ReportTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (treeEntity == null || !(treeEntity instanceof ReportTreeEntity)) {
                return false;
            }

            String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText() + "'...";
            monitor.beginTask(taskName, 1);

            ReportEntity report = (ReportEntity) treeEntity.getObject();

            if (report == null) {
                return false;
            }

            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    EntityPartUtil.closePart(report);
                }
            });

            String reportId = report.getId();
            ReportController.getInstance().deleteReport(report);

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
