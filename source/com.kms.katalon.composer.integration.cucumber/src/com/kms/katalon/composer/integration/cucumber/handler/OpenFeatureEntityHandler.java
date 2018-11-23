package com.kms.katalon.composer.integration.cucumber.handler;


import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testcase.providers.TestObjectScriptDropListener;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.tracking.service.Trackings;

public class OpenFeatureEntityHandler {
    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object instanceof SystemFileEntity) {
                    openEditor((SystemFileEntity) object);
                }
            }
        });
    }

    ITextEditor openEditor(SystemFileEntity object) {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        IFile iFile = GroovyUtil.getGroovyProject(currentProject).getFile(object.getRelativePath());
        try {
            iFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
            IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(iFile.getName());
            if (desc == null) {
                desc = PlatformUI.getWorkbench().getEditorRegistry().findEditor("org.eclipse.ui.DefaultTextEditor");
            }
            
            IWorkbenchPage activePage = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow()
                    .getActivePage();
            
            ITextEditor editor = (ITextEditor) activePage.findEditor(new FileEditorInput(iFile));
            if (editor != null) { //editor already opened
                activePage.activate(editor);
            } else {
                editor = (ITextEditor) activePage.openEditor(new FileEditorInput(iFile), desc.getId());
                Trackings.trackOpenObject("bddFeatureFile");
            }
            addTestObjectDropListener(editor);
            
            return editor;
        } catch (CoreException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }
    
    private void addTestObjectDropListener(ITextEditor editor) {
        Control control = (Control) editor.getAdapter(Control.class);
        if (!(control instanceof StyledText)) {
            return;
        }
        DropTarget dropTarget = null;
        Object existingDropTarget = control.getData(DND.DROP_TARGET_KEY);
        if (existingDropTarget != null) {
            dropTarget = (DropTarget) existingDropTarget;
        } else {
            dropTarget = new DropTarget(control, DND.DROP_COPY);
        }
        Transfer[] transfers = dropTarget.getTransfer();
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        if (transfers.length != 0) {
            treeEntityTransfers.addAll(Arrays.asList(transfers));
        }
        dropTarget.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
        dropTarget.addDropListener(new TestObjectScriptDropListener((StyledText)editor.getAdapter(Control.class)));
    }
}
