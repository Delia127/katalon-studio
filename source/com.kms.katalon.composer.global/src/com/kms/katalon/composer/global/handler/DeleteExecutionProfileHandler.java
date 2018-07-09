package com.kms.katalon.composer.global.handler;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.impl.tree.ProfileTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class DeleteExecutionProfileHandler implements IDeleteEntityHandler {

    @Inject
    private IEventBroker eventBroker;

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return ProfileTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        if (!(treeEntity instanceof ProfileTreeEntity)) {
            return false;
        }

        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        ExecutionProfileEntity executionProfile = null;
        try {
            executionProfile = (ExecutionProfileEntity) treeEntity.getObject();
            GlobalVariableController.getInstance().deleteExecutionProfile(executionProfile);
            List<TestSuiteCollectionEntity> updatedTestSuiteCollections = TestSuiteCollectionController.getInstance().
                updateProfileNameInAllTestSuiteCollections(
                        project,
                        executionProfile.getName(),
                        ExecutionProfileEntity.DF_PROFILE_NAME);
            
            eventBroker.post(EventConstants.EXECUTION_PROFILE_DELETED, executionProfile);
            
            updatedTestSuiteCollections.forEach(tsc-> eventBroker.post(EventConstants.TEST_SUITE_COLLECTION_UPDATED,
                    new Object[]{ tsc.getId(), tsc}));
        } catch (Exception ignored) {}

        return true;
    }

}
