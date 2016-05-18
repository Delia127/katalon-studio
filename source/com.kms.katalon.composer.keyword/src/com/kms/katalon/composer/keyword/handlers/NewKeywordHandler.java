package com.kms.katalon.composer.keyword.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.composer.keyword.dialogs.NewKeywordDialog;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class NewKeywordHandler {

    @Inject
    private EventBroker eventBroker;

    @Inject
    private ESelectionService selectionService;

    private FolderTreeEntity keywordTreeRoot;

    @CanExecute
    private boolean canExecute() {
        try {
            if (ProjectController.getInstance().getCurrentProject() != null) {
                return true;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            ITreeEntity parentTreeEntity = findParentTreeEntity(selectedObjects);
            if (parentTreeEntity == null) {
                parentTreeEntity = keywordTreeRoot;
            }

            IPackageFragment packageFragment = null;

            if (parentTreeEntity != null && parentTreeEntity instanceof PackageTreeEntity) {
                packageFragment = (IPackageFragment) parentTreeEntity.getObject();
            } else {
                packageFragment = GroovyUtil.getDefaultPackageForKeyword(ProjectController.getInstance()
                        .getCurrentProject());
            }
            if (packageFragment != null) {
                IProject groovyProject = GroovyUtil.getGroovyProject(ProjectController.getInstance()
                        .getCurrentProject());
                IPackageFragmentRoot rootPackage = JavaCore.create(groovyProject).getPackageFragmentRoot(
                        groovyProject.getFolder(StringConstants.ROOT_FOLDER_NAME_KEYWORD));
                packageFragment.getResource().refreshLocal(IResource.DEPTH_ONE, null);
                NewKeywordDialog dialog = new NewKeywordDialog(parentShell, rootPackage, packageFragment);
                dialog.open();
                if (dialog.getReturnCode() == Dialog.OK) {
                    // get new input package
                    packageFragment = dialog.getParentPackage();
                    IProgressMonitor monitor = new NullProgressMonitor();
                    monitor.setTaskName("Create Keyword");
                    if (!packageFragment.exists()) {
                        // create package
                        monitor.subTask("Create Package");
                        packageFragment = rootPackage.createPackageFragment(packageFragment.getElementName(), true,
                                monitor);

                        // remove any working copy of child complicationUnit that exists in the current package
                        for (ICompilationUnit compicationUnit : packageFragment.getCompilationUnits()) {
                            compicationUnit.discardWorkingCopy();
                        }
                    }

                    // create Keyword class
                    ICompilationUnit createdCompilationUnit = GroovyGuiUtil.createGroovyScriptForCustomKeyword(
                            packageFragment, dialog.getName());

                    if (createdCompilationUnit instanceof GroovyCompilationUnit
                            && createdCompilationUnit.getParent() instanceof IPackageFragment) {
                        ITreeEntity keywordRootFolder = new FolderTreeEntity(FolderController.getInstance()
                                .getKeywordRoot(ProjectController.getInstance().getCurrentProject()), null);
                        ITreeEntity newPackageTreeEntity = new PackageTreeEntity(packageFragment, keywordRootFolder);
                        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordRootFolder);
                        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, new KeywordTreeEntity(
                                createdCompilationUnit, newPackageTreeEntity));
                        eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, createdCompilationUnit);
                    }

                    if (monitor.isCanceled()) {
                        throw new InterruptedException();
                    }

                }
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_CREATE_KEYWORD);
        }

    }

    public static ITreeEntity findParentTreeEntity(Object[] selectedObjects) throws Exception {
        if (selectedObjects != null) {
            for (Object entity : selectedObjects) {
                if (entity instanceof ITreeEntity) {
                    Object entityObject = ((ITreeEntity) entity).getObject();
                    if (entityObject instanceof IPackageFragment) {
                        return (ITreeEntity) entity;
                    } else if (entityObject instanceof IFile) {
                        IFile file = (IFile) entityObject;
                        if (file.getName().endsWith(GroovyConstants.GROOVY_FILE_EXTENSION)) {
                            return (ITreeEntity) ((ITreeEntity) entity).getParent();
                        }
                    } else if (entityObject instanceof FolderEntity
                            && ((FolderEntity) entityObject).getFolderType() == FolderType.KEYWORD) {
                        return (ITreeEntity) entity;
                    }
                }
            }
        }
        return null;
    }

    @Inject
    @Optional
    private void catchKeywordFolderTreeEntitiesRoot(
            @UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) List<Object> treeEntities) {
        try {
            for (Object o : treeEntities) {
                Object entityObject = ((ITreeEntity) o).getObject();
                if (entityObject instanceof FolderEntity) {
                    FolderEntity folder = (FolderEntity) entityObject;
                    if (folder.getFolderType() == FolderType.KEYWORD) {
                        keywordTreeRoot = (FolderTreeEntity) o;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
