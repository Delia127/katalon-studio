package com.kms.katalon.composer.keyword.handlers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.composer.keyword.dialogs.NewKeywordDialog;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.tracking.service.Trackings;

@SuppressWarnings("restriction")
public class NewKeywordHandler {

    @Inject
    private EventBroker eventBroker;

    @Inject
    private ESelectionService selectionService;

    private FolderTreeEntity keywordTreeRoot;

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
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
                    int kwFilePathLength = dialog.getParentPackage().getElementName().length()
                            + dialog.getName().length();
                    if (kwFilePathLength > StringConstants.MAX_PKG_AND_CLASS_NAME_LENGTH) {
                        MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, MessageFormat.format(
                                StringConstants.HAND_ERROR_MSG_EXCEED_CLASS_NAME_LENGTH, kwFilePathLength,
                                StringConstants.MAX_PKG_AND_CLASS_NAME_LENGTH));
                        return;
                    }
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
                    ICompilationUnit createdCompilationUnit;
                    if (dialog.getSampleKeywordType() != 0) {
                        SampleCustomKeywordScriptBuilder sampleScriptBuilder = new SampleCustomKeywordScriptBuilder(dialog);
                        String sampleScript = sampleScriptBuilder.build();
                        createdCompilationUnit = GroovyGuiUtil.createGroovyScriptForCustomKeywordFromTemplate(packageFragment, dialog.getName(), sampleScript);
                    } else {
                        createdCompilationUnit = GroovyGuiUtil.createGroovyScriptForCustomKeyword(packageFragment, dialog.getName());
                    }
                    
                    Trackings.trackCreatingObject("keyword");
                    
                    if (createdCompilationUnit instanceof GroovyCompilationUnit
                            && createdCompilationUnit.getParent() instanceof IPackageFragment) {
                        ITreeEntity keywordRootFolder = new FolderTreeEntity(FolderController.getInstance()
                                .getKeywordRoot(ProjectController.getInstance().getCurrentProject()), null);
                        ITreeEntity newPackageTreeEntity = new PackageTreeEntity(packageFragment, keywordRootFolder);
                        KeywordTreeEntity keywordTreeEntity = new KeywordTreeEntity(createdCompilationUnit, newPackageTreeEntity);
                        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, keywordRootFolder);
                        eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, keywordTreeEntity);
                        eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, createdCompilationUnit);
                        eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordTreeEntity);
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
                if (parent.getFolderType() == FolderType.KEYWORD) {
                    return (ITreeEntity) entity;
                }
                return null;
            }

            if (entityObject instanceof ICompilationUnit
                    && ((ICompilationUnit) entityObject).getElementName().endsWith(
                            GroovyConstants.GROOVY_FILE_EXTENSION)) {
                PackageTreeEntity packageTreeEntity = (PackageTreeEntity) ((ITreeEntity) entity).getParent();
                FolderEntity parentFolder = (FolderEntity) packageTreeEntity.getParent().getObject();
                if (parentFolder.getFolderType() == FolderType.KEYWORD) {
                    return packageTreeEntity;
                }
                return null;
            }

            if (entityObject instanceof FolderEntity
                    && ((FolderEntity) entityObject).getFolderType() == FolderType.KEYWORD) {
                return (ITreeEntity) entity;
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

    @Inject
    @Optional
    private void execute(@UIEventTopic(EventConstants.KEYWORD_NEW) Object eventData) {
        if (!canExecute()) {
            return;
        }
        execute(Display.getCurrent().getActiveShell());
    }

    private class SampleCustomKeywordScriptBuilder {
        
        private static final String COMMON_IMPORTS_FILE = "resources/template/common_imports.tpl";
        private static final String SAMPLE_WEB_KEYWORD_FILE = "resources/template/web_keyword.tpl";
        private static final String SAMPLE_MOBILE_KEYWORD_FILE = "resources/template/mobile_keyword.tpl";
        private static final String SAMPLE_API_KEYWORD_FILE = "resources/template/api_keyword.tpl";
        
        private final NewKeywordDialog dialog;
        
        public SampleCustomKeywordScriptBuilder(NewKeywordDialog dialog) {
            this.dialog = dialog;
        }
        
        public String build() {
            String imports = getFileContent(COMMON_IMPORTS_FILE);
            String keywords = buildKeywordScript();
            
            StringBuilder scriptBuilder = new StringBuilder();
            if (dialog.getParentPackage().getElementName().equals("")) {
                scriptBuilder.append(imports)
                        .append("\n\n")
                        .append(String.format("class %s {\n %s \n}", dialog.getName(), keywords));
            } else {
                scriptBuilder.append(String.format("package %s\n", dialog.getParentPackage().getElementName()))
                        .append(imports)
                        .append("\n\n")
                        .append(String.format("class %s {\n %s \n}", dialog.getName(), keywords));

            }

            return scriptBuilder.toString();
        }
        
        private String buildKeywordScript() {
            StringBuilder keywordScriptBuilder = new StringBuilder();
            int sampleKeywordType = dialog.getSampleKeywordType();
            if ((sampleKeywordType & NewKeywordDialog.SAMPLE_WEB_KEYWORD) != 0) {
                String webCustomKeywordScript = getFileContent(SAMPLE_WEB_KEYWORD_FILE);
                keywordScriptBuilder.append(webCustomKeywordScript).append("\n\n");
            }
            
            if ((sampleKeywordType & NewKeywordDialog.SAMPLE_MOBILE_KEYWORD) != 0) {
                String mobileCustomKeywordScript = getFileContent(SAMPLE_MOBILE_KEYWORD_FILE);
                keywordScriptBuilder.append(mobileCustomKeywordScript).append("\n\n");
            }
            
            if ((sampleKeywordType & NewKeywordDialog.SAMPLE_API_KEYWORD) != 0) {
                String APICustomKeywordScript = getFileContent(SAMPLE_API_KEYWORD_FILE);
                keywordScriptBuilder.append(APICustomKeywordScript).append("\n\n");
            }
            
            return keywordScriptBuilder.toString();
        }
        
        private String getFileContent(String filePath) {
            URL url = FileLocator.find(FrameworkUtil.getBundle(NewKeywordHandler.class), new Path(filePath), null);
            try {
                return StringUtils.join(IOUtils.readLines(new BufferedInputStream(url.openStream()),
                                GlobalStringConstants.DF_CHARSET), "\n");
            } catch (IOException e) {
                LoggerSingleton.logError(e);
                return StringUtils.EMPTY;
            }
        }
    }
    
    
}
