package com.kms.katalon.composer.report.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.dialogs.CWizardDialog;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.wizard.RenameWizard;
import com.kms.katalon.composer.report.constants.ComposerReportMessageConstants;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.report.ReportEntity;

public class RenameReportHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object instanceof ReportTreeEntity) {
                    execute((ReportTreeEntity) object);
                }
            }
        });
    }

    public List<String> getChildrenReportDisplayNames(FolderEntity folder) throws Exception {
        List<FileEntity> children = FolderController.getInstance().getChildren(folder);
        List<String> childrenNames = new ArrayList<String>();
        for (FileEntity child : children) {
            if (child instanceof ReportEntity) {
                childrenNames.add(((ReportEntity) child).getDisplayName());
            }
        }
        return childrenNames;
    }

    private void execute(ReportTreeEntity reportTreeEntity) {
        try {
            if (reportTreeEntity.getObject() == null) {
                return;
            }
            ReportEntity report = reportTreeEntity.getObject();
            List<String> existingNames = getChildrenReportDisplayNames(report.getParentFolder());
            RenameWizard renameWizard = new RenameWizard(reportTreeEntity, existingNames);
            CWizardDialog wizardDialog = new CWizardDialog(parentShell, renameWizard);
            if (wizardDialog.open() != Window.OK) {
                return;
            }
            String newNameValue = renameWizard.getNewNameValue();
            String oldName = report.getDisplayName();
            try {
                if (StringUtils.isNotEmpty(newNameValue) && !StringUtils.equals(oldName, newNameValue)) {
                    report = ReportController.getInstance().renameReport(report, newNameValue);

                    eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, reportTreeEntity.getParent());
                    eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, reportTreeEntity);
                    eventBroker.post(EventConstants.REPORT_RENAMED, report);
                    partService.saveAll(false);
                }
            } catch (Exception ex) {
                // Restore old name
                report.setDisplayName(oldName);
                LoggerSingleton.logError(ex);
                MessageDialog.openError(parentShell, StringConstants.ERROR,
                        ComposerReportMessageConstants.ERR_MSG_UNABLE_RENAME_REPORT);
                return;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
