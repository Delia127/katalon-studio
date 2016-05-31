package com.kms.katalon.composer.objectrepository.handler;

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
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.wizard.RenameWizard;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebElementEntity;

public class RenameTestObjectHandler {

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
                if (object != null && object instanceof WebElementTreeEntity) {
                    execute((WebElementTreeEntity) object);
                }
            }
        });
    }

    private void execute(WebElementTreeEntity webElementTreeEntity) {
        try {
            if (webElementTreeEntity.getObject() instanceof WebElementEntity) {
                RenameWizard renameWizard = new RenameWizard(webElementTreeEntity, ObjectRepositoryController
                        .getInstance().getSibblingWebElementNames((WebElementEntity) webElementTreeEntity.getObject()));
                CWizardDialog wizardDialog = new CWizardDialog(parentShell, renameWizard);
                int code = wizardDialog.open();
                if (code == Window.OK) {
                    WebElementEntity webElement = (WebElementEntity) webElementTreeEntity.getObject();
                    String oldName = webElement.getName();
                    String pk = webElement.getId();
                    String oldIdForDisplay = webElement.getIdForDisplay();
                    try {
                        if (renameWizard.getNewNameValue() != null && !renameWizard.getNewNameValue().isEmpty()
                                && !renameWizard.getNewNameValue().equals(oldName)) {
                            webElement.setName(renameWizard.getNewNameValue());
                            ObjectRepositoryController.getInstance().updateTestObject(webElement);
                            String newIdForDisplay = webElement.getIdForDisplay();
                            eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] {
                                    oldIdForDisplay, newIdForDisplay });
                        }
                        partService.saveAll(false);
                    } catch (Exception ex) {
                        // Restore old name
                        webElement.setName(oldName);
                        LoggerSingleton.logError(ex);
                        MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                                StringConstants.HAND_ERROR_MSG_UNABLE_TO_RENAME_TEST_OBJECT);
                        return;
                    }

                    eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, webElementTreeEntity.getParent());
                    eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { pk, webElement });
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
