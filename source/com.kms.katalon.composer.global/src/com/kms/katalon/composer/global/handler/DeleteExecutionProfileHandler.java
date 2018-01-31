package com.kms.katalon.composer.global.handler;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.impl.tree.ProfileTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

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

        ExecutionProfileEntity executionProfile = null;
        try {
            executionProfile = (ExecutionProfileEntity) treeEntity.getObject();
            GlobalVariableController.getInstance().deleteExecutionProfile(executionProfile);
            eventBroker.post(EventConstants.EXECUTION_PROFILE_DELETED, executionProfile);
        } catch (Exception ignored) {}

        return true;
    }

}
