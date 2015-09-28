package com.kms.katalon.composer.testsuite.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.dialogs.CWizardDialog;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.wizard.RenameWizard;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class RenameTestSuiteHandler {

    @Inject
    private IEventBroker eventBroker;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof TestSuiteTreeEntity) {
                    execute((TestSuiteTreeEntity) object);
                }
            }
        });
    }

    private void execute(TestSuiteTreeEntity testSuiteTreeEntity) {
        try {
            if (testSuiteTreeEntity.getObject() instanceof TestSuiteEntity) {
                RenameWizard renameWizard = new RenameWizard(testSuiteTreeEntity, TestSuiteController.getInstance()
                        .getSibblingTestSuiteNames((TestSuiteEntity) testSuiteTreeEntity.getObject()));
                CWizardDialog wizardDialog = new CWizardDialog(parentShell, renameWizard);
                int code = wizardDialog.open();
                if (code == Window.OK) {
                    TestSuiteEntity testSuite = (TestSuiteEntity) testSuiteTreeEntity.getObject();
                    String oldName = testSuite.getName();
                    String pk = testSuite.getId();
                    String oldIdForDisplay = TestSuiteController.getInstance().getIdForDisplay(testSuite);
                    try {
                        if (renameWizard.getNewNameValue() != null && !renameWizard.getNewNameValue().isEmpty()
                                && !renameWizard.getNewNameValue().equals(oldName)) {
                            testSuite.setName(renameWizard.getNewNameValue());
                            TestSuiteController.getInstance().updateTestSuite(testSuite);
                            String newIdForDisplay = TestSuiteController.getInstance().getIdForDisplay(testSuite);
                            eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] {
                                    oldIdForDisplay, newIdForDisplay });
                        }
                    } catch (Exception ex) {
                        // Restore old name
                        testSuite.setName(oldName);
                        LoggerSingleton.logError(ex);
                        MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                                StringConstants.HAND_ERROR_MSG_UNABLE_TO_RENAME_TEST_SUITE);
                        return;
                    }

                    eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, testSuiteTreeEntity.getParent());
                    eventBroker.post(EventConstants.TEST_SUITE_UPDATED, new Object[] { pk, testSuite });
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
