package com.kms.katalon.composer.integration.cucumber.handler;

import static java.text.MessageFormat.format;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.FeatureTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FeatureController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FeatureEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class DeleteFeatureEntityHandler implements IDeleteEntityHandler {

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return FeatureTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        if (!(treeEntity instanceof FeatureTreeEntity)) {
            return false;
        }

        FeatureEntity featureEntity = null;
        try {
            featureEntity = (FeatureEntity) treeEntity.getObject();
        } catch (Exception ignored) {}
        monitor.subTask(format("Deleting Feature file: ", featureEntity.getIdForDisplay()));

        try {
            IFile iFile = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject())
                    .getFile(Path.fromOSString(featureEntity.getRelativePath()));
            iFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
            iFile.delete(true, new NullProgressMonitor());
            FeatureController.getInstance().deleteFeature(featureEntity);
            
            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, treeEntity.getParent());
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        } finally {
            monitor.done();
        }
    }

}
