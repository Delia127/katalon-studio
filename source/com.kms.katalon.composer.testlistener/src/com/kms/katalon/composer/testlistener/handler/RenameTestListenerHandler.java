package com.kms.katalon.composer.testlistener.handler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.tree.TestListenerFolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestListenerTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testlistener.constant.ComposerTestListenerMessageConstants;
import com.kms.katalon.composer.testlistener.dialog.RenameTestListenerDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestListenerController;
import com.kms.katalon.entity.file.TestListenerEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class RenameTestListenerHandler {
    @Inject
    private IEventBroker eventBroker;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                Object object = getObject(event);
                if (object instanceof TestListenerTreeEntity) {
                    execute((TestListenerTreeEntity) object);
                }
            }
        });
    }

    private void execute(TestListenerTreeEntity testListenerTreeEntity) {
        try {
            TestListenerController testListenerController = TestListenerController.getInstance();

            TestListenerFolderTreeEntity parentTreeFolder = (TestListenerFolderTreeEntity) testListenerTreeEntity
                    .getParent();
            TestListenerEntity renamedTestListener = testListenerTreeEntity.getObject();
            FolderEntity parentFolder = parentTreeFolder.getObject();
            RenameTestListenerDialog dialog = new RenameTestListenerDialog(parentShell,
                    testListenerTreeEntity.getObject(),
                    testListenerController.getSiblingTestListeners(renamedTestListener, parentFolder));
            if (dialog.open() != RenameTestListenerDialog.OK) {
                return;
            }
            String newName = dialog.getNewName();
            if (renamedTestListener.getName().equals(newName)) {
                return;
            }

            IFile iFile = GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject())
                    .getFile(Path.fromOSString(renamedTestListener.getRelativePath()));
            iFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
            GroovyCompilationUnit unit = (GroovyCompilationUnit) JavaCore.createCompilationUnitFrom(iFile);
            unit.rename(newName + ".groovy", true, new NullProgressMonitor());

            TestListenerEntity newTestListener = testListenerController.renameTestListener(newName,
                    renamedTestListener);
            testListenerTreeEntity.setObject(newTestListener);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, testListenerTreeEntity);
            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, testListenerTreeEntity);
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e,
                    ComposerTestListenerMessageConstants.HDL_MSG_UNABLE_TO_RENAME_TEST_LISTENER, e.getMessage());
            LoggerSingleton.logError(e);
        }
    }
}
