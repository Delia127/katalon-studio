package com.kms.katalon.composer.testsuite.collection.handler;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.dialogs.CWizardDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.wizard.RenameWizard;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class RenameTestSuiteCollectionHandler {
    @Inject
    private IEventBroker eventBroker;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                Object object = getObject(event);
                if (object instanceof TestSuiteCollectionTreeEntity) {
                    execute((TestSuiteCollectionTreeEntity) object);
                }
            }
        });
    }

    private void execute(TestSuiteCollectionTreeEntity testRunTree) {
        try {
            if (!(testRunTree.getObject() instanceof TestSuiteCollectionEntity)) {
                return;
            }

            rename(testRunTree);

            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, testRunTree.getParent());

            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, testRunTree);
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HDL_MSG_UNABLE_TO_RENAME_TEST_SUITE_COLLECTION,
                    e.getMessage());
            LoggerSingleton.logError(e);
        }
    }

    private void rename(TestSuiteCollectionTreeEntity testSuiteCollectionTree) throws Exception {
        TestSuiteCollectionEntity testSuiteCollectionEntity = (TestSuiteCollectionEntity) testSuiteCollectionTree.getObject();
        RenameWizard renameWizard = new RenameWizard(testSuiteCollectionTree, getSibblingNames(testSuiteCollectionEntity));
        if (new CWizardDialog(parentShell, renameWizard).open() != Window.OK) {
            return;
        }

        String newName = renameWizard.getNewNameValue();
        if (testSuiteCollectionEntity.getName().equals(newName)) {
            return;
        }

        String oldIdForDisplay = testSuiteCollectionEntity.getIdForDisplay();
        TestSuiteCollectionController.getInstance().renameTestSuiteCollection(newName, testSuiteCollectionEntity);

        eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM,
                new Object[] { oldIdForDisplay, testSuiteCollectionEntity.getIdForDisplay() });
    }

    private List<String> getSibblingNames(TestSuiteCollectionEntity testRunEntity) throws Exception {
        List<String> sibblingNames = FolderController.getInstance().getChildNames(testRunEntity.getParentFolder());
        sibblingNames.remove(testRunEntity.getName());
        return sibblingNames;
    }
}
