package com.kms.katalon.composer.global.handler;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.ProfileTreeEntity;
import com.kms.katalon.composer.global.constants.ComposerGlobalMessageConstants;
import com.kms.katalon.composer.global.dialog.ExecutionProfileNameDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class RenameExecutionProfileHandler {

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
                if (object != null && object instanceof ProfileTreeEntity) {
                    execute((ProfileTreeEntity) object);
                }
            }
        });
    }

    private void execute(ProfileTreeEntity selectedTreeEntity) {
        ExecutionProfileEntity selectedProfile = null;
        try {
            selectedProfile = selectedTreeEntity.getObject();
        } catch (Exception ignored) {}
        try {
            String profileName = selectedProfile.getName();
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            List<ExecutionProfileEntity> sibblingProfiles = GlobalVariableController.getInstance()
                    .getAllGlobalVariableCollections(project)
                    .stream()
                    .filter(p -> !p.getName().equals(profileName))
                    .collect(Collectors.toList());

            ExecutionProfileNameDialog dialog = new ExecutionProfileNameDialog(parentShell,
                    selectedProfile.getName(), sibblingProfiles,
                    ComposerGlobalMessageConstants.DIA_TITLE_RENAME_EXECUTION_PROFILE);
            if (dialog.open() != ExecutionProfileNameDialog.OK) {
                return;
            }
            String newName = dialog.getNewName();
            ExecutionProfileEntity renamedProfile = GlobalVariableController.getInstance()
                    .renameExecutionProfile(newName, selectedProfile);
            List<TestSuiteCollectionEntity> updatedTestSuiteCollections = TestSuiteCollectionController.getInstance()
                    .updateProfileNameInAllTestSuiteCollections(project, profileName, newName);
            selectedTreeEntity.setObject(renamedProfile);

            eventBroker.post(EventConstants.EXECUTION_PROFILE_RENAMED, renamedProfile);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, selectedTreeEntity.getParent());

            updatedTestSuiteCollections.forEach(tsc-> eventBroker.post(EventConstants.TEST_SUITE_COLLECTION_UPDATED,
                    new Object[]{ tsc.getId(), tsc}));
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(
                    MessageFormat.format("Unable to rename execution profile '{0}'", selectedProfile.getName()),
                    e.getMessage(), ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }
}
