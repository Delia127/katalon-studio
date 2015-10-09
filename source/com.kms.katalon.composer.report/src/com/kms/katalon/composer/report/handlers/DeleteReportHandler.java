package com.kms.katalon.composer.report.handlers;

import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.report.ReportEntity;

public class DeleteReportHandler implements IDeleteEntityHandler{

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return ReportTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (treeEntity == null || !(treeEntity instanceof ReportTreeEntity)) { return false;}
            
            String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText()
                    + "'...";
            monitor.beginTask(taskName, 1);
            
            ReportEntity report = (ReportEntity) treeEntity.getObject();
            
            if (report == null) {
                return false;
            }
            
            EntityPartUtil.closePart(report);
            
            ReportController.getInstance().deleteReport(report);
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_REPORT,
                    e.getMessage());
            return false;
        } finally {
            monitor.done();
        }
    }
}
