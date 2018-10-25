package com.kms.katalon.composer.keyword.handlers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.composer.keyword.dialogs.NewStepDefinitionDialog;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.tracking.service.Trackings;

public class NewStepDefinitionHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    ESelectionService selectionService;

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    public static ITreeEntity findParentTreeEntity(Object[] selectedObjects) throws Exception {
        if (selectedObjects == null) {
            return null;
        }
        for (Object entity : selectedObjects) {
            if (!(entity instanceof ITreeEntity)) {
                continue;
            }

            Object entityObject = ((ITreeEntity) entity).getObject();
            if (entityObject instanceof IPackageFragment) {
                PackageTreeEntity treeEntity = (PackageTreeEntity) entity;
                FolderEntity parent = (FolderEntity) treeEntity.getParent().getObject();
                if (parent.getFolderType() == FolderType.INCLUDE) {
                    return (ITreeEntity) entity;
                }
                return null;
            }

            if (entityObject instanceof FolderEntity && FolderController.getInstance()
                    .isSourceFolder(ProjectController.getInstance().getCurrentProject(), (FolderEntity) entityObject)) {

                return (ITreeEntity) entity;
            }

            if (entity instanceof KeywordTreeEntity) {
                PackageTreeEntity parentPackage = (PackageTreeEntity) ((KeywordTreeEntity) entity).getParent();
                FolderEntity parentFolder = (FolderEntity) parentPackage.getParent().getObject();
                if (parentFolder.getFolderType() == FolderType.INCLUDE) {
                    return parentPackage;
                }
            }
        }
        return null;
    }

    public static boolean isUnderSourceFolder(Object[] selectedObjects) throws Exception {
        if (selectedObjects == null) {
            return false;
        }
        for (Object entity : selectedObjects) {
            if (!(entity instanceof ITreeEntity)) {
                continue;
            }

            Object entityObject = ((ITreeEntity) entity).getObject();
            if (entityObject instanceof IPackageFragment) {
                PackageTreeEntity treeEntity = (PackageTreeEntity) entity;
                FolderEntity parent = (FolderEntity) treeEntity.getParent().getObject();
                if (parent.getFolderType() == FolderType.INCLUDE) {
                    return true;
                }
            }

            if (entityObject instanceof FolderEntity
                    && ((FolderEntity) entityObject).getFolderType() == FolderType.INCLUDE) {
                return true;
            }

            if (entity instanceof KeywordTreeEntity) {
                PackageTreeEntity treeEntity = (PackageTreeEntity) ((KeywordTreeEntity) entity).getParent();
                FolderEntity parent = (FolderEntity) treeEntity.getParent().getObject();
                if (parent.getFolderType() == FolderType.INCLUDE) {
                    return true;
                }
            }
        }
        return false;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            ITreeEntity parentTreeEntity = getParentTreeEntity();

            FolderTreeEntity rootParentFolder = null;
            IPackageFragment packageFragment = null;
            IPackageFragmentRoot rootPackage = null;
            IProject groovyProject = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject());
            if (parentTreeEntity instanceof FolderTreeEntity) {
                FolderEntity folder = (FolderEntity) parentTreeEntity.getObject();
                rootPackage = JavaCore.create(groovyProject)
                        .getPackageFragmentRoot(groovyProject.getFolder(folder.getRelativePath()));
                String packagePath = groovyProject.getFolder(folder.getRelativePath()).getFullPath().toString()
                        .replaceFirst(rootPackage.getPath().toString(), "");
                packageFragment = rootPackage.getPackageFragment(packagePath);
                rootParentFolder = (FolderTreeEntity) parentTreeEntity;
            } else if (parentTreeEntity instanceof PackageTreeEntity) {
                packageFragment = (IPackageFragment) parentTreeEntity.getObject();
                rootPackage = (IPackageFragmentRoot) packageFragment.getParent();
                rootParentFolder = (FolderTreeEntity) parentTreeEntity.getParent();
            }

            if (packageFragment != null) {
                NewStepDefinitionDialog dialog = new NewStepDefinitionDialog(parentShell, rootPackage, packageFragment);
                dialog.open();
                if (dialog.getReturnCode() == Dialog.OK) {
                    int kwFilePathLength = dialog.getParentPackage().getElementName().length()
                            + dialog.getName().length();
                    if (kwFilePathLength > StringConstants.MAX_PKG_AND_CLASS_NAME_LENGTH) {
                        MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                                MessageFormat.format(StringConstants.HAND_ERROR_MSG_EXCEED_CLASS_NAME_LENGTH,
                                        kwFilePathLength, StringConstants.MAX_PKG_AND_CLASS_NAME_LENGTH));
                        return;
                    }
                    // get new input package
                    packageFragment = dialog.getParentPackage();
                    IProgressMonitor monitor = new NullProgressMonitor();
                    monitor.setTaskName("Create Step Definition");
                    if (!packageFragment.exists()) {
                        // create package
                        monitor.subTask("Create Package");
                        packageFragment = rootPackage.createPackageFragment(packageFragment.getElementName(), true,
                                monitor);

                        // remove any working copy of child complicationUnit
                        // that exists in the current package
                        for (ICompilationUnit compicationUnit : packageFragment.getCompilationUnits()) {
                            compicationUnit.discardWorkingCopy();
                        }
                    }

                    // create Keyword class
                    ICompilationUnit createdCompilationUnit;
                    if (dialog.getSampleKeywordType() != 0) {
                        SampleCustomKeywordScriptBuilder sampleScriptBuilder = new SampleCustomKeywordScriptBuilder(
                                dialog);
                        String sampleScript = sampleScriptBuilder.build();
                        createdCompilationUnit = GroovyGuiUtil.createGroovyScriptForCustomKeywordFromTemplate(
                                packageFragment, dialog.getName(), sampleScript);
                    } else {
                        createdCompilationUnit = GroovyGuiUtil.createGroovyScriptForCustomKeyword(packageFragment,
                                dialog.getName());
                    }

                    Trackings.trackCreatingObject("groovyScriptFile");

                    if (createdCompilationUnit instanceof GroovyCompilationUnit
                            && createdCompilationUnit.getParent() instanceof IPackageFragment) {
                        ITreeEntity newPackageTreeEntity = new PackageTreeEntity(packageFragment, rootParentFolder);
                        KeywordTreeEntity keywordTreeEntity = new KeywordTreeEntity(createdCompilationUnit,
                                newPackageTreeEntity);
                        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, rootParentFolder);
                        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, keywordTreeEntity);
                        eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, createdCompilationUnit);
                    }

                    if (monitor.isCanceled()) {
                        throw new InterruptedException();
                    }

                }
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private ITreeEntity getParentTreeEntity() throws Exception {
        Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        ITreeEntity parentTreeEntity = findParentTreeEntity(selectedObjects);

        if (parentTreeEntity == null) {
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            FolderEntity includeRootFolder = FolderController.getInstance().getIncludeRoot(project);
            FolderEntity groovyScriptFolder = FolderController.getInstance().getGroovyScriptRoot(project);
            FolderTreeEntity treeEntity = new FolderTreeEntity(groovyScriptFolder, TreeEntityUtil
                    .createSelectedTreeEntityHierachy(groovyScriptFolder.getParentFolder(), includeRootFolder));
            parentTreeEntity = treeEntity;
        }
        return parentTreeEntity;
    }

    private class SampleCustomKeywordScriptBuilder {

        private static final String COMMON_IMPORTS_FILE = "resources/template/step_definition/step_definition_imports.tpl";

        private static final String SAMPLE_STEP_DEFINTION = "resources/template/step_definition/step_definition.tpl";

        private final NewStepDefinitionDialog dialog;

        public SampleCustomKeywordScriptBuilder(NewStepDefinitionDialog dialog) {
            this.dialog = dialog;
        }

        public String build() {
            String imports = getFileContent(COMMON_IMPORTS_FILE);
            String keywords = buildKeywordScript();

            StringBuilder scriptBuilder = new StringBuilder();
            if (StringUtils.isNotEmpty(dialog.getParentPackage().getElementName())) {
                scriptBuilder.append(String.format("package %s\n", dialog.getParentPackage().getElementName()));
            }
            scriptBuilder.append(imports).append("\n\n")
                    .append(String.format("class %s {\n %s \n}", dialog.getName(), keywords));

            return scriptBuilder.toString();
        }

        private String buildKeywordScript() {
            StringBuilder keywordScriptBuilder = new StringBuilder();
            int sampleKeywordType = dialog.getSampleKeywordType();
            if ((sampleKeywordType & NewStepDefinitionDialog.SAMPLE_STEP_DEFINITION) != 0) {
                String webCustomKeywordScript = getFileContent(SAMPLE_STEP_DEFINTION);
                keywordScriptBuilder.append(webCustomKeywordScript).append("\n\n");
            }

            return keywordScriptBuilder.toString();
        }

        private String getFileContent(String filePath) {
            URL url = FileLocator.find(FrameworkUtil.getBundle(NewStepDefinitionHandler.class), new Path(filePath),
                    null);
            try {
                return StringUtils.join(
                        IOUtils.readLines(new BufferedInputStream(url.openStream()), GlobalStringConstants.DF_CHARSET),
                        "\n");
            } catch (IOException e) {
                LoggerSingleton.logError(e);
                return StringUtils.EMPTY;
            }
        }
    }

}
