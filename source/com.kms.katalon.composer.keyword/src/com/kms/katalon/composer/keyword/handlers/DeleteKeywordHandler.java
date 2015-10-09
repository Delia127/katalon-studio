package com.kms.katalon.composer.keyword.handlers;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.handlers.deletion.IDeleteEntityHandler;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;

public class DeleteKeywordHandler implements IDeleteEntityHandler {
    
    @Inject
    private UISynchronize sync;
    

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return KeywordTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        try {
            if (treeEntity == null || !(treeEntity instanceof KeywordTreeEntity)) {
                return false;
            }
            
            
            String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText() + "'...";
            monitor.beginTask(taskName, 1);

            if (treeEntity.getObject() != null && treeEntity.getObject() instanceof ICompilationUnit) {
                ICompilationUnit file = (ICompilationUnit) treeEntity.getObject();
                IFile iFile = (IFile) file.getResource();

                KeywordController.getInstance().removeMethodNodesCustomKeywordFile(iFile,
                        ProjectController.getInstance().getCurrentProject());

                if (file.exists()) {
                    file.getResource().refreshLocal(IResource.DEPTH_ZERO, null);

                    closeEditor(iFile);
                    
                    if (file.isWorkingCopy()) {
                        file = file.getPrimary();
                    }
                    file.delete(true, null);
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_KEYWORD);
            return false;
        } finally {
            monitor.done();
        }
    }
    
    private void closeEditor(final IFile file) {
        sync.asyncExec(new Runnable() {
            
            @Override
            public void run() {
                IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage();
                IEditorPart editorPart = workbenchPage.findEditor(new FileEditorInput(file));
                if (editorPart != null) {
                    workbenchPage.closeEditor(editorPart, false);
                }
            }
        });
    }

}
