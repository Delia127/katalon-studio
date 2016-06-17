package com.kms.katalon.composer.testcase.handlers;

import java.util.List;

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
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.wizard.RenameWizard;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class RenameTestCaseHandler {

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
                if (object != null && object instanceof TestCaseTreeEntity) {
                    execute((TestCaseTreeEntity) object);
                }
            }
        });
    }

    private void execute(TestCaseTreeEntity testCaseTreeEntity) {
        try {
            if (testCaseTreeEntity.getObject() instanceof TestCaseEntity) {
                TestCaseEntity testCase = testCaseTreeEntity.getObject();
                List<String> existingNames = FolderController.getInstance()
                        .getChildrenNames(testCase.getParentFolder());
                RenameWizard renameWizard = new RenameWizard(testCaseTreeEntity, existingNames);
                CWizardDialog wizardDialog = new CWizardDialog(parentShell, renameWizard);
                int code = wizardDialog.open();
                if (code == Window.OK) {
                    String oldName = testCase.getName();
                    String pk = testCase.getId();
                    String oldIdForDisplay = testCase.getIdForDisplay();
                    try {
                        if (renameWizard.getNewNameValue() != null && !renameWizard.getNewNameValue().isEmpty()
                                && !renameWizard.getNewNameValue().equals(oldName)) {
                            GroovyUtil.loadScriptContentIntoTestCase(testCase);
                            testCase.setName(renameWizard.getNewNameValue());
                            TestCaseController.getInstance().updateTestCase(testCase);
                            String newIdForDisplay = testCase.getIdForDisplay();

                            eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] {
                                    oldIdForDisplay, newIdForDisplay });
                            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                                    testCaseTreeEntity.getParent());
                            eventBroker.post(EventConstants.TESTCASE_UPDATED, new Object[] { pk, testCase });

                            partService.saveAll(false);
                        }
                    } catch (Exception ex) {
                        // Restore old name
                        testCase.setName(oldName);
                        LoggerSingleton.logError(ex);
                        MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                                StringConstants.HAND_ERROR_MSG_UNABLE_TO_RENAME_TEST_CASE);
                        return;
                    }

                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

}
