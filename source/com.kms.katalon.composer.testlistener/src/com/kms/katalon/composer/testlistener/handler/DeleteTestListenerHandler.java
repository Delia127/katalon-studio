package com.kms.katalon.composer.testlistener.handler;

import static java.text.MessageFormat.format;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.kms.katalon.composer.components.impl.tree.TestListenerTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.testlistener.constant.ComposerTestListenerMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestListenerController;
import com.kms.katalon.entity.file.TestListenerEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class DeleteTestListenerHandler implements IDeleteEntityHandler {

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return TestListenerTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        if (!(treeEntity instanceof TestListenerTreeEntity)) {
            return false;
        }

        TestListenerEntity testListener = null;
        try {
            testListener = (TestListenerEntity) treeEntity.getObject();
        } catch (Exception ignored) {}
        monitor.subTask(format(ComposerTestListenerMessageConstants.HAND_JOB_DELETING, testListener.getIdForDisplay()));

        try {
            IFile iFile = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject())
                    .getFile(Path.fromOSString(testListener.getRelativePath()));
            iFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
            iFile.delete(true, new NullProgressMonitor());
            TestListenerController.getInstance().deleteTestListener(testListener);
            return true;
        } catch (CoreException e) {
            LoggerSingleton.logError(e);
            return false;
        } finally {
            monitor.done();
        }
    }

}
