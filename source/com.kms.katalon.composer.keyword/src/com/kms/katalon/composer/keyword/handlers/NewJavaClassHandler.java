package com.kms.katalon.composer.keyword.handlers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
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
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.composer.keyword.dialogs.NewJavaClassDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

public class NewJavaClassHandler {
    
    @Inject
    private IEventBroker eventBroker;

    @Execute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        try {
            Object[] selectedObjects = ExplorerPart.getInstance().getSelectedTreeEntities().toArray();
            ITreeEntity parentTreeEntity = NewStepDefinitionHandler.findParentTreeEntity(selectedObjects);

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
            NewJavaClassDialog dialog = new NewJavaClassDialog(parentShell, rootPackage, packageFragment);
            dialog.open();

            if (dialog.getReturnCode() == Dialog.OK) {
                int kwFilePathLength = dialog.getParentPackage().getElementName().length() + dialog.getName().length();
                if (kwFilePathLength > StringConstants.MAX_PKG_AND_CLASS_NAME_LENGTH) {
                    MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE,
                            MessageFormat.format(StringConstants.HAND_ERROR_MSG_EXCEED_CLASS_NAME_LENGTH,
                                    kwFilePathLength, StringConstants.MAX_PKG_AND_CLASS_NAME_LENGTH));
                    return;
                }
                // get new input package
                packageFragment = dialog.getParentPackage();
                IProgressMonitor monitor = new NullProgressMonitor();
                monitor.setTaskName("Create Java Class");
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
                ICompilationUnit createdCompilationUnit = createJavaClass(packageFragment, dialog.getName());
                eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
                KeywordTreeEntity keywordEntity = new KeywordTreeEntity(createdCompilationUnit, parentTreeEntity);
                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, keywordEntity);
                eventBroker.post(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, createdCompilationUnit);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private ICompilationUnit createJavaClass(IPackageFragment parentPackage, String typeName) throws IOException {
        String packageName = "";
        if (!parentPackage.getElementName().equals("")) {
            packageName = "package " + parentPackage.getElementName() + ";";
        }
        String javaTemplateContent = getFileContent("resources/template/java_class.tpl");
        Map<String, String> valuePlaceholder = new HashMap<>();
        valuePlaceholder.put("package", packageName);
        valuePlaceholder.put("class", typeName);
        String javaFileContent = StrSubstitutor.replace(javaTemplateContent, valuePlaceholder);

        File newJavaFile = new File(parentPackage.getResource().getRawLocationURI().toURL().getFile(),
                typeName + GroovyConstants.JAVA_FILE_EXTENSION);
        FileUtils.write(newJavaFile, javaFileContent, StandardCharsets.UTF_8);
        return parentPackage.getCompilationUnit(typeName+ GroovyConstants.JAVA_FILE_EXTENSION);
    }

    private String getFileContent(String filePath) {
        URL url = FileLocator.find(FrameworkUtil.getBundle(NewJavaClassHandler.class), new Path(filePath), null);
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
