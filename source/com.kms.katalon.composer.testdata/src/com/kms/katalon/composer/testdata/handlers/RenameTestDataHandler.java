package com.kms.katalon.composer.testdata.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.dialogs.CWizardDialog;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.wizard.RenameWizard;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class RenameTestDataHandler {

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
                if (object != null && object instanceof TestDataTreeEntity) {
                    execute((TestDataTreeEntity) object);
                }
            }
        });
    }

    private void execute(TestDataTreeEntity testDataTreeEntity) {
        try {
            if (testDataTreeEntity.getObject() instanceof DataFileEntity) {
                RenameWizard renameWizard = new RenameWizard(testDataTreeEntity, TestDataController.getInstance()
                        .getSibblingDataFileNames((DataFileEntity) testDataTreeEntity.getObject()));
                CWizardDialog wizardDialog = new CWizardDialog(parentShell, renameWizard);
                int code = wizardDialog.open();
                if (code == Window.OK) {
                    DataFileEntity testData = (DataFileEntity) testDataTreeEntity.getObject();
                    String oldName = testData.getName();
                    String pk = testData.getId();
                    String oldIdForDisplay = testData.getIdForDisplay();
                    try {
                        if (renameWizard.getNewNameValue() != null && !renameWizard.getNewNameValue().isEmpty()
                                && !renameWizard.getNewNameValue().equals(oldName)) {
                            testData = TestDataController.getInstance().renameDataFile(testData,
                                    renameWizard.getNewNameValue());
                            testDataTreeEntity.setObject(testData);
                            String newIdForDisplay = testData.getIdForDisplay();
                            eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] {
                                    oldIdForDisplay, newIdForDisplay });
                            partService.saveAll(false);
                        }
                    } catch (Exception ex) {
                        // Restore old name
                        testData.setName(oldName);
                        LoggerSingleton.logError(ex);
                        MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                                StringConstants.HAND_ERROR_MSG_UNABLE_TO_RENAME_TEST_DATA);
                        return;
                    }

                    eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, testDataTreeEntity.getParent());
                    eventBroker.post(EventConstants.TEST_DATA_UPDATED, new Object[] { pk, testData });
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
