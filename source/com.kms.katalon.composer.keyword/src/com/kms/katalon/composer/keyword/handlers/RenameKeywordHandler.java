package com.kms.katalon.composer.keyword.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.dialogs.RenameKeywordDialog;
import com.kms.katalon.composer.keyword.refactoring.KeywordClassRenamingParticipant;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;

public class RenameKeywordHandler extends RenamePackageHandler {

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
                if (object != null && object instanceof KeywordTreeEntity) {
                    execute((KeywordTreeEntity) object);
                }
            }
        });
    }

    private void execute(KeywordTreeEntity keywordTreeEntity) {
        try {
            ITreeEntity parentTreeEntity = keywordTreeEntity.getParent();
            if (parentTreeEntity instanceof PackageTreeEntity) {
                IFile keywordFile = (IFile) ((ICompilationUnit) keywordTreeEntity.getObject()).getResource();
                IPackageFragment packageFragment = ((IPackageFragment) ((PackageTreeEntity) parentTreeEntity).getObject());
                RenameKeywordDialog dialog = new RenameKeywordDialog(parentShell, packageFragment);
                String kwName = StringUtils.removeEndIgnoreCase(keywordTreeEntity.getText(),
                        GroovyConstants.GROOVY_FILE_EXTENSION);
                dialog.setName(kwName);
                dialog.open();
                if (dialog.getReturnCode() == Dialog.OK) {
                    ProjectEntity project = ProjectController.getInstance().getCurrentProject();
                    KeywordController.getInstance().removeMethodNodesCustomKeywordFile(keywordFile, project);

                    // Do rename KeywordTreeEntity
                    GroovyCompilationUnit cu = (GroovyCompilationUnit) keywordTreeEntity.getObject();
                    IProgressMonitor monitor = new NullProgressMonitor();
                    cu.rename(dialog.getName() + GroovyConstants.GROOVY_FILE_EXTENSION, false, monitor);
                    KeywordClassRenamingParticipant.updateReferences(getClassName(packageFragment, kwName),
                            getClassName(packageFragment, dialog.getName()));
                    if (monitor.isCanceled()) {
                        throw new InterruptedException();
                    }

                    eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordTreeEntity);
                    
                    refreshParentAndSelect(parentTreeEntity, dialog.getName() + GroovyConstants.GROOVY_FILE_EXTENSION);
                    KeywordController.getInstance().parseCustomKeywordFile(keywordFile,
                            ProjectController.getInstance().getCurrentProject());
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private String getClassName(IPackageFragment pkg, String clazz) {
        clazz = StringUtils.removeEnd(clazz, GroovyConstants.GROOVY_FILE_EXTENSION);
        return (pkg.getElementName().isEmpty()) ? clazz : pkg.getElementName() + "." + clazz;
    }
}
